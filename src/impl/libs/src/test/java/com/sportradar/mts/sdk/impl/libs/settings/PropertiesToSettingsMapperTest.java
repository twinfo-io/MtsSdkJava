/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.settings;

import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.settings.PropertiesToSettingsMapper;
import com.sportradar.mts.sdk.api.settings.SettingsKeys;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author andrej.resnik on 20/06/16 at 14:29
 */
@SuppressWarnings("FieldCanBeLocal")
public class PropertiesToSettingsMapperTest extends TimeLimitedTestBase {

    private Properties properties;
    private SdkConfiguration config;

    private String nodeId;
    private String vHost;
    private String username;
    private String password;
    private String hostname;
    private String port;
    private String loggingLevel;
    private String loggingFolder;
    private String ticketResponseTimeout;
    private String ssl;
    private String messagesPerSecond;
    private int bookmakerId;
    private int limitId;
    private String currency;
    private SenderChannel channel;
    private String accessToken;
    private boolean provideAdditionalMarketSpecifiers;

    @Before
    public void setUp() {
        nodeId = "10";
        vHost = "/virtualHost";
        username = "username";
        password = "password";
        hostname = "hostname";
        port = "5672";
        loggingLevel = "all";
        loggingFolder = "loggingFolder";
        ticketResponseTimeout = "30000";
        ssl = "false";
        messagesPerSecond = "10";
        bookmakerId = 1234;
        limitId = 10;
        currency = "EUR";
        channel = SenderChannel.INTERNET;
        accessToken = "aaa";
        provideAdditionalMarketSpecifiers = true;

        setPropertiesToValidTestValues();
    }

    @Test
    public void getSettings_OkTest() {
        retrieveMtsSdkSettings();

        assertEquals(config.getNode(), (int) Integer.valueOf(nodeId));
        assertEquals(config.getVirtualHost(), vHost);
        assertEquals(config.getUsername(), username);
        assertEquals(config.getPassword(), password);
        assertEquals(config.getHost(), hostname);
        assertEquals(config.getPort(), (int) Integer.valueOf(port));
        assertEquals(config.getTicketResponseTimeoutLive(), (int) Integer.valueOf(ticketResponseTimeout));
        assertEquals(config.getUseSsl(), Boolean.valueOf(ssl));
        assertEquals(config.getMessagesPerSecond(), Double.valueOf(messagesPerSecond), 0.0);
        assertEquals(config.getAccessToken(), accessToken);
        assertEquals(config.getProvideAdditionalMarketSpecifiers(), provideAdditionalMarketSpecifiers);
    }

    @Test (expected = NullPointerException.class)
    public void getSettings_NoPropertiesTest() {
        properties.clear();

        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_NodeEmptyStringTest() {
        properties.setProperty(SettingsKeys.NODE_ID, " ");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_TicketResponseTimeoutEmptyStringTest() {
        properties.setProperty(SettingsKeys.TICKET_RESPONSE_TIMEOUT_LIVE, " ");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_SslEmptyStringTest() {
        properties.setProperty(SettingsKeys.SSL, " ");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_MessagesPerSecondEmptyStringTest() {
        properties.setProperty(SettingsKeys.MESSAGES_PER_SECOND, " ");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_BookmakerZeroTest() {
        properties.setProperty(SettingsKeys.BOOKMAKER_ID, "0");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_BookmakerLessThenZeroTest() {
        properties.setProperty(SettingsKeys.BOOKMAKER_ID, "-10");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_LimitZeroTest() {
        properties.setProperty(SettingsKeys.LIMIT_ID, "0");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_LimitLessThenZeroTest() {
        properties.setProperty(SettingsKeys.LIMIT_ID, "-10");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_CurrencyEmptyTest() {
        properties.setProperty(SettingsKeys.CURRENCY, " ");
        retrieveMtsSdkSettings();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_CurrencyWrongTest() {
        properties.setProperty(SettingsKeys.CURRENCY, "US");
        retrieveMtsSdkSettings();
    }

    @Test
    public void getSettings_CurrencyCorrectTest() {
        properties.setProperty(SettingsKeys.CURRENCY, "EUR");
        retrieveMtsSdkSettings();
        Assert.assertEquals("EUR", config.getCurrency());

        properties.setProperty(SettingsKeys.CURRENCY, "eur");
        retrieveMtsSdkSettings();
        Assert.assertEquals("eur", config.getCurrency());

        properties.setProperty(SettingsKeys.CURRENCY, "mBTC");
        retrieveMtsSdkSettings();
        Assert.assertEquals("mBTC", config.getCurrency());
    }

    @Test (expected = IllegalArgumentException.class)
    public void getSettings_SenderWrongTest() {
        properties.setProperty(SettingsKeys.CHANNEL, "US");
        retrieveMtsSdkSettings();
    }

    @Test
    public void getSettings_SenderCorrectTest() {
        properties.setProperty(SettingsKeys.CHANNEL, "INTERNET");
        retrieveMtsSdkSettings();
        Assert.assertEquals(SenderChannel.INTERNET, config.getSenderChannel());
    }

    private void setPropertiesToValidTestValues() {
        properties = new Properties();
        properties.setProperty(SettingsKeys.NODE_ID, nodeId);
        properties.setProperty(SettingsKeys.VIRTUAL_HOST, vHost);
        properties.setProperty(SettingsKeys.USERNAME, username);
        properties.setProperty(SettingsKeys.PASSWORD, password);
        properties.setProperty(SettingsKeys.HOST, hostname);
        properties.setProperty(SettingsKeys.TICKET_RESPONSE_TIMEOUT_LIVE, ticketResponseTimeout);
        properties.setProperty(SettingsKeys.SSL, ssl);
        properties.setProperty(SettingsKeys.MESSAGES_PER_SECOND, messagesPerSecond);
        properties.setProperty(SettingsKeys.BOOKMAKER_ID, Integer.toString(bookmakerId));
        properties.setProperty(SettingsKeys.LIMIT_ID, Integer.toString(limitId));
        properties.setProperty(SettingsKeys.CURRENCY, currency);
        properties.setProperty(SettingsKeys.CHANNEL, channel.toString());
        properties.setProperty(SettingsKeys.ACCESS_TOKEN, accessToken);
    }

    private void retrieveMtsSdkSettings() {
        config = PropertiesToSettingsMapper.getSettings(properties);
    }
}
