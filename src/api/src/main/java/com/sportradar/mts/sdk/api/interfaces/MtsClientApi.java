/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.Ccf;
import com.sportradar.mts.sdk.api.Ticket;

/**
 * Entry point for the MTS Client API
 */
public interface MtsClientApi {

    /**
     * Gets maximum stake for a ticket
     *
     * @param ticket A {@link Ticket} to be send
     * @return Maximum reoffer stake (quantity multiplied by 10000 and rounded to a long value)
     * @throws Exception throws runtime exception
     */
    long getMaxStake(Ticket ticket) throws Exception;

    /**
     * Gets maximum stake for a ticket
     *
     * @param ticket A {@link Ticket} to be send
     * @param username A username used for authentication
     * @param password A password used for authentication
     * @return Maximum reoffer stake (quantity multiplied by 10000 and rounded to a long value)
     * @throws Exception throws runtime exception
     */
    long getMaxStake(Ticket ticket, String username, String password) throws Exception;

    /**
     * Gets customer confidence factor for a customer
     *
     * @param sourceId A source ID which identifies a customer
     * @return A {@link Ccf} values for sport and prematch/live (if set for customer)
     * @throws Exception throws runtime exception
     */
    Ccf getCcf(String sourceId) throws Exception;

    /**
     * Gets customer confidence factor for a customer
     *
     * @param sourceId A source ID which identifies a customer
     * @param username A username used for authentication
     * @param password A password used for authentication
     * @return A {@link Ccf} values for sport and prematch/live (if set for customer)
     * @throws Exception throws runtime exception
     */
    Ccf getCcf(String sourceId, String username, String password) throws Exception;
}
