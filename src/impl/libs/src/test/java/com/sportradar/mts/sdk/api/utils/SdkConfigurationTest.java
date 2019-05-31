/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationBuilderImpl;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationImpl;
import com.sportradar.mts.sdk.api.settings.SettingsKeys;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class SdkConfigurationTest extends TimeLimitedTestBase {

    @Test
    public void builderMinimalConfiguration() {
        SdkConfiguration config = new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .build();

        checkAllSettings(config);
    }

    @Test
    public void propertiesMinimalConfiguration() {
        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);

        checkAllSettings(config);
    }

    @Test
    public void builderSetters() {
        SdkConfiguration config = new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setPort(1)
                .setVirtualHost("/virtualHost")
                .setUseSsl(false)
                .setNode(2)
                .setBookmakerId(3)
                .setLimitId(4)
                .setCurrency("EUR")
                .setSenderChannel(SenderChannel.INTERNET)
                .setAccessToken("accessToken")
                .setProvideAdditionalMarketSpecifiers(false)
                .setExclusiveConsumer(false)
                .setKeycloakHost("keycloakHost")
                .setKeycloakUsername("keycloakUsername")
                .setKeycloakPassword("keycloakPassword")
                .setKeycloakSecret("keycloakSecret")
                .setMtsClientApiHost("clientApiHost")
                .setTicketResponseTimeout(10000)
                .setTicketCancellationResponseTimeout(10001)
                .setTicketCashoutResponseTimeout(10002)
                .setTicketNonSrSettleResponseTimeout(10003)
                .build();

        checkAllSettings(
                config,
                "username",
                "password",
                "host",
                1,
                "/virtualHost",
                false,
                2,
                3,
                4,
                "EUR",
                SenderChannel.INTERNET,
                "accessToken",
                false,
                false,
                "keycloakHost",
                "keycloakUsername",
                "keycloakPassword",
                "keycloakSecret",
                "clientApiHost",
                10000,
                10001,
                10002);
    }

    @Test
    public void propertiesSetters() {
        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.PORT, "1");
        properties.setProperty(SettingsKeys.VIRTUAL_HOST, "/virtualHost");
        properties.setProperty(SettingsKeys.SSL, "false");
        properties.setProperty(SettingsKeys.NODE_ID, "2");
        properties.setProperty(SettingsKeys.BOOKMAKER_ID, "3");
        properties.setProperty(SettingsKeys.LIMIT_ID, "4");
        properties.setProperty(SettingsKeys.CURRENCY, "EUR");
        properties.setProperty(SettingsKeys.CHANNEL, "INTERNET");
        properties.setProperty(SettingsKeys.ACCESS_TOKEN, "accessToken");
        properties.setProperty(SettingsKeys.PROVIDE_ADDITIONAL_MARKET_SPECIFIERS, "false");
        properties.setProperty(SettingsKeys.EXCLUSIVE_CONSUMER, "false");
        properties.setProperty(SettingsKeys.KEYCLOAK_HOST, "keycloakHost");
        properties.setProperty(SettingsKeys.KEYCLOAK_USERNAME, "keycloakUsername");
        properties.setProperty(SettingsKeys.KEYCLOAK_PASSWORD, "keycloakPassword");
        properties.setProperty(SettingsKeys.KEYCLOAK_SECRET, "keycloakSecret");
        properties.setProperty(SettingsKeys.MTS_CLIENT_API_HOST, "clientApiHost");
        properties.setProperty(SettingsKeys.TICKET_RESPONSE_TIMEOUT, "10000");
        properties.setProperty(SettingsKeys.TICKET_CANCELLATION_RESPONSE_TIMEOUT, "10001");
        properties.setProperty(SettingsKeys.TICKET_CASHOUT_RESPONSE_TIMEOUT, "10002");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);

        checkAllSettings(
                config,
                "username",
                "password",
                "host",
                1,
                "/virtualHost",
                false,
                2,
                3,
                4,
                "EUR",
                SenderChannel.INTERNET,
                "accessToken",
                false,
                false,
                "keycloakHost",
                "keycloakUsername",
                "keycloakPassword",
                "keycloakSecret",
                "clientApiHost",
                10000,
                10001,
                10002);
    }

    @Test
    public void yamlSetters() {
        SdkConfiguration config = SdkConfigurationImpl.getConfigurationFromYaml();

        checkAllSettings(
                config,
                "test-username",
                "test-pass",
                "test-hostname",
                7777,
                "/test-vhost",
                false,
                46,
                5555,
                1000,
                "btc",
                SenderChannel.CALLCENTRE,
                "test-tkn",
                false,
                false,
                "keycloak",
                "keycloak-username",
                "keycloak-password",
                "keycloak-secret",
                "client-api",
                10001,
                10002,
                10003);
    }

    @Test
    public void builderMissingUsername() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing username");

        new SdkConfigurationBuilderImpl()
                .setPassword("password")
                .setHost("host")
                .build();
    }

    @Test
    public void propertiesMissingUsername() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(SettingsKeys.USERNAME + " missing from properties");

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderMissingPassword() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing password");

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setHost("host")
                .build();
    }

    @Test
    public void propertiesMissingPassword() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(SettingsKeys.PASSWORD + " missing from properties");

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.HOST, "host");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderMissingHost() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing host");

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .build();
    }

    @Test
    public void propertiesMissingHost() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(SettingsKeys.HOST + " missing from properties");

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderEmptyUsername() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("")
                .setPassword("password")
                .setHost("host")
                .build();
    }

    @Test
    public void builderNullUsername() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername(null)
                .setPassword("password")
                .setHost("host")
                .build();
    }

    @Test
    public void propertiesEmptyUsername() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderEmptyPassword() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("")
                .setHost("host")
                .build();
    }

    @Test
    public void builderNullPassword() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword(null)
                .setHost("host")
                .build();
    }

    @Test
    public void propertiesEmptyPassword() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "");
        properties.setProperty(SettingsKeys.HOST, "host");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderEmptyHost() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("")
                .build();
    }

    @Test
    public void builderNullHost() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost(null)
                .build();
    }

    @Test
    public void propertiesEmptyHost() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderNegativePort() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setPort(-1)
                .build();
    }

    @Test
    public void propertiesNegativePort() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.PORT, "-1");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderEmptyVirtualHost() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setVirtualHost("")
                .build();
    }

    @Test
    public void builderNullVirtualHost() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setVirtualHost(null)
                .build();
    }

    @Test
    public void propertiesEmptyVirtualHost() {
        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.VIRTUAL_HOST, "");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);

        assertEquals("/username", config.getVirtualHost());
    }

    @Test
    public void builderNegativeNodeId() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setNode(-1)
                .build();
    }

    @Test
    public void propertiesNegativeNodeId() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.NODE_ID, "-1");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderNegativeBookmakerId() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setBookmakerId(-1)
                .build();
    }

    @Test
    public void propertiesNegativeBookmakerId() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.BOOKMAKER_ID, "-1");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderNegativeLimitId() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setLimitId(-1)
                .build();
    }

    @Test
    public void propertiesNegativeLimitId() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.LIMIT_ID, "-1");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderCurrencyToShort() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setCurrency("E")
                .build();
    }

    @Test
    public void propertiesCurrencyToShort() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.CURRENCY, "E");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderCurrencyToLong() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setCurrency("EUREUR")
                .build();
    }

    @Test
    public void propertiesCurrencyToLong() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.CURRENCY, "EUREUR");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderNullCurrency() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setCurrency(null)
                .build();
    }

    @Test
    public void builderEmptyAccessToken() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setAccessToken("")
                .build();
    }

    @Test
    public void builderNullAccessToken() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setAccessToken(null)
                .build();
    }

    @Test
    public void builderEmptyKeycloakHost() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setKeycloakHost("")
                .build();
    }

    @Test
    public void builderNullKeycloakHost() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setKeycloakHost(null)
                .build();
    }

    @Test
    public void builderEmptyKeycloakUsername() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setKeycloakUsername("")
                .build();
    }

    @Test
    public void builderNullKeycloakUsername() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setKeycloakUsername(null)
                .build();
    }

    @Test
    public void builderEmptyKeycloakPassword() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setKeycloakPassword("")
                .build();
    }

    @Test
    public void builderNullKeycloakPassword() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setKeycloakPassword(null)
                .build();
    }

    @Test
    public void builderEmptyKeycloakSecret() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setKeycloakSecret("")
                .build();
    }

    @Test
    public void builderNullKeycloakSecret() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setKeycloakSecret(null)
                .build();
    }

    @Test
    public void builderEmptyMtsClientApiHost() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setMtsClientApiHost("")
                .build();
    }

    @Test
    public void builderNullMtsClientApiHost() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setMtsClientApiHost(null)
                .build();
    }

    @Test
    public void builderTicketResponseTimeoutToLow() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setTicketResponseTimeout(10000 - 1)
                .build();
    }

    @Test
    public void propertiesTicketResponseTimeoutToLow() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.TICKET_RESPONSE_TIMEOUT, String.valueOf(10000 - 1));

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderTicketResponseTimeoutToHigh() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setTicketResponseTimeout(30000 + 1)
                .build();
    }

    @Test
    public void propertiesTicketResponseTimeoutToHigh() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.TICKET_RESPONSE_TIMEOUT, String.valueOf(30000 + 1));

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderTicketCancellationResponseTimeoutToLow() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setTicketCancellationResponseTimeout(10000 - 1)
                .build();
    }

    @Test
    public void propertiesTicketCancellationResponseTimeoutToLow() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.TICKET_CANCELLATION_RESPONSE_TIMEOUT, String.valueOf(10000 - 1));

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderTicketCancellationResponseTimeoutToHigh() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setTicketCancellationResponseTimeout(3600000 + 1)
                .build();
    }

    @Test
    public void propertiesTicketCancellationResponseTimeoutToHigh() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.TICKET_CANCELLATION_RESPONSE_TIMEOUT, String.valueOf(3600000 + 1));

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderTicketCashoutResponseTimeoutToLow() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setTicketCashoutResponseTimeout(10000 - 1)
                .build();
    }

    @Test
    public void propertiesTicketCashoutResponseTimeoutToLow() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.TICKET_CANCELLATION_RESPONSE_TIMEOUT, String.valueOf(10000 - 1));

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderTicketCashoutResponseTimeoutToHigh() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setTicketCancellationResponseTimeout(3600000 + 1)
                .build();
    }

    @Test
    public void propertiesTicketCashoutResponseTimeoutToHigh() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.TICKET_CASHOUT_RESPONSE_TIMEOUT, String.valueOf(3600000 + 1));

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderTicketNonSrSettleResponseTimeoutToLow() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setTicketNonSrSettleResponseTimeout(10000 - 1)
                .build();
    }

    @Test
    public void propertiesTicketNonSrSettleResponseTimeoutToLow() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.TICKET_NON_SR_SETTLE_RESPONSE_TIMEOUT, String.valueOf(10000 - 1));

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderTicketNonSrSettleResponseTimeoutToHigh() {
        thrown.expect(IllegalArgumentException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setTicketNonSrSettleResponseTimeout(3600000 + 1)
                .build();
    }

    @Test
    public void propertiesTicketNonSrSettleResponseTimeoutToHigh() {
        thrown.expect(IllegalArgumentException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.TICKET_NON_SR_SETTLE_RESPONSE_TIMEOUT, String.valueOf(3600000 + 1));

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderMissingKeycloakHost() {
        thrown.expect(NullPointerException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setMtsClientApiHost("clientApi")
                .setKeycloakSecret("secret")
                .build();
    }

    @Test
    public void propertiesMissingKeycloakHost() {
        thrown.expect(NullPointerException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.MTS_CLIENT_API_HOST, "clientApi");
        properties.setProperty(SettingsKeys.KEYCLOAK_SECRET, "secret");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderMissingKeycloakSecret() {
        thrown.expect(NullPointerException.class);

        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setMtsClientApiHost("clientApi")
                .setKeycloakHost("keycloak")
                .build();
    }

    @Test
    public void propertiesMissingKeycloakSecret() {
        thrown.expect(NullPointerException.class);

        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.MTS_CLIENT_API_HOST, "clientApi");
        properties.setProperty(SettingsKeys.KEYCLOAK_HOST, "keycloak");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    @Test
    public void builderMissingKeycloakUsernameAndPassword() {
        new SdkConfigurationBuilderImpl()
                .setUsername("username")
                .setPassword("password")
                .setHost("host")
                .setMtsClientApiHost("clientApi")
                .setKeycloakHost("keycloak")
                .setKeycloakSecret("secret")
                .build();
    }

    @Test
    public void propertiesMissingKeycloakUsernameAndPassword() {
        Properties properties = new Properties();
        properties.setProperty(SettingsKeys.USERNAME, "username");
        properties.setProperty(SettingsKeys.PASSWORD, "password");
        properties.setProperty(SettingsKeys.HOST, "host");
        properties.setProperty(SettingsKeys.MTS_CLIENT_API_HOST, "clientApi");
        properties.setProperty(SettingsKeys.KEYCLOAK_HOST, "keycloak");
        properties.setProperty(SettingsKeys.KEYCLOAK_SECRET, "secret");

        SdkConfiguration config = SdkConfigurationImpl.getConfiguration(properties);
    }

    private static void checkAllSettings(SdkConfiguration config) {
        checkAllSettings(
                config,
                "username",
                "password",
                "host",
                5671,
                "/username",
                true,
                1,
                0,
                0,
                null,
                null,
                null,
                true,
                true,
                null,
                null,
                null,
                null,
                null,
                15000,
                600000,
                600000);
    }

    private static void checkAllSettings(
            SdkConfiguration config,
            String username,
            String password,
            String host,
            int port,
            String virtualHost,
            boolean useSsl,
            int nodeId,
            int bookmakerId,
            int limitId,
            String currency,
            SenderChannel channel,
            String accessToken,
            boolean provideAdditionalMarketSpecifiers,
            boolean exclusiveConsumer,
            String keycloakHost,
            String keycloakUsername,
            String keycloakPassword,
            String keycloakSecret,
            String mtsClientApiHost,
            int ticketResponseTimeout,
            int ticketCancellationResponseTimeout,
            int ticketCashoutResponseTimeout) {
        assertEquals(username, config.getUsername());
        assertEquals(password, config.getPassword());
        assertEquals(host, config.getHost());
        assertEquals(port, config.getPort());
        assertEquals(virtualHost, config.getVirtualHost());
        assertEquals(useSsl, config.getUseSsl());
        assertEquals(nodeId, config.getNode());
        assertEquals(bookmakerId, config.getBookmakerId());
        assertEquals(limitId, config.getLimitId());
        assertEquals(currency, config.getCurrency());
        assertEquals(channel, config.getSenderChannel());
        assertEquals(accessToken, config.getAccessToken());
        assertEquals(provideAdditionalMarketSpecifiers, config.getProvideAdditionalMarketSpecifiers());
        assertEquals(exclusiveConsumer, config.getExclusiveConsumer());
        assertEquals(keycloakHost, config.getKeycloakHost());
        assertEquals(keycloakUsername, config.getKeycloakUsername());
        assertEquals(keycloakPassword, config.getKeycloakPassword());
        assertEquals(keycloakSecret, config.getKeycloakSecret());
        assertEquals(mtsClientApiHost, config.getMtsClientApiHost());
        assertEquals(ticketResponseTimeout, config.getTicketResponseTimeout());
        assertEquals(ticketCancellationResponseTimeout, config.getTicketCancellationResponseTimeout());
        assertEquals(ticketCashoutResponseTimeout, config.getTicketCashoutResponseTimeout());
    }
}