/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;

import java.io.InputStream;
import java.util.Properties;

public class SdkInfo {

    public static String mtsTicketVersion()  {
        return "2.3";
    }

    public static final int TicketResponseTimeoutLiveDefault = 17000;
    public static final int TicketResponseTimeoutPrematchDefault = 5000;
    public static final int TicketCancellationResponseTimeoutDefault = 600000;
    public static final int TicketCashoutResponseTimeoutDefault = 600000;
    public static final int TicketNonSrResponseTimeoutDefault = 600000;
    public static final int TicketResponseTimeoutLiveMin = 10000;
    public static final int TicketResponseTimeoutPrematchMin = 3000;
    public static final int TicketCancellationResponseTimeoutMin = 10000;
    public static final int TicketCashoutResponseTimeoutMin = 10000;
    public static final int TicketNonSrResponseTimeoutMin = 10000;
    public static final int TicketResponseTimeoutLiveMax = 30000;
    public static final int TicketResponseTimeoutPrematchMax = 30000;
    public static final int TicketCancellationResponseTimeoutMax = 3600000;
    public static final int TicketCashoutResponseTimeoutMax = 3600000;
    public static final int TicketNonSrResponseTimeoutMax = 3600000;
    public static final String ApiHostIntegration = "https://stgapi.betradar.com";
    public static final String ApiHostProduction = "https://api.betradar.com";

    public static String getVersion()
    {
        try {
            //String version = SdkConfiguration.class.getPackage().getSpecificationVersion();
            InputStream is = SdkConfiguration.class.getResourceAsStream("/version/sdk.properties");
            Properties props = new Properties();
            props.load(is);
            is.close();
            return props.getProperty("version");
        } catch (Exception e) {
            return "0.0.0.0";
        }
    }
}
