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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

/**
 * Geolocation of an IP address.
 * Current implementation uses ip-api.com as a provider.
 */
public class GeoIp {
	private String country;
	private String city;
	private boolean proxy;
	
	/**
	 * Subclasses are allowed.
	 */
	protected GeoIp() {}
	
	/**
	 * Looks up the given address and returns geolocation of the IP address if found.
	 * @param address address to be looked up (textual form of an IPv4 or IPv6 address).
	 * @return the geolocation object or null if not possible
	 */
	public static GeoIp lookup(String address) {
		HttpURLConnection http;
		try {
			http = (HttpURLConnection)new URL("http://ip-api.com/json/"
					+ URLEncoder.encode(address, StandardCharsets.UTF_8)
					+ "?fields=status,country,city,proxy")
					.openConnection();
		} catch (MalformedURLException e) {
			System.err.println("invalid url for ip geo lookups " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.err.println("could not connect to the ip geo lookup api " + e.getMessage());
			return null;
		}
		
		StringBuilder response = new StringBuilder();
		
		try (InputStreamReader isr = new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8)) {
			char[] buffer = new char[1024];
			int read;
			while ((read = isr.read(buffer, 0, buffer.length)) > 0) {
				response.append(buffer, 0, read);
			}
		} catch (IOException ex) {
			System.err.println("failed querying the ip geo lookup api " + ex.getMessage());
			return null;
		}
		
		try {
			JSONObject data = new JSONObject(response.toString());
			if (!"success".equals(data.getString("status"))) {
				System.err.println("geo ip lookup api responded with a status " + data.getString("status"));
				return null;
			}
			
			GeoIp geoIp = new GeoIp();
			geoIp.country = data.getString("country");
			geoIp.city = data.getString("city");
			geoIp.proxy = data.getBoolean("proxy");
			return geoIp;
		} catch (Exception ex) {
			System.err.println("geo lookup api json parse failed " + ex.getMessage());
			return null;
		}
	}

	/**
	 * Gets the country of the IP address.
	 * @return country of the IP address
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Gets the city of the IP address.
	 * @return city of the IP address
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Gets a value indicating whether the IP address is a proxy, VPN or a Tor-exit point.
	 * @return true in case of a proxy, VPN or a Tor-exit point, false otherwise
	 */
	public boolean isProxy() {
		return proxy;
	}
}
