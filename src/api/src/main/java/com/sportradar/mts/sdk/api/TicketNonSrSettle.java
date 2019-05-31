/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import java.util.Date;

public interface TicketNonSrSettle extends SdkTicket {

    /**
     * Get bookmaker id
     * @return the bookmaker id
     */
    int getBookmakerId();

    /**
     * Timestamp of non-Sportradar settle placement
     */
    Date getTimestampUtc();

    /**
     * Get settle ticket id
     * @return the ticket id
     */
    String getTicketId();

    /**
     * Get non-Sportradar settle stake in same currency as original ticket.
     */
     Long getNonSrSettleStake();
}
