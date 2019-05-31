/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.sportradar.mts.sdk.api.RejectionInfo;

/**
 * A basic {@link RejectionInfo} implementation
 */
public class RejectionInfoImpl implements RejectionInfo {
    private final String id;
    private final String eventId;
    private final Integer odds;

    public RejectionInfoImpl(String id, String eventId, Integer odds) {
        this.id = id;
        this.eventId = eventId;
        this.odds = odds;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public Integer getOdds() {
        return odds;
    }
}
