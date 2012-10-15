/*
 * UMSessionListenerTest.java
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

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.testng.annotations.Test;

public class UMSessionListenerTest {
	HttpSession fooSession = new HttpSession () {
		public Object getAttribute(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration getAttributeNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public long getCreationTime() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getId() {
			return TestConstants.UUID1;
		}

		public long getLastAccessedTime() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getMaxInactiveInterval() {
			// TODO Auto-generated method stub
			return 0;
		}

		public ServletContext getServletContext() {
			// TODO Auto-generated method stub
			return null;
		}

		public HttpSessionContext getSessionContext() {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getValue(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getValueNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public void invalidate() {
			// TODO Auto-generated method stub
			
		}

		public boolean isNew() {
			// TODO Auto-generated method stub
			return false;
		}

		public void putValue(String arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		public void removeAttribute(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void removeValue(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setAttribute(String arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		public void setMaxInactiveInterval(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	@Test
	public void testSessionExpire() throws Exception {
		UMSessionListener listener = new UMSessionListener();	
		UserMetrix.initialize(TestConstants.UUID1);
		assertTrue(TestConstants.logFileFor(TestConstants.UUID1).exists());

		listener.sessionDestroyed(new HttpSessionEvent(fooSession));
		assertFalse(TestConstants.logFileFor(TestConstants.UUID1).exists());
	}
}
