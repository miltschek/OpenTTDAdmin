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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.ClientListenerAdapter;
import de.miltschek.openttdadmin.data.ServerInfo;
import de.miltschek.openttdadmin.data.ServerListenerAdapter;
import de.miltschek.openttdadmin.data.ChatMessage.Recipient;
import de.miltschek.openttdadmin.integration.SlackClient;

public class BasicTool {
	private static OttdAdminClient admin;
	private static SlackClient slack;
	
	private static Set<Integer> resetRequests = new HashSet<Integer>();

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
				admin.requestClientInfo(t.getSenderId());

				synchronized (resetRequests) {
					resetRequests.add(t.getSenderId());
				}
			} else if (t.getMessage().equals("!help")) {
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "Available commands:"));
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "!admin <message>: sends the message to the server's admin"));
				admin.sendChat(new ChatMessage(0, Recipient.Client, t.getSenderId(), "!reset: resets your company; you will be kicked of the server, so please re-join"));
			} else if (t.getSenderId() != 1) {
				if (slack != null) {
					slack.sendMessage(t.getSenderId() + " " + t.getMessage());
				}
			}
		}
	}

	private static class CustomClientListener extends ClientListenerAdapter {
		@Override
		public void clientInfoReceived(ClientInfo clientInfo) {
			boolean found;
			synchronized (resetRequests) {
				found = resetRequests.remove(clientInfo.getClientId());
			}
			
			if (found) {
				admin.executeRCon("kick " + clientInfo.getClientId() + " \"resetting company\"");
				admin.executeRCon("resetcompany " + (clientInfo.getPlayAs() + 1));
			} else {
				if (slack != null) {
					slack.sendMessage(":information_source: client " + clientInfo.getClientId() + " " + clientInfo.getClientName() + " " + clientInfo.getNetworkAddress());
				}
			}
		}
		
		@Override
		public void clientConnected(int clientId) {
			if (slack != null) {
				slack.sendMessage(":information_source: new client " + clientId);
			}
		}
		
		@Override
		public void clientDisconnected(int clientId) {
			if (slack != null) {
				slack.sendMessage(":information_source: client left " + clientId);
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
