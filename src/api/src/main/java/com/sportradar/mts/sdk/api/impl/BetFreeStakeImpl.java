/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.BetFreeStake;
import com.sportradar.mts.sdk.api.enums.BetFreeStakeDescription;
import com.sportradar.mts.sdk.api.enums.BetFreeStakePaidAs;
import com.sportradar.mts.sdk.api.enums.BetFreeStakeType;

/**
 * Implementation of BetFreeStake interface
 */
public class BetFreeStakeImpl implements BetFreeStake {

    private final long value;
    private final BetFreeStakeType betFreeStakeType;
    private final BetFreeStakeDescription betFreeStakeDescription;
    private final BetFreeStakePaidAs betFreeStakePaidAs;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BetFreeStakeImpl(@JsonProperty("value") long value,
                            @JsonProperty("type") BetFreeStakeType betFreeStakeType,
                            @JsonProperty("description") BetFreeStakeDescription betFreeStakeDescription,
                            @JsonProperty("paidAs") BetFreeStakePaidAs betFreeStakePaidAs)
    {
        Preconditions.checkArgument(value > 0);
        this.value = value;
        this.betFreeStakeType = betFreeStakeType;
        this.betFreeStakeDescription = betFreeStakeDescription;
        this.betFreeStakePaidAs = betFreeStakePaidAs;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public BetFreeStakeType getType() {
        return betFreeStakeType;
    }

    @Override
    public BetFreeStakeDescription getDescription() {
        return betFreeStakeDescription;
    }

    @Override
    public BetFreeStakePaidAs getPaidAs() {
        return betFreeStakePaidAs;
    }

    @Override
    public String toString() {
        return "BetFreeStakeImpl{" +
                "value=" + value +
                ", type=" + betFreeStakeType +
                ", description=" + betFreeStakeDescription +
                ", paidAs=" + betFreeStakePaidAs +
                '}';
    }
}
