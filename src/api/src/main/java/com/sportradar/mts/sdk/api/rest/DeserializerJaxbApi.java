/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * An implementation of the {@link Deserializer} used to deserialize/unmarshall the content of the Unified API
 * endpoint request
 */
public class DeserializerJaxbApi implements Deserializer {
    private final static Logger logger = LoggerFactory.getLogger(DeserializerJaxbApi.class);
    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    public DeserializerJaxbApi(Unmarshaller unmarshaller, Marshaller marshaller) {
        this.unmarshaller = unmarshaller;
        this.marshaller = marshaller;
    }

    @Override
    public synchronized <T> T deserialize(InputStream inStr, Class<T> clazz){
        try {
            return (T) JAXBIntrospector.getValue(unmarshaller.unmarshal(inStr));
        } catch (JAXBException e) {
            logger.warn("There was a problem unmarshalling an object, ex: ", e);
        }
        return null;
    }

    @Override
    public <T> String serialize(T inObj) {
        try {
            StringWriter writer = new StringWriter();
            marshaller.marshal(inObj, writer);
            return  writer.toString();
        } catch (JAXBException e) {
            logger.warn("There was a problem marshaling the provided data, ex: ", e);
        }
        return null;
    }
}
