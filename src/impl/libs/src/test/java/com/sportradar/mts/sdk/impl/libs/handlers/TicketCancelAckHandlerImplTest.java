/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.TicketCancelAck;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.TicketCancelAckStatus;
import com.sportradar.mts.sdk.api.impl.builders.TicketCancelAckBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelAckSender;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.StaticRandom;
import com.sportradar.mts.sdk.impl.libs.SdkHelper;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisher;
import com.sportradar.mts.sdk.impl.libs.logging.SdkLogger;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author andrej.resnik on 09/06/16 at 15:07
 */
public class TicketCancelAckHandlerImplTest extends TimeLimitedTestBase {

    private AmqpPublisher publisher;
    private TicketCancelAck ticketCancelAcknowledgment;
    private SdkLogger sdkLogger;
    private String routingKey;
    private String correlationId;
    private TicketCancelAckSender cancelAckSender;
    private byte[] msg;
    private BuilderFactory builderFactory;

    @Before
    public void setUp() {
        sdkLogger = mock(SdkLogger.class);
        publisher = mock(AmqpPublisher.class);
        ExecutorService executorService = mock(ExecutorService.class);
        routingKey = "ack.ticket";
        builderFactory = new SdkHelper().getBuilderFactory();

        cancelAckSender = new TicketCancelAckHandlerImpl(publisher,
                                                        routingKey,
                                                        executorService,
                                                        40,
                                                        sdkLogger);
        ticketCancelAcknowledgment = getTicketCancelAcknowledgment();
        msg = JsonUtils.serialize(MtsDtoMapper.map(ticketCancelAcknowledgment));

        // cancelAckSender gets the correlationId: "ticket_ack:" + ticketCancelAcknowledgment.getTicketId()
        // due to this fact the same is done here to ensure proper testing conditions
        correlationId = ticketCancelAcknowledgment.getCorrelationId();

        when(publisher.isOpen()).thenReturn(true);
    }

    @Test
    public void newBuilderTest() {
        assertThat(builderFactory.createTicketCancelAckBuilder(), is(notNullValue()));
        assertThat(builderFactory.createTicketCancelAckBuilder(), instanceOf(TicketCancelAckBuilderImpl.class));
    }

    @Test
    public void sendAsyncTest() {
        cancelAckSender.open();
        cancelAckSender.send(ticketCancelAcknowledgment);

        verify(publisher, times(1)).publishAsync(msg, correlationId, routingKey, routingKey);

        String ticketCancelAckString = JsonUtils.serializeAsString(MtsDtoMapper.map(ticketCancelAcknowledgment));
        verify(sdkLogger, times(1)).logSendMessage(ticketCancelAckString);
    }

    private TicketCancelAck getTicketCancelAcknowledgment() {
        return builderFactory.createTicketCancelAckBuilder()
                .setTicketId(StaticRandom.S1000)
                .setAckStatus(TicketCancelAckStatus.CANCELLED)
                .setBookmakerId(9985)
                .setSourceCode(100)
                .build();
    }
}
