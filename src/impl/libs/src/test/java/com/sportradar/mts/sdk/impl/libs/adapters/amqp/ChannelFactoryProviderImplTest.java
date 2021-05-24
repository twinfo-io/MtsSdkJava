/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.sportradar.mts.sdk.api.impl.ConnectionStatusImpl;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.*;

/**
 * @author andrej.resnik on 23/06/16 at 12:25
 */
public class ChannelFactoryProviderImplTest extends TimeLimitedTestBase {

    private AmqpCluster amqpCluster;
    private int mqWorkerThreadCount;
    private ChannelFactoryProvider channelFactoryProvider;
    private boolean executed;

    @Before
    public void setUp(){
        int mqWorkerThreadCount = 1;
        channelFactoryProvider = new ChannelFactoryProviderImpl(mqWorkerThreadCount, new ConnectionStatusImpl());
    }

    @Test
    public void execute_SuccessTest() throws InterruptedException {
        channelFactoryProvider.registerInstance();
        Semaphore semaphore = new Semaphore(0);
        String msg = "channel factory provider successfully executed a runnable";
        execute(semaphore, msg);

        assertTrue(semaphore.tryAcquire(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void execute_FailTest() throws InterruptedException {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Pool 'executorService' is null");

        channelFactoryProvider.unregisterInstance();
        Semaphore semaphore = new Semaphore(0);
        execute(semaphore, "this msg is redundant");
    }

    @Test
    public void registerInstance() {
        channelFactoryProvider.registerInstance();
        assertIsExecutorServiceOpened(true);
    }

    @Test
    public void unregisterInstance() {
        channelFactoryProvider.unregisterInstance();
        assertIsExecutorServiceOpened(false);
    }

    @Test
    public void getChannelFactory_OkTest() {
        amqpCluster = AmqpCluster.from(
                "testUsername",
                "testPassword",
                "testVirtualHost",
                false,
                new NetworkAddress("127.0.0.1", 8080),
                10);

        ChannelFactory channelFactory = channelFactoryProvider.getChannelFactory(amqpCluster);

        assertNotNull(channelFactory);
    }

    @Test
    public void getChannelFactory_NullTest() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("rabbitMqCluster");

        ChannelFactory channelFactory = channelFactoryProvider.getChannelFactory(amqpCluster);
    }

    private synchronized void execute(Semaphore semaphore, String msg) {
        checkNotNull(semaphore, "semaphore cannot be a null reference");

        channelFactoryProvider.execute(() -> {
            System.out.println(msg);
            semaphore.release();
        });
    }

    private void assertIsExecutorServiceOpened(boolean expected) {
        if (channelFactoryProvider.isExecutorServiceOpened()) {
            System.out.println("executor service opened");
            assertTrue(expected);
        } else {
            System.out.println("executor service not opened");
            assertFalse(expected);
        }
    }
}
