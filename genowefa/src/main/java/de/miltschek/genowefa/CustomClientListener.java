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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.genowefa.Context.ClientDataProvider;
import de.miltschek.genowefa.Context.EventType;
import de.miltschek.integrations.GeoIp;
import de.miltschek.openttdadmin.data.ClientInfo;
import de.miltschek.openttdadmin.data.ClientListenerAdapter;
import de.miltschek.openttdadmin.data.CompanyInfo;
import de.miltschek.openttdadmin.data.ErrorCode;

/**
 * Handler of client-specific events.
 */
public class CustomClientListener extends ClientListenerAdapter implements ClientDataProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomClientListener.class);

	private final Context context;
	private final HashMap<Integer, ClientData> newClients = new HashMap<>();

	/**
	 * Returns a 1-based company identifier or a name for special cases (e.g. a spectator).
	 * @param company 0-based company identifier (as from playAs).
	 * @return 1-based company identifier or a name for special cases (e.g. a spectator).
	 */
	private String getCompanyDescription(byte company) {
		switch (company) {
		case CompanyInfo.DEITY: return "deity";
		case CompanyInfo.INACTIVE_CLIENT: return "inactive";
		case CompanyInfo.NEW_COMPANY: return "new";
		case CompanyInfo.NONE: return "none";
		case CompanyInfo.SPECTATOR: return "spectator";
		case CompanyInfo.TOWN: return "town";
		case CompanyInfo.WATER: return "water";
		default:
			{
				CompanyData companyData = this.context.getCompany(company);
				if (companyData == null) {
					return String.valueOf((company & 0xff) + 1);
				} else {
					return companyData.getColorName() + " (" + String.valueOf((company & 0xff) + 1) + ")";
				}
			}
		}
	}
	
	@Override
	public void clearCache() {
		synchronized (newClients) {
			newClients.clear();
		}
	}
	
	/**
	 * Creates the handler.
	 * @param context application's context
	 */
	public CustomClientListener(Context context) {
		this.context = context;
		context.registerClientDataProvider(this);
	}
	
	@Override
	public void clientInfoReceived(ClientInfo clientInfo) {
		if (this.context.tryResetCompany(clientInfo.getClientId(),
				clientInfo.getPlayAs(),
				(clientId, companyId) -> {
					LOGGER.info("Kicking user {}.", clientId);
					this.context.kickClient(clientId, "resetting company; please re-join");

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
		
		ClientData clientData = new ClientData(clientInfo.getClientId(), geoIp);
		clientData.setClientInfo(clientInfo);
		synchronized (newClients) {
			newClients.put(clientInfo.getClientId(), clientData);
		}
		
		this.context.clientUpdate(clientData);
		context.playerJoined(clientInfo.getClientId(), clientInfo.getPlayAs());

		this.context.notifyAdmin(
			EventType.Client,
			":bust_in_silhouette: ID "
				+ clientInfo.getClientId()
				+ ", name " + clientInfo.getClientName()
				+ ", IP " + clientInfo.getNetworkAddress()
				+ ", plays as " + getCompanyDescription(clientInfo.getPlayAs())
				+ ", joined " + clientInfo.getJoinDate()
				// + ", lang " + clientInfo.getLanguage() // it's always 'Any'
				+ ((geoIp != null) ? (", from " + geoIp.getCountry() + ", " + geoIp.getCity()) + (geoIp.isProxy() ? ", proxy" : ""): ""));
	}
	
	@Override
	public void clientConnected(int clientId) {
		LOGGER.info("New user {}.", clientId);
		
		ClientData clientData;
		synchronized (newClients) {
			clientData = newClients.get((Integer)clientId);
			if (clientData == null) {
				clientData = new ClientData(clientId, null);
				newClients.put(clientId, clientData);
			}
		}

		String welcomeMessage;
		if (clientData.getGeoIp() == null) {
			welcomeMessage = this.context.getWelcomeMessage("*")
					.replaceAll("[$][{]COUNTRY[}]", "the Universe")
					.replaceAll("[$][{]CITY[}]", "the beautiful City");
		} else {
			welcomeMessage = this.context.getWelcomeMessage(clientData.getGeoIp().getCountryCode())
					.replaceAll("[$][{]COUNTRY[}]", clientData.getGeoIp().getCountry())
					.replaceAll("[$][{]CITY[}]", clientData.getGeoIp().getCity());
		}
		
		if (clientData.getName() == null) {
			welcomeMessage = welcomeMessage.replaceAll("[$][{]USERNAME[}]", "player");
		} else {
			welcomeMessage = welcomeMessage.replaceAll("[$][{]USERNAME[}]", clientData.getName());
		}
		
		this.context.notifyAll(welcomeMessage);
		
		this.context.notifyAdmin(
			EventType.Client,
			":bust_in_silhouette: new ID " + clientId
				+ (clientData.getName() == null ? "" : (", name " + clientData.getName())));

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
		
		ClientData clientData;
		synchronized (newClients) {
			clientData = newClients.get((Integer)clientId);
			if (clientData != null) {
				clientData.left(this.context.getCurrentDate());
			}
		}
		
		this.context.notifyAdmin(
			EventType.Client,
			":runner: ID " + clientId
				+ (clientData == null || clientData.getName() == null ? "" : (", name " + clientData.getName()))
				+ " left");
		
		context.playerLeft(clientId);
	}
	
	@Override
	public void clientError(int clientId, ErrorCode errorCode) {
		LOGGER.info("Client {} error {}.", clientId, errorCode);
		
		ClientData clientData;
		synchronized (newClients) {
			clientData = newClients.get((Integer)clientId);
			if (clientData != null) {
				clientData.setErrorCode(errorCode);
			}
		}

		this.context.notifyAdmin(
			EventType.Client,
			":punch: ID " + clientId
				+ (clientData == null || clientData.getName() == null ? "" : (", name " + clientData.getName()))
				+ " error " + errorCode);
	}
	
	@Override
	public void clientUpdated(int clientId, String clientName, byte playAs) {
		LOGGER.info("Client {} update name {}, update company {}.", clientId, clientName, playAs);
		
		ClientData clientData;
		synchronized (newClients) {
			clientData = newClients.get((Integer)clientId);
			if (clientData == null) {
				clientData = new ClientData(clientId, null);
				clientData.setClientInfo(new ClientInfo(clientId, null, clientName, null, null, playAs));
				newClients.put(clientId, clientData);
			} else {
				clientData.setClientInfo(new ClientInfo(clientId, clientData.getNetworkAddress(), clientName, clientData.getLanguage(), clientData.getJoinDate(), playAs));
			}
		}
		
		this.context.notifyAdmin(
			EventType.Client,
			":bust_in_silhouette: ID "
				+ clientId
				+ ", name " + clientName
				+ ", plays as " + getCompanyDescription(playAs));
		
		context.clientUpdate(clientData);
		context.playerJoined(clientId, playAs);
	}

	@Override
	public ClientData get(int clientId) {
		synchronized (this.newClients) {
			return this.newClients.get(clientId);
		}
	}
	
	@Override
	public Collection<ClientData> getAll() {
		List<ClientData> result = new ArrayList<>();
		
		synchronized (this.newClients) {
			result.addAll(this.newClients.values());
		}
		
		result.sort(new Comparator<ClientData>() {
			@Override
			public int compare(ClientData o1, ClientData o2) {
				return Integer.compare(o1.getClientId(), o2.getClientId());
			}
		});
		
		return result;
	}
}