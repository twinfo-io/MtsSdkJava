/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.TicketCashoutResponse;

/**
 * Ticket cashout response listener
 */
public interface TicketCashoutResponseListener extends PublishResultListener<TicketCashout>, TicketResponseTimeoutListener<TicketCashout> {
    /**
     * Triggered when ticket cashout response is received from MTS system
     *
     * @param ticketCashoutResponse - ticket cashout response
     */
    void responseReceived(TicketCashoutResponse ticketCashoutResponse);
}
