/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.caching;

/**
 * Defines a contract implemented by classes used to caching market descriptions
 */
public interface MarketDescriptionCache {

    /**
     * Gets the {@link MarketDescriptionCI} instance for the specific marketId
     * @param marketId Id of the market
     * @return {@link MarketDescriptionCI} for specific marketId
     */
    MarketDescriptionCI getMarketDescription(int marketId);
}
