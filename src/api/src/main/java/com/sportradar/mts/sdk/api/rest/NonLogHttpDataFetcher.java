package com.sportradar.mts.sdk.api.rest;

import com.google.inject.Inject;
import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

public class NonLogHttpDataFetcher extends HttpDataFetcher {
    @Inject
    public NonLogHttpDataFetcher(SdkConfiguration config, CloseableHttpClient httpClient) {
        super(config, httpClient);
    }

    @Override
    protected String send(AccessToken token, HttpUriRequest request) {
        return super.send(token, request);
    }
}
