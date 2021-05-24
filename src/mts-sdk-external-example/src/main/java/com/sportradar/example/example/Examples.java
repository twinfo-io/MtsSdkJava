/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.example.example;

import com.sportradar.example.listeners.TicketAckHandler;
import com.sportradar.example.listeners.TicketCancelAckHandler;
import com.sportradar.example.listeners.TicketCancelResponseHandler;
import com.sportradar.example.listeners.TicketResponseHandler;
import com.sportradar.example.tickets.TicketExamples;
import com.sportradar.mts.sdk.api.interfaces.*;
import com.sportradar.mts.sdk.app.MtsSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MTS integration examples
 */
public final class Examples {
    private static final Logger logger = LoggerFactory.getLogger(Examples.class);

    private Examples() { throw new IllegalStateException("Examples class"); }

    public static void run()
    {
        SdkConfiguration config = MtsSdk.getConfiguration();
        MtsSdkApi mtsSdk = new MtsSdk(config);
        mtsSdk.open();
        TicketAckSender ticketAckSender = mtsSdk.getTicketAckSender(new TicketAckHandler());
        TicketCancelAckSender ticketCancelAckSender = mtsSdk.getTicketCancelAckSender(new TicketCancelAckHandler());
        TicketCancelSender ticketCancelSender = mtsSdk.getTicketCancelSender(new TicketCancelResponseHandler(ticketCancelAckSender, mtsSdk.getBuilderFactory()));
        TicketSender ticketSender = mtsSdk.getTicketSender(new TicketResponseHandler(ticketCancelSender, ticketAckSender, mtsSdk.getBuilderFactory()));

        TicketExamples ticketExamples = new TicketExamples(mtsSdk.getBuilderFactory());
        logger.info("Example 1");
        ticketSender.send(ticketExamples.example1());
        logger.info("Example 2");
        ticketSender.send(ticketExamples.example2());
        logger.info("Example 3");
        ticketSender.send(ticketExamples.example3());
        logger.info("Example 4");
        ticketSender.send(ticketExamples.example4());
        logger.info("Example 5");
        ticketSender.send(ticketExamples.example5());
        logger.info("Example 6");
        ticketSender.send(ticketExamples.example6());
        logger.info("Example 7");
        ticketSender.send(ticketExamples.example7());
        logger.info("Example 8");
        ticketSender.send(ticketExamples.example8());
        logger.info("Example 9");
        ticketSender.send(ticketExamples.example9());
        logger.info("Example 10");
        ticketSender.send(ticketExamples.example10());
        logger.info("Example 11");
        ticketSender.send(ticketExamples.example11());
        logger.info("Example 12");
        ticketSender.send(ticketExamples.example12());
        logger.info("Example 13");
        ticketSender.send(ticketExamples.example13());
        logger.info("Example 14");
        ticketSender.send(ticketExamples.example14());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.info("interrupted while sleeping");
            Thread.currentThread().interrupt();
        } finally {
            mtsSdk.close();
        }
    }
}
