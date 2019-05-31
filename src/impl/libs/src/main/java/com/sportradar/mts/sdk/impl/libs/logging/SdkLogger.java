/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.logging;

import com.sportradar.mts.sdk.api.interfaces.Openable;

public interface SdkLogger extends Openable {

    void logSendMessage(String value);

    void logReceivedMessage(String value);
}
