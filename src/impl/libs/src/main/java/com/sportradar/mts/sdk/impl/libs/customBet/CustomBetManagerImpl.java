/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.customBet;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPICalculationResponse;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPISelections;
import com.sportradar.mts.sdk.api.exceptions.CustomBetException;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.interfaces.customBet.*;
import com.sportradar.mts.sdk.api.rest.DataProvider;
import com.sportradar.mts.sdk.api.rest.Deserializer;
import com.sportradar.mts.sdk.api.rest.URN;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The basic implementation of the {@link CustomBetManager}
 */
public class CustomBetManagerImpl implements CustomBetManager {
    private static final Logger logger = LoggerFactory.getLogger(CustomBetManagerImpl.class);

    private final DataProvider<CAPIAvailableSelections> availableSelectionsDataProvider;
    private final DataProvider<CAPICalculationResponse> calculationResponseDataProvider;
    private final Deserializer deserializer;

    @Inject
    CustomBetManagerImpl(SdkConfiguration configuration,
                         DataProvider<CAPIAvailableSelections> availableSelectionsDataProvider,
                         DataProvider<CAPICalculationResponse> calculationResponseDataProvider,
                         @Named("CustomBetApiJaxbDeserializer") Deserializer deserializer) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(availableSelectionsDataProvider);
        Preconditions.checkNotNull(calculationResponseDataProvider);
        Preconditions.checkNotNull(deserializer);

        this.availableSelectionsDataProvider = availableSelectionsDataProvider;
        this.calculationResponseDataProvider = calculationResponseDataProvider;
        this.deserializer = deserializer;
    }

    @Override
    public AvailableSelections getAvailableSelections(URN eventId) throws CustomBetException {
        Preconditions.checkNotNull(eventId);

        try {
            logger.info("Called getAvailableSelections with eventId={}.", eventId);
            CAPIAvailableSelections availableSelections = availableSelectionsDataProvider.getData(eventId.toString());
            if (availableSelections == null)
                throw new Exception("Failed to fetch available selections result.");
            return new AvailableSelectionsImpl(availableSelections);
        } catch (Exception e) {
            logger.warn("Getting available selections for eventId={} failed.", eventId);
            throw new CustomBetException("Getting available selections failed", e);
        }
    }

    @Override
    public Calculation calculateProbability(List<Selection> selections) throws CustomBetException {
        Preconditions.checkNotNull(selections);

        try {
            logger.info("Called calculateProbability with selections={}.", selections);
            CAPISelections content = MtsDtoMapper.map(selections);
            String c = deserializer.serialize(content);
            StringEntity entity = new StringEntity(c, ContentType.APPLICATION_XML);
            CAPICalculationResponse calculationResponse = calculationResponseDataProvider.postData(entity);
            if (calculationResponse == null)
                throw new Exception("Failed to fetch calculation response.");
            return new CalculationImpl(calculationResponse);
        } catch (Exception e) {
            logger.warn("Getting calculation response for selections={} failed.", selections);
            throw new CustomBetException("Getting calculation response failed" ,e);
        }
    }

    @Override
    public CustomBetSelectionBuilder getCustomBetSelectionBuilder() {
        return new CustomBetSelectionBuilderImpl();
    }
}
