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

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.openttdadmin.data.ChatMessage;

/**
 * Handler of game chat messages.
 */
public class ChatListener implements Consumer<ChatMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatListener.class);
	private static final Pattern RENAME_PATTERN = Pattern.compile("[!]name[ \\t]+(\"|)(?<value>(?:[^\"\\\\]|\\\\.)*)\\1");
	private final Context context;
	
	/**
	 * Instantiates the handler with the context.
	 * @param context context object
	 */
	public ChatListener(Context context) {
		this.context = context;
	}
	
	/**
	 * Receives messages and decides on what to do.
	 */
	public void accept(ChatMessage t) {
		ClientData senderData = this.context.getClient(t.getSenderId());
		String senderId = (senderData == null) ? String.valueOf(t.getSenderId()) : (senderData.getClientInfo().getClientName() + "(" + t.getSenderId() + ")");
		
		if (t.getMessage() == null) {
			// nix
			LOGGER.debug("A null chat message has been received from {} to {} private {} company {} public {}.", t.getSenderId(), t.getRecipientId(), t.isPrivate(), t.isCompany(), t.isPublic());
		} else if (t.getMessage().startsWith("!admin")) {
			LOGGER.warn("Admin action has been requested by {}: {}.", t.getSenderId(), t.getMessage());
	    	if (this.context.notifyAdmin(":boom: " + senderId + " " + t.getMessage())) {
	    		this.context.notifyUser(t.getSenderId(), "Your message has been sent to the admin. Thank you!");
	    	} else {
	    		this.context.notifyUser(t.getSenderId(), "No connection to the administrator at the moment, please try again later.");
	    	}
		} else if (t.getMessage().equals("!reset")) {
			LOGGER.info("Company reset has been requested by {}.", t.getSenderId());

			// need to find, what company is playing the sender
			// then, what other clients do play the same company
			// then, kick them
			// then, reset the company
			// (did not find a way to reset a company with active clients, TODO?)
			if (!this.context.startResetProcess(t.getSenderId())) {
				this.context.notifyUser(t.getSenderId(), "Another reset request still being processed. Please retry in a few seconds.");
			}
			
			this.context.requestAllClientsInfo();
			
			this.context.notifyAdmin(":recycle: user " + senderId + " requested a reset");
		} else if (t.getMessage().startsWith("!name")) {
			LOGGER.info("Name change has been requested by {} raw {}.", t.getSenderId(), t.getMessage());

			Matcher m = RENAME_PATTERN.matcher(t.getMessage());
			if (m.find()) {
				String newName = m.group("value");
				this.context.renameUser(t.getSenderId(), newName);
				this.context.notifyAdmin(":name_badge: user " + senderId + " requested a rename to " + newName);
			}
		} else if (t.getMessage().equals("!help")) {
			LOGGER.info("User {} requested help.", t.getSenderId());
			
			this.context.notifyUser(t.getSenderId(), "Available commands:");
			this.context.notifyUser(t.getSenderId(), "!admin <message>: sends the message to the server's admin");
			this.context.notifyUser(t.getSenderId(), "!reset: resets your company; you will be kicked of the server, so please re-join");
			this.context.notifyUser(t.getSenderId(), "!name <new_name>: changes your name; surround multiple words with double quotes");
			this.context.notifyUser(t.getSenderId(), "!dict <message>: tries to translate your message to English");
		} else if (t.getMessage().startsWith("!dict ") && t.getMessage().length() > 6) {
			LOGGER.info("User {} requested translation.", t.getSenderId());
			
			Statement translation = this.context.translate(new Statement(t.getMessage().substring("!dict ".length())));
			if (translation != null) {
				if (translation.getSourceLanguage().equals("en")) {
					this.context.notifyUser(t.getSenderId(), "It was English already. Nothing to translate.");
				} else {
					this.context.notifyAll("[translation/" + translation.getSourceLanguage() + "] " + translation.getStatement());
					this.context.notifyAdmin(":flags: user " + senderId + " [" + translation.getSourceLanguage() + "] from: " + t.getMessage() + " to: " + translation.getStatement());
				}
			} else {
				this.context.notifyUser(t.getSenderId(), "Sorry, translation did not work.");
				this.context.notifyAdmin(":exclamation: user " + senderId + " failed translation from: " + t.getMessage());
			}
		} else if (t.getMessage().startsWith("!")) {
			LOGGER.debug("User {} entered an invalid command {}.", t.getSenderId(), t.getMessage());
			this.context.notifyUser(t.getSenderId(), "No such command. For help, enter !help");
		} else if (t.getSenderId() != 1 && !t.getMessage().isEmpty()) {
			LOGGER.debug("User {} sent a message to {} {}: {}.",
					t.getSenderId(),
					t.isPrivate() ? "user" : t.isCompany() ? "company" : t.isPublic() ? "all" : "unknown",
					t.getRecipientId(),
					t.getMessage());

			Statement translation = this.context.translate(new Statement(t.getMessage()));
			if (translation != null && !"en".equals(translation.getSourceLanguage())) {
				this.context.notifyAdmin(":pencil: " + senderId + " " + t.getMessage() + "\r\n"
						+ ":flags: " + translation.getStatement());
			} else {
				this.context.notifyAdmin(":pencil: " + senderId + " " + t.getMessage());
			}
		}
	}
}