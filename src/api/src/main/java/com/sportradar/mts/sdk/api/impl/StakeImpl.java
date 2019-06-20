/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.Stake;
import com.sportradar.mts.sdk.api.enums.StakeType;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementation of Stake
 */
public class StakeImpl implements Stake {

    private final long value;
    private final StakeType type;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public StakeImpl(@JsonProperty("value") long value,
                     @JsonProperty("type") StakeType type)
    {
        checkArgument(value > 0, "value is missing");

        this.value = value;
        this.type = type;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public StakeType getType() {
        return type;
    }
}
