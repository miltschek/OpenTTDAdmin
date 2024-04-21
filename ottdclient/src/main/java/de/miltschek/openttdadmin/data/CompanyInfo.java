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
package de.miltschek.openttdadmin.data;

/**
 * Company information data.
 */
public class CompanyInfo {
	private byte index;
	private String companyName;
	private String managerName;
	private Color color;
	private boolean passwordProtected;
	private boolean inauguratedYearSet;
	private int inauguratedYear;
	private boolean aiSet;
	private boolean ai;
	private byte quartersOfBankruptcy;
	private boolean sharesSupported;
	private byte[] shareOwners;
	
	/** Special company ID denoting a town. */
	public static final byte TOWN = 0x0f;
	/** Special company ID denoting none/nobody/no company. */
	public static final byte NONE = 0x10;
	/** Special company ID denoting water. */
	public static final byte WATER = 0x11;
	/** Special company ID denoting deity (the game admin/server). */
	public static final byte DEITY = 0x12;
	/** Special company ID denoting an client that is joining a game. */
	public static final byte INACTIVE_CLIENT = (byte)253;
	/** Special company ID denoting a client that requests a new company. */
	public static final byte NEW_COMPANY = (byte)254;
	/** Special company ID denoting a spectator. */
	public static final byte SPECTATOR = (byte)255;
	
	/**
	 * Creates company information data (full record).
	 * @param index index of the company (ID minus one)
	 * @param companyName name of the company
	 * @param managerName name of the company's manager
	 * @param color color of the company
	 * @param passwordProtected true if the company is password protected, false otherwise
	 * @param inauguratedYear year of the company's opening
	 * @param ai true if it's an AI player, false otherwise
	 * @param monthsOfBankruptcy months of bankruptcy TODO: check how it is calculated
	 * @param sharesSupported a flag denoting whether shares buying/selling is supported
	 * @param shareOwners owners of company's shares (4 times 25%)
	 */
	public CompanyInfo(byte index, String companyName, String managerName, Color color, boolean passwordProtected,
			int inauguratedYear, boolean ai, byte quartersOfBankruptcy, boolean sharesSupported, byte[] shareOwners) {
		super();
		this.index = index;
		this.companyName = companyName;
		this.managerName = managerName;
		this.color = color;
		this.passwordProtected = passwordProtected;
		this.inauguratedYearSet = true;
		this.inauguratedYear = inauguratedYear;
		this.aiSet = true;
		this.ai = ai;
		this.quartersOfBankruptcy = quartersOfBankruptcy;
		this.sharesSupported = sharesSupported;
		this.shareOwners = shareOwners;
	}

	/**
	 * Creates company information data (update changes).
	 * @param index index of the company (ID minus one)
	 * @param companyName name of the company
	 * @param managerName name of the company's manager
	 * @param color color of the company
	 * @param passwordProtected true if the company is password protected, false otherwise
	 * @param quartersOfBankruptcy number quarters that the company is unable to pay its debts
	 * @param sharesSupported a flag denoting whether shares buying/selling is supported 
	 * @param shareOwners IDs of the owners of company's shares (4 times 25%)
	 */
	public CompanyInfo(byte index, String companyName, String managerName, Color color, boolean passwordProtected,
			byte quartersOfBankruptcy, boolean sharesSupported, byte[] shareOwners) {
		super();
		this.index = index;
		this.companyName = companyName;
		this.managerName = managerName;
		this.color = color;
		this.passwordProtected = passwordProtected;
		this.inauguratedYearSet = false;
		this.aiSet = false;
		this.quartersOfBankruptcy = quartersOfBankruptcy;
		this.sharesSupported = sharesSupported;
		this.shareOwners = shareOwners;
	}

	/**
	 * Gets the index of the company (ID minus one).
	 * @return index of the company (ID minus one)
	 */
	public byte getIndex() {
		return index;
	}

	/**
	 * Gets the name of the company.
	 * @return name of the company
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Gets the name of the company's manager.
	 * @return name of the company's manager
	 */
	public String getManagerName() {
		return managerName;
	}

	/**
	 * Gets the color of the company.
	 * @return color of the company
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Gets a value indicating whether the company is password-protected.
	 * @return true if the company is password protected, false otherwise
	 */
	public boolean isPasswordProtected() {
		return passwordProtected;
	}
	
	/**
	 * Gets a value indicating whether the inaugurated year is available.
	 * @return true of the inaugurated year is available, false otherwise
	 */
	public boolean isInauguratedYearSet() {
		return inauguratedYearSet;
	}

	/**
	 * Gets the year of the company's opening.
	 * Please check {@link #isInauguratedYearSet()} whether this value is valid.
	 * @return year of the company's opening
	 */
	public int getInauguratedYear() {
		return inauguratedYear;
	}
	
	/**
	 * Gets a value indicating whether the AI flag is available.
	 * @return true if the AI flag is available, false otherwise
	 */
	public boolean isAiSet() {
		return aiSet;
	}

	/**
	 * Gets a value indicating wheter the player is an AI.
	 * Please check {@link #isAiSet()} whether this value is valid.
	 * @return true if it's an AI player, false otherwise.
	 */
	public boolean isAi() {
		return ai;
	}

	/**
	 * Gets the number of quarters that the company is unable to pay its debts.
	 * @return the number of quarters that the company is unable to pay its debts
	 */
	public byte getQuartersOfBankruptcy() {
		return quartersOfBankruptcy;
	}
	
	/**
	 * Returns a flag denoting whether shares buying/selling is supported.
	 * @return true if the shares buying/selling is supported, false otherwise
	 */
	public boolean isSharesSupported() {
		return this.sharesSupported;
	}

	/**
	 * Gets the IDs of the owners of company's shares (4 times 25%).
	 * @param part the share to be returned (0..3)
	 * @return ID of the owner of the requested company's share
	 * @deprecated since OpenTTD14.0 shares are not supported, all values are set to 0
	 */
	public byte getShareOwner(int part) {
		return shareOwners[part];
	}
}
