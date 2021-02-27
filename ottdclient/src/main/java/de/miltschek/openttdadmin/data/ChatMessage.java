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
package de.miltschek.openttdadmin.data;

/**
 * Chat message.
 */
public class ChatMessage {
	private int senderId;
	private Recipient recipient;
	private int recipientId;
	private String message;
	
	/**
	 * Addressee type.
	 */
	public static enum Recipient {
		/** Everyone / public. */
		All,
		/** Specific client / private. */
		Client,
		/** Specific company / team. */
		Company
	}
	
	/**
	 * Creates a chat message.
	 * @param senderId identifier of a sender
	 * @param recipient recipient type
	 * @param recipientId recipient's ID (client ID for private messages, company ID for team messages, ignored for public messages)
	 * @param message message's value (please note strict restrictions on the maximal length of the message)
	 */
	public ChatMessage(int senderId, Recipient recipient, int recipientId, String message) {
		this.senderId = senderId;
		this.recipient = recipient;
		this.recipientId = recipientId;
		this.message = message;
	}
	
	/**
	 * Returns a value indicating whether the message is a public one (to everyone).
	 * @return true for a public message, false otherwise
	 */
	public boolean isPublic() {
		return this.recipient == Recipient.All;
	}
	
	/**
	 * Returns a value indicating whether the message is a private one (to a specific client).
	 * @return true for a private message, false otherwise
	 */
	public boolean isPrivate() {
		return this.recipient == Recipient.Client;
	}
	
	/**
	 * Returns a value indicating whether the message is a company one.
	 * @return true for a company message, false otherwise
	 */
	public boolean isCompany() {
		return this.recipient == Recipient.Company;
	}
	
	/**
	 * Gets an identifier of the sender.
	 * TODO: document special IDs
	 * @return identifier of the sender
	 */
	public int getSenderId() {
		return senderId;
	}
	
	/**
	 * Gets an identifier of the recipient.
	 * @return identifier of the recipient
	 */
	public int getRecipientId() {
		return recipientId;
	}
	
	/**
	 * Gets the message.
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
