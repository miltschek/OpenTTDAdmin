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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

/**
 * A lock for the subsequent objects for the company-reset process.
 */
public class ResetLock {
	/** Time window hoping to be enough for a successful company reset process. */
	private static final long RESET_TIME_WINDOW = 10000;
	/** Timestamp of the last company reset request. */
	private long lastResetRequest;
	/** Client ID of the current company reset request. */
	private int resetRequestClientId;
	/** Flag indicating whether a company has been found. */
	private boolean companyFound;
	/** Company ID that is to be reset. */
	private byte companyToReset;
	/** Collection of client IDs and their company IDs. */
	private Map<Integer, Byte> clientsCompanies = new HashMap<Integer, Byte>();

	/**
	 * Stores temporary data for a duration of a reset process.
	 * @param clientId client ID who requested a company reset
	 * @return true if the process has been started, false if not possible (e.g. another process still active)
	 */
	public synchronized boolean startResetProcess(int clientId) {
		long now = System.currentTimeMillis();
		if (this.lastResetRequest < now - RESET_TIME_WINDOW) {
			this.lastResetRequest = now;
			this.resetRequestClientId = clientId;
			this.companyFound = false;
			this.clientsCompanies.clear();
			
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Sends a company reset request to the server.
	 * @param clientId client ID to be cached for the reset process
	 * @param playAs client's company ID to be cached for the reset process
	 * @param resetCallback function taking care of kicking the client and trying to remove the company
	 * @return true if the reset process is still in place, false if no reset process is active at the moment
	 */
	public synchronized boolean tryResetCompany(int clientId, byte playAs, BiConsumer<Integer, Byte> resetCallback) {
		long now = System.currentTimeMillis();
		if (this.lastResetRequest >= now - RESET_TIME_WINDOW) {
			// keep all clients and their companies in cache
			this.clientsCompanies.put(clientId, playAs);
			
			if (this.resetRequestClientId == clientId) {
				// found the company to be reset
				this.companyToReset = playAs;
				this.companyFound = true;
			}
			
			// optimization: it's enough to try our luck only if at least the current client plays the company
			if (this.companyFound && this.companyToReset == playAs) {
				for (Entry<Integer, Byte> entry : this.clientsCompanies.entrySet()) {
					if (entry.getValue() == this.companyToReset) {
						resetCallback.accept(entry.getKey(), playAs);
					}
				}
				
				// don't need to kick same players again in next iterations
				this.clientsCompanies.clear();
			}
			
			return true;
		} else {
			return false;
		}
	}
}
