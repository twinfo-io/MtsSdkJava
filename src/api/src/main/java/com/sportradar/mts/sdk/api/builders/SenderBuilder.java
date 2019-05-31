/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.EndCustomer;
import com.sportradar.mts.sdk.api.Sender;
import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.impl.builders.SenderBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationImpl;
import com.sportradar.mts.sdk.api.utils.StringUtils;

/**
 * Builder used to create a new instance of {@link Sender}
 */
public interface SenderBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static SenderBuilder create() {
        SdkConfiguration config = SdkConfigurationImpl.getConfiguration();
        return new SenderBuilderImpl(config);
    }
    static SenderBuilder create(int bookmakerId) { return new SenderBuilderImpl(bookmakerId, 0, StringUtils.EMPTY, StringUtils.EMPTY); }
    static SenderBuilder create(int bookmakerId, int limitId) { return new SenderBuilderImpl(bookmakerId, limitId, StringUtils.EMPTY, StringUtils.EMPTY); }
    static SenderBuilder create(int bookmakerId, int limitId, String currency) { return new SenderBuilderImpl(bookmakerId, limitId, currency, StringUtils.EMPTY); }
    static SenderBuilder create(int bookmakerId, int limitId, String currency, SenderChannel channel) { return new SenderBuilderImpl(bookmakerId, limitId, currency, channel.toString()); }

    /**
     * Sets bookmaker id
     *
     * @param bookmakerId bookmaker id
     * @return current builder reference
     */
    SenderBuilder setBookmakerId(Integer bookmakerId);

    /**
     * Sets currency
     *
     * @param currency currency
     * @return current builder reference
     */
    SenderBuilder setCurrency(String currency);

    /**
     * Sets terminal id
     * Mandatory for {@link SenderChannel#TERMINAL}
     *
     * @param terminalId terminal id
     * @return current builder reference
     */
    SenderBuilder setTerminalId(String terminalId);

    /**
     * Sets sender channel
     * Mandatory
     *
     * @param channel channel
     * @return current builder reference
     */
    SenderBuilder setSenderChannel(SenderChannel channel);

    /**
     * Sets shop id
     * Mandatory for {@link SenderChannel#RETAIL}
     *
     * @param shopId shop id
     * @return current builder reference
     */
    SenderBuilder setShopId(String shopId);

    /**
     * Sets end customer
     * Mandatory for {@link SenderChannel#INTERNET}, {@link SenderChannel#MOBILE}, {@link SenderChannel#SMS}
     *
     * @param endCustomer end customer
     * @return current builder reference
     */
    SenderBuilder setEndCustomer(EndCustomer endCustomer);

    /**
     * Sets end customer
     * Mandatory for {@link SenderChannel#INTERNET}, {@link SenderChannel#MOBILE}
     *
     * @param customerIp end customer ip
     * @param customerId customer id
     * @param languageId language id
     * @param deviceId device id
     * @param confidence confidence
     * @return current builder reference
     */
    SenderBuilder setEndCustomer(String customerIp, String customerId, String languageId, String deviceId, Long confidence);

    /**
     * Sets limit id
     *
     * @param limitId limit id
     * @return current builder reference
     */
    SenderBuilder setLimitId(Integer limitId);

    /**
     * Creates new {@link Sender} instance
     * @return new {@link Sender} instance
     */
    Sender build();
}
