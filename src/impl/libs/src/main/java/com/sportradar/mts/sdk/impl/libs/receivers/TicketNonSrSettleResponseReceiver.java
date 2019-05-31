/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.TicketNonSrSettleResponse;

/**
 * Handler for the messages of type {@link com.sportradar.mts.sdk.api.TicketNonSrSettle}
 */
public interface TicketNonSrSettleResponseReceiver {

    /**
     * An event that is invoked when the {@link TicketNonSrSettleResponse} message is received
     *
     * @param ticketNonSrSettleResponse - the {@link TicketNonSrSettleResponse} message data
     */
    void setTicketNonSrSettleResponse(TicketNonSrSettleResponse ticketNonSrSettleResponse);
}
