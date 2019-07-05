/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest;



import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale;

/**
 * The generic class used to get various data from the Unified API endpoints
 *
 * @param <TOut> - valid Unified API endpoint object generated from API xsd schemas
 */
@SuppressWarnings("FieldCanBeLocal")
public class DataProvider <TOut>{

    private final String uriFormat;
    private final SdkConfiguration config;
    private final LogHttpDataFetcher logHttpDataFetcher;
    private final Deserializer deserializer;
    private final Locale defaultLocale = Locale.ENGLISH;
    private final Class<TOut> clazz;

    public DataProvider(String uriFormat,
                        SdkConfiguration config,
                        LogHttpDataFetcher logHttpDataFetcher,
                        Deserializer deserializer,
                        Class<TOut> clazz) {
        this.uriFormat = uriFormat;
        this.config = config;
        this.deserializer = deserializer;
        this.logHttpDataFetcher = logHttpDataFetcher;
        this.clazz = clazz;
    }

    /**
     * If successful returns the requested API endpoint object, else null
     */
    public TOut getData() {
        return getData(defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param content - a content to be sent
     */
    public TOut postData(HttpEntity content) {
        return postData(content, defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     */
    public TOut getData(AccessToken token) {
        return getData(token, defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param content - a content to be sent
     */
    public TOut postData(AccessToken token, HttpEntity content) {
        return sendData(token, content, defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public TOut getData(String... args) {
        return getData(defaultLocale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param content - a content to be sent
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public TOut postData(HttpEntity content, String... args) {
        return postData(content, defaultLocale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public TOut getData(AccessToken token, String... args) {
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
    public TOut postData(AccessToken token, HttpEntity content, String... args) {
        return sendData(token, content, defaultLocale, args);
    }

    /**
     * If successful returns the requested API endpoint object, else null
     *
     * @param locale - the locale that is used with the supplied URI format
     * @param args - that are used with the supplied URI format
     * @return - the requested API endpoint object or null
     */
    public TOut getData(Locale locale, String... args) {
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
    public TOut postData(HttpEntity content, Locale locale, String... args) {
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
    public TOut getData(AccessToken token, Locale locale, String... args) {
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
    public TOut sendData(AccessToken token, HttpEntity content, Locale locale, String... args) {
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

        InputStream inputStream = new ByteArrayInputStream(fetchedContent.getBytes());

        try {

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