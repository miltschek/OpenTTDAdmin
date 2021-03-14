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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.genowefa.Context.CompanyDataProvider;
import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.Color;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.CompanyListenerAdapter;

/**
 * Handler of company-specific events.
 */
public class CustomCompanyListener extends CompanyListenerAdapter implements CompanyDataProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomCompanyListener.class);
	
	private final Context context;
	private final HashMap<Byte, CompanyData> newCompanies = new HashMap<>();
	
	@Override
	public void clearCache() {
		synchronized (newCompanies) {
			newCompanies.clear();
		}
	}

	/**
	 * Creates the handler.
	 * @param context application's context
	 */
	public CustomCompanyListener(Context context) {
		this.context = context;
		context.registerCompanyDataProvider(this);
	}
	
	@Override
	public void companyCreated(byte companyId) {
		LOGGER.info("New company created {}.", companyId);
		
		this.context.notifyAdmin(":new: ID " + (companyId + 1) + " created");
	}
	
	@Override
	public void companyInfoReceived(CompanyInfo companyInfo) {
		LOGGER.info("Company info received {}, name {}, color {}, password-protected {}.", companyInfo.getIndex(), companyInfo.getCompanyName(), companyInfo.getColor(), companyInfo.isPasswordProtected());
		
		synchronized (newCompanies) {
			newCompanies.put(companyInfo.getIndex(), new CompanyData(companyInfo));
		}
		
		this.context.notifyAdmin(":office: ID "
				+ (companyInfo.getIndex() + 1)
				+ ", color " + CompanyData.getColorName(companyInfo.getColor())
				+ ", name " + companyInfo.getCompanyName()
				+ ", manager " + companyInfo.getManagerName()
				+ ", " + (companyInfo.isPasswordProtected() ? "protected" : "unprotected"));
	}
	
	@Override
	public void companyRemoved(byte companyId, ClosureReason closureReason) {
		LOGGER.info("Company removed {}, reason {}.", companyId, closureReason);
		
		CompanyData companyData;
		synchronized (newCompanies) {
			companyData = newCompanies.get(companyId);
			if (companyData != null) {
				companyData.setClosureReason(closureReason);
			}
		}
		
		this.context.notifyAdmin(":hammer: ID "
				+ (companyId + 1)
				+ (companyData != null && companyData.getCompanyInfo() != null ? ", color " + companyData.getColorName() + ", name " + companyData.getCompanyInfo().getCompanyName() : "")
				+ " closed " + closureReason);
	}
	
	@Override
	public void companyUpdated(CompanyInfo companyInfo) {
		LOGGER.info("Company updated received {}, name {}, color {}, password-protected {}.", companyInfo.getIndex(), companyInfo.getCompanyName(), companyInfo.getColor(), companyInfo.isPasswordProtected());
		
		CompanyData companyData;
		CompanyInfo previousCompanyInfo = null;
		synchronized (newCompanies) {
			companyData = newCompanies.get(companyInfo.getIndex());
			if (companyData == null) {
				companyData = new CompanyData(companyInfo);
				newCompanies.put(companyInfo.getIndex(), companyData);
			} else {
				previousCompanyInfo = companyData.getCompanyInfo();
				companyData.setCompanyInfo(companyInfo);
			}
		}
		
		this.context.notifyAdmin(":office: ID "
				+ (companyInfo.getIndex() + 1)
				+ " updated"
				+ (previousCompanyInfo == null || previousCompanyInfo.getColor() != companyInfo.getColor() ? ", new color " + CompanyData.getColorName(companyInfo.getColor()) : "")
				+ (previousCompanyInfo == null || !companyInfo.getCompanyName().equals(previousCompanyInfo.getCompanyName()) ? ", new name " + companyInfo.getCompanyName() : "")
				+ (previousCompanyInfo == null || !companyInfo.getManagerName().equals(previousCompanyInfo.getManagerName()) ? ", new manager " + companyInfo.getManagerName() : "")
				+ (previousCompanyInfo == null || previousCompanyInfo.isPasswordProtected() != companyInfo.isPasswordProtected() ? ", " + (companyInfo.isPasswordProtected() ? "protected" : "unprotected") : ""));
	}
	
	@Override
	public CompanyData get(byte companyId) {
		synchronized (newCompanies) {
			return newCompanies.get(companyId);
		}
	}
}