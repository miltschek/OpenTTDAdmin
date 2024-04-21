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
 * Frequencies, which may be registered for a certain update type.
 */
public enum UpdateFrequency {
	/** No update. */
	NONE(0),
	/** The admin can poll this. */
	ADMIN_FREQUENCY_POLL(0x01),
	/** The admin gets information about this on a daily basis. */
	ADMIN_FREQUENCY_DAILY(0x02),
	/** The admin gets information about this on a weekly basis. */
	ADMIN_FREQUENCY_WEEKLY(0x04),
	/** The admin gets information about this on a monthly basis. */
	ADMIN_FREQUENCY_MONTHLY(0x08),
	/** The admin gets information about this on a quarterly basis. */
	ADMIN_FREQUENCY_QUARTERLY(0x10),
	/** The admin gets information about this on a yearly basis. */
	ADMIN_FREQUENCY_ANUALLY(0x20),
	/** The admin gets information about this when it changes. */
	ADMIN_FREQUENCY_AUTOMATIC(0x40);
	
	private int value;
	
	private UpdateFrequency(int value) {
		this.value = value;
	}
	
	/**
	 * Get a value associated with the update frequency.
	 * @return a value associated with the update frequency
	 */
	public int getValue() {
		return value;
	}
	
	/*public static UpdateFrequency getEnum(int value) {
		switch (value) {
		case 0x01: return ADMIN_FREQUENCY_POLL;
		case 0x02: return ADMIN_FREQUENCY_DAILY;
		case 0x04: return ADMIN_FREQUENCY_WEEKLY;
		case 0x08: return ADMIN_FREQUENCY_MONTHLY;
		case 0x10: return ADMIN_FREQUENCY_QUARTERLY;
		case 0x20: return ADMIN_FREQUENCY_ANUALLY;
		case 0x40: return ADMIN_FREQUENCY_AUTOMATIC;
		default:
			return null;
		}
	}*/
}
