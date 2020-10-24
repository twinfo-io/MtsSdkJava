/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.TicketReofferCancel;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.impl.builders.TicketReofferCancelBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.TicketReofferCancelSender;
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

public class TicketReofferCancelHandlerImplTest {

    private AmqpPublisher publisher;
    private TicketReofferCancel ticketReofferCancel;
    private String routingKey;
    private String correlationId;
    private TicketReofferCancelSender reofferCancelSender;
    private byte[] msg;
    private SdkLogger sdkLogger;
    private BuilderFactory builderFactory;

    @Before
    public void setUp() {
        sdkLogger = mock(SdkLogger.class);
        ExecutorService executorService = mock(ExecutorService.class);
        builderFactory = new SdkHelper().getBuilderFactory();

        routingKey = "cancel.reoffer";
        publisher = mock(AmqpPublisher.class);
        reofferCancelSender = new TicketReofferCancelHandlerImpl(publisher, routingKey, executorService, 40, sdkLogger);
        ticketReofferCancel = getTicketReofferCancel();
        msg = JsonUtils.serialize(MtsDtoMapper.map(ticketReofferCancel));

        // reofferCancelSender gets the correlationId: "cancel_reoffer:" + ticketReofferCancel.getTicketId()
        // due to this fact the same is done here to ensure proper testing conditions
        correlationId = ticketReofferCancel.getCorrelationId();

        when(publisher.isOpen()).thenReturn(true);
    }

    @Test
    public void newBuilderTest() {
        assertThat(builderFactory.createTicketReofferCancelBuilder(), is(notNullValue()));
        assertThat(builderFactory.createTicketReofferCancelBuilder(), instanceOf(TicketReofferCancelBuilderImpl.class));
    }

    @Test
    public void sendAsyncTest() {
        reofferCancelSender.open();
        reofferCancelSender.send(ticketReofferCancel);

        verify(publisher, times(1)).publishAsync(ticketReofferCancel.getTicketId(), msg, correlationId, routingKey, routingKey);

        String ticketString = JsonUtils.serializeAsString(MtsDtoMapper.map(ticketReofferCancel));
        verify(sdkLogger, times(1)).logSendMessage(ticketString);
    }


    private TicketReofferCancel getTicketReofferCancel() {
        return builderFactory.createTicketReofferCancelBuilder()
                .setTicketId(StaticRandom.S1000)
                .setBookmakerId(9985)
                .build();
    }
}
