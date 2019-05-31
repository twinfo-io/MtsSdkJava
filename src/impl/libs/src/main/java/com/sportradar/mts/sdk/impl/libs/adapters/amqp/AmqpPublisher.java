/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.sportradar.mts.sdk.api.interfaces.Openable;

public interface AmqpPublisher extends Openable {

    void publishAsync(byte[] msg,
                      String correlationId,
                      String routingKey,
                      String replyRoutingKey);

    void setListener(AmqpPublishResultListener listener);
}
