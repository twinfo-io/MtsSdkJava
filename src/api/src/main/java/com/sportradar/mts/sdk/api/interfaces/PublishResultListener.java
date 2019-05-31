/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.SdkTicket;

/**
 * Base for any {@link SdkTicket} send result listener
 *
 * @param <T> message to be send to the MTS
 */
public interface PublishResultListener<T extends SdkTicket> {

    /**
     * Publishing was successful
     *
     * @param message message that was send
     */
    void publishFailure(T message);

    /**
     * Publishing was unsuccessful. Client can decide to resend message.
     *
     * @param message message that failed to be send
     */
    void publishSuccess(T message);
}
