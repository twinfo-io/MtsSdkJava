/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.impl.builders.TicketCashoutBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationImpl;


/**
 * Builder used to create a new instance of {@link TicketCashout}
 */
public interface TicketCashoutBuilder {
    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static TicketCashoutBuilder create() {
        SdkConfiguration config = SdkConfigurationImpl.getConfiguration();
        return new TicketCashoutBuilderImpl(config);
    }

    /**
     * Sets the ticket id
     *
     * @param ticketId - the ticket id
     * @return - the current instance reference
     */
    TicketCashoutBuilder setTicketId(String ticketId);

    /**
     * Sets the bookmaker id
     *
     * @param bookmakerId - the bookmaker id
     * @return - the current instance reference
     */
    TicketCashoutBuilder setBookmakerId(int bookmakerId);

    /**
     * Sets the cashout stake (quantity multiplied by 10_000 and rounded to a long value)
     *
     * @param cashoutStake - the cashout stake value of the assigned ticket
     * @return - the current instance reference
     */
    TicketCashoutBuilder setCashoutStake(long cashoutStake);

    /**
     * Sets the cashout percent (quantity multiplied by 10_000 and rounded to a int value)
     *
     * @param cashoutPercent - the cashout percent value of the assigned ticket
     * @return - the current instance reference
     */
    TicketCashoutBuilder setCashoutPercent(int cashoutPercent);

    /**
     * Add the bet cashout
     *
     * @param betId - the bet id
     * @param cashoutStake - the cashout stake value of the assigned bet (quantity multiplied by 10_000 and rounded to a long value)
     * @param cashoutPercent - the cashout percent value of the assigned bet (quantity multiplied by 10_000 and rounded to a int value)
     * @return - the current instance reference
     */
    TicketCashoutBuilder addBetCashout(String betId, long cashoutStake, Integer cashoutPercent);

    /**
     * Creates a new {@link TicketCashout} instance using the preset builder parameters
     *
     * @return - a new {@link TicketCashout} instance
     */
    TicketCashout build();
}
