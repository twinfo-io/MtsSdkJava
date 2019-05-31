/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Possible values for type of stake
 */
public enum StakeType {

    /**
     * Total
     */
    @JsonProperty("total")
    TOTAL,
    /**
     * Unit
     */
    @JsonProperty("unit")
    UNIT
}
