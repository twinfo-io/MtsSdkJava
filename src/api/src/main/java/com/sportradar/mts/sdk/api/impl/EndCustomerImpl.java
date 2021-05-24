/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.EndCustomer;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

/**
 * Implementation of EndCustomer interface
 */
public class EndCustomerImpl implements EndCustomer {

    private final String id;
    private final String ip;
    private final String langId;
    private final String deviceId;
    private final Long confidence;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public EndCustomerImpl(@JsonProperty("id") String id,
                           @JsonProperty("endCustomerIp") String ip,
                           @JsonProperty("languageId") String languageId,
                           @JsonProperty("deviceId") String deviceId,
                           @JsonProperty("confidence") Long confidence)
    {
        Preconditions.checkArgument(id == null || MtsTicketHelper.validateUserId(id), "id is not valid");
        Preconditions.checkArgument(id == null || id.length() <= 36, "id is too long");
        Preconditions.checkArgument(languageId == null || languageId.length() == 2, "languageId can be null or 2-letter sign");
        Preconditions.checkArgument(deviceId == null || MtsTicketHelper.validateUserId(deviceId), "deviceId is not valid");
        Preconditions.checkArgument(deviceId == null || deviceId.length() <= 36, "deviceId is too long");
        Preconditions.checkArgument(confidence == null || confidence >= 0, "confidence must not be negative");

        this.id = id;
        this.ip = ip;
        if(languageId != null && !languageId.isEmpty()) {
            this.langId = languageId.toUpperCase();
        }
        else
        {
            this.langId = null;
        }
        this.deviceId = deviceId;
        this.confidence = confidence;
    }

    @Override
    public String getEndCustomerIp() {
        return ip;
    }

    @Override
    public String getLanguageId() {
        return langId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Long getConfidence() {
        return confidence;
    }
}
