/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.interfaces.ConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class ChannelFactoryProviderImpl implements ChannelFactoryProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelFactoryProviderImpl.class);

    private final int mqWorkerThreadCount;
    private final Object factoriesLock = new Object();
    private final Map<AmqpCluster, ChannelFactory> factories = new HashMap<>();
    private final ThreadFactory amqpThreadFactory = new AmqpThreadFactory();

    private final Object executorServiceLock = new Object();
    private volatile ExecutorService executorService;
    private int executorRegistrationCount = 0;
    private boolean opened;
    private ConnectionStatus connectionStatus;

    public ChannelFactoryProviderImpl(int mqWorkerThreadCount, ConnectionStatus connectionStatus) {

        this.mqWorkerThreadCount = mqWorkerThreadCount;
        this.connectionStatus = connectionStatus;
    }

    @Override
    public void execute(final Runnable command) {
        final ExecutorService result = this.getExecutorService();
        result.execute(command);
    }

    @Override
    public void registerInstance() {
        synchronized (this.executorServiceLock) {
            if (this.executorRegistrationCount == 0) {
                openExecutorService();
            }
            this.executorRegistrationCount++;
        }
    }

    @Override
    public void unregisterInstance() {
        synchronized (this.executorServiceLock) {
            if (this.executorRegistrationCount > 0) {
                this.executorRegistrationCount--;
            }
            if (this.executorRegistrationCount == 0) {
                closeExecutorService();
            }
        }
    }

    @Override
    public ChannelFactory getChannelFactory(final AmqpCluster mqCluster) {
        Preconditions.checkNotNull(mqCluster, "rabbitMqCluster");
        try {
            synchronized (this.factoriesLock) {
                if (!this.factories.containsKey(mqCluster)) {
                    this.factories.put(mqCluster, new ChannelFactory(mqCluster, this, connectionStatus));
                }
                return this.factories.get(mqCluster);
            }
        } catch (Exception exc) {
            logger.error("Get channel factory error: ", exc);
            throw new RuntimeException(exc);
        }
    }

    public boolean isExecutorServiceOpened() {
        return opened;
    }

    ThreadFactory getAmqpThreadFactory() {
        return this.amqpThreadFactory;
    }

    ExecutorService getExecutorService() {
        final ExecutorService result = this.executorService;
        if (result == null) {
            throw new RuntimeException("Pool 'executorService' is null");
        }
        return result;
    }

    private void openExecutorService() {
        if (this.executorService == null) {
            this.executorService = Executors.newFixedThreadPool(this.mqWorkerThreadCount, amqpThreadFactory);
        }
        opened = true;
    }

    private void closeExecutorService() {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
            this.executorService = null;
        }
        opened = false;
    }

    private static final class AmqpThreadFactory implements ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public AmqpThreadFactory() {
            final SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "AMQP-client-thread-";
        }

        public Thread newThread(Runnable r) {
            final Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
            if (!t.isDaemon()) {
                t.setDaemon(true);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
