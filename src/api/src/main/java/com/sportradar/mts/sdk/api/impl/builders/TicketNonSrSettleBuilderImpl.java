/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.TicketNonSrSettle;
import com.sportradar.mts.sdk.api.builders.TicketNonSrSettleBuilder;
import com.sportradar.mts.sdk.api.impl.TicketNonSrSettleImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;
import com.sportradar.mts.sdk.api.utils.SdkInfo;

import java.util.Date;

public class TicketNonSrSettleBuilderImpl implements TicketNonSrSettleBuilder {

    /**
     * The assigned ticket id
     */
    private String ticketId;

    /**
     * The bookmaker id
     */
    private int bookmakerId;

    /**
     * The non-Sportradar ticket settle stake of the assigned ticket
     */
    private Long nonSrSettleStake;

    public TicketNonSrSettleBuilderImpl(SdkConfiguration config) {
        Preconditions.checkNotNull(config);
        this.bookmakerId = config.getBookmakerId();
    }

    /**
     * Sets the ticket id
     *
     * @param ticketId - the ticket id
     * @return - the current instance reference
     */
    @Override
    public TicketNonSrSettleBuilder setTicketId(String ticketId) {
        if (!MtsTicketHelper.validateTicketId(ticketId))
        {
            throw new IllegalArgumentException("TicketId not valid");
        }
        this.ticketId = ticketId;
        return this;
    }

    /**
     * Sets the bookmaker id
     *
     * @param bookmakerId - the bookmaker id
     * @return - the current instance reference
     */
    @Override
    public TicketNonSrSettleBuilder setBookmakerId(int bookmakerId) {
        if (bookmakerId < 1)
        {
            throw new IllegalArgumentException("BookmakerId not valid");
        }
        this.bookmakerId = bookmakerId;
        return this;
    }

    /**
     * Sets the non-Sportradar settle stake (quantity multiplied by 10_000 and rounded to a long value)
     *
     * @param nonSRSettleStake - the non-Sportradar settle stake value of the assigned ticket
     * @return - the current instance reference
     */
    @Override
    public TicketNonSrSettleBuilder setNonSRSettleStake(long nonSRSettleStake) {
        if (nonSRSettleStake < 0)
        {
            throw new IllegalArgumentException("Stake not valid");
        }
        this.nonSrSettleStake = nonSRSettleStake;
        return this;
    }

    /**
     * Creates a new {@link TicketNonSrSettle} instance using the preset builder parameters
     *
     * @return - a new {@link TicketNonSrSettle} instance
     */
    @Override
    public TicketNonSrSettle build() {
        return new TicketNonSrSettleImpl(ticketId, bookmakerId, new Date(), nonSrSettleStake, SdkInfo.MTS_TICKET_VERSION);
    }
}
