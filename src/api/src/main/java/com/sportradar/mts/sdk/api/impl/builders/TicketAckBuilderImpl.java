/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.TicketAck;
import com.sportradar.mts.sdk.api.builders.TicketAckBuilder;
import com.sportradar.mts.sdk.api.enums.TicketAckStatus;
import com.sportradar.mts.sdk.api.impl.TicketAckImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;
import com.sportradar.mts.sdk.api.utils.SdkInfo;

import java.util.Date;

public class TicketAckBuilderImpl implements TicketAckBuilder {

    private Integer bookmakerId;
    private String extTicket;
    private Integer sourceCode;
    private String sourceMessage;
    private TicketAckStatus ackStatus;

    public TicketAckBuilderImpl(SdkConfiguration config)
    {
        Preconditions.checkNotNull(config);
        this.bookmakerId = config.getBookmakerId();
    }

    @Override
    public TicketAckBuilder setBookmakerId(Integer bookmakerId) {
        this.bookmakerId = bookmakerId;
        validateData(false, false, true);
        return this;
    }

    @Override
    public TicketAckBuilder setTicketId(String ticketId) {
        this.extTicket = ticketId;
        validateData(false, true, false);
        return this;
    }

    @Override
    public TicketAckBuilder setSourceCode(Integer sourceCode) {
        this.sourceCode = sourceCode;
        return this;
    }

    @Override
    public TicketAckBuilder setSourceMessage(String sourceMessage) {
        this.sourceMessage = sourceMessage;
        return this;
    }

    @Override
    public TicketAckBuilder setAckStatus(TicketAckStatus ackStatus) {
        this.ackStatus = ackStatus;
        return this;
    }

    @Override
    public TicketAck build() {
        validateData(true, false, false);
        return new TicketAckImpl(
                extTicket,
                bookmakerId,
                sourceCode,
                sourceMessage,
                ackStatus,
                new Date(),
                SdkInfo.MTS_TICKET_VERSION);
    }

    private void validateData(boolean all, boolean ticketId, boolean bookmakerId)
    {
        if ((all || ticketId) && !MtsTicketHelper.validateTicketId(extTicket)) {
            throw new IllegalArgumentException("TicketId not valid");
        }
        if ((all || bookmakerId) && this.bookmakerId <= 0) {
            throw new IllegalArgumentException("BookmakerId not valid.");
        }
    }
}
