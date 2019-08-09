/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.sportradar.mts.sdk.api.SdkTicket;
import com.sportradar.mts.sdk.api.interfaces.TicketResponseTimeoutListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An utility class used to handle ticket response time-outs if they are enabled
 */
public class ResponseTimeoutHandlerImpl<T extends SdkTicket> implements RemovalListener<String, T>, ResponseTimeoutHandler<T> {
    private static final Logger logger = LoggerFactory.getLogger(ResponseTimeoutHandlerImpl.class);

    private final ScheduledExecutorService executorService;
    private final Cache<String, T> responseTimeoutMonitoringCache;
    private final boolean ticketTimeOutCallbackEnabled;

    private TicketResponseTimeoutListener<T> responseTimeoutListener;

    public ResponseTimeoutHandlerImpl(ScheduledExecutorService executorService,
                                      int responseTimeout,
                                      boolean ticketTimeOutCallbackEnabled) {
        Preconditions.checkNotNull(executorService);

        this.ticketTimeOutCallbackEnabled = ticketTimeOutCallbackEnabled;

        this.executorService = executorService;

        responseTimeoutMonitoringCache = CacheBuilder.newBuilder()
                .expireAfterWrite(responseTimeout, TimeUnit.MILLISECONDS)
                .removalListener(this)
                .build();

        if (this.ticketTimeOutCallbackEnabled) {
            executorService.scheduleAtFixedRate(responseTimeoutMonitoringCache::cleanUp, 500, 500, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void setResponseTimeoutListener(TicketResponseTimeoutListener<T> responseTimeoutListener) {
        Preconditions.checkNotNull(responseTimeoutListener);

        if (!ticketTimeOutCallbackEnabled) {
            return;
        }

        this.responseTimeoutListener = responseTimeoutListener;
    }

    @Override
    public void onAsyncTicketSent(T ticket) {
        Preconditions.checkNotNull(ticket);

        if (!ticketTimeOutCallbackEnabled) {
            return;
        }

        responseTimeoutMonitoringCache.put(ticket.getCorrelationId(), ticket);
    }

    @Override
    public void onAsyncTicketResponseReceived(String correlationId) {
        Preconditions.checkNotNull(correlationId);

        if (!ticketTimeOutCallbackEnabled) {
            return;
        }

        responseTimeoutMonitoringCache.invalidate(correlationId);
    }

    @Override
    public void onAsyncPublishFailure(String correlationId) {
        if (!ticketTimeOutCallbackEnabled) {
            return;
        }

        responseTimeoutMonitoringCache.invalidate(correlationId);
    }

    @Override
    public void onRemoval(RemovalNotification<String, T> notification) {
        if (notification.getCause() == RemovalCause.EXPIRED) {
            handleTicketResponseTimedOut(notification.getValue());
        }
    }

    private void handleTicketResponseTimedOut(T ticket) {
        if (!ticketTimeOutCallbackEnabled) {
            return;
        }

        logger.info("Ticket[{}] response timed-out - dispatching onTicketResponseTimedOut. ticketId: {}, correlationId: {}", ticket.getClass().getSimpleName(), ticket.getTicketId(), ticket.getCorrelationId());

        if (responseTimeoutListener != null) {
            executorService.submit(() -> {
                try {
                    responseTimeoutListener.onTicketResponseTimedOut(ticket);
                } catch (Exception e) {
                    logger.error("There was an error dispatching onTicketResponseTimedOut[{}] for ticketId: '{}', correlationId: '{}'", ticket.getClass().getSimpleName(), ticket.getTicketId(), ticket.getCorrelationId(), e);
                }
            });
        } else {
            logger.error("Response timeout listener[{}] is null, non-dispatched ticketId: '{}', correlationId: '{}'", ticket.getClass().getSimpleName(), ticket.getTicketId(), ticket.getCorrelationId());
        }
    }
}
