/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import java.util.Map;
import java.util.concurrent.Future;

public interface AmqpSendResult extends Future<Boolean> {

    boolean isRejected();

    byte[] getContent();

    String getRoutingKey();

    String getCorrelationId();

    Map<String, Object> getMessageHeaders();

    AmqpProducer getMqProducer();
}
