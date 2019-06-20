/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.EndCustomer;
import com.sportradar.mts.sdk.api.Sender;
import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.utils.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of Sender interface
 */
public class SenderImpl implements Sender {

    private final int bookmakerId;
    private final String currency;
    private final int limitId;
    private final String terminalId;
    private final SenderChannel channel;
    private final String shopId;
    private final EndCustomer endCustomer;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SenderImpl(@JsonProperty("bookmakerId") int bookmakerId,
                      @JsonProperty("currency") String currency,
                      @JsonProperty("limitId") int limitId,
                      @JsonProperty("terminalId") String terminalId,
                      @JsonProperty("channel") SenderChannel channel,
                      @JsonProperty("shopId") String shopId,
                      @JsonProperty("endCustomer") EndCustomer endCustomer)
    {
        checkArgument(bookmakerId > 0, "bookmakerId is missing");
        checkArgument(!StringUtils.isNullOrEmpty(currency), "currency is missing");
        checkArgument(currency.length() == 3 || currency.length() == 4, "currency must be 3 (or 4) letter sign");
        checkArgument(limitId >= 0, "limitId is invalid");
        checkArgument(terminalId == null || (terminalId.length() > 0 && terminalId.length() <= 36), "terminalId is not valid");
        checkNotNull(channel, "channel cannot be null");
        checkArgument(shopId == null || (shopId.length() > 0 && shopId.length() <= 36), "shopId is not valid");

        this.bookmakerId = bookmakerId;
        this.currency = currency.length() == 3 ? currency.toUpperCase() : currency;
        this.limitId = limitId;
        this.terminalId = terminalId;
        this.channel = channel;
        this.shopId = shopId;
        this.endCustomer = endCustomer;
    }

    @Override
    public int getBookmakerId() {
        return bookmakerId;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public int getLimitId() {
        return limitId;
    }

    @Override
    public String getTerminalId() {
        return terminalId;
    }

    @Override
    public SenderChannel getChannel() {
        return channel;
    }

    @Override
    public String getShopId() {
        return shopId;
    }

    @Override
    public EndCustomer getEndCustomer() {
        return endCustomer;
    }

    @Override
    public String toString() {
        return "SenderImpl{" +
                "bookmakerId=" + bookmakerId +
                ", currency='" + currency + '\'' +
                ", limitId=" + limitId +
                ", terminalId='" + terminalId + '\'' +
                ", channel=" + channel +
                ", shopId='" + shopId + '\'' +
                ", endCustomer='" + endCustomer + '\'' +
                '}';
    }
}
