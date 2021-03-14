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
 * Mapping of network currency codes to their IDs and vice-versa.
 */
public enum NetworkCurrency {
	CURRENCY_GBP(0),       ///< British Pound
	CURRENCY_USD(1),       ///< US Dollar
	CURRENCY_EUR(2),       ///< Euro
	CURRENCY_JPY(3),       ///< Japanese Yen
	CURRENCY_ATS(4),       ///< Austrian Schilling
	CURRENCY_BEF(5),       ///< Belgian Franc
	CURRENCY_CHF(6),       ///< Swiss Franc
	CURRENCY_CZK(7),       ///< Czech Koruna
	CURRENCY_DEM(8),       ///< Deutsche Mark
	CURRENCY_DKK(9),       ///< Danish Krona
	CURRENCY_ESP(10),       ///< Spanish Peseta
	CURRENCY_FIM(11),       ///< Finish Markka
	CURRENCY_FRF(12),       ///< French Franc
	CURRENCY_GRD(13),       ///< Greek Drachma
	CURRENCY_HUF(14),       ///< Hungarian Forint
	CURRENCY_ISK(15),       ///< Icelandic Krona
	CURRENCY_ITL(16),       ///< Italian Lira
	CURRENCY_NLG(17),       ///< Dutch Gulden
	CURRENCY_NOK(18),       ///< Norwegian Krone
	CURRENCY_PLN(19),       ///< Polish Zloty
	CURRENCY_RON(20),       ///< Romenian Leu
	CURRENCY_RUR(21),       ///< Russian Rouble
	CURRENCY_SIT(22),       ///< Slovenian Tolar
	CURRENCY_SEK(23),       ///< Swedish Krona
	CURRENCY_YTL(24),       ///< Turkish Lira
	CURRENCY_SKK(25),       ///< Slovak Kornuna
	CURRENCY_BRL(26),       ///< Brazilian Real
	CURRENCY_EEK(27),       ///< Estonian Krooni
	CURRENCY_LTL(28),       ///< Lithuanian Litas
	CURRENCY_KRW(29),       ///< South Korean Won
	CURRENCY_ZAR(30),       ///< South African Rand
	CURRENCY_CUSTOM(31),    ///< Custom currency
	CURRENCY_GEL(32),       ///< Georgian Lari
	CURRENCY_IRR(33),       ///< Iranian Rial
	CURRENCY_RUB(34),       ///< New Russian Ruble
	CURRENCY_MXN(35),       ///< Mexican Peso
	CURRENCY_NTD(36),       ///< New Taiwan Dollar
	CURRENCY_CNY(37),       ///< Chinese Renminbi
	CURRENCY_HKD(38);       ///< Hong Kong Dollar

	private final int value;
	
	private NetworkCurrency(int id) {
		this.value = id;
	}

	/**
	 * Gets the ID of the currency.
	 * @return the ID of the currency.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Gets an enum for the given ID.
	 * @param an ID to look up.
	 * @return value the enum for the given ID or null if not defined.
	 */
	public static NetworkCurrency getEnum(int value) {
		switch (value) {
		case 0: return CURRENCY_GBP;       ///< British Pound
		case 1: return CURRENCY_USD;       ///< US Dollar
		case 2: return CURRENCY_EUR;       ///< Euro
		case 3: return CURRENCY_JPY;       ///< Japanese Yen
		case 4: return CURRENCY_ATS;       ///< Austrian Schilling
		case 5: return CURRENCY_BEF;       ///< Belgian Franc
		case 6: return CURRENCY_CHF;       ///< Swiss Franc
		case 7: return CURRENCY_CZK;       ///< Czech Koruna
		case 8: return CURRENCY_DEM;       ///< Deutsche Mark
		case 9: return CURRENCY_DKK;       ///< Danish Krona
		case 10: return CURRENCY_ESP;       ///< Spanish Peseta
		case 11: return CURRENCY_FIM;       ///< Finish Markka
		case 12: return CURRENCY_FRF;       ///< French Franc
		case 13: return CURRENCY_GRD;       ///< Greek Drachma
		case 14: return CURRENCY_HUF;       ///< Hungarian Forint
		case 15: return CURRENCY_ISK;       ///< Icelandic Krona
		case 16: return CURRENCY_ITL;       ///< Italian Lira
		case 17: return CURRENCY_NLG;       ///< Dutch Gulden
		case 18: return CURRENCY_NOK;       ///< Norwegian Krone
		case 19: return CURRENCY_PLN;       ///< Polish Zloty
		case 20: return CURRENCY_RON;       ///< Romenian Leu
		case 21: return CURRENCY_RUR;       ///< Russian Rouble
		case 22: return CURRENCY_SIT;       ///< Slovenian Tolar
		case 23: return CURRENCY_SEK;       ///< Swedish Krona
		case 24: return CURRENCY_YTL;       ///< Turkish Lira
		case 25: return CURRENCY_SKK;       ///< Slovak Kornuna
		case 26: return CURRENCY_BRL;       ///< Brazilian Real
		case 27: return CURRENCY_EEK;       ///< Estonian Krooni
		case 28: return CURRENCY_LTL;       ///< Lithuanian Litas
		case 29: return CURRENCY_KRW;       ///< South Korean Won
		case 30: return CURRENCY_ZAR;       ///< South African Rand
		case 31: return CURRENCY_CUSTOM;    ///< Custom currency
		case 32: return CURRENCY_GEL;       ///< Georgian Lari
		case 33: return CURRENCY_IRR;       ///< Iranian Rial
		case 34: return CURRENCY_RUB;       ///< New Russian Ruble
		case 35: return CURRENCY_MXN;       ///< Mexican Peso
		case 36: return CURRENCY_NTD;       ///< New Taiwan Dollar
		case 37: return CURRENCY_CNY;       ///< Chinese Renminbi
		case 38: return CURRENCY_HKD;       ///< Hong Kong Dollar
		default:
			return null;
		}
	}
}
