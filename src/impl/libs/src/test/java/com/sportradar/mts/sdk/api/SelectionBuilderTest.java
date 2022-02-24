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
                .setBoostedOdds(10500)
                .setBanker(false)
                .build();
        Assert.assertNotNull(selection);
        Assert.assertEquals("sr:match:12345", selection.getEventId());
        Assert.assertEquals("live:2/0/*/1", selection.getId());
        Assert.assertEquals((Integer) 10400, selection.getOdds());
        Assert.assertEquals((Integer) 10500, selection.getBoostedOdds());
        Assert.assertEquals(false, selection.getIsBanker());
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
        Assert.assertEquals("sr:match:12345", selection.getEventId());
        Assert.assertEquals("live:2/0/*/1", selection.getId());
        Assert.assertEquals((Integer) 10400, selection.getOdds());
        Assert.assertEquals(true, selection.getIsBanker());
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
        Assert.assertEquals("TR:0192_234_934", selection.getEventId());
        Assert.assertEquals("live:2/0/*/1", selection.getId());
        Assert.assertEquals((Integer) 10400, selection.getOdds());
        Assert.assertEquals(true, selection.getIsBanker());
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
        Assert.assertEquals("TR:0192_234_934", selection.getEventId());
        Assert.assertEquals("cust:2/0/*/1", selection.getId());
        Assert.assertEquals((Integer) 10400, selection.getOdds());
        Assert.assertEquals(true, selection.getIsBanker());
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
        Assert.assertEquals("TR:0192_234_934", selection.getEventId());
        Assert.assertEquals("tree:2/0/*/1", selection.getId());
        Assert.assertNull(selection.getOdds());
        Assert.assertEquals(true, selection.getIsBanker());
    }

    @Test
    public void BuildSelectionSetWithNoOddsTest()
    {
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder(true)
                .set("TR:0192_234_934", "tree:2/0/*/1", null, null, true)
                .build();
        Assert.assertNotNull(selection);
        Assert.assertEquals("TR:0192_234_934", selection.getEventId());
        Assert.assertEquals("tree:2/0/*/1", selection.getId());
        Assert.assertNull(selection.getOdds());
        Assert.assertEquals(true, selection.getIsBanker());
    }

    @Test
    public void BuildSelectionSetWithNegativeOddsTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("odds too low");
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder(true)
                .set("TR:0192_234_934", "tree:2/0/*/1", -1, null, true)
                .build();
    }

    @Test
    public void BuildSelectionSetWithToHighOddsTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("odds too high");
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder(true)
                .set("TR:0192_234_934", "tree:2/0/*/1", 1100000000, null, true)
                .build();
    }

    @Test
    public void BuildSelectionCustomWithNegativeOddsTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("odds too low");
        Selection selection = ticketBuilderHelper.builderFactory.createSelectionBuilder(true)
                .setEventId("TR:0192_234_934")
                .setId("tree:2/0/*/1")
                .setBanker(true)
                .setOdds(-1)
                .build();
    }
}
