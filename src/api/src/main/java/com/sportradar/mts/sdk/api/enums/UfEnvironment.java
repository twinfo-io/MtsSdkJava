/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

public enum UfEnvironment {
    /**
     * Integration
     */
    INTEGRATION("https://stgapi.betradar.com"),

    /**
     * Production
     */
    PRODUCTION("https://api.betradar.com");

    private final String host;

    UfEnvironment(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public static UfEnvironment fromString(String name) {
        return UfEnvironment.valueOf(name.toUpperCase());
    }
}
