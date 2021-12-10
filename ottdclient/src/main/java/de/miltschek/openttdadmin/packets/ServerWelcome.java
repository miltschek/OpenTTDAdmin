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
 * TODO: document it
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
	
	public String getServerName() {
		return serverName;
	}

	public String getNetworkRevision() {
		return networkRevision;
	}

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

	public int getGenerationSeed() {
		return generationSeed;
	}

	public byte getLandscape() {
		return landscape;
	}

	public int getStartingYear() {
		return startingYear;
	}

	public int getMapSizeX() {
		return mapSizeX;
	}

	public int getMapSizeY() {
		return mapSizeY;
	}
	
	
}
