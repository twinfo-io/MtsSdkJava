/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.TicketCashoutResponse;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;
import com.sportradar.mts.sdk.api.interfaces.TicketCashoutResponseListener;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisher;
import com.sportradar.mts.sdk.impl.libs.logging.SdkLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * An implementation of the {@link TicketCashoutHandler} used to handle {@link TicketCashout} messages
 */
public class TicketCashoutHandlerImpl extends SenderBase<TicketCashout> implements TicketCashoutHandler {
    /**
     * The main class logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TicketCashoutHandlerImpl.class);

    /**
     * The routing key used for {@link TicketCashout} messages
     */
    private final String routingKey;

    /**
     * The routing key used to receive {@link TicketCashoutResponse} messages
     */
    private final String replyRoutingKey;

    /**
     * The listener that will be used to dispatch received messages
     */
    private TicketCashoutResponseListener ticketCashoutResponseListener;

    /**
     * The executor for dispatching received messages
     */
    private final ExecutorService executorService;

    /**
     * The async tickets response time-out handler
     */
    private final ResponseTimeoutHandler<TicketCashout> timeoutHandler;

    /**
     * The max allowed cashout response time used for <code>sendBlocking</code>
     */
    private final int responseTimeout;

    /**
     * The caching used to store send messages that are used to check if the message is <code>sendBlocking</code>
     */
    private final Cache<String, TicketCashoutSendEntry> ticketCashoutSendEntryCache;

    /**
     * The caching used to store response messages so they can be utilized as the return for <code>sendBlocking</code>
     */
    private final Cache<String, TicketCashoutResponse> ticketCashoutResponseCache;

    /**
     * Initializes a new instance of the {@link TicketCashoutHandlerImpl}
     *
     * @param amqpPublisher - tha {@link AmqpPublisher} that will be used to publish messages
     * @param routingKey - the routing key used to publish messages
     * @param replyRoutingKey - the routing key used to receive {@link TicketCashoutResponse} messages
     * @param executorService - the {@link ExecutorService} used for async publishing
     * @param messagesPerSecond - the max number of messages/second that should be sent
     * @param sdkLogger - the main SDK logging interface
     */
    public TicketCashoutHandlerImpl(AmqpPublisher amqpPublisher, String routingKey, String replyRoutingKey,
                                    ExecutorService executorService, ResponseTimeoutHandler<TicketCashout> timeoutHandler,
                                    int responseTimeout, double messagesPerSecond, SdkLogger sdkLogger) {
        super(amqpPublisher, executorService, messagesPerSecond, sdkLogger);

        this.routingKey = routingKey == null ? "ticket.cashout" : routingKey;
        this.replyRoutingKey = replyRoutingKey;
        this.executorService = executorService;
        this.responseTimeout = responseTimeout;
        this.timeoutHandler = timeoutHandler;

        ticketCashoutSendEntryCache = CacheBuilder.newBuilder()
                .expireAfterWrite(responseTimeout, TimeUnit.MILLISECONDS)
                .build();
        ticketCashoutResponseCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Publishes a new {@link TicketCashout} message
     *
     * @param ticketCashoutData - the data from which the message will be built
     */
    @Override
    public void send(TicketCashout ticketCashoutData) {
        checkState(isOpen(), "sender is closed");
        checkNotNull(ticketCashoutData, "ticketCashoutData can not be null");
        checkNotNull(ticketCashoutResponseListener, "no response listener set");

        internalSend(ticketCashoutData);

        timeoutHandler.onAsyncTicketSent(ticketCashoutData);
    }

    /**
     * Publishes a new {@link TicketCashout} message and waits for a response with the specified timeout
     *
     * @param ticketCashout - the data from which the message will be built
     * @return - the cashout response from the MTS
     * @throws ResponseTimeoutException - if the max timeout for the response has exceeded
     */
    @Override
    public TicketCashoutResponse sendBlocking(TicketCashout ticketCashout) throws ResponseTimeoutException {
        Preconditions.checkState(isOpen(), "sender is closed");
        Preconditions.checkNotNull(ticketCashout, "ticketCashout cannot be null");

        String ticketId = ticketCashout.getTicketId();
        Semaphore semaphore = new Semaphore(0);
        ticketCashoutSendEntryCache.put(ticketId, new TicketCashoutSendEntry(ticketCashout, semaphore));
        publishAsync(ticketCashout, routingKey, replyRoutingKey);

        boolean acquire = false;
        try {
            acquire = semaphore.tryAcquire(responseTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("interrupted waiting for response, throwing timeout");
        }
        if (!acquire) {
            String error = String.format("Timeout reached. Missing response for ticketCashout %s with correlationId=", ticketCashout.getTicketId(), ticketCashout.getCorrelationId());
            throw new ResponseTimeoutException(error);
        }
        return ticketCashoutResponseCache.getIfPresent(ticketId);
    }

    private void internalSend(TicketCashout ticketCashout) {
        checkState(isOpen(), "sender is closed");
        checkNotNull(ticketCashout, "ticket cannot be null");

        publishAsync(ticketCashout, routingKey, replyRoutingKey);
    }

    /**
     * The listener that will be called when a message is received
     *
     * @param responseListener - the listener that will be called when a message is received
     */
    @Override
    public void setListener(TicketCashoutResponseListener responseListener) {
        checkNotNull(responseListener, "response listener cannot be null");
        setPublishListener(responseListener);
        timeoutHandler.setResponseTimeoutListener(responseListener);
        this.ticketCashoutResponseListener = responseListener;
    }

    /**
     * Returns a {@link String} object that contains a serialized {@link TicketCashout} instance
     *
     * @param messageObj - a {@link TicketCashout} instance that will be serialized
     * @return - a {@link String} object that contains a serialized {@link TicketCashout} instance
     */
    @Override
    protected String getSerializedDto(TicketCashout messageObj) {
        return messageObj.getJsonValue();
    }

    /**
     * Method invoked when the SDK receives the {@link TicketCashoutResponse} message
     *
     * @param ticketCashoutResponse - the {@link TicketCashoutResponse} message data
     */
    @Override
    public void ticketCashoutResponseReceived(TicketCashoutResponse ticketCashoutResponse) {
        checkNotNull(ticketCashoutResponse, "ticketCashoutResponse cannot be null");
        getSdkLogger().logReceivedMessage(JsonUtils.serializeAsString(ticketCashoutResponse));

        timeoutHandler.onAsyncTicketResponseReceived(ticketCashoutResponse.getCorrelationId());

        String ticketId = ticketCashoutResponse.getTicketId();
        ticketCashoutResponseCache.put(ticketId, ticketCashoutResponse);
        TicketCashoutSendEntry entry = ticketCashoutSendEntryCache.getIfPresent(ticketId);

        if (entry != null) {
            Semaphore semaphore = entry.getSemaphore();
            if (semaphore != null) {
                semaphore.release(1);
            }
            ticketCashoutSendEntryCache.invalidate(ticketId);
        } else {
            executorService.submit(() -> ticketCashoutResponseListener.responseReceived(ticketCashoutResponse));
        }
    }

    @Override
    protected void onPublishFailure(String correlationId) {
        Preconditions.checkNotNull(correlationId);

        timeoutHandler.onAsyncPublishFailure(correlationId);
    }

    private class TicketCashoutSendEntry {
        private final TicketCashout ticket;
        private final Semaphore semaphore;

        TicketCashoutSendEntry(TicketCashout ticket,
                                      Semaphore semaphore) {
            this.ticket = ticket;
            this.semaphore = semaphore;
        }

        public TicketCashout getTicket() {
            return ticket;
        }

        Semaphore getSemaphore() {
            return semaphore;
        }
    }
}
