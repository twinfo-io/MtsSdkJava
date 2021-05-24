/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.example.example;

import com.sportradar.example.TicketBuilderHelper;
import com.sportradar.example.listeners.TicketAckHandler;
import com.sportradar.example.listeners.TicketCancelAckHandler;
import com.sportradar.example.listeners.TicketCancelResponseHandler;
import com.sportradar.example.listeners.TicketResponseHandler;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.interfaces.*;
import com.sportradar.mts.sdk.app.MtsSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic example of creating and sending ticket
 */
public final class Basic {
    private static final Logger logger = LoggerFactory.getLogger(Basic.class);

    private Basic() { throw new IllegalStateException("Basic class"); }

    public static void run()
    {
        SdkConfiguration config = MtsSdk.getConfiguration();
        logger.info("Configuration set");
        MtsSdkApi mtsSdk = new MtsSdk(config);
        mtsSdk.open();
        logger.info("Connection opened");
        TicketAckSender ticketAckSender = mtsSdk.getTicketAckSender(new TicketAckHandler());
        TicketCancelAckSender ticketCancelAckSender = mtsSdk.getTicketCancelAckSender(new TicketCancelAckHandler());
        TicketCancelSender ticketCancelSender = mtsSdk.getTicketCancelSender(new TicketCancelResponseHandler(ticketCancelAckSender, mtsSdk.getBuilderFactory()));
        TicketResponseHandler ticketResponseHandler = new TicketResponseHandler(ticketCancelSender, ticketAckSender, mtsSdk.getBuilderFactory());
        TicketSender ticketSender = mtsSdk.getTicketSender(ticketResponseHandler);

        Ticket ticket = new TicketBuilderHelper(mtsSdk.getBuilderFactory()).getTicket();
        logger.info("Ticket created");
        //Notice: there are two ways of sending tickets to MTS (non-blocking is recommended)
        //send non-blocking (the TicketResult will be handled in TicketResponseHandler)
        ticketSender.send(ticket);
        logger.info("Ticket sent");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            mtsSdk.close();
        }
    }
}
