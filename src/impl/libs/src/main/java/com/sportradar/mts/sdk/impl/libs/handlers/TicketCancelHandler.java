/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.interfaces.TicketCancelResponseListener;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
import com.sportradar.mts.sdk.impl.libs.receivers.TicketCancelResponseReceiver;

public interface TicketCancelHandler extends TicketCancelSender, TicketCancelResponseReceiver {

    void setListener(TicketCancelResponseListener responseListener);
}
