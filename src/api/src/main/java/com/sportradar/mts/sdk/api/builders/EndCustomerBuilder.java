/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.EndCustomer;
import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.impl.builders.EndCustomerBuilderImpl;

/**
 * Builder used to create {@link EndCustomer} instances
 */
public interface EndCustomerBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static EndCustomerBuilder create()
    {
        return new EndCustomerBuilderImpl();
    }

    /**
     * Sets end customer id
     * Mandatory for {@link SenderChannel#INTERNET}, {@link SenderChannel#MOBILE}, {@link SenderChannel#SMS}
     *
     * @param endCustomerId end customer id
     * @return current builder reference
     */
    EndCustomerBuilder setId(String endCustomerId);

    /**
     * Sets end customer ip
     * Mandatory for {@link SenderChannel#INTERNET}, {@link SenderChannel#MOBILE}
     *
     * @param endCustomerIp end customer ip
     * @return current builder reference
     */
    EndCustomerBuilder setIp(String endCustomerIp);

    /**
     * Sets language id
     * Mandatory for {@link SenderChannel#INTERNET}, {@link SenderChannel#MOBILE}, {@link SenderChannel#SMS}
     *
     * @param languageId language id
     * @return current builder reference
     */
    EndCustomerBuilder setLanguageId(String languageId);

    /**
     * Sets device id
     * Optional
     *
     * @param deviceId device id
     * @return current builder reference
     */
    EndCustomerBuilder setDeviceId(String deviceId);

    /**
     * Sets the suggested CCF of the customer multiplied by 10000 and rounded to a long value
     * @param confidence confidence to be set
     * @return current builder reference
     */
    EndCustomerBuilder setConfidence(Long confidence);

    /**
     * Creates new {@link EndCustomer} instance
     * @return new {@link EndCustomer} instance
     */
    EndCustomer build();
}
