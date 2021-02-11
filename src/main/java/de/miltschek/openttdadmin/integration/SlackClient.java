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
package de.miltschek.openttdadmin.integration;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Very basic implementation of slack connector.
 */
public class SlackClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(SlackClient.class);
	
	private final HttpClient http;
	private String channelEncoded;
	private String token;

	/**
	 * Creates a slack connector.
	 * @param channel channel name or ID
	 * @param token authentication token (xoxo-...)
	 */
	public SlackClient(String channel, String token) {
		this.http = HttpClient.newHttpClient();
		this.channelEncoded = URLEncoder.encode(channel, StandardCharsets.UTF_8);
		this.token = token;
	}
	
	/**
	 * Sends a message to the slack channel.
	 * @param message message to be sent
	 * @return true in case the message has been successfully sent, false otherwise
	 */
	public boolean sendMessage(String message) {
		for (int retryCount = 2; retryCount > 0; retryCount--) {
			try {
	    		String messageEncoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
		    	BodyPublisher publisher = BodyPublishers.ofString("channel=" + channelEncoded + "&text=" + messageEncoded);
		    	HttpRequest request = HttpRequest.newBuilder(new URI("https://slack.com/api/chat.postMessage"))
		    			.setHeader("Authorization", "Bearer " + token)
		    			.setHeader("Content-Type", "application/x-www-form-urlencoded")
		    			.POST(publisher)
		    			.build();
		    	BodyHandler<String> responseHandler = BodyHandlers.ofString();
		    	HttpResponse<String> response = http.send(request, responseHandler);
		    	
		    	if (response.statusCode() == 200) {
		    		JSONObject jsonResponse = new JSONObject(response.body());
		    		if (jsonResponse.getBoolean("ok")) {
		    			return true;
		    		} else {
		    			LOGGER.error("failed to send slack message; response {}", jsonResponse.getString("error"));
		    		}
		    	} else if (response.statusCode() == 429) {
		    		// rate exceeded
		    		LOGGER.warn("slack reported rate exceeded");
		    	} else {
		    		LOGGER.error("slack responded with error code {}", response.statusCode());
		    	}
	    	} catch (Exception ex) {
	    		LOGGER.error("slack exception", ex);
	    	}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		LOGGER.debug("failed to deliver the message {}", message);
		return false;
	}
}
