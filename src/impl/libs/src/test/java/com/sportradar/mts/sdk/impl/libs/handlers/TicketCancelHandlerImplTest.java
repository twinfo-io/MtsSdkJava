/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.TicketCancellationReason;
import com.sportradar.mts.sdk.api.impl.builders.TicketCancelBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelResponseListener;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.impl.libs.SdkHelper;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisher;
import com.sportradar.mts.sdk.impl.libs.logging.SdkLogger;
import com.sportradar.mts.sdk.impl.libs.receivers.TicketCancelResponseWrapper;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * @author andrej.resnik on 16/06/16 at 10:02
 */
@SuppressWarnings("SameParameterValue")
public class TicketCancelHandlerImplTest extends TimeLimitedTestBase {

    private ExecutorService executor;
    private AmqpPublisher publisher;
    private SdkLogger sdkLogger;
    private TicketCancelResponseListener listener;
    private TicketCancelHandler handler;
    private TicketCancel ticketCancel;
    private String routingKey;
    private String replyRoutingKey;
    private BuilderFactory builderFactory;
    private ResponseTimeoutHandler responseTimeoutHandler;

    @Before
    public void setUp() {
        executor = mock(ExecutorService.class);
        publisher = mock(AmqpPublisher.class);
        sdkLogger = mock(SdkLogger.class);
        listener = mock(TicketCancelResponseListener.class);
        responseTimeoutHandler = mock(ResponseTimeoutHandler.class);
        builderFactory = new SdkHelper().getBuilderFactory();

        routingKey = "cancel";
        replyRoutingKey = "nodeXY.cancel.confirm";
        handler = new TicketCancelHandlerImpl(publisher, routingKey, replyRoutingKey, executor, responseTimeoutHandler, 40, 50, sdkLogger);
        ticketCancel = getTicketCancel();
    }

    @Test
    public void setListener_OnNullValuePassedTest() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("response listener cannot be null");

        handler.setListener(null);
    }

    @Test
    public void newBuilderTest() {
        assertThat(builderFactory.createTicketCancelBuilder(), is(notNullValue()));
        assertThat(builderFactory.createTicketCancelBuilder(), instanceOf(TicketCancelBuilderImpl.class));
    }

    @Test
    public void send_OnTicketCancelNullTest() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("ticketCancel cannot be null");

        handler.open();
        handler.send(null);
    }

    @Test
    public void send_OnExtTicketIdNullTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("TicketId not valid");

        ticketCancel = buildTicketCancel(9985, null, TicketCancellationReason.CustomerTriggeredPrematch);

        handler.setListener(listener);
        handler.open();
        handler.send(ticketCancel);
    }

    //TODO: @Test
    public void sendTest() {
        TicketCancelResponseWrapper response = new TicketCancelResponseWrapper();
        response.setExtTicket(ticketCancel.getTicketId());
        byte[] msg = JsonUtils.serialize(ticketCancel);
        String correlationId = getFormattedCorrelationId(ticketCancel);

        when(publisher.isOpen()).thenReturn(true);
        doAnswer(invocation -> {
            handler.ticketCancelResponseReceived(response);
            return null;
        }).when(publisher).publishAsync(ticketCancel.getTicketId(), msg, correlationId, routingKey, replyRoutingKey);

        handler.setListener(listener);
        handler.open();
        handler.send(ticketCancel);

        verify(publisher, times(1)).publishAsync(ticketCancel.getTicketId(), msg, correlationId, routingKey, replyRoutingKey);
        assertThat(response, is(notNullValue()));

        String ticketCancelString = JsonUtils.serializeAsString(ticketCancel);
        verify(sdkLogger, times(1)).logSendMessage(ticketCancelString);
    }

    @Test
    public void ticketCancelResponseReceived_ResponseNullTest() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("no response listener set");

        byte[] msg = JsonUtils.serialize(ticketCancel);
        String correlationId = getFormattedCorrelationId(ticketCancel);

        when(publisher.isOpen()).thenReturn(true);
        doAnswer(invocation -> {
            handler.ticketCancelResponseReceived(null);
            return null;
        }).when(publisher).publishAsync(ticketCancel.getTicketId(), msg, correlationId, routingKey, replyRoutingKey);

        handler.open();
        handler.send(ticketCancel);
    }

    //TODO: @Test
    public void ticketCancelResponseReceivedTest() throws InterruptedException {
        TicketCancelResponseWrapper response = new TicketCancelResponseWrapper();
        response.setExtTicket(ticketCancel.getTicketId());
        byte[] msg = JsonUtils.serialize(ticketCancel);
        String correlationId = getFormattedCorrelationId(ticketCancel);

        when(publisher.isOpen()).thenReturn(true);
        doAnswer(invocation -> {
            handler.ticketCancelResponseReceived(response);
            return null;
        }).when(publisher).publishAsync(ticketCancel.getTicketId(), msg, correlationId, routingKey, replyRoutingKey);

        Semaphore semaphore = new Semaphore(0);
        when(executor.submit(any(Runnable.class))).then(invocation -> {
            new Thread((Runnable) invocation.getArguments()[0]).run();
            semaphore.release();
            return null;
        });

        handler.open();
        handler.setListener(listener);
        handler.send(ticketCancel);

        semaphore.acquire();
        verify(listener, times(1)).responseReceived(response);

        String responseString = JsonUtils.serializeAsString(response);
        verify(sdkLogger, times(1)).logReceivedMessage(responseString);
    }


    public TicketCancel getTicketCancel() {
        return builderFactory.createTicketCancelBuilder()
                .setBookmakerId(9985)
                .setTicketId("ticket-id-to-cancel")
                .setCode(TicketCancellationReason.CustomerTriggeredPrematch)
                .build();
    }

    private TicketCancel buildTicketCancel(
            Integer bookmakerId,
            String extTicket,
            TicketCancellationReason cancellationReason) {

        return builderFactory.createTicketCancelBuilder()
                .setBookmakerId(bookmakerId)
                .setTicketId(extTicket)
                .setCode(cancellationReason)
                .build();
    }

    private String getFormattedCorrelationId(TicketCancel ticketCancel) {
        return "ticket_cancel:" + ticketCancel.getTicketId();
    }
}
