/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.TicketCancelAck;
import com.sportradar.mts.sdk.api.builders.TicketCancelAckBuilder;
import com.sportradar.mts.sdk.api.enums.TicketCancelAckStatus;
import com.sportradar.mts.sdk.api.impl.TicketCancelAckImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;
import com.sportradar.mts.sdk.api.utils.SdkInfo;

import java.util.Date;

/**
 * implementation of the {@link TicketAckBuilderImpl} interface
 */
public class TicketCancelAckBuilderImpl implements TicketCancelAckBuilder {
    private Integer bookmakerId;
    private String extTicket;
    private Integer sourceCode;
    private String sourceMessage;
    private TicketCancelAckStatus ackStatus;

    public TicketCancelAckBuilderImpl(SdkConfiguration config)
    {
        Preconditions.checkNotNull(config, "missing SdkConfiguration");
        this.bookmakerId = config.getBookmakerId();
    }

    @Override
    public TicketCancelAckBuilder setBookmakerId(Integer bookmakerId) {
        this.bookmakerId = bookmakerId;
        ValidateData(false, false, true);
        return this;
    }

    @Override
    public TicketCancelAckBuilder setTicketId(String ticketId) {
        this.extTicket = ticketId;
        ValidateData(false, true, false);
        return this;
    }

    @Override
    public TicketCancelAckBuilder setSourceCode(Integer sourceCode) {
        this.sourceCode = sourceCode;
        return this;
    }

    @Override
    public TicketCancelAckBuilder setSourceMessage(String sourceMessage) {
        this.sourceMessage = sourceMessage;
        return this;
    }

    @Override
    public TicketCancelAckBuilder setAckStatus(TicketCancelAckStatus ackStatus) {
        this.ackStatus = ackStatus;
        return this;
    }

    @Override
    public TicketCancelAck build() {
        ValidateData(true, false, false);
        return new TicketCancelAckImpl(
                extTicket,
                bookmakerId,
                sourceCode,
                sourceMessage,
                ackStatus,
                new Date(),
                SdkInfo.mtsTicketVersion());
    }

    private void ValidateData(boolean all, boolean ticketId, boolean bookmakerId)
    {
        if (all || ticketId)
        {
            if (!MtsTicketHelper.validateTicketId(extTicket))
            {
                throw new IllegalArgumentException("TicketId not valid");
            }
        }
        if (all || bookmakerId)
        {
            if (this.bookmakerId <= 0)
            {
                throw new IllegalArgumentException("BookmakerId not valid.");
            }
        }
    }
}
