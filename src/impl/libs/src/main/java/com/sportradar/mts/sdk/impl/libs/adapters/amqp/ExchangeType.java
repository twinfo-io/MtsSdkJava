/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ExchangeType {
    DIRECT,
    FANOUT,
    TOPIC;

    private static final Map<String, ExchangeType> lookup;

    static {
        Map<String, ExchangeType> lookupTmp = new HashMap<>();

        for (ExchangeType mqType : EnumSet.allOf(ExchangeType.class)) {
            lookupTmp.put(mqType.toString().trim().toLowerCase(), mqType);
        }

        lookup = lookupTmp;
    }

    public static ExchangeType fromString(String input) {
        if (input == null) {
            return null;
        }
        return lookup.get(input.trim().toLowerCase());
    }
}
