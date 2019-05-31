/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.TicketCancelResponse;

/**
 * Ticket cancel response listener
 */
public interface TicketCancelResponseListener extends PublishResultListener<TicketCancel>, TicketResponseTimeoutListener<TicketCancel> {

    /**
     * Triggered when ticket cancel response is received from MTS system
     *
     * @param ticketCancelResponse ticket cancel response
     */
    void responseReceived(TicketCancelResponse ticketCancelResponse);
}
