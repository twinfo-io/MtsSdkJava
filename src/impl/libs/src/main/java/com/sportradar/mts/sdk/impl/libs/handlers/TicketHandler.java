/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.interfaces.TicketResponseListener;
import com.sportradar.mts.sdk.api.interfaces.TicketSender;
import com.sportradar.mts.sdk.impl.libs.receivers.TicketResponseReceiver;

public interface TicketHandler extends TicketSender, TicketResponseReceiver {

    void setListener(TicketResponseListener responseListener);
}
