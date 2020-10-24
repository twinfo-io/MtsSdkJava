package com.sportradar.mts.sdk.api.impl;

import com.sportradar.mts.sdk.api.interfaces.ConnectionChange;

public class ConnectionChangeImpl implements ConnectionChange {

    private boolean isConnected;
    private String message;

    public ConnectionChangeImpl(boolean isConnected, String message){
        this.isConnected = isConnected;
        this.message = message;
    }

    @Override
    public boolean isConnected() { return isConnected; }

    @Override
    public String getMessage() { return message; }
}
