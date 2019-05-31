/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.TicketCancelResponse;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;

/**
 * {@link TicketCancel} sender
 */
public interface TicketCancelSender extends MessageSender {

    /**
     * Sends the {@link TicketCancel} to the MTS
     *
     * @param ticketCancel ticket cancel to send
     */
    void send(TicketCancel ticketCancel);

    /**
     * Sends the {@link TicketCancel} to the MTS and returns {@link TicketCancelResponse}
     *
     * @param ticketCancel ticketCancel to send
     * @return ticket response
     * @throws ResponseTimeoutException if no response is received in time
     */
    TicketCancelResponse sendBlocking(TicketCancel ticketCancel) throws ResponseTimeoutException;
}
