/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

/**
 * Reoffer cancel ticket
 */
public interface TicketReofferCancel extends SdkTicket {

    /**
     * Gets bookmaker id
     *
     * @return bookmaker id
     */
    int getBookmakerId();
}
