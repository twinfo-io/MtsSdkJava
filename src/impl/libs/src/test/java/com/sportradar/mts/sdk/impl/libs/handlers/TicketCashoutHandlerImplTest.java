/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.builders.TicketCashoutBuilder;
import com.sportradar.mts.sdk.api.interfaces.TicketCashoutResponseListener;
import com.sportradar.mts.sdk.api.utils.StaticRandom;
import com.sportradar.mts.sdk.impl.libs.SdkHelper;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisher;
import com.sportradar.mts.sdk.impl.libs.logging.SdkLogger;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created on 12/05/2017.
 */
public class TicketCashoutHandlerImplTest extends TimeLimitedTestBase {
    private AmqpPublisher publisher;
    private TicketCashoutResponseListener listener;
    private String routingKey;
    private TicketCashoutHandlerImpl handler;
    private TicketCashout ticketCashout;
    private BuilderFactory builderFactory;
    private ResponseTimeoutHandler responseTimeoutHandler;

    @Before
    public void setUp() {
        ExecutorService executor = mock(ExecutorService.class);
        publisher = mock(AmqpPublisher.class);
        SdkLogger sdkLogger = mock(SdkLogger.class);
        listener = mock(TicketCashoutResponseListener.class);
        responseTimeoutHandler = mock(ResponseTimeoutHandler.class);
        builderFactory = new SdkHelper().getBuilderFactory();

        routingKey = "ticket.cashout";
        handler = new TicketCashoutHandlerImpl(publisher, routingKey, routingKey, executor, responseTimeoutHandler, 40, 50, sdkLogger);
        ticketCashout = getTicketCashout("ticket-" + StaticRandom.S1000, 1111, 60);
    }

    @Test
    public void setListener_OnNullValuePassedTest() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("response listener cannot be null");

        handler.setListener(null);
    }

    @Test
    public void newBuilderTest() {
        assertThat(builderFactory.createTicketCashoutBuilder(), is(notNullValue()));
        assertThat(builderFactory.createTicketCashoutBuilder(), instanceOf(TicketCashoutBuilder.class));
    }

    @Test
    public void send_OnTicketCashoutNullTest() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("ticketCashoutData can not be null");

        handler.open();
        handler.send(null);
    }

    @Test
    public void send_OnExtTicketIdNullTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("TicketId not valid");

        ticketCashout = getTicketCashout(null, 1234, 80);

        handler.setListener(listener);
        handler.open();
        handler.send(ticketCashout);
    }

    @Test
    public void send_OnMissingListener() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("no response listener set");
        handler.open();
        handler.send(ticketCashout);
    }

    @Test
    public void ticketCashoutResponseReceived_ResponseNullTest() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("ticketCashoutResponse cannot be null");

        handler.setListener(listener);

        String serializedDto = handler.getSerializedDto(ticketCashout);
        String correlationId = ticketCashout.getCorrelationId();

        when(publisher.isOpen()).thenReturn(true);
        doAnswer(invocation -> {
            handler.ticketCashoutResponseReceived(null);
            return null;
        }).when(publisher).publishAsync(ticketCashout.getTicketId(),
                                        serializedDto.getBytes(StandardCharsets.UTF_8),
                                        correlationId,
                                        routingKey,
                                        routingKey);

        handler.open();
        handler.send(ticketCashout);
    }

    private TicketCashout getTicketCashout(String ticketId, int bookmakerId, int cashoutStake) {
        return builderFactory.createTicketCashoutBuilder().
                setTicketId(ticketId).
                setBookmakerId(bookmakerId).
                setCashoutStake(cashoutStake).build();
    }
}
