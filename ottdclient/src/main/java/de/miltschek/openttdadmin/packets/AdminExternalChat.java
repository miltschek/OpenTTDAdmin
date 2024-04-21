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

import de.miltschek.openttdadmin.data.TextColor;

/**
 * Represents an external chat message.
 * @since OTTD 12.0
 */
public class AdminExternalChat extends OttdPacket {
	public static final int NETWORK_CHAT_LENGTH = 900;
	
	/**
	 * Creates a packet out of binary data.
	 * @param buffer buffer containing binary data of the packet
	 * @param startPosition beginning of the valid data within the given buffer
	 * @param length length of the valid data within the given buffer
	 */
	public AdminExternalChat(byte[] buffer, int startPosition, int length) {
		super(buffer, startPosition, length);
	}
	
	/**
	 * Creates a packet out of given arguments.
	 * @param source TODO max length {@value #NETWORK_CHAT_LENGTH}
	 * @param color color of the chat message
	 * @param user TODO max length {@value #NETWORK_CHAT_LENGTH}
	 * @param message TODO max length {@value #NETWORK_CHAT_LENGTH}
	 * @return a constructed network packet
	 */
	public static AdminExternalChat createPacket(String source, TextColor color, String user, String message) {
		byte[] buffer = new byte[MAX_MTU];
		
		// type
		int pos = 2;
		buffer[pos++] = (byte)NetworkPacketType.ADMIN_PACKET_ADMIN_EXTERNAL_CHAT.getValue();
		
		// source
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		int length = sourceBytes.length >= NETWORK_CHAT_LENGTH ? NETWORK_CHAT_LENGTH - 1 : sourceBytes.length;
		if (length >= buffer.length - pos) {
			throw new Error("The arguments are too long for the network packet.");
		}
		System.arraycopy(sourceBytes, 0, buffer, pos, length);
		pos += length;
		buffer[pos++] = 0;
		
		// color
		if (buffer.length - pos < 2) {
			throw new Error("The arguments are too long for the network packet.");
		}
		buffer[pos++] = (byte)color.getValue();
		buffer[pos++] = (byte)(color.getValue() >>> 8);
		
		// user
		byte[] userBytes = user.getBytes(StandardCharsets.UTF_8);
		length = userBytes.length >= NETWORK_CHAT_LENGTH ? NETWORK_CHAT_LENGTH - 1 : userBytes.length;
		if (length >= buffer.length - pos) {
			throw new Error("The arguments are too long for the network packet.");
		}
		System.arraycopy(userBytes, 0, buffer, pos, length);
		pos += length;
		buffer[pos++] = 0;
		
		// message
		byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
		length = messageBytes.length >= NETWORK_CHAT_LENGTH ? NETWORK_CHAT_LENGTH - 1 : messageBytes.length;
		if (length >= buffer.length - pos) {
			throw new Error("The arguments are too long for the network packet.");
		}
		System.arraycopy(messageBytes, 0, buffer, pos, length);
		pos += length;
		buffer[pos++] = 0;
		
		return new AdminExternalChat(buffer, 0, pos);
	}
}
