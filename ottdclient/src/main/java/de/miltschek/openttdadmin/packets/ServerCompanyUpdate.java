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
public class ServerCompanyUpdate extends OttdPacket {
	private byte index;
	private String companyName;
	private String managerName;
	private byte color;
	private boolean passwordProtected;
	private byte monthsOfBankruptcy;
	private byte[] shareOwners = new byte[4];
	
	public ServerCompanyUpdate(byte[] buffer) {
		super(buffer);
		
		resetCursor();
		this.index = readByte();
		this.companyName = readString();
		this.managerName = readString();
		this.color = readByte();
		this.passwordProtected = readBoolean();
		this.monthsOfBankruptcy = readByte();
		
		for (int n = 0; n < this.shareOwners.length; n++) {
			this.shareOwners[n] = readByte();
		}
	}

	public byte getIndex() {
		return index;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getManagerName() {
		return managerName;
	}

	public byte getColor() {
		return color;
	}

	public boolean isPasswordProtected() {
		return passwordProtected;
	}

	public byte getMonthsOfBankruptcy() {
		return monthsOfBankruptcy;
	}

	public byte getShareOwners(int part) {
		return shareOwners[part];
	}
	
	
}
