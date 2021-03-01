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
package de.miltschek.genowefa;

/**
 * A language-specific message.
 */
public class Statement {
	private final String sourceLanguage;
	private final String targetLanguage;
	private final String statement;
	
	/**
	 * Stores data of a message.
	 * @param sourceLanguage source language code
	 * @param targetLanguage destination language code
	 * @param statement message
	 */
	public Statement(String sourceLanguage, String targetLanguage, String statement) {
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		this.statement = statement;
	}
	
	/**
	 * Stores a message without specified language metadata.
	 * @param statement message
	 */
	public Statement(String statement) {
		this.sourceLanguage = null;
		this.targetLanguage = null;
		this.statement = statement;
	}

	/**
	 * Gets the source language.
	 * @return the source language or null if not specified
	 */
	public String getSourceLanguage() {
		return sourceLanguage;
	}
	
	/**
	 * Gets the target language.
	 * @return the target language or null if not specified.
	 */
	public String getTargetLanguage() {
		return targetLanguage;
	}
	
	/**
	 * Gets the message.
	 * @return the message
	 */
	public String getStatement() {
		return statement;
	}
}
