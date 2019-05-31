/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest.dto;

import com.google.common.base.Preconditions;
import com.sportradar.mts.api.rest.sportsapi.datamodel.DescMarket;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MarketDescriptionDTO {
    private final long id;
    private final String name;
    private final String description;
    private final List<MarketMappingDTO> mappings;
    private final List<OutcomeDescriptionDTO> outcomes;
    private final List<MarketSpecifierDTO> specifiers;
    private final List<MarketAttributeDTO> attributes;
    private final String variant;

    public MarketDescriptionDTO(DescMarket market) {
        Preconditions.checkNotNull(market);

        id = market.getId();
        name = market.getName();
        variant = market.getVariant();
        description = market.getDescription();

        outcomes = market.getOutcomes() == null ? null :
                market.getOutcomes().getOutcome().stream()
                        .map(OutcomeDescriptionDTO::new).collect(Collectors.toList());

        mappings = market.getMappings() == null ? null :
                market.getMappings().getMapping().stream()
                        .map(MarketMappingDTO::new).collect(Collectors.toList());

        specifiers = market.getSpecifiers() == null ? null :
                market.getSpecifiers().getSpecifier().stream()
                        .map(MarketSpecifierDTO::new).collect(Collectors.toList());

        attributes = market.getAttributes() == null ? null :
                market.getAttributes().getAttribute().stream()
                        .map(MarketAttributeDTO::new).collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public List<MarketMappingDTO> getMappings() {
        return mappings == null ? null : Collections.unmodifiableList(mappings);
    }

    public List<OutcomeDescriptionDTO> getOutcomes() {
        return outcomes == null ? null : Collections.unmodifiableList(outcomes);
    }

    public List<MarketSpecifierDTO> getSpecifiers() {
        return specifiers == null ? null : Collections.unmodifiableList(specifiers);
    }

    public List<MarketAttributeDTO> getAttributes() {
        return attributes == null ? null : Collections.unmodifiableList(attributes);
    }

    public String getVariant() {
        return variant;
    }
}
