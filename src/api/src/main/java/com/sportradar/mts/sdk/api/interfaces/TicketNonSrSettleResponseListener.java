/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketNonSrSettle;
import com.sportradar.mts.sdk.api.TicketNonSrSettleResponse;

/**
 * Ticket non-Sportradar response listener
 */
public interface TicketNonSrSettleResponseListener extends PublishResultListener<TicketNonSrSettle>, TicketResponseTimeoutListener<TicketNonSrSettle> {
    /**
     * Triggered when ticket non-Sportradar response is received from MTS system
     *
     * @param ticketNonSrSettleResponse - ticket non-Sportradar response
     */
    void responseReceived(TicketNonSrSettleResponse ticketNonSrSettleResponse);
}
