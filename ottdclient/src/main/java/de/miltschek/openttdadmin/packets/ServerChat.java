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
package de.miltschek.openttdadmin.packets;

/**
 * The server received a chat message and relays it.
 */
public class ServerChat extends OttdPacket {
	private NetworkAction action;
	private DestinationType destinationType;
	private int clientId;
	private String message;
	private int data;
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param buffer buffer containing raw data
	 */
	public ServerChat(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.action = NetworkAction.getEnum(0xff & readByte());
		this.destinationType = DestinationType.getEnum(0xff & readByte());
		this.clientId = readInt32();
		this.message = readString();
		this.data = readInt32();
	}

	/**
	 * Gets the network action value.
	 * @return the network action value.
	 */
	public NetworkAction getAction() {
		return action;
	}

	/**
	 * Gets the type of the destination address. 
	 * @return the type of the destination address
	 */
	public DestinationType getDestinationType() {
		return destinationType;
	}

	/**
	 * Gets the client ID of the sender of the message.
	 * @return the client ID of the sender of the message
	 */
	public int getClientId() {
		return clientId;
	}

	/**
	 * Gets the message.
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets additional data, if any.
	 * @return additional data, if any
	 */
	public int getData() {
		return data;
	}
}
