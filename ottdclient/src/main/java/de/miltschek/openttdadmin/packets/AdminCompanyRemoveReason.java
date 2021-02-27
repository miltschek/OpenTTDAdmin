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
public enum AdminCompanyRemoveReason {
	ADMIN_CRR_MANUAL(0),    ///< The company is manually removed.
	ADMIN_CRR_AUTOCLEAN(1), ///< The company is removed due to autoclean.
	ADMIN_CRR_BANKRUPT(2);  ///< The company went belly-up.

	private int value;
	
	private AdminCompanyRemoveReason(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static AdminCompanyRemoveReason getEnum(int value) {
		switch (value) {
		case 0: return ADMIN_CRR_MANUAL;
		case 1: return ADMIN_CRR_AUTOCLEAN;
		case 2: return ADMIN_CRR_BANKRUPT;
		default:
			return null;
		}
	}
}
