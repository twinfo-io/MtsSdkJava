/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.Bet;
import com.sportradar.mts.sdk.api.Selection;
import com.sportradar.mts.sdk.api.enums.*;
import com.sportradar.mts.sdk.api.impl.builders.BetBuilderImpl;

import java.util.List;

/**
 * Builder for creating new {@link Bet} instances
 */
public interface BetBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static BetBuilder create()
    {
        return new BetBuilderImpl();
    }

    /**
     * Sets the BetBonus
     * @param value The quantity multiplied by 10000 and rounded to a long value
     * @return current builder reference
     */
    BetBuilder setBetBonus(long value);

    /**
     * Sets the BetBonus
     * @param value The quantity multiplied by 10000 and rounded to a long value
     * @param betBonusMode bet bonus mode
     * @param betBonusType bet bonus type
     * @param betBonusDescription bet bonus description
     * @param betBonusPaidAs bet bonus payment type
     * @return current builder reference
     */
    BetBuilder setBetBonus(long value, BetBonusMode betBonusMode, BetBonusType betBonusType, BetBonusDescription betBonusDescription, BetBonusPaidAs betBonusPaidAs);

    /**
     * Sets the Stake
     * @param value The quantity multiplied by 10000 and rounded to a long value
     * @param stakeType type of stake
     * @return current builder reference
     */
    BetBuilder setStake(long value, StakeType stakeType);

    /**
     * Sets the entire stake
     * @param value The quantity multiplied by 10000 and rounded to a long value
     * @param stakeType type of stake
     * @return current builder reference
     */
    BetBuilder setEntireStake(long value, StakeType stakeType);

    /**
     * Sets the bet id
     * @param id bet id
     * @return current builder reference
     */
    BetBuilder setBetId(String id);

    /**
     * Add a system to selected system array
     * @param systemId system id to add
     * @return current builder reference
     */
    BetBuilder addSelectedSystem(int systemId);

    /**
     * Gets the array of selected systems
     * @return array of int
     */
    List<Integer> getSelectedSystems();

    /**
     * Sets the reoffer reference bet id
     * @param reofferId reoffer id
     * @return current builder reference
     */
    BetBuilder setReofferId(String reofferId);

    /**
     * Sets the sum of all wins for all generated combinations for this bet (in ticket currency, used for validation)
     * @param sum sum
     * @return current builder reference
     */
    BetBuilder setSumOfWins(Long sum);

    /**
     * Adds the selection
     * @param selection selection to be added to this bet
     * @return current builder reference
     */
    BetBuilder addSelection(Selection selection);

    /**
     * Gets the bet selections
     * @return array of Selections
     */
    List<Selection> getSelections();

    /**
     * Sets the flag if bet is a custom bet (optional, default false)
     * @param customBet flag if bet is a custom bet
     * @return current builder reference
     */
    BetBuilder setCustomBet(Boolean customBet);

    /**
     * Sets the odds calculated for custom bet multiplied by 10_000 and rounded to int value
     * @param calculationOdds odds calculated for custom bet
     * @return current builder reference
     */
    BetBuilder setCalculationOdds(Integer calculationOdds);

    /**
     * Creates new {@link Bet} instance
     * @return new {@link Bet} instance
     */
    Bet build();
}
