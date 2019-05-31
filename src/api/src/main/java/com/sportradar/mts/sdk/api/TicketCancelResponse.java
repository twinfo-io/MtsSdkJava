/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.TicketCancelAcceptance;

import java.util.Map;

/**
 * Object containing ticket cancel response data send as a result of {@link TicketCancel}
 */
public interface TicketCancelResponse extends SdkTicket {

    /**
     * Gets the response reason
     *
     * @return cancellation reason
     */
    ResponseReason getReason();

    /**
     * Gets the status of the cancel
     * @return TicketCancelAcceptance
     */
    TicketCancelAcceptance getStatus();

    /**
     * Gets the response signature/hash (previous BetAcceptanceId)
     * @return signature
     */
    String getSignature();

    /**
     * Returns additional ticket response info
     * Contains timestamps describing mts processing (receivedUtcTimestamp, validatedUtcTimestamp, respondedUtcTimestamp)
     * @return additional ticket response info
     */
    Map<String, String> getAdditionalInfo();
}
