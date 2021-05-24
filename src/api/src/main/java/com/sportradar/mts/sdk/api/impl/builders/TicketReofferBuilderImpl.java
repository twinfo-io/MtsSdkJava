/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl.builders;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.*;
import com.sportradar.mts.sdk.api.builders.BetBuilder;
import com.sportradar.mts.sdk.api.builders.SimpleBuilderFactory;
import com.sportradar.mts.sdk.api.builders.TicketBuilder;
import com.sportradar.mts.sdk.api.builders.TicketReofferBuilder;
import com.sportradar.mts.sdk.api.utils.StringUtils;

import java.util.Optional;

/**
 * Implementation of {@link TicketReofferBuilder}
 */
public class TicketReofferBuilderImpl implements TicketReofferBuilder {

    private Ticket ticket;
    private TicketResponse ticketResponse;
    private String newTicketId;
    private long newStake;
    private final SimpleBuilderFactory builderFactory;

    public TicketReofferBuilderImpl()
    {
        builderFactory = new SimpleBuilderFactoryImpl();
    }

    public TicketReofferBuilderImpl(SimpleBuilderFactory builderFactory)
    {
        Preconditions.checkNotNull(builderFactory);
        this.builderFactory = builderFactory;
    }

    @Override
    public TicketReofferBuilder set(Ticket ticket, TicketResponse ticketResponse, String newTicketId) {
        this.ticket = ticket;
        this.ticketResponse = ticketResponse;
        this.newTicketId = newTicketId;
        return this;
    }

    @Override
    public TicketReofferBuilder set(Ticket ticket, long newStake, String newTicketId) {
        this.ticket = ticket;
        this.newStake = newStake;
        this.newTicketId = newTicketId;
        return this;
    }

    @Override
    public Ticket build() {
        return ticketResponse != null
                ? buildReofferTicket(ticket, ticketResponse, newTicketId)
                : buildReofferTicket(ticket, newStake, newTicketId);
    }

    /**
     * Builds the reoffer ticket based on the original ticket and the ticket response
     * @param ticket the original ticket
     * @param ticketResponse the ticket response from which the stake info will be used
     * @param newTicketId the new reoffer ticket id
     * @return Returns the {@link Ticket} representing the reoffer
     * @exception IllegalArgumentException ticket and ticketResponse are mandatory
     */
    private Ticket buildReofferTicket(Ticket ticket, TicketResponse ticketResponse, String newTicketId)
    {
        checkArgsForReofferTicket(ticket, ticketResponse);

        if (ticket.getBets().size() == 1)
        {
            return buildReofferTicket(ticket, ticketResponse.getBetDetails().get(0).getReoffer().getStake(), newTicketId);
        }

        TicketBuilder reofferTicketBuilder = builderFactory.createTicketBuilder()
                .setTicketId(StringUtils.isNullOrEmpty(newTicketId) ? ticket.getTicketId() + "R" : newTicketId)
                .setSender(ticket.getSender())
                .setOddsChange(ticket.getOddsChange())
                .setTestSource(ticket.getTestSource())
                .setReofferId(ticket.getTicketId());

        if (ticket.getLastMatchEndTime() != null){
            reofferTicketBuilder.setLastMatchEndTime(ticket.getLastMatchEndTime());
        }

        for (Bet ticketBet : ticket.getBets())
        {
            Optional<BetDetail> responseBetDetail = ticketResponse.getBetDetails().stream().filter(f -> f.getBetId().equals(ticketBet.getId())).findFirst();
            if (!responseBetDetail.isPresent() || StringUtils.isNullOrEmpty(responseBetDetail.get().getBetId()))
            {
                throw new IllegalArgumentException("Ticket response is missing a bet details for the bet " + ticketBet.getId());
            }
            BetBuilder newBetBuilder = builderFactory.createBetBuilder()
                    .setBetId(ticketBet.getId() + "R")
                    .setReofferId(ticketBet.getId())
                    .setSumOfWins(ticketBet.getSumOfWins())
                    .setStake(responseBetDetail.get().getReoffer().getStake(), ticketBet.getStake().getType());

            if (ticketBet.getBetBonus() != null)
            {
                newBetBuilder.setBetBonus(ticketBet.getBetBonus().getValue(), ticketBet.getBetBonus().getMode(), ticketBet.getBetBonus().getType());
            }
            for (Selection ticketBetSelection : ticketBet.getSelections())
            {
                newBetBuilder.addSelection(ticketBetSelection);
            }
            for (int ticketBetSelectedSystem : ticketBet.getSelectedSystems())
            {
                newBetBuilder.addSelectedSystem(ticketBetSelectedSystem);
            }
            reofferTicketBuilder.addBet(newBetBuilder.build());
        }
        return reofferTicketBuilder.build();
    }

    private void checkArgsForReofferTicket(Ticket ticket, TicketResponse ticketResponse){
        if (ticket == null || ticketResponse == null)
        {
            throw new IllegalArgumentException("Ticket and TicketResponse must not be null.");
        }
        if (ticket.getBets().size() != 1)
        {
            throw new IllegalArgumentException("Only tickets with exactly 1 bet are supported.");
        }
        if (ticketResponse.getBetDetails().stream().anyMatch(a -> a.getReoffer() == null))
        {
            throw new IllegalArgumentException("Response bet details are missing Reoffer info.");
        }
    }

    /**
     * Builds the reoffer ticket based on the original ticket and the ticket response
     * @param ticket the original ticket
     * @param newStake the new stake value which will be used to set bet stake
     * @param newTicketId the new reoffer ticket id
     * @return Returns the {@link Ticket} representing the reoffer
     * @exception IllegalArgumentException ticket and newStake are mandatory
     */
    private Ticket buildReofferTicket(Ticket ticket, long newStake, String newTicketId)
    {
        if (ticket == null)
        {
            throw new IllegalArgumentException("Ticket must not be null.");
        }
        if (ticket.getBets().size() != 1)
        {
            throw new IllegalArgumentException("Only tickets with exactly 1 bet are supported.");
        }
        if (newStake <= 0)
        {
            throw new IllegalArgumentException("New stake info is invalid.");
        }

        TicketBuilder reofferTicketBuilder = builderFactory.createTicketBuilder()
                .setTicketId(StringUtils.isNullOrEmpty(newTicketId) ? ticket.getTicketId() + "R" : newTicketId)
                .setSender(ticket.getSender())
                .setOddsChange(ticket.getOddsChange())
                .setTestSource(ticket.getTestSource())
                .setReofferId(ticket.getTicketId());

        if (ticket.getLastMatchEndTime() != null){
            reofferTicketBuilder.setLastMatchEndTime(ticket.getLastMatchEndTime());
        }

        for (Bet ticketBet : ticket.getBets())
        {
            BetBuilder newBetBuilder = builderFactory.createBetBuilder()
                    .setBetId(ticketBet.getId() + "R")
                    .setReofferId(ticketBet.getId())
                    .setSumOfWins(ticketBet.getSumOfWins())
                    .setStake(newStake, ticketBet.getStake().getType());

            if (ticketBet.getBetBonus() != null)
            {
                newBetBuilder.setBetBonus(ticketBet.getBetBonus().getValue(), ticketBet.getBetBonus().getMode(), ticketBet.getBetBonus().getType());
            }
            for (Selection ticketBetSelection : ticketBet.getSelections())
            {
                newBetBuilder.addSelection(ticketBetSelection);
            }
            for (int ticketBetSelectedSystem : ticketBet.getSelectedSystems())
            {
                newBetBuilder.addSelectedSystem(ticketBetSelectedSystem);
            }
            reofferTicketBuilder.addBet(newBetBuilder.build());
        }
        return reofferTicketBuilder.build();
    }
}
