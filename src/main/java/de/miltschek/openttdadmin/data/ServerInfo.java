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
package de.miltschek.openttdadmin.data;

/**
 * Server information data.
 */
public class ServerInfo {
	private String serverName;
	private String networkRevision;
	private boolean serverDedicated;
	private String mapName;
	private int generationSeed;
	private byte landscape;
	private Date startingYear;
	private int mapSizeX;
	private int mapSizeY;

	/**
	 * Creates server information data.
	 * @param serverName name of the server
	 * @param networkRevision revision of the network protocol
	 * @param serverDedicated true for a dedicated server, false otherwise
	 * @param mapName name of the map
	 * @param generationSeed seed of the pseudo-random generator 
	 * @param landscape TODO: no idea
	 * @param startingYear starting year of the game
	 * @param mapSizeX size of the map (x)
	 * @param mapSizeY size of the map (y)
	 */
	public ServerInfo(String serverName, String networkRevision, boolean serverDedicated, String mapName,
			int generationSeed, byte landscape, Date startingYear, int mapSizeX, int mapSizeY) {
		this.serverName = serverName;
		this.networkRevision = networkRevision;
		this.serverDedicated = serverDedicated;
		this.mapName = mapName;
		this.generationSeed = generationSeed;
		this.landscape = landscape;
		this.startingYear = startingYear;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
	}

	/**
	 * Gets the name of the server.
	 * @return name of the server
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Gets the revision of the network protocol.
	 * @return revision of the network protocol
	 */
	public String getNetworkRevision() {
		return networkRevision;
	}

	/**
	 * Gets a value indicating whether it is a dedicated server or a regular game.
	 * @return true for a dedicated server, false otherwise
	 */
	public boolean isServerDedicated() {
		return serverDedicated;
	}

	/**
	 * Gets the name of the map.
	 * @return name of the map
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Gets the seed of the pseudo-random generator.
	 * @return seed of the pseudo-random generator
	 */
	public int getGenerationSeed() {
		return generationSeed;
	}

	/**
	 * Gets the TODO: no idea.
	 * @return TODO: no idea
	 */
	public byte getLandscape() {
		return landscape;
	}

	/**
	 * Gets the starting year of the game.
	 * @return starting year of the game
	 */
	public Date getStartingYear() {
		return startingYear;
	}

	/**
	 * Gets the size of the map (x).
	 * @return size of the map (x)
	 */
	public int getMapSizeX() {
		return mapSizeX;
	}

	/**
	 * Gets the size of the map (y).
	 * @return size of the map (y)
	 */
	public int getMapSizeY() {
		return mapSizeY;
	}
}
