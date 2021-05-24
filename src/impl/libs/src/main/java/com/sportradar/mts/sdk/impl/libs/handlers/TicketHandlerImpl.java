/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sportradar.mts.sdk.api.SdkTicket;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.TicketResponse;
import com.sportradar.mts.sdk.api.exceptions.ResponseTimeoutException;
import com.sportradar.mts.sdk.api.interfaces.TicketResponseListener;
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

public class TicketHandlerImpl extends SenderBase<Ticket> implements TicketHandler {

    private static final Logger logger = LoggerFactory.getLogger(TicketHandlerImpl.class);
    private final String routingKey;
    private final Cache<String, TicketSendEntry> ticketSendEntries1;
    private final Cache<String, TicketResponse> responseCache1;
    private final Cache<String, TicketSendEntry> ticketSendEntries2;
    private final Cache<String, TicketResponse> responseCache2;
    private final ExecutorService executorService;
    private final int responseTimeout1;
    private final int responseTimeout2;
    private final ResponseTimeoutHandler<Ticket> responseTimeoutHandler;
    private TicketResponseListener ticketResponseListener;

    @SuppressWarnings("java:S107") // Methods should not have too many parameters
    public TicketHandlerImpl(AmqpPublisher amqpPublisher,
                             String routingKey,
                             ExecutorService executorService,
                             ResponseTimeoutHandler<Ticket> responseTimeoutHandler,
                             int responseTimeout1,
                             int responseTimeout2,
                             double messagesPerSecond,
                             SdkLogger sdkLogger) {
        super(amqpPublisher, executorService, messagesPerSecond, sdkLogger);

        checkNotNull(executorService, "executorService cannot be null");
        checkNotNull(responseTimeoutHandler, "responseTimeoutHandler cannot be null");

        this.executorService = executorService;
        this.routingKey = routingKey == null ? "ticket" : routingKey;
        this.responseTimeout1 = responseTimeout1;
        this.responseTimeout2 = responseTimeout2;
        this.responseTimeoutHandler = responseTimeoutHandler;

        ticketSendEntries1 = CacheBuilder.newBuilder()
                .expireAfterWrite(responseTimeout1, TimeUnit.MILLISECONDS)
                .build();
        responseCache1 = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
        ticketSendEntries2 = CacheBuilder.newBuilder()
                .expireAfterWrite(responseTimeout2, TimeUnit.MILLISECONDS)
                .build();
        responseCache2 = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void send(Ticket ticket) {
        checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        checkNotNull(ticket, SdkInfo.Literals.TICKET_HANDLER_TICKET_NULL);

        checkNotNull(ticketResponseListener, "no response listener set");

        internalSend(ticket, null);

        responseTimeoutHandler.onAsyncTicketSent(ticket);
    }

    @Override
    public TicketResponse sendBlocking(Ticket ticket) throws ResponseTimeoutException {
        checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        checkNotNull(ticket, SdkInfo.Literals.TICKET_HANDLER_TICKET_NULL);

        Stopwatch stopwatch = Stopwatch.createStarted();
        String ticketId = ticket.getTicketId();
        Semaphore semaphore = new Semaphore(0);
        if(isTicketPrematch(ticket)){
            ticketSendEntries2.put(ticketId, new TicketSendEntry(ticket, null, semaphore));
        }
        else{
            ticketSendEntries1.put(ticketId, new TicketSendEntry(ticket, null, semaphore));
        }
        publishAsync(ticket, routingKey);

        boolean acquire = false;
        try {
            acquire = semaphore.tryAcquire(isTicketPrematch(ticket) ? responseTimeout2 : responseTimeout1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("interrupted waiting for response, throwing timeout");
            Thread.currentThread().interrupt();
        }
        if (!acquire) {
            String error = String.format("Timeout reached. Missing response for ticket %s with correlationId=%s", ticket.getTicketId(), ticket.getCorrelationId());
            throw new ResponseTimeoutException(error);
        }
        TicketResponse ticketResponse = isTicketPrematch(ticket) ? responseCache2.getIfPresent(ticketId) : responseCache1.getIfPresent(ticketId);
        stopwatch.stop();
        logger.debug("Response for ticket:{} is received in {} ms.", ticketId, stopwatch.elapsed(TimeUnit.MILLISECONDS));

        return ticketResponse;
    }

    @Override
    public void setListener(TicketResponseListener responseListener) {
        checkNotNull(responseListener, "responseListener cannot be null");
        setPublishListener(responseListener);
        responseTimeoutHandler.setResponseTimeoutListener(responseListener);
        this.ticketResponseListener = responseListener;
    }

    @Override
    public void ticketResponseReceived(TicketResponse ticketResponse) {
        checkNotNull(ticketResponse, "ticketResponse cannot be null");
        getSdkLogger().logReceivedMessage(JsonUtils.serializeAsString(ticketResponse));

        responseTimeoutHandler.onAsyncTicketResponseReceived(ticketResponse.getCorrelationId());

        String ticketId = ticketResponse.getTicketId();
        TicketSendEntry entry = ticketSendEntries1.getIfPresent(ticketId);
        responseCache1.put(ticketId, ticketResponse);
        if(entry == null)
        {
            entry = ticketSendEntries2.getIfPresent(ticketId);
            responseCache2.put(ticketId, ticketResponse);
        }
        final TicketResponseListener listenerToRespond;
        if (entry != null) {
            TicketResponseListener tmpTicketResponseListener = entry.getResponseListener();
            Semaphore semaphore = entry.getSemaphore();
            if (semaphore != null) {
                semaphore.release(1);
            }
            ticketSendEntries1.invalidate(ticketId);
            ticketSendEntries2.invalidate(ticketId);
            listenerToRespond = tmpTicketResponseListener;
        } else {
            listenerToRespond = this.ticketResponseListener;
        }
        if (listenerToRespond != null) {
            executorService.submit(() -> {
                try {
                    listenerToRespond.responseReceived(ticketResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void close() {
        super.close();
        ticketSendEntries1.cleanUp();
        if (ticketSendEntries1.size() > 0) {
            logger.info("there are still ticket responses pending, will wait till completion or timeout");
            while (ticketSendEntries1.size() > 0) {
                try {
                    ticketSendEntries1.cleanUp();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error("interrupted waiting to get/timeout all ticket responses");
                    Thread.currentThread().interrupt();
                }
            }
        }
        ticketSendEntries2.cleanUp();
        if (ticketSendEntries2.size() > 0) {
            logger.info("there are still ticket responses pending, will wait till completion or timeout");
            while (ticketSendEntries2.size() > 0) {
                try {
                    ticketSendEntries2.cleanUp();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error("interrupted waiting to get/timeout all ticket responses");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void internalSend(Ticket ticket, TicketResponseListener responseListener) {
        checkState(isOpen(), SdkInfo.Literals.TICKET_HANDLER_SENDER_CLOSED);
        checkNotNull(ticket, SdkInfo.Literals.TICKET_HANDLER_TICKET_NULL);

        String ticketId = ticket.getTicketId();
        if (responseListener != null) {
            if(isTicketPrematch(ticket)){
                ticketSendEntries2.put(ticketId, new TicketSendEntry(ticket, responseListener, null));
            }
            else{
                ticketSendEntries1.put(ticketId, new TicketSendEntry(ticket, responseListener, null));
            }
        }
        publishAsync(ticket, ticket.getCorrelationId(), routingKey);
    }

    @Override
    protected String getSerializedDto(Ticket message) {
        return message.getJsonValue();
    }

    @Override
    protected void onPublishFailure(String correlationId) {
        Preconditions.checkNotNull(correlationId);

        responseTimeoutHandler.onAsyncPublishFailure(correlationId);
    }

    private final class TicketSendEntry {

        private final Ticket ticket;
        private final TicketResponseListener responseListener;
        private final Semaphore semaphore;

        public TicketSendEntry(Ticket ticket,
                               TicketResponseListener responseListener,
                               Semaphore semaphore) {
            this.ticket = ticket;
            this.responseListener = responseListener;
            this.semaphore = semaphore;
        }

        public Ticket getTicket() {
            return ticket;
        }

        public Semaphore getSemaphore() {
            return semaphore;
        }

        public TicketResponseListener getResponseListener() {
            return responseListener;
        }
    }

    public static boolean isTicketPrematch(SdkTicket ticket) {
        return ticket instanceof Ticket && ((Ticket) ticket).getSelections().stream().anyMatch(a -> a.getId().contains("lcoo"));
    }
}
