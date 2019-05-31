/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.TicketNonSrSettle;

/**
 * Builder used to create a new instance of {@link TicketNonSrSettle}
 */
public interface TicketNonSrSettleBuilder {

    /**
     * Sets the ticket id
     *
     * @param ticketId - the ticket id
     * @return - the current instance reference
     */
    TicketNonSrSettleBuilder setTicketId(String ticketId);

    /**
     * Sets the bookmaker id
     *
     * @param bookmakerId - the bookmaker id
     * @return - the current instance reference
     */
    TicketNonSrSettleBuilder setBookmakerId(int bookmakerId);

    /**
     * Sets the non-Sportradar settle stake (quantity multiplied by 10_000 and rounded to a long value)
     *
     * @param nonSRSettleStake - the non-Sportradar settle stake value of the assigned ticket
     * @return - the current instance reference
     */
    TicketNonSrSettleBuilder setNonSRSettleStake(long nonSRSettleStake);

    /**
     * Creates a new {@link TicketNonSrSettle} instance using the preset builder parameters
     *
     * @return - a new {@link TicketNonSrSettle} instance
     */
    TicketNonSrSettle build();
}
