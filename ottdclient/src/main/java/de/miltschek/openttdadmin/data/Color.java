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
 * Company colors.
 */
public enum Color {
	/** Dark blue. */
	COLOUR_DARK_BLUE(0),
	/** Pale green. */
	COLOUR_PALE_GREEN(1),
	/** Pink. */
	COLOUR_PINK(2),
	/** Yellow. */
	COLOUR_YELLOW(3),
	/** Red. */
	COLOUR_RED(4),
	/** Light blue. */
	COLOUR_LIGHT_BLUE(5),
	/** Green. */
	COLOUR_GREEN(6),
	/** Dark green. */
	COLOUR_DARK_GREEN(7),
	/** Blue. */
	COLOUR_BLUE(8),
	/** Cream. */
	COLOUR_CREAM(9),
	/** Mauve. What's that? Wiki: pale purple. */
	COLOUR_MAUVE(10),
	/** Purple. */
	COLOUR_PURPLE(11),
	/** Orange. */
	COLOUR_ORANGE(12),
	/** Brown. */
	COLOUR_BROWN(13),
	/** Grey. Some write gray. */
	COLOUR_GREY(14),
	/** White. */
	COLOUR_WHITE(15);

	private int value;
	
	private Color(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	/**
	 * Gets an enum object out of its value.
	 * @param value value
	 * @return matching enum or null if not existing
	 */
	public static Color getEnum(int value) {
		switch (value) {
		case 0: return COLOUR_DARK_BLUE;
		case 1: return COLOUR_PALE_GREEN;
		case 2: return COLOUR_PINK;
		case 3: return COLOUR_YELLOW;
		case 4: return COLOUR_RED;
		case 5: return COLOUR_LIGHT_BLUE;
		case 6: return COLOUR_GREEN;
		case 7: return COLOUR_DARK_GREEN;
		case 8: return COLOUR_BLUE;
		case 9: return COLOUR_CREAM;
		case 10: return COLOUR_MAUVE;
		case 11: return COLOUR_PURPLE;
		case 12: return COLOUR_ORANGE;
		case 13: return COLOUR_BROWN;
		case 14: return COLOUR_GREY;
		case 15: return COLOUR_WHITE;
		default:
			return null;
		}
	}}
