/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

public enum MessageStatus {
    CONSUMED_SUCCESSFULLY,
    RETRY_FOREVER,
    RETRY_LIMITED
}
