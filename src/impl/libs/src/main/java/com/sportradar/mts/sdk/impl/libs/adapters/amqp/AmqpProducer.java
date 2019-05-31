/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.rabbitmq.client.ReturnListener;
import com.sportradar.mts.sdk.api.interfaces.Openable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface AmqpProducer extends Openable {

    String DEFAULT_ROUTING_KEY = "";

    default AmqpSendResult sendAsync(String correlationId, byte[] msg) {
        return sendAsync(correlationId, msg, DEFAULT_ROUTING_KEY, new HashMap<>());
    }

    default AmqpSendResult sendAsync(String correlationId, byte[] msg, Map<String, Object> messageHeaders) {
        return sendAsync(correlationId, msg, DEFAULT_ROUTING_KEY, messageHeaders);
    }

    default AmqpSendResult sendAsync(String correlationId, byte[] msg, String routingKey) {
        return sendAsync(correlationId, msg, routingKey, new HashMap<>());
    }

    AmqpSendResult sendAsync(String correlationId, byte[] msg, String routingKey, Map<String, Object> messageHeaders);

    default boolean sendAsync(String correlationId, byte[] msg, Consumer<AmqpSendResult> doneCallback) {
        return sendAsync(correlationId, msg, DEFAULT_ROUTING_KEY, new HashMap<>(), doneCallback);
    }

    default boolean sendAsync(String correlationId,
                              byte[] msg,
                              Map<String, Object> messageHeaders,
                              Consumer<AmqpSendResult> doneCallback) {
        return sendAsync(correlationId, msg, DEFAULT_ROUTING_KEY, messageHeaders, doneCallback);
    }

    default boolean sendAsync(String correlationId,
                              byte[] msg,
                              String routingKey,
                              Consumer<AmqpSendResult> doneCallback) {
        return sendAsync(correlationId, msg, routingKey, new HashMap<>(), doneCallback);
    }

    boolean sendAsync(String correlationId,
                      byte[] msg,
                      String routingKey,
                      Map<String, Object> messageHeaders,
                      Consumer<AmqpSendResult> doneCallback);

    default boolean send(String correlationId, byte[] msg) {
        return send(correlationId, msg, DEFAULT_ROUTING_KEY, new HashMap<>());
    }

    default boolean send(String correlationId, byte[] msg, Map<String, Object> messageHeaders) {
        return send(correlationId, msg, DEFAULT_ROUTING_KEY, messageHeaders);
    }

    default boolean send(String correlationId, byte[] msg, String routingKey) {
        return send(correlationId, msg, routingKey, new HashMap<>());
    }

    boolean send(String correlationId, byte[] msg, String routingKey, Map<String, Object> messageHeaders);


    void setReturnListener(ReturnListener returnListener);
}
