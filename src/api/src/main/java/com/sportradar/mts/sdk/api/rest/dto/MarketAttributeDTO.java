/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest.dto;

import com.google.common.base.Preconditions;
import com.sportradar.mts.api.rest.sportsapi.datamodel.Attributes;

public class MarketAttributeDTO {
    private final String name;
    private final String description;

    public MarketAttributeDTO(Attributes.Attribute a) {
        Preconditions.checkNotNull(a);

        name = a.getName();
        description = a.getDescription();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
