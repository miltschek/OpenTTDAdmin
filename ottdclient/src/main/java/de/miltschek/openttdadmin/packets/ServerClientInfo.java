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
public class ServerClientInfo extends OttdPacket {
	private int clientId;
	private String networkAddress;
	private String clientName;
	private byte rawLanguage;
	private NetworkLanguage language;
	private int joinDate;
	private byte playAs;
	
	public ServerClientInfo(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.clientId = readInt32();
		this.networkAddress = readString();
		this.clientName = readString();
		this.rawLanguage = readByte();
		this.language = NetworkLanguage.getEnum(rawLanguage);
		this.joinDate = readInt32();
		this.playAs = readByte();
	}

	public int getClientId() {
		return clientId;
	}

	public String getNetworkAddress() {
		return networkAddress;
	}

	public String getClientName() {
		return clientName;
	}

	/**
	 * Returns client language ID.
	 * @deprecated Since OTTD 12.0 the value is fixed to 0.
	 * @return client language ID.
	 */
	public byte getRawLanguage() {
		return rawLanguage;
	}
	
	public NetworkLanguage getLanguage() {
		return language;
	}

	public int getJoinDate() {
		return joinDate;
	}

	public byte getPlayAs() {
		return playAs;
	}
}
