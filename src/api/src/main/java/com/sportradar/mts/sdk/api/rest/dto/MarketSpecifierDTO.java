/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest.dto;

import com.google.common.base.Preconditions;
import com.sportradar.mts.api.rest.sportsapi.datamodel.DescSpecifiers;

public class MarketSpecifierDTO {
    private final String name;
    private final String type;

    public MarketSpecifierDTO(DescSpecifiers.Specifier s) {
        Preconditions.checkNotNull(s);
        Preconditions.checkArgument(!s.getType().isEmpty());
        Preconditions.checkArgument(!s.getName().isEmpty());

        type = s.getType();
        name = s.getName();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
