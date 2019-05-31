/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.interfaces.TicketCancelAckResponseListener;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelAckSender;

public interface TicketCancelAckHandler extends TicketCancelAckSender {

    void setListener(TicketCancelAckResponseListener responseListener);
}
