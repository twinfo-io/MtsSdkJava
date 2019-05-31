/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.sportradar.mts.sdk.api.AccessToken;

/**
 * Implementation of the user exposed entity {@link AccessToken}
 */
public class AccessTokenImpl implements AccessToken {
    /**
     * The access token
     */
    private String accessToken;

    /**
     * The expiration time
     */
    private long expiresIn;

    /**
     * Initializes a new instance of the {@link AccessTokenImpl}
     *
     * @param accessToken the access token
     * @param expiresIn the expiration time
     */
    public AccessTokenImpl(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    /**
     * Gets access token
     *
     * @return access token
     */
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Gets access token expiration time in seconds
     *
     * @return expiration time
     */
    @Override
    public long getExpiresIn() {
        return expiresIn;
    }
}
