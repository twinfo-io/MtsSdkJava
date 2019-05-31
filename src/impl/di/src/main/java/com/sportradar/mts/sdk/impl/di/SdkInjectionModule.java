/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.di;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPICalculationResponse;
import com.sportradar.mts.api.rest.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.SdkTicket;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.caching.MarketDescriptionCI;
import com.sportradar.mts.sdk.api.caching.MarketDescriptionCache;
import com.sportradar.mts.sdk.api.caching.MarketDescriptionCacheImpl;
import com.sportradar.mts.sdk.api.caching.MarketDescriptionProvider;
import com.sportradar.mts.sdk.api.enums.UfEnvironment;
import com.sportradar.mts.sdk.api.impl.builders.BuilderFactoryImpl;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.AccessTokenSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.CcfResponseSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.MaxStakeResponseSchema;
import com.sportradar.mts.sdk.api.interfaces.MtsClientApi;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
import com.sportradar.mts.sdk.api.interfaces.TicketSender;
import com.sportradar.mts.sdk.api.interfaces.customBet.CustomBetManager;
import com.sportradar.mts.sdk.api.interfaces.customBet.CustomBetSelectionBuilder;
import com.sportradar.mts.sdk.api.rest.*;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import com.sportradar.mts.sdk.impl.libs.adapters.amqp.*;
import com.sportradar.mts.sdk.impl.libs.clientapi.MtsClientApiImpl;
import com.sportradar.mts.sdk.impl.libs.customBet.CustomBetManagerImpl;
import com.sportradar.mts.sdk.impl.libs.customBet.CustomBetSelectionBuilderImpl;
import com.sportradar.mts.sdk.impl.libs.handlers.*;
import com.sportradar.mts.sdk.impl.libs.logging.FileSdkLoggerImpl;
import com.sportradar.mts.sdk.impl.libs.logging.SdkLogger;
import com.sportradar.mts.sdk.impl.libs.receivers.*;
import com.sportradar.mts.sdk.impl.libs.root.SdkRoot;
import com.sportradar.mts.sdk.impl.libs.root.SdkRootImpl;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SdkInjectionModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(SdkInjectionModule.class);
    private final SdkConfiguration sdkConfiguration;
    /**
     * The {@link JAXBContext} used to unmarshall API responses
     */
    private final JAXBContext apiJaxbContext;
    private final JAXBContext customBetJaxbContext;

    private final List<Locale> locales;
    private final int rabbitPrefetchCount = 10;
    private UfEnvironment ufEnvironment;// = UfEnvironment.PRODUCTION;

//    public SdkInjectionModule(Properties properties) {
//        sdkConfiguration = PropertiesToSettingsMapper.getSettings(properties);
//        logger.info("passed MTS SDK settings : {}", sdkConfiguration);
//    }

    public SdkInjectionModule(SdkConfiguration config) {
        sdkConfiguration = config;
        logger.info("passed MTS SDK configuration: {}", sdkConfiguration);

        try {
            apiJaxbContext = JAXBContext.newInstance("com.sportradar.mts.api.rest.sportsapi.datamodel");
            customBetJaxbContext = JAXBContext.newInstance("com.sportradar.mts.api.rest.custombet.datamodel");
        } catch (JAXBException e) {
            throw new IllegalStateException("JAXB contexts creation failed, ex: ", e);
        }

        locales = new ArrayList<>();
        locales.add(Locale.ENGLISH);
    }

    @Singleton
    @Provides
    public SdkRoot provideSdkRoot(SdkLogger sdkLogger,
                                  ScheduledExecutorService executorService,
                                  ChannelFactoryProvider channelFactoryProvider,
                                  TicketHandler ticketHandler,
                                  @TicketResponseMessageReceiverBinding AmqpMessageReceiver ticketAmqpMessageReceiver,
                                  TicketCancelHandler ticketCancelHandler,
                                  @TicketCancelResponseMessageReceiverBinding AmqpMessageReceiver ticketCancelAmqpMessageReceiver,
                                  TicketAckHandler ticketAcknowledgmentSender,
                                  TicketCancelAckHandler ticketCancelAckHandler,
                                  TicketReofferCancelHandler ticketReofferCancelHandler,
                                  TicketCashoutHandler ticketCashoutHandler,
                                  @TicketCashoutResponseMessageReceiverBinding AmqpMessageReceiver ticketCashoutAmqpMessageReceiver,
                                  TicketNonSrSettleHandler ticketNonSrSettleHandler,
                                  @TicketNonSrSettleResponseMessageReceiverBinding AmqpMessageReceiver ticketNonSrSettleAmpqMessageReceiver
                                  ) {
        return new SdkRootImpl(sdkLogger,
                               executorService,
                               channelFactoryProvider,
                               ticketHandler,
                               ticketAmqpMessageReceiver,
                               ticketCancelHandler,
                               ticketCancelAmqpMessageReceiver,
                               ticketAcknowledgmentSender,
                               ticketCancelAckHandler,
                               ticketReofferCancelHandler,
                               ticketCashoutHandler,
                               ticketCashoutAmqpMessageReceiver,
                               ticketNonSrSettleHandler,
                               ticketNonSrSettleAmpqMessageReceiver);
    }

    @Singleton
    @Provides
    public SdkLogger provideSdkLogger() {
        return new FileSdkLoggerImpl("com.sportradar.mts.traffic");
    }

    @Singleton
    @Provides
    public TicketHandler provideTicketHandler(@TicketPublisherBinding AmqpPublisher amqpPublisher,
                                              ScheduledExecutorService executorService,
                                              SdkLogger sdkLogger
    ) {
        String routingKey = "node" + sdkConfiguration.getNode() + ".ticket.confirm"; // ticketRoutingKey(node);
        return new TicketHandlerImpl(amqpPublisher,
                                     routingKey,
                                     executorService,
                                     getTimeoutHandler(executorService, sdkConfiguration.getTicketResponseTimeout()),
                                     sdkConfiguration.getTicketResponseTimeout(),
                                     sdkConfiguration.getMessagesPerSecond(),
                                     sdkLogger);
    }

    @Singleton
    @Provides
    public TicketCancelHandler provideTicketSender(@TicketCancelPublisherBinding AmqpPublisher amqpPublisher,
                                                   ScheduledExecutorService executorService,
                                                   SdkLogger sdkLogger
    ) {
        String routingKey = "cancel";
        String replyRoutingKey = "node" + sdkConfiguration.getNode() + ".cancel.confirm";
        return new TicketCancelHandlerImpl(amqpPublisher,
                                           routingKey,
                                           replyRoutingKey,
                                           executorService,
                                           getTimeoutHandler(executorService, sdkConfiguration.getTicketCancellationResponseTimeout()),
                                           sdkConfiguration.getTicketCancellationResponseTimeout(),
                                           sdkConfiguration.getMessagesPerSecond(),
                                           sdkLogger);
    }

    @Singleton
    @Provides
    public TicketReofferCancelHandler provideTicketReofferSender(@TicketReofferCancelPublisherBinding AmqpPublisher amqpPublisher,
                                                   ExecutorService executorService,
                                                   SdkLogger sdkLogger
    ) {
//        String routingKey = "node" + sdkConfiguration.getNode() + ".cancel.confirm";
        String routingKey = "cancel.reoffer";
        return new TicketReofferCancelHandlerImpl(amqpPublisher,
                routingKey,
                executorService,
                sdkConfiguration.getMessagesPerSecond(),
                sdkLogger);
    }

    @Singleton
    @Provides
    public TicketAckHandler provideTicketAcknowledgmentHandler(@TicketAcknowledgmentPublisherBinding AmqpPublisher amqpPublisher,
                                                               ExecutorService executorService,
                                                               SdkLogger sdkLogger
    ) {
        String routingKey = "ack.ticket";
        return new TicketAckHandlerImpl(amqpPublisher,
                                        routingKey,
                                        executorService,
                                        sdkConfiguration.getMessagesPerSecond(),
                                        sdkLogger);
    }

    @Singleton
    @Provides
    public TicketCancelAckHandler provideTicketCancelAcknowledgmentHandler(@TicketCancelAcknowledgmentPublisherBinding AmqpPublisher amqpPublisher,
                                                                           ExecutorService executorService,
                                                                           SdkLogger sdkLogger
    ) {
        String routingKey = "ack.cancel";
        return new TicketCancelAckHandlerImpl(amqpPublisher,
                                              routingKey,
                                              executorService,
                                              sdkConfiguration.getMessagesPerSecond(),
                                              sdkLogger);
    }

    @Singleton
    @Provides
    public TicketCashoutHandler provideTicketCashoutHandler(@TicketCashoutPublisherBinding AmqpPublisher amqpPublisher,
                                                                           ScheduledExecutorService executorService,
                                                                           SdkLogger sdkLogger) {
        String routingKey = "ticket.cashout";
        String replyRoutingKey = "node" + sdkConfiguration.getNode() + ".ticket.cashout";
        return new TicketCashoutHandlerImpl(amqpPublisher,
                routingKey,
                replyRoutingKey,
                executorService,
                getTimeoutHandler(executorService, sdkConfiguration.getTicketCashoutResponseTimeout()),
                sdkConfiguration.getTicketCashoutResponseTimeout(),
                sdkConfiguration.getMessagesPerSecond(),
                sdkLogger);
    }

    @Singleton
    @Provides
    public TicketNonSrSettleHandler provideTicketNonSrSettleHandler(@TicketNonSrSettlePublisherBinding AmqpPublisher amqpPublisher,
                                                            ScheduledExecutorService executorService,
                                                            SdkLogger sdkLogger) {
        String routingKey = "ticket.nonsrsettle";
        String replyRoutingKey = "node" + sdkConfiguration.getNode() + ".ticket.nonsrsettle";
        return new TicketNonSrSettleHandlerImpl(amqpPublisher,
                routingKey,
                replyRoutingKey,
                executorService,
                getTimeoutHandler(executorService, sdkConfiguration.getTicketNonSrSettleResponseTimeout()),
                sdkConfiguration.getTicketNonSrSettleResponseTimeout(),
                sdkConfiguration.getMessagesPerSecond(),
                sdkLogger);
    }

    @Singleton
    @Provides
    @TicketProducerBinding
    public AmqpProducer provideTicketAmqpProducer(ChannelFactoryProvider channelFactoryProvider,
                                                  AmqpCluster amqpCluster
    ) {
//        String exchangeName = sdkConfiguration.getVirtualHost() + "-Submit";
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Submit";
        return new RabbitMqProducer(channelFactoryProvider,
                                    "ticket-producer",
                                    amqpCluster,
                                    exchangeName,
                                    ExchangeType.FANOUT,
                                    1,
                                    64,
                                    1,
                                    true,
                                    true,
                                    true);
    }

    @Singleton
    @Provides
    @TicketCancelProducerBinding
    public AmqpProducer provideTicketCancelAmqpProducer(ChannelFactoryProvider channelFactoryProvider,
                                                        AmqpCluster amqpCluster
    ) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Control";
        return new RabbitMqProducer(channelFactoryProvider,
                                    "ticket-cancel-producer",
                                    amqpCluster,
                                    exchangeName,
                                    ExchangeType.TOPIC,
                                    1,
                                    64,
                                    1,
                                    true,
                                    true,
                                    true);
    }

    @Singleton
    @Provides
    @TicketReofferCancelProducerBinding
    public AmqpProducer provideTicketReofferCancelAmqpProducer(ChannelFactoryProvider channelFactoryProvider,
                                                        AmqpCluster amqpCluster
    ) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Control";
        return new RabbitMqProducer(channelFactoryProvider,
                "ticket--reoffer-cancel-producer",
                amqpCluster,
                exchangeName,
                ExchangeType.TOPIC,
                1,
                64,
                1,
                true,
                true,
                true);
    }

    @Singleton
    @Provides
    @TicketCashoutProducerBinding
    public AmqpProducer provideTicketCashoutAmqpProducer(ChannelFactoryProvider channelFactoryProvider,
                                                        AmqpCluster amqpCluster
    ) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Control";
        return new RabbitMqProducer(channelFactoryProvider,
                "ticket-cashout-producer",
                amqpCluster,
                exchangeName,
                ExchangeType.TOPIC,
                1,
                64,
                1,
                true,
                true,
                true);
    }

    @Singleton
    @Provides
    @TicketNonSrSettleProducerBinding
    public AmqpProducer provideTicketNonSrSettleAmqpProducer(ChannelFactoryProvider channelFactoryProvider,
                                                         AmqpCluster amqpCluster
    ) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Control";
        return new RabbitMqProducer(channelFactoryProvider,
                "ticket-non-sr-settle-producer",
                amqpCluster,
                exchangeName,
                ExchangeType.TOPIC,
                1,
                64,
                1,
                true,
                true,
                true);
    }

    @Singleton
    @Provides
    @AcknowledgmentProducerBinding
    public AmqpProducer provideAcknowledgmentAmqpProducer(ChannelFactoryProvider channelFactoryProvider,
                                                          AmqpCluster amqpCluster
    ) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Ack";
        return new RabbitMqProducer(channelFactoryProvider,
                                    "ack-producer",
                                    amqpCluster,
                                    exchangeName,
                                    ExchangeType.TOPIC,
                                    1,
                                    64,
                                    1,
                                    true,
                                    true,
                                    true);
    }

    @Singleton
    @Provides
    @TicketResponseConsumerBinding
    public AmqpConsumer provideTicketResponseConsumer(ChannelFactoryProvider channelFactoryProvider,
                                                      AmqpCluster amqpCluster) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Confirm";
        String routingKey = "node" + sdkConfiguration.getNode() + ".ticket.confirm";
        String queueName = sdkConfiguration.getUsername() + "-Confirm-node" + sdkConfiguration.getNode();
        return new RabbitMqConsumer(channelFactoryProvider,
                                    routingKey,
                                    "ticket-response-consumer",
                                    amqpCluster,
                                    exchangeName,
                                    ExchangeType.TOPIC,
                                    queueName,
                                    1,
                                    rabbitPrefetchCount,
                                    1,
                                    false,
                                    sdkConfiguration.getExclusiveConsumer());
    }


    @Singleton
    @Provides
    @TicketCancelResponseConsumerBinding
    public AmqpConsumer provideTicketCancelResponseConsumer(ChannelFactoryProvider channelFactoryProvider,
                                                            AmqpCluster amqpCluster) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Reply";
        String routingKey = "node" + sdkConfiguration.getNode() + ".cancel.confirm";
        String queueName = sdkConfiguration.getUsername() + "-Reply-node" + sdkConfiguration.getNode();
        return new RabbitMqConsumer(channelFactoryProvider,
                                    routingKey,
                                    "ticket-cancel-response-consumer",
                                    amqpCluster,
                                    exchangeName,
                                    ExchangeType.TOPIC,
                                    queueName,
                                    1,
                                    rabbitPrefetchCount,
                                    1,
                                    false,
                                    sdkConfiguration.getExclusiveConsumer());
    }

    @Singleton
    @Provides
    @TicketCashoutResponseConsumerBinding
    public AmqpConsumer provideTicketCashoutResponseConsumer(ChannelFactoryProvider channelFactoryProvider,
                                                            AmqpCluster amqpCluster) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Reply";
        String routingKey = "node" + sdkConfiguration.getNode() + ".ticket.cashout";
        String queueName = sdkConfiguration.getUsername() + "-Reply-cashout-node" + sdkConfiguration.getNode();
        return new RabbitMqConsumer(channelFactoryProvider,
                                    routingKey,
                                    "ticket-cashout-response-consumer",
                                    amqpCluster,
                                    exchangeName,
                                    ExchangeType.TOPIC,
                                    queueName,
                                    1,
                                    rabbitPrefetchCount,
                                    1,
                                    false,
                                    sdkConfiguration.getExclusiveConsumer());
    }

    @Singleton
    @Provides
    @TicketNonSrSettleResponseConsumerBinding
    public AmqpConsumer provideTicketNonSrSettleResponseConsumer(ChannelFactoryProvider channelFactoryProvider,
                                                             AmqpCluster amqpCluster) {
        String exchangeName = sdkConfiguration.getVirtualHost().replace("/", "") + "-Reply";
        String routingKey = "node" + sdkConfiguration.getNode() + ".ticket.nonsrsettle";
        String queueName = sdkConfiguration.getUsername() + "-Reply-nonsrsettle-node" + sdkConfiguration.getNode();
        return new RabbitMqConsumer(channelFactoryProvider,
                routingKey,
                "ticket-nonsrsettle-response-consumer",
                amqpCluster,
                exchangeName,
                ExchangeType.TOPIC,
                queueName,
                1,
                rabbitPrefetchCount,
                1,
                false,
                sdkConfiguration.getExclusiveConsumer());
    }


    @Singleton
    @Provides
    @TicketPublisherBinding
    public AmqpPublisher provideTicketAmqpPublisher(
            @TicketProducerBinding AmqpProducer amqpProducer,
            @TicketAmqpSendResultHandlerBinding AmqpSendResultHandler amqpSendResultHandler
    ) {
        return new AmqpPublisherImpl(amqpProducer, amqpSendResultHandler);
    }

    @Singleton
    @Provides
    @TicketCancelPublisherBinding
    public AmqpPublisher provideTicketCancelAmqpPublisher(
            @TicketCancelProducerBinding AmqpProducer amqpProducer,
            @TicketCancelAmqpSendResultHandlerBinding AmqpSendResultHandler amqpSendResultHandler
    ) {
        return new AmqpPublisherImpl(amqpProducer, amqpSendResultHandler);
    }

    @Singleton
    @Provides
    @TicketReofferCancelPublisherBinding
    public AmqpPublisher provideTicketReofferCancelAmqpPublisher(
            @TicketReofferCancelProducerBinding AmqpProducer amqpProducer,
            @TicketReofferCancelAmqpSendResultHandlerBinding AmqpSendResultHandler amqpSendResultHandler
    ) {
        return new AmqpPublisherImpl(amqpProducer, amqpSendResultHandler);
    }

    @Singleton
    @Provides
    @TicketAcknowledgmentPublisherBinding
    public AmqpPublisher provideTicketAcknowledgmentAmqpPublisher(
            @AcknowledgmentProducerBinding AmqpProducer amqpProducer,
            @TicketAckAmqpSendResultHandlerBinding AmqpSendResultHandler amqpSendResultHandler
    ) {
        return new AmqpPublisherImpl(amqpProducer, amqpSendResultHandler);
    }

    @Singleton
    @Provides
    @TicketCancelAcknowledgmentPublisherBinding
    public AmqpPublisher provideTicketCancelAcknowledgmentAmqpPublisher(
            @AcknowledgmentProducerBinding AmqpProducer amqpProducer,
            @TicketCancelAckAmqpSendResultHandlerBinding AmqpSendResultHandler amqpSendResultHandler
    ) {
        return new AmqpPublisherImpl(amqpProducer, amqpSendResultHandler);
    }

    @Singleton
    @Provides
    @TicketCashoutPublisherBinding
    public AmqpPublisher provideTicketCashoutAmqpPublisher(
            @TicketCashoutProducerBinding AmqpProducer amqpProducer,
            @TicketCashoutAmqpSendResultHandlerBinding AmqpSendResultHandler amqpSendResultHandler
    ) {
        return new AmqpPublisherImpl(amqpProducer, amqpSendResultHandler);
    }

    @Singleton
    @Provides
    @TicketNonSrSettlePublisherBinding
    public AmqpPublisher provideTicketNonSrSettleAmqpPublisher(
            @TicketNonSrSettleProducerBinding AmqpProducer amqpProducer,
            @TicketNonSrSettleAmqpSendResultHandlerBinding AmqpSendResultHandler amqpSendResultHandler
    ) {
        return new AmqpPublisherImpl(amqpProducer, amqpSendResultHandler);
    }

    @Singleton
    @Provides
    public ExecutorService provideExecutorService(ScheduledExecutorService service) {
        return service;
    }

    @Singleton
    @Provides
    public ScheduledExecutorService provideScheduledExecutorService() {
        return Executors.newScheduledThreadPool(1);
    }

    @Singleton
    @Provides
    public AmqpCluster provideAmqpCluster() {
        return AmqpCluster.from(sdkConfiguration.getUsername(),
                                sdkConfiguration.getPassword(),
                                sdkConfiguration.getVirtualHost(),
                                sdkConfiguration.getUseSsl(),
                                new NetworkAddress(sdkConfiguration.getHost(), sdkConfiguration.getPort()));
    }

    @Singleton
    @Provides
    public ChannelFactoryProvider provideChannelFactoryProvider() {
        // 3 producers + 2 receivers + 1 extra
        return new ChannelFactoryProviderImpl(6);
    }

    @Singleton
    @Provides
    @TicketAmqpSendResultHandlerBinding
    public AmqpSendResultHandler provideTicketAmqpSendResultHandler() {
        return new AmqpSendResultHandlerImpl("ticket");
    }

    @Singleton
    @Provides
    @TicketCancelAmqpSendResultHandlerBinding
    public AmqpSendResultHandler provideTicketCancelAmqpSendResultHandler() {
        return new AmqpSendResultHandlerImpl("ticketCancel");
    }

    @Singleton
    @Provides
    @TicketReofferCancelAmqpSendResultHandlerBinding
    public AmqpSendResultHandler provideTicketReofferCancelAmqpSendResultHandler() {
        return new AmqpSendResultHandlerImpl("ticketReofferCancel");
    }

    @Singleton
    @Provides
    @TicketAckAmqpSendResultHandlerBinding
    public AmqpSendResultHandler provideTicketAckAmqpSendResultHandler() {
        return new AmqpSendResultHandlerImpl("ticketAck");
    }


    @Singleton
    @Provides
    @TicketCancelAckAmqpSendResultHandlerBinding
    public AmqpSendResultHandler provideTicketCancelAckAmqpSendResultHandler() {
        return new AmqpSendResultHandlerImpl("ticketCancelAck");
    }

    @Singleton
    @Provides
    @TicketCashoutAmqpSendResultHandlerBinding
    public AmqpSendResultHandler provideTicketCashoutAmqpSendResultHandler() {
        return new AmqpSendResultHandlerImpl("ticketCashout");
    }

    @Singleton
    @Provides
    @TicketNonSrSettleAmqpSendResultHandlerBinding
    public AmqpSendResultHandler provideTicketNonSrSettleAmqpSendResultHandler() {
        return new AmqpSendResultHandlerImpl("ticketNonSrSettle");
    }

    @Singleton
    @Provides
    @TicketResponseMessageReceiverBinding
    public AmqpMessageReceiver provideTicketAmqpMessageReceived(@TicketResponseConsumerBinding AmqpConsumer amqpConsumer,
                                                                TicketResponseReceiver ticketResponseReceiver) {
        return new AmqpTicketResponseReceiverImpl(amqpConsumer, ticketResponseReceiver);
    }

    @Singleton
    @Provides
    @TicketCancelResponseMessageReceiverBinding
    public AmqpMessageReceiver provideTicketAmqpMessageReceived(@TicketCancelResponseConsumerBinding AmqpConsumer amqpConsumer,
                                                                TicketCancelResponseReceiver ticketCancelResponseReceiver) {
        return new AmqpTicketCancelResponseReceiverImpl(amqpConsumer, ticketCancelResponseReceiver);
    }

    @Singleton
    @Provides
    @TicketCashoutResponseMessageReceiverBinding
    public AmqpMessageReceiver provideTicketAmqpMessageReceived(@TicketCashoutResponseConsumerBinding AmqpConsumer amqpConsumer,
                                                                TicketCashoutResponseReceiver ticketCashoutResponseReceiver) {
        return new AmqpTicketCashoutResponseReceiverImpl(amqpConsumer, ticketCashoutResponseReceiver);
    }

    @Singleton
    @Provides
    @TicketNonSrSettleResponseMessageReceiverBinding
    public AmqpMessageReceiver provideTicketAmqpMessageReceived(@TicketNonSrSettleResponseConsumerBinding AmqpConsumer amqpConsumer,
                                                                TicketNonSrSettleResponseReceiver ticketNonSrSettleResponseReceiver) {
        return new AmqpTicketNonSrSettleResponseReceiverImpl(amqpConsumer, ticketNonSrSettleResponseReceiver);
    }

    @Override
    protected void configure() {
        bind(TicketSender.class).to(TicketHandler.class);
        bind(TicketResponseReceiver.class).to(TicketHandler.class);
        bind(TicketCancelSender.class).to(TicketCancelHandler.class);
        bind(TicketCancelResponseReceiver.class).to(TicketCancelHandler.class);
        bind(TicketCashoutResponseReceiver.class).to(TicketCashoutHandler.class);
        bind(TicketNonSrSettleResponseReceiver.class).to(TicketNonSrSettleHandler.class);
        bind(CustomBetManager.class).to(CustomBetManagerImpl.class).in(com.google.inject.Singleton.class);
        bind(CustomBetSelectionBuilder.class).to(CustomBetSelectionBuilderImpl.class);

        bind(SdkConfiguration.class).toInstance(sdkConfiguration);
//        bind(BuilderFactory.class).to();
    }


    /**
     * Provides the http client used to fetch data from the API
     */
    @Provides
    @Singleton
    private CloseableHttpClient provideHttpClient(){
        return HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    }

    /**
     * Provides the {@link Deserializer} used to deserialize API xmls
     *
     * @return The {@link Deserializer} instance to be registered with the DI container
     */
    @Provides @Named("ApiJaxbDeserializer")
    private Deserializer provideApiJaxbDeserializer() {
        try {
            return new DeserializerJaxbApi(apiJaxbContext.createUnmarshaller(), apiJaxbContext.createMarshaller());
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to create unmarshaller for 'api', ex: ", e);
        }
    }

    /**
     * Provides the {@link Deserializer} used to deserialize API xmls
     *
     * @return The {@link Deserializer} instance to be registered with the DI container
     */
    @Provides @Named("CustomBetApiJaxbDeserializer")
    private Deserializer provideCustomBetApiJaxbDeserializer() {
        try {
            return new DeserializerJaxbApi(customBetJaxbContext.createUnmarshaller(),  customBetJaxbContext.createMarshaller());
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to create unmarshaller for 'customBet api', ex: ", e);
        }
    }

    /**
     * Provides the {@link Deserializer} used to deserialize MTS Client API responses
     *
     * @return The {@link Deserializer} instance to be registered with the DI container
     */
    @Provides @Named("ApiJsonDeserializer")
    private Deserializer provideApiJsonDeserializer() {
            return new DeserializerJsonApi();
    }

    @Singleton
    @Provides
    @Named("MarketDescriptionCache")
    private MarketDescriptionCache provideMarketDescriptionCache(LogHttpDataFetcher logHttpDataFetcher, SdkConfiguration cfg,
                                                                 UfEnvironment ufEnvironment,
                                                                 @Named("ApiJaxbDeserializer") Deserializer deserializer) {

        String uriFormat = ufEnvironment.getHost() + "/v1/descriptions/%s/markets.xml?include_mappings=true";

        DataProvider<MarketDescriptions> dataProvider = new DataProvider<>(uriFormat, sdkConfiguration, logHttpDataFetcher, deserializer, MarketDescriptions.class);

        Cache<String, MarketDescriptionCI> invariantMarketCache = CacheBuilder.newBuilder().build();
        return new MarketDescriptionCacheImpl(invariantMarketCache, dataProvider, locales, sdkConfiguration.getAccessToken());
    }

    @Singleton
    @Provides
    @Named("MarketDescriptionProvider")
    private MarketDescriptionProvider provideMarketDescriptionProvider(@Named("MarketDescriptionCache")MarketDescriptionCache marketDescriptionCache) {

        return new MarketDescriptionProvider(marketDescriptionCache, locales);
    }

    @Singleton
    @Provides
    private BuilderFactory provideBuilderFactory(@Named("MarketDescriptionProvider")MarketDescriptionProvider marketDescriptionProvider) {

        return new BuilderFactoryImpl(sdkConfiguration, marketDescriptionProvider);
    }

    @Singleton
    @Provides
    private MtsClientApi provideMtsClientApi(CloseableHttpClient httpClient, @Named("ApiJsonDeserializer") Deserializer deserializer) {
        LogHttpDataFetcher logHttpDataFetcher = new LogHttpDataFetcher(null, httpClient);

        String keycloakHostFormat = sdkConfiguration.getKeycloakHost() + "/auth/realms/mts/protocol/openid-connect/token";
        String maxStakeUriFormat = sdkConfiguration.getMtsClientApiHost() + "/ClientApi/api/maxStake/v1";
        String ccfUriFormat = sdkConfiguration.getMtsClientApiHost() + "/ClientApi/api/ccf/v1?sourceId=%2$s";

        DataProvider<AccessTokenSchema> accessTokenDataProvider = new DataProvider<>(keycloakHostFormat, null, logHttpDataFetcher, deserializer, AccessTokenSchema.class);
        DataProvider<MaxStakeResponseSchema> maxStakeDataProvider = new DataProvider<>(maxStakeUriFormat, null, logHttpDataFetcher, deserializer, MaxStakeResponseSchema.class);
        DataProvider<CcfResponseSchema> ccfDataProvider = new DataProvider<>(ccfUriFormat, null, logHttpDataFetcher, deserializer, CcfResponseSchema.class);

        LoadingCache<String, AccessToken> accessTokenCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(240, TimeUnit.SECONDS)
                .build(new CacheLoader<String, AccessToken>() {
                    @Override
                    public AccessToken load(String key) throws Exception {
                        String[] values = key.split("\n");
                        String username = values[0];
                        String password = values[1];
                        HttpEntity content = new UrlEncodedFormEntity(Arrays.asList(
                                new BasicNameValuePair("client_id", "mts-edge-ext"),
                                new BasicNameValuePair("client_secret", sdkConfiguration.getKeycloakSecret()),
                                new BasicNameValuePair("username", username),
                                new BasicNameValuePair("password", password),
                                new BasicNameValuePair("grant_type", "password"),
                                new BasicNameValuePair("client_auth_method", "client-secret")
                        ));
                        AccessTokenSchema token = accessTokenDataProvider.getData(content);
                        if (token == null) {
                            logger.warn("Failed to authenticate.");
                            throw new Exception("Failed to authenticate.");
                        }
                        return MtsDtoMapper.map(token);
                    }
                });

        return new MtsClientApiImpl(accessTokenCache, maxStakeDataProvider, ccfDataProvider, sdkConfiguration.getKeycloakUsername(), sdkConfiguration.getKeycloakPassword());
    }

    @Provides
    private DataProvider<CAPIAvailableSelections> providesAvailableSelectionsDataProvider(SdkConfiguration cfg, UfEnvironment ufEnvironment,
                                                                                               LogHttpDataFetcher httpDataFetcher,
                                                                                               @Named("CustomBetApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                ufEnvironment.getHost() + "/v1/custombet/%2$s/available_selections",
                cfg,
                httpDataFetcher,
                deserializer,
                CAPIAvailableSelections.class);
    }

    @Provides
    private DataProvider<CAPICalculationResponse> providesCalculateProbabilityDataProvider(SdkConfiguration cfg, UfEnvironment ufEnvironment,
                                                                                                LogHttpDataFetcher httpDataFetcher,
                                                                                                @Named("CustomBetApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                ufEnvironment.getHost() + "/v1/custombet/calculate",
                cfg,
                httpDataFetcher,
                deserializer,
                CAPICalculationResponse.class);
    }

    private <T extends SdkTicket> ResponseTimeoutHandlerImpl<T> getTimeoutHandler(ScheduledExecutorService executorService, int responseTimeout) {
        Preconditions.checkNotNull(executorService);

        return new ResponseTimeoutHandlerImpl<>(
                executorService,
                responseTimeout,
                sdkConfiguration.isTicketTimeOutCallbackEnabled());
    }

    @Provides
    private UfEnvironment provideUfEnvironment(SdkConfiguration cfg, LogHttpDataFetcher httpDataFetcher) {
        if (cfg.getUfEnvironment() != null)
            return cfg.getUfEnvironment();

        if (ufEnvironment != null)
            return ufEnvironment;

        if (StringUtils.isNullOrEmpty(cfg.getAccessToken()))
            ufEnvironment = UfEnvironment.INTEGRATION;
        else {
            String response = httpDataFetcher.get(UfEnvironment.PRODUCTION.getHost() + "/v1/users/whoami.xml");
            if (StringUtils.isNullOrEmpty(response) || response.contains("FORBIDDEN"))
                ufEnvironment = UfEnvironment.INTEGRATION;
            else
                ufEnvironment = UfEnvironment.PRODUCTION;
        }

        return ufEnvironment;
    }
}
