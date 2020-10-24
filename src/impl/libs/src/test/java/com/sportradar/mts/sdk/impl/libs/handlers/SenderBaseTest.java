/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.impl.ConnectionStatusImpl;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpProducer;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisher;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisherImpl;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpSendResultHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author andrej.resnik on 13/06/16 at 08:15
 */
public class SenderBaseTest extends TimeLimitedTestBase {

    @Mock
    private AmqpProducer sender;
    @Mock
    private AmqpSendResultHandler resender;

    private AmqpPublisher publisher;

    @Before
    public void setUp() {
        sender = mock(AmqpProducer.class);
        resender = mock(AmqpSendResultHandler.class);

        publisher = new AmqpPublisherImpl(sender, resender, new ConnectionStatusImpl());
    }

    @Test
    public void openTest() {
        publisher.open();
        assertThat(publisher.isOpen(), is(true));
        verify(sender, times(1)).open();
        verify(resender, times(1)).open();
    }

    @Test
    public void doubleOpenTest() {
        publisher.open();
        assertThat(publisher.isOpen(), is(true));
        verify(sender, times(1)).open();
        verify(resender, times(1)).open();

        publisher.open();
        verify(sender, times(1)).open();
        verify(resender, times(1)).open();
    }

    @Test
    public void closeTest() {
        publisher.open();
        assertThat(publisher.isOpen(), is(true));

        publisher.close();
        assertThat(publisher.isOpen(), is(false));
        verify(sender, times(1)).close();
        verify(resender, times(1)).close();
    }

    @Test
    public void doubleCloseTest() {
        publisher.open();
        assertThat(publisher.isOpen(), is(true));

        publisher.close();
        assertThat(publisher.isOpen(), is(false));
        verify(sender, times(1)).close();
        verify(resender, times(1)).close();

        publisher.close();
        verify(sender, times(1)).close();
        verify(resender, times(1)).close();
    }
}
