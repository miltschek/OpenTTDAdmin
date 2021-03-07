/*
 *  MIT License
 *
 *  Copyright (c) 2021 miltschek
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package de.miltschek.genowefa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.genowefa.Configuration.Game;
import de.miltschek.integrations.GoogleTranslate;
import de.miltschek.integrations.SlackMessage;
import de.miltschek.integrations.SlackRTMClient;
import de.miltschek.openttdadmin.OttdAdminClient;
import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ChatMessage.Recipient;

/**
 * Main class of Genowefa, a cool admin tool for OpenTTD.
 */
public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static final Collection<OttdAdminClient> ottdAdminClients = new ArrayList<>();
	private static final Map<String, OttdAdminClient> slackToAdminClients = new HashMap<>();
	private static SlackRTMClient slack;
	private static GoogleTranslate googleTranslate;
	
	/**
	 * Handles incoming slack messages.
	 * @param slackMessage message to be processed
	 * @return true if forwarded to the game, false otherwise
	 */
	private static Boolean onMessage(SlackMessage slackMessage) {
		OttdAdminClient admin = slackToAdminClients.get(slackMessage.getChannelName());
		if (admin == null) {
			return false;
		} else {
			try {
				admin.sendChat(new ChatMessage(0, Recipient.All, 0, slackMessage.getText()));
				return true;
			} catch (Exception ex) {
				LOGGER.error("Failed to forward a chat {}.", slackMessage, ex);
				return false;
			}
		}
	}
	
	/**
	 * Handles incoming slack commands.
	 * @param slackMessage command to be processed
	 * @return true if identified and processed (independent of a processing result - this one is asynchronous), false otherwise
	 */
	private static Boolean onCommand(SlackMessage slackMessage) {
		OttdAdminClient admin = slackToAdminClients.get(slackMessage.getChannelName());
		if (admin == null) {
			return false;
		} else {
			try {
				if ("/date".equals(slackMessage.getCommand())) {
					admin.requestDate();
				} else if ("/getclients".equals(slackMessage.getCommand())) {
					admin.requestAllClientsInfo();
				} else if ("/getcompanies".equals(slackMessage.getCommand())) {
					admin.requestAllCompaniesInfo();
				} else if ("/kickuser".equals(slackMessage.getCommand())) {
					admin.executeRCon("kick " + slackMessage.getText());
				} else if ("/ban".equals(slackMessage.getCommand())) {
					admin.executeRCon("ban " + slackMessage.getText());
				} else if ("/pause".equals(slackMessage.getCommand())) {
					admin.executeRCon("pause");
				} else if ("/quit".equals(slackMessage.getCommand())) {
					if ("roger".equals(slackMessage.getText())) {
						admin.executeRCon("quit");
					} else {
						slack.sendMessage(slackMessage.getChannelName(), "In order to quit the game, provide the word 'roger' as an argument to the quit command.");
					}
				} else if ("/unban".equals(slackMessage.getCommand())) {
					admin.executeRCon("unban " + slackMessage.getText());
				} else if ("/unpause".equals(slackMessage.getCommand())) {
					admin.executeRCon("unpause");
				} else if ("/setting".equals(slackMessage.getCommand())) {
					admin.executeRCon("setting " + slackMessage.getText());
				} else if ("/resetcompany".equals(slackMessage.getCommand())) {
					admin.executeRCon("resetcompany " + slackMessage.getText());
				} else {
					return false;
				}
				
				return true;
			} catch (Exception ex) {
				LOGGER.error("Failed to handle a command {}.", slackMessage, ex);
				return false;
			}
		}
	}

	/**
	 * Entry point of the application.
	 * @param args arguments in the fixed order: OTTD server address, port number, admin password, slack channel name, slack token
	 */
	public static void main(String[] args) {
		String cfgPath = "genowefa.json";
		if (args.length == 1) {
			cfgPath = args[0];
		} else if (args.length > 1) {
			System.err.println("Usage:");
			System.err.println(Main.class.getName() + " <config_file_path>");
			return;
		}
		
		Configuration configuration;
		try {
			configuration = new Configuration(cfgPath);
		} catch (Exception ex) {
			LOGGER.error("Failed to read the configuration file {}.", cfgPath, ex);
			System.err.println("Failed to read the configuration file " + cfgPath + " due to " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			return;
		}
		
		LOGGER.info("Starting the basic tool.");
		
		if (configuration.getSlack() != null
				&& configuration.getSlack().getAppToken() != null && !configuration.getSlack().getAppToken().isEmpty()
				&& configuration.getSlack().getBotToken() != null && !configuration.getSlack().getBotToken().isEmpty()) {
			LOGGER.debug("Starting slack connector.");
			
			try {
				slack = new SlackRTMClient(configuration.getSlack().getAppToken(), configuration.getSlack().getBotToken());
				slack.registerChatHandler(Main::onMessage);
				slack.registerCommand("/date", Main::onCommand);
				slack.registerCommand("/getclients", Main::onCommand);
				slack.registerCommand("/getcompanies", Main::onCommand);
				slack.registerCommand("/kickuser", Main::onCommand);
				slack.registerCommand("/ban", Main::onCommand);
				slack.registerCommand("/pause", Main::onCommand);
				slack.registerCommand("/quit", Main::onCommand);
				slack.registerCommand("/unban", Main::onCommand);
				slack.registerCommand("/unpause", Main::onCommand);
				slack.registerCommand("/setting", Main::onCommand);
				slack.registerCommand("/resetcompany", Main::onCommand);
			} catch (IOException e) {
				LOGGER.error("Failed to initialize the Slack client.", e);
			}
		}
		
		if (configuration.getGoogle() != null
				&& configuration.getGoogle().getKeyFile() != null
				&& !configuration.getGoogle().getKeyFile().isEmpty()) {
			LOGGER.debug("Starting google translate service.");
			
			try {
				googleTranslate = new GoogleTranslate(configuration.getGoogle().getKeyFile());
			} catch (IOException ex) {
				LOGGER.error("Failed to initialize the google translate service.", ex);
			}
		}
		
		for (Game game : configuration.getGames()) {
			LOGGER.info("Configuring OTTD Admin client to connect to {} on port {}.", game.getAddress(), game.getPort());
			
			OttdAdminClient admin = new OttdAdminClient(game.getAddress(), game.getPort(), game.getPassword());

			admin.setDeliveryChatMessages(true);
			admin.setUpdateClientInfos(true);
			admin.setUpdateCompanyInfos(true);
			//admin.setDeliveryCommandLogs(true); // TODO test
			
			ResetLock resetLock = new ResetLock();
			Context context = new Context(configuration, game, resetLock, admin, slack, game.getSlackChannel(), googleTranslate);
			admin.addChatListener(new ChatListener(context));
			admin.addClientListener(new CustomClientListener(context));
			admin.addCompanyListener(new CustomCompanyListener(context));
			admin.addServerListener(new CustomServerListener(context));

			LOGGER.debug("Starting the OTTD Admin client address {} port {}.", game.getAddress(), game.getPort());
			admin.start();
			
			ottdAdminClients.add(admin);
			
			if (game.getSlackChannel() != null) {
				if (slackToAdminClients.put(game.getSlackChannel(), admin) != null) {
					LOGGER.warn("More than one game is using the same Slack channel {}. Undefined behavior.", game.getSlackChannel());
					System.err.println("More than one game is using the same Slack channel " + game.getSlackChannel() + ". Undefined behavior.");
				}
			}
		}
		
		System.out.println("enter q/quit/exit to quit");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command;
		do {
			try {
				command = br.readLine();
			} catch (IOException e) {
				return;
			}
		} while (!command.equals("q") && !command.equals("quit") && !command.equals("exit"));

		LOGGER.info("Closing the basic tool.");
		for (OttdAdminClient admin : ottdAdminClients) {
			try {
					admin.close();
			} catch (IOException e) {
				LOGGER.warn("Failed to close the OTTD Admin client.", e);
			}
		}
	}
}
