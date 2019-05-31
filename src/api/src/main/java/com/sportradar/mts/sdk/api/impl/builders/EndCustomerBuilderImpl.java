/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.sportradar.mts.sdk.api.EndCustomer;
import com.sportradar.mts.sdk.api.builders.EndCustomerBuilder;
import com.sportradar.mts.sdk.api.impl.EndCustomerImpl;

/**
 * Implementation of the EndCustomerBuilder
 */
public class EndCustomerBuilderImpl implements EndCustomerBuilder {

    private String id;
    private String ip;
    private String langId;
    private String deviceId;
    private Long confidence;

    @Override
    public EndCustomerBuilder setId(String endCustomerId) {
        this.id = endCustomerId;
        return this;
    }

    @Override
    public EndCustomerBuilder setIp(String endCustomerIp) {
        this.ip = endCustomerIp;
        return this;
    }

    @Override
    public EndCustomerBuilder setLanguageId(String languageId) {
        this.langId = languageId;
        return this;
    }

    @Override
    public EndCustomerBuilder setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    @Override
    public EndCustomerBuilder setConfidence(Long confidence) {
        this.confidence = confidence;
        return this;
    }

    @Override
    public EndCustomer build() {
        return new EndCustomerImpl(id, ip, langId, deviceId, confidence);
    }
}
