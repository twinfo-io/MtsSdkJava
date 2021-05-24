package com.sportradar.mts.sdk.api.exceptions;

public class MtsApiException extends MtsSdkException {

    private static final long serialVersionUID = 12456998724592558L;

    public MtsApiException(String message) {
        super(message);
    }

    public MtsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
