/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.BetFreeStakeDescription;
import com.sportradar.mts.sdk.api.enums.BetFreeStakePaidAs;
import com.sportradar.mts.sdk.api.enums.BetFreeStakeType;

import java.io.Serializable;

/**
 * FreeStake of the bet (optional, default null)
 */
public interface BetFreeStake extends Serializable {

    /**
     * Gets the value of the bet free stake
     * Mandatory
     * @return bet free stake value
     */
    long getValue();

    /**
     * Gets the type of the bet free stake
     * @return type of bet free stake
     */
    BetFreeStakeType getType();

    /**
     * Gets the description of the bet free stake
     * @return description of bet free stake
     */
    BetFreeStakeDescription getDescription();

    /**
     * Gets the description of the bet free stake payment type
     * @return description of bet free stake payment type
     */
    BetFreeStakePaidAs getPaidAs();


}
