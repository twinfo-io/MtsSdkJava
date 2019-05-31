/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.StakeType;

/**
 * Bet Stake
 */
public interface Stake {

    /**
     * Gets the value of the bet bonus
     * Mandatory
     * @return bet bonus value
     */
    long getValue();

    /**
     * Gets the type of the stake
     * @return type of stake
     */
    StakeType getType();
}
