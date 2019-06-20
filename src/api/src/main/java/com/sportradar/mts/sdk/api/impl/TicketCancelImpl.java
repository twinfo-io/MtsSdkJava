/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.BetCancel;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.enums.TicketCancellationReason;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

import java.util.Date;
import java.util.List;

/**
 * Implementation of TicketCancel
 */
public class TicketCancelImpl implements TicketCancel {
    private final String ticketId;
    private final Date timestampUtc;
    private final String version;
    private final int bookmakerId;
    private final TicketCancellationReason code;
    private final String correlationId;
    private final Integer cancelPercent;
    private final List<BetCancel> betCancels;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TicketCancelImpl(@JsonProperty("ticketId") String ticketId,
                            @JsonProperty("bookmakerId") int bookmakerId,
                            @JsonProperty("code") TicketCancellationReason code,
                            @JsonProperty("timestampUtc") Date timestampUtc,
                            @JsonProperty("cancelPercent") Integer cancelPercent,
                            @JsonProperty("betCancels") List<BetCancel> betCancels,
                            @JsonProperty("version") String version) {
        Preconditions.checkNotNull(ticketId, "ticketId cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(ticketId), "ticketId is not valid");
        Preconditions.checkArgument(bookmakerId > 0, "bookmakerId is missing");
        Preconditions.checkNotNull(timestampUtc, "timestamp cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTimestamp(timestampUtc), "timestampUtc is not valid");
        Preconditions.checkNotNull(version, "version cannot be null");
        Preconditions.checkArgument(version.length() == 3, "version is not valid");
        Preconditions.checkArgument(cancelPercent == null || cancelPercent > 0, "cancelPercent has wrong value");
        Preconditions.checkArgument(!(cancelPercent != null && betCancels != null), "cancelPercent and betCancels cannot be set at the same time");

        this.ticketId = ticketId;
        this.bookmakerId = bookmakerId;
        this.code = code;
        this.timestampUtc = timestampUtc;
        this.version = version;
        this.correlationId = MtsTicketHelper.GenerateTicketCorrelationId();
        this.cancelPercent = cancelPercent;
        this.betCancels = betCancels;
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

    /**
     * Returns the correlation id
     * @return correlation id
     */
    @Override
    public String getCorrelationId() { return correlationId; }

    @Override
    public int getBookmakerId() {
        return bookmakerId;
    }

    @Override
    public TicketCancellationReason getCode() {
        return code;
    }

    /**
     * Cancel percent. Quantity multiplied by 10_000 and rounded to a long value. Percent of ticket to cancel.
     *
     * @return percent of ticket to cancel
     */
    @Override
    public Integer getCancelPercent() {
        return cancelPercent;
    }

    /**
     * Array of (betId, cancelPercent) pairs, if performing partial cancel. Applicable only if ticket-level cancel percent is null.
     *
     * @return array of betId, cancelPercent pairs
     */
    @Override
    public List<BetCancel> getBetCancels() {
        return betCancels;
    }

    /**
     * Returns the associated ticket in the required MTS JSON format
     *
     * @return the associated ticket in the required MTS JSON format
     */
    @JsonIgnore
    @Override
    public String getJsonValue() {
        com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.TicketCancelSchema dto = MtsDtoMapper.map(this);
        try {
            return JsonUtils.OBJECT_MAPPER.writeValueAsString(dto.getCancel());
        }
        catch (JsonProcessingException ex)
        {
            throw new RuntimeException("Exception during dto mapping: " + ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public String toString() {

        int count = betCancels == null ? 0 : betCancels.size();
        return "TicketCancelImpl{" +
                "bookmakerId=" + bookmakerId +
                ", ticketId='" + ticketId + '\'' +
                ", code=" + code +
                ", percent=" + cancelPercent +
                ", betCancels=" + count +
                ", timestamp=" + timestampUtc +
                ", version='" + version + '\'' +
                "}";
    }
}
