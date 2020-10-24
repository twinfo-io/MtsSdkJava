/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.mts.sdk.api.BetCancel;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.builders.TicketCancelBuilder;
import com.sportradar.mts.sdk.api.enums.TicketCancellationReason;
import com.sportradar.mts.sdk.api.impl.BetCancelImpl;
import com.sportradar.mts.sdk.api.impl.TicketCancelImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;
import com.sportradar.mts.sdk.api.utils.SdkInfo;

import java.util.Date;
import java.util.List;

/**
 * Implementation of the TicketCancelBuilder
 */
public class TicketCancelBuilderImpl implements TicketCancelBuilder {
    
    private String ticketId;
    private int bookmakerId;
    private TicketCancellationReason reason;
    private Integer cancelPercent;
    private List<BetCancel> betCancels;

    public TicketCancelBuilderImpl(SdkConfiguration config)
    {
        Preconditions.checkNotNull(config);
        this.bookmakerId = config.getBookmakerId();
        cancelPercent = null;
        betCancels = null;
    }
    
    @Override
    public TicketCancelBuilder setTicketId(String ticketId) {
        if (!MtsTicketHelper.validateTicketId(ticketId))
        {
            throw new IllegalArgumentException("TicketId not valid");
        }
        this.ticketId = ticketId;
        return this;
    }

    @Override
    public TicketCancelBuilder setBookmakerId(int bookmakerId) {
        if (bookmakerId < 1)
        {
            throw new IllegalArgumentException("BookmakerId not valid");
        }
        this.bookmakerId = bookmakerId;
        return this;
    }

    @Override
    public TicketCancelBuilder setCode(TicketCancellationReason code) {
        this.reason = code;
        return this;
    }

    /**
     * Sets the cancel percent (quantity multiplied by 10_000 and rounded to a long value)
     *
     * @param cancelPercent - the cancel percent value of the assigned ticket
     * @return - the current instance reference
     */
    @Override
    public TicketCancelBuilder setCancelPercent(int cancelPercent) {
        if (!MtsTicketHelper.validatePercent(cancelPercent))
        {
            throw new IllegalArgumentException("Percent not valid");
        }
        this.cancelPercent = cancelPercent;
        return this;
    }

    /**
     * Add the bet cashout
     *
     * @param betId         - the bet id
     * @param cancelPercent - the cancel percent value of the assigned bet (quantity multiplied by 10_000 and rounded to a int value)
     * @return - the current instance reference
     */
    @Override
    public TicketCancelBuilder addBetCancel(String betId, Integer cancelPercent) {
        if (!MtsTicketHelper.validateTicketId(betId))
        {
            throw new IllegalArgumentException("BetId not valid");
        }
        if (!MtsTicketHelper.validatePercent(cancelPercent))
        {
            throw new IllegalArgumentException("Percent not valid");
        }
        if(betCancels == null) {
            betCancels = Lists.newArrayList();
        }
        if(betCancels.size() >= 50) {
            throw new IllegalArgumentException("List size limit reached. Only 50 allowed.");
        }
        if(betCancels.stream().anyMatch(m->m.getBetId().equalsIgnoreCase(betId))) {
            throw new IllegalArgumentException("BetId already in collection");
        }
        betCancels.add(new BetCancelImpl(betId, cancelPercent));
        return this;
    }

    @Override
    public TicketCancel build(String ticketId, int bookmakerId, TicketCancellationReason reason) {
        return new TicketCancelImpl(ticketId, bookmakerId, reason, new Date(), null, null, SdkInfo.mtsTicketVersion());
    }

    @Override
    public TicketCancel build() {
        return new TicketCancelImpl(ticketId, bookmakerId, reason, new Date(), cancelPercent, betCancels, SdkInfo.mtsTicketVersion());
    }
}
