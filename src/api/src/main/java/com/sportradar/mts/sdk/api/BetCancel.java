/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import java.io.Serializable;

/**
 * Defines a contract for bet-level cashout
 */
public interface BetCancel extends Serializable {

    /**
     * Gets the id of the bet
     * @return betId
     */
    String getBetId();

    /**
     * Cancel percent. Quantity multiplied by 10_000 and rounded to a long value. Percent of bet to cancel.
     * @return percent of bet to cancel
     */
    Integer getCancelPercent();
}
