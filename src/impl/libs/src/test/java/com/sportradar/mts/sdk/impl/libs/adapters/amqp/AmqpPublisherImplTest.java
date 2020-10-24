/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.sportradar.mts.sdk.api.impl.ConnectionStatusImpl;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author andrej.resnik on 09/06/16 at 15:01
 */
public class AmqpPublisherImplTest extends TimeLimitedTestBase {

    @Mock
    private AmqpPublisher publisher;

    @Mock
    private AmqpProducer sender;
    @Mock
    private AmqpSendResultHandler resender;

    private String ticketId;
    private String correlationId;
    private String routingKey;
    private byte[] msg;

    @Before
    public void setUp() {
        sender = mock(AmqpProducer.class);
        resender = mock(AmqpSendResultHandler.class);
        publisher = new AmqpPublisherImpl(sender, resender, new ConnectionStatusImpl());
        ticketId = "ticket-001";
        correlationId = "correlationId";
        routingKey = "routingKey";
        msg = new byte[] {};
    }

    @Test
    public void propagateSenderExceptions() {
        HashMap<String, Object> messageHeaders = new HashMap<>();
        messageHeaders.put("replyRoutingKey", routingKey);

        String errorMsg = "error";
        RuntimeException error = new RuntimeException(errorMsg);
        thrown.expect(error.getClass());
        thrown.expectMessage(errorMsg);
        when(sender.sendAsync(correlationId, msg, routingKey, messageHeaders)).thenThrow(error);

        publisher.open();
        publisher.publishAsync(ticketId, msg, correlationId, routingKey, routingKey);
    }


    @Test
    public void publishAsyncPublisherClosedTest() {
        assertThat(publisher.isOpen(), is(false));

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("sender is not open");

        publisher.publishAsync(ticketId, msg, correlationId, routingKey, routingKey);
    }

    @Test
    public void publishAsyncTest() {
        AmqpSendResult result = mock(AmqpSendResult.class);
        HashMap<String, Object> messageHeaders = new HashMap<>();
        messageHeaders.put("replyRoutingKey", routingKey);

        when(sender.sendAsync(correlationId, msg, routingKey, messageHeaders)).thenReturn(result);

        publisher.open();
        publisher.publishAsync(ticketId, msg, correlationId, routingKey, routingKey);
        verify(sender, times(1)).sendAsync(correlationId, msg, routingKey, messageHeaders);
        verify(resender, times(1)).handleSendResult(result);
    }

    @Test
    public void openTest() {
        publisher.open();

        verify(sender, times(1)).open();
        verify(resender, times(1)).open();
        assertThat(publisher.isOpen(), is(true));
    }

    @Test
    public void doubleOpenTest() {
        publisher.open();

        verify(sender, times(1)).open();
        verify(resender, times(1)).open();
        assertThat(publisher.isOpen(), is(true));

        publisher.open();

        verify(sender, times(1)).open();
        verify(resender, times(1)).open();
        assertThat(publisher.isOpen(), is(true));
    }

    @Test
    public void closeTest() {
        publisher.open();

        assertThat(publisher.isOpen(), is(true));

        publisher.close();

        verify(sender, times(1)).close();
        verify(resender, times(1)).close();
        assertThat(publisher.isOpen(), is(false));
    }

    @Test
    public void doubleCloseTest() {
        publisher.open();

        assertThat(publisher.isOpen(), is(true));

        publisher.close();

        verify(sender, times(1)).close();
        verify(resender, times(1)).close();
        assertThat(publisher.isOpen(), is(false));

        publisher.close();

        verify(sender, times(1)).close();
        verify(resender, times(1)).close();
        assertThat(publisher.isOpen(), is(false));
    }
}
