/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author andrej.resnik on 13/06/16 at 15:49
 */
public class LoggerTestAppender {

    private final List<ILoggingEvent> loggingEvents;

    public LoggerTestAppender(String logger) {
        this(logger, null);

    }
    @SuppressWarnings("unchecked")
    public LoggerTestAppender(String logger, final LogEventListener listener) {
        Logger root = (Logger) LoggerFactory.getLogger(logger);
        Appender<ILoggingEvent> appender = mock(Appender.class);
        when(appender.getName()).thenReturn("MOCK");
        loggingEvents = new ArrayList<>();
        doAnswer(invocation -> {
            ILoggingEvent loggingEvent = (ILoggingEvent) invocation.getArguments()[0];
            if (listener != null) {
                listener.logEventReceived(loggingEvent);
            }
            loggingEvents.add(loggingEvent);
            return null;
        }).when(appender).doAppend(any());
        root.addAppender(appender);
    }

    public LoggerTestAppender() {
        this(Logger.ROOT_LOGGER_NAME);
    }

    public LoggerTestAppender(Class clazz) {
        this(clazz.getName());
    }

    public LoggerTestAppender(Class clazz, LogEventListener listener) {
        this(clazz.getName(), listener);
    }

    public List<ILoggingEvent> getLoggingEvents() {
        return loggingEvents;
    }

    public boolean searchLoggingEventByFormattedMessage(String shouldContain) {

        return loggingEvents.stream().anyMatch(iLoggingEvent -> iLoggingEvent.getFormattedMessage().contains(shouldContain));
    }

    public boolean searchLoggingEventByTwoFormattedMessage(String shouldContain1, String shouldContain2) {

        return loggingEvents.stream().anyMatch(iLoggingEvent -> iLoggingEvent.getFormattedMessage().contains(shouldContain1)&&iLoggingEvent.getFormattedMessage().contains(shouldContain2));
    }
}
