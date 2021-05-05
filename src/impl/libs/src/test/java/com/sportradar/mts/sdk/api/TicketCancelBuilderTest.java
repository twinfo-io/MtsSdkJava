/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.enums.TicketCancellationReason;
import com.sportradar.mts.sdk.api.impl.BetCancelImpl;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.TicketCancelSchema;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.StaticRandom;
import com.sportradar.mts.sdk.impl.libs.SdkHelper;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Assert;
import org.junit.Test;

public class TicketCancelBuilderTest extends TimeLimitedTestBase {

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
    public void cancelPercentBuildTest() {
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCode(TicketCancellationReason.BookmakerBackofficeTriggered)
                .setCancelPercent(80)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertNotNull(ticket.getCancelPercent());
        Assert.assertNull(ticket.getBetCancels());
        Assert.assertEquals(80, ticket.getCancelPercent().intValue());

        TicketCancelSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCancel().getTicketId());
        Assert.assertEquals(ticket.getTicketId(), dto.getCancel().getTicketId());
        Assert.assertEquals(ticket.getBookmakerId(), dto.getCancel().getSender().getBookmakerId().intValue());
        Assert.assertNotNull(dto.getCancel().getCancelPercent());
        Assert.assertEquals(ticket.getCancelPercent(), dto.getCancel().getCancelPercent());
        Assert.assertNull(dto.getCancel().getBetCancel());
    }

    @Test
    public void cancelBetCancelNoPercentBuildTest() {
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCode(TicketCancellationReason.BookmakerBackofficeTriggered)
                .addBetCancel("bet-01", null)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertNull(ticket.getCancelPercent());
        Assert.assertNotNull(ticket.getBetCancels());

        TicketCancelSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCancel().getTicketId());
        Assert.assertEquals(ticket.getTicketId(), dto.getCancel().getTicketId());
        Assert.assertEquals(ticket.getBookmakerId(), dto.getCancel().getSender().getBookmakerId().intValue());
        Assert.assertNull(dto.getCancel().getCancelPercent());
        Assert.assertEquals(ticket.getCancelPercent(), dto.getCancel().getCancelPercent());
        Assert.assertNotNull(dto.getCancel().getBetCancel());
        Assert.assertEquals("bet-01", dto.getCancel().getBetCancel().stream().findFirst().get().getId());
        Assert.assertEquals(null, dto.getCancel().getBetCancel().stream().findFirst().get().getCancelPercent());
    }

    @Test
    public void cancelBetCancelBuildTest() {
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCode(TicketCancellationReason.BookmakerBackofficeTriggered)
                .addBetCancel("bet-01", 1000)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertNull(ticket.getCancelPercent());
        Assert.assertNotNull(ticket.getBetCancels());

        TicketCancelSchema dto = MtsDtoMapper.map(ticket);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCancel().getTicketId());
        Assert.assertEquals(ticket.getTicketId(), dto.getCancel().getTicketId());
        Assert.assertEquals(ticket.getBookmakerId(), dto.getCancel().getSender().getBookmakerId().intValue());
        Assert.assertNull(dto.getCancel().getCancelPercent());
        Assert.assertEquals(ticket.getCancelPercent(), dto.getCancel().getCancelPercent());
        Assert.assertNotNull(dto.getCancel().getBetCancel());
        Assert.assertEquals(1, dto.getCancel().getBetCancel().size());
        Assert.assertEquals("bet-01", dto.getCancel().getBetCancel().get(0).getId());
        Assert.assertEquals(1000, dto.getCancel().getBetCancel().get(0).getCancelPercent().intValue());
    }

    @Test
    public void cancelTicketIdNullFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId(null)
                .setBookmakerId(1111)
                .build();
    }

    @Test
    public void cancelPercentLowBuildTest(){
        builderFactory.createTicketCancelBuilder().setCancelPercent(0);
        Assert.assertNotNull(builderFactory);
    }

    @Test
    public void cancelPercentHighBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder().setCancelPercent(1000001);
    }

    @Test
    public void cancelPercentAndBetCancelBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .setCancelPercent(239)
                .addBetCancel("bet-id:01", 2939)
                .build();
    }

    @Test
    public void cancelBetCancelsBuildTest(){
        TicketCancel ticket = builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCancel("bet-id:01", 1230)
                .addBetCancel("bet-id:02", 234324)
                .build();
        Assert.assertNotNull(ticket);
        Assert.assertEquals(2, ticket.getBetCancels().size());
    }

    @Test
    public void cancelBetCashoutRepeatBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.S1000)
                .setBookmakerId(1111)
                .addBetCancel("bet-id:01", 10101)
                .addBetCancel("bet-id:02", 87866)
                .addBetCancel("bet-id:01", 42342)
                .build();
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
        BetCancel betCancel = new BetCancelImpl("", 110);
    }

    @Test
    public void cancelTicketIdCorruptedFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-***123456")
                .setBookmakerId(1111)
                .build();
    }

    @Test
    public void cancelInvalidBookmakerIdFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(-1)
                .build();
    }

    @Test
    public void cancelInvalidCancelStakeFailBuildTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(1111)
                .setCancelPercent(-1)
                .build();
    }

    @Test
    public void cancelWrongCombinationTest(){
        thrown.expect(IllegalArgumentException.class);
        builderFactory.createTicketCancelBuilder()
                .setTicketId("ticket-" + StaticRandom.I1000P)
                .setBookmakerId(1111)
                .setCancelPercent(123)
                .addBetCancel("bet01", 101)
                .build();
    }
}
