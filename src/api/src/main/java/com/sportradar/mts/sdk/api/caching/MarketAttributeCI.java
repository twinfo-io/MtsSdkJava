/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.caching;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.rest.dto.MarketAttributeDTO;

public class MarketAttributeCI {
    private final String name;
    private final String description;

    public MarketAttributeCI(MarketAttributeDTO a) {
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
