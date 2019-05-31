/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ticket acceptance status
 */
public enum TicketAcceptance {

    /**
     * Accepted
     */
    @JsonProperty("accepted")
    ACCEPTED,
    /**
     * Rejected
     */
    @JsonProperty("rejected")
    REJECTED
}
