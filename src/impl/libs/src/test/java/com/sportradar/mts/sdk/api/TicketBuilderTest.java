/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.*;
import com.sportradar.mts.sdk.api.utils.StaticRandom;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class TicketBuilderTest extends TimeLimitedTestBase {

    private TicketBuilderHelper ticketBuilderHelper;
    private EndCustomer defaultEndCustomer;
    private Sender defaultSender;

    @Before
    public void Init()
    {
        ticketBuilderHelper = new TicketBuilderHelper(null);
        defaultEndCustomer = ticketBuilderHelper.getEndCustomer(null, null, null, null, 0);
        defaultSender = ticketBuilderHelper.builderFactory.createSenderBuilder()
                .setEndCustomer(ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
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
    public void BuildTicketTest()
    {
        Ticket ticket = ticketBuilderHelper.getTicket(null, 0, 0, 0);
        Assert.assertNotNull(ticket);
    }

    @Test
    public void BuildEndCustomerTest()
    {
        EndCustomer item = ticketBuilderHelper.getEndCustomer(null, null, null, null, 0);
        Assert.assertNotNull(item);
    }

    @Test
    public void BuildEndCustomer_CustomerIdNullTest()
    {
        EndCustomer item = ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
                .setId(null)
                .setIp("127.0.0.1")
                .setLanguageId("en")
                .setDeviceId("device-" + StaticRandom.I1000P)
                .setConfidence(StaticRandom.L1000P)
                .build();
        Assert.assertNotNull(item);
    }

    @Test
    public void BuildEndCustomer_CustomerIdEmptyTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("id is not valid");

        EndCustomer item = ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
                .setId("")
                .setIp("127.0.0.1")
                .setLanguageId("en")
                .setDeviceId("device-" + StaticRandom.I1000P)
                .setConfidence(StaticRandom.L1000P)
                .build();
    }

    @Test
    public void BuildEndCustomer_LanguageIdEmptyTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("languageId can be null or 2-letter sign");

        EndCustomer item = ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
                .setId("customer-" + StaticRandom.I1000P)
                .setIp("127.0.0.1")
                .setLanguageId("")
                .setDeviceId("device-" + StaticRandom.I1000P)
                .setConfidence(StaticRandom.L1000P)
                .build();
    }

    @Test
    public void BuildEndCustomer_LanguageIdNot2letterTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("languageId can be null or 2-letter sign");

        EndCustomer item = ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
                .setId("customer-" + StaticRandom.I1000P)
                .setIp("127.0.0.1")
                .setLanguageId("eng")
                .setDeviceId("device-" + StaticRandom.I1000P)
                .setConfidence(StaticRandom.L1000P)
                .build();
    }

    @Test
    public void BuildEndCustomer_DeviceIdNullTest()
    {
        EndCustomer item = ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
                .setId("customer-" + StaticRandom.L1000P)
                .setIp("127.0.0.1")
                .setLanguageId("en")
                .setDeviceId(null)
                .setConfidence(StaticRandom.L1000P)
                .build();
        Assert.assertNotNull(item);
    }

    @Test
    public void BuildEndCustomer_DeviceIdEmptyTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("deviceId is not valid");

        EndCustomer item = ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
                .setId("customer-" + StaticRandom.I1000P)
                .setIp("127.0.0.1")
                .setLanguageId("en")
                .setDeviceId("")
                .setConfidence(StaticRandom.L1000P)
                .build();
    }

    @Test
    public void BuildEndCustomer_ConfidenceZeroTest()
    {
        EndCustomer item = ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
                .setId("customer-" + StaticRandom.I1000P)
                .setIp("127.0.0.1")
                .setLanguageId("en")
                .setDeviceId("device-" + StaticRandom.I1000P)
                .setConfidence(0L)
                .build();
        Assert.assertNotNull(item);
    }

    @Test
    public void BuildEndCustomer_ConfidenceNegativeTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("confidence must not be negative");

        EndCustomer item = ticketBuilderHelper.builderFactory.createEndCustomerBuilder()
                .setId("customer-" + StaticRandom.I1000P)
                .setIp("127.0.0.1")
                .setLanguageId("en")
                .setDeviceId("device-" + StaticRandom.I1000P)
                .setConfidence(-1L)
                .build();
    }

    @Test
    public void BuildSender_ValidCurrencyTest()
    {
        Sender item = ticketBuilderHelper.builderFactory.createSenderBuilder()
                .setCurrency("eur")
                .setBookmakerId(1)
                .setEndCustomer(defaultEndCustomer)
                .setLimitId(1)
                .setSenderChannel(SenderChannel.INTERNET)
                .build();
        Assert.assertNotNull(item);
        Assert.assertEquals("EUR", item.getCurrency());

        Sender item2 = ticketBuilderHelper.builderFactory.createSenderBuilder()
                .setCurrency("mBTC")
                .setBookmakerId(1)
                .setEndCustomer(defaultEndCustomer)
                .setLimitId(1)
                .setSenderChannel(SenderChannel.INTERNET)
                .build();
        Assert.assertNotNull(item2);
        Assert.assertEquals("mBTC", item2.getCurrency());
    }

    @Test
    public void BuildSender_TooShortCurrencyTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("currency must be 3 (or 4) letter sign");
        Sender item = ticketBuilderHelper.builderFactory.createSenderBuilder()
                .setCurrency("eu")
                .setBookmakerId(1)
                .setEndCustomer(defaultEndCustomer)
                .setLimitId(1)
                .setSenderChannel(SenderChannel.INTERNET)
                .build();
    }

    @Test
    public void BuildSender_TooLongCurrencyTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("currency must be 3 (or 4) letter sign");
        Sender item = ticketBuilderHelper.builderFactory.createSenderBuilder()
                .setCurrency("mmBTC")
                .setBookmakerId(1)
                .setEndCustomer(defaultEndCustomer)
                .setLimitId(1)
                .setSenderChannel(SenderChannel.INTERNET)
                .build();
    }

    @Test
    public void buildBetWithSameSelectionDifferentOddsTest()
    {
        thrown.expect(IllegalArgumentException.class);

        Ticket ticket = ticketBuilderHelper.builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(defaultSender)
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000, BetBonusMode.ALL, BetBonusType.TOTAL).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(false).build())
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(26000).setBanker(false).build())
                        .build())
                .build();
    }

    @Test
    public void BuildBetWithSameSelectionDifferentBankerTest()
    {
        thrown.expect(IllegalArgumentException.class);

        Ticket ticket = ticketBuilderHelper.builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(defaultSender)
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000, BetBonusMode.ALL, BetBonusType.TOTAL).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(true).build())
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(false).build())
                        .build())
                .build();
    }

    @Test
    public void BuildMultiBetWithSameSelectionDifferentOddsTest()
    {
        Ticket ticket = ticketBuilderHelper.builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(defaultSender)
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000, BetBonusMode.ALL, BetBonusType.TOTAL).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(false).build())
                        .build())
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000, BetBonusMode.ALL, BetBonusType.TOTAL).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(26000).setBanker(false).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
    }

    @Test
    public void BuildMultiBetWithSameSelectionDifferentOddsAndSameBankerTest()
    {
        Ticket ticket = ticketBuilderHelper.builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(defaultSender)
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000, BetBonusMode.ALL, BetBonusType.TOTAL).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(true).build())
                        .build())
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(26000).setBanker(true).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
    }

    @Test
    public void BuildMultiBetWithSameSelectionDifferentBankerTest()
    {
        Ticket ticket = ticketBuilderHelper.builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(defaultSender)
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(true).build())
                        .build())
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(false).build())
                        .build())
                .build();

        Assert.assertNotNull(ticket);
    }

    @Test
    public void BuildMultiBetWithSameSelectionDifferentBankerWithLastMatchEndTimeTest()
    {
        Ticket ticket = ticketBuilderHelper.builderFactory.createTicketBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(defaultSender)
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(true).build())
                        .build())
                .addBet(ticketBuilderHelper.builderFactory.createBetBuilder().addSelectedSystem(1).setBetId("bet-id-" + StaticRandom.I1000).setBetBonus(StaticRandom.I1000).setStake(92343, StakeType.TOTAL)
                        .addSelection(ticketBuilderHelper.builderFactory.createSelectionBuilder().setEventId("9691139").setIdLcoo(324, 1, "", "1").setOdds(16000).setBanker(false).build())
                        .build())
                .setLastMatchEndTime(new Date(new Date().getTime() + 10000))
                .build();

        Assert.assertNotNull(ticket);
    }
}
