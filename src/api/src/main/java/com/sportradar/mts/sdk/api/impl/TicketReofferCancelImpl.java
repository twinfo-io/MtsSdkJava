/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.TicketReofferCancel;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

import java.util.Date;

/**
 * Implementation of TicketReofferCancel
 */
public class TicketReofferCancelImpl implements TicketReofferCancel {
    private final String ticketId;
    private final Date timestampUtc;
    private final String version;
    private final int bookmakerId;
    private final String correlationId;

    public TicketReofferCancelImpl(String ticketId,
                                   int bookmakerId,
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
    public String getCorrelationId() { return correlationId; }

    /**
     * Returns the associated ticket in the required MTS JSON format
     *
     * @return the associated ticket in the required MTS JSON format
     */
    @Override
    public String getJsonValue() {
        com.sportradar.mts.sdk.api.impl.mtsdto.reoffercancel.TicketReofferCancelSchema dto = MtsDtoMapper.map(this);
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
        return "TicketReofferCancelImpl{" +
                "bookmakerId=" + bookmakerId +
                ", ticketId='" + ticketId + '\'' +
                ", timestamp=" + timestampUtc +
                ", version='" + version + '\'' +
                ", correlationId='" + correlationId + '\'' +
                "}";
    }
}
