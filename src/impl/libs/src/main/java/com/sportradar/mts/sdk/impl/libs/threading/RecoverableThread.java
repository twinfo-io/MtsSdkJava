/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.threading;

import com.sportradar.mts.sdk.api.exceptions.MtsSdkProcessException;
import com.sportradar.mts.sdk.api.interfaces.Openable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class RecoverableThread implements Openable {

    private static final Logger logger = LoggerFactory.getLogger(RecoverableThread.class);
    private static final AtomicLong threadCounter = new AtomicLong(0L);

    private final boolean daemon;
    private final String name;
    private final Runnable runnable;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private final AtomicReference<Thread> actualThread = new AtomicReference<>(null);

    private volatile boolean isRunning = false;

    public RecoverableThread() {
        this(null, true, null);
    }

    public RecoverableThread(Runnable runnable) {
        this(null, true, runnable);
    }

    public RecoverableThread(String name) {
        this(name, true, null);
    }

    public RecoverableThread(String name, Runnable runnable) {
        this(name, true, runnable);
    }

    public RecoverableThread(String name, boolean daemon) {
        this(name, daemon, null);
    }

    public RecoverableThread(String name, boolean daemon, Runnable runnable) {
        this.name = name == null ? "RecoverableThread" : name;
        this.daemon = daemon;
        this.runnable = new DefaultRunnable(this, runnable);
        this.uncaughtExceptionHandler = new NewThreadCreator(this);
    }

    protected void run() {
        if(runnable != null){
            runnable.run();
        }
    }

    @Override
    public boolean isOpen() {
        return this.isRunning;
    }

    @Override
    public synchronized void open()  {
        if (this.isRunning) {
            logger.info("Already open...");
            return;
        }
        this.isRunning = true;
        this.createAndStartActualThread(null);
    }

    @Override
    public void close() {
        try {
            this.join(false);
        } catch (InterruptedException e) {
            logger.warn("close(): caught InterruptedException: ", e);
            Thread.currentThread().interrupt();
        }
    }

    public void closeNow() {
        try {
            this.join(true);
        } catch (InterruptedException e) {
            logger.warn("closeNow(): caught InterruptedException: ", e);
            Thread.currentThread().interrupt();
        }
    }

    private synchronized void join(final boolean interrupt) throws InterruptedException {
        if (!this.isRunning) {
            logger.debug("Skipping join...");
            return;
        }
        this.isRunning = false;
        Thread t = null;
        try {
            t = this.actualThread.get();
            if (t != null) {
                if (interrupt) {
                    logger.debug("Interrupting thread [id: {}, name: {}]: ", t.getId(), t.getName());
                    t.interrupt();
                }
                logger.trace("Joining thread [id: {}, name: {}]: ", t.getId(), t.getName());
                t.join();
            }
        } finally {
            if (t != null) {
                logger.trace("Joined thread [id: {}, name: {}]: ", t.getId(), t.getName());
            }
            this.actualThread.set(null);
        }
    }

    private synchronized void createAndStartActualThread(Thread thread) {
        if (!this.isRunning) {
            logger.debug("Skipping starting new thread...");
            return;
        }
        Thread result = new Thread(this.runnable);
        result.setName(this.name + "-" + threadCounter.incrementAndGet());
        result.setDaemon(this.daemon);
        result.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
        if (this.actualThread.compareAndSet(thread, result)) {
            result.start();
            logger.debug("Started thread [id: {}, name: {}]: ", result.getId(), result.getName());
        }
    }

    private static final class DefaultRunnable implements Runnable {

        private final RecoverableThread parent;
        private final Runnable runnable;

        public DefaultRunnable(RecoverableThread parent, Runnable runnable) {
            this.parent = parent;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                Thread t = Thread.currentThread();

                logger.debug("Start running thread [id: {}, name: {}]: ", t.getId(), t.getName());

                if (this.runnable != null) {
                    this.runnable.run();
                } else {
                    this.parent.run();
                }
                this.parent.actualThread.set(null);
                this.parent.isRunning = false;
            } catch (Exception exc) {
                logger.error("Unexpected exc while executing runnable: " + exc.getMessage(), exc);
                logger.error("Killing thread [id: {}, name: {}]: ", Thread.currentThread().getId(), Thread.currentThread().getName());
                throw new MtsSdkProcessException("Unexpected exc while executing runnable: " + exc.getMessage(), exc);
            }
        }
    }

    private static final class NewThreadCreator implements Thread.UncaughtExceptionHandler {

        private final RecoverableThread parent;

        public NewThreadCreator(RecoverableThread parent) {
            this.parent = parent;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            logger.error("Recoverable thread died: ", throwable);
            if (throwable instanceof Exception) {
                logger.error("Recovering thread: {}", thread.getName());
                this.parent.createAndStartActualThread(thread);
            }
        }
    }
}
