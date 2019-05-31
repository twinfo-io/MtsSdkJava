/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest.dto;

import com.google.common.base.Preconditions;
import com.sportradar.mts.api.rest.sportsapi.datamodel.Mappings;

public class OutcomeMappingDTO {
    private final String outcomeId;
    private final String producerOutcomeId;
    private final String producerOutcomeName;

    public OutcomeMappingDTO(Mappings.Mapping.MappingOutcome o) {
        Preconditions.checkNotNull(o);

        outcomeId = o.getOutcomeId();
        producerOutcomeId = o.getProductOutcomeId();
        producerOutcomeName = o.getProductOutcomeName();
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public String getProducerOutcomeId() {
        return producerOutcomeId;
    }

    public String getProducerOutcomeName() {
        return producerOutcomeName;
    }

}
