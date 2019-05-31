/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest.dto;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.mts.api.rest.sportsapi.datamodel.Mappings;
import com.sportradar.mts.sdk.api.rest.URN;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

public class MarketMappingDTO {
    private final int marketTypeId;
    private final Integer marketSubTypeId;
    private final int producerId;
    private final URN sportId;
    private final String sovTemplate;
    private final String validFor;
    private final List<OutcomeMappingDTO> outcomeMappings;

    public MarketMappingDTO(Mappings.Mapping mm) {
        Preconditions.checkNotNull(mm);
        Preconditions.checkArgument(mm.getProductId() > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mm.getSportId()));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mm.getMarketId()));

        producerId = mm.getProductId();
        sportId = mm.getSportId().equals("all") ? null : URN.parse(mm.getSportId());

        AbstractMap.SimpleImmutableEntry<Integer, Integer> marketIds = parseMappingMarketId(mm.getMarketId());
        marketTypeId = marketIds.getKey();
        marketSubTypeId = marketIds.getValue();

        sovTemplate = mm.getSovTemplate();
        validFor = mm.getValidFor();
        outcomeMappings = mm.getMappingOutcome() == null ? null :
                mm.getMappingOutcome().stream().map(OutcomeMappingDTO::new).collect(Collectors.toList());
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

    public List<OutcomeMappingDTO> getOutcomeMappings() {
        return outcomeMappings;
    }


    // k -> id, v -> subTypeId
    private static AbstractMap.SimpleImmutableEntry<Integer, Integer> parseMappingMarketId(String id) {
        String[] split = id.split(":");
        if (split.length == 2) {
            return new AbstractMap.SimpleImmutableEntry<>(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        } else {
            return new AbstractMap.SimpleImmutableEntry<>(Integer.valueOf(split[0]), null);
        }
    }
}
