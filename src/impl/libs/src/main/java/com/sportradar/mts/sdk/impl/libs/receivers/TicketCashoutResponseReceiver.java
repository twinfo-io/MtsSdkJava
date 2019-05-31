/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.TicketCashoutResponse;
import com.sportradar.mts.sdk.api.interfaces.Openable;

/**
 * The instance that handles cashout responses
 */
public interface TicketCashoutResponseReceiver extends Openable {

    /**
     * An event that is invoked when the {@link TicketCashoutResponse} message is received
     *
     * @param ticketCashoutResponse - the {@link TicketCashoutResponse} message data
     */
    void ticketCashoutResponseReceived(TicketCashoutResponse ticketCashoutResponse);
}
