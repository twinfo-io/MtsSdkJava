/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.com.mts.sdk.app.root;

import com.rabbitmq.client.impl.ClientVersion;
import com.sportradar.mts.sdk.api.interfaces.MtsSdkApi;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.app.MtsSdk;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MtsSdkRootTest{

    @Test
    public void OpenCloseMtsSdkTest() throws InterruptedException{

        String pk = ClientVersion.VERSION;
        Properties properties = new Properties();
        properties.setProperty("mts.sdk.hostname", "10.27.26.83");
        properties.setProperty("mts.sdk.vhost", "/klika");
        properties.setProperty("mts.sdk.username", "test");
        properties.setProperty("mts.sdk.password", "test");
        properties.setProperty("mts.sdk.test", "false");
        properties.setProperty("mts.sdk.ssl", "false");

        SdkConfiguration config = MtsSdk.getConfiguration(properties);
        MtsSdkApi mtsSdk = new MtsSdk(config);
        mtsSdk.open();
        assertTrue(mtsSdk.isOpen());
        mtsSdk.close();
        assertFalse(mtsSdk.isOpen());
    }
}
