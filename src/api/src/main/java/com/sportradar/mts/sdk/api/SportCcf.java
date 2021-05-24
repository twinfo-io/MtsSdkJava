/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import java.io.Serializable;

/**
 * Object containing CCF per sport response data
 */
public interface SportCcf extends Serializable {

    /**
     * Gets sport ID
     *
     * @return sport ID
     */
    String getSportId();

    /**
     * Gets customer confidence factor for the sport for prematch selections (factor multiplied by 10000)
     *
     * @return customer confidence factor for the sport for prematch selections
     */
    long getPrematchCcf();

    /**
     * Gets customer confidence factor for the sport for live selections (factor multiplied by 10000)
     *
     * @return customer confidence factor for the sport for live selections
     */
    long getLiveCcf();
}
