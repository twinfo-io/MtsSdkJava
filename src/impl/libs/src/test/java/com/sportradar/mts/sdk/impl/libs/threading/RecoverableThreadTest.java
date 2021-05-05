/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.threading;

import com.sportradar.mts.sdk.impl.libs.LoggerTestAppender;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author andrej.resnik on 09/08/16 at 11:08
 */
public class RecoverableThreadTest extends TimeLimitedTestBase {

    private RecoverableThread thread;
    private LoggerTestAppender appender;
    private boolean contains;

    @Before
    public void setUp() {
        thread = new RecoverableThread("TestRecoverableThread"){
            @Override
            protected void run() {
                while (this.isOpen());
            }
        };
    }

    @After
    public void tearDown() {
        if (thread.isOpen()) {
            thread.close();
        }
    }

    @Test
    public void openTest() {
        appender = new LoggerTestAppender(RecoverableThread.class, loggingEvent -> {
            if (loggingEvent.getFormattedMessage().contains("Started thread [id: ")) {
                contains = true;
            }});
        assertThat(thread.isOpen(), is(false));
        thread.open();
        assertThat(thread.isOpen(), is(true));
        assertTrue(contains);
    }

    @Test
    public void doubleOpenTest() {
        appender = new LoggerTestAppender(RecoverableThread.class, loggingEvent -> {
            if (loggingEvent.getFormattedMessage().equals("Already open...")) {
                contains = true;
            }});
        assertThat(thread.isOpen(), is(false));
        thread.open();
        assertThat(thread.isOpen(), is(true));
        thread.open();
        assertTrue(contains);
    }

    @Test
    public void closeTest() {
        appender = new LoggerTestAppender(RecoverableThread.class, loggingEvent -> {
            if (!loggingEvent.getFormattedMessage().contains("Interrupting thread [id: ")
                    && (loggingEvent.getFormattedMessage().contains("Joining thread [id: ")
                    || loggingEvent.getFormattedMessage().contains("Joined thread [id: "))) {
                contains = true;
            }
        });
        thread.open();
        assertThat(thread.isOpen(), is(true));
        thread.close();
        assertThat(thread.isOpen(), is(false));
        assertTrue(contains);
    }

    @Test
    public void doubleCloseTest() {
        appender = new LoggerTestAppender(RecoverableThread.class, loggingEvent -> {
            if (loggingEvent.getFormattedMessage().equals("Skipping join...")) {
                contains = true;
            }
        });
        thread.open();
        assertThat(thread.isOpen(), is(true));
        thread.close();
        assertThat(thread.isOpen(), is(false));
        thread.close();
        assertThat(thread.isOpen(), is(false));
        assertTrue(contains);

    }

    @Test
    public void closeNowTest() {
        appender = new LoggerTestAppender(RecoverableThread.class, loggingEvent -> {
            if (!loggingEvent.getFormattedMessage().contains("Joining thread [id: ")
                    && (loggingEvent.getFormattedMessage().contains("Interrupting thread [id: ")
                    || loggingEvent.getFormattedMessage().contains("Joined thread [id: "))) {
                contains = true;
            }
        });
        assertThat(thread.isOpen(), is(false));
        thread.open();
        assertThat(thread.isOpen(), is(true));
        thread.closeNow();
        assertThat(thread.isOpen(), is(false));
        assertTrue(contains);
    }

    @Test
    public void doubleCloseNowTest() {
        appender = new LoggerTestAppender(RecoverableThread.class, loggingEvent -> {
            if (loggingEvent.getFormattedMessage().equals("Skipping join...")) {
                contains = true;
            }
        });
        thread.open();
        assertThat(thread.isOpen(), is(true));
        thread.closeNow();
        assertThat(thread.isOpen(), is(false));
        thread.closeNow();
        assertThat(thread.isOpen(), is(false));
        assertTrue(contains);
    }
}
