/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.TicketCancelResponse;
import com.sportradar.mts.sdk.api.interfaces.Openable;

public interface TicketCancelResponseReceiver extends Openable {

    void ticketCancelResponseReceived(TicketCancelResponse ticketCancelResponse);

}
