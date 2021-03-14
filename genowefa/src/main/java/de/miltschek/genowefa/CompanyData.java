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
import de.miltschek.openttdadmin.data.CompanyInfo;

/**
 * Local cache of company data.
 */
public class CompanyData {
	private CompanyInfo companyInfo;
	private ClosureReason closureReason;
	
	/**
	 * Stores company information. 
	 * @param companyInfo company information object
	 */
	public CompanyData(CompanyInfo companyInfo) {
		this.companyInfo = companyInfo;
	}
	
	/**
	 * Marks the company as closed.
	 * @param closureReason closure reason
	 */
	public void setClosureReason(ClosureReason closureReason) {
		this.closureReason = closureReason;
	}
	
	/**
	 * Updates the company information.
	 * @param companyInfo company information
	 */
	public void setCompanyInfo(CompanyInfo companyInfo) {
		this.companyInfo = companyInfo;
	}
	
	/**
	 * Gets the company information.
	 * @return company information
	 */
	public CompanyInfo getCompanyInfo() {
		return companyInfo;
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
	 * Returns a color name of the company.
	 * @return color name of the company or null if not available
	 */
	public String getColorName() {
		if (companyInfo == null) {
			return null;
		} else {
			return getColorName(companyInfo.getColor());
		}
	}
}
