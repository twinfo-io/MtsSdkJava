/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

/**
 * Connection change listener
 */
public interface ConnectionChangeListener {

    /**
     * Triggered when the change regarding connection to rabbit server occurs
     * @param change connection change
     */
    void connectionChanged(ConnectionChange change);
}
