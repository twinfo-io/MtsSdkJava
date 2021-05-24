/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.TicketCancelResponse;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelResponseListener;
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

public class TicketCancelHandlerImpl extends SenderBase<TicketCancel> implements TicketCancelHandler {

    private static final Logger logger = LoggerFactory.getLogger(TicketCancelHandlerImpl.class);
    private final String routingKey;
    private final ExecutorService executorService;
    private final ResponseTimeoutHandler<TicketCancel> timeoutHandler;
    private TicketCancelResponseListener ticketCancelResponseListener;
    private final Cache<String, TicketCancelSendEntry> ticketCancelSendEntries;
    private final Cache<String, TicketCancelResponse> cancelResponseCache;
    private final int responseTimeout;
    private final String replyRoutingKey;

    @SuppressWarnings("java:S107") // Methods should not have too many parameters
    public TicketCancelHandlerImpl(AmqpPublisher amqpPublisher,
                                   String routingKey,
                                   String replyRoutingKey,
                                   ExecutorService executorService,
                                   ResponseTimeoutHandler<TicketCancel> timeoutHandler,
                                   int responseTimeout,
                                   double messagesPerSecond,
                                   SdkLogger sdkLogger) {
        super(amqpPublisher, executorService, messagesPerSecond, sdkLogger);

        checkNotNull(executorService, "executorService cannot be null");
        checkNotNull(timeoutHandler, "timeoutHandler cannot be null");

        this.executorService = executorService;
        this.routingKey = routingKey == null ? "cancel" : routingKey;
        this.replyRoutingKey = replyRoutingKey;
        this.responseTimeout = responseTimeout;
        this.timeoutHandler = timeoutHandler;

        ticketCancelSendEntries = CacheBuilder.newBuilder()
                .expireAfterWrite(responseTimeout, TimeUnit.MILLISECONDS)
                .build();
        cancelResponseCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void send(TicketCancel ticketCancel) {
        checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        checkNotNull(ticketCancel, SdkInfo.Literals.TICKET_HANDLER_TICKET_CANCEL_NULL);
        checkNotNull(ticketCancelResponseListener, "no response listener set");

        internalSend(ticketCancel, null);

        timeoutHandler.onAsyncTicketSent(ticketCancel);
    }

    private void internalSend(TicketCancel ticketCancel, TicketCancelResponseListener responseListener) {
        checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        checkNotNull(ticketCancel, SdkInfo.Literals.TICKET_HANDLER_TICKET_CANCEL_NULL);
        String ticketId = ticketCancel.getTicketId();
        if (responseListener != null) {
            ticketCancelSendEntries.put(ticketId, new TicketCancelSendEntry(ticketCancel, responseListener, null));
        }
        publishAsync(ticketCancel, routingKey, replyRoutingKey);
    }

    @Override
    public TicketCancelResponse sendBlocking(TicketCancel ticketCancel) throws ResponseTimeoutException {
        checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        checkNotNull(ticketCancel, SdkInfo.Literals.TICKET_HANDLER_TICKET_CANCEL_NULL);
        String ticketId = ticketCancel.getTicketId();
        Semaphore semaphore = new Semaphore(0);
        ticketCancelSendEntries.put(ticketId, new TicketCancelSendEntry(ticketCancel, null, semaphore));
        publishAsync(ticketCancel, routingKey, replyRoutingKey);

        boolean acquire = false;
        try {
            acquire = semaphore.tryAcquire(responseTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("interrupted waiting for response, throwing timeout");
            Thread.currentThread().interrupt();
        }
        if (!acquire) {
            String error = String.format("Timeout reached. Missing response for ticketCancel %s with correlationId= %s", ticketCancel.getTicketId(), ticketCancel.getCorrelationId());
            throw new ResponseTimeoutException(error);
        }
        return cancelResponseCache.getIfPresent(ticketId);
    }

    @Override
    public void setListener(TicketCancelResponseListener responseListener) {
        checkNotNull(responseListener, "response listener cannot be null");
        setPublishListener(responseListener);
        timeoutHandler.setResponseTimeoutListener(responseListener);
        this.ticketCancelResponseListener = responseListener;
    }

    @Override
    public void ticketCancelResponseReceived(TicketCancelResponse ticketCancelResponse) {

        checkNotNull(ticketCancelResponse, "ticketCancelResponse cannot be null");
        getSdkLogger().logReceivedMessage(JsonUtils.serializeAsString(ticketCancelResponse));

        timeoutHandler.onAsyncTicketResponseReceived(ticketCancelResponse.getCorrelationId());

        String ticketId = ticketCancelResponse.getTicketId();
        cancelResponseCache.put(ticketId, ticketCancelResponse);
        TicketCancelSendEntry entry = ticketCancelSendEntries.getIfPresent(ticketId);
        final TicketCancelResponseListener listenerToRespond;
        if (entry != null) {
            TicketCancelResponseListener ticketResponseListener = entry.getResponseListener();
            Semaphore semaphore = entry.getSemaphore();
            if (semaphore != null) {
                semaphore.release(1);
            }
            ticketCancelSendEntries.invalidate(ticketId);
            listenerToRespond = ticketResponseListener;
        } else {
            listenerToRespond = this.ticketCancelResponseListener;
        }
        if (listenerToRespond != null) {
            executorService.submit(() -> {
                try {
                    listenerToRespond.responseReceived(ticketCancelResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void close() {
        super.close();
        ticketCancelSendEntries.cleanUp();
        if (ticketCancelSendEntries.size() > 0) {
            logger.info("there are still ticketCancel responses pending, will wait till completion or timeout");
            while (ticketCancelSendEntries.size() > 0) {
                try {
                    ticketCancelSendEntries.cleanUp();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error("interrupted waiting to get/timeout all ticket cancel responses");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    protected String getSerializedDto(TicketCancel message) {
        return message.getJsonValue();
    }

    @Override
    protected void onPublishFailure(String correlationId) {
        Preconditions.checkNotNull(correlationId);

        timeoutHandler.onAsyncPublishFailure(correlationId);
    }

    private final class TicketCancelSendEntry {

        private final TicketCancel ticket;
        private final TicketCancelResponseListener responseListener;
        private final Semaphore semaphore;

        public TicketCancelSendEntry(TicketCancel ticket,
                               TicketCancelResponseListener responseListener,
                               Semaphore semaphore) {
            this.ticket = ticket;
            this.responseListener = responseListener;
            this.semaphore = semaphore;
        }

        public TicketCancel getTicket() {
            return ticket;
        }

        public Semaphore getSemaphore() {
            return semaphore;
        }

        public TicketCancelResponseListener getResponseListener() {
            return responseListener;
        }
    }
}
