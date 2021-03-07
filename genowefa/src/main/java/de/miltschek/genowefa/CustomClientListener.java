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

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.integrations.GeoIp;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.ClientListenerAdapter;
import de.miltschek.openttdadmin.data.ErrorCode;

/**
 * Handler of client-specific events.
 */
public class CustomClientListener extends ClientListenerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomClientListener.class);

	private final Context context;
	private final HashMap<Integer, ClientData> newClients = new HashMap<>();

	/**
	 * Local cache of client data.
	 */
	private static class ClientData {
		private ClientInfo clientInfo;
		private GeoIp geoIp;
		
		/**
		 * Stores client info object and geolocation data.
		 * @param clientInfo client info object
		 * @param geoIp geolocation data
		 */
		private ClientData(ClientInfo clientInfo, GeoIp geoIp) {
			this.clientInfo = clientInfo;
			this.geoIp = geoIp;
		}
	}
	
	/**
	 * Creates the handler.
	 * @param context application's context
	 */
	public CustomClientListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void clientInfoReceived(ClientInfo clientInfo) {
		if (this.context.tryResetCompany(clientInfo.getClientId(),
				clientInfo.getPlayAs(),
				(clientId, companyId) -> {
					LOGGER.info("Kicking user {}.", clientId);
					this.context.kickUser(clientId, "resetting company; please re-join");

					// ugly: since we don't know, how many clients are still to be delivered
					// we need to try our luck with each client to achieve the goal = reset the company
					LOGGER.info("Trying to reset the company {}.", companyId);
					this.context.resetCompany(companyId);
		})) {
			// within of a reset-time-window, nothing more to do
			return;
		}

		// the further part will be executed only if not within the reset-company-process
		
		GeoIp geoIp = GeoIp.lookup(clientInfo.getNetworkAddress());
		if (geoIp == null) {
			LOGGER.warn("User info {} IP {} no geo info.", clientInfo.getClientId(), clientInfo.getNetworkAddress());
		} else {
			LOGGER.info("User info {} IP {} from {}, {} proxy {}.", clientInfo.getClientId(), clientInfo.getNetworkAddress(), geoIp.getCountry(), geoIp.getCity(), geoIp.isProxy());
		}
		
		this.context.notifyAdmin(":bust_in_silhouette: user "
				+ clientInfo.getClientId()
				+ " " + clientInfo.getClientName()
				+ " " + clientInfo.getNetworkAddress()
				+ ((geoIp != null) ? (" " + geoIp.getCountry() + ", " + geoIp.getCity()) + (geoIp.isProxy() ? ", proxy" : ""): ""));

		synchronized (newClients) {
			newClients.put(clientInfo.getClientId(), new ClientData(clientInfo, geoIp));
		}
	}
	
	@Override
	public void clientConnected(int clientId) {
		LOGGER.info("New user {}.", clientId);
		
		ClientData clientData;
		synchronized (newClients) {
			clientData = newClients.get((Integer)clientId);
		}

		String welcomeMessage;
		if (clientData.geoIp == null) {
			welcomeMessage = this.context.getWelcomeMessage("*")
					.replaceAll("[$][{]COUNTRY[}]", "the Universe")
					.replaceAll("[$][{]CITY[}]", "the beautiful City");
		} else {
			welcomeMessage = this.context.getWelcomeMessage(clientData.geoIp.getCountryCode())
					.replaceAll("[$][{]COUNTRY[}]", clientData.geoIp.getCountry())
					.replaceAll("[$][{]CITY[}]", clientData.geoIp.getCity());
		}
		
		welcomeMessage = welcomeMessage.replaceAll("[$][{]USERNAME[}]", clientData.clientInfo.getClientName());
		this.context.notifyAll(welcomeMessage);
		
		this.context.notifyAdmin(":bust_in_silhouette: new user " + clientId);

		try {
			File onNewClient = new File(this.context.getWelcomeMessagePath());
			if (onNewClient.exists()) {
				for (String line : Files.readAllLines(onNewClient.toPath())) {
					this.context.notifyUser(clientId, line);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Failed to send on-new-client message.", ex);
		}
	}
	
	@Override
	public void clientDisconnected(int clientId) {
		LOGGER.info("User {} disconnected.", clientId);
		
		this.context.notifyAdmin(":runner: user " + clientId + " left");
	}
	
	@Override
	public void clientError(int clientId, ErrorCode errorCode) {
		LOGGER.info("Client {} error {}.", clientId, errorCode);
		
		this.context.notifyAdmin(":punch: user " + clientId + " error " + errorCode);
	}
	
	@Override
	public void clientUpdated(int clientId, String clientName, byte playAs) {
		LOGGER.info("Client {} update name {}, update company {}.", clientId, clientName, playAs);
		
		this.context.notifyAdmin(":bust_in_silhouette: user "
				+ clientId
				+ " username " + clientName
				+ " plays " + playAs);
	}
}