/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.enums.UfEnvironment;

/**
 * Defines a contract for classes implementing builder for {@link SdkConfiguration}
 */
public interface SdkConfigurationBuilder {

    /**
     * Sets the username
     * @param username to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setUsername(String username);

    /**
     * Sets the password
     * @param password to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setPassword(String password);

    /**
     * Sets the host used to connect to AMQP broker
     * @param host to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setHost(String host);

    /**
     * Sets the port used to connect to AMQP broker. Port should be set through the setUseSsl method. Manually setting port number should be used only when non-default port is required.
     * @param port to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setPort(int port);

    /**
     * Sets the virtual host
     * @param vhost to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setVirtualHost(String vhost);

    /**
     * Sets the node id
     * @param nodeId to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setNode(int nodeId);

    /**
     * Sets whether ssl should be used
     * @param useSsl value to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setUseSsl(boolean useSsl);

    /**
     * Sets the bookmakerId
     * @param bookmakerId to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setBookmakerId(int bookmakerId);
    
    /**
     * Sets the limitId
     * @param limitId to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setLimitId(int limitId);
    
    /**
     * Sets the currency
     * @param currency to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setCurrency(String currency);

    /**
     * Sets the sender channel
     * @param channel to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setSenderChannel(SenderChannel channel);

    /**
     * Sets the access token
     * @param accessToken to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setAccessToken(String accessToken);

    /**
     * Sets the uf environment
     *
     * @param ufEnvironment to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setUfEnvironment(UfEnvironment ufEnvironment);

    /**
     * This value is used to indicate if the sdk should add market specifiers for specific markets. Only used when building selection using UnifiedOdds ids. (default: true) If this is set to true and the user uses UOF markets, when there are special cases (market 215, or $score in SOV/SBV template), sdk automatically tries to add appropriate specifier; if set to false, user will need to add this manually.
     * @param provideAdditionalMarketSpecifiers value to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setProvideAdditionalMarketSpecifiers(boolean provideAdditionalMarketSpecifiers);

    /**
     * Sets the value indicating if the {@link com.sportradar.mts.sdk.api.Ticket}s sent async have the time-out callback enabled
     *
     * @param ticketTimeOutCallbackEnabled value to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setTicketTimeOutCallbackEnabled(boolean ticketTimeOutCallbackEnabled);

    /**
     * Sets the ticket response timeout(ms). This value is being used only if the ticket is sent blocking or {@link #setTicketTimeOutCallbackEnabled(boolean)} is set to <code>true</code>
     *
     * @param responseTimeout the ticket response timeout to set(ms) (will set for lcoo and live responses)
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setTicketResponseTimeout(int responseTimeout);

    /**
     * Sets the ticket response timeout(ms). This value is being used only if the ticket is sent blocking or {@link #setTicketTimeOutCallbackEnabled(boolean)} is set to <code>true</code>
     *
     * @param responseTimeout the ticket response timeout to set(ms)
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setTicketResponseTimeoutLive(int responseTimeout);

    /**
     * Sets the ticket response timeout(ms). This value is being used only if the ticket is sent blocking or {@link #setTicketTimeOutCallbackEnabled(boolean)} is set to <code>true</code>
     *
     * @param responseTimeout the ticket response timeout to set(ms)
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setTicketResponseTimeoutPrematch(int responseTimeout);

    /**
     * Sets the ticket cancellation response timeout(ms). This value is being used only if the ticket is sent blocking or {@link #setTicketTimeOutCallbackEnabled(boolean)} is set to <code>true</code>
     *
     * @param responseTimeout the ticket cancellation response timeout to set(ms)
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setTicketCancellationResponseTimeout(int responseTimeout);

    /**
     * Sets the ticket cashout response timeout(ms). This value is being used only if the ticket is sent blocking or {@link #setTicketTimeOutCallbackEnabled(boolean)} is set to <code>true</code>
     *
     * @param responseTimeout the ticket cashout response timeout to set(ms)
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setTicketCashoutResponseTimeout(int responseTimeout);

    /**
     * Sets the ticket non-Sportradar response timeout(ms). This value is being used only if the ticket is sent blocking or {@link #setTicketTimeOutCallbackEnabled(boolean)} is set to <code>true</code>
     *
     * @param responseTimeout the ticket non-Sportradar response timeout to set(ms)
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setTicketNonSrSettleResponseTimeout(int responseTimeout);

    /**
     * Sets whether the rabbit consumer channel should be exclusive
     * @param exclusiveConsumer value to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setExclusiveConsumer(boolean exclusiveConsumer);

    /**
     * Sets the Keycloak host for authorization
     *
     * @param keycloakHost the Keycloak host to be set
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setKeycloakHost(String keycloakHost);

    /**
     * Sets the username used to connect authenticate to Keycloak
     *
     * @param keycloakUsername the username used to connect authenticate to Keycloak
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setKeycloakUsername(String keycloakUsername);

    /**
     * Sets the password used to connect authenticate to Keycloak
     *
     * @param keycloakPassword the password used to connect authenticate to Keycloak
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setKeycloakPassword(String keycloakPassword);

    /**
     * Sets the secret used to connect authenticate to Keycloak
     *
     * @param keycloakSecret the secret used to connect authenticate to Keycloak
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setKeycloakSecret(String keycloakSecret);

    /**
     * Sets the Client API host
     *
     * @param mtsClientApiHost the Client API host
     * @return {@link SdkConfigurationBuilder}
     */
    SdkConfigurationBuilder setMtsClientApiHost(String mtsClientApiHost);

    /**
     * Build and return the {@link SdkConfiguration}
     * @return {@link SdkConfiguration}
     */
    SdkConfiguration build();
}
