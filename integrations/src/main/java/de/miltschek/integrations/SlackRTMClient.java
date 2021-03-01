package de.miltschek.integrations;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
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
	
	private final App app;
	private final MethodsClient client;
	private final SocketModeApp socketModeApp;
	private final Thread socketModeThread;
	private final Map<String, String> channelsIdToName = new HashMap<>();
	//private final Map<String, String> channelsNameToId = new HashMap<>();

	/**
	 * Creates an instance of the Slack client.
	 * Application token (for the socket mode) has to be set in the environment variable SLACK_APP_TOKEN.
	 * Bot token (for writing to the chat and sending reactions) has to be set in the environment variable SLACK_BOT_TOKEN. 
	 * @throws IOException thrown in case of a failed initialization
	 */
	public SlackRTMClient() throws IOException {
		this(System.getenv("SLACK_APP_TOKEN"), System.getenv("SLACK_BOT_TOKEN"));
	}
	
	/**
	 * Sends a message to the slack channel.
	 * @param channel channel name or channel ID
	 * @param message message to be sent
	 * @return true in case the message has been successfully sent, false otherwise
	 */
	public boolean sendMessage(String channel, String message) {
		try {
		    ChatPostMessageRequest cpmrq = ChatPostMessageRequest.builder().channel(channel).text(message).build();
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
	 * Registers a command handler.
	 * In order to get it work, you need to register the command of this name in the application's settings
	 * on Slack under 'Features', menu 'Slash Commands', 'Create new command'.
	 * The application needs to have the 'commands' scope enabled under 'Features', menu 'OAuth & Permissions',
	 * 'Scopes', 'Bot Token Scopes'.
	 * @param command a /command (slash-something) to be serviced
	 * @param handler a function that will receive these command calls,
	 * getting command arguments and having to return true for a success, false otherwise
	 */
	public void registerCommand(String command, Function<SlackMessage, Boolean> handler) {
		this.app.command(command, (req, ctx) -> {
			SlackMessage slackMessage = new SlackMessage();
			slackMessage.setChannelId(req.getPayload().getChannelId());
			slackMessage.setChannelName("#" + req.getPayload().getChannelName());
			slackMessage.setCommand(req.getPayload().getCommand());
			slackMessage.setText(req.getPayload().getText());
			slackMessage.setUserId(req.getPayload().getUserId());
			slackMessage.setUserName(req.getPayload().getUserName());
			
			if (handler.apply(slackMessage)) {
				LOGGER.debug("Successful command: {}.", slackMessage);
			} else {
				LOGGER.error("Failed to handle a command: {}.", slackMessage);
			}
			
			return ctx.ack();
		});
	}
	
	/**
	 * Registers a chat handler.
	 * In order to get it work, you need to enable 'Event Subscriptions' in the application's settings
	 * on Slack under 'Features', menu 'Event Subscriptions' and select 'message.channels' scope in the
	 * 'Subscribe to bot events' section.
	 * @param handler a function that will receive chat messages and having to return
	 * true for a success, false otherwise
	 */
	public void registerChatHandler(Function<SlackMessage, Boolean> handler) {
		app.event(MessageEvent.class, (req, ctx) -> {
    		SlackMessage slackMessage = new SlackMessage();
    		slackMessage.setChannelId(req.getEvent().getChannel());
    		slackMessage.setChannelName(this.channelsIdToName.get(req.getEvent().getChannel()));
    		slackMessage.setText(req.getEvent().getText());
    		slackMessage.setUserId(req.getEvent().getUser());
    		
	    	if (handler.apply(slackMessage)) {
	    		LOGGER.debug("Successful chat: {}.", slackMessage);
	    		ReactionsAddResponse reaction = ctx.client().reactionsAdd(r ->
	    			r.channel(req.getEvent().getChannel())
	    			.timestamp(req.getEvent().getTs())
	    			.name("thumbsup"));
	    		
	    		if (!reaction.isOk()) {
	    			LOGGER.error("Failed to add a reaction to a request: {}.", reaction.getError());
	    		}
	    	} else {
	    		LOGGER.error("Failed to handle chat: {}.", slackMessage);
	    		ReactionsAddResponse reaction = ctx.client().reactionsAdd(r ->
	    			r.channel(req.getEvent().getChannel())
	    			.timestamp(req.getEvent().getTs())
	    			.name("thumbsdown"));
			
	    		if (!reaction.isOk()) {
	    			LOGGER.error("Failed to add a reaction to a request: {}.", reaction.getError());
	    		}
	    	}
	    	
	    	return ctx.ack();
	    });
	}
	
	/**
	 * Creates an instance of the Slack client.
	 * @param appToken application token (for the socket mode).
	 * @param botToken bot token (for writing to the chat and sending reactions).
	 * @throws IOException thrown in case of a failed initialization
	 */
	public SlackRTMClient(String appToken, String botToken) throws IOException {
	    this.app = new App(AppConfig.builder().singleTeamBotToken(botToken).build());
	    this.client = app.slack().methods(botToken);

	    // create a list of available channels and their IDs
	    MethodsClient methods = app.slack().methods(botToken);
    	ConversationsListResponse clr;
		try {
			clr = methods.conversationsList(r -> r);
	    	if (clr.isOk()) {
	    		for (Conversation c : clr.getChannels()) {
	    			LOGGER.debug("Caching channel id {} and name {}.", c.getId(), c.getName());
	    			channelsIdToName.put(c.getId(), "#" + c.getName());
	    			//channelsNameToId.put(c.getName(), c.getId());
	    		}
	    	} else {
	    		LOGGER.error("Failed to get a list of channels: {}.", clr.getError());
	    	}
		} catch (IOException e) {
			LOGGER.error("Failed to get channels (IO).", e);
		} catch (SlackApiException e) {
			LOGGER.error("Failed to get channels (API).", e);
		}

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
		SlackRTMClient client = new SlackRTMClient();
		client.registerChatHandler(text -> {
			System.out.println(text);
			return rnd.nextBoolean();
		});
		client.registerCommand("/date", text -> {
			System.out.println(text);
			return rnd.nextBoolean();
		});
		
		System.out.println("Hit enter to close the client...");
		System.in.read();
		System.out.println("Closing the client...");
		client.close();
	}
}
