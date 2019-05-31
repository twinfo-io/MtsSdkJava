/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;


import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.BetCashout;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

public class BetCashoutImpl implements BetCashout {
    private final String betId;
    private final long cashoutStake;
    private final Integer cashoutPercent;

    public BetCashoutImpl(String betId, long cashoutStake, Integer cashoutPercent) {

        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(betId), "betId is not valid");
        Preconditions.checkArgument(cashoutStake > 0, "stake not valid");
        Preconditions.checkArgument(MtsTicketHelper.validatePercent(cashoutPercent), "percent not valid");

        this.betId = betId;
        this.cashoutStake = cashoutStake;
        this.cashoutPercent = cashoutPercent;
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
     * Returns the cashout stake of the assigned bet
     *
     * @return - the cashout stake of the assigned bet
     */
    @Override
    public long getCashoutStake() {
        return cashoutStake;
    }

    /**
     * Cashout percent. Quantity multiplied by 10_000 and rounded to a integer value. Percent of bet to cashout.
     *
     * @return percent of bet to cashout
     */
    @Override
    public Integer getCashoutPercent() {
        return cashoutPercent;
    }
}
