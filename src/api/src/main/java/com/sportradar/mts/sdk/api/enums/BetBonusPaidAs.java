/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.BetBonus;

/**
 * Description of BetBonus payment type. Used in {@link BetBonus}
 */
public enum BetBonusPaidAs {

    /**
     * cash
     */
    @JsonProperty("cash")
    CASH,

    /**
     * Free Bet
     */
    @JsonProperty("freeBet")
    FREE_BET
}
