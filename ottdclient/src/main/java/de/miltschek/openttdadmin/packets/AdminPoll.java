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
 * The admin explicitly polls for a piece of information.
 */
public class AdminPoll extends OttdPacket {
	/**
	 * Creates a packet out of binary data.
	 * @param buffer buffer containing binary data of the packet
	 * @param startPosition beginning of the valid data within the given buffer
	 * @param length length of the valid data within the given buffer
	 */
	public AdminPoll(byte[] buffer, int startPosition, int length) {
		super(buffer, startPosition, length);
	}
	
	/**
	 * Creates a packet out of provided data.
	 * @param updateType type of information requested from the server
	 * @param parameter depends on the updateType:
	 *        for ADMIN_UPDATE_DATE: ignored,
	 *        for ADMIN_UPDATE_CLIENT_INFO: UINT32_MAX requests all clients, 1 requests the server's info, all other values a specific client ID,
	 *        for ADMIN_UPDATE_COMPANY_INFO:  UINT32_MAX requests all companies, all other values a specific company ID,
	 *        for ADMIN_UPDATE_COMPANY_ECONOMY, ADMIN_UPDATE_COMPANY_STATS, ADMIN_UPDATE_CMD_NAMES: ignored
	 * @return a created packet
	 */
	public static AdminPoll createPacket(UpdateType updateType, int parameter) {
		byte[] buffer = new byte[MAX_MTU];
		
		// type
		int pos = 2;
		buffer[pos++] = (byte)NetworkPacketType.ADMIN_PACKET_ADMIN_POLL.getValue();
		
		// command
		buffer[pos++] = (byte)updateType.getValue();
		
		// parameter
		buffer[pos++] = (byte)parameter;
		buffer[pos++] = (byte)(parameter >>> 8);
		buffer[pos++] = (byte)(parameter >>> 16);
		buffer[pos++] = (byte)(parameter >>> 24);
		
		// size
		buffer[0] = (byte)pos;
		buffer[1] = (byte)(pos >>> 8);
				
		return new AdminPoll(buffer, 0, pos);
	}
}
