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
 * Company economy data.
 */
public class CompanyEconomy {
	private long money;
	private long loan;
	private long income;
	private int deliveredCargo;
	
	private long[] pastCompanyValue;
	private int[] pastPerformance;
	private int[] pastDeliveredCargo;

	/**
	 * Creates a company economy data.
	 * @param money amount of money TODO: it seems, it's translated to some other currency
	 * @param loan amount of loan TODO: currency?
	 * @param income income TODO: how is it measured?
	 * @param deliveredCargo delivered cargo TODO: how is it measured?
	 * @param pastCompanyValue some number of past financial periods - company value TODO: currency?
	 * @param pastPerformance some number of past financial periods - performance TODO: what's that?
	 * @param pastDeliveredCargo some number of past financial periods - delivered cargo TODO: how is it measured?
	 */
	public CompanyEconomy(long money, long loan, long income, int deliveredCargo, long[] pastCompanyValue,
			int[] pastPerformance, int[] pastDeliveredCargo) {
		super();
		this.money = money;
		this.loan = loan;
		this.income = income;
		this.deliveredCargo = deliveredCargo;
		this.pastCompanyValue = pastCompanyValue;
		this.pastPerformance = pastPerformance;
		this.pastDeliveredCargo = pastDeliveredCargo;
	}

	/**
	 * Gets the amount of money.
	 * TODO: currency?
	 * @return amount of money
	 */
	public long getMoney() {
		return money;
	}

	/**
	 * Gets the amount of loan.
	 * TODO: currency?
	 * @return amount of loan
	 */
	public long getLoan() {
		return loan;
	}

	/**
	 * Gets the amount of income.
	 * TODO: currency?
	 * @return amount of income.
	 */
	public long getIncome() {
		return income;
	}

	/**
	 * Gets amount of? delivered cargo.
	 * TODO: how is it measured?
	 * @return amount of? delivered cargo
	 */
	public int getDeliveredCargo() {
		return deliveredCargo;
	}

	/**
	 * Gets past values of the company value.
	 * @return past values of the company value.
	 */
	public long[] getPastCompanyValue() {
		return pastCompanyValue;
	}

	/**
	 * Gets past values of company's performance.
	 * TODO: what's that?
	 * @return past values of company's performance.
	 */
	public int[] getPastPerformance() {
		return pastPerformance;
	}

	/**
	 * Gets past values of delivered cargo.
	 * TODO: how is it measured?
	 * @return past values of delivered cargo.
	 */
	public int[] getPastDeliveredCargo() {
		return pastDeliveredCargo;
	}
}
