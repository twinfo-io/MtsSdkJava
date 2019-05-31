/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.TicketResponse;

/**
 * Ticket response listener
 */
public interface TicketResponseListener extends PublishResultListener<Ticket>, TicketResponseTimeoutListener<Ticket> {

    /**
     * Triggered when ticket response is received from MTS system
     *
     * @param ticketResponse ticket response
     */
    void responseReceived(TicketResponse ticketResponse);
}
