/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.TicketAcceptance;

import java.util.Map;

public interface TicketNonSrSettleResponse extends SdkTicket {
    /**
     * Returns the reason for ticket rejection
     *
     * @return - the reason of the cancellation if available; otherwise null
     */
    ResponseReason getReason();

    /**
     * Returns the status of ticket assigned to the current instance
     *
     * @return - the status of ticket assigned to the current instance
     */
    TicketAcceptance getStatus();

    /**
     * Returns the signature of the response message
     *
     * @return - the signature of the response message
     */
    String getSignature();

    /**
     * Returns additional ticket response info
     * Contains timestamps describing mts processing (receivedUtcTimestamp, validatedUtcTimestamp, respondedUtcTimestamp)
     * @return additional ticket response info
     */
    Map<String, String> getAdditionalInfo();
}
