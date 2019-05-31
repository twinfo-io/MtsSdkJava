/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.TicketCashoutResponse;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;

/**
 * {@link TicketCashout} sender
 */
public interface TicketCashoutSender extends MessageSender {

    /**
     * Sends the {@link TicketCashout} to the MTS
     *
     * @param ticketData ticket acknowledgment to send
     */
    void send(TicketCashout ticketData);

    /**
     * Publishes a new {@link TicketCashout} message and waits for a response with the specified timeout
     *
     * @param ticketCashout - the data from which the message will be built
     * @return - the cashout response from the MTS
     * @throws ResponseTimeoutException - if the max timeout for the response has exceeded
     */
    TicketCashoutResponse sendBlocking(TicketCashout ticketCashout) throws ResponseTimeoutException;
}
