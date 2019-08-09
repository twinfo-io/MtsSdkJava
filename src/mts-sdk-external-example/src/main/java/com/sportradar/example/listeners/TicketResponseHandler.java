/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.example.listeners;

import com.sportradar.example.TicketBuilderHelper;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.TicketAck;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.TicketResponse;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import com.sportradar.mts.sdk.api.interfaces.TicketAckSender;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
import com.sportradar.mts.sdk.api.interfaces.TicketResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketResponseHandler extends PublishResultHandler<Ticket> implements TicketResponseListener {

    private static final Logger logger = LoggerFactory.getLogger(TicketResponseHandler.class);

    private final TicketCancelSender ticketCancelSender;
    private final TicketAckSender ticketAckSender;
    private final BuilderFactory builderFactory;

    public TicketResponseHandler(TicketCancelSender ticketCancelSender,
                                 TicketAckSender ticketAckSender,
                                 BuilderFactory builderFactory) {
        this.ticketCancelSender = ticketCancelSender;
        this.ticketAckSender = ticketAckSender;
        this.builderFactory = builderFactory;
    }

    public void responseReceived(TicketResponse ticketResponse) {
        logger.info("ticket {} was {}", ticketResponse.getTicketId(), ticketResponse.getStatus());

        if(ticketResponse.getStatus() == TicketAcceptance.ACCEPTED) {

            // required only if 'explicit acking' is enabled in MTS admin
            TicketAck ticketAcknowledgment =  new TicketBuilderHelper(builderFactory).getTicketAck(ticketResponse);
            ticketAckSender.send(ticketAcknowledgment);

            //if in some case we want to cancel already accepted ticket
            if (Math.random() > 0.3) {
                TicketCancel ticketCancel = new TicketBuilderHelper(builderFactory).getTicketCancel(ticketResponse.getTicketId());
                //send non-blocking (the TicketCancelResult will we handled in TicketCancelResponseHandler)
                ticketCancelSender.send(ticketCancel);
            }
        }
    }

    public void onTicketResponseTimedOut(Ticket ticket){
        logger.warn("Sending ticket {} timed-out", ticket.getTicketId());
    }
}
