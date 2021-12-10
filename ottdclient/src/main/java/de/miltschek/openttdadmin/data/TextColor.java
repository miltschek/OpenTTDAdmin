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
 * Colors of strings.
 */
public enum TextColor {
	/** TODO Probably follow colors as defined within the string value? */
	TC_FROMSTRING(0x00),
	/** Blue. */
	TC_BLUE(0x00),
	/** Silver. */
	TC_SILVER(0x01),
	/** Gold. */
	TC_GOLD(0x02),
	/** Red. */
	TC_RED(0x03),
	/** Purple. */
	TC_PURPLE(0x04),
	/** Light brown. */
	TC_LIGHT_BROWN(0x05),
	/** Orange. */
	TC_ORANGE(0x06),
	/** Green. */
	TC_GREEN(0x07),
	/** Yellow. */
	TC_YELLOW(0x08),
	/** Dark green. */
	TC_DARK_GREEN(0x09),
	/** Cream. */
	TC_CREAM(0x0A),
	/** Brown. */
	TC_BROWN(0x0B),
	/** White. */
	TC_WHITE(0x0C),
	/** Light blue. */
	TC_LIGHT_BLUE(0x0D),
	/** Grey. */
	TC_GREY(0x0E),
	/** Dark blue. */
	TC_DARK_BLUE(0x0F),
	/** Black. */
	TC_BLACK(0x10),
	/** Invalid color. */
	TC_INVALID(0xFF),
	
	/** Color value is already a real palette color index, not an index of a StringColour. */
	TC_IS_PALETTE_COLOUR(0x100),
	/** Do not add shading to this text color. */
	TC_NO_SHADE(0x200),
	/** Ignore color changes from strings. */
	TC_FORCED(0x400);

	private int value;
	
	private TextColor(int value) {
		this.value = value;
	}
	
	/** Value of the color. */
	public int getValue() {
		return value;
	}
	
	/**
	 * Gets an enum object out of its value.
	 * @param value value
	 * @return matching enum or null if not existing
	 */
	public static TextColor getEnum(int value) {
		switch (value & 0xFF) {
		case 0x00: return TC_BLUE;
		case 0x01: return TC_SILVER;
		case 0x02: return TC_GOLD;
		case 0x03: return TC_RED;
		case 0x04: return TC_PURPLE;
		case 0x05: return TC_LIGHT_BROWN;
		case 0x06: return TC_ORANGE;
		case 0x07: return TC_GREEN;
		case 0x08: return TC_YELLOW;
		case 0x09: return TC_DARK_GREEN;
		case 0x0A: return TC_CREAM;
		case 0x0B: return TC_BROWN;
		case 0x0C: return TC_WHITE;
		case 0x0D: return TC_LIGHT_BLUE;
		case 0x0E: return TC_GREY;
		case 0x0F: return TC_DARK_BLUE;
		case 0x10: return TC_BLACK;
		case 0xFF: return TC_INVALID;
		default:
			return null;
		}
	}
	
	/**
	 * Returns true if the color is a real palette color index.
	 * @param value color value to test
	 * @return true if the color is a real palette color index.
	 */
	public boolean isPaletteColor(int value) {
		return (value & TC_IS_PALETTE_COLOUR.value) != 0;
	}
	
	/**
	 * Returns true if the no shading should be added to this color.
	 * @param value color value to test
	 * @return true if the no shading should be added to this color.
	 */
	public boolean isNoShade(int value) {
		return (value & TC_NO_SHADE.value) != 0;
	}
	
	/**
	 * Returns true if color changes should be ignored.
	 * @param value color value to test
	 * @return true if color changes should be ignored.
	 */
	public boolean isForced(int value) {
		return (value & TC_FORCED.value) != 0;
	}
}
