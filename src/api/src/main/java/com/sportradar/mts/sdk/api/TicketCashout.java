/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import java.util.List;

/**
 * A representation of the TicketCashout message
 */
public interface TicketCashout extends SdkTicket {

    /**
     * Returns the parent bookmaker id
     * @return - the parent bookmaker id
     */
    int getBookmakerId();

    /**
     * Returns the cashout stake of the assigned ticket
     * @return - the cashout stake of the assigned ticket
     */
    Long getCashoutStake();

    /**
     * Cashout percent. Quantity multiplied by 10_000 and rounded to a long value. Percent of ticket to cashout.
     * @return percent of ticket to cashout
     */
    Integer getCashoutPercent();

    /**
     * Array of (betId, cashoutStake) pairs, if performing partial cashout. Applicable only if ticket-level cashout stake is null.
     * @return array of betId, cashoutStake pairs
     */
    List<BetCashout> getBetCashouts();
}
