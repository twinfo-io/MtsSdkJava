/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.sportradar.mts.sdk.api.interfaces.Openable;

import java.util.Map;

public interface AmqpMessageReceiver extends Openable {

    MessageStatus consume(byte[] msg, String routingKey, String correlationId, Map<String, Object> messageHeaders);

    void afterLimitReached(byte[] msg, String routingKey, String correlationId);
}
