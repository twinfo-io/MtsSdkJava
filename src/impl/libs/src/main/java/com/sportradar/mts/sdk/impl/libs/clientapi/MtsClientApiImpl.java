/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.clientapi;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;
import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.Ccf;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.exceptions.MtsApiException;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.CcfResponseSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.MaxStakeResponseSchema;
import com.sportradar.mts.sdk.api.interfaces.MtsClientApi;
import com.sportradar.mts.sdk.api.rest.DataProvider;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class MtsClientApiImpl implements MtsClientApi {
    private static final Logger logger = LoggerFactory.getLogger(MtsClientApiImpl.class);

    private final LoadingCache<String, AccessToken> accessTokenCache;
    private final DataProvider<MaxStakeResponseSchema> maxStakeDataProvider;
    private final DataProvider<CcfResponseSchema> ccfDataProvider;
    private final String username;
    private final String password;

    public MtsClientApiImpl(LoadingCache<String, AccessToken> accessTokenCache, DataProvider<MaxStakeResponseSchema> maxStakeDataProvider, DataProvider<CcfResponseSchema> ccfDataProvider, String keycloakUsername, String keycloakPassword) {
        this.accessTokenCache = accessTokenCache;
        this.maxStakeDataProvider = maxStakeDataProvider;
        this.ccfDataProvider = ccfDataProvider;
        this.username = keycloakUsername;
        this.password = keycloakPassword;
    }

    @Override
    public long getMaxStake(Ticket ticket) throws MtsApiException {
        return getMaxStake(ticket, username, password);
    }

    @Override
    public long getMaxStake(Ticket ticket, String username, String password) throws MtsApiException {
        Preconditions.checkNotNull(ticket);
        Preconditions.checkNotNull(username);
        Preconditions.checkNotNull(password);

        try {
            logger.info("Called getMaxStake with ticketId={}.", ticket.getTicketId());
            AccessToken token = accessTokenCache.get(getCacheKey(username, password));
            HttpEntity content = new StringEntity(ticket.getJsonValue(), ContentType.APPLICATION_JSON);
            Long result = MtsDtoMapper.map(maxStakeDataProvider.postData(token, content));
            if (result == null) {
                throw new MtsApiException("Failed to get max stake result.");
            }
            return result;
        } catch (ExecutionException e) {
            logger.warn("Getting max stake for ticketId={} failed.", ticket.getTicketId());
            throw new MtsApiException(e.getCause().getMessage());
        }
    }

    @Override
    public Ccf getCcf(String sourceId) throws MtsApiException {
        return getCcf(sourceId, username, password);
    }

    @Override
    public Ccf getCcf(String sourceId, String username, String password) throws MtsApiException {
        Preconditions.checkNotNull(sourceId);
        Preconditions.checkNotNull(username);
        Preconditions.checkNotNull(password);

        try {
            logger.info("Called getCcf with sourceId={}.", sourceId);
            AccessToken token = accessTokenCache.get(getCacheKey(username, password));
            Ccf result = MtsDtoMapper.map(ccfDataProvider.getData(token, sourceId));
            if (result == null) {
                throw new MtsApiException("Failed to get ccf result.");
            }
            return result;
        } catch (ExecutionException e) {
            logger.warn("Getting ccf for sourceId={} failed.", sourceId);
            throw new MtsApiException(e.getCause().getMessage());
        }
    }

    private String getCacheKey(String username, String password) {
        return username + "\n" + password;
    }
}
