/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.sportradar.mts.sdk.api.utils.StringUtils;
import com.sportradar.mts.sdk.impl.libs.LoggerTestAppender;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author andrej.resnik on 09/06/16 at 15:04
 */
public class AmqpSendResultHandlerImplTest extends TimeLimitedTestBase {

    @Mock
    private AmqpSendResult result;

    private AmqpSendResultHandler resender;
    private AmqpProducer sender;
    private String correlationId;
    private String routingKey;
    private LoggerTestAppender appender;
    private Semaphore semaphore;

    @Before
    public void setUp() {
        result = mock(AmqpSendResult.class);
        resender = new AmqpSendResultHandlerImpl("name");
        sender = mock(AmqpProducer.class);
        correlationId = "correlationId";
        routingKey = "routingKey";
        semaphore = new Semaphore(1);
    }

    @Test
    public void waitAndResendIfFailed_SuccessfullySendTest() throws InterruptedException {
        setAmqpSendResult();
        when(result.isRejected()).thenReturn(false);
        when(result.isDone()).thenReturn(true);

        setAmqpProducerSendAsync();

        appender = new LoggerTestAppender(AmqpSendResultHandlerImpl.class, loggingEvent -> {
            if (loggingEvent.getFormattedMessage().contains(StringUtils.format("successfully published: {}", result.getCorrelationId()))) {
                semaphore.release();
            }
        });

        resender.open();
        acquireAndProcess();

        assertTrue(isAcquired());
    }


    @Test
    public void waitAndResendIfFailed_OnExecutionExceptionTest() throws InterruptedException, ExecutionException {
        setAmqpSendResult();
        when(result.isRejected()).thenReturn(false);
        when(result.isDone()).thenReturn(true);
        when(result.get()).thenThrow(ExecutionException.class);

        setAmqpProducerSendAsync();

        appender = new LoggerTestAppender(AmqpSendResultHandlerImpl.class, loggingEvent -> {
            if (loggingEvent.getFormattedMessage().contains(StringUtils.format("exception while getting sendBlocking result for {}", result.getCorrelationId()))) {
                semaphore.release();
            }
        });

        resender.open();
        acquireAndProcess();

        assertTrue(isAcquired());
    }

    @Test
    public void waitAndResendIfFailed_OnInterruptedExceptionTest() throws InterruptedException, ExecutionException {
        setAmqpSendResult();
        when(result.isRejected()).thenReturn(false);
        when(result.isDone()).thenReturn(true);
        when(result.get()).thenThrow(InterruptedException.class);

        setAmqpProducerSendAsync();

        appender = new LoggerTestAppender(AmqpSendResultHandlerImpl.class, loggingEvent -> {
            if (loggingEvent.getFormattedMessage().contains(StringUtils.format("interrupted while getting sendBlocking result for {}", result.getCorrelationId()))) {
                semaphore.release();
            }
        });

        resender.open();
        acquireAndProcess();

        assertTrue(isAcquired());
    }

    @Test
    public void waitAndResendIfFailed_OnCloseTest() throws InterruptedException {
        setAmqpSendResult();
        when(result.isRejected()).thenReturn(false);
        when(result.isDone()).thenReturn(false);

        appender = new LoggerTestAppender(AmqpSendResultHandlerImpl.class, loggingEvent -> {
            if (loggingEvent.getFormattedMessage().contains("closed")) {
                semaphore.release();
            }
        });

        resender.open();
        acquireAndProcess();
        resender.close();

        assertFalse(resender.isOpen());

        when(result.isDone()).thenReturn(true);
        assertTrue(isAcquired());
    }

    @Test
    public void openTest() {
        resender.open();

        assertThat(resender.isOpen(), is(true));
    }

    @Test
    public void closeTest() {
        resender.open();

        resender.close();
        assertThat(resender.isOpen(), is(false));
    }

    private void setAmqpSendResult() {
        when(result.getCorrelationId()).thenReturn(correlationId);
        when(result.getContent()).thenReturn(new byte[] {1, 2, 3, 4, 5});
        when(result.getRoutingKey()).thenReturn(routingKey);
        when(result.getMessageHeaders()).thenReturn(new HashMap<>());
        when(result.getMqProducer()).thenReturn(sender);
    }

    private void setAmqpProducerSendAsync() {
        when(sender.sendAsync(
                result.getCorrelationId(),
                result.getContent(),
                result.getRoutingKey(),
                result.getMessageHeaders())).thenReturn(result);
    }

    private void acquireAndProcess() throws InterruptedException {
        semaphore.tryAcquire(500, TimeUnit.MILLISECONDS);
        resender.handleSendResult(result);
    }

    private boolean isAcquired() throws InterruptedException {
        return semaphore.tryAcquire(500, TimeUnit.MILLISECONDS);
    }
}
