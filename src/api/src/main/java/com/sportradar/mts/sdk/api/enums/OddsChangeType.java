/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.Ticket;

/**
 * Type of OddsChange. Used in {@link Ticket}
 */
public enum OddsChangeType {

    /**
     * None (default)
     */
    @JsonProperty("none")
    NONE,

    /**
     * Any
     */
    @JsonProperty("any")
    ANY,

    /**
     * Higher
     */
    @JsonProperty("higher")
    HIGHER
}
