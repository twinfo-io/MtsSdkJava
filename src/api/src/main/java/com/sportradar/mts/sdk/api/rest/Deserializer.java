/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest;

import java.io.InputStream;

/**
 * The basic interface representation of a deserializer used to produce valid Java object from a data source
 */
public interface Deserializer {
    <T> T deserialize(InputStream inStr, Class<T> clazz);
    <T> String serialize(T inObj);
}