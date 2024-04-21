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
 * The server gives the admin information about a company.
 */
public class ServerCompanyInfo extends OttdPacket {
	private byte index;
	private String companyName;
	private String managerName;
	private byte color;
	private boolean passwordProtected;
	private int inauguratedYear;
	private boolean ai;
	private byte quartersOfBankruptcy;
	private boolean sharesSupported;
	private byte[] shareOwners = new byte[4];
	
	/**
	 * Interprets raw data to create a representation of the packet.
	 * @param protocolVersion protocol version number
	 * @param buffer buffer containing raw data
	 */
	public ServerCompanyInfo(byte protocolVersion, byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.index = readByte();
		this.companyName = readString();
		this.managerName = readString();
		this.color = readByte();
		this.passwordProtected = readBoolean();
		this.inauguratedYear = readInt32();
		this.ai = readBoolean();
		this.quartersOfBankruptcy = readByte();
		
		// company shares removed with the version 3
		if (protocolVersion < 3) {
			this.sharesSupported = true;
			for (int n = 0; n < this.shareOwners.length; n++) {
				this.shareOwners[n] = readByte();
			}
		} else {
			this.sharesSupported = false;
		}
	}

	/**
	 * Gets the ID of the company.
	 * @return the ID of the company
	 */
	public byte getIndex() {
		return index;
	}

	/**
	 * Gets the name of the company.
	 * @return the name of the company
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Gets the name of the manager of the company.
	 * @return the name of the manager of the company
	 */
	public String getManagerName() {
		return managerName;
	}

	/**
	 * Gets the ID of the color of the company.
	 * @return the ID of the color of the company
	 */
	public byte getColor() {
		return color;
	}

	/**
	 * Returns a flag denoting whether the company is password-protected.
	 * @return true if the company is password-protected, false otherwise
	 */
	public boolean isPasswordProtected() {
		return passwordProtected;
	}

	/**
	 * Returns the year-of-game when the company has been established.
	 * @return the year-of-game when the company has been established
	 */
	public int getInauguratedYear() {
		return inauguratedYear;
	}

	/**
	 * Return a flag denoting whether the company is computer-operated (AI).
	 * @return true if the company is computer-operated (AI), false if human-operated
	 */
	public boolean isAi() {
		return ai;
	}

	/**
	 * Returns the number of quarters (rounded to the next one) that the company is unable to pay its debts.
	 * @return the number of quarters (rounded to the next one) that the company is unable to pay its debts
	 */
	public byte getMonthsOfBankruptcy() {
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
	 * Returns an ID of the shareholder, if any. The shares are organized in 25% steps.
	 * @param one of the 25% shares of the company (0..3)
	 * @return holder ID of the share or 0 if not sold
	 * @deprecated since OpenTTD14.0 shares have been removed, the values are always 0 
	 */
	public byte getShareOwners(int part) {
		return shareOwners[part];
	}
}
