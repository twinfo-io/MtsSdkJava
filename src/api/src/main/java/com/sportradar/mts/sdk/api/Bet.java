/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import java.io.Serializable;
import java.util.List;

/**
 * Ticket bet
 */
public interface Bet extends Serializable {

    /**
     * Gets the bet bonus
     * @return bet bonus
     */
    BetBonus getBetBonus();

    /**
     * Gets the stake
     * Mandatory
     * @return stake
     */
    Stake getStake();

    /**
     * Gets the entire stake
     * @return entire stake
     */
    Stake getEntireStake();

    /**
     * Gets the bet id
     * @return bet id
     */
    String getId();

    /**
     * Gets the selected systems
     * Array of all the systems (mandatory, [0] is not allowed, use [fold] instead)
     * Mandatory
     * @return system
     */
    List<Integer> getSelectedSystems();

    /**
     * Gets the array of selection which form the bet
     * @return system
     */
    List<Selection> getSelections();

    /**
     * Gets reoffer reference bet id
     * @return reofferRefId
     */
    String getReofferRefId();

    /**
     * Gets the sum of all wins for all generated combinations for this bet (in ticket currency, used in validation)
     * @return sum of wins
     */
    Long getSumOfWins();

    /**
     * Gets the flag if bet is a custom bet (optional, default false)
     * @return flag if bet is a custom bet
     */
    Boolean getCustomBet();

    /**
     * Gets the odds calculated for custom bet multiplied by 10_000 and rounded to int value
     * @return odds calculated for custom bet
     */
    Integer getCalculationOdds();
}
