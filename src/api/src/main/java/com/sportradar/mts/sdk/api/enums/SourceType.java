package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SourceType {
    /**
     * SHOP
     */
    @JsonProperty("SHOP")
    SHOP,
    /**
     * Retail
     */
    @JsonProperty("TERMINAL")
    TERMINAL,
    /**
     * CUSTOMER
     */
    @JsonProperty("CUSTOMER")
    CUSTOMER,
    /**
     * SUB_BOOKMAKER
     */
    @JsonProperty("SUB_BOOKMAKER")
    SUB_BOOKMAKER,
    /**
     * BOOKMAKER
     */
    @JsonProperty("BOOKMAKER")
    BOOKMAKER,
    /**
     * DISTRIBUTION_CHANNEL
     */
    @JsonProperty("DISTRIBUTION_CHANNEL")
    DISTRIBUTION_CHANNEL
}
