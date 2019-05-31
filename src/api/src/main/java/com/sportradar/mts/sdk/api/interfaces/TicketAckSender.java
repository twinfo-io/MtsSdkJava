/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketAck;

/**
 * {@link TicketAck} sender
 */
public interface TicketAckSender extends MessageSender {

    /**
     * Sends the {@link TicketAck} to the MTS
     *
     * @param ticketAcknowledgment ticket acknowledgment to send
     */
    void send(TicketAck ticketAcknowledgment);
}
