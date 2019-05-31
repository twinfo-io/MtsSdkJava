/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.sportradar.mts.sdk.api.SportCcf;

/**
 * Implementation of the user exposed entity {@link SportCcf}
 */
public class SportCcfImpl implements SportCcf {
    /**
     * The sport ID
     */
    private String sportId;

    /**
     * The customer confidence factor for the sport for prematch selections
     */
    private long prematchCcf;

    /**
     * The customer confidence factor for the sport for live selections
     */
    private long liveCcf;

    /**
     * Initializes a new instance of the {@link SportCcfImpl}
     *
     * @param sportId sport ID
     * @param prematchCcf customer confidence factor for the sport for prematch selections
     * @param liveCcf customer confidence factor for the sport for live selections
     */
    public SportCcfImpl(String sportId, long prematchCcf, long liveCcf) {
        this.sportId = sportId;
        this.prematchCcf = prematchCcf;
        this.liveCcf = liveCcf;
    }

    /**
     * Gets sport ID
     *
     * @return sport ID
     */
    @Override
    public String getSportId() {
        return sportId;
    }

    /**
     * Gets customer confidence factor for the sport for prematch selections (factor multiplied by 10000)
     *
     * @return customer confidence factor for the sport for prematch selections
     */
    @Override
    public long getPrematchCcf() {
        return prematchCcf;
    }

    /**
     * Gets customer confidence factor for the sport for live selections (factor multiplied by 10000)
     *
     * @return customer confidence factor for the sport for live selections
     */
    @Override
    public long getLiveCcf() {
        return liveCcf;
    }
}
