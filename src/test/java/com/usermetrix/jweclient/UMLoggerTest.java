/*
 * UMLoggerTest.java
 * UserMetrix-jweclient
 *
 * Copyright (c) 2011 UserMetrix Pty Ltd. All rights reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import com.usermetrix.jweclient.UMLogger;

public final class UMLoggerTest {

    @Test
    public void testCreation() throws Exception {
    	UMLogger logger = new UMLogger(TestConstants.UUID1);    	

    	File log = TestConstants.logFileFor(TestConstants.UUID1);
    	assertTrue(log.exists());

    	Yaml yaml = new Yaml();
    	InputStream in = new FileInputStream(log);

    	Map<String, Object> logContents = (Map<String, Object>) yaml.load(in);
    	Integer v = (Integer) logContents.get("v");
    	assertEquals(new Integer(1), v);
    	Map<String, Object> system = (Map<String, Object>) logContents.get("system");
    	String id = (String) system.get("id");
    	assertEquals("19C1AA26-B806-4108-956D-BB13D4D626F1", id);

    	logger.shutdown();
    	assertFalse(log.exists());
    }

    @Test
    public void testLogCalls() throws Exception {
    	UMLogger logger = new UMLogger(TestConstants.UUID1);

    	logger.event("testEvent", UMLogger.class);
    	logger.view("testView", UMLogger.class);
    	logger.error("testError", UMLogger.class);
    	logger.error("testError", new Exception("foo"), UMLogger.class);
    	logger.error(new Exception("foo"), UMLogger.class);

    	File log = TestConstants.logFileFor(TestConstants.UUID1);
    	Yaml yaml = new Yaml();
    	InputStream in = new FileInputStream(log);

    	Map<String, Object> logContents = (Map<String, Object>) yaml.load(in);
    	ArrayList<Map<String, Object> > logStream = (ArrayList<Map<String, Object> >) logContents.get("log");

    	Map<String, Object> logItem = logStream.get(0);
    	assertEquals("usage", logItem.get("type"));
    	assertEquals("class com.usermetrix.jweclient.UMLogger", logItem.get("source"));
    	assertEquals("testEvent", logItem.get("message"));

    	logItem = logStream.get(1);
    	assertEquals("view", logItem.get("type"));
    	assertEquals("class com.usermetrix.jweclient.UMLogger", logItem.get("source"));
    	assertEquals("testView", logItem.get("message"));

    	logItem = logStream.get(2);
    	assertEquals("error", logItem.get("type"));
    	assertEquals("class com.usermetrix.jweclient.UMLogger", logItem.get("source"));
    	assertEquals("testError", logItem.get("message"));

    	logItem = logStream.get(3);
    	assertEquals("error", logItem.get("type"));
    	assertEquals("class com.usermetrix.jweclient.UMLogger", logItem.get("source"));
    	assertEquals("testError", logItem.get("message"));

    	logItem = logStream.get(4);
    	assertEquals("error", logItem.get("type"));
    	assertEquals("class com.usermetrix.jweclient.UMLogger", logItem.get("source"));
    	assertEquals("null", logItem.get("message"));
    }
}
