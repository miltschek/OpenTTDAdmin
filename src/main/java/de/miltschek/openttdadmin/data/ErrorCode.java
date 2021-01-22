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
 * Error codes returned by the server.
 */
public enum ErrorCode {
	/** Unspecific error. */
	GeneralError,
	/** Synchronization lost. */
	Desync,
	/** Save game failed. */
	SavegameFailed,
	/** Connection lost. */
	ConnectionLost,
	/** Unexpected network packet received. */
	IllegalNetworkPacket,
	/** NewGRF mismatch. */
	NewGRFMismatch,
	/** Not authorized. */
	NotAuthorized,
	/** Not expected, e.g. invalid state. */
	NotExpected,
	/** Wrong revision. */
	WrongRevision,
	/** Name in use. */
	NameInUse,
	/** Wrong password. */
	WrongPassword,
	/** Company mismatch. */
	CompanyMismatch,
	/** Kicked out of the server. */
	Kicked,
	/** Cheater. */
	Cheater,
	/** Server not accepting more connections. */
	ServerFull,
	/** Too many commands. */
	TooManyCommands,
	/** Timeout while waiting for a password. */
	TimeoutPassword,
	/** Timeout computer. */
	TimeoutComputer,
	/** Timeout map. */
	TimeoutMap,
	/** Timeout while loading the game and joining. */
	TimeoutJoin,
	/** An error unknown to this implementation. */
	UnknownError
}
