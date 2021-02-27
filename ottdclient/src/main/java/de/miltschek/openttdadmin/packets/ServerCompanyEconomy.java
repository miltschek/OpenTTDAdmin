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

	public static int getHistorySize() {
		return HISTORY_SIZE;
	}

	public byte getIndex() {
		return index;
	}

	public long getMoney() {
		return money;
	}

	public long getLoan() {
		return loan;
	}

	public long getIncome() {
		return income;
	}

	public int getDeliveredCargo() {
		return deliveredCargo;
	}

	public long getPastCompanyValue(int quarter) {
		return pastCompanyValue[quarter];
	}

	public int getPastPerformance(int quarter) {
		return pastPerformance[quarter];
	}

	public int getPastDeliveredCargo(int quarter) {
		return pastDeliveredCargo[quarter];
	}
	
	
}
