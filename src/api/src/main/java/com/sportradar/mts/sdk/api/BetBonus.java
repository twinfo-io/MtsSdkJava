/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.BetBonusMode;
import com.sportradar.mts.sdk.api.enums.BetBonusType;

/**
 * Bonus of the bet (optional, default null)
 */
public interface BetBonus {

    /**
     * Gets the value of the bet bonus
     * Mandatory
     * @return bet bonus value
     */
    long getValue();

    /**
     * Gets the type of the bet bonus
     * @return type of bet bonus
     */
    BetBonusType getType();

    /**
     * Gets the mode of the bet bonus
     * @return mode of bet bonus
     */
    BetBonusMode getMode();
}
