/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.exceptions;

/**
 * Exception to be thrown when response timeout is reached
 */
public class CustomBetException extends MtsSdkException {

    private static final long serialVersionUID = -4442103402296950069L;

    public CustomBetException() {
        super();
    }

    public CustomBetException(String message) {
        super(message);
    }

    public CustomBetException(String message, Throwable cause) {
        super(message, cause);
    }
}
