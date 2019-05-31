/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.ResponseReason;
import com.sportradar.mts.sdk.api.TicketNonSrSettleResponse;
import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

import java.util.Date;
import java.util.Map;

/**
 * Implementation of the user exposed entity {@link TicketNonSrSettleResponse}
 */
public class TicketNonSrSettleResponseImpl implements TicketNonSrSettleResponse {

    /**
     * The assigned ticket id
     */
    private final String ticketId;

    /**
     * The timestamp of the ticket generation in UTC
     */
    private final Date timestampUtc;

    /**
     * The version of the format in which the ticket is built
     */
    private final String version;

    /**
     * The reason for rejection
     */
    private final ResponseReason reason;

    /**
     * The status of ticket assigned to the current instance
     */
    private final TicketAcceptance status;

    /**
     * The signature of the response message
     */
    private final String signature;

    /**
     * The correlation id
     */
    private final String correlationId;

    private final Map<String, String> additionalInfo;

    /**
     * the raw JSON payload received from MTS
     */
    private final String msgBody;

    public TicketNonSrSettleResponseImpl(String ticketId,
                                         Date timestampUtc,
                                         String version,
                                         ResponseReason reason,
                                         TicketAcceptance status,
                                         String signature,
                                         String correlationId,
                                         Map<String, String> additionalInfo,
                                         String msgBody) {
        Preconditions.checkNotNull(ticketId, "ticketId cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(ticketId), "ticketId is not valid");
        Preconditions.checkNotNull(timestampUtc, "timestamp cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTimestamp(timestampUtc), "timestampUtc is not valid");
        Preconditions.checkNotNull(version, "version cannot be null");
        Preconditions.checkArgument(version.length() == 3, "version is not valid");
        Preconditions.checkNotNull(status, "status cannot be null");
        Preconditions.checkNotNull(signature, "signature can not be null");
        Preconditions.checkNotNull(msgBody);

        this.ticketId = ticketId;
        this.timestampUtc = timestampUtc;
        this.version = version;
        this.reason = reason;
        this.status = status;
        this.signature = signature;
        this.correlationId = correlationId;
        this.additionalInfo = additionalInfo;
        this.msgBody = msgBody;
    }

    /**
     * Returns the assigned ticket id
     *
     * @return the assigned ticket id
     */
    @Override
    public String getTicketId() {
        return ticketId;
    }

    /**
     * Returns the timestamp of the ticket generation in UTC
     *
     * @return the timestamp of the ticket generation in UTC
     */
    @Override
    public Date getTimestampUtc() {
        return timestampUtc;
    }

    /**
     * Returns the version of the format in which the ticket is built
     *
     * @return the version of the format in which the ticket is built
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Returns the reason of the cancellation
     *
     * @return the reason of the cancellation if available; otherwise null
     */
    @Override
    public ResponseReason getReason() {
        return reason;
    }

    /**
     * Returns the status of ticket assigned to the current instance
     *
     * @return the status of ticket assigned to the current instance
     */
    @Override
    public TicketAcceptance getStatus() {
        return status;
    }

    /**
     * Returns the signature of the response message
     *
     * @return the signature of the response message
     */
    @Override
    public String getSignature() {
        return signature;
    }

    /**
     * Returns the correlation id
     * @return correlation id
     */
    @Override
    public String getCorrelationId() { return correlationId; }

    /**
     * Returns additional ticket response info
     * Contains timestamps describing mts processing (receivedUtcTimestamp, validatedUtcTimestamp, respondedUtcTimestamp)
     * @return additional ticket response info
     */
    @Override
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Returns the associated ticket in the required MTS JSON format
     *
     * @return the associated ticket in the required MTS JSON format
     */
    @Override
    public String getJsonValue() {
        return msgBody;
    }
}
