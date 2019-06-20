/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportradar.mts.sdk.api.ResponseReason;

/**
 * Implementation of the {@link ResponseReason} interface
 */
public class ResponseReasonImpl implements ResponseReason {

    private final int code;
    private final String message;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ResponseReasonImpl(@JsonProperty("code") int code,
                              @JsonProperty("message") String message)
    {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getInternalMessage() {
        return null;
    }
}
