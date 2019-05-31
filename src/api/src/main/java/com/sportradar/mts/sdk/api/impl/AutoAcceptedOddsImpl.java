/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;


import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.AutoAcceptedOdds;

public class AutoAcceptedOddsImpl implements AutoAcceptedOdds {

    private final Integer selectionIndex;
    private final Integer requestedOdds;
    private final Integer usedOdds;

    /**
     * Selection index from 'ticket.selections' array (zero based)
     */
    @Override
    public Integer getSelectionIndex() {
        return selectionIndex;
    }

    /**
     * Odds with which the ticket was placed
     */
    @Override
    public Integer getRequestedOdds() {
        return requestedOdds;
    }

    /**
     * Odds with which the ticket was accepted
     */
    @Override
    public Integer getUsedOdds() {
        return usedOdds;
    }

    public AutoAcceptedOddsImpl(com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.AutoAcceptedOdd autoAcceptedOdd) {
        Preconditions.checkNotNull(autoAcceptedOdd, "autoAcceptedOdd can not be null");

        this.selectionIndex = autoAcceptedOdd.getSelectionIndex();
        this.requestedOdds = autoAcceptedOdd.getRequestedOdds();
        this.usedOdds = autoAcceptedOdd.getUsedOdds();
    }

    @Override
    public String toString() {
        return "AutoAcceptedOddsImpl{" +
                "selectionIndex='" + selectionIndex + '\'' +
                ", requestedOdds=" + requestedOdds +
                ", usedOdds=" + usedOdds +
                '}';
    }
}
