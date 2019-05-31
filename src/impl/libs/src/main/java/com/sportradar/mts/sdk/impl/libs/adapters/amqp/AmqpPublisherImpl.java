/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class AmqpPublisherImpl implements AmqpPublisher {

    private final Object stateLock = new Object();
    private final AmqpProducer messageSender;
    private final AmqpSendResultHandler messageHandler;
    private boolean opened;

    public AmqpPublisherImpl(AmqpProducer messageSender,
                             AmqpSendResultHandler sendResultHandler
    ) {
        checkNotNull(messageSender, "messageSender cannot be null");
        checkNotNull(sendResultHandler, "sendResultHandler cannot be null");
        this.messageSender = messageSender;
        this.messageHandler = sendResultHandler;
    }

    @Override
    public void publishAsync(byte[] msg,
                             String correlationId,
                             String routingKey,
                             String replyRoutingKey) {
        checkState(isOpen(), "sender is not open");
        HashMap<String, Object> messageHeaders = new HashMap<>();
        messageHeaders.put("replyRoutingKey", replyRoutingKey);
        AmqpSendResult sendResult = messageSender.sendAsync(correlationId, msg, routingKey, messageHeaders);
        messageHandler.handleSendResult(sendResult);
    }

    @Override
    public void setListener(AmqpPublishResultListener listener) {
        this.messageSender.setReturnListener((replyCode, replyText, exchange, routingKey, properties, body) ->
                                                     listener.publishFailure(properties.getCorrelationId())
        );
        this.messageHandler.setPublishResultListener(new AmqpPublishResultListener() {
            @Override
            public void publishSuccess(String correlationId) {
                listener.publishSuccess(correlationId);
            }

            @Override
            public void publishFailure(String correlationId) {
                listener.publishFailure(correlationId);
            }
        });
    }

    @Override
    public void open() {
        synchronized (stateLock) {
            if (!opened) {
                messageHandler.open();
                messageSender.open();
                opened = true;
            }
        }
    }

    @Override
    public void close() {
        synchronized (stateLock) {
            if (opened) {
                messageSender.close();
                messageHandler.close();
                opened = false;
            }
        }
    }

    @Override
    public boolean isOpen() {
        return opened;
    }
}
