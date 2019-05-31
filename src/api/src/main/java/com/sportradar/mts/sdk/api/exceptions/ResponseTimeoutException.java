/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.exceptions;

/**
 * Exception to be thrown when response timeout is reached
 */
public class ResponseTimeoutException extends MtsSdkException {


    private static final long serialVersionUID = 6234498842378859869L;

    public ResponseTimeoutException(String message) {
        super(message);
    }
}
