/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.SenderChannel;

import java.io.Serializable;

/**
 * Interface definition for the EndCustomer
 */
public interface EndCustomer extends Serializable {

    /**
     * Gets end customer ip
     * Mandatory for {@link SenderChannel#INTERNET}, {@link SenderChannel#MOBILE}
     *
     * @return end customer ip
     */
    String getEndCustomerIp();

    /**
     * Gets language id
     * Mandatory for {@link SenderChannel#INTERNET}, {@link SenderChannel#MOBILE}, {@link SenderChannel#SMS}
     *
     * @return language id
     */
    String getLanguageId();

    /**
     * Gets device id
     * Optional
     *
     * @return device id
     */
    String getDeviceId();

    /**
     * Gets end customer id
     * Mandatory for {@link SenderChannel#INTERNET}, {@link SenderChannel#MOBILE}, {@link SenderChannel#SMS}
     *
     * @return end customer id
     */
    String getId();

    /**
     * Gets suggested CCF of the customer multiplied by 10_000 and rounded to a long value
     *
     *
     * @return confidence
     */
    Long getConfidence();
}
