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
 * The admin sends a ping to the server, expecting a ping-reply (PONG) packet.
 */
public class AdminPing extends OttdPacket {
	/**
	 * Creates a packet out of binary data.
	 * @param buffer buffer containing binary data of the packet
	 * @param startPosition beginning of the valid data within the given buffer
	 * @param length length of the valid data within the given buffer
	 */
	public AdminPing(byte[] buffer, int startPosition, int length) {
		super(buffer, startPosition, length);
	}
	
	/**
	 * Creates a packet out of provided data.
	 * @param d1 a 32-bit value that identified the 'ping' request and can be matched with a 'pong' response afterwards
	 * @return a created packet
	 */
	public static AdminPing createPacket(int d1) {
		byte[] buffer = new byte[MAX_MTU];
		
		// type
		int pos = 2;
		buffer[pos++] = (byte)NetworkPacketType.ADMIN_PACKET_ADMIN_PING.getValue();
		
		// parameter
		buffer[pos++] = (byte)d1;
		buffer[pos++] = (byte)(d1 >>> 8);
		buffer[pos++] = (byte)(d1 >>> 16);
		buffer[pos++] = (byte)(d1 >>> 24);
		
		// size
		buffer[0] = (byte)pos;
		buffer[1] = (byte)(pos >>> 8);
				
		return new AdminPing(buffer, 0, pos);
	}
}
