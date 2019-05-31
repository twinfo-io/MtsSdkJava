/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.interfaces.TicketAckResponseListener;
import com.sportradar.mts.sdk.api.interfaces.TicketAckSender;

public interface TicketAckHandler extends TicketAckSender {

    void setListener(TicketAckResponseListener responseListener);
}
