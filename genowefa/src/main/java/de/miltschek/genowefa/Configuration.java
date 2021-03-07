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
import java.util.List;
import java.util.Map;

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
		private final String address;
		private final int port;
		private final String password;
		
		private String slackChannel;
		private String welcomeMessagePath = "on_new_client.txt";

		/**
		 * Creates settings of an OTTD game server.
		 * @param address network address of the server (IP or name).
		 * @param port admin port of the server
		 * @param password admin password to the server
		 */
		public Game(String address, int port, String password) {
			super();
			this.address = address;
			this.port = port;
			this.password = password;
		}
		
		/**
		 * Sets a slack channel name to be coupled with this game server.
		 * @param slackChannel a slack channel name to be coupled with this game server. Include the hash-symbol.
		 */
		public void setSlackChannel(String slackChannel) {
			this.slackChannel = slackChannel;
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
		 * Gets the welcome message file path.
		 * @return the welcome message file path.
		 */
		public String getWelcomeMessagePath() {
			return welcomeMessagePath;
		}
	}
	
	private final Slack slack;
	private final Google google;
	private final List<Game> games;
	private final Map<String, String> welcomeMessages;
	
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
					
					Game game = new Game(gameJson.getString("address"), gameJson.getInt("port"), gameJson.getString("password"));
	
					if (gameJson.has("slack_channel")) {
						game.setSlackChannel(gameJson.getString("slack_channel"));
					}
					
					if (gameJson.has("welcome_msg_path")) {
						game.setWelcomeMessagePath(gameJson.getString("welcome_msg_path"));
					}
					
					this.games.add(game);
				}
			} else {
				this.games = null;
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
}
