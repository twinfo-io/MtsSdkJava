/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.sportradar.mts.sdk.api.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AmqpSendResultHandlerImpl implements AmqpSendResultHandler {

    private static final Logger logger = LoggerFactory.getLogger(AmqpSendResultHandlerImpl.class);
    private final Object stateLock = new Object();
    private boolean opened;
    private boolean closed;
    private final List<AmqpSendResult> sendResults;
    private AmqpPublishResultListener amqpSendResultListener;
    private final Thread runThread;

    public AmqpSendResultHandlerImpl(String name) {
        sendResults = new ArrayList<>();
        runThread = new Thread(this::run, StringUtils.format("{}-send-result-handler-run-thread", name));
    }

    @Override
    public void handleSendResult(AmqpSendResult sendResult) {
        synchronized (this) {
            this.sendResults.add(sendResult);
            this.notifyAll();
        }
    }

    @Override
    public void setPublishResultListener(AmqpPublishResultListener listener) {
        amqpSendResultListener = listener;
    }

    @Override
    public void open() {
        synchronized (stateLock) {
            if (!opened) {
                runThread.start();
                opened = true;
            }
        }
    }

    @Override
    public void close() {
        synchronized (stateLock) {
            if (opened) {
                closed = true;
                runThread.interrupt();
                opened = false;
            }
        }
    }

    @Override
    public boolean isOpen() {
        return opened;
    }

    private void run() {
        while (!closed && !Thread.interrupted()) {
            synchronized (this) {
                if (sendResults.isEmpty()) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        if (!closed) {
                            logger.error("interrupted while waiting for messages", e);
                        }
                        break;
                    }
                }
            }
            if (!handleCompletedMessages()) {
                try {
                    // wait for message put
                    synchronized (this) {
                        if (sendResults.isEmpty()) {
                            this.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    if (!closed) {
                        logger.error("interrupted while sleeping");
                    }
                    break;
                }
            }
        }

        Thread.currentThread().interrupt();
        boolean moreMessages;
        do {
            handleCompletedMessages();
            synchronized (this) {
                moreMessages = !sendResults.isEmpty();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                if (!closed) {
                    logger.error("interrupted while sleeping");
                }
                Thread.currentThread().interrupt();
            }
        } while (moreMessages);
        logger.info("closed");
    }

    private boolean handleCompletedMessages() {
        synchronized (this) {
            List<AmqpSendResult> completed = AmqpSendResultHelper.filterCompleted(sendResults);
            if (completed == null || completed.isEmpty()) {
                return sendResults.isEmpty();
            }
            for (AmqpSendResult result : completed) {
                String correlationId = result.getCorrelationId();
                try {
                    result.get();
                    if (result.isRejected()) {
                        logger.warn("publishing of {} was rejected", correlationId);
                        amqpSendResultListener.publishFailure(correlationId);
                    } else {
                        logger.info("successfully published: {}", correlationId);
                        amqpSendResultListener.publishSuccess(correlationId);
                    }
                } catch (InterruptedException e) {
                    logger.error("interrupted while getting sendBlocking result for {}",
                                 correlationId,
                                 e);
                    return false;
                } catch (ExecutionException e) {
                    logger.error("exception while getting sendBlocking result for {}",
                                 correlationId,
                                 e);
                    return false;
                } catch (Exception e) {
                    logger.error("uncaught exception while processing completed sendBlocking result", e);
                }
            }
            return true;
        }
    }
}
