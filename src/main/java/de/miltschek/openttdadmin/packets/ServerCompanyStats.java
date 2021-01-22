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
public class ServerCompanyStats extends OttdPacket {
	private static final int NETWORK_VEH_END = 5;
	
	public static final int TRAIN = 0,
		LORRY = 1,
		BUS = 2,
		PLANE = 3,
		SHIP = 4;
	
	private byte index;
	private int[] vehicles = new int[NETWORK_VEH_END];
	private int[] stations = new int[NETWORK_VEH_END];
	
	public ServerCompanyStats(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.index = readByte();
		
		for (int n = 0; n < NETWORK_VEH_END; n++) {
			this.vehicles[n] = readInt16();
		}
		
		for (int n = 0; n < NETWORK_VEH_END; n++) {
			this.stations[n] = readInt16();
		}
	}
	
	public byte getIndex() {
		return index;
	}

	public int getVehicles(int vehicleType) {
		return vehicles[vehicleType];
	}

	public int getStations(int vehicleType) {
		return stations[vehicleType];
	}
}
