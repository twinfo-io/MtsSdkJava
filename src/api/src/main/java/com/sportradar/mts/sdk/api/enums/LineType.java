/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.Selection;

/**
 * Line type. Used in {@link Selection}
 */
public enum LineType {

    /**
     * Prematch
     */
    @JsonProperty("prematch")
    PREMATCH,
    /**
     * Live
     */
    @JsonProperty("live")
    LIVE
}
