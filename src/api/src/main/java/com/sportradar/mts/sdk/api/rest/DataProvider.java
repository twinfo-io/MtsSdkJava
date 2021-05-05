/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest;


import com.sportradar.mts.sdk.api.AccessToken;
import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale;

/**
 * The generic class used to get various data from the Unified API endpoints
 *
 * @param <T> - valid Unified API endpoint object generated from API xsd schemas
 */
@SuppressWarnings("FieldCanBeLocal")
public class DataProvider <T>{

    private final String uriFormat;
    private final HttpDataFetcher logHttpDataFetcher;
    private final Deserializer deserializer;
    private static final Locale defaultLocale = Locale.ENGLISH;
    private final Class<T> clazz;

    public DataProvider(String uriFormat,
                        HttpDataFetcher logHttpDataFetcher,
                        Deserializer deserializer,
                        Class<T> clazz) {
        this.uriFormat = uriFormat;
        this.deserializer = deserializer;
        this.logHttpDataFetcher = logHttpDataFetcher;
        this.clazz = clazz;
    }

    /**
     * If successful returns the requested API endpoint object, else null
     * @return - the requested API endpoint object or null
     */
    public T getData() {
        return getData(defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param content - a content to be sent
     * @return - the requested API endpoint object or null
     */
    public T postData(HttpEntity content) {
        return postData(content, defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @return - the requested API endpoint object or null
     */
    public T getData(AccessToken token) {
        return getData(token, defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param content - a content to be sent
     * @return - the requested API endpoint object or null
     */
    public T postData(AccessToken token, HttpEntity content) {
        return sendData(token, content, defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public T getData(String... args) {
        return getData(defaultLocale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param content - a content to be sent
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public T postData(HttpEntity content, String... args) {
        return postData(content, defaultLocale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public T getData(AccessToken token, String... args) {
        return getData(token, defaultLocale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param content - a content to be sent
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public T postData(AccessToken token, HttpEntity content, String... args) {
        return sendData(token, content, defaultLocale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param locale - the locale that is used with the supplied URI format
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public T getData(Locale locale, String... args) {
        return sendData(null, null, locale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param content - a content to be sent
     * @param locale - the locale that is used with the supplied URI format
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public T postData(HttpEntity content, Locale locale, String... args) {
        return sendData(null, content, locale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param locale - the locale that is used with the supplied URI format
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public T getData(AccessToken token, Locale locale, String... args) {
        return sendData(token, null, locale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param content - a content to be sent
     * @param locale - the locale that is used with the supplied URI format
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public T sendData(AccessToken token, HttpEntity content, Locale locale, String... args) {
        int fwArgSize = (args != null) ? (args.length + 1) : 1;

        String[] forwardArgs = new String[fwArgSize];
        forwardArgs[0] = locale.toString();

        if (args != null) {
            System.arraycopy(args, 0, forwardArgs, 1, args.length);
        }

        String formattedPath = String.format(uriFormat, (Object[]) forwardArgs);

        String fetchedContent = content == null ?
                logHttpDataFetcher.get(token, formattedPath) :
                logHttpDataFetcher.post(token, content, formattedPath);

        if (fetchedContent.equals("")) {
            return null;
        }

        try (InputStream inputStream = new ByteArrayInputStream(fetchedContent.getBytes())) {
            return deserializer.deserialize(inputStream, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "DataProvider{" +
                "uriFormat='" + uriFormat + '\'' +
                '}';
    }
}