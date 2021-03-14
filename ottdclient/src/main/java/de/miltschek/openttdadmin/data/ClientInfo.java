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
package de.miltschek.openttdadmin.data;

/**
 * Client information.
 */
public class ClientInfo {
	private int clientId;
	private String networkAddress;
	private String clientName;
	private Language language;
	private Date joinDate;
	private byte playAs;
	
	/**
	 * Creates a client information object.
	 * @param clientId ID of the client
	 * @param networkAddress network address of the client
	 * @param clientName name of the client
	 * @param language ID of the language
	 * @param joinDate game-date when the client joined
	 * @param playAs ID of the company
	 */
	public ClientInfo(int clientId, String networkAddress, String clientName, Language language, Date joinDate,
			byte playAs) {
		super();
		this.clientId = clientId;
		this.networkAddress = networkAddress;
		this.clientName = clientName;
		this.language = language;
		this.joinDate = joinDate;
		this.playAs = playAs;
	}
	
	/**
	 * Gets the ID of the client.
	 * @return ID of the client
	 */
	public int getClientId() {
		return clientId;
	}
	
	/**
	 * Gets the network address of the client.
	 * @return network address of the client
	 */
	public String getNetworkAddress() {
		return networkAddress;
	}
	
	/**
	 * Gets the name of the client.
	 * @return name of the client
	 */
	public String getClientName() {
		return clientName;
	}
	
	/**
	 * Gets the ID of the language of the client.
	 * @return ID of the language of the client
	 */
	public Language getLanguage() {
		return language;
	}
	
	/**
	 * Gets the in-game date when the client joined
	 * @return in-game date when the client joined
	 */
	public Date getJoinDate() {
		return joinDate;
	}
	
	/**
	 * Gets the ID of the company the client is playing
	 * TODO: document special values
	 * @return ID of the company the client is playing
	 */
	public byte getPlayAs() {
		return playAs;
	}
}
