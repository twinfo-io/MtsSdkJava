/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.google.common.collect.Lists;
import com.rabbitmq.client.*;
import com.sportradar.mts.sdk.api.impl.ConnectionStatusImpl;
import com.sportradar.mts.sdk.api.interfaces.ConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

final class ConnectionWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionWrapper.class);

    private final ThreadLocal<Long> connectionIndex = ThreadLocal.withInitial(() -> 0L);

    private final ChannelFactoryProviderImpl parentChannelFactoryProvider;
    private final ConnectionFactory connectionFactory;
    private final AmqpCluster cluster;
    private final DummyAddressResolver addressResolver;
    private final TreeMap<Long, ConnectionHolder> connections = new TreeMap<>();
    private long lastIndex = 0;
    private final ConnectionStatusImpl connectionStatus;

    ConnectionWrapper(final ChannelFactoryProviderImpl channelFactoryProvider,
                      final ConnectionFactory connectionFactory,
                      final AmqpCluster cluster,
                      ConnectionStatus connectionStatus) {
        this.parentChannelFactoryProvider = channelFactoryProvider;
        this.connectionFactory = connectionFactory;
        this.cluster = cluster;
        this.addressResolver = new DummyAddressResolver(extractAddresses(this.cluster));
        this.connectionStatus = (ConnectionStatusImpl) connectionStatus;
    }

    ChannelWrapper getChannel() throws IOException, TimeoutException {
        final long currentIndex = this.connectionIndex.get();
        final ChannelWrapper result = this.getChannel(currentIndex);
        this.connectionIndex.set(result.getIndex());
        return result;
    }

    void releaseChannel(final long index) {
        this.release(index);
    }

    private Address[] extractAddresses(final AmqpCluster cluster) {
        final Address[] result = new Address[cluster.getAddresses().length];
        for (int i = 0; i < cluster.getAddresses().length; i++) {
            NetworkAddress addressTmp = cluster.getAddresses()[i];
            result[i] = new Address(addressTmp.getHost(), addressTmp.getPort());
        }
        return result;
    }

    private synchronized void release(final long index) {
        try {
            final ConnectionHolder connectionHolder = this.connections.get(index);
            if (connectionHolder == null) {
                return;
            }
            connectionHolder.count--;
            this.removeClosedConnections();
        } finally {
            this.logConnections();
            connectionIndex.remove();
        }
    }

    private synchronized ChannelWrapper getChannel(final long index) throws IOException, TimeoutException {
        try {
            this.removeClosedConnections();
            try {
                // allow same connection
                return this.getOrCreateChannel(index - 1L);
            } catch (Exception exc) {
                logger.warn("Get or create channel error. Retrying with new connection... ", exc);
                // force new connection
                return this.getOrCreateChannel(index);
            }
        } finally {
            this.logConnections();
        }
    }

    private ChannelWrapper getOrCreateChannel(final long index) throws IOException, TimeoutException {
        final ConnectionHolder connectionHolder = this.getConnectionHolder(index);
        final Channel channel = connectionHolder.connection.createChannel();
        connectionHolder.count++;
        return new ChannelWrapper(this, connectionHolder.index, channel);
    }

    private ConnectionHolder getConnectionHolder(final long index) throws IOException, TimeoutException {
        if (!this.connections.isEmpty()) {
            final ConnectionHolder lastConnectionHolder = this.connections.lastEntry().getValue();
            if ((lastConnectionHolder.index > index) && (lastConnectionHolder.connection.isOpen())) {
                return lastConnectionHolder;
            }
        }

        final long nextIndex = (this.lastIndex + 1);
        final Connection connection = this.connectionFactory.newConnection(this.parentChannelFactoryProvider.getExecutorService(),
                                                                           this.addressResolver);
        this.lastIndex = nextIndex;
        final ConnectionHolder result = new ConnectionHolder(this.lastIndex, connection);
        this.connections.put(this.lastIndex, result);
        this.connectionStatus.connect("Connection established.");
        return result;
    }

    private void logConnections() {
        StringBuilder sb = new StringBuilder(256);
        sb.append('\n');
        sb.append("AMQP connections to ").append(this.cluster.getDescription()).append(" are: [").append('\n');
        for (final Map.Entry<Long, ConnectionHolder> entry : this.connections.entrySet()) {
            final ConnectionHolder holder = entry.getValue();
            sb.append("   Connection {index: ").append(holder.index).append(", channels: ").append(holder.count).append(
                    "}").append('\n');
        }
        sb.append("]");
        String msg = sb.toString();
        logger.debug(msg);
    }

    private void removeClosedConnections() {

        final Set<Long> removeConnectionIndexes = new HashSet<>();
        for (final Map.Entry<Long, ConnectionHolder> entry : this.connections.entrySet()) {
            final ConnectionHolder connectionHolder = entry.getValue();

            if (connectionHolder.connection == null) {
                removeConnectionIndexes.add(entry.getKey());
                continue;
            }

            if ((connectionHolder.count <= 0) || (!connectionHolder.connection.isOpen())) {
                try {
                    connectionHolder.connection.close();
                } catch (Exception exc) {
                    logger.warn("Connection close error: ", exc);
                }
                removeConnectionIndexes.add(entry.getKey());
            }
        }

        for (final Long index : removeConnectionIndexes) {
            this.connections.remove(index);
        }
    }

    private final class ConnectionHolder {

        private final long index;
        private final Connection connection;
        private int count;

        ConnectionHolder(final long index, final Connection connection) {
            this.index = index;
            this.connection = connection;
            this.connection.addShutdownListener(new ConnectionShutdownHandler());
            this.connection.addBlockedListener(new ConnectionBlockedHandler());
        }
    }

    private static class DummyAddressResolver implements AddressResolver {

        private List<Address> addresses;

        DummyAddressResolver(Address[] addresses) {
            this.addresses = Lists.newArrayList(addresses);
        }

        @Override
        public List<Address> getAddresses() {
            return this.addresses;
        }
    }

    private class ConnectionShutdownHandler implements ShutdownListener{

        @Override
        public void shutdownCompleted(ShutdownSignalException e) {
            if(!e.isInitiatedByApplication())
            {
                logger.warn("Connection shutdown invoked.");
                connectionStatus.disconnect("Connection shutdown invoked. Message: " + e.getMessage());
            }
        }
    }

    private class ConnectionBlockedHandler implements BlockedListener{

        @Override
        public void handleBlocked(String s) throws IOException {
            logger.warn("Connection blocked invoked.");
        }

        @Override
        public void handleUnblocked() throws IOException {
            logger.warn("Connection unblocked invoked.");
        }
    }
}
