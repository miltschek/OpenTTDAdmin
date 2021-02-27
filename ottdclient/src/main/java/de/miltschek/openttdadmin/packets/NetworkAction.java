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
public enum NetworkAction {

	NETWORK_ACTION_JOIN(0),
	NETWORK_ACTION_LEAVE(1),
	NETWORK_ACTION_SERVER_MESSAGE(2),
	NETWORK_ACTION_CHAT(3),
	NETWORK_ACTION_CHAT_COMPANY(4),
	NETWORK_ACTION_CHAT_CLIENT(5),
	NETWORK_ACTION_GIVE_MONEY(6),
	NETWORK_ACTION_NAME_CHANGE(7),
	NETWORK_ACTION_COMPANY_SPECTATOR(8),
	NETWORK_ACTION_COMPANY_JOIN(9),
	NETWORK_ACTION_COMPANY_NEW(10),
	NETWORK_ACTION_KICKED(11);
	
	private int value;
	
	private NetworkAction(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static NetworkAction getEnum(int value) {
		switch (value) {
		case 0: return NETWORK_ACTION_JOIN;
		case 1: return NETWORK_ACTION_LEAVE;
		case 2: return NETWORK_ACTION_SERVER_MESSAGE;
		case 3: return NETWORK_ACTION_CHAT;
		case 4: return NETWORK_ACTION_CHAT_COMPANY;
		case 5: return NETWORK_ACTION_CHAT_CLIENT;
		case 6: return NETWORK_ACTION_GIVE_MONEY;
		case 7: return NETWORK_ACTION_NAME_CHANGE;
		case 8: return NETWORK_ACTION_COMPANY_SPECTATOR;
		case 9: return NETWORK_ACTION_COMPANY_JOIN;
		case 10: return NETWORK_ACTION_COMPANY_NEW;
		case 11: return NETWORK_ACTION_KICKED;
		default:
			return null;
		}
	}
}
