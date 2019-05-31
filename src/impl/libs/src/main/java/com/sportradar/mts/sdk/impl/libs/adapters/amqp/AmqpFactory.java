/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

public interface AmqpFactory {

    AmqpProducer createProducer(String instanceName,
                                AmqpCluster mqCluster,
                                String exchangeName,
                                ExchangeType exchangeType,
                                int maxRetryCount,
                                int maxBufferSize,
                                int concurrencyLevel,
                                boolean msgMemOnly,
                                boolean waitForPublishConfirmations,
                                boolean mandatory);

    AmqpConsumer createConsumer(String instanceName,
                                AmqpCluster mqCluster,
                                String exchangeName,
                                ExchangeType exchangeType,
                                String queueName,
                                String routingKey,
                                int maxRetryCount,
                                int prefetchCount,
                                int concurrencyLevel,
                                boolean deleteQueueOnClose,
                                boolean exclusiveConsumer);
}
