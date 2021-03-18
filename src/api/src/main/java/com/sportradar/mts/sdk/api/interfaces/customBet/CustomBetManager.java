/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces.customBet;

import com.sportradar.mts.sdk.api.exceptions.CustomBetException;
import com.sportradar.mts.sdk.api.rest.URN;

import java.util.List;

/**
 * Defines methods used to perform various custom bet operations
 */
public interface CustomBetManager {
    /**
     * Returns an {@link AvailableSelections} instance providing the available selections
     * for the event associated with the provided {@link URN} identifier
     *
     * @param eventId the {@link URN} identifier of the event for which the available selections should be returned
     * @return an {@link AvailableSelections} providing the the available selections of the associated event
     * @throws CustomBetException throws bet exception
     */
    AvailableSelections getAvailableSelections(URN eventId) throws CustomBetException;

    /**
     * Returns an {@link Calculation} instance providing the probability for the specified selections
     *
     * @param selections the {@link Selection} containing selections for which the probability should be calculated
     * @return an {@link Calculation} providing the the probability for the specified selections
     * @throws CustomBetException throws bet exception
     */
    Calculation calculateProbability(List<Selection> selections) throws CustomBetException;

    /**
     * Returns an {@link CustomBetSelectionBuilder} instance used to build selections
     *
     * @return an {@link CustomBetSelectionBuilder} instance used to build selections
     */
    CustomBetSelectionBuilder getCustomBetSelectionBuilder();
}
