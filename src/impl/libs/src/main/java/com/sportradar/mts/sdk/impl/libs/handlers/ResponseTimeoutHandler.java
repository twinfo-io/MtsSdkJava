/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.SdkTicket;
import com.sportradar.mts.sdk.api.interfaces.TicketResponseTimeoutListener;

/**
 * Defines methods used to handle async tickets response time-outs
 */
public interface ResponseTimeoutHandler<T extends SdkTicket> {
    void setResponseTimeoutListener(TicketResponseTimeoutListener<T> responseTimeoutListener);

    void onAsyncTicketSent(T ticket);

    void onAsyncTicketResponseReceived(String correlationId);

    void onAsyncPublishFailure(String correlationId);
}
