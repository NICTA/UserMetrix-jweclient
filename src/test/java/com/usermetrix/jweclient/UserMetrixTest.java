/*
 * UserMetrixTest.java
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

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import java.io.File;

import org.testng.annotations.Test;

public final class UserMetrixTest {

	@Test
	public void testInit() throws Exception {
		UserMetrix.initalize("19C1AA26B8064108956DBB13D4D626F1");
		File log = new File("um19C1AA26B8064108956DBB13D4D626F1.log");
    	assertTrue(log.exists());    	
    	UserMetrix.shutdown("19C1AA26B8064108956DBB13D4D626F1");
    	assertFalse(log.exists());

		UserMetrix.initalize("19C1AA26B8064108956DBB13D4D626F2");
		log = new File("um19C1AA26B8064108956DBB13D4D626F2.log");
    	assertTrue(log.exists());
    	UserMetrix.shutdown("19C1AA26B8064108956DBB13D4D626F2");
    	assertFalse(log.exists());
	}

	@Test
	public void testShutdownAll() throws Exception {
		UserMetrix.initalize("19C1AA26B8064108956DBB13D4D626F1");
		UserMetrix.initalize("19C1AA26B8064108956DBB13D4D626F2");

		UserMetrix.shutdownAll();
		assertFalse(new File("um19C1AA26B8064108956DBB13D4D626F1.log").exists());
    	assertFalse(new File("um19C1AA26B8064108956DBB13D4D626F2.log").exists());
	}

    @Test
	public void rawStartUp() throws Exception {
		UserMetrix.event("19C1AA26B8064108956DBB13D4D626F1", UserMetrixTest.class, "rawStartUp");
		assertTrue(new File("um19C1AA26B8064108956DBB13D4D626F1.log").exists());
	}
}
