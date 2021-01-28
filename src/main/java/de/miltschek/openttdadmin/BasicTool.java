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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.ClientListenerAdapter;
import de.miltschek.openttdadmin.data.ErrorCode;
import de.miltschek.openttdadmin.data.ServerInfo;
import de.miltschek.openttdadmin.data.ServerListenerAdapter;
import de.miltschek.openttdadmin.data.ChatMessage.Recipient;
import de.miltschek.openttdadmin.integration.GeoIp;
import de.miltschek.openttdadmin.integration.SlackClient;

/**
 * Basic game administration tool.
 */
public class BasicTool {
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
		
		slack = new SlackClient(channel, token);
		
		admin = new OttdAdminClient(host, port, password);
		admin.setDeliveryChatMessages(true);
		admin.setUpdateClientInfos(true);
		
		admin.addChatListener(new ChatListener());
		admin.addClientListener(new CustomClientListener());
		admin.addServerListener(new CustomServerListener());
		
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

		try {
			admin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class ChatListener implements Consumer<ChatMessage> {
		public void accept(ChatMessage t) {
			if (t.getMessage() == null) {
				// nix
			} else if (t.getMessage().startsWith("!admin")) {
		    	if (slack != null) {
		    		slack.sendMessage(":boom: " + t.getSenderId() + " " + t.getMessage());
		    	}
			} else if (t.getMessage().equals("!reset")) {
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
					slack.sendMessage(":information_source: client "
							+ t.getSenderId()
							+ " requested a company to be reset");
				}
			} else if (t.getMessage().equals("!help")) {
				System.out.println("User " + t.getSenderId() + " requested help.");
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "Available commands:"));
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "!admin <message>: sends the message to the server's admin"));
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "!reset: resets your company; you will be kicked of the server, so please re-join"));
			} else if (t.getSenderId() != 1 && !t.getMessage().isEmpty()) {
				if (slack != null) {
					slack.sendMessage(":pencil: " + t.getSenderId() + " " + t.getMessage());
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
								admin.executeRCon("kick " + entry.getKey() + " \"resetting company; please re-join\"");
								System.out.println("kicking " + entry.getKey());

								// ugly: since we don't know, how many clients are still to be delivered
								// we need to try our luck with each client to achieve the goal = reset the company
								admin.executeRCon("resetcompany " + (companyToReset + 1));
								System.out.println("resetting company id " + companyToReset);
							}
						}
						
						// don't need to kick same players again in next iterations
						clientsCompanies.clear();
					}
					
					return;
				}
			}
			
			// will be executed only if not within the reset-company-process
			GeoIp geoIp = GeoIp.lookup(clientInfo.getNetworkAddress());
			if (geoIp != null) {
				admin.sendChat(
						new ChatMessage(
								0,
								Recipient.All,
								0,
								"-> Warm welcome to " + clientInfo.getClientName() + " coming from " + geoIp.getCountry() + "! <-"));
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
				System.err.println("Failed to send on-new-client " + ex.getClass().getSimpleName() + " " + ex.getMessage());
			}
		}
		
		@Override
		public void clientDisconnected(int clientId) {
			if (slack != null) {
				slack.sendMessage(":information_source: client left " + clientId);
			}
		}
		
		@Override
		public void clientError(int clientId, ErrorCode errorCode) {
			if (slack != null) {
				slack.sendMessage(":information_source: client error " + clientId + " " + errorCode);
			}
		}
	}
	
	private static class CustomServerListener extends ServerListenerAdapter {
		@Override
		public void newGame() {
			if (slack != null) {
				slack.sendMessage(":information_source: new game");
			}
		}
		
		@Override
		public void serverInfoReceived(ServerInfo serverInfo) {
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
