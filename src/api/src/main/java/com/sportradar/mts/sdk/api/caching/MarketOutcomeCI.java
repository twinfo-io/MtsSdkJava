/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.caching;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.mts.sdk.api.rest.dto.OutcomeDescriptionDTO;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarketOutcomeCI {
    private final String id;
    private final Map<Locale, String> names;
    private final Map<Locale, String> descriptions;

    public MarketOutcomeCI(OutcomeDescriptionDTO o, Locale locale) {
        Preconditions.checkNotNull(o);
        Preconditions.checkNotNull(locale);

        id = o.getId();

        names = new ConcurrentHashMap<>();
        names.put(locale, o.getName());

        descriptions = new ConcurrentHashMap<>();
        if (!Strings.isNullOrEmpty(o.getDescription())) {
            descriptions.put(locale, o.getDescription());
        }
    }

    public String getId() {
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

    public void merge(OutcomeDescriptionDTO o, Locale locale) {
        Preconditions.checkNotNull(o);
        Preconditions.checkNotNull(locale);

        names.put(locale, o.getName());
        if (!Strings.isNullOrEmpty(o.getDescription())) {
            descriptions.put(locale, o.getDescription());
        }
    }
}

