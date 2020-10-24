/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.impl.ConnectionStatusImpl;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import com.sportradar.mts.sdk.impl.libs.LoggerTestAppender;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpConsumer;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpMessageReceiver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author andrej.resnik on 15/06/16 at 09:41
 */
public class AmqpTicketCancelResponseReceiverImplTest extends TimeLimitedTestBase {
    @Mock
    private AmqpConsumer consumer;
    @Mock
    private TicketCancelResponseReceiver responseReceiver;

    private AmqpMessageReceiver msgReceiver;

    private byte[] msg;
    private String correlationId;
    private String routingKey;
    private Map<String, Object> messageHeaders;
    private boolean contains;
    private LoggerTestAppender appender;

    @Before
    public void setUp() {
        consumer = mock(AmqpConsumer.class);
        responseReceiver = mock(TicketCancelResponseReceiver.class);
        msgReceiver = new AmqpTicketCancelResponseReceiverImpl(consumer, responseReceiver, new ConnectionStatusImpl());

        msg = "testMessage".getBytes();
        correlationId = "correlationId";
        routingKey = "routingKey";
        messageHeaders = new HashMap<>();

        appender = new LoggerTestAppender(AmqpTicketCancelResponseReceiverImpl.class);
    }

    @Test
    public void openTest() {
        msgReceiver.open();

        assertTrue(msgReceiver.isOpen());
        verify(consumer, times(1)).open();
        verify(consumer, times(1)).setMessageReceivedHandler(msgReceiver);
        verifyNoMoreInteractions(consumer);
    }

    @Test
    public void doubleOpenTest() {
        msgReceiver.open();

        assertTrue(msgReceiver.isOpen());
        verify(consumer, times(1)).open();
        verify(consumer, times(1)).setMessageReceivedHandler(msgReceiver);

        msgReceiver.open();

        assertTrue(msgReceiver.isOpen());
        verify(consumer, times(1)).open();
        verify(consumer, times(1)).setMessageReceivedHandler(msgReceiver);
        verifyNoMoreInteractions(consumer);
    }

    @Test
    public void closeTest() {
        msgReceiver.open();

        assertTrue(msgReceiver.isOpen());

        msgReceiver.close();

        assertFalse(msgReceiver.isOpen());
        verify(consumer, times(1)).open();
        verify(consumer, times(1)).close();
        verify(consumer, times(1)).setMessageReceivedHandler(msgReceiver);
        verifyNoMoreInteractions(consumer);
    }

    @Test
    public void doubleCloseTest() {
        msgReceiver.open();

        assertTrue(msgReceiver.isOpen());

        msgReceiver.close();

        assertFalse(msgReceiver.isOpen());
        verify(consumer, times(1)).open();
        verify(consumer, times(1)).close();
        verify(consumer, times(1)).setMessageReceivedHandler(msgReceiver);

        msgReceiver.close();

        assertFalse(msgReceiver.isOpen());
        verify(consumer, times(1)).close();
        verify(consumer, times(1)).setMessageReceivedHandler(msgReceiver);
        verifyNoMoreInteractions(consumer);
    }

    @Test
    public void consume_DeserializationFailTest() {
        msgReceiver.open();
        msgReceiver.consume(msg, routingKey, correlationId, messageHeaders);

        contains = appender.searchLoggingEventByFormattedMessage(StringUtils.format("failed to deserialize ticket cancel response! msg:", msg));
        assertTrue(contains);
    }

//    @Test
//    public void consume_OKTest() throws IOException {
//        TicketCancelResponseWrapper ticketCancelResponse = new TicketCancelResponseWrapper();
//        msg = JsonUtils.serialize(ticketCancelResponse);
//
//        msgReceiver.open();
//        MessageStatus messageStatus = msgReceiver.consume(msg, routingKey, correlationId, messageHeaders);
//
//        verify(responseReceiver, times(1)).ticketCancelResponseReceived(any(TicketCancelResponseImpl.class));
//        assertTrue(MessageStatus.CONSUMED_SUCCESSFULLY.equals(messageStatus));
//    }

    @Test
    public void afterLimitReachedTest() {
        msgReceiver.open();
        msgReceiver.afterLimitReached(msg, routingKey, correlationId);

        contains = appender.searchLoggingEventByFormattedMessage(StringUtils.format("ticket cancel response consume retry reached!"));
        assertTrue(contains);
    }
}
