/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class StringToDoubleDeserializer extends JsonDeserializer<Double> {

    private static final Logger logger = LoggerFactory.getLogger(StringToDoubleDeserializer.class);

    @Override
    public Double deserialize(final JsonParser jp, final DeserializationContext deserializationContext) throws
                                                                                                        IOException {
        if (jp.getValueAsString() == null) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(jp.getValueAsString());
        } catch (Exception exc) {
            logger.debug("Failed to decode Double for input: {}", jp.getText(), exc);
            return Double.NaN;
        }
    }
}
