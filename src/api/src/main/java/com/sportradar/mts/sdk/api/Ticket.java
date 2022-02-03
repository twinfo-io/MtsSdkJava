/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.OddsChangeType;

import java.util.Date;
import java.util.List;

/**
 * Ticket that can be send to the MTS
 */
public interface Ticket extends SdkTicket {

    /**
     * Gets the array of bets
     * Mandatory
     * @return array of bets
     */
    List<Bet> getBets();

    /**
     * Gets the identification and settings of the ticket sender
     * Mandatory
     * @return sender
     */
    Sender getSender();

    /**
     * Gets reoffer reference bet id
     * @return reofferRefId
     */
    String getReofferId();

    /**
     * Gets alternative stake reference ticket id
     * @return altStakeRefId
     */
    String getAltStakeRefId();

    /**
     * Gets value indicating if this is test source
     * (default false)
     * @return test source
     */
    boolean getTestSource();

    /**
     * Gets Accept change in odds (optional, default none) none: default behaviour, any: any odds change accepted, higher: accept higher odds
     * @return oddsChange
     */
    OddsChangeType getOddsChange();

    /**
     * Gets the array of all selections. Order is very important as they can be referenced by index in 'ticket.bets.selectionRefs'
     * Mandatory
     * @return selections
     */
    List<Selection> getSelections();

    /**
     * Get the total combinations
     * @return the total combinations
     */
    Integer getTotalCombinations();

    /**
     * Get the end time of last (non Sportradar) match on ticket.
     * @return end time of last (non Sportradar) match on ticket
     */
    Date getLastMatchEndTime();

    /**
     * Get the maximum payment win for ticket (capped).
     * @return maximum payment win for ticket (capped).
     */
    Long getPayCap();
}
