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
 * The admin tells the server the update frequency of a particular piece of information.
 */
public class AdminUpdateFrequency extends OttdPacket {
	/**
	 * Creates a packet out of binary data.
	 * @param buffer buffer containing binary data of the packet
	 * @param startPosition beginning of the valid data within the given buffer
	 * @param length length of the valid data within the given buffer
	 */
	public AdminUpdateFrequency(byte[] buffer, int startPosition, int length) {
		super(buffer, startPosition, length);
	}
	
	/**
	 * Creates a packet out of provided data.
	 * @param updateType type of information requested from the server
	 * @param updateFrequency update frequency at which the server should provide the data
	 * @return a created packet
	 */
	public static AdminUpdateFrequency createPacket(UpdateType updateType, UpdateFrequency updateFrequency) {
		byte[] buffer = new byte[MAX_MTU];
		
		// type
		int pos = 2;
		buffer[pos++] = (byte)NetworkPacketType.ADMIN_PACKET_ADMIN_UPDATE_FREQUENCY.getValue();
		
		// update type
		buffer[pos++] = (byte)updateType.getValue();
		buffer[pos++] = (byte)(updateType.getValue() << 8);
		
		// update frequency
		buffer[pos++] = (byte)updateFrequency.getValue();
		buffer[pos++] = (byte)(updateFrequency.getValue() << 8);
		
		// size
		buffer[0] = (byte)pos;
		buffer[1] = (byte)(pos >>> 8);
		
		return new AdminUpdateFrequency(buffer, 0, pos);
	}
}
