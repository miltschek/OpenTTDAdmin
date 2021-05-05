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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.genowefa.Configuration.Game;
import de.miltschek.integrations.GeoIp;
import de.miltschek.integrations.GoogleTranslate;
import de.miltschek.integrations.SlackMessage;
import de.miltschek.integrations.SlackRTMClient;
import de.miltschek.openttdadmin.OttdAdminClient;
import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ChatMessage.Recipient;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.Color;
import de.miltschek.openttdadmin.data.CompanyEconomy;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.CompanyStatistics;
import de.miltschek.openttdadmin.data.Frequency;
import de.miltschek.openttdadmin.data.FrequencyLong;
import de.miltschek.openttdadmin.data.Language;

/**
 * Main class of Genowefa, a cool admin tool for OpenTTD.
 */
public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static final Collection<OttdAdminClient> ottdAdminClients = new ArrayList<>();
	private static final Map<String, Context> slackToContext = new HashMap<>();
	private static SlackRTMClient slack;
	private static GoogleTranslate googleTranslate;
	private static DatabaseConnector db;
	
	/**
	 * Handles incoming slack messages.
	 * @param slackMessage message to be processed
	 * @return true if forwarded to the game, false otherwise
	 */
	private static Boolean onMessage(SlackMessage slackMessage) {
		Context context = slackToContext.get(slackMessage.getChannelName());
		if (context == null) {
			return false;
		} else {
			try {
				context.notifyAll(slackMessage.getText());
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
		Context context = slackToContext.get(slackMessage.getChannelName());
		if (context == null) {
			return false;
		} else {
			try {
				String[] params = parameters(slackMessage.getText());
				
				if ("/date".equals(slackMessage.getCommand())) {
					slack.sendMessage(slackMessage.getChannelName(), ":computer: Game date " + context.getCurrentDate());
					
				} else if ("/kickuser".equals(slackMessage.getCommand())) {
					if (params.length == 2) {
						if (params[0].matches("[1-9][0-9]*")) {
							context.kickClient(Integer.parseInt(params[0]), params[1]);
						} else {
							context.kickClient(params[0], params[1]);
						}
					} else {
						slack.sendMessage(slackMessage.getChannelName(), "Usage: /kickuser <client_id|ip_address> \"<reason>\"");
					}
					
				} else if ("/ban".equals(slackMessage.getCommand())) {
					if (params.length == 2) {
						if (params[0].matches("[1-9][0-9]*")) {
							context.banClient(Integer.parseInt(params[0]), params[1]);
						} else {
							context.banClient(params[0], params[1]);
						}
					} else {
						slack.sendMessage(slackMessage.getChannelName(), "Usage: /ban <client_id|ip_address> \"<reason>\"");
					}
					
				} else if ("/pause".equals(slackMessage.getCommand())) {
					context.pauseGame();
					
				} else if ("/quit".equals(slackMessage.getCommand())) {
					if ("roger".equals(slackMessage.getText())) {
						context.quitGame();
					} else {
						slack.sendMessage(slackMessage.getChannelName(), "In order to quit the game, provide the word 'roger' as an argument to the quit command.");
					}
					
				} else if ("/unban".equals(slackMessage.getCommand())) {
					if (params.length == 1) {
						if (params[0].matches("[1-9][0-9]*")) {
							context.unbanClient(Integer.parseInt(params[0]));
						} else {
							context.unbanClient(params[0]);
						}
					} else {
						slack.sendMessage(slackMessage.getChannelName(), "Usage: /unban <ip_address|banlist_index>");
					}
					
				} else if ("/unpause".equals(slackMessage.getCommand())) {
					context.restoreGame();
					
				} else if ("/setting".equals(slackMessage.getCommand())) {
					if (params.length == 1) {
						context.getParameter(params[0]);
					} else if (params.length == 2) {
						context.setParameter(params[0], params[1]);
					} else {
						slack.sendMessage(slackMessage.getChannelName(), "Usage: /setting <name> \"[value]\"");
					}
					
				} else if ("/resetcompany".equals(slackMessage.getCommand())) {
					if (params.length == 1 && params[0].matches("[1-9][0-9]*")) {
						context.resetCompanyOneBased(Integer.parseInt(params[0]));
					} else {
						slack.sendMessage(slackMessage.getChannelName(), "Usage: /resetcompany <company_id_1_based>");
					}
					
				} else if ("/companies".equals(slackMessage.getCommand())) {
					StringBuffer sb = new StringBuffer();
					for (CompanyData company : context.getCompanies()) {
						sb.append(":office: ");
						sb.append(company.getCompanyId() + 1);
						sb.append(" ");
						sb.append(company.getName());
						sb.append(" (");
						sb.append(company.getColorName());
						sb.append(")");
						
						sb.append("\n");
						
						for (ClientData client : context.getClients()) {
							if (client.getPlaysAs() == company.getCompanyId()) {
								sb.append(" - ");
								sb.append(client.getClientId());
								sb.append(": ");
								sb.append(client.getName());
								sb.append("\n");
							}
						}
						
						/*sb.append(" - loan ");
						sb.append(company.getLoan());
						sb.append("\n - income ");
						sb.append(company.getIncome());
						sb.append("\n - money ");
						sb.append(company.getMoney());
						sb.append("\n - value ");
						sb.append(company.getValue());
						sb.append("\n - performance ");
						sb.append(company.getPerformance());
						sb.append("\n");
						
						sb.append(" - ");
						
						sb.append(company.getBusses());
						sb.append("/");
						sb.append(company.getBusStops());
						sb.append(" :bus: ");
						
						sb.append(company.getTrains());
						sb.append("/");
						sb.append(company.getTrainStations());
						sb.append(" :bullettrain_front: ");
						
						sb.append(company.getShips());
						sb.append("/");
						sb.append(company.getHarbours());
						sb.append(" :boat: ");
						
						sb.append(company.getLorries());
						sb.append("/");
						sb.append(company.getLorryDepots());
						sb.append(" :truck: ");
						
						sb.append(company.getPlanes());
						sb.append("/");
						sb.append(company.getAirports());
						sb.append(" :airplane:");*/

						sb.append("\n");
					}
					
					slack.sendMessage(slackMessage.getChannelName(), sb.toString());
					
				} else if ("/clients".equals(slackMessage.getCommand())) {
					StringBuffer sb = new StringBuffer();

					for (ClientData client : context.getClients()) {
						if (client.getLeftTs() > 0) {
							continue;
						}
						
						sb.append(":bust_in_silhouette: ");
						sb.append(client.getClientId());
						
						if (client.getName() != null) {
							sb.append(" ");
							sb.append(client.getName());
						}
						
						sb.append("\n");
						
						if (client.getCountry() != null) {
							sb.append(" - ");
							sb.append(client.getCountry());
							sb.append(", ");
							sb.append(client.getCity());
							sb.append("\n");
						}
						
						if (client.getNetworkAddress() != null) {
							sb.append(" - ");
							sb.append(client.getNetworkAddress());
							if (client.isProxy()) {
								sb.append(" proxy");
							}
							sb.append("\n");
						}
						
						sb.append(" - joined ");
						sb.append(SDF.format(new Date(client.getJoinedTs())));
						sb.append(" UTC");
						
						sb.append(" - plays as ");
						switch (client.getPlaysAs()) {
						case CompanyInfo.DEITY:
							sb.append("deity (the server itself)"); break;
						case CompanyInfo.INACTIVE_CLIENT:
							sb.append("inactive client (should never happen)"); break;
						case CompanyInfo.NEW_COMPANY:
							sb.append("new company (ID not yet assigned)"); break;
						case CompanyInfo.NONE:
							sb.append("none (should never happen)"); break;
						case CompanyInfo.SPECTATOR:
							sb.append("spectator"); break;
						case CompanyInfo.TOWN:
							sb.append("town (should never happen)"); break;
						case CompanyInfo.WATER:
							sb.append("water (should never happen)"); break;
							default:
							{
								CompanyData companyData = context.getCompany(client.getPlaysAs());
								if (companyData == null) {
									sb.append((int)client.getPlaysAs() + 1);
								} else {
									sb.append((int)client.getPlaysAs() + 1);
									sb.append(": ");
									sb.append(companyData.getName() == null ? "n/a" : companyData.getName());
									String color = companyData.getColorName();
									if (color != null) {
										sb.append(" [");
										sb.append(color);
										sb.append("]");
									}
								}
							}
						}
						
						sb.append("\n");
						
						if (client.getJoinDate() != null) {
							sb.append(" - joined ");
							sb.append(client.getJoinDate().getDay());
							sb.append("-");
							sb.append(client.getJoinDate().getMonth());
							sb.append("-");
							sb.append(client.getJoinDate().getYear());
							sb.append(" game time \n");
						}
					}
					
					slack.sendMessage(slackMessage.getChannelName(), sb.toString());
				
				} else if ("/server".equals(slackMessage.getCommand())) {
					
					slack.sendMessage(slackMessage.getChannelName(),
							":computer: Server " + context.getAddress() + ":" + context.getPort() + "\n"
							+ "Currently " + (context.isGameConnected() ? "connected" : "disconnected") + "\n"
							+ "Database ID " + context.getDbGameId() + "\n"
							+ "Game-Date " + context.getCurrentDate() + "\n"
							+ "Performance " + context.getPerformance() + " ms/game-day\n"
							+ "No. clients " + context.getClients().size() + "\n"
							+ "No. companies " + context.getCompanies().size());
					
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
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.ROOT);
	private static final Pattern P_PARAM = Pattern.compile("\\\"(?<v1>[^\"]*)\\\"|(?<v2>[^ \t\"]+)");
	private static String[] parameters(String line) {
		List<String> result = new ArrayList<String>();
		
		Matcher m = P_PARAM.matcher(line);
		while (m.find()) {
			if (m.group("v1") != null) {
				result.add(m.group("v1"));
			} else {
				result.add(m.group("v2"));
			}
		}
		
		return result.toArray(new String[result.size()]);
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
				slack.registerCommand("/kickuser", Main::onCommand);
				slack.registerCommand("/ban", Main::onCommand);
				slack.registerCommand("/pause", Main::onCommand);
				slack.registerCommand("/quit", Main::onCommand);
				slack.registerCommand("/unban", Main::onCommand);
				slack.registerCommand("/unpause", Main::onCommand);
				slack.registerCommand("/setting", Main::onCommand);
				slack.registerCommand("/resetcompany", Main::onCommand);
				slack.registerCommand("/clients", Main::onCommand);
				slack.registerCommand("/companies", Main::onCommand);
				slack.registerCommand("/server", Main::onCommand);
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
		
		if (configuration.getDatabase() != null) {
			try {
				db = new DatabaseConnector(configuration.getDatabase());
			} catch (SQLException ex) {
				LOGGER.error("Failed to initialize the database.", ex);
			}
		}
		
		/*for (String s : configuration.getPlayerNames()) {
			System.out.println(s);
		}*/
		//long dbGameId = 1;//db.createNewGame(new GameData("127.0.0.1", 12345, "serwer", "mapa", 12345, 2019, 1024, 2048));
		//LOGGER.info("create game {}", dbGameId);
		//Map<Long, GameData> games = db.getGames(true);
		/*for (Entry<Long, GameData> entry : games.entrySet()) {
			System.out.println(entry.getValue().getAddress());
			System.out.println(new Date(entry.getValue().getStartedTs()));
			System.out.println(entry.getValue().getFinishedTs());
		}*/
		
		/*CompanyData c0 = new CompanyData((byte)0);
		c0.updateData(new CompanyInfo((byte)0, "Zero", "Mr. Zero", Color.COLOUR_BLUE, true, (byte)0, new byte[] { 0, 0, 0, 0 }));
		CompanyEconomy e0 = new CompanyEconomy(1234, 5678, 9012, 345, new long[] { 6789,  2345 }, new int[] { 678, 901 }, new int[] { 234, 567 });
		c0.updateData(e0);
		CompanyStatistics s0 = new CompanyStatistics(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		c0.updateData(s0);
		
		e0 = new CompanyEconomy(1, 2, 3, 4, new long[] { 5,  6 }, new int[] { 7, 8 }, new int[] { 9, 10 });
		s0 = new CompanyStatistics(11, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		
		//LOGGER.info("create comp {}", db.createOrUpdateCompany(dbGameId, c0));
		LOGGER.info("store econo {}", db.storeEconomicData(dbGameId, c0.getCompanyId(), e0));
		LOGGER.info("store stats {}", db.storeStatisticalData(dbGameId, c0.getCompanyId(), s0));
		*/
		//LOGGER.warn("Key {}", key);
		//LOGGER.info("closed {}", db.closeGame(4));
		/*CompanyData cmp = new CompanyData((byte)7);
		cmp.updateData(new CompanyEconomy(10, 20, 30, 40, new long[] { 60,  70}, new int[] { 80, 90}, new int[] {100, 110}));
		cmp.updateData(new CompanyStatistics(1, 3, 5, 7, 9, 11, 13, 15, 17, 19));
		long key = db.createNewGame(new GameData("serwer", "mapa", 12345, 2019, 1024, 2048));
		LOGGER.info("result1 {}", db.createOrUpdateCompany(key, cmp));
		//LOGGER.info("result2 {}", db.closeCompany(4, (byte)7, new de.miltschek.openttdadmin.data.Date(12345), ClosureReason.Bankrupt));
		LOGGER.info("result3 {}", db.storeEconomicData(key, cmp));
		LOGGER.info("result4 {}", db.storeStatisticalData(key, cmp));
		
		ClientData clientData = new ClientData(77, GeoIp.lookup("4.4.4.4"));
		clientData.setClientInfo(new ClientInfo(77, "4.4.4.4", "john", Language.Afrikaans, new de.miltschek.openttdadmin.data.Date(1234), cmp.getCompanyId()));
		
		LOGGER.info("result4 {}", db.createOrUpdatePlayer(key, clientData));
		LOGGER.info("result4 {}", db.storePlayer(key, clientData.getClientId(), cmp.getCompanyId()));
		LOGGER.info("result4 {}", db.playerQuit(key, clientData.getClientId()));
		*/
		//if (true) return;
		
		for (Game game : configuration.getGames()) {
			LOGGER.info("Configuring OTTD Admin client to connect to {} on port {}.", game.getAddress(), game.getPort());
			
			OttdAdminClient admin = new OttdAdminClient(game.getAddress(), game.getPort(), game.getPassword());

			admin.setDeliveryChatMessages(true);
			admin.setUpdateClientInfos(true);
			admin.setUpdateCompanyInfos(true);
			//admin.setDeliveryCommandLogs(true); // TODO test
			admin.setUpdateCompanyEconomyInfos(FrequencyLong.Quarterly);
			admin.setUpdateCompanyStatistics(FrequencyLong.Quarterly);
			admin.setUpdateDates(Frequency.Daily);
			
			ResetLock resetLock = new ResetLock();
			Context context = new Context(configuration, game, resetLock, admin, slack, game.getSlackChannel(), game.getSlackAdminChannel(), googleTranslate, db);
			admin.addChatListener(new ChatListener(context));
			admin.addClientListener(new CustomClientListener(context));
			admin.addCompanyListener(new CustomCompanyListener(context));
			admin.addServerListener(new CustomServerListener(context));

			LOGGER.debug("Starting the OTTD Admin client address {} port {}.", game.getAddress(), game.getPort());
			admin.start();
			
			ottdAdminClients.add(admin);
			
			if (game.getSlackChannel() != null) {
				if (slackToContext.put(game.getSlackChannel(), context) != null) {
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
		
		if (db != null) {
			try {
				db.close();
			} catch (IOException e) {
				LOGGER.warn("Failed to close the database connection.", e);
			}
		}
	}
}
