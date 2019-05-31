/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

public interface BuilderFactory extends SimpleBuilderFactory {

    /**
     * Constructs and returns a new instance of the {@link TicketCancelBuilder} class
     * @return TicketCancelBuilder
     */
    TicketCancelBuilder createTicketCancelBuilder();

    /**
     * Constructs and returns a new instance of the {@link TicketReofferCancelBuilder} class
     * @return TicketReofferCancelBuilder
     */
    TicketReofferCancelBuilder createTicketReofferCancelBuilder();

    /**
     * Constructs and returns a new instance of the {@link TicketCashoutBuilder} class
     * @return TicketCashoutBuilder
     */
    TicketCashoutBuilder createTicketCashoutBuilder();

    /**
     * Constructs and returns a new instance of the {@link SenderBuilder} class
     * @return SenderBuilder
     */
    SenderBuilder createSenderBuilder();

    /**
     * Constructs and returns a new instance of the {@link SelectionBuilder} class
     * @return SelectionBuilder
     */
    SelectionBuilder createSelectionBuilder();

    /**
     * Constructs and returns a new instance of the {@link TicketAckBuilder} class
     * @return TicketAckBuilder
     */
    TicketAckBuilder createTicketAckBuilder();

    /**
     * Constructs and returns a new instance of the {@link TicketCancelAckBuilder} class
     * @return TicketCancelAckBuilder
     */
    TicketCancelAckBuilder createTicketCancelAckBuilder();

    /**
     * Constructs and returns a new instance of the {@link TicketNonSrSettleBuilder} class
     * @return TicketNonSrSettleBuilder
     */
    TicketNonSrSettleBuilder createTicketNonSrSettleBuilder();
}
