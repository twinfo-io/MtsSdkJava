/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Possible sender channel values
 */
public enum SenderChannel {
    /**
     * Internet
     */
    @JsonProperty("Internet")
    INTERNET,

    /**
     * Retail
     */
    @JsonProperty("Retail")
    RETAIL,

    /**
     * Terminals
     */
    @JsonProperty("Terminal")
    TERMINAL,

    /**
     * Mobile
     */
    @JsonProperty("Mobile")
    MOBILE,

    /**
     * SMS
     */
    @JsonProperty("SMS")
    SMS,

    /**
     * CallCentre
     */
    @JsonProperty("CallCentre")
    CALLCENTRE,

    /**
     * TvApp
     */
    @JsonProperty("TvApp")
    TVAPP,

    /**
     * Agent
     */
    @JsonProperty("Agent")
    AGENT
}
