/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.BetFreeStake;

/**
 * Type of BetFreeStake. Used in {@link BetFreeStake}
 */
public enum BetFreeStakeType {

    /**
     * Total
     */
    @JsonProperty("total")
    TOTAL,

    /**
     * Unit
     */
    @JsonProperty("unit")
    UNIT,
}
