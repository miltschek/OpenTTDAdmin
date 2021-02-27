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
 * Adapter for client event listeners.
 */
public abstract class ClientListenerAdapter {
	/**
	 * Called whenever a new client connects to the game.
	 * @param clientId ID of the client
	 */
	public void clientConnected(int clientId) {}
	
	/**
	 * Called whenever a client leaves the game.
	 * @param clientId ID of the client
	 */
	public void clientDisconnected(int clientId) {}
	
	/**
	 * Called whenever a client changes her/his name or joins another company.
	 * TODO: document special values of the company IDs
	 * @param clientId ID of the client
	 * @param clientName name of the client
	 * @param playAs current company of the client
	 */
	public void clientUpdated(int clientId, String clientName, byte playAs) {}
	
	/**
	 * Called whenever a client information has been received from the server.
	 * @param clientInfo client information object
	 */
	public void clientInfoReceived(ClientInfo clientInfo) {}
	
	/**
	 * Called whenever an error related to a specific client occurred.
	 * @param clientId ID of the client
	 * @param errorCode error code
	 */
	public void clientError(int clientId, ErrorCode errorCode) {}
}
