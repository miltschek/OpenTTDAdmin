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

import java.nio.charset.StandardCharsets;

/**
 * The admin sends a chat message to be distributed.
 */
public class AdminChat extends OttdPacket {
	/**
	 * Creates a packet out of binary data.
	 * @param buffer buffer containing binary data of the packet
	 * @param startPosition beginning of the valid data within the given buffer
	 * @param length length of the valid data within the given buffer
	 */
	public AdminChat(byte[] buffer, int startPosition, int length) {
		super(buffer, startPosition, length);
	}
	
	/**
	 * Creates a packet out of given arguments.
	 * @param networkAction TODO
	 * @param destinationType specifies the meaning of the destination ID
	 * @param destinationId specifies the ID of the recipient of the type destinationType
	 * @param message content of the message TODO max length
	 * @return a constructed network packet
	 */
	public static AdminChat createPacket(NetworkAction networkAction, DestinationType destinationType, int destinationId, String message) {
		byte[] buffer = new byte[MAX_MTU];
		
		// type
		int pos = 2;
		buffer[pos++] = (byte)NetworkPacketType.ADMIN_PACKET_ADMIN_CHAT.getValue();
		
		// action
		buffer[pos++] = (byte)networkAction.getValue();
		
		// destination type
		buffer[pos++] = (byte)destinationType.getValue();
		
		// destination
		buffer[pos++] = (byte)destinationId;
		buffer[pos++] = (byte)(destinationId >>> 8);
		buffer[pos++] = (byte)(destinationId >>> 16);
		buffer[pos++] = (byte)(destinationId >>> 24);
		
		// message
		byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
		int length = messageBytes.length >= 900 ? 899 : messageBytes.length;
		System.arraycopy(messageBytes, 0, buffer, pos, length);
		pos += length;
		buffer[pos++] = 0;
		
		// size
		buffer[0] = (byte)pos;
		buffer[1] = (byte)(pos >>> 8);
		
		return new AdminChat(buffer, 0, pos);
	}
}
