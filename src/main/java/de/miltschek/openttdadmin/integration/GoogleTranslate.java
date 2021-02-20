package de.miltschek.openttdadmin.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		 * @return true if the translation was successfull, false otherwise
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
	public static Result translateToEnglish(String sourceText) {
		return translate(sourceText, "en");
	}
	
	/**
	 * Detects the source language automatically and tries to translate it to the requested language.
	 * @param sourceText source text to be translated
	 * @param targetLanguage an ISO-639-1 identifier of the requested target language
	 * @return
	 * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO-639-1 Codes</a>
	 */
	public static Result translate(String sourceText, String targetLanguage) {
		if (System.getenv("GOOGLE_APPLICATION_CREDENTIALS") == null) {
			LOGGER.error("GOOGLE_APPLICATION_CREDENTIALS environment variable has not been set");
			return new Result("GOOGLE_APPLICATION_CREDENTIALS environment variable has not been set");
		}

		try {
			Translate translate = TranslateOptions.getDefaultInstance().getService();
			Translation translation = translate.translate(sourceText, TranslateOption.targetLanguage(targetLanguage));
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
