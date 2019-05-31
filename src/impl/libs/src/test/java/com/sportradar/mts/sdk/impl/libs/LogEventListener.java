/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author andrej.resnik on 15/06/16 at 11:57
 */
public interface LogEventListener {

    void logEventReceived(ILoggingEvent loggingEvent);
}
