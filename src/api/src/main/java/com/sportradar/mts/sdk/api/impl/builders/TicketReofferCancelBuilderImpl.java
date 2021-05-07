/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.TicketReofferCancel;
import com.sportradar.mts.sdk.api.builders.TicketReofferCancelBuilder;
import com.sportradar.mts.sdk.api.impl.TicketReofferCancelImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;
import com.sportradar.mts.sdk.api.utils.SdkInfo;

import java.util.Date;

/**
 * Implementation of the TicketReofferCancelBuilder
 */
public class TicketReofferCancelBuilderImpl implements TicketReofferCancelBuilder {
    
    private String ticketId;
    private int bookmakerId;

    public TicketReofferCancelBuilderImpl(SdkConfiguration config)
    {
        Preconditions.checkNotNull(config);
        this.bookmakerId = config.getBookmakerId();
    }

    @Override
    public TicketReofferCancelBuilder setTicketId(String ticketId) {
        if (!MtsTicketHelper.validateTicketId(ticketId))
        {
            throw new IllegalArgumentException("TicketId not valid");
        }
        this.ticketId = ticketId;
        return this;
    }

    @Override
    public TicketReofferCancelBuilder setBookmakerId(int bookmakerId) {
        if (bookmakerId < 1)
        {
            throw new IllegalArgumentException("BookmakerId not valid");
        }
        this.bookmakerId = bookmakerId;
        return this;
    }

    @Override
    public TicketReofferCancel build(String ticketId, int bookmakerId) {
        return new TicketReofferCancelImpl(ticketId, bookmakerId, new Date(), SdkInfo.MTS_TICKET_VERSION);
    }

    @Override
    public TicketReofferCancel build() {
        return new TicketReofferCancelImpl(ticketId, bookmakerId, new Date(), SdkInfo.MTS_TICKET_VERSION);
    }
}
