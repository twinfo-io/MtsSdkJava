/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.EndCustomer;
import com.sportradar.mts.sdk.api.Sender;
import com.sportradar.mts.sdk.api.builders.SenderBuilder;
import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.impl.EndCustomerImpl;
import com.sportradar.mts.sdk.api.impl.SenderImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.StringUtils;

/**
 * Implementation of the SenderBuilder
 */
public class SenderBuilderImpl implements SenderBuilder {

    private int bookmakerId;
    private String currency;
    private String terminalId;
    private SenderChannel senderChannel;
    private String shopId;
    private EndCustomer endCustomer;
    private int limitId;

    public SenderBuilderImpl(SdkConfiguration config)
    {
        Preconditions.checkNotNull(config);

        this.bookmakerId = config.getBookmakerId();
        this.limitId = config.getLimitId();
        this.currency = config.getCurrency();
        this.senderChannel = config.getSenderChannel();

    }

    public SenderBuilderImpl(int bookmakerId,
                             int limitId,
                             String currency,
                             SenderChannel channel)
    {
        if (bookmakerId > 0)
        {
            this.bookmakerId = bookmakerId;
        }
        if (limitId > 0)
        {
            this.limitId = limitId;
        }
        if (!StringUtils.isNullOrEmpty(currency))
        {
            this.currency = currency;
        }

        this.senderChannel = channel;
    }

    public SenderBuilderImpl(int bookmakerId,
                             int limitId,
                             String currency,
                             String channel)
    {
        if (bookmakerId > 0)
        {
            this.bookmakerId = bookmakerId;
        }
        if (limitId > 0)
        {
            this.limitId = limitId;
        }
        if (!StringUtils.isNullOrEmpty(currency))
        {
            this.currency = currency;
        }
        if(!StringUtils.isNullOrEmpty(channel))
        {
            this.senderChannel = SenderChannel.valueOf(channel);
        }
    }

    @Override
    public SenderBuilder setBookmakerId(Integer bookmakerId) {
        this.bookmakerId = bookmakerId;
        return this;
    }

    @Override
    public SenderBuilder setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public SenderBuilder setTerminalId(String terminalId) {
        this.terminalId = terminalId;
        return this;
    }

    @Override
    public SenderBuilder setSenderChannel(SenderChannel channel) {
        this.senderChannel = channel;
        return this;
    }

    @Override
    public SenderBuilder setShopId(String shopId) {
        this.shopId = shopId;
        return this;
    }

    @Override
    public SenderBuilder setEndCustomer(EndCustomer endCustomer) {
        this.endCustomer = endCustomer;
        return this;
    }

    @Override
    public SenderBuilder setEndCustomer(String customerIp, String customerId, String languageId, String deviceId, Long confidence) {
        this.endCustomer = new EndCustomerImpl(customerId, customerIp, languageId, deviceId, confidence);
        return this;
    }

    @Override
    public SenderBuilder setLimitId(Integer limitId) {
        this.limitId = limitId;
        return this;
    }

    @Override
    public Sender build() {
        return new SenderImpl(bookmakerId, currency, limitId, terminalId, senderChannel, shopId, endCustomer);
    }
}
