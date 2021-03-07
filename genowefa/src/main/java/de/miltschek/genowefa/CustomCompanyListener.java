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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.Color;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.CompanyListenerAdapter;

/**
 * Handler of company-specific events.
 */
public class CustomCompanyListener extends CompanyListenerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomCompanyListener.class);
	
	private final Context context;

	/**
	 * Returns a simple name of a color.
	 * @param colorId color ID
	 * @return English name of the color
	 */
	private String getColor(Color colorId) {
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
	 * Creates the handler.
	 * @param context application's context
	 */
	public CustomCompanyListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void companyCreated(byte companyId) {
		LOGGER.info("New company created {}.", companyId);
		
		this.context.notifyAdmin(":new: company ID " + (companyId + 1));
	}
	
	@Override
	public void companyInfoReceived(CompanyInfo companyInfo) {
		LOGGER.info("Company info received {}, name {}, color {}, password-protected {}.", companyInfo.getIndex(), companyInfo.getCompanyName(), companyInfo.getColor(), companyInfo.isPasswordProtected());
		
		this.context.notifyAdmin(":office: company ID "
				+ (companyInfo.getIndex() + 1)
				+ " " + getColor(companyInfo.getColor())
				+ ": " + companyInfo.getCompanyName()
				+ ", manager " + companyInfo.getManagerName()
				+ ", pwd " + (companyInfo.isPasswordProtected() ? "yes" : "no"));
	}
	
	@Override
	public void companyRemoved(byte companyId, ClosureReason closureReason) {
		LOGGER.info("Company removed {}, reason {}.", companyId, closureReason);
		
		this.context.notifyAdmin(":hammer: company ID "
				+ (companyId + 1)
				+ " closed " + closureReason);
	}
	
	@Override
	public void companyUpdated(CompanyInfo companyInfo) {
		LOGGER.info("Company updated received {}, name {}, color {}, password-protected {}.", companyInfo.getIndex(), companyInfo.getCompanyName(), companyInfo.getColor(), companyInfo.isPasswordProtected());
		
		this.context.notifyAdmin(":office: company ID "
				+ (companyInfo.getIndex() + 1)
				+ " update " + getColor(companyInfo.getColor())
				+ ": " + companyInfo.getCompanyName()
				+ ", manager " + companyInfo.getManagerName()
				+ ", pwd " + (companyInfo.isPasswordProtected() ? "yes" : "no"));
	}
}