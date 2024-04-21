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
 * The server tells the admin that a client caused an error.
 */
public class ServerClientError extends OttdPacket {
	private int clientId;
	private NetworkErrorCode errorCode;
	private byte rawErrorCode;
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param buffer buffer containing raw data
	 */
	public ServerClientError(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.clientId = readInt32();
		this.rawErrorCode = readByte();
		this.errorCode = NetworkErrorCode.getEnum(rawErrorCode);
	}

	/**
	 * Gets the client ID of the client that caused the error.
	 * @return the client ID of the client that caused the error
	 */
	public int getClientId() {
		return clientId;
	}

	/**
	 * Gets the translated error code (if possible).
	 * @return the translated error code (if possible)
	 */
	public NetworkErrorCode getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Gets the original network-level value of the error code.
	 * @return the original network-level value of the error code
	 */
	public byte getRawErrorCode() {
		return rawErrorCode;
	}
}
