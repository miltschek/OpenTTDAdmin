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
 * The server gives the admin some economy related company information.
 */
public class ServerCompanyEconomy extends OttdPacket {
	private static final int HISTORY_SIZE = 2;
	
	private byte index;
	private long money;
	private long loan;
	private long income;
	private int deliveredCargo;
	
	private long[] pastCompanyValue = new long[HISTORY_SIZE];
	private int[] pastPerformance = new int[HISTORY_SIZE];
	private int[] pastDeliveredCargo = new int[HISTORY_SIZE];
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param buffer buffer containing raw data
	 */
	public ServerCompanyEconomy(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.index = readByte();
		this.money = readInt64();
		this.loan = readInt64();
		this.income = readInt64();
		this.deliveredCargo = readInt16();
		
		for (int n = 0; n < HISTORY_SIZE; n++) {
			this.pastCompanyValue[n] = readInt64();
			this.pastPerformance[n] = readInt16();
			this.pastDeliveredCargo[n] = readInt16();
		}
	}

	/**
	 * Returns a number of quarter contained in the report.
	 * @return number of quarter contained in the report
	 */
	public static int getHistorySize() {
		return HISTORY_SIZE;
	}

	/**
	 * Returns the company ID.
	 * @return the company ID
	 */
	public byte getIndex() {
		return index;
	}

	/**
	 * Returns the amount of money.
	 * @return the amount of money
	 */
	public long getMoney() {
		return money;
	}

	/**
	 * Returns the amount of a loan.
	 * @return the amount of a loan
	 */
	public long getLoan() {
		return loan;
	}

	/**
	 * Returns the income.
	 * @return the income
	 */
	public long getIncome() {
		return income;
	}

	/**
	 * Returns the delivered cargo TODO verify the units.
	 * @return the delivered cargo TODO verify the units
	 */
	public int getDeliveredCargo() {
		return deliveredCargo;
	}

	/**
	 * Returns the company value for the given quarter.
	 * @param quarter the quarter in the range 0..getHistorySize()-1
	 * @return the company value for the given quarter
	 */
	public long getPastCompanyValue(int quarter) {
		return pastCompanyValue[quarter];
	}

	/**
	 * Returns the company performance for the given quarter.
	 * @param quarter the quarter in the range 0..getHistorySize()-1
	 * @return the company performance for the given quarter
	 */
	public int getPastPerformance(int quarter) {
		return pastPerformance[quarter];
	}

	/**
	 * Returns the delivered cargo TODO verify the units for the given quarter.
	 * @param quarter the quarter in the range 0..getHistorySize()-1
	 * @return the delivered cargo TODO verify the units for the given quarter
	 */
	public int getPastDeliveredCargo(int quarter) {
		return pastDeliveredCargo[quarter];
	}
}
