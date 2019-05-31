/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.mts.sdk.api.BetCashout;
import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.builders.TicketCashoutBuilder;
import com.sportradar.mts.sdk.api.impl.BetCashoutImpl;
import com.sportradar.mts.sdk.api.impl.TicketCashoutImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;
import com.sportradar.mts.sdk.api.utils.SdkInfo;

import java.util.Date;
import java.util.List;

/**
 * Implementation of the {@link TicketCashoutBuilder} interface, which is used to
 * create new instances of {@link TicketCashoutImpl}
 */
public class TicketCashoutBuilderImpl implements TicketCashoutBuilder {
    /**
     * The assigned ticket id
     */
    private String ticketId;

    /**
     * The parent bookmaker id
     */
    private int bookmakerId;

    /**
     * The cashout stake of the assigned ticket
     */
    private Long cashoutStake;

    /**
     * cashout percent
     */
    private Integer cashoutPercent;

    /**
     * array of betId, cashoutStake pairs
     */
    private List<BetCashout> betCashouts;

    public TicketCashoutBuilderImpl(SdkConfiguration config)
    {
        Preconditions.checkNotNull(config);
        this.bookmakerId = config.getBookmakerId();
        cashoutPercent = null;
        betCashouts = null;
    }

    /**
     * Sets the ticket id
     *
     * @param ticketId - the ticket id
     * @return - the current instance reference
     */
    @Override
    public TicketCashoutBuilder setTicketId(String ticketId) {
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
    public TicketCashoutBuilder setBookmakerId(int bookmakerId) {
        if (bookmakerId < 1)
        {
            throw new IllegalArgumentException("BookmakerId not valid");
        }
        this.bookmakerId = bookmakerId;
        return this;
    }

    /**
     * Sets the cashout stake (quantity multiplied by 10_000 and rounded to a long value)
     *
     * @param cashoutStake - the cashout stake value of the assigned ticket
     * @return - the current instance reference
     */
    @Override
    public TicketCashoutBuilder setCashoutStake(long cashoutStake) {
        if (cashoutStake < 1)
        {
            throw new IllegalArgumentException("Stake not valid");
        }
        this.cashoutStake = cashoutStake;
        return this;
    }

    /**
     * Sets the cashout percent (quantity multiplied by 10_000 and rounded to a long value)
     *
     * @param cashoutPercent - the cashout percent value of the assigned ticket
     * @return - the current instance reference
     */
    @Override
    public TicketCashoutBuilder setCashoutPercent(int cashoutPercent) {
        if (!MtsTicketHelper.validatePercent(cashoutPercent))
        {
            throw new IllegalArgumentException("Percent not valid");
        }
        this.cashoutPercent = cashoutPercent;
        return this;
    }

    /**
     * Add the bet cashout
     *
     * @param betId          - the bet id
     * @param cashoutStake   - the cashout stake value of the assigned bet
     * @param cashoutPercent - the cashout percent value of the assigned bet (quantity multiplied by 10_000 and rounded to a long value)
     * @return - the current instance reference
     */
    @Override
    public TicketCashoutBuilder addBetCashout(String betId, long cashoutStake, Integer cashoutPercent) {
        if (!MtsTicketHelper.validateTicketId(betId))
        {
            throw new IllegalArgumentException("BetId not valid");
        }
        if (cashoutStake < 1)
        {
            throw new IllegalArgumentException("Stake not valid");
        }
        if (!MtsTicketHelper.validatePercent(cashoutPercent))
        {
            throw new IllegalArgumentException("Percent not valid");
        }
        if(betCashouts == null) {
            betCashouts = Lists.newArrayList();
        }
        if(betCashouts.size() == 50) {
            throw new IllegalArgumentException("List size limit reached.");
        }
        if(betCashouts.stream().anyMatch(m->m.getBetId().equalsIgnoreCase(betId))) {
            throw new IllegalArgumentException("BetId already in collection");
        }
        betCashouts.add(new BetCashoutImpl(betId, cashoutStake, cashoutPercent));
        return this;
    }

    /**
     * Creates a new {@link TicketCashout} instance using the preset builder parameters
     *
     * @return - a new {@link TicketCashout} instance
     */
    @Override
    public TicketCashout build() {
        return new TicketCashoutImpl(ticketId, bookmakerId, new Date(), cashoutStake, cashoutPercent, betCashouts, SdkInfo.mtsTicketVersion());
    }
}
