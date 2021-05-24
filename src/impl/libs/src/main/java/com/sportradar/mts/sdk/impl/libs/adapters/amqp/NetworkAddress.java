/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

public final class NetworkAddress {

    private final String host;
    private final int port;

    public NetworkAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public NetworkAddress(String host) {
        this.host = host;
        this.port = -1;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public static NetworkAddress parseAddress(final String addressString) {
        final int idx = addressString.indexOf(58);
        return ((idx == -1)
                ? new NetworkAddress(addressString.trim())
                : new NetworkAddress(addressString.substring(0, idx).trim(), Integer.parseInt(addressString.substring(idx + 1))));
    }

    public int hashCode() {
        return 31 * this.host.hashCode() + this.port;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj != null) && (this.getClass() == obj.getClass())) {
            NetworkAddress address = (NetworkAddress) obj;
            return (this.host.equals(address.host) && (this.port == address.port));
        }
        return false;
    }

    public String toString() {
        return this.port == -1 ? this.host : this.host + ":" + this.port;
    }
}
