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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Holds application's configuration.
 */
public class Configuration {
	/**
	 * Holds settings specific to Slack connector.
	 */
	public static class Slack {
		private final String appToken;
		private final String botToken;
		
		private boolean reportServerEvents = true;
		private boolean reportCompanyEvents = true;
		private boolean reportClientEvents = true;
		private boolean reportChatMessages = true;
		
		/**
		 * Creates settings for a Slack connector.
		 * @param appToken application token
		 * @param botToken bot token
		 */
		public Slack(String appToken, String botToken) {
			super();
			this.appToken = appToken;
			this.botToken = botToken;
		}
		
		/**
		 * Gets the application token.
		 * @return the application token.
		 */
		public String getAppToken() {
			return appToken;
		}
		
		/**
		 * Gets the bot token.
		 * @return the bot token.
		 */
		public String getBotToken() {
			return botToken;
		}
		
		/**
		 * Gets a value indicating whether to report chat messages to the slack channel.
		 * @return true to turn reporting on, false to turn reporting off
		 */
		public boolean isReportChatMessages() {
			return reportChatMessages;
		}
		
		/**
		 * Gets a value indicating whether to report client-related events to the slack channel.
		 * @return true to turn reporting on, false to turn reporting off
		 */
		public boolean isReportClientEvents() {
			return reportClientEvents;
		}
		
		/**
		 * Gets a value indicating whether to report company-related events to the slack channel.
		 * @return true to turn reporting on, false to turn reporting off
		 */
		public boolean isReportCompanyEvents() {
			return reportCompanyEvents;
		}
		
		/**
		 * Gets a value indicating whether to report server-related events to the slack channel.
		 * @return true to turn reporting on, false to turn reporting off
		 */
		public boolean isReportServerEvents() {
			return reportServerEvents;
		}
		
		/**
		 * Sets a value indicating whether to report chat messages to the slack channel.
		 * @param reportChatMessages true to turn reporting on, false to turn reporting off
		 */
		public void setReportChatMessages(boolean reportChatMessages) {
			this.reportChatMessages = reportChatMessages;
		}
		
		/**
		 * Sets a value indicating whether to report client-related events to the slack channel.
		 * @param reportClientEvents true to turn reporting on, false to turn reporting off
		 */
		public void setReportClientEvents(boolean reportClientEvents) {
			this.reportClientEvents = reportClientEvents;
		}
		
		/**
		 * Sets a value indicating whether to report company-related events to the slack channel.
		 * @param reportCompanyEvents true to turn reporting on, false to turn reporting off
		 */
		public void setReportCompanyEvents(boolean reportCompanyEvents) {
			this.reportCompanyEvents = reportCompanyEvents;
		}
		
		/**
		 * Sets a value indicating whether to report server-related events to the slack channel.
		 * @param reportServerEvents true to turn reporting on, false to turn reporting off
		 */
		public void setReportServerEvents(boolean reportServerEvents) {
			this.reportServerEvents = reportServerEvents;
		}
	}

	/**
	 * Holds settings specific to Google services.
	 */
	public static class Google {
		private final String keyFile;

		/**
		 * Creates settings for Google services.
		 * @param keyFile file path to a service key file
		 */
		public Google(String keyFile) {
			super();
			this.keyFile = keyFile;
		}
		
		/**
		 * Gets the path to the service key file.
		 * @return the path to the service key file.
		 */
		public String getKeyFile() {
			return keyFile;
		}
	}
	
	/**
	 * Holds settings specific to OTTD game server.
	 */
	public static class Game {
		private final String gameName;
		private final String address;
		private final int port;
		private final String password;
		
		private String slackChannel;
		private String slackAdminChannel;
		private String welcomeMessagePath = "on_new_client.txt";
		private boolean forceNameChange;

		/**
		 * Creates settings of an OTTD game server.
		 * @param gameName name of the game
		 * @param address network address of the server (IP or name).
		 * @param port admin port of the server
		 * @param password admin password to the server
		 */
		public Game(String gameName, String address, int port, String password) {
			this.gameName = gameName;
			this.address = address;
			this.port = port;
			this.password = password;
		}
		
		/**
		 * Gets the name of the game.
		 * @return the name of the game.
		 */
		public String getGameName() {
			return gameName;
		}
		
		/**
		 * Sets a slack channel name to be coupled with this game server.
		 * @param slackChannel a slack channel name to be coupled with this game server. Include the hash-symbol.
		 */
		public void setSlackChannel(String slackChannel) {
			this.slackChannel = slackChannel;
		}
		
		/**
		 * Sets an admin slack channel name to be coupled with this game server.
		 * @param slackAdminChannel an admin slack channel name to be coupled with this game server. Include the hash-symbol.
		 */
		public void setSlackAdminChannel(String slackAdminChannel) {
			this.slackAdminChannel = slackAdminChannel;
		}
		
		/**
		 * Sets the welcome message file path.
		 * @param welcomeMessagePath the welcome message file path.
		 */
		public void setWelcomeMessagePath(String welcomeMessagePath) {
			this.welcomeMessagePath = welcomeMessagePath;
		}
		
		/**
		 * Gets the network address of the server.
		 * @return the network address of the server.
		 */
		public String getAddress() {
			return address;
		}
		
		/**
		 * Gets the port number of the admin service.
		 * @return the port number of the admin service.
		 */
		public int getPort() {
			return port;
		}
		
		/**
		 * Gets the admin password.
		 * @return the admin password.
		 */
		public String getPassword() {
			return password;
		}
		
		/**
		 * Gets the slack channel name to be coupled with this game server.
		 * @return the slack channel name to be coupled with this game server.
		 */
		public String getSlackChannel() {
			return slackChannel;
		}
		
		/**
		 * Gets the admin slack channel name to be coupled with this game server.
		 * @return the admin slack channel name to be coupled with this game server.
		 */
		public String getSlackAdminChannel() {
			return slackAdminChannel;
		}
		
		/**
		 * Gets the welcome message file path.
		 * @return the welcome message file path.
		 */
		public String getWelcomeMessagePath() {
			return welcomeMessagePath;
		}
		
		/**
		 * Sets a value indicating whether a non-name Players should be forced to change their names.
		 * @param forceNameChange true to generate names for no-named players, false otherwise
		 */
		public void setForceNameChange(boolean forceNameChange) {
			this.forceNameChange = forceNameChange;
		}
		
		/**
		 * Gets a value indicating whether a non-name Players should be forced to change their names.
		 * @return true to generate names for no-named players, false otherwise
		 */
		public boolean isForceNameChange() {
			return forceNameChange;
		}
	}
	
	/**
	 * Holds settings of a database connection.
	 */
	public static class Database {
		private final String url;
		private final String dbName;
		private final String username;
		private final String password;
		private boolean dropTables;

		/**
		 * Creates settings of a database connection.
		 * @param url database URL
		 * @param dbName database name
		 * @param username username
		 * @param password password
		 */
		public Database(String url, String dbName, String username, String password) {
			super();
			this.url = url;
			this.dbName = dbName;
			this.username = username;
			this.password = password;
		}
		
		/**
		 * Gets the database URL.
		 * @return database URL
		 */
		public String getUrl() {
			return url;
		}
		
		/**
		 * Gets the database name.
		 * @return database name
		 */
		public String getDbName() {
			return dbName;
		}
		
		/**
		 * Gets the username.
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}
		
		/**
		 * Gets the password.
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}
		
		/**
		 * Gets a value indicating whether to drop all database tables on startup.
		 * Default false.
		 * @return a value indicating whether to drop all database tables on startup
		 */
		public boolean isDropTables() {
			return dropTables;
		}
	}
	
	/**
	 * Settings of chat functions.
	 */
	public static class Chat {
		private String hallOfFameLink;
		private String helpMessage;
		
		/**
		 * Sets the message shown as a link to the hall of fame.
		 * @param hallOfFameLink the message shown as a link to the hall of fame
		 */
		public void setHallOfFameLink(String hallOfFameLink) {
			this.hallOfFameLink = hallOfFameLink;
		}
		
		/**
		 * Gets the message shown as a link to the hall of fame.
		 * @return the message shown as a link to the hall of fame
		 */
		public String getHallOfFameLink() {
			return hallOfFameLink;
		}
		
		/**
		 * Sets the additional help message shown when a user requests help.
		 * @param helpMessage the additional help message shown when a user requests help.
		 */
		public void setHelpMessage(String helpMessage) {
			this.helpMessage = helpMessage;
		}
		
		/**
		 * Gets the additional help message shown when a user requests help.
		 * @return the additional help message shown when a user requests help
		 */
		public String getHelpMessage() {
			return helpMessage;
		}
	}
	
	/**
	 * Administrator's settings.
	 */
	public static class Administrator {
		private final String userId;
		private final String nickName;
		private final Set<String> grants = new HashSet<>();
		
		/**
		 * Creates administrator's settings object.
		 * @param userId unique user identifier as assigned by Slack
		 * @param nickName nick name to be used for chat messages
		 */
		public Administrator(String userId, String nickName) {
			this.userId = userId;
			this.nickName = nickName;
		}
		
		/**
		 * Registers granted access to a given command.
		 * @param command command name the user has rights to
		 */
		public void grant(String command) {
			this.grants.add(command);
		}
		
		/**
		 * Gets the user identifier as assigned by Slack
		 * @return the user identifier as assigned by Slack
		 */
		public String getUserId() {
			return userId;
		}
		
		/**
		 * Gets the nick name to be used for chat messages
		 * @return the nick name to be used for chat messages
		 */
		public String getNickName() {
			return nickName;
		}
		
		/**
		 * Gets a set of granted commands.
		 * @return a set of granted commands
		 */
		public Set<? extends String> getGrants() {
			return grants;
		}
	}
	
	/**
	 * Definition of a deny rule to reject players, companies etc.
	 */
	public static class DenyRule {
		private final String type;
		private final String pattern;
		private final String message;
		
		/**
		 * Creates a deny rule.
		 * @param type type of the rule (possible values depend on what other classes support)
		 * @param pattern pattern of the rule (possible values depend on what other classes support)
		 * @param message message to be shown to the player in case the rule gets applied
		 */
		public DenyRule(String type, String pattern, String message) {
			this.type = type;
			this.pattern = pattern;
			this.message = message;
		}
		
		/**
		 * Gets the type of the rule (possible values depend on what other classes support).
		 * @return type of the rule (possible values depend on what other classes support)
		 */
		public String getType() {
			return type;
		}
		
		/**
		 * Gets the pattern of the rule (possible values depend on what other classes support).
		 * @return pattern of the rule (possible values depend on what other classes support)
		 */
		public String getPattern() {
			return pattern;
		}
		
		/**
		 * Gets the message to be shown to the player in case the rule gets applied.
		 * @return message to be shown to the player in case the rule gets applied
		 */
		public String getMessage() {
			return message;
		}
	}
	
	private final Slack slack;
	private final Google google;
	private final List<Game> games;
	private final Map<String, String> welcomeMessages;
	private final String[] playerNames;
	private final Database database;
	private final Chat chat = new Chat();
	private final Map<String, Administrator> administrators = new HashMap<String, Administrator>();
	private final Set<DenyRule> denyRules = new HashSet<>();
	
	/**
	 * Loads the configuration out of a JSON file.
	 * @param path file path to the JSON configuration file
	 * @throws IOException in case of a read or parse error
	 */
	public Configuration(String path) throws IOException {
		try {
			JSONObject json = new JSONObject(Files.readString(new File(path).toPath()));

			if (json.has("slack")) {
				JSONObject slackJson = json.getJSONObject("slack");
				this.slack = new Slack(slackJson.getString("app_token"),
						slackJson.getString("bot_token"));
				
				this.slack.setReportChatMessages(!slackJson.has("report_chat_messages") || slackJson.getBoolean("report_chat_messages"));
				this.slack.setReportClientEvents(!slackJson.has("report_client_events") || slackJson.getBoolean("report_client_events"));
				this.slack.setReportCompanyEvents(!slackJson.has("report_company_events") || slackJson.getBoolean("report_company_events"));
				this.slack.setReportServerEvents(!slackJson.has("report_server_events") || slackJson.getBoolean("report_server_events"));
			} else {
				this.slack = null;
			}

			if (json.has("google")) {
				JSONObject googleJson = json.getJSONObject("google");
				this.google = new Google(googleJson.getString("key_file"));
			} else {
				this.google = null;
			}
				
			if (json.has("games")) {
				this.games = new ArrayList<Configuration.Game>();
	
				JSONArray gamesJson = json.getJSONArray("games");
				for (int n = gamesJson.length() - 1; n >= 0; n--) {
					JSONObject gameJson = gamesJson.getJSONObject(n);
					
					Game game = new Game(
							gameJson.getString("name"),
							gameJson.getString("address"),
							gameJson.getInt("port"),
							gameJson.getString("password"));
	
					if (gameJson.has("slack_channel")) {
						game.setSlackChannel(gameJson.getString("slack_channel"));
					}
					
					if (gameJson.has("slack_admin_channel")) {
						game.setSlackAdminChannel(gameJson.getString("slack_admin_channel"));
					}
					
					if (gameJson.has("welcome_msg_path")) {
						game.setWelcomeMessagePath(gameJson.getString("welcome_msg_path"));
					}
					
					game.setForceNameChange(gameJson.has("force_name_change") && gameJson.getBoolean("force_name_change")); 
					
					this.games.add(game);
				}
			} else {
				this.games = null;
			}
			
			if (json.has("database")) {
				JSONObject dbJson = json.getJSONObject("database");
				
				this.database = new Database(
						dbJson.getString("url"),
						dbJson.getString("db_name"),
						dbJson.getString("username"),
						dbJson.getString("password"));
				
				if (dbJson.has("drop_tables") && dbJson.getBoolean("drop_tables")) {
					this.database.dropTables = true;
				}
			} else {
				this.database = null;
			}
			
			if (json.has("chat")) {
				JSONObject chatJson = json.getJSONObject("chat");
				
				if (chatJson.has("hall_of_fame")) {
					this.chat.setHallOfFameLink(chatJson.getString("hall_of_fame"));
				}
				
				if (chatJson.has("help_message")) {
					this.chat.setHelpMessage(chatJson.getString("help_message"));
				}
			}
			
			if (json.has("welcome_messages")) {
				this.welcomeMessages = new HashMap<>();
				
				JSONArray welcomeJson = json.getJSONArray("welcome_messages");
				for (int n = welcomeJson.length() - 1; n >= 0; n--) {
					JSONObject welcome = welcomeJson.getJSONObject(n);
					this.welcomeMessages.put(welcome.getString("country"), welcome.getString("message"));
				}
			} else {
				this.welcomeMessages = null;
			}
			
			if (json.has("player_names")) {
				JSONArray playerNamesJson = json.getJSONArray("player_names");
				this.playerNames = new String[playerNamesJson.length()];
				for (int n = playerNamesJson.length() - 1; n >= 0; n--) {
					this.playerNames[n] = playerNamesJson.getString(n);
				}
			} else {
				this.playerNames = null;
			}
			
			if (json.has("administrators")) {
				JSONArray administratorsJson = json.getJSONArray("administrators");
				for (int n = administratorsJson.length() - 1; n >= 0; n--) {
					JSONObject administratorJson = administratorsJson.getJSONObject(n);

					Administrator administrator = new Administrator(
							administratorJson.getString("user_id"),
							administratorJson.getString("nick_name"));
					
					JSONArray grantsJson = administratorJson.getJSONArray("grant");
					for (int m = grantsJson.length() - 1; m >= 0; m--) {
						administrator.grant(grantsJson.getString(m));
					}
					
					this.administrators.put(administrator.getUserId(), administrator);
				}
			}
			
			if (json.has("deny_rules")) {
				JSONArray denyRulesJson = json.getJSONArray("deny_rules");
				for (int n = denyRulesJson.length() - 1; n >= 0; n--) {
					JSONObject denyRuleJson = denyRulesJson.getJSONObject(n);
					
					this.denyRules.add(new DenyRule(
							denyRuleJson.getString("type"),
							denyRuleJson.getString("pattern"),
							denyRuleJson.getString("message")));
				}
			}
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}
	
	/**
	 * Gets settings specific to a Slack connector.
	 * @return settings specific to a Slack connector.
	 */
	public Slack getSlack() {
		return slack;
	}
	
	/**
	 * Gets settings specific to Google services.
	 * @return settings specific to Google services.
	 */
	public Google getGoogle() {
		return google;
	}
	
	/**
	 * Gets settings of OTTD servers.
	 * @return settings of OTTD servers.
	 */
	public Collection<? extends Game> getGames() {
		return games;
	}
	
	/**
	 * Gets a list of player names for forced name changes.
	 * @return list of player names for forced name changes
	 */
	public String[] getPlayerNames() {
		return playerNames;
	}
	
	/**
	 * Gets settings of a database connection.
	 * @return settings of a database connection.
	 */
	public Database getDatabase() {
		return database;
	}
	
	/**
	 * Gets settings of chat functions.
	 * @return settings of chat functions
	 */
	public Chat getChat() {
		return chat;
	}
	
	/**
	 * Gets a welcome-message template for a given country.
	 * @param countryCode country code (ISO 3166-1 alpha-2)
	 * @param fallbackToDefault true to return the default message if the specific not available
	 * @return a message template or null if not found
	 */
	public String getWelcomeMessage(String countryCode, boolean fallbackToDefault) {
		if (this.welcomeMessages == null) {
			return null;
		} else {
			String result = this.welcomeMessages.get(countryCode);
			if (result == null && fallbackToDefault) {
				return this.welcomeMessages.get("*");
			} else {
				return result;
			}
		}
	}
	
	/**
	 * Gets administrator's settings for the given user identifier.
	 * @param userId user identifier as assigned by Slack
	 * @return administrator's settings object or null if not available
	 */
	public Administrator getAdministrator(String userId) {
		return this.administrators.get(userId);
	}
	
	/**
	 * Gets a set of deny rules.
	 * @return a set of deny rules
	 */
	public Set<? extends DenyRule> getDenyRules() {
		return this.denyRules;
	}
}
