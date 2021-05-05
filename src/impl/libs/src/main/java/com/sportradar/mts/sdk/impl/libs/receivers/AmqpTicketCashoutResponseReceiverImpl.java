/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.TicketCashoutResponse;
import com.sportradar.mts.sdk.api.impl.ConnectionStatusImpl;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.TicketCashoutResponseSchema;
import com.sportradar.mts.sdk.api.interfaces.ConnectionStatus;
import com.sportradar.mts.sdk.api.utils.JsonUtils;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpConsumer;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.AmqpMessageReceiver;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.MessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class AmqpTicketCashoutResponseReceiverImpl implements AmqpMessageReceiver {
    private static final Logger logger = LoggerFactory.getLogger(AmqpTicketCashoutResponseReceiverImpl.class);
    private final Object stateLock = new Object();
    private final AmqpConsumer consumer;
    private final TicketCashoutResponseReceiver ticketCashoutResponseReceiver;
    private boolean opened;
    private final ConnectionStatusImpl connectionStatus;


    public AmqpTicketCashoutResponseReceiverImpl(AmqpConsumer consumer,
                                                 TicketCashoutResponseReceiver ticketCashoutResponseReceiver,
                                                 ConnectionStatus connectionStatus) {
        checkNotNull(consumer, "consumer cannot be null");
        checkNotNull(ticketCashoutResponseReceiver, "ticketCashoutResponseReceiver cannot be null");
        checkNotNull(connectionStatus, "connectionStatus cannot be null");

        this.consumer = consumer;
        this.ticketCashoutResponseReceiver = ticketCashoutResponseReceiver;
        this.connectionStatus = (ConnectionStatusImpl) connectionStatus;
        consumer.setMessageReceivedHandler(this);
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

        TicketCashoutResponse ticketCashoutResponse = null;
        try {
            TicketCashoutResponseSchema ticketSchema = JsonUtils.deserialize(msg, TicketCashoutResponseSchema.class);
            ticketCashoutResponse = MtsDtoMapper.map(ticketSchema, correlationId, messageHeaders, msgBody);
            connectionStatus.ticketReceived(ticketCashoutResponse.getTicketId());
        }
        catch (IOException e) {
            logger.error("failed to deserialize ticket cashout response! msg: {}", msgBody, e);
        }
        catch(Exception e)
        {
            logger.error("failed to map ticket cashout response! msg: {}", msgBody, e);
        }

        if (ticketCashoutResponse != null) {
            ticketCashoutResponseReceiver.ticketCashoutResponseReceived(ticketCashoutResponse);
        }

        return MessageStatus.CONSUMED_SUCCESSFULLY;
    }

    @Override
    public void afterLimitReached(byte[] msg, String routingKey, String correlationId) {
        String msgStr = msg == null ? "" : new String(msg, StandardCharsets.UTF_8);
        logger.error("ticket cashout response consume retry reached! msg : {}", msgStr);
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
}
