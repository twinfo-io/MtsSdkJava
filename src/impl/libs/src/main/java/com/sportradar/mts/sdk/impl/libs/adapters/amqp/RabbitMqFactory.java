/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import static com.google.common.base.Preconditions.checkNotNull;

public class RabbitMqFactory implements AmqpFactory {

    private final ChannelFactoryProvider channelFactoryProvider;

    public RabbitMqFactory(ChannelFactoryProvider channelFactoryProvider) {
        checkNotNull(channelFactoryProvider, "channelFactoryProvider cannot be null");
        this.channelFactoryProvider = channelFactoryProvider;
    }

    @Override
    public AmqpProducer createProducer(String instanceName,
                                       AmqpCluster mqCluster,
                                       String exchangeName,
                                       ExchangeType exchangeType,
                                       int maxRetryCount,
                                       int maxBufferSize,
                                       int concurrencyLevel,
                                       boolean msgMemOnly,
                                       boolean waitForPublishConfirmations,
                                       boolean mandatory) {
        return new RabbitMqProducer(channelFactoryProvider,
                                    instanceName,
                                    mqCluster,
                                    exchangeName,
                                    exchangeType,
                                    maxRetryCount,
                                    maxBufferSize,
                                    concurrencyLevel,
                                    msgMemOnly,
                                    waitForPublishConfirmations,
                                    mandatory);
    }

    @Override
    public AmqpConsumer createConsumer(String instanceName,
                                       AmqpCluster mqCluster,
                                       String exchangeName,
                                       ExchangeType exchangeType,
                                       String queueName,
                                       String routingKey,
                                       int maxRetryCount,
                                       int prefetchCount,
                                       int concurrencyLevel,
                                       boolean deleteQueueOnClose,
                                       boolean exclusiveConsumer) {
        return new RabbitMqConsumer(channelFactoryProvider,
                                    routingKey,
                                    instanceName,
                                    mqCluster,
                                    exchangeName,
                                    exchangeType,
                                    queueName,
                                    maxRetryCount,
                                    prefetchCount,
                                    concurrencyLevel,
                                    deleteQueueOnClose,
                                    exclusiveConsumer);
    }
}
