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

import de.miltschek.openttdadmin.packets.NetworkCurrency;

/**
 * Currencies.
 */
public enum Currency {
	/** Currency that can't be identified accordingly to the current IDs mapping. */
	Unknown,
	/** Custom currency. */
	Customcurrency,

	/** British Pound. */
	BritishPound,
	/** US Dollar. */
	USDollar,
	/** Euro. */
	Euro,
	/** Japanese Yen. */
	JapaneseYen,
	/** Austrian Schilling. */
	AustrianSchilling,
	/** Belgian Franc. */
	BelgianFranc,
	/** Swiss Franc. */
	SwissFranc,
	/** Czech Koruna. */
	CzechKoruna,
	/** Deutsche Mark. */
	DeutscheMark,
	/** Danish Krona. */
	DanishKrona,
	/** Spanish Peseta. */
	SpanishPeseta,
	/** Finish Markka. */
	FinishMarkka,
	/** French Franc. */
	FrenchFranc,
	/** Greek Drachma. */
	GreekDrachma,
	/** Hungarian Forint. */
	HungarianForint,
	/** Icelandic Krona. */
	IcelandicKrona,
	/** Italian Lira. */
	ItalianLira,
	/** Dutch Gulden. */
	DutchGulden,
	/** Norwegian Krone. */
	NorwegianKrone,
	/** Polish Zloty. */
	PolishZloty,
	/** Romenian Leu. */
	RomenianLeu,
	/** Russian Rouble. */
	RussianRouble,
	/** Slovenian Tolar. */
	SlovenianTolar,
	/** Swedish Krona. */
	SwedishKrona,
	/** Turkish Lira. */
	TurkishLira,
	/** Slovak Koruna. */
	SlovakKoruna,
	/** Brazilian Real. */
	BrazilianReal,
	/** Estonian Krooni. */
	EstonianKrooni,
	/** Lithuanian Litas. */
	LithuanianLitas,
	/** South Korean Won. */
	SouthKoreanWon,
	/** South African Rand. */
	SouthAfricanRand,
	/** Georgian Lari. */
	GeorgianLari,
	/** Iranian Rial. */
	IranianRial,
	/** New Russian Ruble. */
	NewRussianRuble,
	/** Mexican Peso. */
	MexicanPeso,
	/** New Taiwan Dollar. */
	NewTaiwanDollar,
	/** Chinese Renminbi. */
	ChineseRenminbi,
	/** Hong Kong Dollar. */
	HongKongDollar;
	
	/**
	 * Converts network-specific currency type to an internal one.
	 * @param currency network-specific currency type.
	 * @return internal currency type.
	 */
	public static Currency get(NetworkCurrency currency) {
		switch (currency) {
		case CURRENCY_GBP: return BritishPound;
		case CURRENCY_USD: return USDollar;
		case CURRENCY_EUR: return Euro;
		case CURRENCY_JPY: return JapaneseYen;
		case CURRENCY_ATS: return AustrianSchilling;
		case CURRENCY_BEF: return BelgianFranc;
		case CURRENCY_CHF: return SwissFranc;
		case CURRENCY_CZK: return CzechKoruna;
		case CURRENCY_DEM: return DeutscheMark;
		case CURRENCY_DKK: return DanishKrona;
		case CURRENCY_ESP: return SpanishPeseta;
		case CURRENCY_FIM: return FinishMarkka;
		case CURRENCY_FRF: return FrenchFranc;
		case CURRENCY_GRD: return GreekDrachma;
		case CURRENCY_HUF: return HungarianForint;
		case CURRENCY_ISK: return IcelandicKrona;
		case CURRENCY_ITL: return ItalianLira;
		case CURRENCY_NLG: return DutchGulden;
		case CURRENCY_NOK: return NorwegianKrone;
		case CURRENCY_PLN: return PolishZloty;
		case CURRENCY_RON: return RomenianLeu;
		case CURRENCY_RUR: return RussianRouble;
		case CURRENCY_SIT: return SlovenianTolar;
		case CURRENCY_SEK: return SwedishKrona;
		case CURRENCY_YTL: return TurkishLira;
		case CURRENCY_SKK: return SlovakKoruna;
		case CURRENCY_BRL: return BrazilianReal;
		case CURRENCY_EEK: return EstonianKrooni;
		case CURRENCY_LTL: return LithuanianLitas;
		case CURRENCY_KRW: return SouthKoreanWon;
		case CURRENCY_ZAR: return SouthAfricanRand;
		case CURRENCY_CUSTOM: return Customcurrency;
		case CURRENCY_GEL: return GeorgianLari;
		case CURRENCY_IRR: return IranianRial;
		case CURRENCY_RUB: return NewRussianRuble;
		case CURRENCY_MXN: return MexicanPeso;
		case CURRENCY_NTD: return NewTaiwanDollar;
		case CURRENCY_CNY: return ChineseRenminbi;
		case CURRENCY_HKD: return HongKongDollar;
		
		default:
			return Unknown;
		}
	}
}
