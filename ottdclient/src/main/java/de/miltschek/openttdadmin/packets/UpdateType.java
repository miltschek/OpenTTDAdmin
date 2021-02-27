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
public enum UpdateType {
	/** ADMIN_FREQUENCY_POLL | ADMIN_FREQUENCY_DAILY | ADMIN_FREQUENCY_WEEKLY | ADMIN_FREQUENCY_MONTHLY | ADMIN_FREQUENCY_QUARTERLY | ADMIN_FREQUENCY_ANUALLY */
	ADMIN_UPDATE_DATE(0),            ///< Updates about the date of the game.
	/** ADMIN_FREQUENCY_POLL | ADMIN_FREQUENCY_AUTOMATIC */
	ADMIN_UPDATE_CLIENT_INFO(1),     ///< Updates about the information of clients.
	/** ADMIN_FREQUENCY_POLL | ADMIN_FREQUENCY_AUTOMATIC */
	ADMIN_UPDATE_COMPANY_INFO(2),    ///< Updates about the generic information of companies.
	/** ADMIN_FREQUENCY_POLL | ADMIN_FREQUENCY_WEEKLY | ADMIN_FREQUENCY_MONTHLY | ADMIN_FREQUENCY_QUARTERLY | ADMIN_FREQUENCY_ANUALLY */
	ADMIN_UPDATE_COMPANY_ECONOMY(3), ///< Updates about the economy of companies.
	/** ADMIN_FREQUENCY_POLL | ADMIN_FREQUENCY_WEEKLY | ADMIN_FREQUENCY_MONTHLY | ADMIN_FREQUENCY_QUARTERLY | ADMIN_FREQUENCY_ANUALLY */
	ADMIN_UPDATE_COMPANY_STATS(4),   ///< Updates about the statistics of companies.
	/** ADMIN_FREQUENCY_AUTOMATIC */
	ADMIN_UPDATE_CHAT(5),            ///< The admin would like to have chat messages.
	/** ADMIN_FREQUENCY_AUTOMATIC */
	ADMIN_UPDATE_CONSOLE(6),         ///< The admin would like to have console messages.
	/** ADMIN_FREQUENCY_POLL */
	ADMIN_UPDATE_CMD_NAMES(7),       ///< The admin would like a list of all DoCommand names.
	/** ADMIN_FREQUENCY_AUTOMATIC */
	ADMIN_UPDATE_CMD_LOGGING(8),     ///< The admin would like to have DoCommand information.
	/** ADMIN_FREQUENCY_AUTOMATIC */
	ADMIN_UPDATE_GAMESCRIPT(9);      ///< The admin would like to have gamescript messages.

	private int value;
	
	private UpdateType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static UpdateType getEnum(int value) {
		switch (value) {
		case 0: return ADMIN_UPDATE_DATE;
		case 1: return ADMIN_UPDATE_CLIENT_INFO;
		case 2: return ADMIN_UPDATE_COMPANY_INFO;
		case 3: return ADMIN_UPDATE_COMPANY_ECONOMY;
		case 4: return ADMIN_UPDATE_COMPANY_STATS;
		case 5: return ADMIN_UPDATE_CHAT;
		case 6: return ADMIN_UPDATE_CONSOLE;
		case 7: return ADMIN_UPDATE_CMD_NAMES;
		case 8: return ADMIN_UPDATE_CMD_LOGGING;
		case 9: return ADMIN_UPDATE_GAMESCRIPT;
		default:
			return null;
		}
	}
}
