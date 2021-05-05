/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.settings;

import com.sportradar.mts.sdk.api.exceptions.MtsYamlException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created on 13/04/2018.
 * Sdk yaml configuration test
 */
public class SdkYamlConfigurationTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void samplePropertiesLoad() {
        SdkYamlConfigurationReader provider = new SdkYamlConfigurationReader();
        Properties propertiesFromYaml = provider.readPropertiesFromYaml("yaml/valid-cfg.yml");

        assertEquals("test-uname", propertiesFromYaml.getProperty(SettingsKeys.USERNAME));
        assertEquals("test-pass", propertiesFromYaml.getProperty(SettingsKeys.PASSWORD));
        assertEquals("test-hostname", propertiesFromYaml.getProperty(SettingsKeys.HOST));
        assertEquals("test-vhost", propertiesFromYaml.getProperty(SettingsKeys.VIRTUAL_HOST));
        assertEquals("false", propertiesFromYaml.getProperty(SettingsKeys.SSL));
        assertEquals("7777", propertiesFromYaml.getProperty(SettingsKeys.PORT));
        assertEquals("46", propertiesFromYaml.getProperty(SettingsKeys.NODE_ID));
        assertEquals("5555", propertiesFromYaml.getProperty(SettingsKeys.BOOKMAKER_ID));
        assertEquals("1000", propertiesFromYaml.getProperty(SettingsKeys.LIMIT_ID));
        assertEquals("btc", propertiesFromYaml.getProperty(SettingsKeys.CURRENCY));
        assertEquals("CALLCENTRE", propertiesFromYaml.getProperty(SettingsKeys.CHANNEL));
        assertEquals("test-tkn", propertiesFromYaml.getProperty(SettingsKeys.ACCESS_TOKEN));
        assertEquals("false", propertiesFromYaml.getProperty(SettingsKeys.PROVIDE_ADDITIONAL_MARKET_SPECIFIERS));
        assertEquals("300", propertiesFromYaml.getProperty(SettingsKeys.MESSAGES_PER_SECOND));
        assertEquals("false", propertiesFromYaml.getProperty(SettingsKeys.EXCLUSIVE_CONSUMER));
        assertEquals("keycloak-host", propertiesFromYaml.getProperty(SettingsKeys.KEYCLOAK_HOST));
        assertEquals("keycloak-username", propertiesFromYaml.getProperty(SettingsKeys.KEYCLOAK_USERNAME));
        assertEquals("keycloak-password", propertiesFromYaml.getProperty(SettingsKeys.KEYCLOAK_PASSWORD));
        assertEquals("keycloak-secret", propertiesFromYaml.getProperty(SettingsKeys.KEYCLOAK_SECRET));
        assertEquals("client-api", propertiesFromYaml.getProperty(SettingsKeys.MTS_CLIENT_API_HOST));
        assertEquals("99", propertiesFromYaml.getProperty(SettingsKeys.TICKET_RESPONSE_TIMEOUT_LIVE));
        assertEquals("105", propertiesFromYaml.getProperty(SettingsKeys.TICKET_RESPONSE_TIMEOUT_PREMATCH));
        assertEquals("100", propertiesFromYaml.getProperty(SettingsKeys.TICKET_CANCELLATION_RESPONSE_TIMEOUT));
        assertEquals("101", propertiesFromYaml.getProperty(SettingsKeys.TICKET_CASHOUT_RESPONSE_TIMEOUT));
        assertEquals("false", propertiesFromYaml.getProperty(SettingsKeys.TICKET_TIMEOUT_CALLBACK_ENABLED));
    }

    @Test
    public void missingFileLoadTest() {
        thrown.expect(MtsYamlException.class);
        thrown.expectMessage("Could not find yaml/non-existent-file.yml");

        SdkYamlConfigurationReader provider = new SdkYamlConfigurationReader();
        provider.readPropertiesFromYaml("yaml/non-existent-file.yml");
    }

    @Test
    public void invalidRootYamlProperty() {
        thrown.expect(MtsYamlException.class);
        thrown.expectMessage("Could not find the required root property 'sportradar'");

        SdkYamlConfigurationReader provider = new SdkYamlConfigurationReader();
        provider.readPropertiesFromYaml("yaml/invalid-root-property-cfg.yml");
    }

    @Test
    public void invalidSecondYamlProperty() {
        thrown.expect(MtsYamlException.class);
        thrown.expectMessage("Could not find 'sdk' second level yml property");

        SdkYamlConfigurationReader provider = new SdkYamlConfigurationReader();
        provider.readPropertiesFromYaml("yaml/invalid-second-property-cfg.yml");
    }

    @Test
    public void invalidThirdYamlProperty() {
        thrown.expect(MtsYamlException.class);
        thrown.expectMessage("Could not find 'mts' third level yml property");

        SdkYamlConfigurationReader provider = new SdkYamlConfigurationReader();
        provider.readPropertiesFromYaml("yaml/invalid-third-property-cfg.yml");
    }
}
