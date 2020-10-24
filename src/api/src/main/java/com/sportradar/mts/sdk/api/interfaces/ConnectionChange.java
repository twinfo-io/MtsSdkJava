package com.sportradar.mts.sdk.api.interfaces;

public interface ConnectionChange {

    /**
     * Gets an indicator if the connection is on
     * @return an indicator if the connection is on
     */
    boolean isConnected();

    /**
     * Gets the message related to the connection change
     * @return the message related to the connection change
     */
    String getMessage();
}
