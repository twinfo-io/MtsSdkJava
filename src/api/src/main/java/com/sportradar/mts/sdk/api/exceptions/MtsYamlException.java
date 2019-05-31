/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.exceptions;

/**
 * Created on 13/04/2018.
 * // TODO @eti: Javadoc
 */
public class MtsYamlException extends MtsSdkRuntimeException {

    private static final long serialVersionUID = 46468486514682L;

    public MtsYamlException(String message, Throwable cause) {
        super(message, cause);
    }

    public MtsYamlException(String message) {
        super(message);
    }
}
