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
 * The server gives the admin some statistics about a company.
 */
public class ServerCompanyStats extends OttdPacket {
	private static final int NETWORK_VEH_END = 5;
	
	/** A train as a vehicle type. */
	public static final int TRAIN = 0,
			/** A lorry as a vehicle type. */
		LORRY = 1,
			/** A bus as a vehicle type. */
		BUS = 2,
			/** A plane as a vehicle type. */
		PLANE = 3,
			/** A ship as a vehicle type. */
		SHIP = 4;
	
	private byte index;
	private int[] vehicles = new int[NETWORK_VEH_END];
	private int[] stations = new int[NETWORK_VEH_END];
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param buffer buffer containing raw data
	 */
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
	
	/**
	 * Returns the company ID.
	 * @return the company ID
	 */
	public byte getIndex() {
		return index;
	}

	/**
	 * Returns a number of vehicles of the given type.
	 * @param vehicleType type of vehicles {@link #TRAIN}, {@link #LORRY}, {@link #BUS}, {@link #PLANE} or {@link #SHIP}.
	 * @return a number of vehicles of the given type
	 */
	public int getVehicles(int vehicleType) {
		return vehicles[vehicleType];
	}

	/**
	 * Returns a number of stations for the given vehicle type.
	 * @param vehicleType type of vehicles {@link #TRAIN}, {@link #LORRY}, {@link #BUS}, {@link #PLANE} or {@link #SHIP}.
	 * @return a number of stations for the given vehicle type
	 */
	public int getStations(int vehicleType) {
		return stations[vehicleType];
	}
}
