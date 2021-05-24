/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.exceptions;

/**
 * SDK runtime exception base
 */
public abstract class MtsSdkRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -3142173870688185122L;

    protected MtsSdkRuntimeException() { }

    protected MtsSdkRuntimeException(String message) {
        super(message);
    }

    protected MtsSdkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
