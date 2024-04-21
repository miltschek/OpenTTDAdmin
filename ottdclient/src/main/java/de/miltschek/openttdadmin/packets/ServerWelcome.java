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
 * The server welcomes the admin to a game.
 */
public class ServerWelcome extends OttdPacket {
	private String serverName;
	private String networkRevision;
	private boolean serverDedicated;
	private String mapName;
	private int generationSeed;
	private byte landscape;
	private int startingYear;
	private int mapSizeX;
	private int mapSizeY;
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param buffer buffer containing raw data
	 */
	public ServerWelcome(byte[] buffer) {
		super(buffer);

		resetCursor();
		this.serverName = readString();
		this.networkRevision = readString();
		this.serverDedicated = readBoolean();
		this.mapName = readString();
		this.generationSeed = readInt32();
		this.landscape = readByte();
		this.startingYear = readInt32();
		this.mapSizeX = readInt16();
		this.mapSizeY = readInt16();
	}
	
	/**
	 * Returns the name of the server.
	 * @return the name of the server
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Returns the revision (version) of the game, which can be an official tag or a git-hash. 
	 * @return the revision (version) of the game
	 */
	public String getNetworkRevision() {
		return networkRevision;
	}

	/**
	 * Returns a flag denoting whether the server is a dedicated one (server-only, no local player).
	 * @return true if the server is a dedicated one, false otherwise
	 */
	public boolean isServerDedicated() {
		return serverDedicated;
	}

	/**
	 * Returns the map name.
	 * @deprecated Since OTTD 12.0 the string is empty.
	 * @return map name
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Returns the seed used for map generation.
	 * @return the seed used for map generation
	 */
	public int getGenerationSeed() {
		return generationSeed;
	}

	/**
	 * Returns the ID of the landscape type of the map.
	 * @return the ID of the landscape type of the map
	 */
	public byte getLandscape() {
		return landscape;
	}

	/**
	 * Returns the starting year of the game.
	 * @return the starting year of the game
	 */
	public int getStartingYear() {
		return startingYear;
	}

	/**
	 * Returns the width of the map.
	 * @return the width of the map
	 */
	public int getMapSizeX() {
		return mapSizeX;
	}

	/**
	 * Returns the height of the map.
	 * @return the height of the map
	 */
	public int getMapSizeY() {
		return mapSizeY;
	}
}
