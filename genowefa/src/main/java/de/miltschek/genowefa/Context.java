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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.integrations.GoogleTranslate;
import de.miltschek.integrations.GoogleTranslate.Result;
import de.miltschek.integrations.SlackRTMClient;
import de.miltschek.openttdadmin.OttdAdminClient;
import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ChatMessage.Recipient;
import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.CompanyEconomy;
import de.miltschek.openttdadmin.data.CompanyStatistics;
import de.miltschek.openttdadmin.data.Date;

/**
 * Application's context.
 */
public class Context {
	private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);
	
	private final Configuration configuration;
	private final Configuration.Game thisGame;
	private final ResetLock resetLock;
	private final OttdAdminClient admin;
	private final SlackRTMClient slack;
	private final String channel;
	private final GoogleTranslate googleTranslate;
	private final DatabaseConnector db;
	
	private ClientDataProvider clientDataProvider;
	private CompanyDataProvider companyDataProvider;
	
	private boolean gameConnected;
	private long dbGameId;
	private Date currentDate = new Date(0);
	private long performance;
	
	/**
	 * Describes type of an event for filtering purposes.
	 */
	public static enum EventType {
		/** Chat message. */
		Chat,
		/** Client event. */
		Client,
		/** Server event. */
		Server,
		/** Company event. */
		Company,
		/** Admin support request. */
		AdminRequest,
	}
	
	/**
	 * Client data provider for participants of the context.
	 */
	public interface ClientDataProvider {
		/**
		 * Gets the client data for a requested client ID.
		 * @param clientId client ID to look up for
		 * @return client data or null if not available
		 */
		ClientData get(int clientId);
		
		/**
		 * Gets all cached client data.
		 * @return collection of client data or an empty collection if none available
		 */
		Collection<ClientData> getAll();
		
		/**
		 * Clears any cached data.
		 */
		void clearCache();
	}
	
	/**
	 * Company data provider for participants of the context.
	 */
	public interface CompanyDataProvider {
		/**
		 * Gets the company data for a requested company ID (0-based).
		 * @param companyId company ID to look up for (0-based).
		 * @return company data or null if not available
		 */
		CompanyData get(byte companyId);
		
		/**
		 * Gets all cached company data.
		 * @return collection of company data or an empty collection if none available
		 */
		Collection<CompanyData> getAll();
		
		/**
		 * Clears any cached data.
		 */
		void clearCache();
	}

	/**
	 * Creates an application's context object.
	 * @param configuration reference to the application's configuration.
	 * @param resetLock lock object for company-reset function.
	 * @param admin OTTD admin client.
	 * @param slack Slack real-time-messaging connector or null if not available.
	 * @param channel slack channel name (including hash-symbol) to bind with the given game server. 
	 * @param googleTranslate Google Translate service or null if not available.
	 * @param db Database connector for storing statistical data or null if not available.
	 */
	public Context(Configuration configuration,
			Configuration.Game thisGame,
			ResetLock resetLock,
			OttdAdminClient admin,
			SlackRTMClient slack,
			String channel,
			GoogleTranslate googleTranslate,
			DatabaseConnector db) {
		this.configuration = configuration;
		this.thisGame = thisGame;
		this.resetLock = resetLock;
		this.admin = admin;
		this.slack = slack;
		this.channel = channel;
		this.googleTranslate = googleTranslate;
		this.db = db;
	}
	
	/**
	 * Returns the network address of the game server.
	 * @return the network address of the game server
	 */
	public String getAddress() {
		return this.thisGame.getAddress();
	}
	
	/**
	 * Returns the port number of the game server.
	 * @return the port number of the game server
	 */
	public int getPort() {
		return this.thisGame.getPort();
	}
	
	/**
	 * Sets the current in-game date.
	 * @param currentDate current in-game date
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	
	/**
	 * Gets the current in-game date.
	 * @return current in-game date
	 */
	public Date getCurrentDate() {
		return currentDate;
	}
	
	/**
	 * Sets the duration of one in-game day expressed as real-time milliseconds.
	 * @param performance the duration of one in-game day expressed as real-time milliseconds
	 */
	public void setPerformance(long performance) {
		this.performance = performance;
	}
	
	/**
	 * Gets the duration of one in-game day expressed as real-time milliseconds.
	 * @return the duration of one in-game day expressed as real-time milliseconds
	 */
	public long getPerformance() {
		return performance;
	}
	
	/**
	 * Gets the database ID of the game.
	 * @return the database ID of the game
	 */
	public long getDbGameId() {
		return dbGameId;
	}
	
	/**
	 * Sets a value denoting whether there is a connection to the game server.
	 * @param gameConnected true if there is a connection to the game server, false otherwise
	 */
	public void setGameConnected(boolean gameConnected) {
		this.gameConnected = gameConnected;
	}
	
	/**
	 * Gets a value denoting whether there is a connection to the game server.
	 * @return true if there is a connection to the game server, false otherwise
	 */
	public boolean isGameConnected() {
		return gameConnected;
	}
	
	/**
	 * Retrieves a welcome-message template for the given country.
	 * @param countryCode country code (ISO 3166-1 alpha-2)
	 * @return a welcome-message template or null if not available
	 */
	public String getWelcomeMessage(String countryCode) {
		return this.configuration.getWelcomeMessage(countryCode, true);
	}
	
	/**
	 * Retrieves a path to a text file containing a customized server-welcome message. 
	 * @return a file path
	 */
	public String getWelcomeMessagePath() {
		return this.thisGame.getWelcomeMessagePath();
	}
	
	/**
	 * Sends a message to administrator (over Slack).
	 * @param eventType type of the event for filtering purposes
	 * @param message message text
	 * @return true if succeeded, false otherwise
	 */
	public boolean notifyAdmin(EventType eventType, String message) {
		if (this.slack != null) {
			// filtering
			if (eventType == EventType.Chat && !this.configuration.getSlack().isReportChatMessages()
					|| eventType == EventType.Client && !this.configuration.getSlack().isReportClientEvents()
					|| eventType == EventType.Server && !this.configuration.getSlack().isReportServerEvents()
					|| eventType == EventType.Company && !this.configuration.getSlack().isReportCompanyEvents()) {
				return true;
			}
			
			return this.slack.sendMessage(this.channel, message);
		}
		
		return false;
	}
	
	/**
	 * Sends a game chat message to the specified user.
	 * @param clientId client ID
	 * @param message chat message
	 * @return true of succeeded, false otherwise
	 */
	public boolean notifyUser(int clientId, String message) {
		this.admin.sendChat(new ChatMessage(0, Recipient.Client, clientId, message));
		return true;
	}
	
	/**
	 * Sends a game chat message to everyone.
	 * @param message chat message
	 * @return true if succeeded, false otherwise
	 */
	public boolean notifyAll(String message) {
		this.admin.sendChat(new ChatMessage(0, Recipient.All, 0, message));
		return true;
	}
	
	/**
	 * Requests information on all connected clients.
	 */
	public void requestAllClientsInfo() {
		this.admin.requestAllClientsInfo();
	}
	
	/**
	 * Requests renaming of a client.
	 * @param clientId client ID
	 * @param newName new user name
	 */
	public void renameUser(int clientId, String newName) {
		this.admin.executeRCon("client_name " + clientId + " \"" + newName + "\"");
	}
	
	/**
	 * Disconnect the given client.
	 * @param clientId client ID
	 * @param reason reason to be shown to the client
	 */
	public void kickClient(int clientId, String reason) {
		this.admin.executeRCon("kick " + clientId + " \"" + reason + "\"");
	}
	
	/**
	 * Resets the given company.
	 * @param companyId the company ID
	 */
	public void resetCompany(byte companyId) {
		this.admin.executeRCon("resetcompany " + (companyId + 1));
	}

	/**
	 * Sends a request for a date delivery from the server.
	 */
	public void requestDate() {
		this.admin.requestDate();
	}
	
	public void requestAllCompaniesInfo() {
		this.admin.requestAllCompaniesInfo();
	}

	public void kickClient(String address, String reason) {
		this.admin.executeRCon("kick " + address + " \"" + reason + "\"");
	}

	public void banClient(int clientId, String reason) {
		this.admin.executeRCon("ban " + clientId + " \"" + reason + "\"");
	}

	public void banClient(String identifier, String reason) {
		this.admin.executeRCon("ban " + identifier + " \"" + reason + "\"");
	}

	public void pauseGame() {
		this.admin.executeRCon("pause");
	}

	public void quitGame() {
		this.admin.executeRCon("quit");
	}

	public void unbanClient(String address) {
		this.admin.executeRCon("unban " + address);
	}

	public void unbanClient(int index) {
		this.admin.executeRCon("unban " + index);
	}

	public void restoreGame() {
		this.admin.executeRCon("unpause");
	}

	public void getParameter(String name) {
		this.admin.executeRCon("setting " + name);
	}

	public void setParameter(String name, String value) {
		this.admin.executeRCon("setting " + name + " \"" + value + "\"");
	}

	public void resetCompanyOneBased(int companyId) {
		this.admin.executeRCon("resetcompany " + companyId);
	}
	
	/**
	 * Activate the lock for the company reset routine.
	 * @param clientId client ID that requested a reset
	 * @return true if a process has been started, false if not possible (e.g. another process still in place)
	 */
	public boolean startResetProcess(int clientId) {
		return this.resetLock.startResetProcess(clientId);
	}
	
	/**
	 * Sends a company reset request to the server.
	 * @param clientId client ID to be cached for the reset process
	 * @param playAs client's company ID to be cached for the reset process
	 * @param resetCallback function taking care of kicking the client and trying to remove the company
	 * @return true if the reset process is still in place, false if no reset process is active at the moment
	 */
	public boolean tryResetCompany(int clientId, byte playAs, BiConsumer<Integer, Byte> resetCallback) {
		return this.resetLock.tryResetCompany(clientId, playAs, resetCallback);
	}
	
	/**
	 * Translate the given text.
	 * @param input text to get translated.
	 * @return translated result if available, null otherwise
	 */
	public Statement translate(Statement input) {
		if (this.googleTranslate == null) {
			return null;
		} else if (input.getTargetLanguage() == null) {
			Result result = this.googleTranslate.translateToEnglish(input.getStatement());
			return new Statement(result.getSourceLanguage(), result.getTargetLanguage(), result.getTranslatedText());
		} else {
			Result result = this.googleTranslate.translate(input.getStatement(), input.getTargetLanguage());
			return new Statement(result.getSourceLanguage(), result.getTargetLanguage(), result.getTranslatedText());
		}
	}
	
	/**
	 * Gets cached client data if available.
	 * @param clientId client ID to look up for
	 * @return cached client data or null if not available
	 */
	public ClientData getClient(int clientId) {
		return this.clientDataProvider == null ? null : this.clientDataProvider.get(clientId);
	}
	
	/**
	 * Gets cached company data if available.
	 * @param companyId company ID to look up for
	 * @return cached company data or null if not available
	 */
	public CompanyData getCompany(byte companyId) {
		return this.companyDataProvider == null ? null : this.companyDataProvider.get(companyId);
	}
	
	/**
	 * Gets all cached client data.
	 * @return collection of client data or an empty collection if none available
	 */
	public Collection<ClientData> getClients() {
		return this.clientDataProvider == null ? new ArrayList<>() : this.clientDataProvider.getAll();
	}
	
	/**
	 * Gets all cached company data.
	 * @return collection of company data or an empty collection if none available
	 */
	public Collection<CompanyData> getCompanies() {
		return this.companyDataProvider == null ? new ArrayList<CompanyData>() : this.companyDataProvider.getAll();
	}
	
	/**
	 * Registers a client data provider.
	 * @param provider client data provider
	 */
	public void registerClientDataProvider(ClientDataProvider provider) {
		this.clientDataProvider = provider;
	}
	
	/**
	 * Registers a company data provider.
	 * @param provider company data provider
	 */
	public void registerCompanyDataProvider(CompanyDataProvider provider) {
		this.companyDataProvider = provider;
	}
	
	/**
	 * Stores a client who joined a company in a database.
	 * @param clientId client ID
	 * @param companyId company ID
	 */
	public void playerJoined(int clientId, byte companyId) {
		if (companyId >=0 && companyId <= 14) {
			if (this.db != null && dbGameId > 0) {
				if (this.db.storePlayer(dbGameId, clientId, companyId)) {
					LOGGER.debug("Stored client ID {} joined company ID {} of the game ID {}.", clientId, companyId, dbGameId);
				} else {
					LOGGER.error("Failed to store client ID {} joined company ID {} of the game ID {}.", clientId, companyId, dbGameId);
				}
			}
		} else {
			// e.g. player joined spectators
			playerLeft(clientId);
		}
	}
	
	/**
	 * Stores a leaving client in a database.
	 * @param clientId client ID
	 */
	public void playerLeft(int clientId) {
		if (this.db != null && dbGameId > 0) {
			if (this.db.playerQuit(dbGameId, clientId)) {
				LOGGER.debug("Stored client ID {} quit the game ID {}.", clientId, dbGameId);
			} else {
				LOGGER.error("Failed to store client ID {} quit the game ID {}.", clientId, dbGameId);
			}
		}
	}
	
	/**
	 * Closes the current game if any and prepares the internal state
	 * for a reception of details of a new game.
	 */
	public void newGame() {
		if (this.db != null) {
			if (dbGameId > 0) {
				if (db.closeGame(dbGameId)) {
					LOGGER.info("Closed the existing Game ID {} in the database.", dbGameId);
				} else {
					LOGGER.error("Failed to close the existing Game ID {} in the database.", dbGameId);
				}
			}
		}
		
		dbGameId = 0;
	}
	
	/**
	 * Updates data of the game in a database.
	 * Creates a new entry of finds an old one for continuation.
	 * Shall be called after a {@link #newGame()} to generate a new game ID.
	 * @param gameData description of the game
	 */
	public void gameUpdate(GameData gameData) {
		if (this.db != null) {
			if (dbGameId == 0) {
				Map<Long, GameData> games = db.getGames(true);
				
				long candidateId = 0, candidateTs = 0;
				
				if (games != null) {
					// iterate through available games
					// find the one that matches the parameters of the current game
					// take the most recent one if multiple are found
					for (Entry<Long, GameData> entry : games.entrySet()) {
						long id = entry.getKey();
						GameData canditate = entry.getValue();
						
						if (gameData.getAddress().equals(canditate.getAddress())
								&& gameData.getPort() == canditate.getPort()
								&& gameData.getGenerationSeed() == canditate.getGenerationSeed()
								&& gameData.getMapSizeX() == canditate.getMapSizeX()
								&& gameData.getMapSizeY() == canditate.getMapSizeY()
								&& gameData.getStartingYear() == canditate.getStartingYear()
								&& gameData.getServerName().equals(canditate.getServerName())
								&& gameData.getMapName().equals(canditate.getMapName())) {
							if (canditate.getStartedTs() > candidateTs) {
								candidateId = id;
								candidateTs = canditate.getStartedTs();
							}
						}
					}
				}
				
				if (candidateId > 0) {
					dbGameId = candidateId;
					
					LOGGER.info("Using the existing Game ID {} in the database for the server {}:{}, name {}.",
							candidateId,
							this.getAddress(),
							this.getPort(),
							gameData.getServerName());
				} else {
					dbGameId = db.createNewGame(gameData);
					
					if (dbGameId > 0) {
						LOGGER.info("Created a new Game ID {} in the database for the server {}:{}, name {}.",
								dbGameId,
								this.getAddress(),
								this.getPort(),
								gameData.getServerName());
					} else {
						LOGGER.error("Failed to create a new Game ID in the database for the server {}:{}, name {}. Code {}.",
								this.getAddress(),
								this.getPort(),
								gameData.getServerName(),
								dbGameId);
					}
				}
			} else {
				if (db.updateGame(dbGameId, gameData)) {
					LOGGER.info("Updated game details of the Game ID {} in the database for the server {}:{}, name {}.",
							dbGameId,
							this.getAddress(),
							this.getPort(),
							gameData.getServerName());
				} else {
					LOGGER.error("Failed to update game details of the Game ID {} in the database for the server {}:{}, name {}.",
							dbGameId,
							this.getAddress(),
							this.getPort(),
							gameData.getServerName());
				}
			}
		}
	}
	
	/**
	 * Stores company's data in a database.
	 * Creates a new entry if the company does not yet exist.
	 * @param companyData company data to be stored
	 */
	public void companyUpdate(CompanyData companyData) {
		if (this.db != null && dbGameId > 0) {
			long id = db.createOrUpdateCompany(dbGameId, companyData);
			
			if (id > 0) {
				LOGGER.debug("Created/updated company data of company ID {} for the game ID {}. Generated ID {}.", companyData.getCompanyId(), dbGameId, id);
			} else {
				LOGGER.error("Failed to create/update company data of company ID {} for the game ID {}. Code {}.", companyData.getCompanyId(), dbGameId, id);
			}
		}
	}
	
	/**
	 * Marks the given company as closed in a database.
	 * @param companyId company ID
	 * @param closureDate closure game-date
	 * @param closureReason closure reason
	 */
	public void companyClose(byte companyId, Date closureDate, ClosureReason closureReason) {
		if (this.db != null && dbGameId > 0) {
			if (db.closeCompany(dbGameId, companyId, closureDate, closureReason)) {
				LOGGER.debug("Closed company ID {} for the game ID {}.", companyId, dbGameId);
			} else {
				LOGGER.error("Failed to close company ID {} for the game ID {}.", companyId, dbGameId);
			}
		}
	}
	
	/**
	 * Stores economical data of the company in a database.
	 * @param companyData company data containing economical values
	 */
	public void companyEconomyUpdate(byte companyId, CompanyEconomy companyEconomy) {
		if (this.db != null && dbGameId > 0) {
			if (db.storeEconomicData(dbGameId, companyId, companyEconomy)) {
				LOGGER.debug("Stored economy of company ID {} for the game ID {}.", companyId, dbGameId);
			} else {
				LOGGER.error("Failed to store economy of company ID {} for the game ID {}.", companyId, dbGameId);
			}
		}
	}
	
	/**
	 * Stores statistical (infrastructure) data of the company in a database.
	 * @param companyData company data containing statistical (infrastructure) values
	 */
	public void companyStatisticsUpdate(byte companyId, CompanyStatistics companyStatistics) {
		if (this.db != null && dbGameId > 0) {
			if (db.storeStatisticalData(dbGameId, companyId, companyStatistics)) {
				LOGGER.debug("Stored statistics of company ID {} for the game ID {}.", companyId, dbGameId);
			} else {
				LOGGER.error("Failed to store statistics of company ID {} for the game ID {}.", companyId, dbGameId);
			}
		}
	}
	
	/**
	 * Stores client's data in a database.
	 * Creates a new entry if the client does not yet exist.
	 * @param clientData client data to be stored
	 */
	public void clientUpdate(ClientData clientData) {
		if (this.db != null && dbGameId > 0) {
			if (db.createOrUpdatePlayer(dbGameId, clientData)) {
				LOGGER.debug("Created/updated a player ID {} for the game {}.", clientData.getClientId(), dbGameId);
			} else {
				LOGGER.error("Failed to create/update a player ID {} for the game {}.", clientData.getClientId(), dbGameId);
			}
		}
	}
	
	/**
	 * Retrieves a top players list for the current server.
	 * @return an ordered list of top players
	 */
	public List<TopPlayer> getTopList() {
		if (this.db != null && dbGameId > 0) {
			return db.getTopList(dbGameId, 5);
		}
		
		return null;
	}
	
	/**
	 * Clears cache (clients, companies).
	 */
	public void clearCache() {
		if (this.clientDataProvider != null) {
			this.clientDataProvider.clearCache();
		}
		
		if (this.companyDataProvider != null) {
			this.companyDataProvider.clearCache();
		}
	}
}
