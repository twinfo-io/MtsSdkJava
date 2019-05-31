/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces.customBet;

import com.sportradar.mts.sdk.api.rest.URN;

import java.util.List;

/**
 * Provides an available selections for a particular event
 */
public interface AvailableSelections {

    /**
     * @return the {@link URN} of the event
     */
    URN getEvent();

    /**
     * Returns the list of markets for this event
     *
     * @return the list of markets for this event
     */
    List<Market> getMarkets();
}
