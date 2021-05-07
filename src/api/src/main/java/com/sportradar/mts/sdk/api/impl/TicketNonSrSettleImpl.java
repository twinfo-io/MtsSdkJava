/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.TicketNonSrSettle;
import com.sportradar.mts.sdk.api.exceptions.MtsSdkProcessException;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

import java.util.Date;

/**
 * Implementation of the user exposed entity {@link TicketNonSrSettle}
 */
public class TicketNonSrSettleImpl implements TicketNonSrSettle {

    /**
     * The assigned ticket id
     */
    private final String ticketId;

    /**
     * The bookmaker id
     */
    private final int bookmakerId;

    /**
     * The timestamp of the ticket generation in UTC
     */
    private final Date timestampUtc;

    /**
     * The non-Sportradar ticket settle stake of the assigned ticket
     */
    private final Long nonSrSettleStake;

    /**
     * The version of the format in which the ticket is built
     */
    private final String version;

    private final String correlationId;

    /**
     * Initializes a new instance of the {@link TicketCashoutImpl}
     *
     * @param ticketId - the assigned ticket id
     * @param bookmakerId - the parent bookmaker id
     * @param timestampUtc - the timestamp of the ticket generation in UTC
     * @param nonSrSettleStake - the non-Sportradar ticket settle stake of the assigned ticket
     * @param version - the version of the format in which the ticket is built
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TicketNonSrSettleImpl(@JsonProperty("ticketId") String ticketId,
                                 @JsonProperty("bookmakerId") int bookmakerId,
                                 @JsonProperty("timestampUtc") Date timestampUtc,
                                 @JsonProperty("nonSrSettleStake") Long nonSrSettleStake,
                                 @JsonProperty("version") String version) {
        Preconditions.checkNotNull(ticketId, "ticketId cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(ticketId), "ticketId is not valid");
        Preconditions.checkArgument(bookmakerId > 0, "bookmakerId is missing");
        Preconditions.checkNotNull(timestampUtc, "timestamp cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTimestamp(timestampUtc), "timestampUtc is not valid");
        Preconditions.checkNotNull(version, "version cannot be null");
        Preconditions.checkArgument(version.length() == 3, "version is not valid");
        Preconditions.checkArgument(nonSrSettleStake != null && nonSrSettleStake >= 0, "nonSrSettleStake has wrong value");

        this.timestampUtc = timestampUtc;
        this.bookmakerId = bookmakerId;
        this.ticketId = ticketId;
        this.nonSrSettleStake = nonSrSettleStake;
        this.version = version;
        this.correlationId = MtsTicketHelper.generateTicketCorrelationId();
    }

    /**
     * Returns the assigned ticket id
     *
     * @return - the assigned ticket id
     */
    @Override
    public String getTicketId() {
        return ticketId;
    }

    /**
     * Returns the bookmaker id
     *
     * @return - the bookmaker id
     */
    @Override
    public int getBookmakerId() {
        return bookmakerId;
    }

    /**
     * The timestamp of the ticket generation in UTC
     */
    @Override
    public Date getTimestampUtc() {
        return timestampUtc;
    }

    /**
     * The version of the format in which the ticket is built
     */
    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Returns the associated ticket in the required MTS JSON format
     *
     * @return the associated ticket in the required MTS JSON format
     */
    @JsonIgnore
    @Override
    public String getJsonValue() {
        com.sportradar.mts.sdk.api.impl.mtsdto.ticketnonsrsettle.TicketNonSrSettleSchema dto = MtsDtoMapper.map(this);
        try {
            return JsonUtils.OBJECT_MAPPER.writeValueAsString(dto);
        } catch (JsonProcessingException ex) {
            throw new MtsSdkProcessException("Exception during dto mapping: " + ex.getMessage(), ex.getCause());
        }
    }

    /**
     * The non-Sportradar ticket settle stake
     */
    @Override
    public Long getNonSrSettleStake() {
        return nonSrSettleStake;
    }

    /**
     * Returns a {@link String} describing the current instance
     *
     * @return - a {@link String} describing the current instance
     */
    @Override
    public String toString() {
        return "TicketNonSrSettleImpl{" +
                "bookmakerId=" + bookmakerId + ", " +
                "ticketId='" + ticketId + "', " +
                "settleStake=" + nonSrSettleStake + ", " +
                "timestamp=" + timestampUtc + ", " +
                "version='" + version + "'" +
                "}";
    }
}
