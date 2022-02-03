/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.BetBonusDescription;
import com.sportradar.mts.sdk.api.enums.BetBonusMode;
import com.sportradar.mts.sdk.api.enums.BetBonusPaidAs;
import com.sportradar.mts.sdk.api.enums.BetBonusType;

import java.io.Serializable;

/**
 * Bonus of the bet (optional, default null)
 */
public interface BetBonus extends Serializable {

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

    /**
     * Gets the description of the bet bonus
     * @return description of bet bonus
     */
    BetBonusDescription getDescription();

    /**
     * Gets the description of the bet bonus payment type
     * @return description of bet bonus payment type
     */
    BetBonusPaidAs getPaidAs();


}
