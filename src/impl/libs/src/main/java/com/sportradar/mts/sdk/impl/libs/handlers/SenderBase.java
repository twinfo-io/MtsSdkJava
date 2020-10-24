/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.google.common.util.concurrent.RateLimiter;
import com.sportradar.mts.sdk.api.SdkTicket;
import com.sportradar.mts.sdk.api.interfaces.MessageSender;
import com.sportradar.mts.sdk.api.interfaces.PublishResultListener;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublishResultListener;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisher;
import com.sportradar.mts.sdk.impl.libs.logging.SdkLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class SenderBase<T extends SdkTicket> implements MessageSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Object stateLock = new Object();
    private final SdkLogger sdkLogger;
    private final AmqpPublisher amqpPublisher;
    private final ExecutorService executorService;
    private final Map<String, T> messages;
    private final RateLimiter rateLimiter;
    private boolean opened;

    public SenderBase(AmqpPublisher amqpPublisher,
                      ExecutorService executorService,
                      double messagesPerSecond,
                      SdkLogger sdkLogger) {
        checkNotNull(amqpPublisher, "amqpPublisher cannot be null");
        checkNotNull(executorService, "executorService cannot be null");
        checkNotNull(sdkLogger, "sdkLogger cannot be null");
        this.amqpPublisher = amqpPublisher;
        this.executorService = executorService;
        this.sdkLogger = sdkLogger;
        this.messages = new HashMap<>();
        this.rateLimiter = RateLimiter.create(messagesPerSecond);
    }

    @Override
    public void open() {
        synchronized (stateLock) {
            amqpPublisher.open();
            opened = true;
        }
    }

    @Override
    public void close() {
        synchronized (stateLock) {
            amqpPublisher.close();
            opened = false;
        }
    }

    @Override
    public boolean isOpen() {
        return opened;
    }

    protected void publishAsync(T message, String routingKey) {
        publishAsync(message, routingKey, routingKey);
    }

    protected void publishAsync(T message, String routingKey, String replyRoutingKey) {
        rateLimiter.acquire();
        //String msgString = JsonUtils.serializeAsString(message);
        logger.trace("PUBLISH ticket:{}, correlationId:{}, routingKey:{}, replyRoutingKey:{}",
                message.getTicketId(),
                message.getCorrelationId(),
                routingKey,
                replyRoutingKey);
        logger.trace("PUBLISH {}", message.getJsonValue());
        String msgString = getSerializedDto(message);
        getSdkLogger().logSendMessage(msgString);
        if(StringUtils.isNullOrEmpty(message.getCorrelationId()))
        {
            logger.warn("Ticket %s is missing correlationId", message.getTicketId());
        }
        messages.put(message.getCorrelationId(), message);
        amqpPublisher.publishAsync(message.getTicketId(),
                                   msgString.getBytes(StandardCharsets.UTF_8),
                                   message.getCorrelationId(),
                                   routingKey,
                                   replyRoutingKey);
    }

    protected abstract String getSerializedDto(T message);

    protected SdkLogger getSdkLogger() {
        return sdkLogger;
    }

    protected void setPublishListener(PublishResultListener<T> publishResultListener) {
        amqpPublisher.setListener(new AmqpPublishResultListener() {
            @Override
            public void publishSuccess(String correlationId) {
                executorService.submit(() -> {
                    T message = getMessage(correlationId);
                    if (message == null) {
                        return;
                    }
                    messages.remove(correlationId);
                    publishResultListener.publishSuccess(message);
                });
            }

            @Override
            public void publishFailure(String correlationId) {
                onPublishFailure(correlationId);

                executorService.submit(() -> {
                    T message = getMessage(correlationId);
                    if (message == null) {
                        return;
                    }
                    messages.remove(correlationId);
                    publishResultListener.publishFailure(message);
                });
            }
        });
    }

    private T getMessage(String correlationId) {
        T message = messages.get(correlationId);
        if (message == null) {
            logger.error("no message for correlation id:{} was found", correlationId);
        }
        return message;
    }

    protected void onPublishFailure(String correlationId) {
        // NO-OP - default
    }
}
