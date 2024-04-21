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
 * The server gives the admin an information update on a client.
 */
public class ServerClientUpdate extends OttdPacket {
	private int clientId;
	private String clientName;
	private byte playAs;
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param buffer buffer containing raw data
	 */
	public ServerClientUpdate(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.clientId = readInt32();
		this.clientName = readString();
		this.playAs = readByte();
	}

	/**
	 * Gets the unique ID of the client.
	 * @return the unique ID of the client
	 */
	public int getClientId() {
		return clientId;
	}

	/**
	 * Gets the client name.
	 * @return the client name
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * Gets the company ID the client is playing.
	 * @return the company ID the client is playing
	 */
	public byte getPlayAs() {
		return playAs;
	}
}
