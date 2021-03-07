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

import java.util.function.BiConsumer;

import de.miltschek.integrations.GoogleTranslate;
import de.miltschek.integrations.GoogleTranslate.Result;
import de.miltschek.integrations.SlackRTMClient;
import de.miltschek.openttdadmin.OttdAdminClient;
import de.miltschek.openttdadmin.data.ChatMessage;
import de.miltschek.openttdadmin.data.ChatMessage.Recipient;

/**
 * Application's context.
 */
public class Context {
	private final Configuration configuration;
	private final Configuration.Game thisGame;
	private final ResetLock resetLock;
	private final OttdAdminClient admin;
	private final SlackRTMClient slack;
	private final String channel;
	private final GoogleTranslate googleTranslate;

	/**
	 * Creates an application's context object.
	 * @param configuration reference to the application's configuration.
	 * @param resetLock lock object for company-reset function.
	 * @param admin OTTD admin client.
	 * @param slack Slack real-time-messaging connector or null if not available.
	 * @param channel slack channel name (including hash-symbol) to bind with the given game server. 
	 * @param googleTranslate Google Translate service or null if not available.
	 */
	public Context(Configuration configuration, Configuration.Game thisGame, ResetLock resetLock, OttdAdminClient admin, SlackRTMClient slack, String channel, GoogleTranslate googleTranslate) {
		this.configuration = configuration;
		this.thisGame = thisGame;
		this.resetLock = resetLock;
		this.admin = admin;
		this.slack = slack;
		this.channel = channel;
		this.googleTranslate = googleTranslate;
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
	 * @param message message text
	 * @return true if succeeded, false otherwise
	 */
	public boolean notifyAdmin(String message) {
		if (this.slack != null) {
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
	public void kickUser(int clientId, String reason) {
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
}
