/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest.dto;

import com.google.common.base.Preconditions;
import com.sportradar.mts.api.rest.sportsapi.datamodel.DescOutcomes;

public class OutcomeDescriptionDTO {
    private final String id;
    private final String name;
    private final String description;

    public OutcomeDescriptionDTO(DescOutcomes.Outcome o) {
        Preconditions.checkNotNull(o);

        id = o.getId();
        name = o.getName();

        description = o.getDescription();
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }
}
