/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.Selection;
import com.sportradar.mts.sdk.api.builders.SelectionBuilder;
import com.sportradar.mts.sdk.api.caching.MarketDescriptionCI;
import com.sportradar.mts.sdk.api.caching.MarketDescriptionProvider;
import com.sportradar.mts.sdk.api.caching.MarketMappingCI;
import com.sportradar.mts.sdk.api.impl.SelectionImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.rest.URN;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of SelectionBuilder
 */
public class SelectionBuilderImpl implements SelectionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SelectionBuilderImpl.class);

    private final MarketDescriptionProvider marketDescriptionProvider;
    private final SdkConfiguration config;
    private String eventId;
    private String selectionId;
    private int odds;
    private boolean isBanker;
    private boolean isCustomBet;

    public SelectionBuilderImpl(MarketDescriptionProvider marketDescriptionProvider, SdkConfiguration config, boolean isCustomBet) {
        Preconditions.checkNotNull(marketDescriptionProvider);
        Preconditions.checkNotNull(config);

        this.marketDescriptionProvider = marketDescriptionProvider;
        this.config = config;
        this.isCustomBet = isCustomBet;
    }

    @Override
    public SelectionBuilder setEventId(String eventId) {
        this.eventId = eventId;
        ValidateData(false, true, false);
        return this;
    }

    @Override
    public SelectionBuilder setId(String selectionId) {
        this.selectionId = selectionId;
        ValidateData(true, false, false);
        return this;
    }

    @Override
    public SelectionBuilder setIdLo(int type, int subType, String sov, String selectionIds) {
        Preconditions.checkArgument(type > 0, "type is missing");
        if (subType < 0)
        {
            subType = 0;
        }
        if (StringUtils.isNullOrEmpty(sov))
        {
            sov = "*";
        }
        selectionId = String.format("live:%s/%s/%s", type, subType, sov);
        if (!StringUtils.isNullOrEmpty(selectionIds))
        {
            selectionId += "/" + selectionIds;
        }
        ValidateData( true, false, false);
        return this;
    }

    @Override
    public SelectionBuilder setIdLcoo(int type, int sportId, String sov, String selectionIds) {
        Preconditions.checkArgument(type > 0, "type is missing");
        Preconditions.checkArgument(sportId > 0, "sportId is missing");

        if (StringUtils.isNullOrEmpty(sov))
        {
            sov = "*";
        }
        selectionId = String.format("lcoo:%s/%s/%s", type, sportId, sov);
        if (!StringUtils.isNullOrEmpty(selectionIds))
        {
            selectionId += "/" + selectionIds;
        }
        ValidateData(true, false, false);
        return this;
    }

    @Override
    public SelectionBuilder setIdUof(int product, String sportId, int marketId, String selectionIds, String specifiers, Map<String, Object> sportEventStatus) {
        Preconditions.checkArgument(!StringUtils.isNullOrEmpty(sportId), "sportId is missing");
        Preconditions.checkArgument(marketId > 0, "marketId is missing");

        Map<String, String> specs = new HashMap<>();
        if(!StringUtils.isNullOrEmpty(specifiers))
        {
            for (String spec : specifiers.split("\\|"))
            {
                String[] s = spec.split("=");
                specs.putIfAbsent(s[0], s[1]);
            }
        }
        return setIdUof(product, sportId, marketId, selectionIds, specs, sportEventStatus);
    }

    @Override
    public SelectionBuilder setIdUof(int product, String sportId, int marketId, String selectionIds, Map<String, String> specifiers, Map<String, Object> sportEventStatus) {
        Preconditions.checkArgument(!StringUtils.isNullOrEmpty(sportId), "sportId is missing");
        Preconditions.checkArgument(marketId > 0, "marketId is missing");

        if (!sportId.contains(":"))
        {
            throw new IllegalArgumentException("SportId is not valid. Must be similar to 'sr:sport:1'.");
        }
        if (product < 1)
        {
            throw new IllegalArgumentException("Product is not valid.");
        }

        selectionId = String.format("uof:%s/%s/%s", product, sportId, marketId);
        if (!StringUtils.isNullOrEmpty(selectionIds))
        {
            selectionId += "/" + selectionIds;
        }
        Map<String, String> newSpecifiers = HandleMarketDescription(product, sportId, marketId, selectionIds, specifiers, sportEventStatus);
        if (specifiers != null && !specifiers.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            specifiers.forEach((key, value) -> sb.append("&")
                    .append(key)
                    .append("=")
                    .append(value));
            selectionId += "?" + sb.toString().substring(1);
        }
        ValidateData(true, false, false);
        return this;
    }

    @Override
    public SelectionBuilder setOdds(int odds) {
        this.odds = odds;
        ValidateData(false, false, true && !isCustomBet);
        return this;
    }

    @Override
    public SelectionBuilder setBanker(boolean isBanker) {
        this.isBanker = isBanker;
        return this;
    }

    @Override
    public SelectionBuilder set(String eventId, String selectionId, int odds, boolean isBanker) {
        this.eventId = eventId;
        this.selectionId = selectionId;
        this.odds = odds;
        this.isBanker = isBanker;
        ValidateData(true, true, true && !isCustomBet);
        return this;
    }

    @Override
    public Selection build() {
        ValidateData(true, true, true && !isCustomBet);
        return new SelectionImpl(eventId, selectionId, odds, isBanker);
    }

    private void ValidateData(boolean id, boolean eventId, boolean odds)
    {
        if (id)
        {
            if (StringUtils.isNullOrEmpty(this.selectionId) || !MtsTicketHelper.validateId(this.selectionId, false, false, 1, 1000))
            {
                throw new IllegalArgumentException("Id not valid.");
            }
        }
        if (eventId)
        {
            if (StringUtils.isNullOrEmpty(this.eventId) || !MtsTicketHelper.validateId(this.eventId, false, false,1, 100))
            {
                throw new IllegalArgumentException("EventId not valid.");
            }
        }
        if (odds)
        {
            if (!(this.odds >= 10000 && this.odds <= 1000000000))
            {
                throw new IllegalArgumentException("Odds not valid.");
            }
        }
    }

    private Map<String, String> HandleMarketDescription(int productId, String sportId, int marketId, String selectionId, Map<String, String> specifiers, Map<String, Object> sportEventStatus)
    {
        if(!this.config.getProvideAdditionalMarketSpecifiers())
        {
            return specifiers;
        }

        MarketDescriptionCI marketDescriptionCI = null;
        try
        {
            marketDescriptionCI = marketDescriptionProvider.getMarketDescription(marketId, null);
        }
        catch (Exception ignored)
        {
            // logged on MarketDescriptionProvider level
        }

        if(marketDescriptionCI == null)
        {
            logger.info(String.format("No market description found for marketId={}, sportId={}, productId={}."), marketId, sportId, productId);
            return specifiers;
        }

        //handle market 215
        if(marketId == 215)
        {
            Map<String, String> newSpecifiers = new HashMap<>();
            if(specifiers != null && specifiers.size() > 0)
            {
                newSpecifiers = specifiers;
            }
            if(sportEventStatus == null)
            {
                throw new IllegalArgumentException("SportEventStatus is missing");
            }
            newSpecifiers.put("$server", sportEventStatus.get("CurrentServer").toString());
            return newSpecifiers;
        }

        MarketMappingCI marketMapping = marketDescriptionCI.getMappings().stream()
                .filter(f ->
                        f.getProducerId() == productId &&
                                f.getSportId() != null &&
                                f.getSportId().equals(URN.parse(sportId)))
                .findFirst()
                .orElse(null);
        if(marketMapping == null || marketMapping.getProducerId() == 0)
        {
            logger.info(String.format("Market description {}, has no mapping."), marketDescriptionCI.getId(), sportId, productId);
            return specifiers;
        }

        //handle $score
        if(!StringUtils.isNullOrEmpty(marketMapping.getSovTemplate()) && marketMapping.getSovTemplate().equals("{$score}"))
        {
            if(sportEventStatus == null)
            {
                throw new IllegalArgumentException("SportEventStatus is missing");
            }
            if(!sportEventStatus.containsKey("HomeScore"))
            {
                throw new IllegalArgumentException("SportEventStatus is missing HomeScore property");
            }
            if(!sportEventStatus.containsKey("AwayScore"))
            {
                throw new IllegalArgumentException("SportEventStatus is missing AwayScore property");
            }

            Map<String, String> newSpecifiers = new HashMap<>();
            if(specifiers != null && specifiers.size() > 0)
            {
                newSpecifiers = specifiers;
            }
            newSpecifiers.put("$score", sportEventStatus.get("HomeScore") + ":" + sportEventStatus.get("AwayScore"));

            return newSpecifiers;
        }

        return specifiers;
    }
}
