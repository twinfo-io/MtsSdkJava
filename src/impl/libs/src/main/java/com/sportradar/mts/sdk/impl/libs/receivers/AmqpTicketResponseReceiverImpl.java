/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.TicketResponse;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.TicketResponseSchema;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpConsumer;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpMessageReceiver;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.MessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class AmqpTicketResponseReceiverImpl implements AmqpMessageReceiver {

    private static final Logger logger = LoggerFactory.getLogger(AmqpTicketResponseReceiverImpl.class);
    private final Object stateLock = new Object();
    private final AmqpConsumer consumer;
    private final TicketResponseReceiver ticketResponseReceiver;
    private boolean opened;

    public AmqpTicketResponseReceiverImpl(AmqpConsumer consumer,
                                          TicketResponseReceiver responseReceiver
    ) {
        checkNotNull(consumer, "consumer cannot be null");
        checkNotNull(responseReceiver, "responseReceiver cannot be null");
        this.consumer = consumer;
        this.ticketResponseReceiver = responseReceiver;
        consumer.setMessageReceivedHandler(this);
    }

    @Override
    public void open() {
        synchronized (stateLock) {
            if (!opened) {
                consumer.open();
                opened = true;
            }
        }
    }

    @Override
    public void close() {
        synchronized (stateLock) {
            if (opened) {
                consumer.close();
                opened = false;
            }
        }
    }

    @Override
    public boolean isOpen() {
        return opened;
    }

    @Override
    public MessageStatus consume(byte[] msg,
                                 String routingKey,
                                 String correlationId,
                                 Map<String, Object> messageHeaders) {

        String msgBody = new String(msg);
        if(logger.isDebugEnabled())
        {
            logger.debug("received ticket response with correlation id: {}, msg: {}, headers: {}", correlationId, msgBody, messageHeaders);
        }
        else {
            logger.info("received ticket response with correlation id: {}", correlationId);
        }

        TicketResponse ticketResponse = null;
        try {
            TicketResponseSchema ticketResponseSchema = JsonUtils.deserialize(msg, TicketResponseSchema.class);
            ticketResponse = MtsDtoMapper.map(ticketResponseSchema, correlationId, messageHeaders, msgBody);
        }
        catch (IOException e) {
            logger.error("failed to deserialize ticket response! msg: {}", msgBody, e);
        }
        catch(Exception e)
        {
            logger.error("failed to map ticket response! msg: {}", msgBody, e);
        }
        if (ticketResponse != null) {
            ticketResponseReceiver.ticketResponseReceived(ticketResponse);
        }
        return MessageStatus.CONSUMED_SUCCESSFULLY;
    }

    @Override
    public void afterLimitReached(byte[] msg, String routingKey, String correlationId) {
        logger.error("ticket response consume retry reached! msg: {}", msg);
    }
}
