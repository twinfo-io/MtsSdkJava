/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.logging;

import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author andrej.resnik on 22/06/16 at 13:51
 */
public class FileSdkLoggerImplTest extends TimeLimitedTestBase {

    private static Scanner scanner;
    private static File logFile;
    private final SdkLogger sdkLogger;

    public FileSdkLoggerImplTest() throws IOException {
        String folder = "com.sportradar.mts.sdk";
        sdkLogger = new FileSdkLoggerImpl();

        logFile = new File(folder + "/traffic/mts-sdk-feed.log");
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
        scanner = new Scanner(logFile);
    }

    @After
    public void tearDown() throws IOException {
        printLoggedMessages();
        PrintWriter writer = new PrintWriter(logFile);
        writer.print("");
        writer.close();
    }

    // @Test - test doesn't work anymore because Logback isn't a part of SDK core anymore so no logs are written to the file
    public void logSendMessageTest() {
        String logMsg = "sendBlocking message logged successfully";
        sdkLogger.logSendMessage(logMsg);

        assertTrue(fileContains("-> " + logMsg));
    }

    // @Test - test doesn't work anymore because Logback isn't a part of SDK core anymore so no logs are written to the file
    public void logReceivedMessageTest() {
        String logMsg = "received message logged successfully";
        sdkLogger.logReceivedMessage(logMsg);

        assertTrue(fileContains("<- " + logMsg));
    }

    @Test
    public void openTest() {
        sdkLogger.open();

        assertTrue(sdkLogger.isOpen());
    }

    @Test
    public void openCloseTest() {
        sdkLogger.open();

        assertTrue(sdkLogger.isOpen());

        sdkLogger.close();

        assertFalse(sdkLogger.isOpen());
    }

    private boolean fileContains(String msg) {
        while (scanner.hasNextLine()) {
            if (scanner.nextLine().contains(msg)) {
                return true;
            }
        }
        return false;
    }

    private void printLoggedMessages() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
        bufferedReader.lines().forEach(line -> System.out.println("file contains line: " + line));
        bufferedReader.close();
    }

}
