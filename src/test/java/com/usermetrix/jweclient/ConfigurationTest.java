/*
 * ConfigurationTest.java
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

import java.io.File;

import org.testng.annotations.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import com.usermetrix.jweclient.Configuration;

public class ConfigurationTest {
	
	@Test
    public void testFromFile() throws Exception {    	
        Configuration c = new Configuration();
        assertEquals(c.getProjectID(), 1);
        assertEquals(c.getTmpDirectory(), "");
        assertFalse(c.canSendLogs());
    }

	@Test
    public void testSetTmpPath() throws Exception {
        // Make sure that the ID file does not exist before the test.
        File idFile = TestConstants.logFileFor("target", TestConstants.UUID1);
        idFile.delete();
        assertFalse(idFile.exists());

        Configuration c = new Configuration();
        c.setCanSendLogs(false);
        c.setTmpDirectory("target/");
        UMLogger l = new UMLogger(TestConstants.UUID1);
        l.initialize(c, TestConstants.UUID1);

        assertTrue(idFile.exists());
        idFile.delete();
        assertFalse(idFile.exists());

        l.shutdown();

        c.setTmpDirectory("target");
        l.initialize(c, TestConstants.UUID1);
        assertTrue(idFile.exists());
        l.shutdown();
    }
}
