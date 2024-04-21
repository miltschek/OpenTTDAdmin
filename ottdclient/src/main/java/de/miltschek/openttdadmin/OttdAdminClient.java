/*
 *  MIT License
 *
 *  Copyright (c) 2024 miltschek
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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.State;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ChatMessage.Recipient;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.ClientListenerAdapter;
import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.Color;
import de.miltschek.openttdadmin.data.CompanyEconomy;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.CompanyListenerAdapter;
import de.miltschek.openttdadmin.data.CompanyStatistics;
import de.miltschek.openttdadmin.data.Date;
import de.miltschek.openttdadmin.data.ErrorCode;
import de.miltschek.openttdadmin.data.ExternalChatMessage;
import de.miltschek.openttdadmin.data.Frequency;
import de.miltschek.openttdadmin.data.FrequencyLong;
import de.miltschek.openttdadmin.data.Language;
import de.miltschek.openttdadmin.data.ServerInfo;
import de.miltschek.openttdadmin.data.ServerListenerAdapter;
import de.miltschek.openttdadmin.packets.AdminChat;
import de.miltschek.openttdadmin.packets.AdminExternalChat;
import de.miltschek.openttdadmin.packets.AdminGamescript;
import de.miltschek.openttdadmin.packets.AdminJoin;
import de.miltschek.openttdadmin.packets.AdminPing;
import de.miltschek.openttdadmin.packets.AdminPoll;
import de.miltschek.openttdadmin.packets.AdminRcon;
import de.miltschek.openttdadmin.packets.AdminUpdateFrequency;
import de.miltschek.openttdadmin.packets.DestinationType;
import de.miltschek.openttdadmin.packets.NetworkAction;
import de.miltschek.openttdadmin.packets.NetworkLanguage;
import de.miltschek.openttdadmin.packets.OttdPacket;
import de.miltschek.openttdadmin.packets.ServerBanned;
import de.miltschek.openttdadmin.packets.ServerChat;
import de.miltschek.openttdadmin.packets.ServerClientError;
import de.miltschek.openttdadmin.packets.ServerClientInfo;
import de.miltschek.openttdadmin.packets.ServerClientJoin;
import de.miltschek.openttdadmin.packets.ServerClientQuit;
import de.miltschek.openttdadmin.packets.ServerClientUpdate;
import de.miltschek.openttdadmin.packets.ServerCmdLogging;
import de.miltschek.openttdadmin.packets.ServerCmdNames;
import de.miltschek.openttdadmin.packets.ServerCompanyEconomy;
import de.miltschek.openttdadmin.packets.ServerCompanyInfo;
import de.miltschek.openttdadmin.packets.ServerCompanyNew;
import de.miltschek.openttdadmin.packets.ServerCompanyRemove;
import de.miltschek.openttdadmin.packets.ServerCompanyStats;
import de.miltschek.openttdadmin.packets.ServerCompanyUpdate;
import de.miltschek.openttdadmin.packets.ServerConsole;
import de.miltschek.openttdadmin.packets.ServerDate;
import de.miltschek.openttdadmin.packets.ServerError;
import de.miltschek.openttdadmin.packets.ServerFull;
import de.miltschek.openttdadmin.packets.ServerGameScript;
import de.miltschek.openttdadmin.packets.ServerNewGame;
import de.miltschek.openttdadmin.packets.ServerPong;
import de.miltschek.openttdadmin.packets.ServerProtocol;
import de.miltschek.openttdadmin.packets.ServerRcon;
import de.miltschek.openttdadmin.packets.ServerRconEnd;
import de.miltschek.openttdadmin.packets.ServerShutdown;
import de.miltschek.openttdadmin.packets.ServerWelcome;
import de.miltschek.openttdadmin.packets.UpdateFrequency;
import de.miltschek.openttdadmin.packets.UpdateType;

/**
 * OTTD Admin Client.
 */
public class OttdAdminClient implements Closeable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(OttdAdminClient.class);
	
	/** Default address: localhost. */
	public final static String DEFAULT_HOST = "127.0.0.1";
	/** Default port: 3977. */
	public final static int DEFAULT_PORT = 3977;
	
	/** Hardcoded client name. */
	private final static String CLIENT_NAME = "miltschekOAC";
	/** Hardcoded client version. */
	private final static String CLIENT_VERSION = "1.4";
	/** Supported admin protocol's version. */
	private final static byte SUPPORTED_SERVER_VERSION = 3;

	/** Configuration parameter: address. */
	private final String host;
	/** Configuration parameter: port number. */
	private final int port;
	/** Configuration parameter: admin password. */
	private final String password;
	/** Internal worker thread. */
	private final Worker worker;
	
	/** Internal collection of chat message listeners. */
    private Set<Consumer<ChatMessage>> chatMessageListeners = new HashSet<Consumer<ChatMessage>>();
    /** Internal collection of client info listeners. */
    private Set<ClientListenerAdapter> clientListeners = new HashSet<ClientListenerAdapter>();
    /** Internal collection of company info listeners. */
    private Set<CompanyListenerAdapter> companyListeners = new HashSet<CompanyListenerAdapter>();
    /** Internal collection of server info listeners. */
    private Set<ServerListenerAdapter> serverListeners = new HashSet<ServerListenerAdapter>();
    
    /** Configuration: update frequency of available notifications. */
    private UpdateFrequency updateDate = UpdateFrequency.ADMIN_FREQUENCY_POLL,
	    updateCompanyEconomy = UpdateFrequency.ADMIN_FREQUENCY_POLL,
	    updateCompanyStatistics = UpdateFrequency.ADMIN_FREQUENCY_POLL,
	    updateClientInfo = UpdateFrequency.ADMIN_FREQUENCY_POLL,
	    updateCompanyInfo = UpdateFrequency.ADMIN_FREQUENCY_POLL,
	    updateChat = UpdateFrequency.NONE,
	    updateConsole = UpdateFrequency.NONE,
	    updateCommandLogs = UpdateFrequency.NONE,
	    updateGamescripts = UpdateFrequency.NONE;

    /** Stores the admin protocol's version as implemented by the server. */
    private byte serverVersion;
    /** Stores the server information. */
	private ServerInfo serverInfo;
	
	/**
	 * Initializes the client to connect to the default port on the local machine.
	 * @param password admin password for authentication
	 */
	public OttdAdminClient(String password) {
		this(DEFAULT_HOST, DEFAULT_PORT, password);
	}
	
	/**
	 * Initializes the client to connect to the specified machine and port number.
	 * @param host address of the OTTD server
	 * @param port port of the admin service on the OTTD server
	 * @param password admin password for authentication
	 */
    public OttdAdminClient(String host, int port, String password) {
    	this.host = host;
    	this.port = port;
    	this.password = password;
    	this.worker = new Worker();
    	this.worker.setDaemon(true);
    	this.worker.setName("OttdAdminWorkerThread");
    }
    
    /**
     * Starts the client.
     * This action can be performed only once. The client cannot be reused.
     */
    public void start() {
    	if (this.worker.getState() == State.NEW) {
    		this.worker.start();
    	} else {
    		throw new IllegalStateException("the client has been already started");
    	}
    }
    
    /**
     * Closes the client and releases any resources.
     * This action makes destroys the client. It cannot be reused anymore.
     */
    public void close() throws IOException {
    	try {
    		this.worker.close();
    	} catch (InterruptedException ex) {
    		throw new IOException(ex);
    	}
    }
    
    /**
     * Registers a chat listener.
     * Works only if enabled by {@link #setDeliveryChatMessages(boolean)}
     * TODO: make it thread-safe
     * @param consumer chat listener
     */
    public void addChatListener(Consumer<ChatMessage> consumer) {
    	chatMessageListeners.add(consumer);
    }
    
    /**
     * Registers a client info listener.
     * Works only if enabled by {@link #setUpdateClientInfos(boolean)}
     * TODO: make it thread-safe
     * @param listener client info listener
     */
    public void addClientListener(ClientListenerAdapter listener) {
    	clientListeners.add(listener);
    }
    
    /**
     * Registers a company info listener.
     * Creation and update work only if enabled by {@link #setUpdateCompanyInfos(boolean)}
     * Removal works always TODO: test it.
     * Economy works only if subscribed by {@link #setUpdateCompanyEconomyInfos(FrequencyLong)}
     * Statistics works only if subscribed by {@link #setUpdateCompanyStatistics(FrequencyLong)}
     * TODO: make it thread-safe
     * @param listener company info listener
     */
    public void addCompanyListener(CompanyListenerAdapter listener) {
    	companyListeners.add(listener);
    }
    
    /**
     * Registers a server info listener.
     * Console works only if enabled by {@link #setDeliveryConsole(boolean)}
     * Game script works only if enabled by {@link #setDeliveryGameScripts(boolean)}
     * Command logging works only if enabled by {@link #setDeliveryCommandLogs(boolean)}
     * RCon works always TODO: test it.
     * Dates are delivered only if subscribed by {@link #setUpdateDates(Frequency)}
     * TODO: make it thread-safe
     * @param listener server info listener
     */
    public void addServerListener(ServerListenerAdapter listener) {
    	serverListeners.add(listener);
    }
    
    /**
     * Retrieves the admin protocol version as implemented by the server.
     * @return admin protocol version of the server
     */
    public byte getServerVersion() {
		return serverVersion;
	}
    
    /**
     * Sends a ping to the server.
     * TODO: make this function wait for a response
     * @param value any value that is to be returned by the server
     */
    public void sendPing(int value) {
    	this.worker.sendPacket(AdminPing.createPacket(value));
    }
    
    /**
     * Sends a game script to the server.
     * @param json game script to be sent
     */
    public void sendGamescript(String json) {
    	this.worker.sendPacket(AdminGamescript.createPacket(json));
    }
    
    /**
     * Send a chat message to the server.
     * @param chatMessage chat message to be sent
     */
    public void sendChat(ChatMessage chatMessage) {
    	NetworkAction networkAction;
    	DestinationType destinationType;
    	
    	if (chatMessage.isPublic()) {
    		networkAction = NetworkAction.NETWORK_ACTION_CHAT;
    		destinationType = DestinationType.DESTTYPE_BROADCAST;
    	} else if (chatMessage.isCompany()) {
    		networkAction = NetworkAction.NETWORK_ACTION_CHAT_COMPANY;
    		destinationType = DestinationType.DESTTYPE_TEAM;
    	} else if (chatMessage.isPrivate()) {
    		networkAction = NetworkAction.NETWORK_ACTION_CHAT_CLIENT;
    		destinationType = DestinationType.DESTTYPE_CLIENT;
    	} else {
    		throw new IllegalArgumentException("destination type unknown");
    	}
    	
    	this.worker.sendPacket(AdminChat.createPacket(networkAction, destinationType, chatMessage.getRecipientId(), chatMessage.getMessage()));
    }
    
    /**
     * Send an external chat message to the server.
     * @param externalChatMessage external chat message to be sent
     * @since OTTD 12.0
     */
    public void sendExternalChat(ExternalChatMessage externalChatMessage) {
    	this.worker.sendPacket(AdminExternalChat.createPacket(
    			externalChatMessage.getSource(),
    			externalChatMessage.getColor(),
    			externalChatMessage.getUser(),
    			externalChatMessage.getMessage()));
    }
    
    /**
     * Polls the current date of the server.
     */
    public void requestDate() {
    	this.worker.sendPacket(AdminPoll.createPacket(UpdateType.ADMIN_UPDATE_DATE, 0));
    }
    
    /**
     * Polls client information.
     * @param clientId ID of the client to be returned
     */
    public void requestClientInfo(int clientId) {
    	this.worker.sendPacket(AdminPoll.createPacket(UpdateType.ADMIN_UPDATE_CLIENT_INFO, clientId));
    }
    
    /**
     * Polls information on all connected clients.
     */
    public void requestAllClientsInfo() {
    	requestClientInfo(0xffffffff);
    }
    
    /**
     * Polls information of the server itself.
     */
    public void requestServerInfo() {
    	requestClientInfo(1);
    }
    
    /**
     * Polls company information.
     * @param companyId ID of the company to be returned (1..15)
     */
    public void requestCompanyInfo(byte companyId) {
    	this.worker.sendPacket(AdminPoll.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_INFO, companyId));
    }
    
    /**
     * Polls information on all available companies.
     */
    public void requestAllCompaniesInfo() {
    	this.worker.sendPacket(AdminPoll.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_INFO, 0xffffffff));
    }
    
    /**
     * Polls economy information of the requested company.
     * @param companyId ID of the company to be returned (1..15)
     */
    public void requestCompanyEconomy(byte companyId) {
    	this.worker.sendPacket(AdminPoll.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_ECONOMY, companyId));
    }
    
    /**
     * Polls statistics of the requested company.
     * @param companyId ID of the company to be returned (1..15)
     */
    public void requestCompanyStatistics(byte companyId) {
    	this.worker.sendPacket(AdminPoll.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_STATS, companyId));
    }
    
    /**
     * Polls command names implemented by the server.
     */
    public void requestCommandNames() {
    	this.worker.sendPacket(AdminPoll.createPacket(UpdateType.ADMIN_UPDATE_CMD_NAMES, 0));
    }
    
    /**
     * Executes an RCON command.
     * @param command command to be executed (don't prefix it with the admin password) 
     */
    public void executeRCon(String command) {
    	this.worker.sendPacket(AdminRcon.createPacket(command));
    }
    
    /**
     * Configures automatic notifications: reporting of the current date.
     * This value stays valid even if the client has to reconnect to the server.
     * @param frequency frequency at which the event is to be reported
     */
    public void setUpdateDates(Frequency frequency) {
    	this.updateDate = frequency == Frequency.Daily ? UpdateFrequency.ADMIN_FREQUENCY_DAILY
    			: frequency == Frequency.Weekly ? UpdateFrequency.ADMIN_FREQUENCY_WEEKLY
    					: frequency == Frequency.Monthly ? UpdateFrequency.ADMIN_FREQUENCY_MONTHLY
    							: frequency == Frequency.Quarterly ? UpdateFrequency.ADMIN_FREQUENCY_QUARTERLY
    									: frequency == Frequency.Annually ? UpdateFrequency.ADMIN_FREQUENCY_ANUALLY
    											: UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_DATE, this.updateDate));
    }

    /**
     * Configures automatic notifications: delivery of client information.
     * This value stays valid even if the client has to reconnect to the server.
     * @param state true to turn it on, false to turn it off
     */
    public void setUpdateClientInfos(boolean state) {
    	this.updateClientInfo = state ? UpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC : UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_CLIENT_INFO, this.updateClientInfo));
    }

    /**
     * Configures automatic notifications: delivery of company information.
     * This value stays valid even if the client has to reconnect to the server.
     * @param state true to turn it on, false to turn it off
     */
    public void setUpdateCompanyInfos(boolean state) {
    	this.updateCompanyInfo = state ? UpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC : UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_INFO, this.updateCompanyInfo));
    }
    
    /**
     * Configures automatic notifications: delivery of company economy data.
     * This value stays valid even if the client has to reconnect to the server.
     * @param frequency frequency at which the event is to be reported
     */
    public void setUpdateCompanyEconomyInfos(FrequencyLong frequency) {
    	this.updateCompanyEconomy = frequency == FrequencyLong.Weekly ? UpdateFrequency.ADMIN_FREQUENCY_WEEKLY
				: frequency == FrequencyLong.Monthly ? UpdateFrequency.ADMIN_FREQUENCY_MONTHLY
						: frequency == FrequencyLong.Quarterly ? UpdateFrequency.ADMIN_FREQUENCY_QUARTERLY
								: frequency == FrequencyLong.Annually ? UpdateFrequency.ADMIN_FREQUENCY_ANUALLY
										: UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_ECONOMY, this.updateCompanyEconomy));
    }
    
    /**
     * Configures automatic notifications: delivery of company statistical data.
     * This value stays valid even if the client has to reconnect to the server.
     * @param frequency frequency at which the event is to be reported
     */
    public void setUpdateCompanyStatistics(FrequencyLong frequency) {
    	this.updateCompanyStatistics = frequency == FrequencyLong.Weekly ? UpdateFrequency.ADMIN_FREQUENCY_WEEKLY
				: frequency == FrequencyLong.Monthly ? UpdateFrequency.ADMIN_FREQUENCY_MONTHLY
						: frequency == FrequencyLong.Quarterly ? UpdateFrequency.ADMIN_FREQUENCY_QUARTERLY
								: frequency == FrequencyLong.Annually ? UpdateFrequency.ADMIN_FREQUENCY_ANUALLY
										: UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_STATS, this.updateCompanyStatistics));
    }
    
    /**
     * Configures automatic notifications: delivery of chat messages.
     * This value stays valid even if the client has to reconnect to the server.
     * @param state true to turn it on, false to turn it off
     */
    public void setDeliveryChatMessages(boolean state) {
    	this.updateChat = state ? UpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC : UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_CHAT, this.updateChat));
    }
    
    /**
     * Configures automatic notifications: delivery of console messages.
     * This value stays valid even if the client has to reconnect to the server.
     * @param state true to turn it on, false to turn it off
     */
    public void setDeliveryConsole(boolean state) {
    	this.updateConsole = state ? UpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC : UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_CONSOLE, this.updateConsole));
    }
    
    /**
     * Configures automatic notifications: delivery of command logging data.
     * This value stays valid even if the client has to reconnect to the server.
     * @param state true to turn it on, false to turn it off
     */
    public void setDeliveryCommandLogs(boolean state) {
    	this.updateCommandLogs = state ? UpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC : UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_CMD_LOGGING, this.updateCommandLogs));
    }
    
    /**
     * Configures automatic notifications: delivery of game scripts.
     * This value stays valid even if the client has to reconnect to the server.
     * @param state true to turn it on, false to turn it off
     */
    public void setDeliveryGameScripts(boolean state) {
    	this.updateGamescripts = state ? UpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC : UpdateFrequency.ADMIN_FREQUENCY_POLL;
    	this.worker.sendPacket(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_GAMESCRIPT, this.updateGamescripts));
    }
    
    /**
     * Internal worker.
     */
    private class Worker extends Thread {
    	/** Current connection. */
    	private Socket client;
    	/** A queue with awaiting requests to the server. */
    	private final BlockingQueue<byte[]> requests = new LinkedBlockingQueue<byte[]>();
    	/** Current output stream. */
    	private OutputStream outputStream;
    	
    	/** Handler of the outgoing communication. */
    	private final Thread writer = new Thread() {
    		@Override
    		public void run() {
    			try {
	    			byte[] task;
	    			while ((task = requests.take()) != null) {
	    				if (outputStream == null) {
	    					LOGGER.error("could not send data - no output stream");
	    				} else {
	    					try {
	    						LOGGER.debug("sending request...");
		    					outputStream.write(task);
		    					outputStream.flush();
	    	    			} catch (IOException ex) {
	    	    				LOGGER.error("writing to socket failed", ex);
	    	    			} catch (Exception ex) {
	    	    				LOGGER.error("unknown error while writing to socket", ex);
	    	    			}
	    				}
	    			}
    			} catch (InterruptedException ex) {
    				LOGGER.info("writer thread has been interrupted");
    			}
    		}
    	};
    	
    	/** The only entry point for requesting data to be sent to the server. */
    	public void sendPacket(OttdPacket packet) {
    		requests.add(packet.getInternalBuffer());
    		
    		// TODO: try to find a response and deliver it back
    		
    		// error handling:
    		// chat -> no response, not expected, illegal packet
    		// gamescript -> no response, not expected
    		// (don't offer it) join -> wrong password, illegal packet, not expected, protocol & welcome
    		// ping -> pong, not expected
    		// poll -> not expected, date, client info*, company info*, company economy, company stats, cmd names, illegal packet
    		// quit -> no response & close
    		// rcon -> rcon end, not expected
    	}
    	
    	/**
    	 * Nice way of shutting down the worker.
    	 * @throws InterruptedException in case the thread gets interrupted
    	 */
    	public void close() throws InterruptedException {
    		if (this.isAlive()) {
	    		this.interrupt();
	    		try {
	    			LOGGER.info("closing the socket");
	    			client.close();
	    		} catch (Exception ex) {}
	    		this.join();
    		}
    	}
    	
    	/**
    	 * The main loop handling all incoming data from the server.
    	 */
    	@Override
		public void run() {
    		// create the handler of outgoing communication
	    	this.writer.setDaemon(true);
	    	this.writer.setName("OttdAdminWorkerWriterThread");
	    	this.writer.start();

	    	// try - handle thread interruptions in one place
	    	try {
	    		// outside loop - keep connecting to the server 
		    	while (true) {
		    		if (this.isInterrupted()) {
		    			LOGGER.info("stopping worker (1)");
		    			return;
		    		}
		    		
			    	try {
			    		LOGGER.debug("trying to connect...");
						client = new Socket(OttdAdminClient.this.host, OttdAdminClient.this.port);
						LOGGER.debug("socket created");
						// TODO: fine tuning client.setKeepAlive(true);
					} catch (UnknownHostException e) {
						LOGGER.error("unknown host {}", OttdAdminClient.this.host);
						OttdAdminClient.wait(WaitReason.UNKNOWN_HOST);
						continue;
					} catch (IOException e) {
						LOGGER.error("failed to connect to {}:{}", OttdAdminClient.this.host, OttdAdminClient.this.port);
						OttdAdminClient.wait(WaitReason.CANNOT_CONNECT);
						continue;
					}
					
					try {
				    	InputStream adminIs = client.getInputStream();
				    	this.outputStream = client.getOutputStream();
				    	
				    	AdminJoin adminJoin = AdminJoin.createPacket(OttdAdminClient.this.password, CLIENT_NAME, CLIENT_VERSION);
				    	LOGGER.debug("admin join enqueued");
				    	requests.add(adminJoin.getInternalBuffer());
				    	
				    	boolean welcomeReceived = false, settingsSent = false;
				    	
				    	// inside loop - keep reading data from the server
				    	while (true) {
				    		if (this.isInterrupted()) {
				    			LOGGER.info("stopping worker (2)");
				    			return;
				    		}

				    		if (welcomeReceived && !settingsSent) {
				    			// re-register all subscriptions
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_DATE, updateDate).getInternalBuffer());
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_ECONOMY, updateCompanyEconomy).getInternalBuffer());
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_STATS, updateCompanyStatistics).getInternalBuffer());
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_CLIENT_INFO, updateClientInfo).getInternalBuffer());
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_COMPANY_INFO, updateCompanyInfo).getInternalBuffer());
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_CHAT, updateChat).getInternalBuffer());
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_CONSOLE, updateConsole).getInternalBuffer());
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_CMD_LOGGING, updateCommandLogs).getInternalBuffer());
				    			requests.add(AdminUpdateFrequency.createPacket(UpdateType.ADMIN_UPDATE_GAMESCRIPT, updateGamescripts).getInternalBuffer());
				    			
				    			settingsSent = true;
				    		}
				    		
				    		// get the length of the next packet
				    		LOGGER.debug("wating for a response...");
					    	byte[] buffer = new byte[OttdPacket.MAX_MTU];
					    	int read = adminIs.read(buffer, 0, 2);
					    	
					    	if (read <= 0) {
					    		LOGGER.debug("connection closed (1)");
					    		
					    		try {
				    				for (ServerListenerAdapter listener : serverListeners) {
				    						listener.disconnected();
				    				}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server disconnected listener(s)", ex);
			    				}

					    		OttdAdminClient.wait(WaitReason.CONNECTION_INTERRUPTED);
					    		break;
					    	}
					    	
					    	int packetSize = (0xff & buffer[0]) | ((0xff & buffer[1]) << 8);
					    	
					    	if (packetSize == 0) {
					    		continue;
					    	}

					    	// read the rest of the packet
					    	read = adminIs.read(buffer, 2, packetSize - 2);
					    	
					    	if (read <= 0) {
					    		LOGGER.debug("connection closed (2)");
					    		
					    		try {
				    				for (ServerListenerAdapter listener : serverListeners) {
				    						listener.disconnected();
				    				}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server disconnected listener(s)", ex);
			    				}
					    		
					    		OttdAdminClient.wait(WaitReason.CONNECTION_INTERRUPTED);
					    		break;
					    	}
					    	
					    	OttdPacket packetReceived = OttdPacket.parsePacket(OttdAdminClient.this.serverVersion, buffer);
				    		if (packetReceived == null) {
				    			// the packet could not be identified - ignore it
				    			LOGGER.warn("an unidentified packet has been received");
				    		} else if (packetReceived instanceof ServerProtocol) {
			    				ServerProtocol p = (ServerProtocol)packetReceived;
			    				if ((OttdAdminClient.this.serverVersion = p.getAdminVersion()) > SUPPORTED_SERVER_VERSION) {
			    					LOGGER.warn("the server implements a potentially unsupported protocol version {}", p.getAdminVersion());
			    				}
			    				
			    				try {
				    				for (ServerListenerAdapter listener : serverListeners) {
				    						listener.connected();
				    				}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server connected listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerWelcome) {
			    				welcomeReceived = true;
	
			    				ServerWelcome p = (ServerWelcome)packetReceived;
			    				OttdAdminClient.this.serverInfo = new ServerInfo(
			    						p.getServerName(),
			    						p.getNetworkRevision(),
			    						p.isServerDedicated(),
			    						p.getMapName(),
			    						p.getGenerationSeed(),
			    						p.getLandscape(),
			    						new Date(p.getStartingYear()),
			    						p.getMapSizeX(),
			    						p.getMapSizeY());
	
			    				try {
				    				for (ServerListenerAdapter listener : serverListeners) {
				    						listener.serverInfoReceived(serverInfo);
				    				}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server info listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerError) {
			    				ServerError p = (ServerError)packetReceived;
			    				LOGGER.warn("server error received {} ({})", p.getErrorCode(), p.getRawErrorCode());
			    				
			    				switch (p.getErrorCode()) {
			    				case NETWORK_ERROR_NOT_EXPECTED:
			    					// command executed without being authenticated etc
			    					break;
			    					
			    				case NETWORK_ERROR_WRONG_PASSWORD:
			    					try {
					    				for (ServerListenerAdapter listener : serverListeners) {
					    						listener.wrongPassword();
					    				}
				    				} catch (Exception ex) {
				    					LOGGER.error("failed to call server wrong password listener(s)", ex);
				    				}

			    					OttdAdminClient.wait(WaitReason.WRONG_PASSWORD);
			    					break;
			    					
			    				case NETWORK_ERROR_ILLEGAL_PACKET:
			    					// missing name & version
			    					// unsupported update frequency
			    					// unsupported update type (poll)
			    					// chat other than public, client, company, server
			    					break;
			    					
			    				default:
			    					break;
			    				}
			    				
			    				// as of time of writing, all above errors end up with a connection closure
			    			} else if (packetReceived instanceof ServerChat) {
			    				ServerChat p = (ServerChat)packetReceived;
			    				ChatMessage chatMessage = new ChatMessage(
			    						p.getClientId(),
			    						p.getAction() == NetworkAction.NETWORK_ACTION_CHAT && p.getDestinationType() == DestinationType.DESTTYPE_BROADCAST ? Recipient.All
			    								: p.getAction() == NetworkAction.NETWORK_ACTION_CHAT_CLIENT && p.getDestinationType() == DestinationType.DESTTYPE_CLIENT ? Recipient.Client
			    										: p.getAction() == NetworkAction.NETWORK_ACTION_CHAT_COMPANY && p.getDestinationType() == DestinationType.DESTTYPE_TEAM ? Recipient.Company
			    												: null,
			    						p.getData(),
			    						p.getMessage());
			    				
			    				try {
				    				for (Consumer<ChatMessage> listener : chatMessageListeners) {
				    						listener.accept(chatMessage);
				    				}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call chat message listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerClientError) {
			    				ServerClientError p = (ServerClientError)packetReceived;
			    				
			    				try {
			    					for (ClientListenerAdapter listener : clientListeners) {
			    						listener.clientError(p.getClientId(), ErrorCode.get(p.getErrorCode()));
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call client error listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerClientInfo) {
			    				ServerClientInfo p = (ServerClientInfo)packetReceived;
			    				
			    				ClientInfo clientInfo = new ClientInfo(
			    						p.getClientId(),
			    						p.getNetworkAddress(),
			    						p.getClientName(),
			    						Language.get(p.getLanguage()),
			    						new Date(p.getJoinDate()),
			    						p.getPlayAs());
			    				
			    				try {
			    					for (ClientListenerAdapter listener : clientListeners) {
			    						listener.clientInfoReceived(clientInfo);
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call client info listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerClientJoin) {
			    				ServerClientJoin p = (ServerClientJoin)packetReceived;
			    				
			    				try {
			    					for (ClientListenerAdapter listener : clientListeners) {
			    						listener.clientConnected(p.getClientId());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call client connected listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerClientQuit) {
			    				ServerClientQuit p = (ServerClientQuit)packetReceived;
			    				
			    				try {
			    					for (ClientListenerAdapter listener : clientListeners) {
			    						listener.clientDisconnected(p.getClientId());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call client disconnected listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerClientUpdate) {
			    				ServerClientUpdate p = (ServerClientUpdate)packetReceived;
			    				
			    				try {
			    					for (ClientListenerAdapter listener : clientListeners) {
			    						listener.clientUpdated(p.getClientId(), p.getClientName(), p.getPlayAs());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call client updated listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerCompanyInfo) {
			    				ServerCompanyInfo p = (ServerCompanyInfo)packetReceived;
			    				
			    				CompanyInfo companyInfo = new CompanyInfo(
			    						p.getIndex(),
			    						p.getCompanyName(),
			    						p.getManagerName(),
			    						Color.getEnum(p.getColor()),
			    						p.isPasswordProtected(),
			    						p.getInauguratedYear(),
			    						p.isAi(),
			    						p.getMonthsOfBankruptcy(),
			    						p.isSharesSupported(),
			    						new byte[] { p.getShareOwners(0), p.getShareOwners(1), p.getShareOwners(2), p.getShareOwners(3) });
			    				
			    				try {
			    					for (CompanyListenerAdapter listener : companyListeners) {
			    						listener.companyInfoReceived(companyInfo);
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call company info listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerCompanyNew) {
			    				ServerCompanyNew p = (ServerCompanyNew)packetReceived;
	
			    				try {
			    					for (CompanyListenerAdapter listener : companyListeners) {
			    						listener.companyCreated(p.getCompanyId());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call company created listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerCompanyRemove) {
			    				ServerCompanyRemove p = (ServerCompanyRemove)packetReceived;
			    				
			    				ClosureReason reason;
			    				switch (p.getRemoveReason()) {
			    				case ADMIN_CRR_MANUAL:
			    					reason = ClosureReason.Manual; break;
			    				case ADMIN_CRR_AUTOCLEAN:
			    					reason = ClosureReason.Autoclean; break;
			    				case ADMIN_CRR_BANKRUPT:
			    					reason = ClosureReason.Bankrupt; break;
			    					default:
			    						reason = ClosureReason.Unknown; break;
			    				}
			    				
			    				try {
			    					for (CompanyListenerAdapter listener : companyListeners) {
			    						listener.companyRemoved(p.getCompanyId(), reason);
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call company removed listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerCompanyUpdate) {
			    				ServerCompanyUpdate p = (ServerCompanyUpdate)packetReceived;
			    				
			    				CompanyInfo companyInfo = new CompanyInfo(
			    						p.getIndex(),
			    						p.getCompanyName(),
			    						p.getManagerName(),
			    						Color.getEnum(p.getColor()),
			    						p.isPasswordProtected(),
			    						p.getQuartersOfBankruptcy(),
			    						p.isSharesSupported(),
			    						new byte[] { p.getShareOwners(0), p.getShareOwners(1), p.getShareOwners(2), p.getShareOwners(3) });
			    				
			    				try {
			    					for (CompanyListenerAdapter listener : companyListeners) {
			    						listener.companyUpdated(companyInfo);
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call company updated listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerConsole) {
			    				ServerConsole p = (ServerConsole)packetReceived;
	
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.console(p.getOrigin(), p.getText());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server console listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerRcon) {
			    				ServerRcon p = (ServerRcon)packetReceived;
			    				
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.rcon(p.getColor(), p.getResult());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server rcon listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerRconEnd) {
			    				ServerRconEnd p = (ServerRconEnd)packetReceived;
			    				
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.rconFinished(p.getCommand());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server rcon finished listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerNewGame) {
			    				//ServerNewGame p = (ServerNewGame)packetReceived;
			    				
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.newGame();
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server new game listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerDate) {
			    				ServerDate p = (ServerDate)packetReceived;
			    				
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.newDate(new Date(p.getDate()));
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server new date listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerCompanyEconomy) {
			    				ServerCompanyEconomy p = (ServerCompanyEconomy)packetReceived;
			    				
			    				long[] pastCompanyValue = new long[ServerCompanyEconomy.getHistorySize()];
			    				int[] pastPerformance = new int[ServerCompanyEconomy.getHistorySize()];
			    				int[] pastDeliveredCargo = new int[ServerCompanyEconomy.getHistorySize()];
			    				
			    				for (int n = 0; n < pastCompanyValue.length; n++) {
			    					pastCompanyValue[n] = p.getPastCompanyValue(n);
			    					pastPerformance[n] = p.getPastPerformance(n);
			    					pastDeliveredCargo[n] = p.getPastDeliveredCargo(n);
			    				}
			    				
			    				CompanyEconomy companyEconomy = new CompanyEconomy(
			    						p.getMoney(),
			    						p.getLoan(),
			    						p.getIncome(),
			    						p.getDeliveredCargo(),
			    						pastCompanyValue,
			    						pastPerformance,
			    						pastDeliveredCargo);
			    				
			    				try {
			    					for (CompanyListenerAdapter listener : companyListeners) {
			    						listener.companyEconomy(p.getIndex(), companyEconomy);
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call company economy listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerCompanyStats) {
			    				ServerCompanyStats p = (ServerCompanyStats)packetReceived;
			    				
			    				CompanyStatistics companyStatistics = new CompanyStatistics(
			    						p.getVehicles(ServerCompanyStats.TRAIN),
			    						p.getVehicles(ServerCompanyStats.LORRY),
			    						p.getVehicles(ServerCompanyStats.BUS),
			    						p.getVehicles(ServerCompanyStats.PLANE),
			    						p.getVehicles(ServerCompanyStats.SHIP),
			    						p.getStations(ServerCompanyStats.TRAIN),
			    						p.getStations(ServerCompanyStats.LORRY),
			    						p.getStations(ServerCompanyStats.BUS),
			    						p.getStations(ServerCompanyStats.PLANE),
			    						p.getStations(ServerCompanyStats.SHIP));
			    				
			    				try {
			    					for (CompanyListenerAdapter listener : companyListeners) {
			    						listener.companyStatistics(p.getIndex(), companyStatistics);
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call company statistics listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerCmdNames) {
			    				ServerCmdNames p = (ServerCmdNames)packetReceived;
			    				
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.commandNamesReceived(p.getCommands());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server command names listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerCmdLogging) {
			    				ServerCmdLogging p = (ServerCmdLogging)packetReceived;
	
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.logging(p.getClientId(), p.getCompanyId(), p.getCmdId(), p.getP1(), p.getP2(), p.getTile(), p.getText(), p.getFrame());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server command logging listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerGameScript) {
			    				ServerGameScript p = (ServerGameScript)packetReceived;
			    				
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.gameScript(p.getJson());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server game script listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerPong) {
			    				ServerPong p = (ServerPong)packetReceived;
			    				
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.pong(p.getD1());
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server pong listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerFull) {
			    				//ServerFull p = (ServerFull)packetReceived;
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.serverFull();
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server full listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerBanned) {
			    				//ServerBanned p = (ServerBanned)packetReceived;
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.serverBanned();
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server banned listener(s)", ex);
			    				}
			    			} else if (packetReceived instanceof ServerShutdown) {
			    				try {
			    					for (ServerListenerAdapter listener : serverListeners) {
			    						listener.shutdown();
			    					}
			    				} catch (Exception ex) {
			    					LOGGER.error("failed to call server shutdown listener(s)", ex);
			    				}
			    			} else {
			    				LOGGER.error("received an unimplemented package {}", packetReceived.getClass().getSimpleName());
			    			}
				    	}
					} catch (IOException ex) {
						LOGGER.error("io exception; disconnecting", ex);
						OttdAdminClient.wait(WaitReason.IO_EXCEPTION);
					} finally {
						try {
							client.close();
						} catch (Exception closeEx) {
							LOGGER.error("failed to close the connection", closeEx);
						}
					}
		    	}
	    	} catch (InterruptedException ex) {
	    		LOGGER.info("thread interrupted; abort");
	    		return;
	    	}
		}
	}

    /**
     * Single place of controlling delays, depending on the reason.
     * @param reason reason of the requested delay
     * @throws InterruptedException in case the thread got interrupted
     */
    private static void wait(WaitReason reason) throws InterruptedException {
    	long interval;
    	
    	switch (reason) {
    	case CANNOT_CONNECT:
    	case IO_EXCEPTION:
    	case WRONG_PASSWORD:
    		interval = 15000;
    		break;
    		
    	case CONNECTION_INTERRUPTED:
    		interval = 1000;
    		break;
    		
    		default:
    			interval = 100;
    	}
    	
    	LOGGER.debug("delay of {} ms", interval);
    	Thread.sleep(interval);
    }
}
