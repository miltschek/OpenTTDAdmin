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
 * Actions that can be used for NetworkTextMessage.
 */
public enum NetworkAction {
	/** Client joined. */
	NETWORK_ACTION_JOIN(0),
	/** Client left. */
	NETWORK_ACTION_LEAVE(1),
	/** Broadcast server message. */
	NETWORK_ACTION_SERVER_MESSAGE(2),
	/** Broadcast chat message. */
	NETWORK_ACTION_CHAT(3),
	/** Chat message to a company. */
	NETWORK_ACTION_CHAT_COMPANY(4),
	/** Chat message to a client. */
	NETWORK_ACTION_CHAT_CLIENT(5),
	/** Transfer funds (money) from one company to another. */
	NETWORK_ACTION_GIVE_MONEY(6),
	/** Client name change. */
	NETWORK_ACTION_NAME_CHANGE(7),
	/** Client moved to spectators. */
	NETWORK_ACTION_COMPANY_SPECTATOR(8),
	/** Client joined a company. */
	NETWORK_ACTION_COMPANY_JOIN(9),
	/** Client established a new company. */
	NETWORK_ACTION_COMPANY_NEW(10),
	/** Client kicked out of the server. */
	NETWORK_ACTION_KICKED(11);
	
	private int value;
	
	private NetworkAction(int value) {
		this.value = value;
	}
	
	/**
	 * Get a value associated with the network action.
	 * @return a value associated with the network action
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Get a network action associated with the given value.
	 * @param value network-level value denoting a network action
	 * @return a network action associated with the given value
	 */
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
