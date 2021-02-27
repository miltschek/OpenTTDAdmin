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
package de.miltschek.openttdadmin.packets;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: document it
 */
public abstract class OttdPacket {
	private static final Logger LOGGER = LoggerFactory.getLogger(OttdPacket.class);
	
	public static final int MAX_MTU = 1460;
	
	protected byte[] internalBuffer;
	private int position;
	
	protected OttdPacket(byte[] buffer) {
		this.internalBuffer = buffer;
		resetCursor();
	}
	
	protected OttdPacket(byte[] buffer, int startPosition, int length) {
		this.internalBuffer = new byte[length];
		System.arraycopy(buffer, 0, internalBuffer, startPosition, length);
		resetCursor();
	}
	
	public static OttdPacket parsePacket(byte[] buffer) {
		try {
			NetworkPacketType type = NetworkPacketType.getEnum(buffer[2] & 0xff);
			
			if (type == null) {
				LOGGER.error("unknown packet type id {}", (buffer[2] & 0xff));
				return null;
			}
			
			switch (type) {
			case ADMIN_PACKET_SERVER_FULL:
				// ottd does not implement this packet
				return new ServerFull(buffer);
			case ADMIN_PACKET_SERVER_BANNED:
				// ottd does not implement this packet
				return new ServerBanned(buffer);
			case ADMIN_PACKET_SERVER_ERROR:
				return new ServerError(buffer);
			case ADMIN_PACKET_SERVER_PROTOCOL:
				return new ServerProtocol(buffer);
			case ADMIN_PACKET_SERVER_WELCOME:
				return new ServerWelcome(buffer);
			case ADMIN_PACKET_SERVER_NEWGAME:
				return new ServerNewGame(buffer);
			case ADMIN_PACKET_SERVER_SHUTDOWN:
				return new ServerShutdown(buffer);
		
			case ADMIN_PACKET_SERVER_DATE:
				return new ServerDate(buffer);
			case ADMIN_PACKET_SERVER_CLIENT_JOIN:
				return new ServerClientJoin(buffer);
			case ADMIN_PACKET_SERVER_CLIENT_INFO:
				return new ServerClientInfo(buffer);
			case ADMIN_PACKET_SERVER_CLIENT_UPDATE:
				return new ServerClientUpdate(buffer);
			case ADMIN_PACKET_SERVER_CLIENT_QUIT:
				return new ServerClientQuit(buffer);
			case ADMIN_PACKET_SERVER_CLIENT_ERROR:
				return new ServerClientError(buffer);
			case ADMIN_PACKET_SERVER_COMPANY_NEW:
				return new ServerCompanyNew(buffer);
			case ADMIN_PACKET_SERVER_COMPANY_INFO:
				return new ServerCompanyInfo(buffer);
			case ADMIN_PACKET_SERVER_COMPANY_UPDATE:
				return new ServerCompanyUpdate(buffer);
			case ADMIN_PACKET_SERVER_COMPANY_REMOVE:
				return new ServerCompanyRemove(buffer);
			case ADMIN_PACKET_SERVER_COMPANY_ECONOMY:
				return new ServerCompanyEconomy(buffer);
			case ADMIN_PACKET_SERVER_COMPANY_STATS:
				return new ServerCompanyStats(buffer);
			case ADMIN_PACKET_SERVER_CHAT:
				return new ServerChat(buffer);
			case ADMIN_PACKET_SERVER_RCON:
				return new ServerRcon(buffer);
			case ADMIN_PACKET_SERVER_CONSOLE:
				return new ServerConsole(buffer);
			case ADMIN_PACKET_SERVER_CMD_NAMES:
				return new ServerCmdNames(buffer);
			case ADMIN_PACKET_SERVER_CMD_LOGGING:
				return new ServerCmdLogging(buffer);
			case ADMIN_PACKET_SERVER_GAMESCRIPT:
				return new ServerGameScript(buffer);
			case ADMIN_PACKET_SERVER_RCON_END:
				return new ServerRconEnd(buffer);
			case ADMIN_PACKET_SERVER_PONG:
				return new ServerPong(buffer);
		
			case INVALID_ADMIN_PACKET:
				// not a real packet
				break;
				
				default:
					LOGGER.error("unsupported packet type {}", type);
					break;
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			LOGGER.error("invalid packet structure", ex);
		} catch (Exception ex) {
			LOGGER.error("failed parsing the packet", ex);
		}
		
		return null;
	}
	
	public byte[] getInternalBuffer() {
		return internalBuffer;
	}
	
	protected void resetCursor() {
		this.position = internalBuffer == null ? 0 : 3;
	}
	
	protected long readInt64() {
		return (0xffL & internalBuffer[position++])
				| ((0xffL & internalBuffer[position++]) << 8)
				| ((0xffL & internalBuffer[position++]) << 16)
				| ((0xffL & internalBuffer[position++]) << 24)
				| ((0xffL & internalBuffer[position++]) << 32)
				| ((0xffL & internalBuffer[position++]) << 40)
				| ((0xffL & internalBuffer[position++]) << 48)
				| ((0xffL & internalBuffer[position++]) << 56);
	}
	
	protected int readInt32() {
		return (0xff & internalBuffer[position++])
				| ((0xff & internalBuffer[position++]) << 8)
				| ((0xff & internalBuffer[position++]) << 16)
				| ((0xff & internalBuffer[position++]) << 24);
	}
	
	protected int readInt16() {
		return (0xff & internalBuffer[position++])
				| ((0xff & internalBuffer[position++]) << 8);
	}
	
	protected byte readByte() {
		return internalBuffer[position++];
	}
	
	protected boolean readBoolean() {
		return internalBuffer[position++] != 0;
	}
	
	protected String readString() {
		int endIndex = find(internalBuffer, position, (byte)0);
		String result = new String(internalBuffer, position, endIndex - position, StandardCharsets.UTF_8);
		this.position = endIndex + 1;
		return result;
	}
	
	private static int find(byte[] array, int startPosition, byte value) {
		for (int n = startPosition; n < array.length; n++) {
			if (array[n] == value) {
				return n;
			}
		}
		
		return -1;
	}
}
