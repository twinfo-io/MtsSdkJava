/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.*;
import com.sportradar.mts.sdk.api.utils.MtsTicketHelper;

import java.util.List;

public class BetImpl implements Bet {

    private final BetBonus bonus;
    private final BetFreeStake freeStake;
    private final Stake stake;
    private final Stake entireStake;
    private final String id;
    private final List<Integer> selectedSystems;
    private final List<Selection> selections;
    private final String reofferRefId;
    private final Long sumOfWins;
    private final Boolean customBet;
    private final Integer calculationOdds;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BetImpl(@JsonProperty("id") String id,
                   @JsonProperty("betBonus") BetBonus betBonus,
                   @JsonProperty("betFreeStake") BetFreeStake betFreeStake,
                   @JsonProperty("stake") Stake stake,
                   @JsonProperty("entireStake") Stake entireStake,
                   @JsonProperty("selectedSystems") List<Integer> selectedSystems,
                   @JsonProperty("selections") List<Selection> selections,
                   @JsonProperty("reofferRefId") String reofferRefId,
                   @JsonProperty("sumOfWins") Long sumOfWins,
                   @JsonProperty("customBet") Boolean customBet,
                   @JsonProperty("calculationOdds") Integer calculationOdds) {
        Preconditions.checkArgument(MtsTicketHelper.validateTicketId(id), "betId is not valid");
        Preconditions.checkNotNull(selections, "selections cannot be null");
        Preconditions.checkArgument(!selections.isEmpty(), "missing selections, at least one selection must be specified");
        Preconditions.checkArgument(selections.size() == selections.stream().distinct().count(), "selection can not be repeated");
        Preconditions.checkArgument(!selectedSystems.isEmpty(), "missing selectedSystems, at least one selectedSystems must be specified");
        Preconditions.checkArgument(selectedSystems.size() == selectedSystems.stream().distinct().count(), "selectedSystems can not be repeated");
        Preconditions.checkArgument(selectedSystems.stream().allMatch(a->a > 0), "selectedSystems - 0 is not valid value");
        Preconditions.checkArgument(selectedSystems.stream().allMatch(a->a <= selections.size()), "selectedSystems contains invalid value");
        Preconditions.checkNotNull(stake, "stake cannot be null");
        Preconditions.checkArgument(reofferRefId == null || (MtsTicketHelper.validateTicketId(reofferRefId) && reofferRefId.length() <= 50), "not valid reofferRefId");
        Preconditions.checkArgument(sumOfWins == null || sumOfWins >= 0, "sumOfWins not valid");
        boolean customBetBool = Boolean.TRUE.equals(customBet);
        Preconditions.checkArgument((customBetBool && calculationOdds != null && calculationOdds >= 0) || (!customBetBool && calculationOdds == null), "calculationOdds not valid");

        this.id = id;
        this.bonus = betBonus;
        this.freeStake = betFreeStake;
        this.stake = stake;
        this.entireStake = entireStake;
        this.selectedSystems = selectedSystems;
        this.selections = selections;
        this.reofferRefId = reofferRefId;
        this.sumOfWins = sumOfWins;
        this.customBet = customBet;
        this.calculationOdds = calculationOdds;
    }

    @Override
    public String getId() { return id; }

    /**
     * Gets selections
     *
     * @return selections
     */
    @Override
    public List<Selection> getSelections() {
        return selections;
    }

    @Override
    public String getReofferRefId() { return reofferRefId; }

    @Override
    public Long getSumOfWins() { return sumOfWins; }

    @Override
    public Boolean getCustomBet() {
        return customBet;
    }

    @Override
    public Integer getCalculationOdds() {
        return calculationOdds;
    }

    @Override
    public BetBonus getBetBonus() { return bonus; }

    @Override
    public BetFreeStake getBetFreeStake() {
        return freeStake;
    }

    @Override
    public Stake getStake() {
        return stake;
    }

    @Override
    public Stake getEntireStake() {
        return entireStake;
    }

    @Override
    public List<Integer> getSelectedSystems() { return selectedSystems; }

    @Override
    public String toString() {
        return "BetImpl{" +
                "id='" + id + '\'' +
                ", betBonus=" + bonus +
                ", betFreeStake=" + freeStake +
                ", selections=" + selections +
                ", stake=" + stake +
                ", entireStake=" + entireStake +
                ", system=" + selectedSystems +
                ", reofferRefId=" + reofferRefId +
                ", sumOfWins=" + sumOfWins +
                ", customBet=" + customBet +
                ", calculationOdds=" + calculationOdds +
                '}';
    }
}
