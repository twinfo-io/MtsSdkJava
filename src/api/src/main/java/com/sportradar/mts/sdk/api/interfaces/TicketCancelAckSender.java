/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.TicketCancelAck;

/**
 * {@link TicketCancelAck} sender
 */
public interface TicketCancelAckSender extends MessageSender {

    /**
     * Sends the {@link TicketCancelAck} to the MTS
     *
     * @param ticketCancelAcknowledgment ticket cancel acknowledgment to send
     */
    void send(TicketCancelAck ticketCancelAcknowledgment);
}
