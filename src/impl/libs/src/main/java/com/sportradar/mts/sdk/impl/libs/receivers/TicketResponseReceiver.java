/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.TicketResponse;
import com.sportradar.mts.sdk.api.interfaces.Openable;

public interface TicketResponseReceiver extends Openable {

    void ticketResponseReceived(TicketResponse ticketResponse);
}
