/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;

public class TicketSerializerTest extends TimeLimitedTestBase {

    private TicketBuilderHelper ticketBuilderHelper;

    @Before
    public void Init()
    {
        ticketBuilderHelper = new TicketBuilderHelper(null);
    }

    @Test
    public void SerializeTicketTest() throws IOException {
        Ticket ticket = ticketBuilderHelper.getTicket(null, 0, 0, 0);
        String serialized = JsonUtils.serializeAsString(ticket);
        Ticket deserializedTicket = JsonUtils.deserialize(serialized, ticket.getClass());
        String serializedAgain = JsonUtils.serializeAsString(deserializedTicket);
        Assert.assertThat(serializedAgain, is(serialized));
    }

    @Test
    public void SerializeTicketCancelTest() throws IOException {
        TicketCancel ticket = ticketBuilderHelper.getTicketCancel(null);
        String serialized = JsonUtils.serializeAsString(ticket);
        TicketCancel deserializedTicket = JsonUtils.deserialize(serialized, ticket.getClass());
        String serializedAgain = JsonUtils.serializeAsString(deserializedTicket);
        Assert.assertThat(serializedAgain, is(serialized));
    }

    @Test
    public void SerializeTicketCashoutTest() throws IOException {
        TicketCashout ticket = ticketBuilderHelper.getTicketCashout(null);
        String serialized = JsonUtils.serializeAsString(ticket);
        TicketCashout deserializedTicket = JsonUtils.deserialize(serialized, ticket.getClass());
        String serializedAgain = JsonUtils.serializeAsString(deserializedTicket);
        Assert.assertThat(serializedAgain, is(serialized));
    }

    @Test
    public void SerializeTicketReofferCancelTest() throws IOException {
        TicketReofferCancel ticket = ticketBuilderHelper.getTicketReofferCancel(null);
        String serialized = JsonUtils.serializeAsString(ticket);
        TicketReofferCancel deserializedTicket = JsonUtils.deserialize(serialized, ticket.getClass());
        String serializedAgain = JsonUtils.serializeAsString(deserializedTicket);
        Assert.assertThat(serializedAgain, is(serialized));
    }

    @Test
    public void SerializeTicketNonSrSettleTest() throws IOException {
        TicketNonSrSettle ticket = ticketBuilderHelper.getTicketNonSrSettle(null);
        String serialized = JsonUtils.serializeAsString(ticket);
        TicketNonSrSettle deserializedTicket = JsonUtils.deserialize(serialized, ticket.getClass());
        String serializedAgain = JsonUtils.serializeAsString(deserializedTicket);
        Assert.assertThat(serializedAgain, is(serialized));
    }
}
