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
 * Company statistics data.
 */
public class CompanyStatistics {
	private int numberOfTrains;
	private int numberOfLorries;
	private int numberOfBusses;
	private int numberOfPlanes;
	private int numberOfShips;
	
	private int numberOfTrainStations;
	private int numberOfLorryDepots;
	private int numberOfBusStops;
	private int numberOfAirports;
	private int numberOfHarbours;
	
	/**
	 * Creates company statistics data.
	 * @param numberOfTrains number of possessed trains
	 * @param numberOfLorries number of possessed trucks/lorries
	 * @param numberOfBusses number of possessed busses
	 * @param numberOfPlanes number of possessed airplanes
	 * @param numberOfShips number of possessed ships
	 * @param numberOfTrainStations number of possessed train stations
	 * @param numberOfLorryDepots number of possessed truck/lorry depots
	 * @param numberOfBusStops number of possessed bus stops
	 * @param numberOfAirports number of possessed airports
	 * @param numberOfHarbours number of possessed harbors
	 */
	public CompanyStatistics(int numberOfTrains, int numberOfLorries, int numberOfBusses, int numberOfPlanes,
			int numberOfShips, int numberOfTrainStations, int numberOfLorryDepots, int numberOfBusStops,
			int numberOfAirports, int numberOfHarbours) {
		super();
		this.numberOfTrains = numberOfTrains;
		this.numberOfLorries = numberOfLorries;
		this.numberOfBusses = numberOfBusses;
		this.numberOfPlanes = numberOfPlanes;
		this.numberOfShips = numberOfShips;
		this.numberOfTrainStations = numberOfTrainStations;
		this.numberOfLorryDepots = numberOfLorryDepots;
		this.numberOfBusStops = numberOfBusStops;
		this.numberOfAirports = numberOfAirports;
		this.numberOfHarbours = numberOfHarbours;
	}

	/**
	 * Gets the number of possessed trains.
	 * @return number of possessed trains
	 */
	public int getNumberOfTrains() {
		return numberOfTrains;
	}

	/**
	 * Gets the number of possessed trucks/lorries.
	 * @return number of possessed trucks/lorries
	 */
	public int getNumberOfLorries() {
		return numberOfLorries;
	}

	/**
	 * Gets the number of possessed busses.
	 * @return number of possessed busses
	 */
	public int getNumberOfBusses() {
		return numberOfBusses;
	}

	/**
	 * Gets the number of possessed airplanes.
	 * @return number of possessed airplanes
	 */
	public int getNumberOfPlanes() {
		return numberOfPlanes;
	}

	/**
	 * Gets the number of possessed ships.
	 * @return number of possessed ships
	 */
	public int getNumberOfShips() {
		return numberOfShips;
	}

	/**
	 * Gets the number of possessed train stations.
	 * @return number of possessed train stations
	 */
	public int getNumberOfTrainStations() {
		return numberOfTrainStations;
	}

	/**
	 * Gets the number of possessed truck/lorry depots.
	 * @return number of possessed truck/lorry depots
	 */
	public int getNumberOfLorryDepots() {
		return numberOfLorryDepots;
	}

	/**
	 * Gets the number of possessed bus stops.
	 * @return number of possessed bus stops
	 */
	public int getNumberOfBusStops() {
		return numberOfBusStops;
	}

	/**
	 * Gets the number of possessed airports.
	 * @return number of possessed airports
	 */
	public int getNumberOfAirports() {
		return numberOfAirports;
	}

	/**
	 * Gets the number of possessed harbors.
	 * @return number of possessed harbors
	 */
	public int getNumberOfHarbours() {
		return numberOfHarbours;
	}
}
