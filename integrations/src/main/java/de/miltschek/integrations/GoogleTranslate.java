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
