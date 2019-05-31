/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.sportradar.mts.sdk.api.builders.*;

public class SimpleBuilderFactoryImpl implements SimpleBuilderFactory {
    @Override
    public TicketBuilder createTicketBuilder() { return new TicketBuilderImpl(); }

    @Override
    public TicketReofferBuilder createTicketReofferBuilder() {
        return new TicketReofferBuilderImpl();
    }

    @Override
    public EndCustomerBuilder createEndCustomerBuilder() {
        return new EndCustomerBuilderImpl();
    }

    @Override
    public BetBuilder createBetBuilder() {
        return new BetBuilderImpl();
    }
}
