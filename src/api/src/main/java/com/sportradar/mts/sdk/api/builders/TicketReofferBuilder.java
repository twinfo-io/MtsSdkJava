/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.TicketReofferCancel;
import com.sportradar.mts.sdk.api.TicketResponse;
import com.sportradar.mts.sdk.api.impl.builders.TicketReofferBuilderImpl;

/**
 * Builder used to create a new instance of reoffer {@link Ticket} based on the original ticket
 */
public interface TicketReofferBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static TicketReofferBuilder create() { return new TicketReofferBuilderImpl(); }

    /**
     * Sets ticket and ticket response
     *
     * @param ticket original ticket
     * @param ticketResponse response of the original ticket
     * @param newTicketId optional new ticket id
     * @return current builder reference
     */
    TicketReofferBuilder set(Ticket ticket, TicketResponse ticketResponse, String newTicketId);

    /**
     * Sets ticket and ticket response
     *
     * @param ticket original ticket
     * @param newStake the stake used to set the bet stake
     * @param newTicketId optional new ticket id
     * @return current builder reference
     */
    TicketReofferBuilder set(Ticket ticket, long newStake, String newTicketId);

    /**
     * Creates a new {@link TicketReofferCancel}  instance using builder parameters
     *
     * @return new {@link TicketReofferCancel} instance
     */
    Ticket build();
}
