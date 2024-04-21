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

import java.util.HashMap;
import java.util.Map;

/**
 * The server tells the admin its protocol version.
 */
public class ServerProtocol extends OttdPacket {
	private byte adminVersion;
	private Map<Integer, Integer> frequencies = new HashMap<Integer, Integer>();
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param buffer buffer containing raw data
	 */
	public ServerProtocol(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.adminVersion = readByte();
		while (readBoolean()) {
			frequencies.put(readInt16(), readInt16());
		}
	}

	/**
	 * Returns the protocol version number.
	 * @return the protocol version number
	 */
	public byte getAdminVersion() {
		return adminVersion;
	}
	
	/**
	 * Returns update frequencies for a given event type.
	 * @param updateType event type to be retrieved
	 * @return registered update frequency
	 */
	public int getFrequency(UpdateType updateType) {
		return frequencies.get(updateType.getValue());
	}
}
