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
import de.miltschek.openttdadmin.data.ErrorCode;

/**
 * Local cache of client data.
 */
public class ClientData {
	private ClientInfo clientInfo;
	private GeoIp geoIp;
	private boolean left;
	private ErrorCode errorCode;
	
	/**
	 * Stores client info object and geolocation data.
	 * @param clientInfo client info object
	 * @param geoIp geolocation data
	 */
	public ClientData(ClientInfo clientInfo, GeoIp geoIp) {
		this.clientInfo = clientInfo;
		this.geoIp = geoIp;
	}
	
	/**
	 * Gets the client info.
	 * @return the client info
	 */
	public ClientInfo getClientInfo() {
		return clientInfo;
	}
	
	/**
	 * Sets the client info.
	 * @param clientInfo client info to be set
	 */
	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}
	
	/**
	 * Gets the geolocalization.
	 * @return the geolocalization
	 */
	public GeoIp getGeoIp() {
		return geoIp;
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
	 * Gets a value indicating whether the client has left the game.
	 * @return true if the client has left the game, false otherwise
	 */
	public boolean isLeft() {
		return left;
	}
	
	/**
	 * Sets the value indicating whether the client has left the game.
	 * @param left true if the client has left the game, false otherwise
	 */
	public void setLeft(boolean left) {
		this.left = left;
	}
}