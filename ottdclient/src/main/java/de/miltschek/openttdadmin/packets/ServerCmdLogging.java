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
 * In-game command data packet.
 */
public class ServerCmdLogging extends OttdPacket {
	private int clientId;
	private byte companyId;
	private int cmdId;
	private int p1;
	private int p2;
	private int tile;
	private String text;
	private int frame;
	
	/**
	 * Reads the packet out of the byte array and instantiates it.
	 * @param buffer a byte buffer containing packet's data
	 */
	public ServerCmdLogging(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.clientId = readInt32();
		this.companyId = readByte();
		this.cmdId = readInt16();
		this.p1 = readInt32();
		this.p2 = readInt32();
		this.tile = readInt32();
		this.text = readString();
		this.frame = readInt32();
	}

	/**
	 * Gets the client that generated the command.
	 * @return the client that generated the command
	 */
	public int getClientId() {
		return clientId;
	}

	/**
	 * Gets the company ID that generated the command.
	 * @return the company ID that generated the command
	 */
	public byte getCompanyId() {
		return companyId;
	}

	/**
	 * Gets a command identifier as defined in the game's command_type.h file as the enum Commands.
	 * TODO: create a local enum of the commands
	 * @return a command identifier as defined in the game's command_type.h file as the enum Commands.
	 */
	public int getCmdId() {
		return cmdId;
	}

	/**
	 * Gets the first parameter of the command.
	 * The meaning of the parameter is specific to the command and can be interpreted only with the game's original code.
	 * @return the first parameter of the command
	 */
	public int getP1() {
		return p1;
	}

	/**
	 * Gets the second parameter of the command.
	 * The meaning of the parameter is specific to the command and can be interpreted only with the game's original code.
	 * @return the second parameter of the command
	 */
	public int getP2() {
		return p2;
	}

	/**
	 * Gets the tile identifier the command has been executed for.
	 * The tile identifier is calculated as y * MapSizeX + x
	 * @return the tile identifier the command has been executed for
	 */
	public int getTile() {
		return tile;
	}

	/**
	 * Gets the text parameter of the command if available.
	 * @return the text parameter of the command if available
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the frame number, in which the command has been executed.
	 * @return the frame number, in which the command has been executed
	 */
	public int getFrame() {
		return frame;
	}
}
