/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.SdkTicket;

/**
 * Ticket response time-out listener
 */
public interface TicketResponseTimeoutListener<T extends SdkTicket> {
    /**
     * Invoked when the response timed-out, if the {@link SdkConfiguration#isTicketTimeOutCallbackEnabled()} is set to true
     *
     * @param ticket the ticket for which the response timed-out
     */
    default void onTicketResponseTimedOut(T ticket) {
        // default implementation - should be implemented as needed if the time out callback is enabled
        throw new UnsupportedOperationException("Default method not implemented ~ " + ticket.getClass().getSimpleName());
    }
}
