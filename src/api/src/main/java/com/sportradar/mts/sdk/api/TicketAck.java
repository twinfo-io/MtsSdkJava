/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.TicketAckStatus;

/**
 * Object that is send to MTS to acknowledge ticket
 */
public interface TicketAck extends SdkTicket {

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
     * Gets acknowledgment status
     *
     * @return acknowledgment status
     */
    TicketAckStatus getAckStatus();
}
