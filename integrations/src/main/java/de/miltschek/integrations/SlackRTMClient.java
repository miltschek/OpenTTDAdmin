package de.miltschek.integrations;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.response.Response;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.methods.response.reactions.ReactionsAddResponse;
import com.slack.api.model.Conversation;
import com.slack.api.model.event.MessageEvent;

/**
 * A Slack client supporting real-time messaging (bi-directional).
 */
public class SlackRTMClient implements Closeable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SlackRTMClient.class);
	
	private final String channel;
	private final MethodsClient client;
	private final SocketModeApp socketModeApp;
	private final Thread socketModeThread;

	/**
	 * Creates an instance of the Slack client.
	 * Application token (for the socket mode) has to be set in the environment variable SLACK_APP_TOKEN.
	 * Bot token (for writing to the chat and sending reactions) has to be set in the environment variable SLACK_BOT_TOKEN.
	 * Channel name (including the #-symbol) or channel ID to send the messages to has to be set in the environment variable SLACK_CHANNEL.
	 * @param receiver handler of incoming messages
	 * @throws IOException thrown in case of a failed initialization
	 */
	public SlackRTMClient(Function<String, Boolean> receiver) throws IOException {
		this(System.getenv("SLACK_CHANNEL"), receiver);
	}
	
	/**
	 * Creates an instance of the Slack client.
	 * Application token (for the socket mode) has to be set in the environment variable SLACK_APP_TOKEN.
	 * Bot token (for writing to the chat and sending reactions) has to be set in the environment variable SLACK_BOT_TOKEN. 
	 * @param channel channel name (including the #-symbol) or channel ID to send the messages to.
	 * @param receiver handler of incoming messages
	 * @throws IOException thrown in case of a failed initialization
	 */
	public SlackRTMClient(String channel, Function<String, Boolean> receiver) throws IOException {
		this(System.getenv("SLACK_APP_TOKEN"), System.getenv("SLACK_BOT_TOKEN"), channel, receiver);
	}
	
	/**
	 * Sends a message to the slack channel.
	 * @param message message to be sent
	 * @return true in case the message has been successfully sent, false otherwise
	 */
	public boolean sendMessage(String message) {
		try {
		    ChatPostMessageRequest cpmrq = ChatPostMessageRequest.builder().channel(this.channel).text(message).build();
		    ChatPostMessageResponse cpmrp = this.client.chatPostMessage(cpmrq);
		    if (cpmrp.isOk()) {
		    	return true;
		    } else {
		    	LOGGER.error("Failed to post a message to Slack: {}.", cpmrp.getError());
		    }
		} catch (SlackApiException e) {
			LOGGER.error("Failed to post a message to Slack (API).", e);
		} catch (Exception e) {
			LOGGER.error("Failed to post a message to Slack (EX).", e);
		}
		
		return false;
	}

	/**
	 * Creates an instance of the Slack client.
	 * @param appToken application token (for the socket mode).
	 * @param botToken bot token (for writing to the chat and sending reactions).
	 * @param channel channel channel name (including the #-symbol) or channel ID to send the messages to
	 * @param receiver handler of incoming messages
	 * @throws IOException thrown in case of a failed initialization
	 */
	public SlackRTMClient(String appToken, String botToken, String channel, Function<String, Boolean> receiver) throws IOException {
		this.channel = channel;
		
	    App app = new App(AppConfig.builder().singleTeamBotToken(botToken).build());
	    this.client = app.slack().methods(botToken);
	    
	    String foundChannelId = null;
	    if (channel.startsWith("#")) {
	    	MethodsClient methods = app.slack().methods(botToken);
	    	ConversationsListResponse clr;
			try {
				clr = methods.conversationsList(r -> r);
		    	if (clr.isOk()) {
		    		for (Conversation c : clr.getChannels()) {
		    			if (channel.equals("#" + c.getName())) {
		    				foundChannelId = c.getId();
		    				LOGGER.debug("Channel ID found as {}.", foundChannelId);
		    				break;
		    			}
		    		}
		    	} else {
		    		LOGGER.error("Failed to get a list of channels: {}.", clr.getError());
		    	}
			} catch (IOException e) {
				LOGGER.error("Failed to get channels (IO).", e);
			} catch (SlackApiException e) {
				LOGGER.error("Failed to get channels (API).", e);
			}
	    } else {
	    	foundChannelId = channel;
	    	LOGGER.debug("Assuming the channel is a channel ID {}.", foundChannelId);
	    }
	    
	    final String channelId = foundChannelId;
	    if (channelId == null) {
	    	LOGGER.error("No channel ID found. Incoming messages can't be matched with the channel name and will be ignored.");
	    }

	    /*app.command("/say", (req, ctx) -> {
	    	System.out.println("SAY> " + req.getPayload().getText());
	        return ctx.ack();
	      });*/
	    
	    app.event(MessageEvent.class, (req, ctx) -> {
	    	LOGGER.debug("MSG> " + req.getEvent().getText());
	    	if (req.getEvent().getChannel().equals(channelId)) {
		    	if (receiver.apply(req.getEvent().getText())) {
		    		ReactionsAddResponse reaction = ctx.client().reactionsAdd(r ->
		    			r.channel(req.getEvent().getChannel())
		    			.timestamp(req.getEvent().getTs())
		    			.name("thumbsup"));
		    		
		    		if (!reaction.isOk()) {
		    			LOGGER.error("Failed to add a reaction to a request: {}.", reaction.getError());
		    		}
		    	} else {
		    		ReactionsAddResponse reaction = ctx.client().reactionsAdd(r ->
		    			r.channel(req.getEvent().getChannel())
		    			.timestamp(req.getEvent().getTs())
		    			.name("thumbsdown"));
				
		    		if (!reaction.isOk()) {
		    			LOGGER.error("Failed to add a reaction to a request: {}.", reaction.getError());
		    		}
		    	}
	    	}
	    	
	    	return ctx.ack();
	    });
	    
	    socketModeApp = new SocketModeApp(appToken, app);
	    this.socketModeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SlackRTMClient.this.socketModeApp.start();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	    
	    this.socketModeThread.setName("SlackSocketMode");
	    this.socketModeThread.setDaemon(true);
	    this.socketModeThread.start();
	}
	
	@Override
	public void close() {
		try {
			this.socketModeApp.stop();
		} catch (Exception e) {
			// ignore it
		}
	}

	/**
	 * Entry point for isolated testing.
	 * @param args ignored
	 * @throws Exception not relevant
	 */
	public static void main(String[] args) throws Exception {
		Random rnd = new Random();
		SlackRTMClient client = new SlackRTMClient(text -> {
			return false;//rnd.nextBoolean();
		});
		
		System.out.println("Hit enter to close the client...");
		System.in.read();
		System.out.println("Closing the client...");
		client.close();
	}
}
