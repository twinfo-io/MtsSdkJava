/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public final class DoubleToStringSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(final Double value, final JsonGenerator jsonGenerator, final SerializerProvider provider) throws
                                                                                                           IOException {
        jsonGenerator.writeString(Double.isNaN(value) ? null : value.toString());
    }
}