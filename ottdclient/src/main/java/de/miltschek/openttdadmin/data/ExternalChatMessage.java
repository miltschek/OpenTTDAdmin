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
 * External chat message.
 * @since OTTD 12.0
 */
public class ExternalChatMessage {
	private String source;
	private TextColor color;
	private String user;
	private String message;
	
	/**
	 * Creates the external chat message.
	 * @param source TODO
	 * @param color color of the chat message
	 * @param user TODO
	 * @param message TODO
	 */
	public ExternalChatMessage(String source, TextColor color, String user, String message) {
		super();
		this.source = source;
		this.color = color;
		this.user = user;
		this.message = message;
	}
	
	/**
	 * TODO
	 * @return
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * Gets the color of the message.
	 * @return color of the message.
	 */
	public TextColor getColor() {
		return color;
	}
	
	/**
	 * TODO
	 * @return
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * TODO
	 * @return
	 */
	public String getMessage() {
		return message;
	}
}
