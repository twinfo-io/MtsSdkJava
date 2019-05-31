/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.exceptions;

/**
 * Exception to be thrown when there is some problem with processing of provided SDK properties
 */
public class MtsPropertiesException extends MtsSdkRuntimeException {

    private static final long serialVersionUID = 5673034990336874977L;

    public MtsPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public MtsPropertiesException(String message) {
        super(message);
    }
}
