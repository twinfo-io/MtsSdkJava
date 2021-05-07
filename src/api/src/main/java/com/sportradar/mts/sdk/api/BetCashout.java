/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import java.io.Serializable;

/**
 * Defines a contract for bet-level cashout
 */
public interface BetCashout extends Serializable {

    /**
     * Gets the id of the bet
     * @return betId
     */
    String getBetId();

    /**
     * Returns the cashout stake of the assigned bet
     * @return - the cashout stake of the assigned bet
     */
    long getCashoutStake();

    /**
     * Cashout percent. Quantity multiplied by 10_000 and rounded to a integer value. Percent of bet to cashout.
     * @return percent of bet to cashout
     */
    Integer getCashoutPercent();
}
