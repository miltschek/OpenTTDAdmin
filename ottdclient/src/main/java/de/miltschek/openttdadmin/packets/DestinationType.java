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
 * Destination of chat messages.
 */
public enum DestinationType {
	/** Send message/notice to all clients (All). */
	DESTTYPE_BROADCAST(0),
	/** Send message/notice to everyone playing the same company (Team). */
	DESTTYPE_TEAM(1),
	/** Send message/notice to only a certain client (Private). */
	DESTTYPE_CLIENT(2);

	private int value;

	private DestinationType(int value) {
		this.value = value;
	}
	
	/**
	 * Get a value associated with the destination type.
	 * @return a value associated with the destination type
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Get a destination type associated with the given value.
	 * @param value network-level value denoting a destination type
	 * @return a destination type associated with the given value
	 */
	public static DestinationType getEnum(int value) {
		switch (value) {
		case 0: return DESTTYPE_BROADCAST;
		case 1: return DESTTYPE_TEAM;
		case 2: return DESTTYPE_CLIENT;
		default:
			return null;
		}
	}
}
