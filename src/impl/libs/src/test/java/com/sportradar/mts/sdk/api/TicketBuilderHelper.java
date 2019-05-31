/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.builders.BetBuilder;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.builders.TicketBuilder;
import com.sportradar.mts.sdk.api.enums.*;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.*;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.BetDetail;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.SelectionDetail;
import com.sportradar.mts.sdk.api.utils.StaticRandom;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import com.sportradar.mts.sdk.impl.libs.SdkHelper;

import java.util.ArrayList;
import java.util.List;

public class TicketBuilderHelper {

    public final BuilderFactory builderFactory;

    public TicketBuilderHelper(BuilderFactory builderFactory)
    {
        if(builderFactory != null)
        {
            this.builderFactory = builderFactory;
        }
        else
        {
            this.builderFactory = new SdkHelper().getBuilderFactory();
        }
    }

    public Ticket getTicket(String ticketId, int bookmakerId, int betCount, int selectionCount)
    {
        TicketBuilder tb = builderFactory.createTicketBuilder();
        if (StringUtils.isNullOrEmpty(ticketId))
        {
            ticketId = "ticket-" + StaticRandom.I1000P;
        }
        if (bookmakerId < 1)
        {
            bookmakerId = StaticRandom.I1000P;
        }
        if (betCount < 1)
        {
            betCount = 1;
        }
        if (selectionCount < 1)
        {
            selectionCount = 1;
        }
        for (int i = 0; i < betCount; i++)
        {
            BetBuilder betBuilder = builderFactory.createBetBuilder();
            for (int j = 0; j < selectionCount; j++)
            {
                betBuilder.addSelection(builderFactory.createSelectionBuilder().setEventId(StaticRandom.S1000).setIdLcoo(StaticRandom.I1000, 1, "", "1").setOdds(StaticRandom.I1000P).setBanker(StaticRandom.I100 > 90).build());
            }

            Bet bet = betBuilder
                    .addSelectedSystem(1)
                    .setBetId("bet-id-" + StaticRandom.I1000)
                    .setBetBonus(StaticRandom.I1000, BetBonusMode.ALL, BetBonusType.TOTAL)
                    .setStake(92343, StakeType.TOTAL)
                    .build();
            tb.addBet(bet);
        }
        Ticket ticket = tb.setTicketId(ticketId).setOddsChange(OddsChangeType.ANY)
                .setSender(builderFactory.createSenderBuilder().setBookmakerId(bookmakerId).setLimitId(StaticRandom.I100).setCurrency("EUR").setSenderChannel(SenderChannel.INTERNET)
                        .setEndCustomer(builderFactory.createEndCustomerBuilder().setId("customer-client-" + StaticRandom.I1000).setConfidence(StaticRandom.L1000P).setIp("127.0.0.1").setLanguageId("en").setDeviceId(StaticRandom.S1000).build())
                        .build())
                .build();
        return ticket;
    }

    public TicketCancel getTicketCancel(String ticketId)
    {
        if (StringUtils.isNullOrEmpty(ticketId))
        {
            ticketId = "ticket-" + StaticRandom.I1000P;
        }
        return builderFactory.createTicketCancelBuilder().setTicketId(ticketId).setBookmakerId(StaticRandom.I1000).setCode(TicketCancellationReason.BookmakerBackofficeTriggered).build();
    }

    public TicketReofferCancel getTicketReofferCancel(String ticketId)
    {
        if (StringUtils.isNullOrEmpty(ticketId))
        {
            ticketId = "ticket-" + StaticRandom.I1000P;
        }
        return builderFactory.createTicketReofferCancelBuilder().setTicketId(ticketId).setBookmakerId(StaticRandom.I1000).build();
    }

    public TicketCashout getTicketCashout(String ticketId)
    {
        if (StringUtils.isNullOrEmpty(ticketId))
        {
            ticketId = "ticket-" + StaticRandom.I1000P;
        }
        return builderFactory.createTicketCashoutBuilder().setTicketId(ticketId).setBookmakerId(StaticRandom.I1000).setCashoutStake(StaticRandom.I1000P).build();
    }

    private static BetDetail getResponseBetDetail(String betId, boolean reoffer, boolean alt)
    {
        BetDetail betDetail = new BetDetail();
        betDetail.setBetId(StringUtils.isNullOrEmpty(betId) ? "bet-id-" + StaticRandom.S1000 : betId);
        betDetail.setReason(getReason());
        betDetail.setAlternativeStake(alt ? null : new AlternativeStake(StaticRandom.L1000P));

        Reoffer reoffer1 = new Reoffer();
        reoffer1.setStake(StaticRandom.L1000P);
        reoffer1.setType(StaticRandom.B ? Reoffer.Type.AUTO : Reoffer.Type.MANUAL);
        betDetail.setReoffer(reoffer ? null : reoffer1);

        List<SelectionDetail> selectionDetails = new ArrayList<>();
        SelectionDetail selectionDetail = new SelectionDetail();
        selectionDetail.setReason(getReason());
        selectionDetail.setSelectionIndex(StaticRandom.I(0));
        selectionDetails.add(selectionDetail);
        betDetail.setSelectionDetails(selectionDetails);

        return  betDetail;
    }

    private static Reason getReason()
    {
        Reason result = new Reason();
        result.setCode(StaticRandom.I1000);
        result.setMessage("message " + StaticRandom.I1000);
        return result;
    }

    public EndCustomer getEndCustomer(String id, String ip, String langId, String deviceId, long confidence)
    {
        if (StringUtils.isNullOrEmpty(id))
        {
            id = "customer-" + StaticRandom.I1000P;
        }
        if (StringUtils.isNullOrEmpty(ip))
        {
            ip = "127.0.0.1";
        }
        if (StringUtils.isNullOrEmpty(langId))
        {
            langId = "en";
        }
        if (StringUtils.isNullOrEmpty(deviceId))
        {
            deviceId = "device-" + StaticRandom.I1000P;
        }
        if (confidence < 1)
        {
            confidence = StaticRandom.I1000P;
        }
            return builderFactory.createEndCustomerBuilder()
                    .setId(id)
                    .setIp(ip)
                    .setLanguageId(langId)
                    .setDeviceId(deviceId)
                    .setConfidence(confidence)
                    .build();
    }
}
