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

import java.nio.charset.StandardCharsets;

/**
 * TODO: document it
 */
public class AdminJoin extends OttdPacket {
	private static final int NETWORK_PASSWORD_LENGTH = 33;
	private static final int NETWORK_ADMIN_NAME_LENGTH = 25;
	private static final int NETWORK_ADMIN_VERSION_LENGTH = 33;
	
	public AdminJoin(byte[] buffer, int startPosition, int length) {
		super(buffer, startPosition, length);
	}
	
	public static AdminJoin createPacket(String password, String adminName, String adminVersion) {
		byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
		byte[] adminNameBytes = adminName.getBytes(StandardCharsets.UTF_8);
		byte[] adminVersionBytes = adminVersion.getBytes(StandardCharsets.UTF_8);
		
		byte[] buffer = new byte[MAX_MTU];
		
		// type
		int pos = 2;
		buffer[pos++] = (byte)NetworkPacketType.ADMIN_PACKET_ADMIN_JOIN.getValue();
		
		// password
		int length = passwordBytes.length >= NETWORK_PASSWORD_LENGTH ? NETWORK_PASSWORD_LENGTH - 1 : passwordBytes.length;
		System.arraycopy(passwordBytes, 0, buffer, pos, length);
		pos += length;
		buffer[pos++] = 0;
		
		// name
		length = adminNameBytes.length >= NETWORK_ADMIN_NAME_LENGTH ? NETWORK_ADMIN_NAME_LENGTH - 1 : adminNameBytes.length;
		System.arraycopy(adminNameBytes, 0, buffer, pos, length);
		pos += length;
		buffer[pos++] = 0;
		
		// version
		length = adminVersionBytes.length >= NETWORK_ADMIN_VERSION_LENGTH ? NETWORK_ADMIN_VERSION_LENGTH - 1 : adminVersionBytes.length;
		System.arraycopy(adminVersionBytes, 0, buffer, pos, length);
		pos += length;
		buffer[pos++] = 0;
		
		// size
		buffer[0] = (byte)pos;
		buffer[1] = (byte)(pos >>> 8);
		
		return new AdminJoin(buffer, 0, pos);
	}
}
