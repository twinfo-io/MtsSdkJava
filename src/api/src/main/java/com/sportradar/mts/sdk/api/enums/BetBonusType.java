/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.BetBonus;

/**
 * Type of BetBonus. Used in {@link BetBonus}
 */
public enum BetBonusType {

    /**
     * Total
     */
    @JsonProperty("total")
    TOTAL
}
