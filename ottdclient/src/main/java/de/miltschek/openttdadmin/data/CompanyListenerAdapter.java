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
 * Adapter for company event listeners.
 */
public abstract class CompanyListenerAdapter {
	/**
	 * Called whenever a new company is created.
	 * @param companyId ID of the company
	 */
	public void companyCreated(byte companyId) {}
	
	/**
	 * Called whenever a company is removed/closed.
	 * @param companyId ID of the company
	 * @param closureReason the reason of a removal/closure
	 */
	public void companyRemoved(byte companyId, ClosureReason closureReason) {}
	
	/**
	 * Called whenever a company data has changed.
	 * Please not that some fields are not delivered in such cases.
	 * @param companyInfo company data object
	 */
	public void companyUpdated(CompanyInfo companyInfo) {}
	
	/**
	 * Called whenever a company information is delivered by the server.
	 * @param companyInfo company data object
	 */
	public void companyInfoReceived(CompanyInfo companyInfo) {}
	
	/**
	 * Called whenever a company economy data is delivered by the server.
	 * @param companyId ID of the company
	 * @param companyEconomy economy data object
	 */
	public void companyEconomy(byte companyId, CompanyEconomy companyEconomy) {}
	
	/**
	 * Called whenever a company statistical data is delivered by the server.
	 * @param companyId ID of the company
	 * @param companyStatistics statistical data object
	 */
	public void companyStatistics(byte companyId, CompanyStatistics companyStatistics) {}
}
