/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

public interface ChannelFactoryProvider {

    void execute(Runnable command);

    void registerInstance();

    void unregisterInstance();

    ChannelFactory getChannelFactory(AmqpCluster mqCluster);

    boolean isExecutorServiceOpened();
}
