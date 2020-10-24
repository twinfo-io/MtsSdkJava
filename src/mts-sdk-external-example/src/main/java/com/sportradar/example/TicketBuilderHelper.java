/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.example;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.*;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.*;

import java.util.Random;

/**
 * Methods for building various ticket objects
 */
public class TicketBuilderHelper {

    private BuilderFactory builderFactory;

    public TicketBuilderHelper(BuilderFactory builderFactory)
    {
        Preconditions.checkNotNull(builderFactory);
        this.builderFactory = builderFactory;
    }

    public Ticket getTicket() {
        return builderFactory.createTicketBuilder()
                .setTicketId("T-" + System.currentTimeMillis())
                .setOddsChange(OddsChangeType.ANY)
                .setSender(builderFactory.createSenderBuilder()
                        .setBookmakerId(Constants.BOOKMAKER_ID)
                        .setLimitId(Constants.LIMIT_ID)
                        .setSenderChannel(SenderChannel.INTERNET)
                        .setCurrency("EUR")
                        .setEndCustomer("1.2.3.4", "Customer-" + new Random().nextInt(10000), "EN", "device1", 12092L)
                        .build())
                .addBet(builderFactory.createBetBuilder()
                                .setBetId("Bet-" + System.currentTimeMillis())
                                .addSelectedSystem(1)
                                .setStake(50000, StakeType.UNIT)
                                .addSelection(builderFactory.createSelectionBuilder()
                                                .setEventId("11608059")
                                                .setIdUof(1, "sr:sport:1", 1, "1", "", null)
                                                .setOdds(10400)
                                                .setBanker(false)
                                                .build())
                                .build()
                )
                .build();
    }

    public TicketAck getTicketAck(TicketResponse ticketResponse) {
        return builderFactory.createTicketAckBuilder()
                .setTicketId(ticketResponse.getTicketId())
                .setBookmakerId(Constants.BOOKMAKER_ID)
                .setAckStatus(ticketResponse.getStatus() == TicketAcceptance.ACCEPTED ? TicketAckStatus.ACCEPTED : TicketAckStatus.REJECTED)
                .setSourceCode(ticketResponse.getReason().getCode())
                .build();
    }

    public TicketCancelAck getTicketCancelAck(TicketCancelResponse ticketCancelResponse) {
        return builderFactory.createTicketCancelAckBuilder()
                .setTicketId(ticketCancelResponse.getTicketId())
                .setBookmakerId(Constants.BOOKMAKER_ID)
                .setAckStatus(ticketCancelResponse.getReason().getCode() == 1024 ? TicketCancelAckStatus.CANCELLED : TicketCancelAckStatus.NOT_CANCELLED)
                .setSourceCode(ticketCancelResponse.getReason().getCode())
                .build();
    }

    public TicketCancel getTicketCancel(String ticketId) {
        return builderFactory.createTicketCancelBuilder().build(ticketId, Constants.BOOKMAKER_ID, TicketCancellationReason.CustomerTriggeredPrematch);
    }

    public TicketCashout getTicketCashout(String ticketId) {
        return builderFactory.createTicketCashoutBuilder().setBookmakerId(Constants.BOOKMAKER_ID).setTicketId(ticketId).setCashoutStake(12345).build();
    }

    public TicketNonSrSettle getTicketNonSrSettle(String ticketId) {
        return builderFactory.createTicketNonSrSettleBuilder().setBookmakerId(Constants.BOOKMAKER_ID).setTicketId(ticketId).setNonSRSettleStake(12345).build();
    }
}
