/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.Bet;
import com.sportradar.mts.sdk.api.Sender;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.enums.OddsChangeType;
import com.sportradar.mts.sdk.api.impl.builders.TicketBuilderImpl;

import java.util.Date;
import java.util.List;

/**
 * Builder used to create a new instance of {@link Ticket}
 */
public interface TicketBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static TicketBuilder create()
    {
        return new TicketBuilderImpl();
    }

    /**
     * Sets ticket id
     *
     * @param ticketId ticket id
     * @return current builder reference
     */
    TicketBuilder setTicketId(String ticketId);

    /**
     * Gets the bets
     * @return bets
     */
    List<Bet> getBets();

    /**
     * Add the bet to the ticket
     * @param bet bet to be added
     * @return current builder reference
     */
    TicketBuilder addBet(Bet bet);

    /**
     * Sets reoffer id
     *
     * @param reofferId to be set
     * @return current builder reference
     */
    TicketBuilder setReofferId(String reofferId);

    /**
     * Sets the alternative stake reference ticket id
     * @param altStakeRefId to be set
     * @return current builder reference
     */
    TicketBuilder setAltStakeRefId(String altStakeRefId);

    /**
     * Sets the test source
     * @param isTest if set to true is test
     * @return current builder reference
     */
    TicketBuilder setTestSource(boolean isTest);

    /**
     * Sets the sender
     * @param sender the ticket sender
     * @return current builder reference
     */
    TicketBuilder setSender(Sender sender);

    /**
     * Sets the odds change
     * @param oddsChangeType the type to be set
     * @return current builder reference
     */
    TicketBuilder setOddsChange(OddsChangeType oddsChangeType);

    /**
     * Sets the total combinations
     * @param totalCombinations the total combinations
     * @return current builder reference
     */
    TicketBuilder setTotalCombinations(Integer totalCombinations);

    /**
     * Set end time of last (non Sportradar) match on ticket
     * @param lastMatchEndTime end time of last (non Sportradar) match on ticket
     * @return current builder reference
     */
    TicketBuilder setLastMatchEndTime(Date lastMatchEndTime);

    /**
     * Set maximum payment win for ticket (capped).
     * @param payCap maximum payment win for ticket (capped)
     * @return current builder reference
     */
    TicketBuilder setPayCap(Long payCap);

    /**
     * Creates a new {@link Ticket}  instance using builder parameters
     *
     * @return new {@link Ticket} instance
     */
    Ticket build();
}
