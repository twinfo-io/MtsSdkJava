/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

/**
 * Ticket ext
 */
public interface Ext {

    /**
     * Gets ticket max win
     *
     * @return ticket max win
     */
    Double getTicketMaxWin();

    /**
     * Gets sequence group id
     * Sequence groups should be used to delimit different ticket sequences.
     * We suggest group ID to be constructed from Node ID and timestamp indicating when client application on that node was started.
     * Client should make sure tuple seqGroupId, seqId are unique for every ticket.
     *
     * @return sequence group id
     */
    Integer getSeqGroupId();

    /**
     * Gets sequence id
     * A monotonically increasing sequence ID inside the scope of a particular group. Should start with 1.
     *
     * @return sequence id
     */
    Long getSeqId();

    /**
     * Gets bonus win
     * Bonus amount which will be added to Win amount in case bet is won. The format, currency and digit should be the same as stake on bet object
     *
     * @return bonus win
     */
    Double getBonusWin();
}
