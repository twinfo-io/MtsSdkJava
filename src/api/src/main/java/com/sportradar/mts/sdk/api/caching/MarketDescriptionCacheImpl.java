/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.caching;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sportradar.mts.api.rest.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.mts.sdk.api.rest.DataProvider;
import com.sportradar.mts.sdk.api.rest.dto.MarketDescriptionDTO;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class MarketDescriptionCacheImpl implements MarketDescriptionCache {
    private static final Logger logger = LoggerFactory.getLogger(MarketDescriptionCache.class);

    private final Cache<String, MarketDescriptionCI> cache;
    private final DataProvider<MarketDescriptions> dataProvider;
    private final List<Locale> prefetchLocales;
//    private final List<Locale> fetchedLocales;
    private final Object lock = new Object();
    private final boolean accessTokenProvided;
    private static Duration duration;
    private Date timeOfLastFetch;
    private static final Duration minIntervalTimeout = Duration.ofSeconds(30);

    public MarketDescriptionCacheImpl(Cache<String, MarketDescriptionCI> cache,
                                       DataProvider<MarketDescriptions> dataProvider,
                                       List<Locale> prefetchLocales,
                                       String accessToken) {
        this.cache = cache;
        this.dataProvider = dataProvider;
        this.prefetchLocales = prefetchLocales;
//        this.fetchedLocales = new ArrayList<>();

        duration = Duration.ofHours(4);
        timeOfLastFetch = new Date(0);

        accessTokenProvided = !StringUtils.isNullOrEmpty(accessToken);

        logger.debug("AccessToken for API is provided: " + accessTokenProvided + ". It is required only when creating selections for UF markets via method ISelectionBuilder.SetIdUof(). There is no need for it when legacy feeds are used.");
    }

    private MarketDescriptionCI GetItemFromCache(int id)
    {
        return cache.getIfPresent(Integer.toString(id));
    }

    /**
     * Calculates and returns the missing locales within the provided {@link List}
     *
     * @param have - a {@link List} that contains all the available locales
     * @param want - a {@link List} of locales that are required
     * @return - returns a {@link List} of missing locales
     */
    private static List<Locale> findMissingLocales(List<Locale> have, List<Locale> want) {
        Preconditions.checkNotNull(have);
        Preconditions.checkNotNull(want);

        return want.stream().filter(locale -> !have.contains(locale)).collect(Collectors.toList());
    }

    private void getAllMarketDescriptions(List<Locale> locales) {
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        if(!accessTokenProvided)
        {
            throw new IllegalArgumentException("Missing AccessToken.");
        }

        try {
            synchronized (lock) {
                for (Locale locale : locales) {
                    logger.debug("Fetching market descriptions from API for locale: " + locale);
                    MarketDescriptions marketDescriptions = dataProvider.getData(locale);
                    if(marketDescriptions == null)
                    {
                        logger.warn("No market descriptions fetched from API for locale: " + locale);
                        pauseFetching();
                        return;
                    }

                    List<MarketDescriptionDTO> marketDescriptionDTOs = new ArrayList<>();
                    marketDescriptions.getMarket().forEach(market -> marketDescriptionDTOs.add(new MarketDescriptionDTO(market)));

                    for(MarketDescriptionDTO dto : marketDescriptionDTOs) {
                        merge(locale, dto);
                    }
//                    fetchedLocales.add(locale);
                }
            }
            timeOfLastFetch = new Date();
        } catch (Exception ex) {
            pauseFetching();
            logger.error("An error occurred while fetching market descriptions data", ex);
            throw new IllegalStateException("An error occurred while fetching market descriptions data", ex);
        }
    }

    private void pauseFetching()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -(int)duration.getSeconds());
        cal.add(Calendar.SECOND, (int)minIntervalTimeout.getSeconds());
        timeOfLastFetch = cal.getTime();
        logger.debug("Fetching paused for " + minIntervalTimeout.getSeconds() + "s.");
    }

    private void merge(Locale locale, MarketDescriptionDTO dto) {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(dto);

        String processingCacheItemId = String.valueOf(dto.getId());
        MarketDescriptionCI cachedItem = cache.getIfPresent(processingCacheItemId);
        if (cachedItem == null) {
            try {
                cachedItem = new MarketDescriptionCI(dto, locale);
            }
            catch (Exception ex)
            {
                logger.error("Error during creating MarketDescriptionCI", ex);
                cachedItem = new MarketDescriptionCI(dto, locale);
            }
            cache.put(processingCacheItemId, cachedItem);
        } else {
            cachedItem.merge(dto, locale);
        }
    }

    private MarketDescriptionCI getMarketDescription(int marketId, List<Locale> locales) {
        Preconditions.checkArgument(marketId > 0);

        MarketDescriptionCI cachedItem = cache.getIfPresent(String.valueOf(marketId));

        if (cachedItem == null) {
            logger.error("The requested market id was not found");
            throw new IllegalArgumentException("The requested market id was not found");
        }

        return cachedItem;
    }

    public MarketDescriptionCI getMarketDescription(int marketId) {
        if (Duration.between(timeOfLastFetch.toInstant(), Instant.now()).toMillis() > duration.toMillis())
        {
//            fetchedLocales.clear();
            getAllMarketDescriptions(prefetchLocales);
        }
        return getMarketDescription(marketId, prefetchLocales);
    }
}