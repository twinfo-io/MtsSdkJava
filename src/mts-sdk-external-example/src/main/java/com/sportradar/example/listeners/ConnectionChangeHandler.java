package com.sportradar.example.listeners;

import com.sportradar.mts.sdk.api.interfaces.ConnectionChange;
import com.sportradar.mts.sdk.api.interfaces.ConnectionChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionChangeHandler implements ConnectionChangeListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void connectionChanged(ConnectionChange change) {
        String isConnected = change.isConnected() ? "connected" : "disconnected";
        logger.warn("Connection change happened: {} -> {}", isConnected, change.getMessage());
    }
}
