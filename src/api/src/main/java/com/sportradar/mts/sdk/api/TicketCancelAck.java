/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.TicketCancelAckStatus;

/**
 * Object that is send to MTS to acknowledge ticket cancellation
 */
public interface TicketCancelAck extends SdkTicket {

    /**
     * Gets bookmaker id
     *
     * @return bookmaker id
     */
    int getBookmakerId();

    /**
     * Get the code
     *
     * @return code
     */
    Integer getCode();

    /**
     * Get source message
     *
     * @return source message
     */
    String getMessage();

    /**
     * Gets the status of the ticket cancel
     *
     * @return acknowledgment status
     */
    TicketCancelAckStatus getAckStatus();
}
