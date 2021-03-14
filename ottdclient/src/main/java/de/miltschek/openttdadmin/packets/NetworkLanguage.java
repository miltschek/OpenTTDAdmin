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

/**
 * Mapping of network languages to their IDs and vice-versa.
 */
public enum NetworkLanguage {
	NETLANG_ANY((byte)0),
	NETLANG_ENGLISH((byte)1),
	NETLANG_GERMAN((byte)2),
	NETLANG_FRENCH((byte)3),
	NETLANG_BRAZILIAN((byte)4),
	NETLANG_BULGARIAN((byte)5),
	NETLANG_CHINESE((byte)6),
	NETLANG_CZECH((byte)7),
	NETLANG_DANISH((byte)8),
	NETLANG_DUTCH((byte)9),
	NETLANG_ESPERANTO((byte)10),
	NETLANG_FINNISH((byte)11),
	NETLANG_HUNGARIAN((byte)12),
	NETLANG_ICELANDIC((byte)13),
	NETLANG_ITALIAN((byte)14),
	NETLANG_JAPANESE((byte)15),
	NETLANG_KOREAN((byte)16),
	NETLANG_LITHUANIAN((byte)17),
	NETLANG_NORWEGIAN((byte)18),
	NETLANG_POLISH((byte)19),
	NETLANG_PORTUGUESE((byte)20),
	NETLANG_ROMANIAN((byte)21),
	NETLANG_RUSSIAN((byte)22),
	NETLANG_SLOVAK((byte)23),
	NETLANG_SLOVENIAN((byte)24),
	NETLANG_SPANISH((byte)25),
	NETLANG_SWEDISH((byte)26),
	NETLANG_TURKISH((byte)27),
	NETLANG_UKRAINIAN((byte)28),
	NETLANG_AFRIKAANS((byte)29),
	NETLANG_CROATIAN((byte)30),
	NETLANG_CATALAN((byte)31),
	NETLANG_ESTONIAN((byte)32),
	NETLANG_GALICIAN((byte)33),
	NETLANG_GREEK((byte)34),
	NETLANG_LATVIAN((byte)35);
	
	private final byte value;
	
	private NetworkLanguage(byte id) {
		this.value = id;
	}

	/**
	 * Gets the ID of the language.
	 * @return the ID of the language.
	 */
	public byte getValue() {
		return value;
	}
	
	/**
	 * Gets an enum for the given ID.
	 * @param an ID to look up.
	 * @return value the enum for the given ID or null if not defined.
	 */
	public static NetworkLanguage getEnum(byte value) {
		switch (value) {
		case 0: return NETLANG_ANY;
		case 1: return NETLANG_ENGLISH;
		case 2: return NETLANG_GERMAN;
		case 3: return NETLANG_FRENCH;
		case 4: return NETLANG_BRAZILIAN;
		case 5: return NETLANG_BULGARIAN;
		case 6: return NETLANG_CHINESE;
		case 7: return NETLANG_CZECH;
		case 8: return NETLANG_DANISH;
		case 9: return NETLANG_DUTCH;
		case 10: return NETLANG_ESPERANTO;
		case 11: return NETLANG_FINNISH;
		case 12: return NETLANG_HUNGARIAN;
		case 13: return NETLANG_ICELANDIC;
		case 14: return NETLANG_ITALIAN;
		case 15: return NETLANG_JAPANESE;
		case 16: return NETLANG_KOREAN;
		case 17: return NETLANG_LITHUANIAN;
		case 18: return NETLANG_NORWEGIAN;
		case 19: return NETLANG_POLISH;
		case 20: return NETLANG_PORTUGUESE;
		case 21: return NETLANG_ROMANIAN;
		case 22: return NETLANG_RUSSIAN;
		case 23: return NETLANG_SLOVAK;
		case 24: return NETLANG_SLOVENIAN;
		case 25: return NETLANG_SPANISH;
		case 26: return NETLANG_SWEDISH;
		case 27: return NETLANG_TURKISH;
		case 28: return NETLANG_UKRAINIAN;
		case 29: return NETLANG_AFRIKAANS;
		case 30: return NETLANG_CROATIAN;
		case 31: return NETLANG_CATALAN;
		case 32: return NETLANG_ESTONIAN;
		case 33: return NETLANG_GALICIAN;
		case 34: return NETLANG_GREEK;
		case 35: return NETLANG_LATVIAN;
		default:
			return null;
		}
	}
}
