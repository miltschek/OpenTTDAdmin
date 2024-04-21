/*
 *  MIT License
 *
 *  Copyright (c) 2024 miltschek
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
package de.miltschek.openttdadmin.packets;

/**
 * Types of TCP packets specific to the admin network.
 */
public enum NetworkPacketType {
	ADMIN_PACKET_ADMIN_JOIN(0),             ///< The admin announces and authenticates itself to the server.
	ADMIN_PACKET_ADMIN_QUIT(1),             ///< The admin tells the server that it is quitting.
	ADMIN_PACKET_ADMIN_UPDATE_FREQUENCY(2), ///< The admin tells the server the update frequency of a particular piece of information.
	ADMIN_PACKET_ADMIN_POLL(3),             ///< The admin explicitly polls for a piece of information.
	ADMIN_PACKET_ADMIN_CHAT(4),             ///< The admin sends a chat message to be distributed.
	ADMIN_PACKET_ADMIN_RCON(5),             ///< The admin sends a remote console command.
	ADMIN_PACKET_ADMIN_GAMESCRIPT(6),       ///< The admin sends a JSON string for the GameScript.
	ADMIN_PACKET_ADMIN_PING(7),             ///< The admin sends a ping to the server, expecting a ping-reply (PONG) packet.
	ADMIN_PACKET_ADMIN_EXTERNAL_CHAT(7),    ///< The admin sends a chat message from external source.

	ADMIN_PACKET_SERVER_FULL(100),            ///< The server tells the admin it cannot accept the admin.
	ADMIN_PACKET_SERVER_BANNED(101),          ///< The server tells the admin it is banned.
	ADMIN_PACKET_SERVER_ERROR(102),           ///< The server tells the admin an error has occurred.
	ADMIN_PACKET_SERVER_PROTOCOL(103),        ///< The server tells the admin its protocol version.
	ADMIN_PACKET_SERVER_WELCOME(104),         ///< The server welcomes the admin to a game.
	ADMIN_PACKET_SERVER_NEWGAME(105),         ///< The server tells the admin its going to start a new game.
	ADMIN_PACKET_SERVER_SHUTDOWN(106),        ///< The server tells the admin its shutting down.

	ADMIN_PACKET_SERVER_DATE(107),            ///< The server tells the admin what the current game date is.
	ADMIN_PACKET_SERVER_CLIENT_JOIN(108),     ///< The server tells the admin that a client has joined.
	ADMIN_PACKET_SERVER_CLIENT_INFO(109),     ///< The server gives the admin information about a client.
	ADMIN_PACKET_SERVER_CLIENT_UPDATE(110),   ///< The server gives the admin an information update on a client.
	ADMIN_PACKET_SERVER_CLIENT_QUIT(111),     ///< The server tells the admin that a client quit.
	ADMIN_PACKET_SERVER_CLIENT_ERROR(112),    ///< The server tells the admin that a client caused an error.
	ADMIN_PACKET_SERVER_COMPANY_NEW(113),     ///< The server tells the admin that a new company has started.
	ADMIN_PACKET_SERVER_COMPANY_INFO(114),    ///< The server gives the admin information about a company.
	ADMIN_PACKET_SERVER_COMPANY_UPDATE(115),  ///< The server gives the admin an information update on a company.
	ADMIN_PACKET_SERVER_COMPANY_REMOVE(116),  ///< The server tells the admin that a company was removed.
	ADMIN_PACKET_SERVER_COMPANY_ECONOMY(117), ///< The server gives the admin some economy related company information.
	ADMIN_PACKET_SERVER_COMPANY_STATS(118),   ///< The server gives the admin some statistics about a company.
	ADMIN_PACKET_SERVER_CHAT(119),            ///< The server received a chat message and relays it.
	ADMIN_PACKET_SERVER_RCON(120),            ///< The server's reply to a remove console command.
	ADMIN_PACKET_SERVER_CONSOLE(121),         ///< The server gives the admin the data that got printed to its console.
	ADMIN_PACKET_SERVER_CMD_NAMES(122),       ///< The server sends out the names of the DoCommands to the admins.
	ADMIN_PACKET_SERVER_CMD_LOGGING(123),     ///< The server gives the admin copies of incoming command packets.
	ADMIN_PACKET_SERVER_GAMESCRIPT(124),      ///< The server gives the admin information from the GameScript in JSON.
	ADMIN_PACKET_SERVER_RCON_END(125),        ///< The server indicates that the remote console command has completed.
	ADMIN_PACKET_SERVER_PONG(126),            ///< The server replies to a ping request from the admin.

	INVALID_ADMIN_PACKET(0xFF);         ///< An invalid marker for admin packets.
	
	private int value;
	
	private NetworkPacketType(int value) {
		this.value = value;
	}
	
	/**
	 * Get a value associated with the network packet type.
	 * @return a value associated with the network packet type
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Get a network packet type associated with the given value.
	 * @param value network-level value denoting a network packet typen
	 * @return a network packet type associated with the given value
	 */
	public static NetworkPacketType getEnum(int value) {
		switch (value) {

		case 0: return ADMIN_PACKET_ADMIN_JOIN;
		case 1: return ADMIN_PACKET_ADMIN_QUIT;
		case 2: return ADMIN_PACKET_ADMIN_UPDATE_FREQUENCY;
		case 3: return ADMIN_PACKET_ADMIN_POLL;
		case 4: return ADMIN_PACKET_ADMIN_CHAT;
		case 5: return ADMIN_PACKET_ADMIN_RCON;
		case 6: return ADMIN_PACKET_ADMIN_GAMESCRIPT;
		case 7: return ADMIN_PACKET_ADMIN_PING;
		
		case 100: return ADMIN_PACKET_SERVER_FULL;
		case 101: return ADMIN_PACKET_SERVER_BANNED;
		case 102: return ADMIN_PACKET_SERVER_ERROR;
		case 103: return ADMIN_PACKET_SERVER_PROTOCOL;
		case 104: return ADMIN_PACKET_SERVER_WELCOME;
		case 105: return ADMIN_PACKET_SERVER_NEWGAME;
		case 106: return ADMIN_PACKET_SERVER_SHUTDOWN;
		
		case 107: return ADMIN_PACKET_SERVER_DATE;
		case 108: return ADMIN_PACKET_SERVER_CLIENT_JOIN;
		case 109: return ADMIN_PACKET_SERVER_CLIENT_INFO;
		case 110: return ADMIN_PACKET_SERVER_CLIENT_UPDATE;
		case 111: return ADMIN_PACKET_SERVER_CLIENT_QUIT;
		case 112: return ADMIN_PACKET_SERVER_CLIENT_ERROR;
		case 113: return ADMIN_PACKET_SERVER_COMPANY_NEW;
		case 114: return ADMIN_PACKET_SERVER_COMPANY_INFO;
		case 115: return ADMIN_PACKET_SERVER_COMPANY_UPDATE;
		case 116: return ADMIN_PACKET_SERVER_COMPANY_REMOVE;
		case 117: return ADMIN_PACKET_SERVER_COMPANY_ECONOMY;
		case 118: return ADMIN_PACKET_SERVER_COMPANY_STATS;
		case 119: return ADMIN_PACKET_SERVER_CHAT;
		case 120: return ADMIN_PACKET_SERVER_RCON;
		case 121: return ADMIN_PACKET_SERVER_CONSOLE;
		case 122: return ADMIN_PACKET_SERVER_CMD_NAMES;
		case 123: return ADMIN_PACKET_SERVER_CMD_LOGGING;
		case 124: return ADMIN_PACKET_SERVER_GAMESCRIPT;
		case 125: return ADMIN_PACKET_SERVER_RCON_END;
		case 126: return ADMIN_PACKET_SERVER_PONG;
		
		case 0xff: return INVALID_ADMIN_PACKET;

		default:
			return null;
		}
	}
}
