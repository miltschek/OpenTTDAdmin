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
public enum NetworkErrorCode {
	NETWORK_ERROR_GENERAL(0), // Try to use this one like never

	/* Signals from clients */
	NETWORK_ERROR_DESYNC(1),
	NETWORK_ERROR_SAVEGAME_FAILED(2),
	NETWORK_ERROR_CONNECTION_LOST(3),
	NETWORK_ERROR_ILLEGAL_PACKET(4),
	NETWORK_ERROR_NEWGRF_MISMATCH(5),

	/* Signals from servers */
	NETWORK_ERROR_NOT_AUTHORIZED(6),
	NETWORK_ERROR_NOT_EXPECTED(7),
	NETWORK_ERROR_WRONG_REVISION(8),
	NETWORK_ERROR_NAME_IN_USE(9),
	NETWORK_ERROR_WRONG_PASSWORD(10),
	NETWORK_ERROR_COMPANY_MISMATCH(11), // Happens in CLIENT_COMMAND
	NETWORK_ERROR_KICKED(12),
	NETWORK_ERROR_CHEATER(13),
	NETWORK_ERROR_FULL(14),
	NETWORK_ERROR_TOO_MANY_COMMANDS(15),
	NETWORK_ERROR_TIMEOUT_PASSWORD(16),
	NETWORK_ERROR_TIMEOUT_COMPUTER(17),
	NETWORK_ERROR_TIMEOUT_MAP(18),
	NETWORK_ERROR_TIMEOUT_JOIN(19);
	
	private int value;
	
	private NetworkErrorCode(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static NetworkErrorCode getEnum(int value) {
		switch (value) {
		case 0: return NETWORK_ERROR_GENERAL;
		case 1: return NETWORK_ERROR_DESYNC;
		case 2: return NETWORK_ERROR_SAVEGAME_FAILED;
		case 3: return NETWORK_ERROR_CONNECTION_LOST;
		case 4: return NETWORK_ERROR_ILLEGAL_PACKET;
		case 5: return NETWORK_ERROR_NEWGRF_MISMATCH;
		case 6: return NETWORK_ERROR_NOT_AUTHORIZED;
		case 7: return NETWORK_ERROR_NOT_EXPECTED;
		case 8: return NETWORK_ERROR_WRONG_REVISION;
		case 9: return NETWORK_ERROR_NAME_IN_USE;
		case 10: return NETWORK_ERROR_WRONG_PASSWORD;
		case 11: return NETWORK_ERROR_COMPANY_MISMATCH;
		case 12: return NETWORK_ERROR_KICKED;
		case 13: return NETWORK_ERROR_CHEATER;
		case 14: return NETWORK_ERROR_FULL;
		case 15: return NETWORK_ERROR_TOO_MANY_COMMANDS;
		case 16: return NETWORK_ERROR_TIMEOUT_PASSWORD;
		case 17: return NETWORK_ERROR_TIMEOUT_COMPUTER;
		case 18: return NETWORK_ERROR_TIMEOUT_MAP;
		case 19: return NETWORK_ERROR_TIMEOUT_JOIN;
		default:
			return null;
		}
	}
}
