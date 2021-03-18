package com.sportradar.mts.sdk.api.rest;

import java.io.InputStream;

@SuppressWarnings("unchecked")
public class DeserializerStream implements Deserializer {
    @Override
    public synchronized <T> T deserialize(InputStream inStr, Class<T> clazz) {
        return (T) inStr;
    }

    @Override
    public synchronized <T> String serialize(T inObj) {
        return null;
    }
}
