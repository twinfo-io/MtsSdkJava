/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.SenderChannel;

import java.io.Serializable;

/**
 * Ticket Sender
 */
public interface Sender extends Serializable {

    /**
     * Gets bookmaker id
     *
     * @return bookmaker id
     */
    int getBookmakerId();

    /**
     * Gets a 3-letter ISO sign for currency
     * Mandatory
     *
     * @return 3-letter ISO sign for currency
     */
    String getCurrency();

    /**
     * Gets limit id
     * mandatory
     *
     * @return limit id
     */
    int getLimitId();

    /**
     * Gets terminal id
     * Mandatory for {@link SenderChannel#TERMINAL}
     *
     * @return terminal id
     */
    String getTerminalId();

    /**
     * Gets the senders communication channel
     * Mandatory
     * @return channel
     */
    SenderChannel getChannel();

    /**
     * Gets shop id
     * Mandatory for {@link SenderChannel#RETAIL}
     *
     * @return shop id
     */
    String getShopId();

    /**
     * Gets endCustomer
     *
     * @return  endCustomer
     */
    EndCustomer getEndCustomer();
}
