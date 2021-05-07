/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.customBet;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPIMarketsType;
import com.sportradar.mts.sdk.api.interfaces.customBet.AvailableSelections;
import com.sportradar.mts.sdk.api.interfaces.customBet.Market;
import com.sportradar.mts.sdk.api.rest.URN;

import java.util.List;

/**
 * Implements methods used to access available selections for the event
 */
public class AvailableSelectionsImpl implements AvailableSelections {
    /**
     * An {@link URN} specifying the id of the event
     */
    private final URN event;

    /**
     * An {@link List} specifying available markets for the event
     */
    private final List<Market> markets;

    public AvailableSelectionsImpl(CAPIAvailableSelections availableSelections) {
        Preconditions.checkNotNull(availableSelections);

        this.event = URN.parse(availableSelections.getEvent().getId());

        CAPIMarketsType marketList = availableSelections.getEvent().getMarkets();
        this.markets = (marketList != null) ?
                marketList.getMarkets().stream()
                        .map(MarketImpl::new)
                        .collect(ImmutableList.toImmutableList()) :
                ImmutableList.of();
    }

    @Override
    public URN getEvent() {
        return event;
    }

    @Override
    public List<Market> getMarkets() {
        return markets;
    }
}
