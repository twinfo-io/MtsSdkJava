/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.TicketCancellationReason;

import java.util.List;

/**
 * Object that is send to MTS to cancel ticket
 */
public interface TicketCancel extends SdkTicket {

    /**
     * Gets bookmaker id
     *
     * @return bookmaker id
     */
    int getBookmakerId();

    /**
     * Gets cancellation code
     *
     * @return cancellation reason
     */
    TicketCancellationReason getCode();

    /**
     * Cancel percent. Quantity multiplied by 10_000 and rounded to a long value. Percent of ticket to cancel.
     * @return percent of ticket to cancel
     */
    Integer getCancelPercent();

    /**
     * Array of (betId, cancelPercent) pairs, if performing partial cancel. Applicable only if ticket-level cancel percent is null.
     * @return array of betId, cancelPercent pairs
     */
    List<BetCancel> getBetCancels();
}
