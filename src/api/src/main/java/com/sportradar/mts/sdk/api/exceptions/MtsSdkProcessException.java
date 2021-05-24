/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.exceptions;

/**
 * Exception to be thrown when there is some problem with processing within SDK
 */
public class MtsSdkProcessException extends MtsSdkRuntimeException {

    private static final long serialVersionUID = 6238452123879874120L;

    public MtsSdkProcessException(String message) {
        super(message);
    }

    public MtsSdkProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
