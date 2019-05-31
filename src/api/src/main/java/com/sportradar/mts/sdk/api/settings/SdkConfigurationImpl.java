/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.settings;

import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.enums.UfEnvironment;
import com.sportradar.mts.sdk.api.exceptions.MtsPropertiesException;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

public class SdkConfigurationImpl implements SdkConfiguration {
    private static final String DEFAULT_SETTINGS_FILE_NAME = "/mts-sdk.properties";

    private final String username;
    private final String password;
    private final String host;
    private final String vhost;
    private final int node;
    private final boolean ssl;
    private final int port;
    private final int bookmakerId;
    private final int limitId;
    private final String currency;
    private final SenderChannel senderChannel;
    private final String accessToken;
    private final boolean provideAdditionalMarketSpecifiers;
    private final int ticketResponseTimeout;
    private final int ticketCancellationResponseTimeout;
    private final int ticketCashoutResponseTimeout;
    private final int ticketNonSrSettleResponseTimeout;
    private final double messagesPerSecond;
    private final boolean ticketTimeOutCallbackEnabled;
    private final boolean exclusiveConsumer;
    private final String keycloakHost;
    private final String keycloakUsername;
    private final String keycloakPassword;
    private final String keycloakSecret;
    private final String mtsClientApiHost;
    private final UfEnvironment ufEnvironment;

    protected SdkConfigurationImpl(String username,
                                   String password,
                                   String host,
                                   String vhost,
                                   int node,
                                   boolean ssl,
                                   int port,
                                   int ticketResponseTimeout,
                                   int ticketCancellationResponseTimeout,
                                   int ticketCashoutResponseTimeout,
                                   int ticketNonSrSettleResponseTimeout,
                                   double messagesPerSecond,
                                   int bookmakerId,
                                   int limitId,
                                   String currency,
                                   SenderChannel senderChannel,
                                   String accessToken,
                                   UfEnvironment ufEnvironment,
                                   boolean provideAdditionalMarketSpecifiers,
                                   boolean ticketTimeOutCallbackEnabled,
                                   boolean exclusiveConsumer,
                                   String keycloakHost,
                                   String keycloakUsername,
                                   String keycloakPassword,
                                   String keycloakSecret,
                                   String mtsClientApiHost)
    {
        this.username = username;
        this.password = password;
        this.host = host;
        this.vhost = vhost;
        this.node = node;
        this.ssl = ssl;
        if (port > 0)
        {
            this.port = port;
        }
        else
        {
            this.port = ssl ? 5671 : 5672;
        }
        this.bookmakerId = bookmakerId;
        this.limitId = limitId;
        this.currency = currency;
        this.senderChannel = senderChannel;
        this.accessToken = accessToken;
        this.ufEnvironment = ufEnvironment;
        this.provideAdditionalMarketSpecifiers = provideAdditionalMarketSpecifiers;

        this.ticketResponseTimeout = ticketResponseTimeout;
        this.ticketCancellationResponseTimeout = ticketCancellationResponseTimeout;
        this.ticketCashoutResponseTimeout = ticketCashoutResponseTimeout;
        this.ticketNonSrSettleResponseTimeout = ticketNonSrSettleResponseTimeout;
        this.messagesPerSecond = messagesPerSecond;
        this.ticketTimeOutCallbackEnabled = ticketTimeOutCallbackEnabled;
        this.exclusiveConsumer = exclusiveConsumer;

        this.keycloakHost = keycloakHost;
        this.keycloakUsername = keycloakUsername;
        this.keycloakPassword = keycloakPassword;
        this.keycloakSecret = keycloakSecret;
        this.mtsClientApiHost = mtsClientApiHost;
    }

    protected SdkConfigurationImpl(Properties properties)
    {
        SdkConfiguration config = getConfigInternal(properties);
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.host = config.getHost();
        this.vhost = config.getVirtualHost();
        this.node = config.getNode();
        this.ssl = config.getUseSsl();
        if (config.getPort() > 0)
        {
            this.port = config.getPort();
        }
        else
        {
            this.port = this.ssl ? 5671 : 5672;
        }
        this.bookmakerId = config.getBookmakerId();
        this.limitId = config.getLimitId();
        this.currency = config.getCurrency();
        this.senderChannel = config.getSenderChannel();
        this.accessToken = config.getAccessToken();
        this.ufEnvironment = config.getUfEnvironment();
        this.provideAdditionalMarketSpecifiers = config.getProvideAdditionalMarketSpecifiers();

        // also check PropertiesToSettingsMapper
        this.ticketResponseTimeout = config.getTicketResponseTimeout();
        this.ticketCancellationResponseTimeout = config.getTicketCancellationResponseTimeout();
        this.ticketCashoutResponseTimeout = config.getTicketCashoutResponseTimeout();
        this.ticketNonSrSettleResponseTimeout = config.getTicketNonSrSettleResponseTimeout();
        this.messagesPerSecond = config.getMessagesPerSecond();

        this.ticketTimeOutCallbackEnabled = config.isTicketTimeOutCallbackEnabled();
        this.exclusiveConsumer = config.getExclusiveConsumer();

        this.keycloakHost = config.getKeycloakHost();
        this.keycloakUsername = config.getKeycloakUsername();
        this.keycloakPassword = config.getKeycloakPassword();
        this.keycloakSecret = config.getKeycloakSecret();
        this.mtsClientApiHost = config.getMtsClientApiHost();
    }

    @Override
    public int getNode() { return node; }

    @Override
    public String getVirtualHost() { return vhost; }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean getUseSsl() {
        return ssl;
    }

    @Override
    public int getBookmakerId() { return bookmakerId; }

    @Override
    public int getLimitId() { return limitId; }

    @Override
    public String getCurrency() { return currency; }

    @Override
    public SenderChannel getSenderChannel() { return senderChannel; }

    @Override
    public String getAccessToken() { return accessToken; }

    @Override
    public UfEnvironment getUfEnvironment() {
        return ufEnvironment;
    }

    @Override
    public boolean getProvideAdditionalMarketSpecifiers() { return provideAdditionalMarketSpecifiers; }

    @Override
    public boolean isTicketTimeOutCallbackEnabled() {
        return ticketTimeOutCallbackEnabled;
    }

    public int getTicketResponseTimeout() { return ticketResponseTimeout; }

    public int getTicketCancellationResponseTimeout() { return ticketCancellationResponseTimeout; }

    public int getTicketCashoutResponseTimeout() { return ticketCashoutResponseTimeout; }

    public int getTicketNonSrSettleResponseTimeout() { return ticketNonSrSettleResponseTimeout; }

    public double getMessagesPerSecond() { return messagesPerSecond; }

    @Override
    public boolean getExclusiveConsumer() {
        return exclusiveConsumer;
    }

    @Override
    public String getKeycloakHost() {
        return keycloakHost;
    }

    @Override
    public String getKeycloakUsername() {
        return keycloakUsername;
    }

    @Override
    public String getKeycloakPassword() {
        return keycloakPassword;
    }

    @Override
    public String getKeycloakSecret() {
        return keycloakSecret;
    }

    @Override
    public String getMtsClientApiHost() {
        return mtsClientApiHost;
    }

    @Override
    public String toString() {
        return "SdkConfiguration{" +
                "username='" + "*" + '\'' +
                ", password='" + "*" + '\'' +
                ", host='" + host + '\'' +
                ", node=" + node +
                ", vhost='" + vhost + '\'' +
                ", ssl=" + ssl +
                ", port=" + port +
                ", bookmakerId=" + bookmakerId +
                ", limitId=" + limitId +
                ", currency='" + currency + '\'' +
                ", senderChannel='" + senderChannel + '\'' +
                ", accessToken='*'" +
                ", ufEnvironment='" + ufEnvironment + '\'' +
                ", provideAdditionalMarketSpecifiers='" + provideAdditionalMarketSpecifiers + '\'' +
                ", ticketResponseTimeout=" + ticketResponseTimeout +
                ", ticketCancellationResponseTimeout=" + ticketCancellationResponseTimeout +
                ", ticketCashoutResponseTimeout=" + ticketCashoutResponseTimeout +
                ", ticketNonSrSettleResponseTimeout=" + ticketNonSrSettleResponseTimeout +
                ", messagesPerSecond=" + messagesPerSecond +
                ", exclusiveConsumer=" + exclusiveConsumer +
                ", keycloakHost='" + keycloakHost + '\'' +
                ", keycloakUsername='" + "*" + '\'' +
                ", keycloakPassword='" + "*" + '\'' +
                ", keycloakSecret='" + "*" + '\'' +
                ", mtsClientApiHost='" + mtsClientApiHost + '\'' +
                '}';
    }

    /**
     * Gets the SdkConfiguration from the default settings file ("/mts-sdk.properties")
     * @return SdkConfiguration
     */
    public static SdkConfiguration getConfiguration() {
        try {
            final InputStream fileStream = SdkConfiguration.class.getResourceAsStream(DEFAULT_SETTINGS_FILE_NAME);
            return getConfigWithFile(fileStream);
        } catch (IOException e) {
            throw new MtsPropertiesException(e.getMessage(), e);
        }
    }

    public static SdkConfiguration getConfiguration(String path) {
        try {
            return getConfigWithFile(FileUtils.filePathAsInputStream(path));
        } catch (IOException e) {
            throw new MtsPropertiesException(e.getMessage(), e);
        }
    }

    public static SdkConfiguration getConfiguration(Properties properties) {
        return getConfigInternal(properties);
    }

    /**
     * Gets the SdkConfiguration from the 'application.yml' file located in the resources folder
     *
     * @return {@link SdkConfiguration}
     */
    public static SdkConfiguration getConfigurationFromYaml() {
        SdkYamlConfigurationReader sdkYamlConfigurationReader = new SdkYamlConfigurationReader();
        Properties propertiesFromYaml = sdkYamlConfigurationReader.readPropertiesFromYaml();

        return getConfigInternal(propertiesFromYaml);
    }

    private static SdkConfiguration getConfigWithFile(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        checkNotNull(inputStream, "properties file not found");
        properties.load(inputStream);
        return getConfigInternal(properties);
    }

    private static SdkConfiguration getConfigInternal(Properties properties) {
        checkNotNull(properties, "properties cannot be null");
        return PropertiesToSettingsMapper.getSettings(properties);
    }
}
