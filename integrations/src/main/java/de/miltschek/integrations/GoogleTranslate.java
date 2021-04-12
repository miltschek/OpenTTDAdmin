package de.miltschek.integrations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

/**
 * Provides natural language translation services of Google Cloud.
 */
public class GoogleTranslate {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleTranslate.class);
	
	private static final String[] LANGUAGES = new String[] {
			"af", // Afrikaans
			"sq", // Albanian
			"am", // Amharic
			"ar", // Arabic
			"hy", // Armenian
			"az", // Azerbaijani
			"eu", // Basque
			"be", // Belarusian
			"bn", // Bengali
			"bs", // Bosnian
			"bg", // Bulgarian
			"ca", // Catalan
			"ceb", // Cebuano (ISO-639-2)
			"zh-CN", // Chinese (Simplified) (BCP-47)
			"zh", // Chinese (Simplified)
			"zh-TW", // Chinese (Traditional) (BCP-47)
			"co", // Corsican
			"hr", // Croatian
			"cs", // Czech
			"da", // Danish
			"nl", // Dutch
			"en", // English
			"eo", // Esperanto
			"et", // Estonian
			"fi", // Finnish
			"fr", // French
			"fy", // Frisian
			"gl", // Galician
			"ka", // Georgian
			"de", // German
			"el", // Greek
			"gu", // Gujarati
			"ht", // Haitian Creole
			"ha", // Hausa
			"haw", // Hawaiian (ISO-639-2)
			"he", // Hebrew
			"iw", // Hebrew
			"hi", // Hindi
			"hmn", // Hmong (ISO-639-2)
			"hu", // Hungarian
			"is", // Icelandic
			"ig", // Igbo
			"id", // Indonesian
			"ga", // Irish
			"it", // Italian
			"ja", // Japanese
			"jv", // Javanese
			"kn", // Kannada
			"kk", // Kazakh
			"km", // Khmer
			"rw", // Kinyarwanda
			"ko", // Korean
			"ku", // Kurdish
			"ky", // Kyrgyz
			"lo", // Lao
			"la", // Latin
			"lv", // Latvian
			"lt", // Lithuanian
			"lb", // Luxembourgish
			"mk", // Macedonian
			"mg", // Malagasy
			"ms", // Malay
			"ml", // Malayalam
			"mt", // Maltese
			"mi", // Maori
			"mr", // Marathi
			"mn", // Mongolian
			"my", // Myanmar (Burmese)
			"ne", // Nepali
			"no", // Norwegian
			"ny", // Nyanja (Chichewa)
			"or", // Odia (Oriya)
			"ps", // Pashto
			"fa", // Persian
			"pl", // Polish
			"pt", // Portuguese (Portugal, Brazil)
			"pa", // Punjabi
			"ro", // Romanian
			"ru", // Russian
			"sm", // Samoan
			"gd", // Scots Gaelic
			"sr", // Serbian
			"st", // Sesotho
			"sn", // Shona
			"sd", // Sindhi
			"si", // Sinhala (Sinhalese)
			"sk", // Slovak
			"sl", // Slovenian
			"so", // Somali
			"es", // Spanish
			"su", // Sundanese
			"sw", // Swahili
			"sv", // Swedish
			"tl", // Tagalog (Filipino)
			"tg", // Tajik
			"ta", // Tamil
			"tt", // Tatar
			"te", // Telugu
			"th", // Thai
			"tr", // Turkish
			"tk", // Turkmen
			"uk", // Ukrainian
			"ur", // Urdu
			"ug", // Uyghur
			"uz", // Uzbek
			"vi", // Vietnamese
			"cy", // Welsh
			"xh", // Xhosa
			"yi", // Yiddish
			"yo", // Yoruba
			"zu" // Zulu
	};
	
	/**
	 * Checks if the given language code is recognized by Google.
	 * Accordingly to the documentation on https://cloud.google.com/translate/docs/languages.
	 * @param ISO-639-1, sometimes ISO-639-2 and sometimes BCP-47 code
	 * @return true if known to be recognized, false otherwise
	 */
	public static boolean checkLanguageCode(String code) {
		String codeLowercase = code.toLowerCase();
		for (int n = 0; n < LANGUAGES.length; n++) {
			if (LANGUAGES[n].equals(codeLowercase)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Contains results of a translation.
	 */
	public static class Result {
		private boolean success;
		private String errorMessage;
		private String sourceLanguage;
		private String targetLanguage;
		private String translatedText;
		
		private Result(String errorMessage) {
			this.success = false;
			this.errorMessage = errorMessage;
		}
		
		private Result(String sourceLanguage, String targetLanguage, String translatedText) {
			this.success = true;
			this.sourceLanguage = sourceLanguage;
			this.targetLanguage = targetLanguage;
			this.translatedText = translatedText;
		}

		/**
		 * Returns a value denoting whether the translation was successful.
		 * For failed translations, the reason may be available in {@link #getErrorMessage()}
		 * @return true if the translation was successful, false otherwise
		 */
		public boolean isSuccess() {
			return success;
		}
		
		/**
		 * Returns an error message if the translation has been failed.
		 * @return Error message for failed translations.
		 */
		public String getErrorMessage() {
			return errorMessage;
		}
		
		/**
		 * Returns an identifier of the source language if the translation has been successful.
		 * @return An identifier of the source language if the translation has been successful.
		 */
		public String getSourceLanguage() {
			return sourceLanguage;
		}
		
		/**
		 * Returns an identifier of the target language if the translation has been successful.
		 * @return An identifier of the target language if the translation has been successful.
		 */
		public String getTargetLanguage() {
			return targetLanguage;
		}
		
		/**
		 * Returns the translated text in case the translation has been successful.
		 * @return The translated text in case the translation has been successful.
		 */
		public String getTranslatedText() {
			return translatedText;
		}
	}
	
	/**
	 * Detects the source language automatically and tries to translate it to English.
	 * @param sourceText source text to be translated
	 * @return translation result
	 */
	public Result translateToEnglish(String sourceText) {
		return translate(sourceText, "en");
	}
	
	private final Translate translateService;
	
	/**
	 * Instantiates the translation service with a configuration file expected to be given
	 * in the environment variable GOOGLE_APPLICATION_CREDENTIALS.
	 */
	public GoogleTranslate() {
		this.translateService = TranslateOptions.getDefaultInstance().getService();
	}
	
	/**
	 * Instantiates the translation service with the given configuration file.
	 * @param configurationPath path to the Google Application Credentials file.
	 * @throws FileNotFoundException in case the file has not been found
	 * @throws IOException in case the file could not be read
	 */
	public GoogleTranslate(String configurationPath) throws FileNotFoundException, IOException {
		try (FileInputStream fis = new FileInputStream(configurationPath)) {
			this.translateService = TranslateOptions.newBuilder().setCredentials(ServiceAccountCredentials.fromStream(fis)).build().getService();
		} finally {}
	}
	
	/**
	 * Detects the source language automatically and tries to translate it to the requested language.
	 * @param sourceText source text to be translated
	 * @param targetLanguage an ISO-639-1 identifier of the requested target language
	 * @return
	 * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO-639-1 Codes</a>
	 */
	public Result translate(String sourceText, String targetLanguage) {
		try {
			Translation translation = this.translateService.translate(sourceText, TranslateOption.targetLanguage(targetLanguage));
			LOGGER.debug("Translated from {}:{} to {}:{}.",
					translation.getSourceLanguage(),
					sourceText,
					targetLanguage,
					translation.getTranslatedText());
			return new Result(translation.getSourceLanguage(), targetLanguage, translation.getTranslatedText());
		} catch (Exception ex) {
			LOGGER.error("Failed to call Google Translation API.", ex);
			return new Result(ex.getMessage());
		}
	}
}
