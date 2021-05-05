/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.example.listeners;

import com.sportradar.mts.sdk.api.SdkTicket;
import com.sportradar.mts.sdk.api.interfaces.PublishResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PublishResultHandler<T extends SdkTicket> implements PublishResultListener<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void publishFailure(T t) {
        logger.error("publish failed! we should check if we want to republish msg. message : {}", t);
    }

    public void publishSuccess(T t) { logger.info("publish succeeded"); }
}
