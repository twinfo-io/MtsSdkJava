/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    public static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setVisibility(OBJECT_MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                                            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
                                            .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                                            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                                            .withIsGetterVisibility(JsonAutoDetect.Visibility.ANY)
                                            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        OBJECT_MAPPER.addHandler(new JacksonProblemHandler());
        //objectMapper.registerModule(new TicketDeserializerModule());
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static <T> byte[] serialize(T item) {
        if (item == null) {
            return new byte[0];
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            OBJECT_MAPPER.writeValue(byteArrayOutputStream, item);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                logger.error("failed to close output stream");
            }
        }
    }

    public static <T> String serializeAsString(T item) {
        if (item == null) {
            return "";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(byte[] item, Class<T> clazz) throws IOException {
        if ((item == null) || (item.length == 0)) {
            return null;
        }
        return OBJECT_MAPPER.readValue(item, clazz);
    }

    public static <T> T deserialize(String item, Class<T> clazz) throws IOException {
        if ((item == null) || item.isEmpty()) {
            return null;
        }
        return OBJECT_MAPPER.readValue(item, clazz);
    }

    private final static class JacksonProblemHandler extends DeserializationProblemHandler {

        @Override
        public boolean handleUnknownProperty(DeserializationContext deserializationContext,
                                             JsonParser jp,
                                             JsonDeserializer<?> deserializer,
                                             Object beanOrClass,
                                             String propertyName) {

            String desc = "[NA]";
            if (beanOrClass != null) {
                if (beanOrClass instanceof Class) {
                    desc = ((Class) beanOrClass).getCanonicalName();
                } else {
                    desc = beanOrClass.getClass().getCanonicalName();
                }
            }
            logger.warn("Received unknown property {} in class {}", propertyName, desc);
            return false;
        }
    }
}
