/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author andrej.resnik on 02/08/16 at 09:02
 */
public class RabbitMqFactoryTest {

    private AmqpFactory mqFactory;
    private String instanceName;
    private AmqpCluster mqCluster;
    private String exchangeName;
    private ExchangeType exchangeType;
    private int maxRetryCount;
    private int maxBufferSize;
    private int concurrencyLevel;
    private boolean msgMemOnly;
    private boolean waitForPublishConfirmations;
    private boolean mandatory;

    @Before
    public void setUp() {
        ChannelFactoryProvider channelFactoryProvider = mock(ChannelFactoryProvider.class);
        mqFactory = new RabbitMqFactory(channelFactoryProvider);

        instanceName = "test";
        mqCluster = AmqpCluster.from("testUsername", "testPassword", "testVHost", false, new NetworkAddress("testHost"));
        exchangeName = "testExchange";
        exchangeType = ExchangeType.TOPIC;
        maxRetryCount = 1;
        maxBufferSize = 1024;
        concurrencyLevel = 1;
        msgMemOnly = true;
        waitForPublishConfirmations = false;
        mandatory = false;
    }

    @Test (expected = NullPointerException.class)
    public void initNewInstancePassingNullTest() {
        mqFactory = new RabbitMqFactory(null);
    }

    @Test
    public void createProducerTest() {
        AmqpProducer mqProducer = mqFactory.createProducer(
                instanceName,
                mqCluster,
                exchangeName,
                exchangeType,
                maxRetryCount,
                maxBufferSize,
                concurrencyLevel,
                msgMemOnly,
                waitForPublishConfirmations,
                mandatory);

        assertThat(mqProducer, is(notNullValue()));
        assertThat(mqProducer, instanceOf(RabbitMqProducer.class));
    }

    @Test
    public void createConsumerTest() {
        AmqpConsumer mqConsumer = mqFactory.createConsumer(
                instanceName,
                mqCluster,
                exchangeName,
                exchangeType,
                "testQueue",
                "replyRoutingKey",
                maxRetryCount,
                1,
                concurrencyLevel,
                true,
                false);

        assertThat(mqConsumer, is(notNullValue()));
        assertThat(mqConsumer, instanceOf(RabbitMqConsumer.class));
    }
}
