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
package de.miltschek.openttdadmin.packets;

/**
 * TODO: document it
 */
public class ServerChat extends OttdPacket {
	private NetworkAction action;
	private DestinationType destinationType;
	private int clientId;
	private String message;
	private int data;
	
	public ServerChat(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.action = NetworkAction.getEnum(0xff & readByte());
		this.destinationType = DestinationType.getEnum(0xff & readByte());
		this.clientId = readInt32();
		this.message = readString();
		this.data = readInt32();
	}

	public NetworkAction getAction() {
		return action;
	}

	public DestinationType getDestinationType() {
		return destinationType;
	}

	public int getClientId() {
		return clientId;
	}

	public String getMessage() {
		return message;
	}

	public int getData() {
		return data;
	}
}
