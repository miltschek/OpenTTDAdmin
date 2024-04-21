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
 * The server's reply to a remove console command.
 */
public class ServerRcon extends OttdPacket {
	private int color;
	private String result;
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param buffer buffer containing raw data
	 */
	public ServerRcon(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.color = readInt16();
		this.result = readString();
	}

	/**
	 * Returns the color ID.
	 * @return the color ID
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Returns the result of the remote command.
	 * @return the result of the remote command
	 */
	public String getResult() {
		return result;
	}
}
