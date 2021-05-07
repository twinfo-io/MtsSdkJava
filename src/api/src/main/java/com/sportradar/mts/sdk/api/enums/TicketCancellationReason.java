/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ticket cancellation reason. Used in {@link com.sportradar.mts.sdk.api.TicketCancel}
 */
@SuppressWarnings("java:S115") // Constant names should comply with a naming convention
public enum TicketCancellationReason {

    /**
     * The customer triggered prematch bet cancellation
     */
    @JsonProperty("101")
    CustomerTriggeredPrematch(101),

    /**
     * The timeout triggered bet cancellation
     */
    @JsonProperty("102")
    TimeoutTriggered(102),

    /**
     * The bookmaker backoffice triggered bet cancellation
     */
    @JsonProperty("103")
    BookmakerBackofficeTriggered(103),

    /**
     * The bookmaker technical issue bet cancellation
     */
    @JsonProperty("104")
    BookmakerTechnicalIssue(104),

    /**
     * The exceptional bookmaker triggered bet cancellation
     */
    @JsonProperty("105")
    ExceptionalBookmakerTriggered(105),

    /**
     * The bookmaker cashback promotion cancellation
     */
    @JsonProperty("106")
    BookmakerCashbackPromotionCancellation(106),

    /**
     * The sogei triggered bet cancellation
     */
    @JsonProperty("301")
    SogeiTriggered(301),

    /**
     * The SCCS triggered bet cancellation
     */
    @JsonProperty("302")
    SccsTriggered(302);

    private final int id;

    TicketCancellationReason(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
