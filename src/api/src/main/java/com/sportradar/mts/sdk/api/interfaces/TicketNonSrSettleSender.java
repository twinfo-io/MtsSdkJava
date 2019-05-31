/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketNonSrSettle;
import com.sportradar.mts.sdk.api.TicketNonSrSettleResponse;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;

/**
 * {@link TicketNonSrSettle} sender
 */
public interface TicketNonSrSettleSender extends MessageSender {
    /**
     * Sends the {@link TicketNonSrSettle} to the MTS
     *
     * @param ticketData ticket acknowledgment to send
     */
    void send(TicketNonSrSettle ticketData);

    /**
     * Publishes a new {@link TicketNonSrSettle} message and waits for a response with the specified timeout
     *
     * @param ticketNonSrSettle - the data from which the message will be built
     * @return - the non-Sportradar response from the MTS
     * @throws ResponseTimeoutException - if the max timeout for the response has exceeded
     */
    TicketNonSrSettleResponse sendBlocking(TicketNonSrSettle ticketNonSrSettle) throws ResponseTimeoutException;
}
