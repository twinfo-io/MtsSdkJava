/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

public interface AmqpPublishResultListener {

    void publishSuccess(String correlationId);

    void publishFailure(String correlationId);
}
