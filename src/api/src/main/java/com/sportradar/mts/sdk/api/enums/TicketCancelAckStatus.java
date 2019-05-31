/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ticket cancel acknowledgement status
 */
public enum TicketCancelAckStatus {

    /**
     * Cancelled
     */
    @JsonProperty("cancelled")
    CANCELLED,
    /**
     * Not cancelled
     */
    @JsonProperty("not_cancelled")
    NOT_CANCELLED,
}
