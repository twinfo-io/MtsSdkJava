/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.BetBonus;
import com.sportradar.mts.sdk.api.enums.BetBonusDescription;
import com.sportradar.mts.sdk.api.enums.BetBonusMode;
import com.sportradar.mts.sdk.api.enums.BetBonusPaidAs;
import com.sportradar.mts.sdk.api.enums.BetBonusType;

/**
 * Implementation of BetBonus interface
 */
public class BetBonusImpl implements BetBonus {

    private final long value;
    private final BetBonusMode betBonusMode;
    private final BetBonusType betBonusType;
    private final BetBonusDescription betBonusDescription;
    private final BetBonusPaidAs betBonusPaidAs;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BetBonusImpl(@JsonProperty("value") long value,
                        @JsonProperty("mode") BetBonusMode betBonusMode,
                        @JsonProperty("type") BetBonusType betBonusType,
                        @JsonProperty("description") BetBonusDescription betBonusDescription,
                        @JsonProperty("paidAs") BetBonusPaidAs betBonusPaidAs)
    {
        Preconditions.checkArgument(value > 0);
        this.value = value;
        this.betBonusMode = betBonusMode;
        this.betBonusType = betBonusType;
        this.betBonusDescription = betBonusDescription;
        this.betBonusPaidAs = betBonusPaidAs;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public BetBonusType getType() {
        return betBonusType;
    }

    @Override
    public BetBonusMode getMode() {
        return betBonusMode;
    }

    @Override
    public BetBonusDescription getDescription() {
        return betBonusDescription;
    }

    @Override
    public BetBonusPaidAs getPaidAs() {
        return betBonusPaidAs;
    }

    @Override
    public String toString() {
        return "BetBonusImpl{" +
                "value=" + value +
                ", mode=" + betBonusMode +
                ", type=" + betBonusType +
                ", description=" + betBonusDescription +
                ", paidAs=" + betBonusPaidAs +
                '}';
    }
}
