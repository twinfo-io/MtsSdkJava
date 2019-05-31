/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.caching;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.rest.URN;
import com.sportradar.mts.sdk.api.rest.dto.MarketMappingDTO;

import java.util.List;
import java.util.stream.Collectors;

public class MarketMappingCI {
    private final int marketTypeId;
    private final Integer marketSubTypeId;
    private final int producerId;
    private final URN sportId;
    private final String sovTemplate;
    private final String validFor;
    private final List<OutcomeMappingCI> outcomeMappings;

    public MarketMappingCI(MarketMappingDTO mm) {
        Preconditions.checkNotNull(mm);
        Preconditions.checkArgument(mm.getProducerId() > 0);
        Preconditions.checkArgument(mm.getMarketTypeId() > 0);

        producerId = mm.getProducerId();
        sportId = mm.getSportId();
        marketTypeId = mm.getMarketTypeId();
        marketSubTypeId = mm.getMarketSubTypeId();
        sovTemplate = mm.getSovTemplate();
        validFor = mm.getValidFor();
        outcomeMappings = mm.getOutcomeMappings() == null ? null :
                mm.getOutcomeMappings().stream().map(OutcomeMappingCI::new).collect(Collectors.toList());
    }

    public int getMarketTypeId() {
        return marketTypeId;
    }

    public Integer getMarketSubTypeId() {
        return marketSubTypeId;
    }

    public int getProducerId() {
        return producerId;
    }

    public URN getSportId() {
        return sportId;
    }

    public String getSovTemplate() {
        return sovTemplate;
    }

    public String getValidFor() { return validFor; }

    public List<OutcomeMappingCI> getOutcomeMappings() {
        return outcomeMappings;
    }

    @SuppressWarnings("EmptyMethod")
    public void merge(MarketMappingDTO mm)
    {
        // this type has no translatable properties for now, so merge is not required
        // and the method is only defined for consistency
    }
}
