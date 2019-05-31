/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.adapters.amqp;

import com.google.common.collect.ImmutableSet;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public final class ChannelFactory {
    private final static String TLS_VERSION = "TLSv1.2";
    private final static String ENDPOINT_IDENTIFICATION_ALGORITHM = "HTTPS";

    private final static Set<String> TLS_VERIFICATION_IGNORE_LIST = ImmutableSet.of(
            "91.201.213.134", // Client integration environment
            "mtsgate-ci.betradar.com",
            "91.201.212.86", // Production environment
            "mtsgate-t1.betradar.com"
    );

    private static final Logger logger = LoggerFactory.getLogger(ChannelFactory.class);

    private final ConnectionWrapper connectionWrapper;

    ChannelFactory(final AmqpCluster mqCluster, final ChannelFactoryProviderImpl channelFactoryProvider) throws Exception {
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setThreadFactory(channelFactoryProvider.getAmqpThreadFactory());

        connectionFactory.setPassword(mqCluster.getPassword());
        connectionFactory.setUsername(mqCluster.getUsername());
        connectionFactory.setVirtualHost(mqCluster.getVhost());

        if (mqCluster.useSslProtocol() && shouldUseTlsValidation(mqCluster.getAddresses())) {
            connectionFactory.setSocketConfigurator(socket -> {
                socket.setTcpNoDelay(true);
                if (socket instanceof SSLSocket) {
                    SSLSocket sslSocket = (SSLSocket) socket;
                    SSLParameters sslParameters = sslSocket.getSSLParameters();
                    sslParameters.setEndpointIdentificationAlgorithm(ENDPOINT_IDENTIFICATION_ALGORITHM);
                    sslSocket.setSSLParameters(sslParameters);
                }
            });

            connectionFactory.useSslProtocol(TLS_VERSION, getDefaultTrustManager());
        } else if (mqCluster.useSslProtocol()) {
            // some clients might be having issues with the validation for now,
            // because they might be using direct IPs
            connectionFactory.useSslProtocol();
        }

        // Keep sending the heartbeat every 45 seconds to prevent any routers from considering the connection stale.
        connectionFactory.setRequestedHeartbeat(45);

        this.connectionWrapper = new ConnectionWrapper(channelFactoryProvider, connectionFactory, mqCluster);
    }

    public ChannelWrapper getChannel() throws IOException, TimeoutException {
        return this.connectionWrapper.getChannel();
    }

    private static X509TrustManager getDefaultTrustManager() throws GeneralSecurityException {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers != null) {
                for (TrustManager trustManager : trustManagers) {
                    if (trustManager instanceof X509TrustManager) {
                        return (X509TrustManager) trustManager;
                    }
                }
            }
        } catch (NullPointerException ex) {
            throw new GeneralSecurityException("cannot get default trust manager", ex);
        }

        throw new GeneralSecurityException("cannot get default trust manager");
    }

    private boolean shouldUseTlsValidation(NetworkAddress[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return true;
        }

        for (NetworkAddress address : addresses) {
            String addressToBeValidated = address.getHost();
            if (TLS_VERIFICATION_IGNORE_LIST.contains(addressToBeValidated)) {
                return false;
            }
        }

        return true;
    }
}
