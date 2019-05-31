/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.TicketResponse;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;

/**
 * {@link Ticket} sender
 */
public interface TicketSender extends MessageSender {

    /**
     * Sends the {@link Ticket} to the MTS
     *
     * @param ticket ticket to send
     */
    void send(Ticket ticket);

    /**
     * Sends the {@link Ticket} to the MTS and returns {@link TicketResponse}
     *
     * @param ticket ticket to send
     * @return ticket response
     * @throws ResponseTimeoutException if no response is received in time
     */
    TicketResponse sendBlocking(Ticket ticket) throws ResponseTimeoutException;
}
