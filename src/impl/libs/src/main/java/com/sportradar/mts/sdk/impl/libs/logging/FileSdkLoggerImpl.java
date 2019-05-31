/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSdkLoggerImpl implements SdkLogger {

    private final Logger logger;
    private boolean opened;

    public FileSdkLoggerImpl() {
        logger = LoggerFactory.getLogger("com.sportradar.mts.traffic");
    }
    public FileSdkLoggerImpl(String loggerName) {
        logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void logSendMessage(String value) {
        logger.info("put -> " + value);
    }

    @Override
    public void logReceivedMessage(String value) {
        logger.info("get <- " + value);
    }

    @Override
    public void open() {
        opened = true;
    }

    @Override
    public void close() {
        opened = false;
    }

    @Override
    public boolean isOpen() {
        return opened;
    }
}
