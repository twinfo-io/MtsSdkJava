/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest;


import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Class used to fetch content from the Unified API, the output of this.get() is usually
 * used in combination with a {@link Deserializer} to get a valid useful Java object.
 */
class HttpDataFetcher {
    private final SdkConfiguration config;
    private final CloseableHttpClient httpClient;
    private int statusCode;
    private static final Logger logger = LoggerFactory.getLogger(HttpDataFetcher.class);

    HttpDataFetcher(SdkConfiguration config, CloseableHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    /**
     * Gets the content on the given path trough a GET request
     *
     * @param path - a valid HTTP GET request path
     * @return - if successful the content of the request, else empty {@link String}
     */
    public String get(String path) {
        return get(null, path);
    }

    /**
     * Gets the content on the given path trough a GET request
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param path - a valid HTTP GET request path
     * @return - if successful the content of the request, else empty {@link String}
     */
    public String get(AccessToken token, String path) {
        return send(token, new HttpGet(path));
    }

    /**
     * Gets the content on the given path trough a POST request
     *
     * @param content - a content to be sent
     * @param path - a valid HTTP GET request path
     * @return - if successful the content of the request, else empty {@link String}
     */
    public String post(HttpEntity content, String path) {
        return post(null, content, path);
    }

    /**
     * Gets the content on the given path trough a POST request
     *
     * @param token - a {@link AccessToken} used to access protected resources
     * @param content - a content to be sent
     * @param path - a valid HTTP GET request path
     * @return - if successful the content of the request, else empty {@link String}
     */
    public String post(AccessToken token, HttpEntity content, String path) {
        HttpPost httpPost = new HttpPost(path);
        httpPost.setEntity(content);
        return send(token, httpPost);
    }

    protected String send(AccessToken token, HttpUriRequest request) {
        String path = request.getURI().toString();
        try {
            if (config != null) {
                String xAccessToken = config.getAccessToken();
                if (!StringUtils.isNullOrEmpty(xAccessToken))
                    request.addHeader("x-access-token", xAccessToken);
            }

            if (token != null && !StringUtils.isNullOrEmpty(token.getAccessToken()))
                request.addHeader("Authorization", "Bearer " + token.getAccessToken());

            ResponseHandler<String> handler = resp -> {
                statusCode = resp.getStatusLine().getStatusCode();
                // the whoami endpoint is a special case since we are interested in the response even if the response code is forbidden
                boolean isWhoAmI = path.endsWith("whoami.xml");
                if (statusCode == HttpStatus.SC_OK || (isWhoAmI && statusCode == HttpStatus.SC_FORBIDDEN)) {
                    return EntityUtils.toString(resp.getEntity());
                } else {
                    logger.warn("Non OK API response: " + resp.getStatusLine() + " " + statusCode + " " + path);
                    return "";
                }
            };

            String resp = httpClient.execute(request, handler);
            if (!resp.equals("")) {
                return resp;
            }
        } catch (IOException e) {
            logger.warn("Problems reading: " + path + " " + e.getMessage(), e);
        }
        return "";
    }

    public int getStatusCode() {
        return statusCode;
    }
}