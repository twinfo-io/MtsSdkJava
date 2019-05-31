/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

public interface SimpleBuilderFactory {

    /**
     * Constructs and returns a new instance of the {@link TicketBuilder} class
     * @return TicketBuilder
     */
    TicketBuilder createTicketBuilder();

    /**
     * Constructs and returns a new instance of the {@link TicketReofferBuilder} class
     * @return TicketReofferBuilder
     */
    TicketReofferBuilder createTicketReofferBuilder();

    /**
     * Constructs and returns a new instance of the {@link EndCustomerBuilder} class
     * @return EndCustomerBuilder
     */
    EndCustomerBuilder createEndCustomerBuilder();

    /**
     * Constructs and returns a new instance of the {@link BetBuilder} class
     * @return BetBuilder
     */
    BetBuilder createBetBuilder();
}
