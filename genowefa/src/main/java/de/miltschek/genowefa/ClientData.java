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
package de.miltschek.genowefa;

import de.miltschek.integrations.GeoIp;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.Date;
import de.miltschek.openttdadmin.data.ErrorCode;
import de.miltschek.openttdadmin.data.Language;

/**
 * Local cache of client data.
 */
public class ClientData {
	private final int clientId;
	private ClientInfo clientInfo;
	private GeoIp geoIp;
	private final long joinedTs;
	private long leftTs;
	private Date leftGameDate;
	private ErrorCode errorCode;
	
	/**
	 * Stores client info object and geolocation data.
	 * @param clientInfo client info object
	 * @param geoIp geolocation data
	 */
	public ClientData(int clientId, GeoIp geoIp) {
		this.clientId = clientId;
		this.geoIp = geoIp;
		this.joinedTs = System.currentTimeMillis();
	}
	
	/**
	 * Returns the client ID.
	 * @return the client ID
	 */
	public int getClientId() {
		return this.clientId;
	}
	
	/**
	 * Returns the name of the client or null if unknown.
	 * @return the name of the client or null if unknown
	 */
	public String getName() {
		return this.clientInfo == null ? null : this.clientInfo.getClientName();
	}
	
	/**
	 * Returns the network address of the client or null if unknown.
	 * @return the network address of the client or null if unknown
	 */
	public String getNetworkAddress() {
		return this.clientInfo == null ? null : this.clientInfo.getNetworkAddress();
	}
	
	/**
	 * Returns the country code of the client or null if unknown.
	 * @return the country code (ISO 3166-1 alpha-2) of the client or null if unknown
	 */
	public String getCountryCode() {
		return this.geoIp == null ? null : this.geoIp.getCountryCode();
	}
	
	/**
	 * Returns the English name of the country of the client or null if unknown
	 * @return the English name of the country of the client or null if unknown
	 */
	public String getCountry() {
		return this.geoIp == null ? null : this.geoIp.getCountry();
	}
	
	/**
	 * Returns the English name of the city of the client or null if unknown.
	 * @return the English name of the city of the client or null if unknown.
	 */
	public String getCity() {
		return this.geoIp == null ? null : this.geoIp.getCity();
	}
	
	/**
	 * Returns a value denoting whether the address of the client is considered to be a proxy/VPN or false if unknown.
	 * @return true if the address is consideres as a proxy/VPN, false otherwise (including unknown)
	 */
	public boolean isProxy() {
		return this.geoIp == null ? false : this.geoIp.isProxy();
	}
	
	/**
	 * Returns the language setting of the client or null if unknown.
	 * @return the language setting of the client or null if unknown
	 */
	public Language getLanguage() {
		return this.clientInfo == null ? null : this.clientInfo.getLanguage();
	}
	
	/**
	 * Returns the in-game date of joining the game of the client or null if unknown.
	 * @return the in-game date of joining the game of the client or null if unknown
	 */
	public Date getJoinDate() {
		return this.clientInfo == null ? null : this.clientInfo.getJoinDate();
	}
	
	/**
	 * Returns the company ID of the player or a spectator identifier if unknown.
	 * @return the company ID of the player or a spectator identifier if unknown
	 */
	public byte getPlaysAs() {
		return this.clientInfo == null ? CompanyInfo.SPECTATOR : this.clientInfo.getPlayAs();
	}
	
	/**
	 * Sets the client info.
	 * @param clientInfo client info to be set
	 */
	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}
	
	/**
	 * Get an error code.
	 * @return error code
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Sets an error code.
	 * @param errorCode error code
	 */
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Returns a timestamp when the client joined the server.
	 * @return a timestamp (milliseconds since Jan, 1st 1970 UTC)
	 */
	public long getJoinedTs() {
		return joinedTs;
	}
	
	/**
	 * Returns a timestamp when the client left the server or 0 if still active.
	 * @return a timestamp (milliseconds since Jan, 1st 1970 UTC) or 0 if still active
	 */
	public long getLeftTs() {
		return leftTs;
	}
	
	/**
	 * Returns a game date when the client left the server or null if still active.
	 * @return a game date when the client left the server or null if still active
	 */
	public Date getLeftGameDate() {
		return leftGameDate;
	}
	
	/**
	 * Marks the client as left and stores the current timestamp for the event.
	 */
	public void left(Date gameDate) {
		this.leftTs = System.currentTimeMillis();
		this.leftGameDate = gameDate;
	}
}