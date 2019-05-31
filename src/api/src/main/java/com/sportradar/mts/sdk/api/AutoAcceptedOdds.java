/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;


public interface AutoAcceptedOdds {
    /**
     * Selection index from 'ticket.selections' array (zero based)
     *
     */
    Integer getSelectionIndex();
    /**
     * Odds with which the ticket was placed
     *
     */
    Integer getRequestedOdds();
    /**
     * Odds with which the ticket was accepted
     *
     */
    Integer getUsedOdds();
}
