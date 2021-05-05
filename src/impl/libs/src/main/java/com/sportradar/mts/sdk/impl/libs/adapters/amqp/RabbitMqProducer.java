/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.*;

public final class RabbitMqProducer extends RabbitMqBase implements AmqpProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqProducer.class);

    private final BlockingQueue<AcceptedMessage> normalQueue = new LinkedTransferQueue<>();
    private final BlockingQueue<AcceptedMessage> redeliveryQueue = new LinkedTransferQueue<>();
    private final int maxRetryCount;
    private final int maxBufferSize;
    private final AMQP.BasicProperties msgProperties;
    private final boolean waitForPublishConfirmations;
    private final boolean isPublishMandatory;
    private ReturnListener returnListener;

    public RabbitMqProducer(ChannelFactoryProvider channelFactoryProvider,
                            String instanceName,
                            AmqpCluster mqCluster,
                            String exchangeName,
                            ExchangeType exchangeType,
                            int maxRetryCount,
                            int maxBufferSize,
                            int concurrencyLevel,
                            boolean msgMemOnly,
                            boolean waitForPublishConfirmations,
                            boolean mandatory) {
        super(channelFactoryProvider,
                instanceName,
                mqCluster,
                exchangeName,
                exchangeType,
                concurrencyLevel);

        checkArgument(maxRetryCount > 0, "parameter 'maxRetryCount' is zero or less");
        checkArgument(maxBufferSize > 0, "parameter 'maxBufferSize' is zero or less");

        this.maxRetryCount = maxRetryCount;
        this.maxBufferSize = maxBufferSize;
        this.msgProperties = msgMemOnly ? MessageProperties.BASIC : MessageProperties.PERSISTENT_BASIC;
        this.waitForPublishConfirmations = waitForPublishConfirmations;
        this.isPublishMandatory = mandatory;
    }


    @Override
    public AmqpSendResult sendAsync(String correlationId,
                                    byte[] msg,
                                    String routingKey,
                                    Map<String, Object> messageHeaders) {
        return this.sendAsyncInternal(correlationId, msg, routingKey, messageHeaders, null);
    }

    @Override
    public boolean sendAsync(String correlationId, byte[] msg, Consumer<AmqpSendResult> doneCallback) {
        final AmqpSendResult result = this.sendAsyncInternal(correlationId,
                msg,
                DEFAULT_ROUTING_KEY,
                new HashMap<>(),
                doneCallback);
        return (!result.isRejected());
    }

    @Override
    public boolean sendAsync(String correlationId,
                             byte[] msg,
                             Map<String, Object> messageHeaders,
                             Consumer<AmqpSendResult> doneCallback) {
        final AmqpSendResult result = this.sendAsyncInternal(correlationId,
                msg,
                DEFAULT_ROUTING_KEY,
                messageHeaders,
                doneCallback);
        return (!result.isRejected());
    }

    @Override
    public boolean sendAsync(String correlationId,
                             byte[] msg,
                             String routingKey,
                             Consumer<AmqpSendResult> doneCallback) {
        final AmqpSendResult result = this.sendAsyncInternal(correlationId,
                msg,
                routingKey,
                new HashMap<>(),
                doneCallback);
        return (!result.isRejected());
    }

    @Override
    public boolean sendAsync(String correlationId,
                             byte[] msg,
                             String routingKey,
                             Map<String, Object> messageHeaders,
                             Consumer<AmqpSendResult> doneCallback) {
        final AmqpSendResult result = this.sendAsyncInternal(correlationId,
                msg,
                routingKey,
                messageHeaders,
                doneCallback);
        return (!result.isRejected());
    }


    @Override
    public boolean send(String correlationId, byte[] msg, String routingKey, Map<String, Object> messageHeaders) {
        try {
            return this.sendAsyncInternal(correlationId, msg, routingKey, messageHeaders, null).get();
        } catch (Exception exc) {
            logger.error("error in sending data", exc);
            return false;
        }
    }

    @Override
    public void setReturnListener(ReturnListener returnListener) {
        this.returnListener = returnListener;
    }

    @Override
    protected void doWork(Channel channel, int threadId) throws
            InterruptedException,
            IOException {
        if (this.waitForPublishConfirmations) {
            this.sendAndWaitForConfirm(channel);
        } else {
            this.sendAndForget(channel);
        }
    }

    private AmqpSendResult sendAsyncInternal(String correlationId,
                                             byte[] msg,
                                             String routingKey,
                                             Map<String, Object> messageHeaders,
                                             Consumer<AmqpSendResult> doneCallback) {
        checkNotNull(msg, "parameter 'msg' is null");
        checkNotNull(routingKey, "parameter 'routingKey' is null");
        checkArgument(msg.length != 0, "parameter 'msg' is empty");
        checkNotNull(messageHeaders, "parameter 'messageHeaders' is null");

        int currentSize = this.normalQueue.size();
        if (currentSize > this.maxBufferSize) {
            logger.warn("buffer size limit reached [size={}, limit={}, exName={}]", currentSize, this.maxBufferSize, this.exchangeName);
            return new RejectedMessage(correlationId, msg, routingKey, messageHeaders, this);
        }

        checkState(this.isOpen(), "connector is closed");

        AcceptedMessage task;
        if (this.waitForPublishConfirmations) {
            task = new AcceptedMessage(correlationId, msg, routingKey, messageHeaders, doneCallback, this);
        } else {
            task = new AcceptedMessageNoConfirm(correlationId, msg, routingKey, messageHeaders, null, this);
        }
        if (this.normalQueue.offer(task)) {
            if ((!this.waitForPublishConfirmations) && (doneCallback != null)) {
                doneCallback.accept(task);
            }
            return task;
        } else {
            logger.warn("sendBlocking rejected [exName={}]", this.exchangeName);
            return new RejectedMessage(correlationId, msg, routingKey, messageHeaders, this);
        }
    }

    private void sendAndWaitForConfirm(Channel channel) throws
            InterruptedException,
            IOException {

        final ConcurrentSkipListMap<Long, AcceptedMessage> msgWaitingForConfirm = new ConcurrentSkipListMap<>();
        final ConfirmListener msgConfirmedListener = new ConfirmListener() {
            @Override
            public void handleAck(final long tag, final boolean multiple) throws IOException {
                if (multiple) {
                    Map<Long, AcceptedMessage> confirmed = new HashMap<>(msgWaitingForConfirm.headMap(tag, true));
                    for (Map.Entry<Long, AcceptedMessage> entry : confirmed.entrySet()) {
                        entry.getValue().setResult(true);
                        msgWaitingForConfirm.remove(entry.getKey());
                    }
                } else {
                    AcceptedMessage m = msgWaitingForConfirm.remove(tag);
                    m.setResult(true);
                }
            }

            @Override
            public void handleNack(final long tag, final boolean multiple) throws IOException {
                if (multiple) {
                    Map<Long, AcceptedMessage> confirmed = new HashMap<>(msgWaitingForConfirm.headMap(tag, true));
                    for (Map.Entry<Long, AcceptedMessage> entry : confirmed.entrySet()) {
                        entry.getValue().retryCount++;
                        if (entry.getValue().retryCount == maxRetryCount) {
                            entry.getValue().setResult(false);
                        } else {
                            if (!redeliveryQueue.offer(entry.getValue())) {
                                entry.getValue().setResult(false);
                            }
                        }
                        msgWaitingForConfirm.remove(entry.getKey());
                    }
                } else {
                    AcceptedMessage m = msgWaitingForConfirm.remove(tag);
                    m.retryCount++;
                    if (m.retryCount == maxRetryCount) {
                        m.setResult(false);
                    } else {
                        if (!redeliveryQueue.offer(m)) {
                            m.setResult(false);
                        }
                    }
                }
            }
        };

        AcceptedMessage currentMsg = null;
        try {
            channel.confirmSelect();
            channel.addConfirmListener(msgConfirmedListener);
            channel.addReturnListener(returnListener);

            while (this.isOpen() || (!(this.redeliveryQueue.isEmpty() && this.normalQueue.isEmpty()))) {

                if (Thread.interrupted()) {
                    throw new InterruptedException("thread has been interrupted while actively working");
                }
                if (!channel.isOpen()) {
                    throw channel.getCloseReason();
                }

                currentMsg = this.redeliveryQueue.poll();
                if (currentMsg == null) {
                    currentMsg = this.normalQueue.poll(this.waitForTaskMillis, TimeUnit.MILLISECONDS);
                }

                if (currentMsg == null) {
                    continue;
                }
                if ((currentMsg.content == null) || (currentMsg.content.length == 0)) {
                    currentMsg.setResult(false);
                    currentMsg = null;
                    continue;
                }

                long tag = channel.getNextPublishSeqNo();
                msgWaitingForConfirm.put(tag, currentMsg);
                channel.basicPublish(this.exchangeName,
                        currentMsg.routingKey,
                        this.isPublishMandatory,
                        createProperties(currentMsg.messageHeaders, currentMsg.getCorrelationId()),
                        currentMsg.content);
                currentMsg = null;
            }
        } finally {
            boolean cancelCurrentMsg = (currentMsg != null);
            for (AcceptedMessage unconfirmedMsg : msgWaitingForConfirm.values()) {
                if (cancelCurrentMsg && (unconfirmedMsg == currentMsg)) {
                    cancelCurrentMsg = false;
                }
                if ((!this.isOpen()) || (!redeliveryQueue.offer(unconfirmedMsg))) {
                    unconfirmedMsg.setResult(false);
                }
            }
            if (cancelCurrentMsg) {
                if ((!this.isOpen()) || (!redeliveryQueue.offer(currentMsg))) {
                    currentMsg.setResult(false);
                }
            }
        }
    }

    private void sendAndForget(Channel channel) throws
            InterruptedException,
            IOException {

        AcceptedMessage currentMsg = null;
        try {
            channel.addReturnListener(returnListener);

            while (this.isOpen() || (!(this.redeliveryQueue.isEmpty() && this.normalQueue.isEmpty()))) {

                if (Thread.interrupted()) {
                    throw new InterruptedException("thread has been interrupted while actively working");
                }
                if (!channel.isOpen()) {
                    throw channel.getCloseReason();
                }

                currentMsg = this.redeliveryQueue.poll();
                if (currentMsg == null) {
                    currentMsg = this.normalQueue.poll(this.waitForTaskMillis, TimeUnit.MILLISECONDS);
                }

                if (currentMsg == null) {
                    continue;
                }
                if ((currentMsg.content == null) || (currentMsg.content.length == 0)) {
                    currentMsg.setResult(false);
                    currentMsg = null;
                    continue;
                }

                channel.basicPublish(this.exchangeName,
                        currentMsg.routingKey,
                        this.isPublishMandatory,
                        createProperties(currentMsg.messageHeaders, currentMsg.getCorrelationId()),
                        currentMsg.content);
                currentMsg = null;
            }
        } finally {
            if ((currentMsg != null)
                    && ((!this.isOpen()) || (!redeliveryQueue.offer(currentMsg)))) {
                currentMsg.setResult(false);
            }
        }
    }

    private AMQP.BasicProperties createProperties(Map<String, Object> messageHeaders, String correlationId) {
        if (messageHeaders.isEmpty() && correlationId == null) {
            return this.msgProperties;
        }

        AMQP.BasicProperties.Builder builder;
        if (this.msgProperties == null) {
            builder = new AMQP.BasicProperties().builder();//.headers(messageHeaders).build();

        } else {
            builder = this.msgProperties.builder();
        }

        if (correlationId != null) {
            builder.correlationId(correlationId);
        }
        if (!messageHeaders.isEmpty()) {
            builder.headers(messageHeaders);
        }

        return builder.build();
    }

    private static String decodeBody(final byte[] body, final boolean preferBase64) {
        if (body == null) {
            return "null";
        }

        if (preferBase64) {
            try {
                return Base64.getEncoder().encodeToString(body);
            } catch (Exception ignored) {
            }
        } else {
            try {
                return new String(body, StandardCharsets.UTF_8);
            } catch (Exception ignored) {
            }
        }

        final StringBuilder sb = new StringBuilder(1024);
        sb.append("bytes: ");
        for (final byte singleByte : body) {
            sb.append(singleByte).append(" ");
        }
        return sb.toString();
    }

    private static final class RejectedMessage implements AmqpSendResult {

        public final byte[] content;
        public final String routingKey;
        public final AmqpProducer mqProducer;
        public final Map<String, Object> messageHeaders;
        private final String correlationId;

        public RejectedMessage(String correlationId,
                               byte[] content,
                               String routingKey,
                               Map<String, Object> messageHeaders,
                               AmqpProducer mqProducer) {
            this.correlationId = correlationId;
            this.content = content;
            this.routingKey = routingKey;
            this.messageHeaders = messageHeaders;
            this.mqProducer = mqProducer;
        }

        @Override
        public boolean isRejected() {
            return true;
        }

        @Override
        public boolean cancel(boolean b) {
            return false;
        }

        @Override
        public AmqpProducer getMqProducer() {
            return this.mqProducer;
        }


        @Override
        public byte[] getContent() {
            return this.content;
        }

        @Override
        public String getRoutingKey() {
            return this.routingKey;
        }

        @Override
        public String getCorrelationId() {
            return this.correlationId;
        }

        @Override
        public Map<String, Object> getMessageHeaders() {
            return messageHeaders;
        }


        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Boolean get() throws InterruptedException, ExecutionException {
            return false;
        }

        @Override
        public Boolean get(long l, TimeUnit timeUnit) throws
                InterruptedException,
                ExecutionException,
                TimeoutException {
            return false;
        }
    }

    private static final class AcceptedMessageNoConfirm extends AcceptedMessage {

        public AcceptedMessageNoConfirm(String correlationId,
                                        byte[] content,
                                        String routingKey,
                                        Map<String, Object> messageHeaders,
                                        Consumer<AmqpSendResult> onDoneCallback,
                                        RabbitMqProducer mqProducer) {
            super(correlationId, content, routingKey, messageHeaders, onDoneCallback, mqProducer);
        }

        @Override
        public Boolean get() throws InterruptedException, ExecutionException {
            return true;
        }

        @Override
        public Boolean get(long l, TimeUnit timeUnit) throws
                InterruptedException,
                ExecutionException,
                TimeoutException {
            return true;
        }
    }

    private static class AcceptedMessage implements AmqpSendResult {

        public volatile int retryCount = 0;
        private final AtomicReference<Boolean> result = new AtomicReference<>(null);
        private final byte[] content;
        private final String routingKey;
        private final String correlationId;
        private final RabbitMqProducer mqProducer;
        private final Map<String, Object> messageHeaders;
        private final CountDownLatch latch;
        private final Consumer<AmqpSendResult> doneCallback;

        public AcceptedMessage(String correlationId,
                               byte[] content,
                               String routingKey,
                               Map<String, Object> messageHeaders,
                               Consumer<AmqpSendResult> doneCallback,
                               RabbitMqProducer mqProducer) {
            this.correlationId = correlationId;
            this.content = content;
            this.routingKey = routingKey;
            this.messageHeaders = messageHeaders;
            this.doneCallback = doneCallback;
            this.mqProducer = mqProducer;
            this.latch = new CountDownLatch(1);
        }

        public void setResult(boolean res) {
            if (this.result.compareAndSet(null, res)) {
                this.latch.countDown();
                if (this.doneCallback != null) {
                    this.mqProducer.execute(new DoneCallbackRunnable(this.doneCallback, this));
                }
            }
        }

        @Override
        public AmqpProducer getMqProducer() {
            return this.mqProducer;
        }

        @Override
        public byte[] getContent() {
            return this.content;
        }

        @Override
        public String getRoutingKey() {
            return this.routingKey;
        }

        @Override
        public String getCorrelationId() {
            return correlationId;
        }

        @Override
        public Map<String, Object> getMessageHeaders() {
            return messageHeaders;
        }

        @Override
        public boolean isRejected() {
            return false;
        }

        @Override
        public boolean cancel(boolean b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return (this.latch.getCount() < 1L);
        }

        @Override
        public Boolean get() throws InterruptedException, ExecutionException {
            this.latch.await();
            return this.result.get();
        }

        @Override
        public Boolean get(long l, TimeUnit timeUnit) throws
                InterruptedException,
                ExecutionException,
                TimeoutException {
            if (!this.latch.await(l, timeUnit)) {
                throw new TimeoutException();
            }
            final Boolean result = this.result.get();
            if (result == null) {
                throw new TimeoutException();
            }
            return result;
        }


    }

    private static final class DoneCallbackRunnable implements Runnable {

        private final Consumer<AmqpSendResult> doneCallback;
        private final AmqpSendResult result;

        public DoneCallbackRunnable(final Consumer<AmqpSendResult> doneCallback, final AmqpSendResult result) {
            this.doneCallback = doneCallback;
            this.result = result;
        }

        @Override
        public void run() {
            try {
                this.doneCallback.accept(this.result);
            } catch (Exception exc) {
                logger.error("Unexpected exc: ", exc);
            }
        }
    }
}
