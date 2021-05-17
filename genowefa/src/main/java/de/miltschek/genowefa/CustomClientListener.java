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
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.genowefa.Configuration.DenyRule;
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
	
	private static Pattern NO_NAME_PLAYER = Pattern.compile("^Player( [#][0-9]+)?$");
	
	private static final String[] NAMES = new String[] {
			"Aaron",
			"Abigail",
			"Adam",
			"Alan",
			"Albert",
			"Alexander",
			"Alexis",
			"Alice",
			"Amanda",
			"Amber",
			"Amy",
			"Andrea",
			"Andrew",
			"Angela",
			"Ann",
			"Anna",
			"Anthony",
			"Arthur",
			"Ashley",
			"Austin",
			"Barbara",
			"Benjamin",
			"Betty",
			"Beverly",
			"Billy",
			"Bobby",
			"Bradley",
			"Brandon",
			"Brenda",
			"Brian",
			"Brittany",
			"Bruce",
			"Bryan",
			"Carl",
			"Carol",
			"Carolyn",
			"Catherine",
			"Charles",
			"Charlotte",
			"Cheryl",
			"Christian",
			"Christina",
			"Christine",
			"Christopher",
			"Cynthia",
			"Daniel",
			"Danielle",
			"David",
			"Deborah",
			"Debra",
			"Denise",
			"Dennis",
			"Diana",
			"Diane",
			"Donald",
			"Donna",
			"Doris",
			"Dorothy",
			"Douglas",
			"Dylan",
			"Edward",
			"Elizabeth",
			"Emily",
			"Emma",
			"Eric",
			"Ethan",
			"Eugene",
			"Evelyn",
			"Frances",
			"Frank",
			"Gabriel",
			"Gary",
			"George",
			"Gerald",
			"Gloria",
			"Grace",
			"Gregory",
			"Hannah",
			"Harold",
			"Heather",
			"Helen",
			"Henry",
			"Isabella",
			"Jack",
			"Jacob",
			"Jacqueline",
			"James",
			"Janet",
			"Janice",
			"Jason",
			"Jean",
			"Jeffrey",
			"Jennifer",
			"Jeremy",
			"Jerry",
			"Jesse",
			"Jessica",
			"Joan",
			"Joe",
			"John",
			"Johnny",
			"Jonathan",
			"Jordan",
			"Jose",
			"Joseph",
			"Joshua",
			"Joyce",
			"Juan",
			"Judith",
			"Judy",
			"Julia",
			"Julie",
			"Justin",
			"Karen",
			"Katherine",
			"Kathleen",
			"Kathryn",
			"Kayla",
			"Keith",
			"Kelly",
			"Kenneth",
			"Kevin",
			"Kimberly",
			"Kyle",
			"Larry",
			"Laura",
			"Lauren",
			"Lawrence",
			"Linda",
			"Lisa",
			"Logan",
			"Louis",
			"Madison",
			"Margaret",
			"Maria",
			"Marie",
			"Marilyn",
			"Mark",
			"Martha",
			"Mary",
			"Matthew",
			"Megan",
			"Melissa",
			"Michael",
			"Michelle",
			"Nancy",
			"Natalie",
			"Nathan",
			"Nicholas",
			"Nicole",
			"Noah",
			"Olivia",
			"Pamela",
			"Patricia",
			"Patrick",
			"Paul",
			"Peter",
			"Philip",
			"Rachel",
			"Ralph",
			"Randy",
			"Raymond",
			"Rebecca",
			"Richard",
			"Robert",
			"Roger",
			"Ronald",
			"Rose",
			"Roy",
			"Russell",
			"Ruth",
			"Ryan",
			"Samantha",
			"Samuel",
			"Sandra",
			"Sara",
			"Sarah",
			"Scott",
			"Sean",
			"Sharon",
			"Shirley",
			"Sophia",
			"Stephanie",
			"Stephen",
			"Steven",
			"Susan",
			"Teresa",
			"Terry",
			"Theresa",
			"Thomas",
			"Timothy",
			"Tyler",
			"Victoria",
			"Vincent",
			"Virginia",
			"Walter",
			"Wayne",
			"William",
			"Willie",
			"Zachary",
	};

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
		
		ClientData clientData;
		synchronized (newClients) {
			clientData = newClients.get(clientInfo.getClientId());
			if (clientData == null) {
				clientData = new ClientData(clientInfo.getClientId(), geoIp);
				newClients.put(clientInfo.getClientId(), clientData);
			}
			
			clientData.setClientInfo(clientInfo);
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
		
		boolean denyMatched = false;
		for (DenyRule denyRule : this.context.getDenyRules()) {
			if ("country".equals(denyRule.getType())) {
				if (geoIp != null && geoIp.getCountryCode() != null
						&& geoIp.getCountryCode().equalsIgnoreCase(denyRule.getPattern())) {
					denyMatched = true;
				}
			}
			
			if ("name".equals(denyRule.getType())) {
				if (clientInfo.getClientName() != null
						&& clientInfo.getClientName().matches(denyRule.getPattern())) {
					denyMatched = true;
				}
			}
			
			if ("proxy".equals(denyRule.getType())) {
				if (geoIp != null && geoIp.isProxy()) {
					denyMatched = true;
				}
			}
			
			if (denyMatched) {
				LOGGER.info("Kicking client {} due to a matching rule {}/{}.", clientInfo.getClientId(), denyRule.getType(), denyRule.getPattern());
				this.context.kickClient(clientInfo.getClientId(), denyRule.getMessage());
				return;
			}
		}
		
		if (this.context.isForceNameChange() && NO_NAME_PLAYER.matcher(clientInfo.getClientName()).matches()) {
			int hash = clientInfo.getNetworkAddress().hashCode() & 0x7fffffff;

			String[] namesTable = (this.context.getPlayerNames() == null
					|| this.context.getPlayerNames().length == 0) ? NAMES : this.context.getPlayerNames();
			
			int index = hash % namesTable.length;
			final int loopDetection = index;
			
			boolean free;
			do {
				free = true;
				index++;
				if (index >= namesTable.length) {
					index = 0;
				}
				
				if (index == loopDetection) {
					LOGGER.error("Could not find a new name for the client {} IP {}.", clientInfo.getClientId(), clientInfo.getNetworkAddress());
					break;
				}
				
				for (ClientData otherClient : newClients.values()) {
					if (namesTable[index].equals(otherClient.getName())) {
						free = false;
						break;
					}
				}
			} while (!free);
			
			if (free) {
				LOGGER.info("Forcing the player {} to get a new name {}.", clientInfo.getClientId(), namesTable[index]);
				// nobody reads it this.context.notifyUser(clientInfo.getClientId(), "You will get a new nice name. Feel free to change it via !name or in multiplayer settings.");
				this.context.renameUser(clientInfo.getClientId(), namesTable[index]);
			}
		}
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

		String welcomeMessage = this.context.getWelcomeMessage(clientData.getCountryCode() == null ? "*" : clientData.getCountryCode())
					.replaceAll("[$][{]COUNTRY[}]", clientData.getCountry() == null ? "the Universe" : clientData.getCountry())
					.replaceAll("[$][{]CITY[}]", clientData.getCity() == null ? "the beautiful City" : clientData.getCity());
		
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
			clientData = newClients.remove((Integer)clientId);
			if (clientData != null) {
				clientData.left(this.context.getCurrentDate());
			}
		}
		
		// todo TODO optionally put the clientData in a separate collection, oldClients
		
		context.playerLeft(clientId);
		context.clientLeft(clientId);

		this.context.notifyAdmin(
			EventType.Client,
			":runner: ID " + clientId
				+ (clientData == null || clientData.getName() == null ? "" : (", name " + clientData.getName()))
				+ " left");
	}
	
	@Override
	public void clientError(int clientId, ErrorCode errorCode) {
		LOGGER.info("Client {} error {}.", clientId, errorCode);
		
		ClientData clientData;
		synchronized (newClients) {
			clientData = newClients.remove((Integer)clientId);
			if (clientData != null) {
				clientData.setErrorCode(errorCode);
				clientData.left(this.context.getCurrentDate());
			}
		}

		// todo TODO optionally put the clientData in a separate collection, oldClients
		
		context.playerLeft(clientId);
		context.clientLeft(clientId);

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
		
		context.clientUpdate(clientData);
		context.playerJoined(clientId, playAs);

		this.context.notifyAdmin(
			EventType.Client,
			":bust_in_silhouette: ID "
				+ clientId
				+ ", name " + clientName
				+ ", plays as " + getCompanyDescription(playAs));
		
		boolean denyMatched = false;
		for (DenyRule denyRule : this.context.getDenyRules()) {
			if ("name".equals(denyRule.getType())) {
				if (clientName != null
						&& clientName.matches(denyRule.getPattern())) {
					denyMatched = true;
				}
			}
			
			if (denyMatched) {
				LOGGER.info("Kicking client {} due to a matching rule {}/{}.", clientId, denyRule.getType(), denyRule.getPattern());
				this.context.kickClient(clientId, denyRule.getMessage());
				return;
			}
		}
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