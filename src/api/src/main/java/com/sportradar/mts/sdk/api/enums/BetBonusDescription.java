/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.BetBonus;

/**
 * Description of BetBonus. Used in {@link BetBonus}
 */
public enum BetBonusDescription {

    /**
     * Accumulator Bonus
     */
    @JsonProperty("accaBonus")
    ACCUMULATOR_BONUS,

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
