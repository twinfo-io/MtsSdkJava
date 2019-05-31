/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.caching;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.rest.dto.OutcomeMappingDTO;

public class OutcomeMappingCI {
    private final String outcomeId;
    private final String producerOutcomeId;
    private final String producerOutcomeName;

    public OutcomeMappingCI(OutcomeMappingDTO o) {
        Preconditions.checkNotNull(o);

        outcomeId = o.getOutcomeId();
        producerOutcomeId = o.getProducerOutcomeId();
        producerOutcomeName = o.getProducerOutcomeName();
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
