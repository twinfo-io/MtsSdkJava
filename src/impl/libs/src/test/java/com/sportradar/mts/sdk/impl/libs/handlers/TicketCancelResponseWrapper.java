/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.handlers;

import com.sportradar.mts.sdk.api.enums.TicketCancelAcceptance;
import com.sportradar.mts.sdk.api.impl.TicketCancelResponseImpl;
import com.sportradar.mts.sdk.api.utils.StaticRandom;

import java.util.Date;

/**
 * @author andrej.resnik on 16/06/16 at 13:08
 */
public class TicketCancelResponseWrapper extends TicketCancelResponseImpl {

    public TicketCancelResponseWrapper() {
        super(null, null, TicketCancelAcceptance.Cancelled, "signature", new Date(), "2.0", StaticRandom.S1000, null, "{response-payload}");
    }
}
