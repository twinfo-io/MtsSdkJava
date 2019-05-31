/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.interfaces.TicketNonSrSettleResponseListener;
import com.sportradar.mts.sdk.api.interfaces.TicketNonSrSettleSender;
import com.sportradar.mts.sdk.impl.libs.receivers.TicketNonSrSettleResponseReceiver;

public interface TicketNonSrSettleHandler extends TicketNonSrSettleSender, TicketNonSrSettleResponseReceiver {

    /**
     * The listener that will be called when a message is sent
     *
     * @param responseListener - the listener that will be called when a message is sent
     */
    void setListener(TicketNonSrSettleResponseListener responseListener);

}
