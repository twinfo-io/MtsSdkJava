/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sportradar.mts.sdk.api.TicketNonSrSettle;
import com.sportradar.mts.sdk.api.TicketNonSrSettleResponse;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;
import com.sportradar.mts.sdk.api.interfaces.TicketNonSrSettleResponseListener;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.SdkInfo;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpPublisher;
import com.sportradar.mts.sdk.impl.libs.logging.SdkLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class TicketNonSrSettleHandlerImpl extends SenderBase<TicketNonSrSettle> implements TicketNonSrSettleHandler {
    /**
     * The main class logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TicketNonSrSettleHandlerImpl.class);

    /**
     * The routing key used for {@link TicketNonSrSettle} messages
     */
    private final String routingKey;

    /**
     * The routing key used to receive {@link TicketNonSrSettleResponse} messages
     */
    private final String replyRoutingKey;

    /**
     * The listener that will be used to dispatch received messages
     */
    private TicketNonSrSettleResponseListener ticketNonSrSettleResponseListener;

    /**
     * The executor for dispatching received messages
     */
    private final ExecutorService executorService;

    /**
     * The async tickets response time-out handler
     */
    private final ResponseTimeoutHandler<TicketNonSrSettle> timeoutHandler;

    /**
     * The max allowed cashout response time used for <code>sendBlocking</code>
     */
    private final int responseTimeout;

    /**
     * The caching used to store send messages that are used to check if the message is <code>sendBlocking</code>
     */
    private final Cache<String, TicketNonSrSettleSendEntry> ticketNonSrSettleSendEntryCache;

    /**
     * The caching used to store response messages so they can be utilized as the return for <code>sendBlocking</code>
     */
    private final Cache<String, TicketNonSrSettleResponse> ticketNonSrSettleResponseCache;

    @SuppressWarnings("java:S107") // Methods should not have too many parameters
    public TicketNonSrSettleHandlerImpl(AmqpPublisher amqpPublisher,
                                        String routingKey,
                                        String replyRoutingKey,
                                        ExecutorService executorService,
                                        ResponseTimeoutHandler<TicketNonSrSettle> timeoutHandler,
                                        int responseTimeout,
                                        double messagesPerSecond,
                                        SdkLogger sdkLogger) {
        super(amqpPublisher, executorService, messagesPerSecond, sdkLogger);
        this.routingKey = routingKey == null ? "ticket.nonsrsettle" : routingKey;
        this.replyRoutingKey = replyRoutingKey;
        this.executorService = executorService;
        this.timeoutHandler = timeoutHandler;
        this.responseTimeout = responseTimeout;

        ticketNonSrSettleSendEntryCache = CacheBuilder.newBuilder()
                .expireAfterWrite(responseTimeout, TimeUnit.MILLISECONDS)
                .build();
        ticketNonSrSettleResponseCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Publishes a new {@link TicketNonSrSettle} message
     *
     * @param ticketData - the data from which the message will be built
     */
    @Override
    public void send(TicketNonSrSettle ticketData) {
        checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        checkNotNull(ticketData, SdkInfo.Literals.TICKET_HANDLER_TICKET_NONSR_NULL);
        checkNotNull(ticketNonSrSettleResponseListener, "no response listener set");

        internalSend(ticketData);

        timeoutHandler.onAsyncTicketSent(ticketData);
    }

    /**
     * Publishes a new {@link TicketNonSrSettle} message and waits for a response with the specified timeout
     *
     * @param ticketNonSrSettle - the data from which the message will be built
     * @return - the cashout response from the MTS
     * @throws ResponseTimeoutException - if the max timeout for the response has exceeded
     */
    @Override
    public TicketNonSrSettleResponse sendBlocking(TicketNonSrSettle ticketNonSrSettle) throws ResponseTimeoutException {
        Preconditions.checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        Preconditions.checkNotNull(ticketNonSrSettle, SdkInfo.Literals.TICKET_HANDLER_TICKET_NONSR_NULL);

        String ticketId = ticketNonSrSettle.getTicketId();
        Semaphore semaphore = new Semaphore(0);
        ticketNonSrSettleSendEntryCache.put(ticketId, new TicketNonSrSettleSendEntry(ticketNonSrSettle, semaphore));
        publishAsync(ticketNonSrSettle, routingKey, replyRoutingKey);

        boolean acquire = false;
        try {
            acquire = semaphore.tryAcquire(responseTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("interrupted waiting for response, throwing timeout");
            Thread.currentThread().interrupt();
        }
        if (!acquire) {
            throw new ResponseTimeoutException("timeout reached");
        }
        return ticketNonSrSettleResponseCache.getIfPresent(ticketId);
    }

    /**
     * Returns a {@link String} object that contains a serialized {@link TicketNonSrSettle} instance
     *
     * @param message - a {@link TicketNonSrSettle} instance that will be serialized
     * @return - a {@link String} object that contains a serialized {@link TicketNonSrSettle} instance
     */
    @Override
    protected String getSerializedDto(TicketNonSrSettle message) {
        return message.getJsonValue();
    }

    /**
     * The listener that will be called when a message is received
     *
     * @param responseListener - the listener that will be called when a message is received
     */
    @Override
    public void setListener(TicketNonSrSettleResponseListener responseListener) {
        checkNotNull(responseListener, "response listener cannot be null");
        setPublishListener(responseListener);
        timeoutHandler.setResponseTimeoutListener(responseListener);
        this.ticketNonSrSettleResponseListener = responseListener;
    }

    @Override
    public void setTicketNonSrSettleResponse(TicketNonSrSettleResponse ticketNonSrSettleResponse) {
        checkNotNull(ticketNonSrSettleResponse, "TicketNonSrSettleResponse cannot be null");
        getSdkLogger().logReceivedMessage(JsonUtils.serializeAsString(ticketNonSrSettleResponse));

        timeoutHandler.onAsyncTicketResponseReceived(ticketNonSrSettleResponse.getCorrelationId());

        String ticketId = ticketNonSrSettleResponse.getTicketId();
        ticketNonSrSettleResponseCache.put(ticketId, ticketNonSrSettleResponse);
        TicketNonSrSettleSendEntry entry = ticketNonSrSettleSendEntryCache.getIfPresent(ticketId);

        if (entry != null) {
            Semaphore semaphore = entry.getSemaphore();
            if (semaphore != null) {
                semaphore.release(1);
            }
            ticketNonSrSettleSendEntryCache.invalidate(ticketId);
        } else {
            executorService.submit(() -> ticketNonSrSettleResponseListener.responseReceived(ticketNonSrSettleResponse));
        }
    }

    @Override
    protected void onPublishFailure(String correlationId) {
        Preconditions.checkNotNull(correlationId);

        timeoutHandler.onAsyncPublishFailure(correlationId);
    }


    private void internalSend(TicketNonSrSettle ticketNonSrSettle) {
        checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        checkNotNull(ticketNonSrSettle, SdkInfo.Literals.TICKET_HANDLER_TICKET_NONSR_NULL);

        publishAsync(ticketNonSrSettle, routingKey, replyRoutingKey);
    }

    private static class TicketNonSrSettleSendEntry {
        private final TicketNonSrSettle ticket;
        private final Semaphore semaphore;

        TicketNonSrSettleSendEntry(TicketNonSrSettle ticket, Semaphore semaphore) {
            this.ticket = ticket;
            this.semaphore = semaphore;
        }

        public TicketNonSrSettle getTicket() {
            return ticket;
        }

        Semaphore getSemaphore() {
            return semaphore;
        }
    }
}
