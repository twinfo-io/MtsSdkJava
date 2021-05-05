/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.exceptions;

/**
 * SDK exception base
 */
public abstract class MtsSdkException extends Exception {

    private static final long serialVersionUID = 3940119828912029787L;

    protected MtsSdkException() { }

    protected MtsSdkException(String message, Throwable cause) { super(message, cause); }

    protected MtsSdkException(String message) {
        super(message);
    }
}
