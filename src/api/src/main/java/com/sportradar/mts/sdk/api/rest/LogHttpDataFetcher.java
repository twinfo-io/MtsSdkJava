/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for the {@link HttpDataFetcher} with the sole purpose of API request logging
 */
public class LogHttpDataFetcher extends HttpDataFetcher{
    private static final Logger logger = LoggerFactory.getLogger("com.sportradar.mts.rest");

    @Inject
    public LogHttpDataFetcher(SdkConfiguration config, CloseableHttpClient httpClient) {
        super(config, httpClient);
    }

    @Override
    protected String send(AccessToken token, HttpUriRequest request) {
        String path = request.getURI().toString();

        logger.info("Fetching data from: {}", path);

        String result = super.send(token, request);

        String cleanResult = result.replace("\n", "");
        logger.info("Request: {}, response - {}: {}", path, !Strings.isNullOrEmpty(result) ? "OK" : "FAILED", cleanResult);

        return result;
    }
}