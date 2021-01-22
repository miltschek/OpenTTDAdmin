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

import java.util.Map;

/**
 * Adapter for server event listeners.
 */
public abstract class ServerListenerAdapter {
	/**
	 * Called whenever the admin client connected to the server.
	 */
	public void connected() {}
	
	/**
	 * Called whenever the admin client loses a connection to the server.
	 */
	public void disconnected() {}
	
	/**
	 * Called whenever a server delivers self-information.
	 * @param serverInfo server information data
	 */
	public void serverInfoReceived(ServerInfo serverInfo) {}
	
	/**
	 * Called whenever a server delivers a new date.
	 * @param date the current in-game date
	 */
	public void newDate(Date date) {}
	
	/**
	 * Called whenever a new game is started.
	 */
	public void newGame() {}
	
	/**
	 * Called whenever a server delivers a list of supported commands.
	 * Please note the list may be delivered in fragments (multiple calls).
	 * @param commands a map of commands IDs and their names
	 */
	public void commandNamesReceived(Map<? extends Integer, ? extends String> commands) {}
	
	/**
	 * Called whenever the server reports being full and not accepting connections.
	 * Don't rely on this event as no implementation of this even could be found in the game's code.
	 */
	public void serverFull() {}
	
	/**
	 * Called whenever the server reports we have been banned.
	 * Don't rely on this event as no implementation of this even could be found in the game's code.
	 */
	public void serverBanned() {}
	
	/**
	 * Called whenever the server rejects the admin password.
	 */
	public void wrongPassword() {}
	
	/**
	 * Called whenever a new entry has been printed on the console.
	 * @param origin origin of the entry
	 * @param text the content of the entry
	 */
	public void console(String origin, String text) {}
	
	/**
	 * TODO: unknown when it is actually called
	 * @param color TODO: unknown
	 * @param result TODO: probably the r-command
	 */
	public void rcon(int color, String result) {}
	
	/**
	 * Called whenever an rcon-command has been executed (finished).
	 * @param command the command that has been executed
	 */
	public void rconFinished(String command) {}
	
	/**
	 * TODO: unknown what these events actually are
	 * @param clientId TODO: ID of the client that triggered the event?
	 * @param companyId TODO: ID of the company that triggered the event?
	 * @param commandId TODO: ID of the executed command?
	 * @param p1 TODO: parameter one?
	 * @param p2 TODO: paramter two?
	 * @param tile TODO: tile xy-address? 
	 * @param text TODO: associated message?
	 * @param frame TODO: no idea
	 */
	public void logging(int clientId, byte companyId, int commandId, int p1, int p2, int tile, String text, int frame) {}
	
	/**
	 * Called whenever a game script is TODO: no idea - executed? requested?
	 * Please note, game scripts longer than the maximum size of a packet (ca. 1400 Bytes) are silently ignored by the server.
	 * @param json TODO: the script in a JSON format?
	 */
	public void gameScript(String json) {}
	
	/**
	 * Called whenever the server responds to a PING request.
	 * @param value value copied from the original PING request
	 */
	public void pong(int value) {}
}
