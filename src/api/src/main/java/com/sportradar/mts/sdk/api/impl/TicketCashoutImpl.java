/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.BetCashout;
import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

import java.util.Date;
import java.util.List;

/**
 * Implementation of the user exposed entity {@link TicketCashout}
 */
public class TicketCashoutImpl implements TicketCashout {
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
     * The parent bookmaker id
     */
    private final int bookmakerId;

    /**
     * The cashout stake of the assigned ticket
     */
    private final Long cashoutStake;

    /**
     * cashout percent
     */
    private final Integer cashoutPercent;

    /**
     * array of betId, cashoutStake pairs
     */
    private final List<BetCashout> betCashouts;

    private final String correlationId;

    /**
     * Initializes a new instance of the {@link TicketCashoutImpl}
     *
     * @param ticketId - the assigned ticket id
     * @param bookmakerId - the parent bookmaker id
     * @param timestampUtc - the timestamp of the ticket generation in UTC
     * @param cashoutStake - the cashout stake of the assigned ticket
     * @param version - the version of the format in which the ticket is built
     */
    public TicketCashoutImpl(String ticketId, int bookmakerId, Date timestampUtc, Long cashoutStake, Integer cashoutPercent, List<BetCashout> betCashouts, String version) {

        Preconditions.checkNotNull(ticketId, "ticketId cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(ticketId), "ticketId is not valid");
        Preconditions.checkArgument(bookmakerId > 0, "bookmakerId is missing");
        Preconditions.checkNotNull(timestampUtc, "timestamp cannot be null");
        Preconditions.checkArgument(MtsTicketHelper.validateTimestamp(timestampUtc), "timestampUtc is not valid");
        Preconditions.checkNotNull(version, "version cannot be null");
        Preconditions.checkArgument(version.length() == 3, "version is not valid");
        Preconditions.checkArgument(cashoutStake == null || cashoutStake > 0, "cashoutStake has wrong value");
        Preconditions.checkArgument(cashoutPercent == null || cashoutPercent > 0, "cashoutPercent has wrong value");
        Preconditions.checkArgument(cashoutStake != null || cashoutPercent != null || betCashouts != null, "cashoutStake, cashoutPercent or betCashouts must be set");
        Preconditions.checkArgument(!(cashoutStake != null && betCashouts != null), "cashoutStake and betCashouts cannot be set at the same time");
        Preconditions.checkArgument(!(cashoutPercent != null && betCashouts != null), "cashoutPercent and betCashouts cannot be set at the same time");
        Preconditions.checkArgument(cashoutPercent == null || cashoutStake != null, "if cashoutPercent is set, also cashoutStake must be");
        Preconditions.checkArgument(betCashouts == null || betCashouts.size() <= 50, "only 50 betCashouts allowed");

        this.ticketId = ticketId;
        this.bookmakerId = bookmakerId;
        this.timestampUtc = timestampUtc;
        this.cashoutStake = cashoutStake;
        this.version = version;
        this.correlationId = MtsTicketHelper.GenerateTicketCorrelationId();
        this.cashoutPercent = cashoutPercent;
        this.betCashouts = betCashouts;
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
     * Returns the parent bookmaker id
     *
     * @return - the parent bookmaker id
     */
    @Override
    public int getBookmakerId() {
        return bookmakerId;
    }

    /**
     * Returns the timestamp of the ticket generation in UTC
     *
     * @return - the timestamp of the ticket generation in UTC
     */
    @Override
    public Date getTimestampUtc() {
        return timestampUtc;
    }

    /**
     * Returns the cashout stake of the assigned ticket
     *
     * @return - the cashout stake of the assigned ticket
     */
    @Override
    public Long getCashoutStake() {
        return cashoutStake;
    }

    /**
     * Cashout percent. Quantity multiplied by 10_000 and rounded to a long value. Percent of ticket to cashout.
     *
     * @return percent of ticket to cashout
     */
    @Override
    public Integer getCashoutPercent() {
        return cashoutPercent;
    }

    /**
     * Array of (betId, cashoutStake) pairs, if performing partial cashout. Applicable only if ticket-level cashout stake is null.
     *
     * @return array of betId, cashoutStake pairs
     */
    @Override
    public List<BetCashout> getBetCashouts() {
        return betCashouts;
    }

    /**
     * Returns the version of the format in which the ticket is built
     *
     * @return - the version of the format in which the ticket is built
     */
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

    /**
     * Returns the associated ticket in the required MTS JSON format
     *
     * @return the associated ticket in the required MTS JSON format
     */
    @Override
    public String getJsonValue() {
        try {
            return JsonUtils.OBJECT_MAPPER.writeValueAsString(MtsDtoMapper.map(this));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Exception during dto mapping: " + ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Returns a {@link String} describing the current instance
     *
     * @return - a {@link String} describing the current instance
     */
    @Override
    public String toString() {

        int count = betCashouts == null ? 0 : betCashouts.size();
        return "TicketCashoutImpl{" +
                "bookmakerId=" + bookmakerId + ", " +
                "ticketId='" + ticketId + "', " +
                "cashoutStake=" + cashoutStake + ", " +
                "cashoutPercent=" + cashoutPercent + ", " +
                "betCashouts=" + count + ", " +
                "timestamp=" + timestampUtc + ", " +
                "version='" + version + "'" +
                "}";
    }
}
