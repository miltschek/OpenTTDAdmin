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
package de.miltschek.genowefa;

import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.Color;
import de.miltschek.openttdadmin.data.CompanyEconomy;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.CompanyStatistics;
import de.miltschek.openttdadmin.data.Date;

/**
 * Local cache of company data.
 */
public class CompanyData {
	//private static final Logger LOGGER = LoggerFactory.getLogger(CompanyData.class);
	
	private byte companyId;
	private int inauguratedYear;
	private Color color;
	private String name;
	private String managerName;
	private boolean passwordProtected;
	private ClosureReason closureReason;

	private long income, loan, money, value;
	private int performance;
	
	private int airports,
		planes,
		trains,
		trainStations,
		ships,
		harbours,
		busses,
		busStops,
		lorries,
		lorryDepots;
	
	/**
	 * Creates a company data object. 
	 * @param companyId company ID
	 */
	public CompanyData(byte companyId) {
		this.companyId = companyId;
	}
	
	/**
	 * Returns the company's name.
	 * @return the company's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the company ID.
	 * @return company ID
	 */
	public byte getCompanyId() {
		return companyId;
	}
	
	/**
	 * Returns the year of company's establishment.
	 * @return year of company's establishment
	 */
	public int getInauguratedYear() {
		return inauguratedYear;
	}
	
	/**
	 * Marks the company as closed.
	 * @param closureReason closure reason
	 */
	public void closed(ClosureReason closureReason) {
		this.closureReason = closureReason;
	}
	
	/**
	 * Updates the company information.
	 * @param companyInfo company information
	 */
	public void updateData(CompanyInfo companyInfo) {
		this.color = companyInfo.getColor();
		this.name = companyInfo.getCompanyName();
		this.managerName = companyInfo.getManagerName();
		this.passwordProtected = companyInfo.isPasswordProtected();
		if (companyInfo.isInauguratedYearSet()) {
			this.inauguratedYear = companyInfo.getInauguratedYear();
		}
	}
	
	/**
	 * Returns the number of airplanes.
	 * @return the number of airplanes
	 */
	public int getPlanes() {
		return planes;
	}
	
	/**
	 * Returns the number of airports.
	 * @return the number of airports
	 */
	public int getAirports() {
		return airports;
	}
	
	/**
	 * Returns the number of busses.
	 * @return the number of busses
	 */
	public int getBusses() {
		return busses;
	}
	
	/**
	 * Returns the number of lorry depots.
	 * @return the number of lorry depots
	 */
	public int getLorryDepots() {
		return lorryDepots;
	}
	
	/**
	 * Returns the number of harbours.
	 * @return the number of harbours
	 */
	public int getHarbours() {
		return harbours;
	}
	
	/**
	 * Returns the number of lorries.
	 * @return the number of lorries
	 */
	public int getLorries() {
		return lorries;
	}
	
	/**
	 * Returns the number of ships.
	 * @return the number of ships
	 */
	public int getShips() {
		return ships;
	}
	
	/**
	 * Returns the number of train stations.
	 * @return the number of train stations
	 */
	public int getTrainStations() {
		return trainStations;
	}
	
	/**
	 * Returns the number of bus stops.
	 * @return the number of bus stops
	 */
	public int getBusStops() {
		return busStops;
	}
	
	/**
	 * Returns the number of trains.
	 * @return the number of trains
	 */
	public int getTrains() {
		return trains;
	}
	
	/**
	 * Returns the current year's balance.
	 * @return the current year's balance, in GBP.
	 */
	public long getIncome() {
		return income;
	}
	
	/**
	 * Returns the loan amount.
	 * @return the loan amount, in GBP.
	 */
	public long getLoan() {
		return loan;
	}
	
	/**
	 * Returns the possessed money amount, excluding loan.
	 * @return the possessed money amount, excluding load, in GBP.
	 */
	public long getMoney() {
		return money;
	}
	
	/**
	 * Returns the company's value.
	 * @return the company's value, in GBP.
	 */
	public long getValue() {
		return value;
	}
	
	/**
	 * Returns the company's performance score.
	 * @return the company's performance score, unitless.
	 */
	public int getPerformance() {
		return performance;
	}
	
	/**
	 * Stores/updates economical data of the company.
	 * @param economy economical data of the company
	 */
	public void updateData(CompanyEconomy economy) {
		this.income = economy.getIncome();
		this.loan = economy.getLoan();
		this.money = economy.getMoney();
		
		this.value = economy.getPastCompanyValue()[0];
		//economy.getPastDeliveredCargo()[0];
		this.performance = economy.getPastPerformance()[0];
	}
	
	/**
	 * Stores/updates statistical data of the company.
	 * @param stats statistical data of the company
	 */
	public void updateData(CompanyStatistics stats) {
		this.airports = stats.getNumberOfAirports();
		this.planes = stats.getNumberOfPlanes();
		this.trains = stats.getNumberOfTrains();
		this.trainStations = stats.getNumberOfTrainStations();
		this.ships = stats.getNumberOfShips();
		this.harbours = stats.getNumberOfHarbours();
		this.busses = stats.getNumberOfBusses();
		this.busStops = stats.getNumberOfBusStops();
		this.lorries = stats.getNumberOfLorries();
		this.lorryDepots = stats.getNumberOfLorryDepots();
	}
	
	/**
	 * Returns a simple name of a color.
	 * @param colorId color ID
	 * @return English name of the color
	 */
	public static String getColorName(Color colorId) {
		String color;
		switch (colorId) {
		case COLOUR_BLUE: color = "blue"; break;
		case COLOUR_BROWN: color = "brown"; break;
		case COLOUR_CREAM: color = "cream"; break;
		case COLOUR_DARK_BLUE: color = "dark_blue"; break;
		case COLOUR_DARK_GREEN: color = "dark_green"; break;
		case COLOUR_GREEN: color = "green"; break;
		case COLOUR_GREY: color = "grey"; break;
		case COLOUR_LIGHT_BLUE: color = "light_blue"; break;
		case COLOUR_MAUVE: color = "mauve"; break;
		case COLOUR_ORANGE: color = "orange"; break;
		case COLOUR_PALE_GREEN: color = "pale_green"; break;
		case COLOUR_PINK: color = "pink"; break;
		case COLOUR_PURPLE: color = "purple"; break;
		case COLOUR_RED: color = "red"; break;
		case COLOUR_WHITE: color = "white"; break;
		case COLOUR_YELLOW: color = "yellow"; break;
		default: color = String.valueOf(colorId); break;
		}
		
		return color;
	}
	
	/**
	 * Returns a color of the company.
	 * @return color of the company or null if not available
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Returns a color name of the company.
	 * @return color name of the company or null if not available
	 */
	public String getColorName() {
		if (color == null) {
			return null;
		} else {
			return getColorName(color);
		}
	}
	
	/**
	 * Returns a name of the manager.
	 * @return name of the manager or null if not available
	 */
	public String getManagerName() {
		return managerName;
	}
	
	/**
	 * Returns a value denoting whether a password has been set.
	 * @return true if the company is password-protected, false otherwise
	 */
	public boolean isPasswordProtected() {
		return passwordProtected;
	}
}
