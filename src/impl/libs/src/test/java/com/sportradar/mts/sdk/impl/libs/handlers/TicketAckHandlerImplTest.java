/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.TicketAck;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.TicketAckStatus;
import com.sportradar.mts.sdk.api.impl.builders.TicketAckBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.TicketAckSender;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.StaticRandom;
import com.sportradar.mts.sdk.impl.libs.SdkHelper;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisher;
import com.sportradar.mts.sdk.impl.libs.logging.SdkLogger;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class TicketAckHandlerImplTest {

    private AmqpPublisher publisher;
    private TicketAck ticketAcknowledgment;
    private String routingKey;
    private String correlationId;
    private TicketAckSender ackSender;
    private byte[] msg;
    private SdkLogger sdkLogger;
    private BuilderFactory builderFactory;

    @Before
    public void setUp() {
        sdkLogger = mock(SdkLogger.class);
        ExecutorService executorService = mock(ExecutorService.class);
        builderFactory = new SdkHelper().getBuilderFactory();

        routingKey = "ack.ticket";
        publisher = mock(AmqpPublisher.class);
        ackSender = new TicketAckHandlerImpl(publisher, routingKey, executorService, 40, sdkLogger);
        ticketAcknowledgment = getTicketAcknowledgment();
        msg = JsonUtils.serialize(MtsDtoMapper.map(ticketAcknowledgment));

        // ackSender gets the correlationId: "ticket_ack:" + ticketAcknowledgment.getTicketId()
        // due to this fact the same is done here to ensure proper testing conditions
        correlationId = ticketAcknowledgment.getCorrelationId();

        when(publisher.isOpen()).thenReturn(true);
    }

    @Test
    public void newBuilderTest() {
        assertThat(builderFactory.createTicketAckBuilder(), is(notNullValue()));
        assertThat(builderFactory.createTicketAckBuilder(), instanceOf(TicketAckBuilderImpl.class));
    }

    @Test
    public void sendAsyncTest() {
        ackSender.open();
        ackSender.send(ticketAcknowledgment);

        verify(publisher, times(1)).publishAsync(msg, correlationId, routingKey, routingKey);

        String ticketAckString = JsonUtils.serializeAsString(MtsDtoMapper.map(ticketAcknowledgment));
        verify(sdkLogger, times(1)).logSendMessage(ticketAckString);
    }


    private TicketAck getTicketAcknowledgment() {
        return builderFactory.createTicketAckBuilder()
                .setTicketId(StaticRandom.S1000)
                .setAckStatus(TicketAckStatus.ACCEPTED)
                .setBookmakerId(9985)
                .setSourceCode(100)
                .build();
    }
}
