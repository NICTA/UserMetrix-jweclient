/*
 * UserMetrix.java
 * UserMetrix-jweclient
 *
 * Copyright (c) 2012 UserMetrix Pty Ltd. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.usermetrix.jweclient;

import java.util.HashMap;
import java.util.Map;

public class UserMetrix {
	static Map <String, UMLogger> currentLoggers = new HashMap<String, UMLogger>();
	
	private UserMetrix() {
	}

	public static UMLogger initalize(String id) throws Exception {
		UMLogger result = currentLoggers.get(id);
		if (result != null) {
			result.shutdown();
		}

		result = new UMLogger(id);
		currentLoggers.put(id, result);		

		return result;
	}

	public static void error(String id, final Class<?> source, String message) throws Exception {
		getLogger(id).error(message, source);
	}

	public static void error(String id, final Class<?> source, Throwable reason) throws Exception {
		getLogger(id).error(reason, source);
	}

	public static void error(String id, final Class<?> source, String message, Throwable reason) throws Exception {
		getLogger(id).error(message, reason, source);
	}

	public static void event(String id, final Class<?> source, String tag) throws Exception {
		getLogger(id).event(tag, source);
	}

	public static void frustration(String id, final Class<?> source, String tag) throws Exception {
		getLogger(id).frustration(tag, source);
	}

	public static void view(String id, final Class<?> source, String tag) throws Exception {
		getLogger(id).view(tag, source);
	}

	/**
	 * @param id The UUID of the logger you wish to fetch.
	 *
	 * @return The logging instance for the supplied session ID.
	 *
	 * @throws Exception If unable to get the logging instance for the supplied UUID
	 */
	private static UMLogger getLogger(String id) throws Exception {
		UMLogger result = currentLoggers.get(id);
		if (result == null) {
			result = initalize(id);
		}

		return result;
	}

	public static void shutdown(String id) {
		UMLogger result = currentLoggers.get(id);
		if (result != null) {
			result.shutdown();
			currentLoggers.remove(id);
		}		
	}

	public static void shutdownAll() {
		for(UMLogger logger : currentLoggers.values()) {
			logger.shutdown();
		}

		currentLoggers.clear();
	}
}
