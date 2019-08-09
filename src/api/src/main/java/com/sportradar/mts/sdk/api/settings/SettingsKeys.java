/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.settings;

import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.interfaces.TicketSender;

/**
 * Settings keys used in mapping properties object to SDK settings
 */
public class SettingsKeys {

    private static final String PREFIX = "mts.sdk.";
    /**
     * Username to use when connecting to the MTS rabbit
     */
    public static final String USERNAME = PREFIX + "username";
    /**
     * Password to use when connecting to the MTS rabbit
     */
    public static final String PASSWORD = PREFIX + "password";
    /**
     * Hostname to use when connecting to the MTS rabbit
     */
    public static final String HOST = PREFIX + "hostname";
    /**
     * Virtual host to use when connecting to the MTS rabbit
     */
    public static final String VIRTUAL_HOST = PREFIX + "vhost";
    /**
     * Node id to be used when creating routing key
     */
    public static final String NODE_ID = PREFIX + "node";
    /**
     * Use SSL when connecting to MTS rabbit
     */
    public static final String SSL = PREFIX + "ssl";
    /**
     * Port to use when connecting to the MTS rabbit
     */
    public static final String PORT = PREFIX + "port";
    /**
     * Used only when sending with {@link TicketSender#sendBlocking(Ticket)}
     */
    public static final String TICKET_RESPONSE_TIMEOUT_LIVE = PREFIX + "ticketResponseTimeout";
    /**
     * Used only when sending with {@link TicketSender#sendBlocking(Ticket)}
     */
    public static final String TICKET_RESPONSE_TIMEOUT_PREMATCH = PREFIX + "ticketResponseTimeoutPrematch";
    /**
     * Used only when sending with {@link TicketSender#sendBlocking(Ticket)}
     */
    public static final String TICKET_CANCELLATION_RESPONSE_TIMEOUT = PREFIX + "ticketCancellationResponseTimeout";
    /**
     * Used only when sending with {@link TicketSender#sendBlocking(Ticket)}
     */
    public static final String TICKET_CASHOUT_RESPONSE_TIMEOUT = PREFIX + "ticketCashoutResponseTimeout";
    /**
     * Used only when sending with {@link TicketSender#sendBlocking(Ticket)}
     */
    public static final String TICKET_NON_SR_SETTLE_RESPONSE_TIMEOUT = PREFIX + "ticketNonSrSettleResponseTimeout";
    /**
     * Max messages allowed to be send to the MTS for each sender. Default 40
     */
    public static final String MESSAGES_PER_SECOND = PREFIX + "messages_per_second";
//    /**
//     * Connect to client integration environment if true, else production. Default false
//     */
//    public static final String TEST = PREFIX + "test";
    /**
     * Gets the default sender bookmakerId
     */
    public static final String BOOKMAKER_ID = PREFIX + "bookmakerId";
    /**
     * Gets the default sender limitId
     */
    public static final String LIMIT_ID = PREFIX + "limitId";
    /**
     * Gets the default sender currency sign (3-letter ISO)
     */
    public static final String CURRENCY = PREFIX + "currency";
    /**
     * Gets the default sender channel (see SenderChannel for possible values)
     */
    public static final String CHANNEL = PREFIX + "channel";
    /**
     *  Gets the access token for the UoF REST API calls
     */
    public static final String ACCESS_TOKEN = PREFIX + "accessToken";
    /**
     *  Gets the access token for the UoF REST API calls
     */
    public static final String UF_ENVIRONMENT = PREFIX + "ufEnvironment";
    /**
     *  Gets the access token for the UoF REST API calls
     */
    public static final String PROVIDE_ADDITIONAL_MARKET_SPECIFIERS = PREFIX + "provideAdditionalMarketSpecifiers";
    /**
     * An indication if the tickets sent async should have a time-out callback
     */
    public static final String TICKET_TIMEOUT_CALLBACK_ENABLED = PREFIX + "ticketTimeoutCallbackEnabled";
    /**
     * Should the rabbit consumer channel be exclusive
     */
    public static final String EXCLUSIVE_CONSUMER = PREFIX + "exclusiveConsumer";
    /**
     * Gets the Keycloak host for authorization
     */
    public static final String KEYCLOAK_HOST = PREFIX + "keycloakHost";
    /**
     * Gets the username used to connect authenticate to Keycloak
     */
    public static final String KEYCLOAK_USERNAME = PREFIX + "keycloakUsername";
    /**
     * Gets the password used to connect authenticate to Keycloak
     */
    public static final String KEYCLOAK_PASSWORD = PREFIX + "keycloakPassword";
    /**
     * Gets the secret used to connect authenticate to Keycloak
     */
    public static final String KEYCLOAK_SECRET = PREFIX + "keycloakSecret";
    /**
     * Gets the Client API host
     */
    public static final String MTS_CLIENT_API_HOST = PREFIX + "mtsClientApiHost";
}
