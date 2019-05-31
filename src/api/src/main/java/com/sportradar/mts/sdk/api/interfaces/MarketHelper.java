/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

/**
 * Helper class to get get market url
 */
public interface MarketHelper {

    String getLiveMarket(int type, Integer subType, String specialOddsValue, String outcome);

    String getPrematchEventMarket(int type, int sportId, String specialOddsValue, String outcome);

    String getPrematchOutrightMarket(int type, int outrightId, String outcome);
}
