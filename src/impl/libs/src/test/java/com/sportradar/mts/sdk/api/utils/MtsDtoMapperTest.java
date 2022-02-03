/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.sportradar.mts.sdk.api.*;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.*;
import com.sportradar.mts.sdk.api.impl.BetCancelImpl;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Bet;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Bonus;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.TicketSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.TicketCancelSchema;
import com.sportradar.mts.sdk.impl.libs.SdkHelper;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

/**
 * Tests for dto to entities mapper
 */
public class MtsDtoMapperTest extends TimeLimitedTestBase {

    private BuilderFactory builderFactory = new SdkHelper().getBuilderFactory();

    private Sender getSender()
    {
        return builderFactory.createSenderBuilder()
                .setEndCustomer(builderFactory.createEndCustomerBuilder()
                        .setId("customer-client-" + StaticRandom.I1000)
                        .setConfidence(1234L)
                        .setIp("::1")
                        .setLanguageId("en")
                        .setDeviceId("myDevice01")
                        .build())
                .setCurrency("EUR")
                .setBookmakerId(1111)
                .setLimitId(10)
                .setSenderChannel(SenderChannel.INTERNET)
                .build();
    }

    @Test
    public void buildSelectionRefWithDifferentOddsSelectionsTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Bet can not have selections");

        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .build())
                .build();
    }

    @Test
    public void buildReOfferForTicketWithSelectionRefWithDifferentOddsSelectionsWithLastMatchEndTimeTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Bet can not have selections");

        builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .build())
                .setLastMatchEndTime(new Date(new Date().getTime() + 10000))
                .build();
    }

    @Test
    public void buildReOfferForTicketWithSelectionRefWithDifferentOddsSelectionsTest()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                                .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                                .build())
                .setLastMatchEndTime(new Date(new Date().getTime() + 10000))
                .build();

        Assert.assertNotNull(ticket);

        Ticket ticketReOffer = builderFactory.createTicketReofferBuilder()
                .set(ticket, 100L, ticket.getTicketId() + "ReOffer")
                .build();

        Assert.assertNotNull(ticketReOffer);
        Assert.assertEquals(ticket.getLastMatchEndTime(), ticketReOffer.getLastMatchEndTime());
    }


    @Test
    public void buildSelectionRefWithSameOddsSelectionsWithLastMatchEndTimeTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Bet can not have selections");
        Date date = new Date(new Date().getTime() + 10000);
        
        builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .build())
                .setLastMatchEndTime(date)
                .build();
    }

    @Test
    public void buildSelectionRefWithDifferentOddsSelectionsWithLastMatchEndTimeTest()
    {
        Date date = new Date(new Date().getTime() + 10000);

        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                                .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                                .build())
                .setLastMatchEndTime(date)
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertNotNull(ticket.getLastMatchEndTime());
        Assert.assertEquals(date, ticket.getLastMatchEndTime());

        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.TicketSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Ticket dtoTicket = dto.getTicket();
        Assert.assertNotNull(dtoTicket);
        Assert.assertNotNull(dtoTicket.getLastMatchEndTime());
        Assert.assertEquals(new Long(ticket.getLastMatchEndTime().getTime()), dtoTicket.getLastMatchEndTime());
    }

    @Test
    public void buildSelectionRefWithDifferentOddsSelectionsWithPayCapTest()
    {
        Long payCap = 10000L;

        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                                .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                                .build())
                .setPayCap(payCap)
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertNotNull(ticket.getPayCap());
        Assert.assertEquals(payCap, ticket.getPayCap());

        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.TicketSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Ticket dtoTicket = dto.getTicket();
        Assert.assertNotNull(dtoTicket);
        Assert.assertNotNull(dtoTicket.getPayCap());
        Assert.assertEquals(ticket.getPayCap(), dtoTicket.getPayCap());
    }

    @Test
    public void buildSelectionRefWithDifferentOddsSelectionsWithBonusTest()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ODDS_BOOSTER, BetBonusPaidAs.FREE_BET).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                                .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                                .build())
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertNotNull(ticket.getBets());
        Assert.assertTrue(ticket.getBets().size() > 0);
        BetBonus betBonus = ticket.getBets().get(0).getBetBonus();
        Assert.assertNotNull(betBonus);
        Assert.assertEquals(15000, betBonus.getValue());
        Assert.assertEquals(BetBonusType.TOTAL, betBonus.getType());
        Assert.assertEquals(BetBonusMode.ALL, betBonus.getMode());
        Assert.assertEquals(BetBonusDescription.ODDS_BOOSTER, betBonus.getDescription());
        Assert.assertEquals(BetBonusPaidAs.FREE_BET, betBonus.getPaidAs());

        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.TicketSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Ticket dtoTicket = dto.getTicket();
        Assert.assertNotNull(dtoTicket);
        Assert.assertNotNull(dtoTicket.getBets());
        Assert.assertTrue(dtoTicket.getBets().size() > 0);
        Bonus dtoBetBonus = dtoTicket.getBets().get(0).getBonus();
        Assert.assertNotNull(dtoBetBonus);
        Assert.assertEquals(new Long(15000), dtoBetBonus.getValue());
        Assert.assertEquals(Bonus.Type.TOTAL, dtoBetBonus.getType());
        Assert.assertEquals(Bonus.Mode.ALL, dtoBetBonus.getMode());
        Assert.assertEquals(Bonus.Description.ODDS_BOOSTER, dtoBetBonus.getDescription());
        Assert.assertEquals(Bonus.PaidAs.FREE_BET, dtoBetBonus.getPaidAs());
    }

    @Test
    public void buildSelectionRefWithBankerSetWronglyTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Bet can not have selections");

        builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).setBanker(true).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).setBanker(false).build())
                        .build())
                .build();
    }

    @Test
    public void buildSelectionRefCorrectlyTest()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("bet-id-" + StaticRandom.I1000)
                        .setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH)
                        .setStake(92343, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelectedSystem(2)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:2/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:3/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("bet-id-" + StaticRandom.I1000)
                        .setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH)
                        .setStake(12345, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:3/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);

        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.TicketSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTicket());

        Assert.assertEquals(3, dto.getTicket().getSelections().size());
        Assert.assertEquals(2, dto.getTicket().getBets().size());

        Bet bet1 = dto.getTicket().getBets().get(0);
        Assert.assertEquals(2, bet1.getSelectedSystems().size());
        Assert.assertNull(bet1.getSelectionRefs());

        Bet bet2 = dto.getTicket().getBets().get(1);
        Assert.assertEquals(1, bet2.getSelectedSystems().size());
        Assert.assertEquals(1, bet2.getSelectionRefs().size());
        Assert.assertEquals(2, (long)bet2.getSelectionRefs().get(0).getSelectionIndex());

        String json = dto.toString();
        Assert.assertTrue(!json.isEmpty());
    }

    @Test
    public void buildSelectionRefWithMultipleSameSelectionsCorrectlyTest()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162701").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(10000).setBanker(true).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162702").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .build())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(12345, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .build())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(12345, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertEquals(3, ticket.getSelections().size());

        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.TicketSchema dto =  MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTicket());

        Assert.assertEquals(3, dto.getTicket().getSelections().size());
        Assert.assertEquals(3, dto.getTicket().getBets().size());

        Bet bet0 = dto.getTicket().getBets().get(0);
        Assert.assertEquals(1, bet0.getSelectedSystems().size());
        Assert.assertEquals(3, bet0.getSelectionRefs().size());
        Assert.assertEquals(0, (long)bet0.getSelectionRefs().get(0).getSelectionIndex());
        Assert.assertEquals(1, (long)bet0.getSelectionRefs().get(1).getSelectionIndex());
        Assert.assertEquals(2, (long)bet0.getSelectionRefs().get(2).getSelectionIndex());

        Bet bet1 = dto.getTicket().getBets().get(1);
        Assert.assertEquals(1, bet1.getSelectedSystems().size());
        Assert.assertEquals(1, bet1.getSelectionRefs().size());
        Assert.assertEquals(2, (long)bet1.getSelectionRefs().get(0).getSelectionIndex());

        Bet bet2 = dto.getTicket().getBets().get(2);
        Assert.assertEquals(1, bet2.getSelectedSystems().size());
        Assert.assertEquals(1, bet2.getSelectionRefs().size());
        Assert.assertEquals(2, (long)bet2.getSelectionRefs().get(0).getSelectionIndex());

        String json = dto.toString();
        Assert.assertTrue(!json.isEmpty());
    }

    @Test
    public void buildSelectionRefWithTheSameEventIdTest()
    {        
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("bet-id-" + StaticRandom.I1000)
                        .setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH)
                        .setStake(92343, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelectedSystem(2)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:2/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:3/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);

        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.TicketSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTicket());

        Assert.assertEquals(3, dto.getTicket().getSelections().size());

        Bet bet1 = dto.getTicket().getBets().get(0);
        Assert.assertEquals(2, bet1.getSelectedSystems().size());
        Assert.assertNull(bet1.getSelectionRefs());

        String json = dto.toString();
        Assert.assertTrue(!json.isEmpty());
    }

    @Test
    public void buildSelectionRefWithTheSameEventIdWithLastMatchEndTimeTest()
    {
        Date date = new Date(new Date().getTime() + 10000);
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("bet-id-" + StaticRandom.I1000)
                        .setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH)
                        .setStake(92343, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelectedSystem(2)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:2/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:3/sr:sport:1/400/1724?total=4.5").setOdds(10000).build())
                        .build())
                .setLastMatchEndTime(date)
                .build();

        Assert.assertNotNull(ticket);

        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.TicketSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTicket());

        Assert.assertEquals(3, dto.getTicket().getSelections().size());

        Bet bet1 = dto.getTicket().getBets().get(0);
        Assert.assertEquals(2, bet1.getSelectedSystems().size());
        Assert.assertNull(bet1.getSelectionRefs());

        Assert.assertNotNull(dto.getTicket().getLastMatchEndTime());
        Assert.assertEquals(new Long(date.getTime()), dto.getTicket().getLastMatchEndTime());

        String json = dto.toString();
        Assert.assertTrue(!json.isEmpty());
    }

    @Test
    public void betWithWrongSelectedSystemTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("selectedSystems contains invalid value");

        builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("bet-id-" + StaticRandom.I1000)
                        .setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH)
                        .setStake(92343, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelectedSystem(2)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .build())
                .build();
    }

    @Test
    public void betWithRepeatedSelectedSystemTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("selectedSystems can not be repeated");

        builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("bet-id-" + StaticRandom.I1000)
                        .setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH)
                        .setStake(92343, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .build())
                .build();
    }

    @Test
    public void betWithZeroSelectedSystemTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("selectedSystems - 0 is not valid value");

        builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("bet-id-" + StaticRandom.I1000)
                        .setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH)
                        .setStake(92343, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelectedSystem(0)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(20000).build())
                        .build())
                .build();
    }

    @Test
    public void buildMultiBetWithSameSelectionDifferentOddsTest()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(false).build())
                        .build())
                .addBet(builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(26000).setBanker(false).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);

        TicketSchema dto = MtsDtoMapper.map(ticket);
        String jsonMsg = JsonUtils.serializeAsString(dto);
        Assert.assertTrue(!jsonMsg.isEmpty());
    }

    @Test
    public void buildMultiBetWithSameSelectionDifferentOddsAndSameBankerTest()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(true).build())
                        .build())
                .addBet(builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(26000).setBanker(true).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);

        TicketSchema dto = MtsDtoMapper.map(ticket);
        String jsonMsg = JsonUtils.serializeAsString(dto);
        Assert.assertTrue(!jsonMsg.isEmpty());
    }

    @Test
    public void buildMultiBetWithSameSelectionDifferentBankerTest()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(true).build())
                        .build())
                .addBet(builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(false).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);

        TicketSchema dto = MtsDtoMapper.map(ticket);
        String jsonMsg = JsonUtils.serializeAsString(dto);
        Assert.assertTrue(!jsonMsg.isEmpty());
    }

    @Test
    public void betWithDefaultCustomBet()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertNull(ticket.getBets().get(0).getCustomBet());
        Assert.assertNull(ticket.getBets().get(0).getCalculationOdds());
    }

    @Test
    public void betWithoutCustomBet()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setCustomBet(false).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertFalse(ticket.getBets().get(0).getCustomBet());
        Assert.assertNull(ticket.getBets().get(0).getCalculationOdds());
    }

    @Test
    public void betWithoutCustomBetWithCalculationOdds()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("calculationOdds not valid");

        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setCustomBet(false).setCalculationOdds(1000).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .build())
                .build();
    }

    @Test
    public void betWithCustomBet()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("calculationOdds not valid");

        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setCustomBet(true).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .build())
                .build();
    }

    @Test
    public void betWithCustomBetWithCalculationOdds()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setCustomBet(true).setCalculationOdds(1000).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertTrue(ticket.getBets().get(0).getCustomBet());
        Assert.assertEquals(new Integer(1000), ticket.getBets().get(0).getCalculationOdds());
    }

    @Test
    public void betWithoutEntireStake()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertNull(ticket.getBets().get(0).getEntireStake());
    }

    @Test
    public void betWithEntireStake()
    {
        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setOddsChange(OddsChangeType.ANY)
                .setTestSource(false)
                .setSender(getSender())
                .addBet(builderFactory.createBetBuilder().setEntireStake(12345, StakeType.TOTAL).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(15000, BetBonusMode.ALL, BetBonusType.TOTAL, BetBonusDescription.ACCUMULATOR_BONUS, BetBonusPaidAs.CASH).setStake(92343, StakeType.TOTAL).addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder().setEventId("11162703").setId("uof:1/sr:sport:1/400/1724?total=4.5").setOdds(18000).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
        Assert.assertNotNull(ticket.getBets().get(0).getEntireStake());
        Assert.assertEquals(12345, ticket.getBets().get(0).getEntireStake().getValue());
        Assert.assertEquals(StakeType.TOTAL, ticket.getBets().get(0).getEntireStake().getType());
    }

    @Test
    public void cancelSuccessfulPercentBuildTest(){
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCancelPercent(80)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cancelSuccessfulBetCancelBuildTest(){
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCancel("bet-id:01", 192)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cancelTicketIdNullFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId(null)
                .setBookmakerId(1111)
                .setCancelPercent(80)
                .build();
    }

    @Test
    public void cancelWrongPercent01BuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCancelPercent(-80)
                .build();
    }

    @Test
    public void cancelWrongPercent02BuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCancelPercent(0)
                .build();
    }

    @Test
    public void cancelBothBetCancelAndPercentBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCancelPercent(23493)
                .addBetCancel("bet01", 848)
                .build();
    }

    @Test
    public void cancelMultipleBetCancelsBuildTest(){
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCancel("bet01", 848)
                .addBetCancel("bet04", 234)
                .addBetCancel("bet03", 654)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertEquals(3, ticket.getBetCancels().size());
    }

    @Test
    public void cancelRepeatedBetCancelsBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCancel("bet01", 848)
                .addBetCancel("bet03", 234)
                .addBetCancel("bet01", 653)
                .build();
    }

    @Test
    public void cancelTicketStakeToSchemaMappingTest(){
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(1111)
                .setCode(TicketCancellationReason.BookmakerBackofficeTriggered)
                .setCancelPercent(80)
                .build();
        TicketCancelSchema cancelSchema = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(cancelSchema);
        Assert.assertNotNull(cancelSchema.getCancel().getCancelPercent());
        Assert.assertNull(cancelSchema.getCancel().getBetCancel());
        Assert.assertEquals(80, cancelSchema.getCancel().getCancelPercent().intValue());
    }

    @Test
    public void cancelTicketBetCancelToSchemaMappingTest(){
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(1111)
                .setCode(TicketCancellationReason.BookmakerBackofficeTriggered)
                .addBetCancel("bet01", 101)
                .build();
        TicketCancelSchema cancelSchema = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(cancelSchema);
        Assert.assertNull(cancelSchema.getCancel().getCancelPercent());
        Assert.assertNotNull(cancelSchema.getCancel().getBetCancel());
        Assert.assertEquals(1, cancelSchema.getCancel().getBetCancel().size());
        Assert.assertEquals("bet01", cancelSchema.getCancel().getBetCancel().stream().findFirst().get().getId());
        Assert.assertEquals(101, cancelSchema.getCancel().getBetCancel().stream().findFirst().get().getCancelPercent().intValue());
    }

    @Test
    public void betCancelTest(){
        int value = 10101;
        BetCancel betCancel = new BetCancelImpl("bet-id:01", value);
        Assert.assertNotNull(betCancel);
        Assert.assertEquals(value, betCancel.getCancelPercent().intValue());
    }

    @Test
    public void betCancelWrong01Test(){
        BetCancel betCancel = new BetCancelImpl("bet-id:01", 0);
        Assert.assertNotNull(betCancel);
    }

    @Test
    public void betCancelWrong02Test(){
        thrown.expect(IllegalArgumentException.class);
        new BetCancelImpl("bet-id:01", -1010);
    }

    @Test
    public void betCancelWrong03Test(){
        thrown.expect(IllegalArgumentException.class);
        new BetCancelImpl("", 234324);
    }

    private TicketSchema getTicketSchemaFromJson(String json)
    {
        TicketSchema dto = null;
        try {
            dto = JsonUtils.deserialize(json, TicketSchema.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dto;
    }
}