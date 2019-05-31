/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest;

import com.google.common.io.ByteStreams;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * An implementation of the {@link Deserializer} used to deserialize/unmarshall the content of the MTS Client API
 * endpoint request
 */
public class DeserializerJsonApi implements Deserializer {
    private final static Logger logger = LoggerFactory.getLogger(DeserializerJsonApi.class);

    @Override
    public synchronized <T> T deserialize(InputStream inStr, Class<T> clazz){
        try {
            return JsonUtils.deserialize(ByteStreams.toByteArray(inStr), clazz);
        } catch (IOException e) {
            logger.warn("There was a problem unmarshalling an object, ex: ", e);
        }
        return null;
    }

    @Override
    public <T> String serialize(T inObj) {
        try {
            return JsonUtils.serializeAsString(inObj);
        } catch (Exception e) {
            logger.warn("There was a problem marshaling the provided data, ex: ", e);
        }
        return null;
    }
}
