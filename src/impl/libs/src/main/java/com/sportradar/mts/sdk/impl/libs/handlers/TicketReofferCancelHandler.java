/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.interfaces.TicketReofferCancelResponseListener;
import com.sportradar.mts.sdk.api.interfaces.TicketReofferCancelSender;

public interface TicketReofferCancelHandler extends TicketReofferCancelSender {

    void setListener(TicketReofferCancelResponseListener responseListener);
}
