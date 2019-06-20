/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.BetCancel;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

public class BetCancelImpl implements BetCancel {
    private final String betId;
    private final Integer cancelPercent;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BetCancelImpl(@JsonProperty("betId") String betId,
                         @JsonProperty("cancelPercent") Integer cancelPercent) {

        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(betId), "betId is not valid");
        Preconditions.checkArgument(MtsTicketHelper.validatePercent(cancelPercent), "percent not valid");

        this.betId = betId;
        this.cancelPercent = cancelPercent;
    }

    /**
     * Gets the id of the bet
     *
     * @return betId
     */
    @Override
    public String getBetId() {
        return betId;
    }

    /**
     * Cancel percent. Quantity multiplied by 10_000 and rounded to a long value. Percent of bet to cancel.
     *
     * @return percent of bet to cancel
     */
    @Override
    public Integer getCancelPercent() {
        return cancelPercent;
    }
}
