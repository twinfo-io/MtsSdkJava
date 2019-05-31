/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

/**
 * Object containing access token response data
 */
public interface AccessToken {

    /**
     * Gets access token
     *
     * @return access token
     */
    String getAccessToken();

    /**
     * Gets access token expiration time in seconds
     *
     * @return expiration time
     */
    long getExpiresIn();
}
