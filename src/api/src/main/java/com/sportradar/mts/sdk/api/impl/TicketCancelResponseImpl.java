/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.ResponseReason;
import com.sportradar.mts.sdk.api.TicketCancelResponse;
import com.sportradar.mts.sdk.api.enums.TicketCancelAcceptance;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

import java.util.Date;
import java.util.Map;

/**
 * Implementation of TicketCancelResponse
 */
public class TicketCancelResponseImpl implements TicketCancelResponse {

    private final String ticketId;
    private final Date timestampUtc;
    private final String version;
    private final ResponseReason reason;
    private final TicketCancelAcceptance status;
    private final String signature;
    private final String correlationId;
    private final Map<String, String> additionalInfo;
    private final String msgBody;

    public TicketCancelResponseImpl(String ticketId,
                                    ResponseReason reason,
                                    TicketCancelAcceptance status,
                                    String signature,
                                    Date timestampUtc,
                                    String version,
                                    String correlationId,
                                    Map<String, String> additionalInfo,
                                    String msgBody)
    {
        Preconditions.checkNotNull(ticketId, "ticketId cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(ticketId), "ticketId is not valid");
        Preconditions.checkNotNull(timestampUtc, "timestamp cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTimestamp(timestampUtc), "timestampUtc is not valid");
        Preconditions.checkNotNull(version, "version cannot be null");
        Preconditions.checkArgument(version.length() == 3, "version is not valid");
        Preconditions.checkNotNull(msgBody);

        this.ticketId = ticketId;
        this.reason = reason;
        this.status = status;
        this.signature = signature;
        this.timestampUtc = timestampUtc;
        this.version = version;
        this.correlationId = correlationId;
        this.additionalInfo = additionalInfo;
        this.msgBody = msgBody;
    }

    @Override
    public String getTicketId() {
        return ticketId;
    }

    @Override
    public Date getTimestampUtc() {
        return timestampUtc;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public TicketCancelAcceptance getStatus() {
        return status;
    }

    @Override
    public ResponseReason getReason() {
        return reason;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public String getCorrelationId() { return correlationId; }

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

    @Override
    public String toString() {
        return "TicketCancelResponseImpl{" +
                "ticketId='" + ticketId + '\'' +
                ", reason=" + reason +
                ", status=" + status +
                ", timestamp=" + timestampUtc +
                ", version='" + version + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}
