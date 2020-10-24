package com.sportradar.mts.sdk.api.interfaces;

import java.util.Date;

public interface ConnectionStatus {

    /**
     * Returns a value indicating whether this instance is connected.
     * @return a value indicating whether this instance is connected.
     */
    boolean isConnected();

    /**
     * Gets the time of when connection was made.
     * @return the time of when connection was made.
     */
    Date getConnectionTime();

    /**
     * Gets the time of when disconnection was made.
     * @return the time of when disconnection was made.
     */
    Date getDisconnectionTime();

    /**
     * Gets the last send ticket identifier.
     * @return the last send ticket identifier.
     */
    String getLastSendTicketId();

    /**
     * Gets the last received ticket identifier.
     * @return the last received ticket identifier.
     */
    String getLastReceivedTicketId();
}
