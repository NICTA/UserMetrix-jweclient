/*
 * UMLogger.java
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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class to send a log in the below format to usermetrix.com.
 *
 * ---
 * v: 1
 * system:
 *   id: <uuid>
 *   os: <tag>
 *   start: <time&date>
 * meta:
 *   - <key>: <value>
 * log:
 *   - type: <enum>
 *     time: <milliseconds>
 *     source: <source class>
 *     message: <msg>
 *     stack:
 *       - class: <source class>
 *         line: <line number>
 *         method: <method>
 * duration: <milliseconds>
 */
public final class UMLogger {

    /** The current version of the log file this client generates. */
    private static final int LOG_VERSION = 1;

    /** The line ending to use when sending log files. */
    private static final String LINE_END = "\r\n";

    /** Definition of two hyphens to dump when sending log files. */
    private static final String TWO_HYPHENS = "--";

    /** Definition the boundary to use when sending log files. */
    private static final String BOUNDARY =  "*****";

    /** The size of the buffer to use when sending log files. */
    private static final int BUFFER_SIZE = 1048576;

    /* Members of UserMetrix.  -----------------------------------------------*/
    /** The destination stream for the tmp usermetrix log. */
    private FileWriter logStream;

    /** The destination tmp file for the UserMetrix log. */
    private BufferedWriter logWriter;

    /** The destination tmp file for the UserMetrix log. */
    private File logFile;

    /** The clock that we fetch the time from. */
    private Calendar clock;

    /** The UUID for this client. */
    private String clientID;

    /** The configuration for this UserMetrix client. */
    private Configuration config;

    /** Start time in milliseconds. */
    private long startTime;
    
    /**
     * 
     * @param id
     * @throws Exception
     */
    protected UMLogger(String id) throws Exception {
    	logWriter = null;
    	logFile = null;
    	config = new Configuration();
    	clock = Calendar.getInstance();
    	this.initialize(config, id);
    }
    
    /**
     * Correctly hyphanates a HTTP session UUID to be the same format that the UUID helper
     * utility.
     *
     * @param uuid Raw UUID without hyphens.
     *
     * @return A UUID with 4 hypens within it: 8-4-4-4-12.
     */
    public static String hyphateUUID(String uuid) {
		String result = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + 
						uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" +
						uuid.substring(20, 32);			
				
		return result;
		
	}

    /**
     * Initalise the UserMetrix log - call this when you start your application.
     *
     * @param configuration The configuration client to use for this UserMetrix
     * client.
     */
    public void initialize(final Configuration configuration, final String uuid) throws Exception {
        // Check if the temp directory exists - if not, create it.
        File tmpDirectory = new File(configuration.getTmpDirectory());
        if (!tmpDirectory.exists()) {
            tmpDirectory.mkdirs();
        }

        clientID = hyphateUUID(uuid);
        setLogDestination(new File(configuration.getTmpDirectory()
                                   + "um" + clientID + ".log"));
        startLog();
    }

    /**
     * Shutdown the UserMetrix log - call this when your application gracefully
     * exits.
     */
    public void shutdown() {
        finishLog();
    }

    /**
     * Append a view tag to your log.
     *
     * @param tag The unique tag describing the view that the user invoked.
     * @param source The source of the log message.
     */
    public void view(final String tag, final Class<?> source) {
        try {
            if (logWriter != null) {
                logWriter.write("  - type: view");
                writeMessageDetails(tag, source);
            }
        } catch (IOException e) {
            System.err.println("UserMetrix: Unable to write to file." + e.toString());
        }
    }

    /**
     * Append a usage tag to your log.
     *
     * @param tag The unique tag to use for this particular type of software usage.
     * @param source The source of the log message.
     */
    public void event(final String tag, final Class<?> source) {
        try {
            if (logWriter != null) {
                logWriter.write("  - type: usage");
                writeMessageDetails(tag, source);
            }
        } catch (IOException e) {
            System.err.println("UserMetrix: Unable to write to file." + e.toString());
        }
    }

    /**
     * Append a frustration tag to your log.
     *
     * @param tag The tag (perhaps user specified) for this frustration.
     * @param source The source of the frustration (this is not mega-relevant, frustrations
     * are global to their origin).
     */
    public void frustration(final String tag, final Class<?> source) {
        try {
            if (logWriter != null) {
                logWriter.write("  - type: frustration");
                writeMessageDetails(tag, source);
            }
        } catch (IOException e) {
            System.err.println("UserMetrix: Unable to write to file." + e.toString());
        }
    }

    /**
     * Append an error message to your log.
     *
     * @param message What caused this error within your application.
     * @param source The source of the log message.
     */
    public void error(final String message, final Class<?> source) {
        try {
            if (logWriter != null) {
                logWriter.write("  - type: error");
                writeMessageDetails(message, source);
            }
        } catch (IOException e) {
            System.err.println("UserMetrix: Unable to write to file." + e.toString());
        }
    }

    /**
     * Dumps additional message details to disk.
     *
     * @param message The message that is being reported.
     * @param source The source of the log message.
     *
     * @throws IOException If unable to write the details to the log.
     */
    private void writeMessageDetails(final String message,
                                     final Class<?> source) throws IOException {
        if (logWriter != null) {
            logWriter.newLine();
            logWriter.write("    time: " + (System.currentTimeMillis() - this.startTime));
            logWriter.newLine();
            logWriter.write("    source: " + source);
            logWriter.newLine();
            logWriter.write("    message: \"" + message + "\"");
            logWriter.newLine();
            logWriter.flush();
        }
    }

    /**
     * Append an error message to your log.
     *
     * @param message What cause this error within your application.
     * @param exception The exception that caused this error.
     * @param source The source of the log message.
     */
    public void error(final String message,
                      final Throwable exception,
                      final Class<?> source) {
        this.error(message, source);
        this.logStack(exception);
    }

    /**
     * Append an error message to your log.
     *
     * @param exception The exception that caused this error.
     * @param source The source of the log message
     */
    public void error(final Throwable exception, final Class<?> source) {
        this.error("null", source);
        this.logStack(exception);
    }

    /**
     * Writes the stack trace of an exception to your log.
     *
     * @param exception The exception to write to the log.
     */
    private void logStack(final Throwable exception) {
        try {
            if (logWriter != null) {
                logWriter.write("    stack:");
                logWriter.newLine();
                for (StackTraceElement e : exception.getStackTrace()) {
                    logWriter.write("      - class: " + e.getClassName());
                    logWriter.newLine();
                    logWriter.write("        line: " + e.getLineNumber());
                    logWriter.newLine();
                    logWriter.write("        method: " + e.getMethodName());
                    logWriter.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("UserMetrix: Unable to write to file." + e.toString());
        }
    }

    private void startLog() {
        try {
            if (logWriter != null) {
                logWriter.write("---");
                logWriter.newLine();
                logWriter.write("v: " + LOG_VERSION);
                logWriter.newLine();
                logWriter.write("system:");
                logWriter.newLine();

                // Write the unique client identifier to the log.
                logWriter.write("  id: " + clientID);
                logWriter.newLine();

                // Write details of the operating system out to the log.
                logWriter.write("  os: ");
                logWriter.write(System.getProperty("os.name") + " - ");
                logWriter.write(System.getProperty("os.version"));
                logWriter.newLine();

                // Write the application start time out to the log.
                logWriter.write("  start: ");

                // Create a new simple date format class each time to avoid any
                // thread safety issues.
                SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                logWriter.write(SDF.format(clock.getTime()));

                startTime = System.currentTimeMillis();
                logWriter.newLine();

                // Dump meta data stored in the configuration.
                logWriter.write("meta:");
                logWriter.newLine();

                // Begin the log.
                logWriter.write("log:");
                logWriter.newLine();
                logWriter.flush();
            }
        } catch (IOException e) {
            System.err.println("UserMetrix: Unable to write file." + e.toString());
        }
    }

    private void finishLog() {
        try {
            if (logWriter != null) {
                // Write the application end time out to the log.
                logWriter.write("duration: " + (System.currentTimeMillis() - this.startTime));
                logWriter.newLine();
                logWriter.close();

                if (config.getProjectID() != 0) {
                    sendLog();
                }
            }
        } catch (IOException e) {
            System.err.println("UserMetrix: Unable to close file." + e.toString());
        }
    }

    private void cleanLogFromDisk() {
        if (!logFile.delete()) {
            System.err.println("UserMetrix: Unable to clean log from disk.");
        }
    }

    private void sendLog() {
        if (!config.canSendLogs()) {
            // Not permitted to send logs - leave method.
            cleanLogFromDisk();
            return;
        }

        try {
            // Send data
            URL url = new URL("http://usermetrix.com/projects/" + config.getProjectID() + "/log");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

            // Write the header of the multipart HTTP POST request.
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
            wr.writeBytes("Content-Disposition: form-data; name=\"upload\";"
                    + " filename=\"" + "usermetrix.log" +"\"" + LINE_END);
            wr.writeBytes(LINE_END);

            // Read the log and append it as an attachment to the POST request.
            FileInputStream fileInputStream = new FileInputStream(new File(config.getTmpDirectory()
            															   + "um" + clientID + ".log"));
            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                wr.write(buffer, 0, bufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // Write the footer of the multipart HTTP POST request.
            wr.writeBytes(LINE_END);
            wr.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);
            wr.flush();

            // Get the response from the server.
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line; while ((line = rd.readLine()) != null) {
//                System.err.println(line);
            }

            // Close all our streams.
            fileInputStream.close();
            wr.close();
            rd.close();

            // Delete the log file when successfully.
            cleanLogFromDisk();
        } catch (UnknownHostException e) {
            // Silently ignore unknown host exception - can't connect to the internet.
        } catch (Exception e) {
            System.err.println("UserMetrix: Unable to send log - " + e);
        }
    }

    private void setLogDestination(final File newLog) {
        try {
            logFile = newLog;

            // Check for the existence of a log, ff it exists - send it first.
            if (logFile.exists()) {
                sendLog();
            }

            // Log should not exist at this point - create a new log.
            logStream = new FileWriter(logFile);
            logWriter = new BufferedWriter(logStream);
        } catch (IOException e) {
            System.err.println("UserMetrix: Unable to set log location." + e.toString());
        }
    }
}

