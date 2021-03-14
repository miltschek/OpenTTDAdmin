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

import de.miltschek.openttdadmin.packets.NetworkLanguage;

/**
 * Languages.
 */
public enum Language {
	/** Language that can't be identified accordingly to the current IDs mapping. */
	Unknown,
	/** Any language = all languages are welcome. */
	Any,
	/** English. */
	English,
	/** German. */
	German,
	/** French. */
	French,
	/** Brazilian. */
	Brazilian,
	/** Bulgarian. */
	Bulgarian,
	/** Chinese. */
	Chinese,
	/** Czech. */
	Czech,
	/** Danish. */
	Danish,
	/** Dutch. */
	Dutch,
	/** Esperanto. */
	Esperanto,
	/** Finnish. */
	Finnish,
	/** Hungarian. */
	Hungarian,
	/** Icelandic. */
	Icelandic,
	/** Italian. */
	Italian,
	/** Japanese. */
	Japanese,
	/** Korean. */
	Korean,
	/** Lithuanian. */
	Lithuanian,
	/** Norwegian. */
	Norwegian,
	/** Polish. */
	Polish,
	/** Portuguese. */
	Portuguese,
	/** Romanian. */
	Romanian,
	/** Russian. */
	Russian,
	/** Slovak. */
	Slovak,
	/** Slovenian. */
	Slovenian,
	/** Spanish. */
	Spanish,
	/** Swedish. */
	Swedish,
	/** Turkish. */
	Turkish,
	/** Ukrainian. */
	Ukrainian,
	/** Afrikaans. */
	Afrikaans,
	/** Croatian. */
	Croatian,
	/** Catalan. */
	Catalan,
	/** Estonian. */
	Estonian,
	/** Galician. */
	Galician,
	/** Greek. */
	Greek,
	/** Latvian. */
	Latvian;
	
    /**
     * Converts the network-specific language ID to an internal type.
     * @param langauge network-specific language ID
     * @return internal language type
     */
    public static Language get(NetworkLanguage langauge) {
    	switch (langauge) {
    	case NETLANG_ANY: return Language.Any;
    	case NETLANG_ENGLISH: return Language.English;
    	case NETLANG_GERMAN: return Language.German;
    	case NETLANG_FRENCH: return Language.French;
    	case NETLANG_BRAZILIAN: return Language.Brazilian;
    	case NETLANG_BULGARIAN: return Language.Bulgarian;
    	case NETLANG_CHINESE: return Language.Chinese;
    	case NETLANG_CZECH: return Language.Czech;
    	case NETLANG_DANISH: return Language.Danish;
    	case NETLANG_DUTCH: return Language.Dutch;
    	case NETLANG_ESPERANTO: return Language.Esperanto;
    	case NETLANG_FINNISH: return Language.Finnish;
    	case NETLANG_HUNGARIAN: return Language.Hungarian;
    	case NETLANG_ICELANDIC: return Language.Icelandic;
    	case NETLANG_ITALIAN: return Language.Italian;
    	case NETLANG_JAPANESE: return Language.Japanese;
    	case NETLANG_KOREAN: return Language.Korean;
    	case NETLANG_LITHUANIAN: return Language.Lithuanian;
    	case NETLANG_NORWEGIAN: return Language.Norwegian;
    	case NETLANG_POLISH: return Language.Polish;
    	case NETLANG_PORTUGUESE: return Language.Portuguese;
    	case NETLANG_ROMANIAN: return Language.Romanian;
    	case NETLANG_RUSSIAN: return Language.Russian;
    	case NETLANG_SLOVAK: return Language.Slovak;
    	case NETLANG_SLOVENIAN: return Language.Slovenian;
    	case NETLANG_SPANISH: return Language.Spanish;
    	case NETLANG_SWEDISH: return Language.Swedish;
    	case NETLANG_TURKISH: return Language.Turkish;
    	case NETLANG_UKRAINIAN: return Language.Ukrainian;
    	case NETLANG_AFRIKAANS: return Language.Afrikaans;
    	case NETLANG_CROATIAN: return Language.Croatian;
    	case NETLANG_CATALAN: return Language.Catalan;
    	case NETLANG_ESTONIAN: return Language.Estonian;
    	case NETLANG_GALICIAN: return Language.Galician;
    	case NETLANG_GREEK: return Language.Greek;
    	case NETLANG_LATVIAN: return Language.Latvian;
    	
    	default:
    		return Language.Unknown;
    	}
    }
}
