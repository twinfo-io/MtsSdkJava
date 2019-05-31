/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ChannelWrapper for the RabbitMq channel
 */
public final class ChannelWrapper implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ChannelWrapper.class);

    private final ConnectionWrapper parentConnection;
    private final long index;
    private final Channel underlyingChannel;

    ChannelWrapper(final ConnectionWrapper parentConnection, final long index, final Channel underlyingChannel) {
        this.parentConnection = parentConnection;
        this.index = index;
        this.underlyingChannel = underlyingChannel;
    }

    long getIndex() {
        return index;
    }

    public Channel getChannel() {
        return this.underlyingChannel;
    }

    @Override
    public void close() {
        try {
            if (this.underlyingChannel != null) {
                this.underlyingChannel.close();
            }
        } catch (Exception exc) {
            logger.warn("Channel close error: ", exc);
        }
        this.parentConnection.releaseChannel(this.index);
    }
}
