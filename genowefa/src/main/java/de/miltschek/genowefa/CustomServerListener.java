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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.openttdadmin.data.Date;
import de.miltschek.openttdadmin.data.ServerInfo;
import de.miltschek.openttdadmin.data.ServerListenerAdapter;

/**
 * Handler of server-specific events.
 */
public class CustomServerListener extends ServerListenerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomServerListener.class);
	
	private final Context context;

	/**
	 * Creates the handler.
	 * @param context application's context
	 */
	public CustomServerListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void newGame() {
		LOGGER.info("New game started.");
		
		this.context.notifyAdmin(":checkered_flag: new game");
	}
	
	@Override
	public void serverInfoReceived(ServerInfo serverInfo) {
		LOGGER.info("Server name {} revision {} dedicated {} map {} seed {} landscape {} start-year {} x {} y {}.",
				serverInfo.getServerName(),
				serverInfo.getNetworkRevision(),
				serverInfo.isServerDedicated(),
				serverInfo.getMapName(),
				serverInfo.getGenerationSeed(),
				serverInfo.getLandscape(),
				serverInfo.getStartingYear(),
				serverInfo.getMapSizeX(),
				serverInfo.getMapSizeY());
		
		this.context.notifyAdmin(":star: Connected to " + serverInfo.getServerName());
		
		System.out.println(" - server name = " + serverInfo.getServerName());
		System.out.println(" - network revision = " + serverInfo.getNetworkRevision());
		System.out.println(" - dedicated = " + serverInfo.isServerDedicated());
		System.out.println(" - map name = " + serverInfo.getMapName());
		System.out.println(" - generation seed = " + serverInfo.getGenerationSeed());
		System.out.println(" - landscape = " + serverInfo.getLandscape());
		System.out.println(" - starting year = " + serverInfo.getStartingYear());
		System.out.println(" - map size x = " + serverInfo.getMapSizeX());
		System.out.println(" - map size y = " + serverInfo.getMapSizeY());
	}
	
	@Override
	public void rcon(int color, String result) {
		LOGGER.debug("RCon({}): {}.", color, result);
		
		this.context.notifyAdmin(":computer: " + result);
	}
	
	@Override
	public void newDate(Date date) {
		LOGGER.debug("Date {}.", date);
		
		this.context.notifyAdmin(":computer: Game date " + date);
	}
	
	@Override
	public void logging(int clientId, byte companyId, int commandId, int p1, int p2, int tile, String text, int frame) {
		LOGGER.debug("CMD client {} company {}, cmd {}, p1 {}, p2 {}, tile {}, text {}, frame {}", clientId, companyId, commandId, p1, p2, tile, text, frame);
	}
}