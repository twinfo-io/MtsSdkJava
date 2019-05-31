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

    public static String getVersion()
    {
        try {
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
