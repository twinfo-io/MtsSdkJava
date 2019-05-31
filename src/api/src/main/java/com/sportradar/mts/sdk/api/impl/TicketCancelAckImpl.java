/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.TicketCancelAck;
import com.sportradar.mts.sdk.api.enums.TicketCancelAckStatus;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

import java.util.Date;

/**
 * Implementation of the {@link TicketCancelAck} interface
 */
public class TicketCancelAckImpl implements TicketCancelAck {
    private final String ticketId;
    private final Date timestampUtc;
    private final String version;
    private final int bookmakerId;
    private final Integer code;
    private final String message;
    private final TicketCancelAckStatus status;
    private final String correlationId;

    public TicketCancelAckImpl(String ticketId,
                         int bookmakerId,
                         Integer code,
                         String message,
                         TicketCancelAckStatus ackStatus,
                         Date timestampUtc,
                         String version) {
        Preconditions.checkNotNull(ticketId, "ticketId cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(ticketId), "ticketId is not valid");
        Preconditions.checkArgument(bookmakerId > 0, "bookmakerId is missing");
        Preconditions.checkNotNull(timestampUtc, "timestamp cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTimestamp(timestampUtc), "timestampUtc is not valid");
        Preconditions.checkNotNull(version, "version cannot be null");
        Preconditions.checkArgument(version.length() == 3, "version is not valid");

        this.ticketId = ticketId;
        this.bookmakerId = bookmakerId;
        this.code = code;
        this.message = message;
        this.status = ackStatus;
        this.timestampUtc = timestampUtc;
        this.version = version;
        this.correlationId = MtsTicketHelper.GenerateTicketCorrelationId();
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
    public int getBookmakerId() {
        return bookmakerId;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public TicketCancelAckStatus getAckStatus() {
        return status;
    }

    @Override
    public String getCorrelationId() { return correlationId; }

    /**
     * Returns the associated ticket in the required MTS JSON format
     *
     * @return the associated ticket in the required MTS JSON format
     */
    @Override
    public String getJsonValue() {
        com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelack.TicketCancelAckSchema dto = MtsDtoMapper.map(this);
        try {
            return JsonUtils.OBJECT_MAPPER.writeValueAsString(dto);
        }
        catch (JsonProcessingException ex)
        {
            throw new RuntimeException("Exception during dto mapping: " + ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public String toString() {
        return "TicketCancelAckImpl{" +
                "bookmakerId=" + bookmakerId +
                ", ticketId='" + ticketId + '\'' +
                ", code=" + code +
                ", ackStatus=" + status +
                ", message='" + message + '\'' +
                ", timestamp=" + timestampUtc +
                ", version='" + version + '\'' +
                ", correlationId='" + correlationId + '\'' +
                "}";
    }
}
