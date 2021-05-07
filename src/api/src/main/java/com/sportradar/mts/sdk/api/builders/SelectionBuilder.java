/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.Selection;

import java.util.Map;

/**
 * Builder used to create a new instance of {@link Selection}
 */
public interface SelectionBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static SelectionBuilder create() { return null; }

    /**
     * Sets the Betradar event (match or outright) id
     * @param eventId event id
     * @return current builder reference
     */
    SelectionBuilder setEventId(String eventId);

    /**
     * Sets the selection id
     * @param id selection id should be composed according to MTS specification
     * @return current builder reference
     */
    SelectionBuilder setId(String id);

    /**
     * Sets the selection id for LiveOdds
     * @param type type
     * @param subType subType
     * @param sov special odds value
     * @param selectionId selection id
     * @return current builder reference
     */
    SelectionBuilder setIdLo(int type, int subType, String sov, String selectionId);

    /**
     * Sets the selection id for LCOO
     * @param type type
     * @param sportId sport id
     * @param sov special odds value
     * @param selectionId selection id
     * @return current builder reference
     */
    SelectionBuilder setIdLcoo(int type, int sportId, String sov, String selectionId);

    /**
     * Sets the selection id for UOF
     * Note: method requires accessToken in configuration and access to https://api.betradar.com
     * @param product product
     * @param sportId sport id
     * @param marketId market id
     * @param selectionId selection id
     * @param specifiers the array of specifiers represented as string separated with '|'  (example: "total=3.0|playerid=sr:player:10201")
     * @param sportEventStatus sport event statuses
     * @return current builder reference
     */
    SelectionBuilder setIdUof(int product, String sportId, int marketId, String selectionId, String specifiers, Map<String, Object> sportEventStatus);

    /**
     * Sets the selection id for UOF
     * Note: method requires accessToken in configuration and access to https://api.betradar.com
     * @param product product
     * @param sportId sport id
     * @param marketId market id
     * @param selectionId selection id
     * @param specifiers specifiers
     * @param sportEventStatus sport event statuses
     *
     * @return current builder reference
     */
    SelectionBuilder setIdUof(int product, String sportId, int marketId, String selectionId, Map<String, String> specifiers, Map<String, Object> sportEventStatus);

    /**
     * Sets the odds multiplied by 10000 and rounded to int value
     * @param odds odds
     * @return current builder reference
     */
    SelectionBuilder setOdds(int odds);

    /**
     * Sets the banker property
     * @param isBanker banker
     * @return current builder reference
     */
    SelectionBuilder setBanker(boolean isBanker);

    /**
     * Sets the {@link Selection} properties
     * @param eventId event id
     * @param id selection id should be composed according to MTS specification
     * @param odds odds value
     * @param isBanker is banker value
     * @return current builder reference
     */
    SelectionBuilder set(String eventId, String id, Integer odds, boolean isBanker);

    /**
     * Builds new {@link Selection} instance
     * @return new {@link Selection} instance
     */
    Selection build();
}