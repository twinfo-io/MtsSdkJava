/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.example.listeners;

import com.sportradar.example.TicketBuilderHelper;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.TicketCancelAck;
import com.sportradar.mts.sdk.api.TicketCancelResponse;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelAckSender;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketCancelResponseHandler extends PublishResultHandler<TicketCancel> implements TicketCancelResponseListener {

    private static final Logger logger = LoggerFactory.getLogger(TicketCancelResponseHandler.class);

    private final TicketCancelAckSender ticketCancelAckSender;
    private final BuilderFactory builderFactory;

    public TicketCancelResponseHandler(TicketCancelAckSender ticketCancelAckSender,
                                       BuilderFactory builderFactory) {
        this.ticketCancelAckSender = ticketCancelAckSender;
        this.builderFactory = builderFactory;
    }

    public void responseReceived(TicketCancelResponse ticketCancelResponse) {
        logger.info("ticket {} was {}", ticketCancelResponse.getTicketId(), ticketCancelResponse.getStatus());

        // required only if 'explicit acking' is enabled in MTS admin
        TicketCancelAck ticketCancelAck = new TicketBuilderHelper(builderFactory).getTicketCancelAck(ticketCancelResponse);
        ticketCancelAckSender.send(ticketCancelAck);
    }
}
