/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SelectionBuilderTest extends TimeLimitedTestBase {

    private TicketBuilderHelper ticketBuilderHelper;

    @Before
    public void Init()
    {
        ticketBuilderHelper = new TicketBuilderHelper(null);
    }

    @Test
    public void BuildSelectionTest()
    {
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder()
                .setEventId("sr:match:12345")
                .setId("live:2/0/*/1")
                .setOdds(10400)
                .setBanker(false)
                .build();
        Assert.assertNotNull(selection);
        Assert.assertEquals(selection.getEventId(), "sr:match:12345");
        Assert.assertEquals(selection.getId(), "live:2/0/*/1");
        Assert.assertEquals(selection.getOdds(), (Integer) 10400);
        Assert.assertEquals(selection.getIsBanker(), false);
    }

    @Test
    public void BuildSelectionTooSmallOddsTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Odds not valid.");
        ticketBuilderHelper.builderFactory.createSelectionBuilder().setOdds(1400);
    }

    @Test
    public void BuildSelectionTooBigOddsTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Odds not valid.");
        ticketBuilderHelper.builderFactory.createSelectionBuilder().setOdds(1400000000);
    }

    @Test
    public void BuildSelectionBankerTrueTest()
    {
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder()
                .setEventId("sr:match:12345")
                .setId("live:2/0/*/1")
                .setOdds(10400)
                .setBanker(true)
                .build();
        Assert.assertNotNull(selection);
        Assert.assertEquals(selection.getEventId(), "sr:match:12345");
        Assert.assertEquals(selection.getId(), "live:2/0/*/1");
        Assert.assertEquals(selection.getOdds(), (Integer) 10400);
        Assert.assertEquals(selection.getIsBanker(), true);
    }

    @Test
    public void BuildSelectionCustomEventIdTest()
    {
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder()
                .setEventId("TR:0192_234_934")
                .setId("live:2/0/*/1")
                .setOdds(10400)
                .setBanker(true)
                .build();
        Assert.assertNotNull(selection);
        Assert.assertEquals(selection.getEventId(), "TR:0192_234_934");
        Assert.assertEquals(selection.getId(), "live:2/0/*/1");
        Assert.assertEquals(selection.getOdds(), (Integer) 10400);
        Assert.assertEquals(selection.getIsBanker(), true);
    }

    @Test
    public void BuildSelectionCustomSelectionIdTest()
    {
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder()
                .setEventId("TR:0192_234_934")
                .setId("cust:2/0/*/1")
                .setOdds(10400)
                .setBanker(true)
                .build();
        Assert.assertNotNull(selection);
        Assert.assertEquals(selection.getEventId(), "TR:0192_234_934");
        Assert.assertEquals(selection.getId(), "cust:2/0/*/1");
        Assert.assertEquals(selection.getOdds(), (Integer) 10400);
        Assert.assertEquals(selection.getIsBanker(), true);
    }

    @Test
    public void BuildSelectionCustomWithNoOddsTest()
    {
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder(true)
                .setEventId("TR:0192_234_934")
                .setId("tree:2/0/*/1")
                .setBanker(true)
                .build();
        Assert.assertNotNull(selection);
        Assert.assertEquals(selection.getEventId(), "TR:0192_234_934");
        Assert.assertEquals(selection.getId(), "tree:2/0/*/1");
        Assert.assertNull(selection.getOdds());
        Assert.assertEquals(selection.getIsBanker(), true);
    }

    @Test
    public void BuildSelectionSetWithNoOddsTest()
    {
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder(true)
                .set("TR:0192_234_934", "tree:2/0/*/1", null, true)
                .build();
        Assert.assertNotNull(selection);
        Assert.assertEquals(selection.getEventId(), "TR:0192_234_934");
        Assert.assertEquals(selection.getId(), "tree:2/0/*/1");
        Assert.assertNull(selection.getOdds());
        Assert.assertEquals(selection.getIsBanker(), true);
    }

    @Test
    public void BuildSelectionSetWithNegativeOddsTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("selection odds too low");
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder(true)
                .set("TR:0192_234_934", "tree:2/0/*/1", -1, true)
                .build();
    }

    @Test
    public void BuildSelectionCustomWithNegativeOddsTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("selection odds too low");
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder(true)
                .setEventId("TR:0192_234_934")
                .setId("tree:2/0/*/1")
                .setBanker(true)
                .setOdds(-1)
                .build();
    }
}
