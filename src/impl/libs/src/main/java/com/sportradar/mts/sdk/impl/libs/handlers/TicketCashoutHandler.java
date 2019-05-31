/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.interfaces.TicketCashoutResponseListener;
import com.sportradar.mts.sdk.api.interfaces.TicketCashoutSender;
import com.sportradar.mts.sdk.impl.libs.receivers.TicketCashoutResponseReceiver;

/**
 * Handler for the messages of type {@link com.sportradar.mts.sdk.api.TicketCashout}
 */
public interface TicketCashoutHandler extends TicketCashoutSender, TicketCashoutResponseReceiver {

    /**
     * The listener that will be called when a message is sent
     *
     * @param responseListener - the listener that will be called when a message is sent
     */
    void setListener(TicketCashoutResponseListener responseListener);
}
