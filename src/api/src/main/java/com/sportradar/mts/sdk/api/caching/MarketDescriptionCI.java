/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.caching;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.mts.sdk.api.rest.dto.MarketDescriptionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MarketDescriptionCI {

    private static final Logger logger = LoggerFactory.getLogger(MarketDescriptionCI.class);
    private final long id;
    private final Map<Locale, String> names;
    private final Map<Locale, String> descriptions;
    private final List<MarketMappingCI> mappings;
    private final List<MarketOutcomeCI> outcomes;
    private final List<MarketSpecifierCI> specifiers;
    private final List<MarketAttributeCI> attributes;
    private final List<Locale> fetchedLocales;
    private String variant;

    @SuppressWarnings("java:S107") // Methods should not have too many parameters
    public MarketDescriptionCI(long id,
                               Map<Locale, String> names,
                               Map<Locale, String> descriptions,
                               List<MarketMappingCI> mappings,
                               List<MarketOutcomeCI> outcomes,
                               List<MarketSpecifierCI> specifiers,
                               List<MarketAttributeCI> attributes,
                               String variant,
                               Locale locale)
    {
        this.id = id;
        this.names = names;
        this.descriptions = descriptions;
        this.variant = variant;
        this.mappings = mappings;
        this.outcomes = outcomes;
        this.specifiers = specifiers;
        this.attributes = attributes;

        fetchedLocales = new ArrayList<>();
        fetchedLocales.add(locale);
    }

    public MarketDescriptionCI(MarketDescriptionDTO market, Locale locale) {
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(locale);

        id = market.getId();
        names = new ConcurrentHashMap<>();
        names.put(locale, market.getName());
        variant = market.getVariant();
        descriptions = new ConcurrentHashMap<>();
        if (!Strings.isNullOrEmpty(market.getDescription())) {
            descriptions.put(locale, market.getDescription());
        }

        outcomes = market.getOutcomes() == null ? null :
                market.getOutcomes().stream()
                        .map(o -> new MarketOutcomeCI(o, locale)).collect(Collectors.toList());

        mappings = market.getMappings() == null ? null :
                market.getMappings().stream()
                        .map(MarketMappingCI::new).collect(Collectors.toList());

        specifiers = market.getSpecifiers() == null ? null :
                market.getSpecifiers().stream()
                        .map(MarketSpecifierCI::new).collect(Collectors.toList());

        attributes = market.getAttributes() == null ? null :
                market.getAttributes().stream()
                        .map(MarketAttributeCI::new).collect(Collectors.toList());

        fetchedLocales = new ArrayList<>();
        fetchedLocales.add(locale);
    }

    public static MarketDescriptionCI build(MarketDescriptionDTO dto, Locale locale)
    {
        return new MarketDescriptionCI(dto, locale);
    }

    public void merge(MarketDescriptionDTO market, Locale locale) {
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(locale);

        names.put(locale, market.getName());
        variant = market.getVariant();
        if (!Strings.isNullOrEmpty(market.getDescription())) {
            descriptions.put(locale, market.getDescription());
        }

        if (market.getOutcomes() != null) {
            market.getOutcomes().forEach(o -> {
                Optional<MarketOutcomeCI> existingOutcome = outcomes.stream()
                        .filter(exo -> exo.getId().equals(o.getId())).findFirst();
                if (existingOutcome.isPresent()) {
                    existingOutcome.get().merge(o, locale);
                } else {
                    logger.warn("Could not merge outcome[Id={}] on marketDescription[Id={}] because the specified" +
                            " outcome does not exist on stored market description", o.getId(), market.getId());
                }
            });
        }

        if (market.getMappings() != null) {
            market.getMappings().forEach(o -> {
                Optional<MarketMappingCI> existingMapping = mappings.stream()
                        .filter(
                                exm -> o.getMarketTypeId() == exm.getMarketTypeId()
                                        && Objects.equals(o.getMarketSubTypeId(), exm.getMarketSubTypeId()))
                        .findFirst();
                if (existingMapping.isPresent()) {
                    existingMapping.get().merge(o);
                } else {
                    logger.warn("Could not merge mapping[MarketId={}:{}] on marketDescription[Id={}] because " +
                                    "the specified mapping does not exist on stored market description",
                            o.getMarketTypeId(), o. getMarketSubTypeId(), market.getId());
                }
            });

            fetchedLocales.add(locale);
        }
    }

    public long getId() {
        return id;
    }

    public String getName(Locale locale) {
        Preconditions.checkNotNull(locale);

        return names.get(locale);
    }

    public String getDescription(Locale locale) {
        Preconditions.checkNotNull(locale);

        return descriptions.get(locale);
    }

    public List<MarketMappingCI> getMappings() {
        return mappings == null ? null : Collections.unmodifiableList(mappings);
    }

    public List<MarketOutcomeCI> getOutcomes() {
        return outcomes == null ? null : Collections.unmodifiableList(outcomes);
    }

    public List<MarketSpecifierCI> getSpecifiers() {
        return specifiers == null ? null : Collections.unmodifiableList(specifiers);
    }

    public List<MarketAttributeCI> getAttributes() {
        return attributes == null ? null : Collections.unmodifiableList(attributes);
    }

    public String getVariant() {
        return variant;
    }

    public List<Locale> getCachedLocales() {
            return Collections.unmodifiableList(fetchedLocales);
        }
}
