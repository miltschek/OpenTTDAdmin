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

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.ClientListenerAdapter;
import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.CompanyEconomy;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.CompanyListenerAdapter;
import de.miltschek.openttdadmin.data.CompanyStatistics;
import de.miltschek.openttdadmin.data.Date;
import de.miltschek.openttdadmin.data.ErrorCode;
import de.miltschek.openttdadmin.data.Frequency;
import de.miltschek.openttdadmin.data.FrequencyLong;
import de.miltschek.openttdadmin.data.ServerInfo;
import de.miltschek.openttdadmin.data.ServerListenerAdapter;

/**
 * A simple application for demonstration purposes on how to use the library.
 * The application requires either one argument, which is the admin password
 * or three arguments, which are: the address of the OTTD server, the admin port
 * and the admin password. Per default the app connects to the localhost.
 */
public class Demo {
	/**
	 * Entry point of the demo application.
	 * @param args either one argument, which is the admin password
	 * or three arguments, which are: the address of the OTTD server, the admin port
	 * and the admin password.
	 */
	public static void main(String[] args) {
		String host;
		int port;
		String password;
		
		if (args.length == 1) {
			// use the localhost and the default port number
			host = OttdAdminClient.DEFAULT_HOST;
			port = OttdAdminClient.DEFAULT_PORT;
			password = args[0];
		} else if (args.length == 3) {
			// use the address and port from the command line
			host = args[0];
			port = Integer.parseInt(args[1]);
			password = args[2];
		} else {
			// show usage
			System.err.println("Usage:");
			System.err.println(Demo.class.getName() + " <admin_password>");
			System.err.println(" - or -");
			System.err.println(Demo.class.getName() + " <address> <port> <admin_password>");
			return;
		}
		
		// create the client
		OttdAdminClient client = new OttdAdminClient(host, port, password);
		
		// register a chat listener - it will receive all chat messages from the game
		// don't forget to tell the server, you want to get the chats at all (see below)
		client.addChatListener(new Consumer<ChatMessage>() {
			public void accept(ChatMessage t) {
				System.out.println("chat message received");

				System.out.println(" - sender = " + t.getSenderId());
				if (t.isPublic()) {
					System.out.println(" - to everyone");
				} else if (t.isCompany()) {
					System.out.println(" - to company = " + t.getRecipientId());
				} else if (t.isPrivate()) {
					System.out.println(" - to client = " + t.getRecipientId());
				} else {
					System.out.println(" - recipient incorrectly defined = " + t.getRecipientId());
				}
				System.out.println(" - message = " + t.getMessage());
			}
		});
		
		// register a listener of client-related notifications
		// don't forget to tell the server, you want to get the notification at all (see below)
		// you need to override only these methods of the adapter that you are interested in
		client.addClientListener(new ClientListenerAdapter() {
			@Override
			public void clientConnected(int clientId) {
				System.out.println("client connected " + clientId);
			}
			
			@Override
			public void clientDisconnected(int clientId) {
				System.out.println("client disconnected " + clientId);
			}
			
			@Override
			public void clientInfoReceived(ClientInfo clientInfo) {
				System.out.println("client info received");
				
				System.out.println(" - client id = " + clientInfo.getClientId());
				System.out.println(" - client name = " + clientInfo.getClientName());
				System.out.println(" - join date = " + clientInfo.getJoinDate());
				System.out.println(" - language = " + clientInfo.getLanguage());
				System.out.println(" - network address = " + clientInfo.getNetworkAddress());
				System.out.println(" - play as = " + clientInfo.getPlayAs());
			}
			
			@Override
			public void clientUpdated(int clientId, String clientName, byte playAs) {
				System.out.println("client updated");

				System.out.println(" - client id = " + clientId);
				System.out.println(" - client name = " + clientName);
				System.out.println(" - play as = " + playAs);
			}
			
			@Override
			public void clientError(int clientId, ErrorCode errorCode) {
				System.out.println("client error");
				
				System.out.println(" - client id = " + clientId);
				System.out.println(" - error code = " + errorCode);
			}
		});
		
		// register a listener of company-related notifications
		// don't forget to tell the server, you want to get the notification at all (see below)
		// you need to override only these methods of the adapter that you are interested in
		client.addCompanyListener(new CompanyListenerAdapter() {
			private void printCompanyInfo(CompanyInfo companyInfo) {
				System.out.println(" - index = " + companyInfo.getIndex());
				System.out.println(" - company name = " + companyInfo.getCompanyName());
				System.out.println(" - color = " + companyInfo.getColor());
				
				if (companyInfo.isInauguratedYearSet()) {
					System.out.println(" - inaugurated year = " + companyInfo.getInauguratedYear());
				}
				
				System.out.println(" - manager name = " + companyInfo.getManagerName());
				
				if (companyInfo.isAiSet()) {
					System.out.println(" - ai = " + companyInfo.isAi());
				}
				
				System.out.println(" - password protected = " + companyInfo.isPasswordProtected());
				System.out.println(" - months of bankruptcy = " + companyInfo.getMonthsOfBankruptcy());
				System.out.println(" - share owners 1 = " + companyInfo.getShareOwner(0));
				System.out.println(" - share owners 2 = " + companyInfo.getShareOwner(1));
				System.out.println(" - share owners 3 = " + companyInfo.getShareOwner(2));
				System.out.println(" - share owners 4 = " + companyInfo.getShareOwner(3));
			}
			
			@Override
			public void companyCreated(byte companyId) {
				System.out.println("company created " + companyId);
			}
			
			@Override
			public void companyInfoReceived(CompanyInfo companyInfo) {
				System.out.println("company info received");
				printCompanyInfo(companyInfo);
			}
			
			@Override
			public void companyRemoved(byte companyId, ClosureReason closureReason) {
				System.out.println("company removed " + companyId + " reason " + closureReason);
			}
			
			@Override
			public void companyUpdated(CompanyInfo companyInfo) {
				System.out.println("company updated");
				printCompanyInfo(companyInfo);
			}
			
			@Override
			public void companyEconomy(byte companyId, CompanyEconomy companyEconomy) {
				System.out.println("company economy received");
				
				System.out.println(" - company id = " + companyId);
				System.out.println(" - delivered cargo = " + companyEconomy.getDeliveredCargo());
				System.out.println(" - income = " + companyEconomy.getIncome());
				System.out.println(" - loan = " + companyEconomy.getLoan());
				System.out.println(" - money = " + companyEconomy.getMoney());
				
				System.out.println(" - past company value");
				for (int n = 0; n < companyEconomy.getPastCompanyValue().length; n++) {
					System.out.println("   (" + n + ") = " + companyEconomy.getPastCompanyValue()[n]);
				}
				
				System.out.println(" - past delivered cargo");
				for (int n = 0; n < companyEconomy.getPastDeliveredCargo().length; n++) {
					System.out.println("   (" + n + ") = " + companyEconomy.getPastDeliveredCargo()[n]);
				}
				
				System.out.println(" - past performance");
				for (int n = 0; n < companyEconomy.getPastPerformance().length; n++) {
					System.out.println("   (" + n + ") = " + companyEconomy.getPastPerformance()[n]);
				}
			}
			
			@Override
			public void companyStatistics(byte companyId, CompanyStatistics companyStatistics) {
				System.out.println("company statistics received");
				
				System.out.println(" - company id = " + companyId);
				System.out.println(" - number of airports = " + companyStatistics.getNumberOfAirports());
				System.out.println(" - number of buses = " + companyStatistics.getNumberOfBusses());
				System.out.println(" - number of bus stops = " + companyStatistics.getNumberOfBusStops());
				System.out.println(" - number of harbours = " + companyStatistics.getNumberOfHarbours());
				System.out.println(" - number of lorries = " + companyStatistics.getNumberOfLorries());
				System.out.println(" - number of lorry depots = " + companyStatistics.getNumberOfLorryDepots());
				System.out.println(" - number of planes = " + companyStatistics.getNumberOfPlanes());
				System.out.println(" - number of ships = " + companyStatistics.getNumberOfShips());
				System.out.println(" - number of trains = " + companyStatistics.getNumberOfTrains());
				System.out.println(" - number of train stations = " + companyStatistics.getNumberOfTrainStations());
			}
		});
		
		// register a listener of server-related notifications
		// don't forget to tell the server, you want to get the notification at all (see below)
		// you need to override only these methods of the adapter that you are interested in
		client.addServerListener(new ServerListenerAdapter() {
			@Override
			public void connected() {
				System.out.println("connected to the server");
			}
			
			@Override
			public void disconnected() {
				System.out.println("disconnected from the server");
			}
			
			@Override
			public void wrongPassword() {
				System.out.println("wrong password");
			}
			
			@Override
			public void serverInfoReceived(ServerInfo serverInfo) {
				System.out.println("server info received");
				
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
			
			@Override
			public void commandNamesReceived(Map<? extends Integer, ? extends String> commands) {
				System.out.println("command names received");
				
				commands.forEach(new BiConsumer<Integer, String>() {
					public void accept(Integer t, String u) {
						System.out.println(" - (" + t + ") = " + u);
					}
				});
			}
			
			@Override
			public void newDate(Date date) {
				System.out.println("new date " + date);
			}
			
			@Override
			public void newGame() {
				System.out.println("new game");
			}
			
			@Override
			public void serverBanned() {
				System.out.println("server banned");
			}
			
			@Override
			public void serverFull() {
				System.out.println("server full");
			}
			
			@Override
			public void console(String origin, String text) {
				System.out.println("console");
				
				System.out.println(" - origin = " + origin);
				System.out.println(" - text = " + text);
			}
			
			@Override
			public void rcon(int color, String result) {
				System.out.println("rcon");
				
				System.out.println(" - color = " + color);
				System.out.println(" - result = " + result);
			}
			
			@Override
			public void rconFinished(String command) {
				System.out.println("command");
				
				System.out.println(" - command = " + command);
			}
			
			@Override
			public void logging(int clientId, byte companyId, int commandId, int p1, int p2, int tile, String text,
					int frame) {
				System.out.println("logging");
				
				System.out.println(" - client id = " + clientId);
				System.out.println(" - company id = " + companyId);
				System.out.println(" - command id = " + commandId);
				System.out.println(" - p1 = " + p1);
				System.out.println(" - p2 = " + p2);
				System.out.println(" - tile = " + tile);
				System.out.println(" - text = " + text);
				System.out.println(" - frame = " + frame);
			}
			
			@Override
			public void gameScript(String json) {
				System.out.println("game script");
				
				System.out.println(" - json = " + json);
			}
			
			@Override
			public void pong(int value) {
				System.out.println("pong " + value);
			}
		});
		
		// request automatic client notifications (otherwise you would need to poll them)
		client.setUpdateClientInfos(true);
		// request automatic company notifications (otherwise you would need to poll them)
		client.setUpdateCompanyInfos(true);
		// request chat messages (otherwise you wouldn't get them at all)
		client.setDeliveryChatMessages(true);
		// request automatic command logging notifications (otherwise you wouldn't get them at all)
		client.setDeliveryCommandLogs(true);
		// request company economy info at a specified frequency (otherwise you wouldn't get them at all)
		client.setUpdateCompanyEconomyInfos(FrequencyLong.Weekly);
		// request company statistics at a specified frequency (otherwise you wouldn't get them at all)
		client.setUpdateCompanyStatistics(FrequencyLong.Weekly);
		// request automatic console messages delivery (otherwise you wouldn't get them at all)
		client.setDeliveryConsole(true);
		// request game date delivery at a specified frequency (otherwise you wouldn't get them at all)
		client.setUpdateDates(Frequency.Daily);
		// request automatic game scripts delivery (otherwise you wouldn't get them at all)
		client.setDeliveryGameScripts(true);
		
		System.out.println("(i) starting...");
		// start the client - it will work endlessly, even if the connection fails or the password is wrong
		client.start();
		System.out.println("(i) press any key to close the client");
		
		// the client is of a daemon type
		// it means, if your app quits, the client's thread will be killed automatically
		// to avoid it - your main app needs to be kept alive
		// here: by waiting for a keyboard input
		try {
			System.in.read();
		} catch (Exception ex) {}

		System.out.println("(i) closing...");
		// nicely close the client
		// not necessary, but nice :)
		// the operation is blocking until the client's thread gets finished
		try {
			client.close();
		} catch (IOException e) {}
		
		System.out.println("(i) closed.");
	}

}
