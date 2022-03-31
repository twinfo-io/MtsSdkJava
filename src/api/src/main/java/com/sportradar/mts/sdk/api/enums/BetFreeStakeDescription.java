/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.BetFreeStake;

/**
 * Description of BetFreeStake. Used in {@link BetFreeStake}
 */
public enum BetFreeStakeDescription {

    /**
     * Free Bet
     */
    @JsonProperty("freeBet")
    FREE_BET,

    /**
     * Partial Free Bet
     */
    @JsonProperty("partialFreeBet")
    PARTIAL_FREE_BET,

    /**
     * Rollover
     */
    @JsonProperty("rollover")
    ROLLOVER,

    /**
     * Money Back
     */
    @JsonProperty("moneyBack")
    MONEY_BACK,

    /**
     * Odds Booster
     */
    @JsonProperty("oddsBooster")
    ODDS_BOOSTER,

    /**
     * other
     */
    @JsonProperty("other")
    OTHER
}
