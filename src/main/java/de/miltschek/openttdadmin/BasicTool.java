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
package de.miltschek.openttdadmin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ChatMessage.Recipient;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.ClientListenerAdapter;
import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.Color;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.CompanyListenerAdapter;
import de.miltschek.openttdadmin.data.ErrorCode;
import de.miltschek.openttdadmin.data.ServerInfo;
import de.miltschek.openttdadmin.data.ServerListenerAdapter;
import de.miltschek.openttdadmin.integration.GeoIp;
import de.miltschek.openttdadmin.integration.GoogleTranslate;
import de.miltschek.openttdadmin.integration.SlackClient;

/**
 * Basic game administration tool.
 */
public class BasicTool {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicTool.class);

	private static OttdAdminClient admin;
	private static SlackClient slack;
	
	/** A lock for the subsequent objects for the company-reset process. */
	private static Object resetCompanyLock = new Object();
	/** Time window hoping to be enough for a successful company reset process. */
	private static final long RESET_TIME_WINDOW = 10000;
	/** Timestamp of the last company reset request. */
	private static long lastResetRequest;
	/** Client ID of the current company reset request. */
	private static int resetRequestClientId;
	/** Flag indicating whether a company has been found. */
	private static boolean companyFound;
	/** Company ID that is to be reset. */
	private static byte companyToReset;
	/** Collection of client IDs and their company IDs. */
	private static Map<Integer, Byte> clientsCompanies = new HashMap<Integer, Byte>();
	
	private static final Pattern RENAME_PATTERN = Pattern.compile("[!]name[ \\t]+(\"|)(?<value>(?:[^\"\\\\]|\\\\.)*)\\1");
	
	private static final Map<Integer, ClientData> clients = new HashMap<>();
	private static final Map<Integer, CompanyData> companies = new HashMap<>();
	
	private static class ClientData {
		private final int id;
		private String name;
		private String networkAddress;
		private CompanyData playAs;
		private String country;
		private String city;
		private boolean proxy;
		private boolean proxySet;
		
		public ClientData(int id) {
			this.id = id;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getNetworkAddress() {
			return networkAddress;
		}
		
		public void setNetworkAddress(String networkAddress) {
			this.networkAddress = networkAddress;
		}
		
		public CompanyData getPlayAs() {
			return playAs;
		}
		
		public void setPlayAs(CompanyData playAs) {
			this.playAs = playAs;
		}
		
		public String getCountry() {
			return country;
		}
		
		public void setCountry(String country) {
			this.country = country;
		}
		
		public String getCity() {
			return city;
		}
		
		public void setCity(String city) {
			this.city = city;
		}
		
		public boolean isProxy() {
			return proxy;
		}
		
		public void setProxy(boolean proxy) {
			this.proxy = proxy;
			this.proxySet = true;
		}
		
		public boolean isProxySet() {
			return proxySet;
		}
	}
	
	private static class CompanyData {
		private final int id;
		private String name;
		private Color color;
		private boolean passwordProtected;
		private boolean passwordProtectedSet;
		
		public CompanyData(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Color getColor() {
			return color;
		}
		
		public void setColor(Color color) {
			this.color = color;
		}
		
		public boolean isPasswordProtected() {
			return passwordProtected;
		}
		
		public void setPasswordProtected(boolean passwordProtected) {
			this.passwordProtected = passwordProtected;
			this.passwordProtectedSet = true;
		}
		
		public boolean isPasswordProtectedSet() {
			return passwordProtectedSet;
		}
	}

	/**
	 * Entry point of the application.
	 * @param args arguments in the fixed order: OTTD server address, port number, admin password, slack channel name, slack token
	 */
	public static void main(String[] args) {
		String host;
		int port;
		String password;
		String channel;
		String token;
		
		if (args.length == 5) {
			host = args[0];
			port = Integer.parseInt(args[1]);
			password = args[2];
			channel = args[3];
			token = args[4];
		} else {
			System.err.println("Usage:");
			System.err.println(BasicTool.class.getName() + " <address> <port> <admin_password> <slack_channel> <slack_token>");
			return;
		}
		
		LOGGER.info("Starting the basic tool.");
		LOGGER.debug("Configuring slack connector for the channel {}.", channel);
		slack = new SlackClient(channel, token);
		
		LOGGER.debug("Configuring OTTD Admin client to connect to {} on port {}.", host, port);
		admin = new OttdAdminClient(host, port, password);
		admin.setDeliveryChatMessages(true);
		admin.setUpdateClientInfos(true);
		admin.setUpdateCompanyInfos(true);
		
		admin.addChatListener(new ChatListener());
		admin.addClientListener(new CustomClientListener());
		admin.addCompanyListener(new CustomCompanyListener());
		admin.addServerListener(new CustomServerListener());
		
		LOGGER.debug("Starting the OTTD Admin client.");
		admin.start();
		
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
		try {
			admin.close();
		} catch (IOException e) {
			LOGGER.warn("Failed to close the OTTD Admin client.", e);
		}
	}
	
	private static class ChatListener implements Consumer<ChatMessage> {
		public void accept(ChatMessage t) {
			String senderId;
			
			ClientData clientData;
			if ((clientData = clients.get(t.getSenderId())) != null) {
				if (clientData.getName() == null) {
					senderId = String.valueOf(t.getSenderId());
				} else {
					senderId = clientData.getName() + "(" + t.getSenderId() + ")";
				}
				
				CompanyData companyData;
				if ((companyData = clientData.getPlayAs()) != null) {
					if (companyData.getColor() == null) {
						senderId += "[" + companyData.getColor() + "]";
					} else {
						senderId += "[" + companyData.getId() + "]";
					}
				}
			} else {
				senderId = String.valueOf(t.getSenderId());
			}
			
			if (t.getMessage() == null) {
				// nix
				LOGGER.debug("A null chat message has been received from {} to {} private {} company {} public {}.", t.getSenderId(), t.getRecipientId(), t.isPrivate(), t.isCompany(), t.isPublic());
			} else if (t.getMessage().startsWith("!admin")) {
				LOGGER.warn("Admin action has been requested by {}: {}.", t.getSenderId(), t.getMessage());
		    	
				if (slack != null) {
		    		slack.sendMessage(":boom: " + senderId + " " + t.getMessage());
		    		admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "Your message has been sent to the admin. Thank you!"));
		    	}
			} else if (t.getMessage().equals("!reset")) {
				LOGGER.info("Company reset has been requested by {}.", t.getSenderId());

				// need to find, what company is playing the sender
				// then, what other clients do play the same company
				// then, kick them
				// then, reset the company
				// (did not find a way to reset a company with active clients, TODO?)
				synchronized (resetCompanyLock) {
					if (lastResetRequest < System.currentTimeMillis() - RESET_TIME_WINDOW) {
						lastResetRequest = System.currentTimeMillis();
						resetRequestClientId = t.getSenderId();
						companyFound = false;
						clientsCompanies.clear();
					} else {
						admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "Another reset request still being processed. Please retry in a few seconds."));
					}
				}
				
				admin.requestAllClientsInfo();
				
				if (slack != null) {
					slack.sendMessage(":information_source: "
							+ senderId
							+ " resets the company");
				}
			} else if (t.getMessage().startsWith("!name")) {
				LOGGER.info("Name change has been requested by {} raw {}.", t.getSenderId(), t.getMessage());

				Matcher m = RENAME_PATTERN.matcher(t.getMessage());
				if (m.find()) {
					String newName = m.group("value");
					admin.executeRCon("client_name " + t.getSenderId() + " \"" + newName + "\"");
					
					if (slack != null) {
						slack.sendMessage(":information_source: client "
								+ senderId
								+ " requested a new name " + newName);
					}
				}
			} else if (t.getMessage().equals("!help")) {
				LOGGER.info("User {} requested help.", t.getSenderId());

				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "Available commands:"));
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "!admin <message>: sends the message to the server's admin"));
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "!reset: resets your company; you will be kicked of the server, so please re-join"));
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "!name <new_name>: changes your name; surround multiple words with double quotes"));
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "!dict <message>: tries to translate your message to English"));
			} else if (t.getMessage().startsWith("!dict ") && t.getMessage().length() > 6) {
				LOGGER.info("User {} requested translation.", t.getSenderId());
				
				GoogleTranslate.Result translation = GoogleTranslate.translateToEnglish(t.getMessage().substring(6));
				if (translation.isSuccess()) {
					if (translation.getSourceLanguage().equals("en")) {
						admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "It was English already. Nothing to translate."));
					} else {
						admin.sendChat(new ChatMessage(0, Recipient.All, 0, "[translation/" + translation.getSourceLanguage() + "] " + translation.getTranslatedText()));
					}
					
					if (slack != null) {
						slack.sendMessage(":point_right: " + senderId + " " + translation.getSourceLanguage() + ": " + translation.getTranslatedText());
					}
				} else {
					admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "Sorry, translation did not work."));
				}
			} else if (t.getMessage().startsWith("!")) {
				LOGGER.debug("User {} entered an invalid command {}.", t.getSenderId(), t.getMessage());

				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "No such command. For help, enter !help"));
			} else if (t.getSenderId() != 1 && !t.getMessage().isEmpty()) {
				LOGGER.debug("User {} sent a message to {} {}: {}.",
						t.getSenderId(),
						t.isPrivate() ? "user" : t.isCompany() ? "company" : t.isPublic() ? "all" : "unknown",
						t.getRecipientId(),
						t.getMessage());

				if (slack != null) {
					GoogleTranslate.Result translation = GoogleTranslate.translateToEnglish(t.getMessage());
					if (translation.isSuccess() && !translation.getSourceLanguage().equals("en")) {
						slack.sendMessage(":pencil: " + senderId + " " + t.getMessage() + "\r\n"
								+ ":point_right: " + translation.getTranslatedText());
					} else {
						slack.sendMessage(":pencil: " + senderId + " " + t.getMessage());
					}
				}
			}
		}
	}

	private static class CustomClientListener extends ClientListenerAdapter {
		@Override
		public void clientInfoReceived(ClientInfo clientInfo) {
			// build a client-company map if a reset has been requested
			synchronized (resetCompanyLock) {
				if (lastResetRequest >= System.currentTimeMillis() - RESET_TIME_WINDOW) {
					// keep all clients and their companies in cache
					clientsCompanies.put(clientInfo.getClientId(), clientInfo.getPlayAs());
					
					if (resetRequestClientId == clientInfo.getClientId()) {
						// found the company to be reset
						companyToReset = clientInfo.getPlayAs();
						companyFound = true;
					}
					
					// optimization: it's enough to try our luck only if at least the current client plays the company
					if (companyFound && companyToReset == clientInfo.getPlayAs()) {
						for (Entry<Integer, Byte> entry : clientsCompanies.entrySet()) {
							if (entry.getValue() == companyToReset) {
								LOGGER.info("Kicking user {}.", entry.getKey());
								admin.executeRCon("kick " + entry.getKey() + " \"resetting company; please re-join\"");

								// ugly: since we don't know, how many clients are still to be delivered
								// we need to try our luck with each client to achieve the goal = reset the company
								LOGGER.info("Trying to reset the company {}.", companyToReset);
								admin.executeRCon("resetcompany " + (companyToReset + 1));
							}
						}
						
						// don't need to kick same players again in next iterations
						clientsCompanies.clear();
					}
					
					return;
				}
			}
			
			// --- cache data
			ClientData clientData = clients.get(clientInfo.getClientId());
			if (clientData == null) {
				clientData = new ClientData(clientInfo.getClientId());
				clients.put(clientInfo.getClientId(), clientData);
			}
			
			clientData.setName(clientInfo.getClientName());
			clientData.setNetworkAddress(clientInfo.getNetworkAddress());
			
			int playAs = clientInfo.getPlayAs() & 0xff;
			CompanyData companyData = companies.get(playAs);
			if (companyData == null) {
				companyData = new CompanyData(playAs);
				companies.put(playAs, companyData);
			}
			
			clientData.setPlayAs(companyData);
			// --- end of cache data
			
			// will be executed only if not within the reset-company-process
			GeoIp geoIp = GeoIp.lookup(clientInfo.getNetworkAddress());
			if (geoIp != null) {
				LOGGER.info("User info {} IP {} from {}, {} proxy {}.", clientInfo.getClientId(), clientInfo.getNetworkAddress(), geoIp.getCountry(), geoIp.getCity(), geoIp.isProxy());
				
				// --- cache data
				clientData.setCountry(geoIp.getCountry());
				clientData.setCity(geoIp.getCity());
				clientData.setProxy(geoIp.isProxy());
				// --- end of cache data
				
				String welcomeMessage = "-> Warm welcome to " + clientInfo.getClientName() + " coming from " + geoIp.getCountry() + "! <-";
				String cc = geoIp.getCountryCode();
				if (cc == null) {
					// nix
				} else if (cc.equals("DE")) {
					welcomeMessage = "-> Moin moin, " + clientInfo.getClientName() + " aus Deutschland! <-";
				} else if (cc.equals("AT")) {
					welcomeMessage = "-> Servus, " + clientInfo.getClientName() + " aus Österreich! <-";
				} else if (cc.equals("AU")) {
					welcomeMessage = "-> Hey, how are you, " + clientInfo.getClientName() + " from Australia? <-";
				} else if (cc.equals("BE")) {
					welcomeMessage = "-> Hoe gaat het, " + clientInfo.getClientName() + " uit België? <-";
				} else if (cc.equals("BR")) {
					welcomeMessage = "-> Como vai, " + clientInfo.getClientName() + " do Brasil? <-";
				} else if (cc.equals("CH")) {
					welcomeMessage = "-> Grüezi, " + clientInfo.getClientName() + " aus der Schweiz! <-";
				} else if (cc.equals("CZ")) {
					welcomeMessage = "-> Jak se máš, " + clientInfo.getClientName() + " z Čech? <-";
				} else if (cc.equals("DK")) {
					welcomeMessage = "-> Hvordan har du det, " + clientInfo.getClientName() + " fra Danmark? <-";
				} else if (cc.equals("ES")) {
					welcomeMessage = "-> ¿Cómo estás, " + clientInfo.getClientName() + " de España? <-";
				} else if (cc.equals("FI")) {
					welcomeMessage = "-> Kuinka voit, " + clientInfo.getClientName() + " Suomesta? <-";
				} else if (cc.equals("FR")) {
					welcomeMessage = "-> Comment vas-tu, " + clientInfo.getClientName() + " de France? <-";
				} else if (cc.equals("GB")) {
					welcomeMessage = "-> Pleased to meet you, " + clientInfo.getClientName() + " from " + geoIp.getCountry() + "! <-";
				} else if (cc.equals("HU")) {
					welcomeMessage = "-> Hogy vagy, " + clientInfo.getClientName() + "  Magyarországról? <-";
				} else if (cc.equals("IE")) {
					welcomeMessage = "-> Conas atá tú, " + clientInfo.getClientName() + " as Éirinn? <-";
				} else if (cc.equals("IT")) {
					welcomeMessage = "-> Come stai, " + clientInfo.getClientName() + " dall'Italia? <-";
				} else if (cc.equals("LI")) {
					welcomeMessage = "-> Hoi Du, " + clientInfo.getClientName() + " aus Lichtenstein! <-";
				} else if (cc.equals("LU")) {
					welcomeMessage = "-> Wéi geet et dir, " + clientInfo.getClientName() + " aus Lëtzebuerg? <-";
				} else if (cc.equals("NO")) {
					welcomeMessage = "-> Hvordan har du det, " + clientInfo.getClientName() + " fra Norge? <-";
				} else if (cc.equals("PL")) {
					welcomeMessage = "-> Siema, " + clientInfo.getClientName() + " z Polski! <-";
				} else if (cc.equals("PT")) {
					welcomeMessage = "-> Como está você, " + clientInfo.getClientName() + " de Portugal? <-";
				} else if (cc.equals("RU")) {
					welcomeMessage = "-> Как дела, " + clientInfo.getClientName() + " из России? <-";
				} else if (cc.equals("SE")) {
					welcomeMessage = "-> Hur mår du, " + clientInfo.getClientName() + " från Sverige? <-";
				} else if (cc.equals("SI")) {
					welcomeMessage = "-> Kako si, " + clientInfo.getClientName() + " iz Slovaške? <-";
				} else if (cc.equals("SK")) {
					welcomeMessage = "-> Ako sa máš, " + clientInfo.getClientName() + " zo Slovenska? <-";
				} else if (cc.equals("UA")) {
					welcomeMessage = "-> Як справи, " + clientInfo.getClientName() + " з України? <-";
				} else if (cc.equals("US")) {
					welcomeMessage = "-> What’s up, " + clientInfo.getClientName() + " coming from the US? <-";
				}
				
				admin.sendChat(
						new ChatMessage(
								0,
								Recipient.All,
								0,
								welcomeMessage));
			} else {
				LOGGER.info("User info {} IP {} no geo info.", clientInfo.getClientId(), clientInfo.getNetworkAddress());
			}

			if (slack != null) {
				slack.sendMessage(":information_source: client "
						+ clientInfo.getClientId()
						+ " " + clientInfo.getClientName()
						+ " " + clientInfo.getNetworkAddress()
						+ ((geoIp != null) ? (" " + geoIp.getCountry() + ", " + geoIp.getCity()) + (geoIp.isProxy() ? ", proxy" : ""): ""));
			}
		}
		
		@Override
		public void clientConnected(int clientId) {
			LOGGER.info("New user {}.", clientId);
			
			// --- cache data
			if (clients.get(clientId) == null) {
				clients.put(clientId, new ClientData(clientId));
			}
			// --- end of cache data
			
			if (slack != null) {
				slack.sendMessage(":information_source: new client " + clientId);
			}

			try {
				File onNewClient = new File("on_new_client.txt");
				if (onNewClient.exists()) {
					for (String line : Files.readAllLines(onNewClient.toPath())) {
						admin.sendChat(
								new ChatMessage(
										0,
										Recipient.Client,
										clientId,
										line));
					}
				}
			} catch (Exception ex) {
				LOGGER.error("Failed to send on-new-client message.", ex);
			}
		}
		
		@Override
		public void clientDisconnected(int clientId) {
			LOGGER.info("User {} disconnected.", clientId);
			
			// --- cache data
			if (clients.get(clientId) != null) {
				clients.remove(clientId);
			}
			// --- end of cache data

			if (slack != null) {
				slack.sendMessage(":information_source: client left " + clientId);
			}
		}
		
		@Override
		public void clientError(int clientId, ErrorCode errorCode) {
			LOGGER.info("Client {} error {}.", clientId, errorCode);
			
			// --- cache data
			if (clients.get(clientId) != null) {
				clients.remove(clientId);
			}
			// --- end of cache data

			if (slack != null) {
				slack.sendMessage(":information_source: client error " + clientId + " " + errorCode);
			}
		}
		
		@Override
		public void clientUpdated(int clientId, String clientName, byte playAs) {
			LOGGER.info("Client {} update name {}, update company {}.", clientId, clientName, playAs);
			
			// --- cache data
			ClientData clientData = clients.get(clientId);
			if (clientData == null) {
				clientData = new ClientData(clientId);
				clients.put(clientId, clientData);
			}
			
			clientData.setName(clientName);
			
			int playAsInt = playAs & 0xff;
			CompanyData companyData = companies.get(playAsInt);
			if (companyData == null) {
				companyData = new CompanyData(playAsInt);
				companies.put(playAsInt, companyData);
			}
			
			clientData.setPlayAs(companyData);
			// --- end of cache data
		}
	}
	
	private static class CustomCompanyListener extends CompanyListenerAdapter {
		@Override
		public void companyCreated(byte companyId) {
			LOGGER.info("New company created {}.", companyId);
			
			// --- cache data
			int companyIdInt = companyId & 0xff;
			if (!companies.containsKey(companyIdInt)) {
				companies.put(companyIdInt, new CompanyData(companyIdInt));
			}
			// --- end of cache data
		}
		
		@Override
		public void companyInfoReceived(CompanyInfo companyInfo) {
			LOGGER.info("Company info received {}, name {}, color {}, password-protected {}.", companyInfo.getIndex(), companyInfo.getColor(), companyInfo.isPasswordProtected());
			
			// --- cache data
			int companyIdInt = companyInfo.getIndex() & 0xff;
			CompanyData companyData = companies.get(companyIdInt);
			if (companyData == null) {
				companyData = new CompanyData(companyIdInt);
				companies.put(companyIdInt, companyData);
			}
			
			companyData.setColor(companyInfo.getColor());
			companyData.setName(companyInfo.getCompanyName());
			companyData.setPasswordProtected(companyInfo.isPasswordProtected());
			// --- end of cache data
			
			if (slack != null) {
				slack.sendMessage(":information_source: company info "
						+ companyInfo.getColor()
						+ " " + companyInfo.getCompanyName()
						+ " " + companyInfo.getManagerName()
						+ " " + (companyInfo.isPasswordProtected() ? "pwd" : "no-pwd"));
			}
		}
		
		@Override
		public void companyRemoved(byte companyId, ClosureReason closureReason) {
			LOGGER.info("Company removed {}, reason {}.", companyId, closureReason);
			
			// --- cache data
			int companyIdInt = companyId & 0xff;
			CompanyData companyData = companies.remove(companyIdInt);
			// --- end of cache data
			
			if (slack != null) {
				slack.sendMessage(":information_source: company closed "
						+ (companyData == null ? "?" : String.valueOf(companyData.getColor())) + "/" + companyId
						+ " " + closureReason);
			}
		}
		
		@Override
		public void companyUpdated(CompanyInfo companyInfo) {
			LOGGER.info("Company updated received {}, name {}, color {}, password-protected {}.", companyInfo.getIndex(), companyInfo.getColor(), companyInfo.isPasswordProtected());
			
			// --- cache data
			int companyIdInt = companyInfo.getIndex() & 0xff;
			CompanyData companyData = companies.get(companyIdInt);
			if (companyData == null) {
				companyData = new CompanyData(companyIdInt);
				companies.put(companyIdInt, companyData);
			}
			
			companyData.setColor(companyInfo.getColor());
			companyData.setName(companyInfo.getCompanyName());
			companyData.setPasswordProtected(companyInfo.isPasswordProtected());
			// --- end of cache data
			
			if (slack != null) {
				slack.sendMessage(":information_source: company update "
						+ companyInfo.getColor()
						+ " " + companyInfo.getCompanyName()
						+ " " + companyInfo.getManagerName()
						+ " " + (companyInfo.isPasswordProtected() ? "pwd" : "no-pwd"));
			}
		}
	}
	
	private static class CustomServerListener extends ServerListenerAdapter {
		@Override
		public void newGame() {
			LOGGER.info("New game started.");
			
			if (slack != null) {
				slack.sendMessage(":information_source: new game");
			}
		}
		
		@Override
		public void serverInfoReceived(ServerInfo serverInfo) {
			LOGGER.info("Server name {} revision {} dedicated {} map {} seed {} landscape {} start-year {} x {} y {}.",
					serverInfo.getServerName(),
					serverInfo.getNetworkRevision(),
					serverInfo.isServerDedicated(),
					serverInfo.getMapName(),
					serverInfo.getGenerationSeed(),
					serverInfo.getLandscape(),
					serverInfo.getStartingYear(),
					serverInfo.getMapSizeX(),
					serverInfo.getMapSizeY());
			
			System.out.println(" - server name = " + serverInfo.getServerName());
			System.out.println(" - network revision = " + serverInfo.getNetworkRevision());
			System.out.println(" - dedicated = " + serverInfo.isServerDedicated());
			System.out.println(" - map name = " + serverInfo.getMapName());
			System.out.println(" - generation seed = " + serverInfo.getGenerationSeed());
			System.out.println(" - landscape = " + serverInfo.getLandscape());
			System.out.println(" - starting year = " + serverInfo.getStartingYear());
			System.out.println(" - map size x = " + serverInfo.getMapSizeX());
			System.out.println(" - map size y = " + serverInfo.getMapSizeY());
		}
	}
}
