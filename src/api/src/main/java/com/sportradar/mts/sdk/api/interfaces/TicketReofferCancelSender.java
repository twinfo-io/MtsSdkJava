/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketReofferCancel;

/**
 * {@link TicketReofferCancel} sender
 */
public interface TicketReofferCancelSender extends MessageSender {

    /**
     * Sends the {@link TicketReofferCancel} to the MTS
     *
     * @param ticketReofferCancel reoffer ticket cancel to send
     */
    void send(TicketReofferCancel ticketReofferCancel);
}
