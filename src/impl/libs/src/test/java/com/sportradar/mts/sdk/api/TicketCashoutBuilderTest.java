/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.impl.BetCashoutImpl;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashout.TicketCashoutSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.Reason;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.Result;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.TicketCashoutResponseSchema;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.SdkInfo;
import com.sportradar.mts.sdk.api.utils.StaticRandom;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import com.sportradar.mts.sdk.impl.libs.SdkHelper;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Assert;
import org.junit.Test;

public class TicketCashoutBuilderTest extends TimeLimitedTestBase {

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
    public void cashoutStakeBuildTest() {
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCashoutStake(80)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertNotNull(ticket.getCashoutStake());
        Assert.assertNull(ticket.getCashoutPercent());
        Assert.assertNull(ticket.getBetCashouts());
        Assert.assertEquals(80, ticket.getCashoutStake().longValue());

        TicketCashoutSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTicketId());
        Assert.assertEquals(ticket.getTicketId(), dto.getTicketId());
        Assert.assertEquals(ticket.getBookmakerId(), dto.getSender().getBookmakerId().intValue());
        Assert.assertEquals(ticket.getCashoutStake(), dto.getCashoutStake());
        Assert.assertNull(dto.getCashoutPercent());
        Assert.assertNull(dto.getBetCashout());
    }

    @Test
    public void cashoutStakePercentBuildTest() {
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCashoutStake(80)
                .setCashoutPercent(1000)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertNotNull(ticket.getCashoutStake());
        Assert.assertNotNull(ticket.getCashoutPercent());
        Assert.assertNull(ticket.getBetCashouts());
        Assert.assertEquals(80, ticket.getCashoutStake().longValue());
        Assert.assertEquals(1000, ticket.getCashoutPercent().intValue());

        TicketCashoutSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTicketId());
        Assert.assertEquals(ticket.getTicketId(), dto.getTicketId());
        Assert.assertEquals(ticket.getBookmakerId(), dto.getSender().getBookmakerId().intValue());
        Assert.assertEquals(ticket.getCashoutStake(), dto.getCashoutStake());
        Assert.assertNotNull(dto.getCashoutPercent());
        Assert.assertEquals(ticket.getCashoutPercent(), dto.getCashoutPercent());
        Assert.assertNull(dto.getBetCashout());
    }

    @Test
    public void cashoutBetCashoutBuildTest() {
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCashout("bet-01", 1000, null)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertNull(ticket.getCashoutStake());
        Assert.assertNull(ticket.getCashoutPercent());
        Assert.assertNotNull(ticket.getBetCashouts());
        Assert.assertEquals(1000, ticket.getBetCashouts().stream().findFirst().get().getCashoutStake());
        Assert.assertEquals(null, ticket.getBetCashouts().stream().findFirst().get().getCashoutPercent());

        TicketCashoutSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTicketId());
        Assert.assertEquals(ticket.getTicketId(), dto.getTicketId());
        Assert.assertEquals(ticket.getBookmakerId(), dto.getSender().getBookmakerId().intValue());
        Assert.assertEquals(ticket.getCashoutStake(), dto.getCashoutStake());
        Assert.assertEquals(ticket.getCashoutPercent(), dto.getCashoutPercent());
        Assert.assertNotNull(dto.getBetCashout());
        Assert.assertEquals(1000, dto.getBetCashout().stream().findFirst().get().getCashoutStake().longValue());
        Assert.assertEquals(null, dto.getBetCashout().stream().findFirst().get().getCashoutPercent());
    }

    @Test
    public void cashoutBetCashoutWithPercentBuildTest() {
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCashout("bet-01", 1000, 3000)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertNull(ticket.getCashoutStake());
        Assert.assertNull(ticket.getCashoutPercent());
        Assert.assertNotNull(ticket.getBetCashouts());
        Assert.assertEquals(1000, ticket.getBetCashouts().stream().findFirst().get().getCashoutStake());
        Assert.assertEquals(3000, ticket.getBetCashouts().stream().findFirst().get().getCashoutPercent().intValue());

        TicketCashoutSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTicketId());
        Assert.assertEquals(ticket.getTicketId(), dto.getTicketId());
        Assert.assertEquals(ticket.getBookmakerId(), dto.getSender().getBookmakerId().intValue());
        Assert.assertEquals(ticket.getCashoutStake(), dto.getCashoutStake());
        Assert.assertEquals(ticket.getCashoutPercent(), dto.getCashoutPercent());
        Assert.assertNotNull(dto.getBetCashout());
        Assert.assertEquals(1000, dto.getBetCashout().stream().findFirst().get().getCashoutStake().longValue());
        Assert.assertEquals(3000, dto.getBetCashout().stream().findFirst().get().getCashoutPercent().intValue());
    }

    @Test
    public void cashoutTicketIdNullFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId(null)
                .setBookmakerId(1111)
                .setCashoutStake(80)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutWrongStakeBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCashoutStake(-80)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutStakeLowBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCashoutStake(0)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutPercentLowBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCashoutBuilder().setCashoutPercent(0);
    }

    @Test
    public void cashoutPercentHighBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCashoutBuilder().setCashoutPercent(1000001);
    }

    @Test
    public void cashoutMissingStakeOrPercentBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutOnlyPercentBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCashoutPercent(239)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutStakeAndBetCashoutBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCashoutStake(239)
                .addBetCashout("bet-id:01", 2939, null)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutPercentAndBetCashoutBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCashoutPercent(239)
                .addBetCashout("bet-id:01", 2939, null)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutBetCashoutMissingStakeOrPercentBuildTest(){
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCashout("bet-id:01", 1230, null)
                .addBetCashout("bet-id:02", 234324, null)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutBetCashoutRepeatBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCashout("bet-id:01", 10101, null)
                .addBetCashout("bet-id:02", 87866, null)
                .addBetCashout("bet-id:01", 42342, null)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void betCashoutTest(){
        long value = 10101;
        BetCashout betCashout = new BetCashoutImpl("bet-id:01", value, null);
        Assert.assertNotNull(betCashout);
        Assert.assertEquals(value, betCashout.getCashoutStake());
        Assert.assertNull(betCashout.getCashoutPercent());
    }

    @Test
    public void betCashoutWrong01Test(){
        thrown.expect(IllegalArgumentException.class);
        BetCashout betCashout = new BetCashoutImpl("bet-id:01", 0, null);
    }

    @Test
    public void betCashoutWrong02Test(){
        thrown.expect(IllegalArgumentException.class);
        int value = 10101;
        BetCashout betCashout = new BetCashoutImpl("bet-id:01", 0, value);
    }

    @Test
    public void betCashoutWrong03Test(){
        thrown.expect(IllegalArgumentException.class);
        long value = 10101;
        BetCashout betCashout = new BetCashoutImpl("", value, null);
    }

    @Test
    public void betCashoutWrong04Test(){
        thrown.expect(IllegalArgumentException.class);
        long value = -10101;
        BetCashout betCashout = new BetCashoutImpl("bet-id:01", value, null);
    }

    @Test
    public void cashoutTicketIdCorruptedFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-***123456")
                .setBookmakerId(1111)
                .setCashoutStake(80)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutInvalidBookmakerIdFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(-1)
                .setCashoutStake(80)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutInvalidCashoutStakeFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(1111)
                .setCashoutStake(-1)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutTicketResponseToDtoMappingTest(){
        String ticketId = "ticket-" + StaticRandom.S1000;
        Result.Status status = Result.Status.ACCEPTED;
        Reason reason = new Reason(500, "Invalid request");
        Result result = new Result(ticketId, status, reason);
        TicketCashoutResponseSchema responseSchema = new TicketCashoutResponseSchema(result, "someSignature", SdkInfo.mtsTicketVersion());
        TicketCashoutResponse cashoutResponse = MtsDtoMapper.map(responseSchema, StaticRandom.S1000, null,"raw message");
        Assert.assertNotNull(cashoutResponse);
        Assert.assertFalse(StringUtils.isNullOrEmpty(cashoutResponse.getCorrelationId()));
    }

    @Test
    public void cashoutWrongCombination01Test(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(1111)
                .setCashoutStake(123)
                .addBetCashout("bet01", 101, null)
                .build();
        Assert.assertNotNull(ticket);
    }

    @Test
    public void cashoutWrongCombination02Test(){
        thrown.expect(IllegalArgumentException.class);
        TicketCashout ticket = builderFactory.createTicketCashoutBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(1111)
                .setCashoutPercent(123)
                .addBetCashout("bet01", 101, null)
                .build();
        Assert.assertNotNull(ticket);
    }
}
