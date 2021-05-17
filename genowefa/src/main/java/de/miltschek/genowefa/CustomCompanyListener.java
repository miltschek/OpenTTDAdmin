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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.genowefa.Configuration.DenyRule;
import de.miltschek.genowefa.Context.CompanyDataProvider;
import de.miltschek.genowefa.Context.EventType;
import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.Color;
import de.miltschek.openttdadmin.data.CompanyEconomy;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.CompanyListenerAdapter;
import de.miltschek.openttdadmin.data.CompanyStatistics;
import de.miltschek.openttdadmin.data.Date;

/**
 * Handler of company-specific events.
 */
public class CustomCompanyListener extends CompanyListenerAdapter implements CompanyDataProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomCompanyListener.class);
	
	private final Context context;
	private final HashMap<Byte, CompanyData> newCompanies = new HashMap<>();
	private final Collection<CompanyData> oldCompanies = new ArrayList<>();
	
	@Override
	public void clearCache() {
		synchronized (newCompanies) {
			newCompanies.clear();
		}
		
		synchronized (oldCompanies) {
			oldCompanies.clear();
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
		
		this.context.notifyAdmin(
			EventType.Company,
			":new: ID " + (companyId + 1) + " created");
	}
	
	@Override
	public void companyInfoReceived(CompanyInfo companyInfo) {
		LOGGER.info("Company info received {}, name {}, color {}, password-protected {}.", companyInfo.getIndex(), companyInfo.getCompanyName(), companyInfo.getColor(), companyInfo.isPasswordProtected());
		
		CompanyData companyData;
		synchronized (newCompanies) {
			companyData = newCompanies.get(companyInfo.getIndex());
			if (companyData == null) {
				companyData = new CompanyData(companyInfo.getIndex());
				newCompanies.put(companyInfo.getIndex(), companyData);
			}

			companyData.updateData(companyInfo);
		}
		
		this.context.companyUpdate(companyData);
		
		this.context.notifyAdmin(
			EventType.Company,
			":office: ID "
				+ (companyInfo.getIndex() + 1)
				+ ", color " + CompanyData.getColorName(companyInfo.getColor())
				+ ", name " + companyInfo.getCompanyName()
				+ ", manager " + companyInfo.getManagerName()
				+ ", " + (companyInfo.isPasswordProtected() ? "protected" : "unprotected"));
		
		boolean denyMatched = false;
		for (DenyRule denyRule : this.context.getDenyRules()) {
			if ("name".equals(denyRule.getType())) {
				if (companyInfo.getCompanyName() != null
						&& companyInfo.getCompanyName().matches(denyRule.getPattern())
						|| companyInfo.getManagerName() != null
						&& companyInfo.getManagerName().matches(denyRule.getPattern())) {
					denyMatched = true;
				}
			}
			
			if (denyMatched) {
				/*LOGGER.info("Closing company (0-based) {}:{}, manager {} due to a matching rule {}/{}.",
						companyInfo.getIndex(), companyInfo.getCompanyName(), companyInfo.getManagerName(),
						denyRule.getType(), denyRule.getPattern());
				this.context.resetCompany(companyInfo.getIndex(), denyRule.getMessage());*/
				this.context.notifyAdmin(EventType.AdminRequest,
						"Deny rule matched for company (1-based) " + (companyInfo.getIndex() + 1)
							+ ":" + companyInfo.getCompanyName()
							+ ", manager " + companyInfo.getManagerName()
							+ ". No automatic action taken.");
				return;
			}
		}
	}
	
	@Override
	public void companyRemoved(byte companyId, ClosureReason closureReason) {
		LOGGER.info("Company removed {}, reason {}.", companyId, closureReason);
		
		CompanyData companyData;
		synchronized (newCompanies) {
			companyData = newCompanies.remove(companyId);
			if (companyData != null) {
				companyData.closed(closureReason);
			}
		}
		
		if (companyData != null) {
			synchronized (oldCompanies) {
				oldCompanies.add(companyData);
			}
		}
		
		this.context.companyClose(companyId, this.context.getCurrentDate(), closureReason);
		
		this.context.notifyAdmin(
			EventType.Company,
			":hammer: ID "
				+ (companyId + 1)
				+ (companyData != null ? ", color " + companyData.getColorName() + ", name " + companyData.getName() : "")
				+ " closed " + closureReason);
	}
	
	@Override
	public void companyUpdated(CompanyInfo companyInfo) {
		LOGGER.info("Company updated received {}, name {}, color {}, password-protected {}.", companyInfo.getIndex(), companyInfo.getCompanyName(), companyInfo.getColor(), companyInfo.isPasswordProtected());
		
		CompanyData companyData;

		boolean newColor = false, newName = false, newManager = false, newPassword = false;
		synchronized (newCompanies) {
			companyData = newCompanies.get(companyInfo.getIndex());
			if (companyData == null) {
				companyData = new CompanyData(companyInfo.getIndex());
				newCompanies.put(companyInfo.getIndex(), companyData);
				
				newColor = true;
				newName = true;
				newManager = true;
				newPassword = true;
			} else {
				newColor = companyData.getColor() != companyInfo.getColor();
				newName = !companyInfo.getCompanyName().equals(companyData.getName());
				newManager = !companyInfo.getManagerName().equals(companyData.getManagerName());
				newPassword = companyData.isPasswordProtected() != companyInfo.isPasswordProtected();
			}

			companyData.updateData(companyInfo);
		}
		
		this.context.companyUpdate(companyData);
		
		this.context.notifyAdmin(
			EventType.Company,
			":office: ID "
				+ (companyInfo.getIndex() + 1)
				+ " updated"
				+ (newColor ? ", new color " + companyData.getColorName() : "")
				+ (newName ? ", new name " + companyData.getName() : "")
				+ (newManager ? ", new manager " + companyData.getManagerName() : "")
				+ (newPassword ? ", " + (companyData.isPasswordProtected() ? "protected" : "unprotected") : ""));
		
		boolean denyMatched = false;
		for (DenyRule denyRule : this.context.getDenyRules()) {
			if ("name".equals(denyRule.getType())) {
				if (companyInfo.getCompanyName() != null
						&& companyInfo.getCompanyName().matches(denyRule.getPattern())
						|| companyInfo.getManagerName() != null
						&& companyInfo.getManagerName().matches(denyRule.getPattern())) {
					denyMatched = true;
				}
			}
			
			if (denyMatched) {
				LOGGER.info("Closing company (0-based) {}:{}, manager {} due to a matching rule {}/{}.",
						companyInfo.getIndex(), companyInfo.getCompanyName(), companyInfo.getManagerName(),
						denyRule.getType(), denyRule.getPattern());
				this.context.notifyAdmin(EventType.AdminRequest,
						"Deny rule matched for company (1-based) " + (companyInfo.getIndex() + 1)
							+ ":" + companyInfo.getCompanyName()
							+ ", manager " + companyInfo.getManagerName()
							+ ". No automatic action taken.");
				return;
			}
		}
	}
	
	@Override
	public void companyEconomy(byte companyId, CompanyEconomy companyEconomy) {
		CompanyData companyData;
		
		synchronized (newCompanies) {
			companyData = newCompanies.get(companyId);
			
			if (companyData == null) {
				companyData = new CompanyData(companyId);
				newCompanies.put(companyId, companyData);
			}

			companyData.updateData(companyEconomy);
		}
		
		this.context.companyEconomyUpdate(companyId, companyEconomy);
	}
	
	@Override
	public void companyStatistics(byte companyId, CompanyStatistics companyStatistics) {
		CompanyData companyData;
		
		synchronized (newCompanies) {
			companyData = newCompanies.get(companyId);
			if (companyData == null) {
				companyData = new CompanyData(companyId);
				newCompanies.put(companyId, companyData);
			}

			companyData.updateData(companyStatistics);
		}
		
		this.context.companyStatisticsUpdate(companyId, companyStatistics);
	}
	
	@Override
	public CompanyData get(byte companyId) {
		synchronized (newCompanies) {
			return newCompanies.get(companyId);
		}
	}
	
	@Override
	public Collection<CompanyData> getAll() {
		List<CompanyData> result = new ArrayList<CompanyData>();
		
		synchronized (newCompanies) {
			result.addAll(newCompanies.values());
		}
		
		result.sort(new Comparator<CompanyData>() {
			@Override
			public int compare(CompanyData o1, CompanyData o2) {
				return Byte.compare(o1.getCompanyId(), o2.getCompanyId());
			}
		});
		
		return result;
	}
}