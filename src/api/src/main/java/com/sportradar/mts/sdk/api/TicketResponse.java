/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.TicketAcceptance;

import java.util.List;
import java.util.Map;

/**
 * Object containing ticket response data send as a result of {@link Ticket}
 */
public interface TicketResponse extends SdkTicket
{
    /**
     * Gets ticket acceptance result
     *
     * @return accepted or rejected
     */
    TicketAcceptance getStatus();

    /**
     * Gets the response reason
     *
     * @return cancellation reason
     */
    ResponseReason getReason();

    /**
     * Get the list of bet details
     * @return bet details
     */
    List<BetDetail> getBetDetails();

    /**
     * Gets the response signature/hash (previous BetAcceptanceId)
     * @return signature
     */
    String getSignature();

    /**
     * Gets the exchange rate used when converting currencies to EUR. Long multiplied by 10000 and rounded to a long value
     *
     * @return acceptance code
     */
    long getExchangeRate();

    /**
     * Returns additional ticket response info
     * Contains timestamps describing mts processing (receivedUtcTimestamp, validatedUtcTimestamp, respondedUtcTimestamp)
     * @return additional ticket response info
     */
    Map<String, String> getAdditionalInfo();

    /**
     * Gets the auto accepted odd
     * @return the auto accepted odd
     */
    List<AutoAcceptedOdds> getAutoAcceptedOdds();
}
