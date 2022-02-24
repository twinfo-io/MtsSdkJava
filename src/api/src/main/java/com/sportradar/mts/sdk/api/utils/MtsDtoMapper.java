/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPISelectionType;
import com.sportradar.mts.api.rest.custombet.datamodel.CAPISelections;
import com.sportradar.mts.sdk.api.*;
import com.sportradar.mts.sdk.api.enums.TicketAckStatus;
import com.sportradar.mts.sdk.api.enums.TicketCancelAckStatus;
import com.sportradar.mts.sdk.api.impl.*;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.AccessTokenSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.CcfResponseSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.MaxStakeResponseSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.reoffercancel.TicketReofferCancelSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.EndCustomer;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Selection;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Stake;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Ticket;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.*;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketack.TicketAckSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.Cancel;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.TicketCancelSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelack.TicketCancelAckSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelresponse.TicketCancelResponseSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashout.TicketCashoutSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketnonsrsettle.TicketNonSrSettleSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.Reason;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.RejectionInfo;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.Reoffer;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.TicketResponseSchema;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper for DTOs
 */
public class MtsDtoMapper {

    private MtsDtoMapper() { throw new IllegalStateException("MtsDtoMapper class"); }

    private static final Set<String> TICKET_RESPONSE_INTERESTING_HEADERS = ImmutableSet.<String>builder()
            .add("validatedUtcTimestamp")
            .add("receivedUtcTimestamp")
            .add("respondedUtcTimestamp")
            .add("Content-Type")
            .build();

    public static TicketSchema map(com.sportradar.mts.sdk.api.Ticket ticket)
    {
        Ticket dtoTicket = new Ticket();
        dtoTicket.setTicketId(ticket.getTicketId());
        dtoTicket.setSender(map(ticket.getSender()));
        dtoTicket.setOddsChange(MtsTicketHelper.convert(ticket.getOddsChange()));
        dtoTicket.setTestSource(ticket.getTestSource());
        dtoTicket.setTimestampUtc(MtsDateFormatter.dateTimeToUnixTime(ticket.getTimestampUtc()));
        dtoTicket.setVersion(ticket.getVersion());
        dtoTicket.setAltStakeRefId(ticket.getAltStakeRefId());
        dtoTicket.setReofferRefId(ticket.getReofferId());
        if (ticket.getLastMatchEndTime()!= null){
            dtoTicket.setLastMatchEndTime(ticket.getLastMatchEndTime().getTime());
        }
        dtoTicket.setPayCap(ticket.getPayCap());

        boolean hasBanker = false;
        List<Selection> selections = Lists.newArrayList();
        for(com.sportradar.mts.sdk.api.Selection selection : ticket.getSelections())
        {
            hasBanker = selection.getIsBanker();
            if(selections.stream().noneMatch(s -> s.getEventId().equals(selection.getEventId())
                                         && s.getId().equals(selection.getId())
                                         && s.getOdds().equals(selection.getOdds())))
            {
                selections.add(map(selection));
            }
        }
        dtoTicket.setSelections(selections);
        for(com.sportradar.mts.sdk.api.Bet ticketBet : ticket.getBets())
        {
            com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Bet b = map(ticketBet, dtoTicket.getSelections(), hasBanker);
            dtoTicket.getBets().add(b);
        }

        dtoTicket.setTotalCombinations(ticket.getTotalCombinations());

        TicketSchema dto = new TicketSchema();
        dto.setTicket(dtoTicket);
        return dto;
    }

    private static com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Bet map(com.sportradar.mts.sdk.api.Bet bet, List<Selection> selections, boolean hasBanker)
    {
        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Bet dtoBet = new com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Bet();
        dtoBet.setId(bet.getId());
        dtoBet.setSumOfWins(bet.getSumOfWins());
        dtoBet.setReofferRefId(bet.getReofferRefId());
        Stake stake = new Stake();
        stake.setValue(bet.getStake().getValue());
        stake.setType(MtsTicketHelper.convertStakeType(bet.getStake().getType()));
        dtoBet.setStake(stake);
        if (bet.getEntireStake() != null) {
            EntireStake entireStake = new EntireStake();
            entireStake.setValue(bet.getEntireStake().getValue());
            entireStake.setType(MtsTicketHelper.convertEntireStakeType(bet.getEntireStake().getType()));
            dtoBet.setEntireStake(entireStake);
        }
        dtoBet.setBonus(null);
        if(bet.getBetBonus() != null) {
            Bonus b = new Bonus();
            b.setValue(bet.getBetBonus().getValue());
            b.setMode(MtsTicketHelper.convert(bet.getBetBonus().getMode()));
            b.setType(MtsTicketHelper.convert(bet.getBetBonus().getType()));
            b.setDescription(MtsTicketHelper.convert(bet.getBetBonus().getDescription()));
            b.setPaidAs(MtsTicketHelper.convert(bet.getBetBonus().getPaidAs()));
            dtoBet.setBonus(b);
        }
        dtoBet.setSelectedSystems(null);
        if(bet.getSelectedSystems() != null)
        {
            dtoBet.setSelectedSystems(ImmutableList.copyOf(bet.getSelectedSystems()));
        }

        dtoBet.setSelectionRefs(null);
        List<SelectionRef> refs = getBetSelectionRefs(bet, selections, hasBanker);
        if(refs != null && !refs.isEmpty()) {
            dtoBet.setSelectionRefs(ImmutableList.copyOf(refs));
        }

        dtoBet.setCustomBet(bet.getCustomBet());
        dtoBet.setCalculationOdds(bet.getCalculationOdds());
        return dtoBet;
    }

    private static List<SelectionRef> getBetSelectionRefs(com.sportradar.mts.sdk.api.Bet bet, List<Selection> allSelections, boolean hasBanker)
    {
        if (bet.getSelections().size() != allSelections.size()
            || bet.getSelections().stream().anyMatch(com.sportradar.mts.sdk.api.Selection::getIsBanker)
            || hasBanker)
        {
            List<SelectionRef> refs = Lists.newArrayList();
            for (com.sportradar.mts.sdk.api.Selection betSelection : bet.getSelections())
            {
                SelectionRef ref = new SelectionRef();
                ref.setSelectionIndex(findSelectionIndex(allSelections, betSelection));
                ref.setBanker(betSelection.getIsBanker());
                refs.add(ref);
            }
            return refs;
        }
        return new ArrayList<>();
    }

    private static int findSelectionIndex(List<Selection> allSelections, com.sportradar.mts.sdk.api.Selection specific)
    {
        for (int i = 0; i < allSelections.size(); i++)
        {
            Selection sel = allSelections.get(i);
            if (sel.getEventId().equals(specific.getEventId()) && sel.getId().equals(specific.getId()) && sel.getOdds().equals(specific.getOdds()))
            {
                return i;
            }
        }
        return -1;
    }

    private static Selection map(com.sportradar.mts.sdk.api.Selection betSelection) {
        Selection dtoSelection = new Selection();
        dtoSelection.setId(betSelection.getId());
        dtoSelection.setEventId(betSelection.getEventId());
        dtoSelection.setOdds(betSelection.getOdds());
        dtoSelection.setBoostedOdds(betSelection.getBoostedOdds());
        return dtoSelection;
    }

    public static com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Sender map(com.sportradar.mts.sdk.api.Sender orgSender)
    {
        com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Sender dtoSender = new com.sportradar.mts.sdk.api.impl.mtsdto.ticket.Sender();
        dtoSender.setBookmakerId(orgSender.getBookmakerId());
        dtoSender.setChannel(MtsTicketHelper.convert(orgSender.getChannel()));
        dtoSender.setCurrency(orgSender.getCurrency());
        dtoSender.setLimitId(orgSender.getLimitId());
        dtoSender.setShopId(orgSender.getShopId());
        dtoSender.setTerminalId(orgSender.getTerminalId());
        if (orgSender.getEndCustomer() != null) {
            dtoSender.setEndCustomer(map(orgSender.getEndCustomer()));
        }
        return dtoSender;
    }

    public static EndCustomer map(com.sportradar.mts.sdk.api.EndCustomer customer)
    {
        EndCustomer dtoCustomer = new EndCustomer();
        dtoCustomer.setId(customer.getId());
        dtoCustomer.setIp(customer.getEndCustomerIp());
        dtoCustomer.setDeviceId(customer.getDeviceId());
        dtoCustomer.setLanguageId(customer.getLanguageId());
        dtoCustomer.setConfidence(customer.getConfidence());
        return dtoCustomer;
    }

    public static TicketCancelSchema map(TicketCancel ticketCancel)
    {
        Cancel dtoCancel = new Cancel();
        dtoCancel.setTicketId(ticketCancel.getTicketId());
        dtoCancel.setCode(ticketCancel.getCode().getId());
        com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.Sender sender = new com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.Sender();
        sender.setBookmakerId(ticketCancel.getBookmakerId());
        dtoCancel.setSender(sender);
        dtoCancel.setTimestampUtc(MtsDateFormatter.dateTimeToUnixTime(ticketCancel.getTimestampUtc()));
        dtoCancel.setVersion(ticketCancel.getVersion());
        dtoCancel.setCancelPercent(ticketCancel.getCancelPercent());
        List<com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.BetCancel> betCancels = Lists.newArrayList();
        if(ticketCancel.getBetCancels() != null) {
            for (BetCancel betCancel : ticketCancel.getBetCancels()) {
                betCancels.add(new com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancel.BetCancel(betCancel.getBetId(), betCancel.getCancelPercent()));
            }
        }
        else {
            betCancels = null;
        }
        dtoCancel.setBetCancel(betCancels);
        TicketCancelSchema dto = new TicketCancelSchema();
        dto.setCancel(dtoCancel);
        return dto;
    }

    public static TicketAckSchema map(TicketAck ticketAck)
    {
        TicketAckSchema dto = new TicketAckSchema();
        dto.setTicketId(ticketAck.getTicketId());
        dto.setCode(ticketAck.getCode());
        dto.setMessage(ticketAck.getMessage());
        dto.setTicketStatus(ticketAck.getAckStatus() == TicketAckStatus.ACCEPTED
                ? TicketAckSchema.TicketStatus.ACCEPTED
                : TicketAckSchema.TicketStatus.REJECTED);
        com.sportradar.mts.sdk.api.impl.mtsdto.ticketack.Sender sender = new com.sportradar.mts.sdk.api.impl.mtsdto.ticketack.Sender();
        sender.setBookmakerId(ticketAck.getBookmakerId());
        dto.setSender(sender);
        dto.setTimestampUtc(MtsDateFormatter.dateTimeToUnixTime(ticketAck.getTimestampUtc()));
        dto.setVersion(ticketAck.getVersion());
        return dto;
    }

    public static TicketCancelAckSchema map(TicketCancelAck ticketCancelAck)
    {
        TicketCancelAckSchema dto = new TicketCancelAckSchema();
        dto.setTicketId(ticketCancelAck.getTicketId());
        dto.setCode(ticketCancelAck.getCode());
        dto.setMessage(ticketCancelAck.getMessage());
        dto.setTicketCancelStatus(ticketCancelAck.getAckStatus() == TicketCancelAckStatus.CANCELLED
            ? TicketCancelAckSchema.TicketCancelStatus.CANCELLED
            : TicketCancelAckSchema.TicketCancelStatus.NOT_CANCELLED);
        com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelack.Sender sender = new com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelack.Sender();
        sender.setBookmakerId(ticketCancelAck.getBookmakerId());
        dto.setSender(sender);
        dto.setTimestampUtc(MtsDateFormatter.dateTimeToUnixTime(ticketCancelAck.getTimestampUtc()));
        dto.setVersion(ticketCancelAck.getVersion());
        return dto;
    }

    public static TicketCashoutSchema map(TicketCashout ticketCashout) {

        List<com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashout.BetCashout> betCashouts = Lists.newArrayList();
        if(ticketCashout.getBetCashouts() != null) {
            for (BetCashout betCashout : ticketCashout.getBetCashouts()) {
            betCashouts.add(new com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashout.BetCashout(betCashout.getBetId(), betCashout.getCashoutStake(), betCashout.getCashoutPercent()));
            }
        }
        else {
            betCashouts = null;
        }
        return new TicketCashoutSchema(
                MtsDateFormatter.dateTimeToUnixTime(ticketCashout.getTimestampUtc()),
                ticketCashout.getTicketId(),
                new com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashout.Sender(ticketCashout.getBookmakerId()),
                ticketCashout.getCashoutStake(),
                ticketCashout.getCashoutPercent(),
                betCashouts,
                ticketCashout.getVersion()
        );
    }

    public static TicketNonSrSettleSchema map(TicketNonSrSettle ticketNonSrSettle) {
        return new TicketNonSrSettleSchema(MtsDateFormatter.dateTimeToUnixTime(ticketNonSrSettle.getTimestampUtc()),
                ticketNonSrSettle.getTicketId(),
                new com.sportradar.mts.sdk.api.impl.mtsdto.ticketnonsrsettle.Sender(ticketNonSrSettle.getBookmakerId()),
                ticketNonSrSettle.getNonSrSettleStake(),
                ticketNonSrSettle.getVersion());
    }


    public static TicketResponse map(TicketResponseSchema response, String correlationId, Map<String, Object> messageHeaders, String msgBody)
    {
        return new TicketResponseImpl(response.getResult().getTicketId(),
                                      map(response.getResult().getReason()),
                                      MtsTicketHelper.convert(response.getResult().getStatus()),
                                      response.getResult().getBetDetails(),
                                      response.getSignature(),
                                      response.getExchangeRate(),
                                      new Date(),
                                      response.getVersion(),
                                      correlationId,
                                      parseAdditionalInfo(messageHeaders),
                                      response.getAutoAcceptedOdds(),
                                      msgBody);
    }

    public static ResponseReason map(Reason reason)
    {
        return new ResponseReasonImpl(reason.getCode(), reason.getMessage());
    }

    public static TicketCancelResponse map(TicketCancelResponseSchema response, String correlationId, Map<String, Object> messageHeaders, String msgBody)
    {
        return new TicketCancelResponseImpl(
                response.getResult().getTicketId(),
                map(response.getResult().getReason()),
                MtsTicketHelper.convert(response.getResult().getStatus()),
                response.getSignature(),
                new Date(),
                response.getVersion(),
                correlationId,
                parseAdditionalInfo(messageHeaders),
                msgBody);
    }

    public static ResponseReason map(com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelresponse.Reason reason)
    {
        return new ResponseReasonImpl(reason.getCode(), reason.getMessage());
    }

    public static TicketCashoutResponse map(com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.TicketCashoutResponseSchema schema, String correlationId, Map<String, Object> messageHeaders, String msgBody) {

        return new TicketCashoutResponseImpl(
                schema.getResult().getTicketId(),
                new Date(),
                map(schema.getResult().getReason()),
                MtsTicketHelper.convert(schema.getResult().getStatus()),
                schema.getSignature(),
                schema.getVersion(),
                correlationId,
                parseAdditionalInfo(messageHeaders),
                msgBody);
    }

    public static TicketNonSrSettleResponse map(com.sportradar.mts.sdk.api.impl.mtsdto.ticketnonsrsettle.TicketNonSrSettleResponseSchema schema, String correlationId, Map<String, Object> messageHeaders, String msgBody) {

        return new TicketNonSrSettleResponseImpl(
                schema.getResult().getTicketId(),
                new Date(),
                schema.getVersion(),
                map(schema.getResult().getReason()),
                MtsTicketHelper.convert(schema.getResult().getStatus()),
                schema.getSignature(),
                correlationId,
                parseAdditionalInfo(messageHeaders),
                msgBody);
    }

    public static ResponseReason map(com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.Reason reason) {
        return new ResponseReasonImpl(reason.getCode(), reason.getMessage());
    }

    public static ResponseReason map(com.sportradar.mts.sdk.api.impl.mtsdto.ticketnonsrsettle.Reason reason) {
        return new ResponseReasonImpl(reason.getCode(), reason.getMessage());
    }

    public static SelectionDetail map(com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.SelectionDetail input)
    {
        if(input != null) {
            return new SelectionDetailsImpl(input.getSelectionIndex(), map(input.getReason()), map(input.getRejectionInfo()));
        }
        return null;
    }

    public static com.sportradar.mts.sdk.api.RejectionInfo map(RejectionInfo rejectionInfo) {
        return new RejectionInfoImpl(rejectionInfo.getId(), rejectionInfo.getEventId(), rejectionInfo.getOdds());
    }

    public static BetReoffer map(Reoffer input)
    {
        if(input != null) {
            return new BetReofferImpl(input.getStake(), MtsTicketHelper.convert(input.getType()));
        }
        return null;
    }

    public static TicketReofferCancelSchema map(TicketReofferCancel ticketReofferCancel)
    {
        TicketReofferCancelSchema dto = new TicketReofferCancelSchema();
        dto.setTicketId(ticketReofferCancel.getTicketId());
        com.sportradar.mts.sdk.api.impl.mtsdto.reoffercancel.Sender sender = new com.sportradar.mts.sdk.api.impl.mtsdto.reoffercancel.Sender();
        sender.setBookmakerId(ticketReofferCancel.getBookmakerId());
        dto.setSender(sender);
        dto.setTimestampUtc(MtsDateFormatter.dateTimeToUnixTime(ticketReofferCancel.getTimestampUtc()));
        dto.setVersion(ticketReofferCancel.getVersion());
        return dto;
    }

    public static Ccf map(CcfResponseSchema ccfResponse) {
        if (ccfResponse == null)
            return null;
        return new CcfImpl(ccfResponse.getCcf(),
                ccfResponse.getSportCcfDetails().stream()
                        .map(s -> new SportCcfImpl(s.getSportId(), s.getPrematchCcf(), s.getLiveCcf()))
                        .collect(Collectors.toList()));
    }

    public static Long map(MaxStakeResponseSchema maxStakeResponse) {
        if (maxStakeResponse == null)
            return null;
        return maxStakeResponse.getMaxStake();
    }

    public static AccessToken map(AccessTokenSchema accessTokenResponse) {
        return new AccessTokenImpl(accessTokenResponse.getAccessToken(), accessTokenResponse.getExpiresIn());
    }

    public static CAPISelections map(List<com.sportradar.mts.sdk.api.interfaces.customBet.Selection> selections) {
        CAPISelections content = new CAPISelections();
        content.getSelections().addAll(selections.stream()
                .map(s -> {
                    CAPISelectionType selection = new CAPISelectionType();
                    selection.setId(s.getEventId().toString());
                    selection.setMarketId(s.getMarketId());
                    selection.setSpecifiers(s.getSpecifiers());
                    selection.setOutcomeId(s.getOutcomeId());
                    return selection;
                })
                .collect(Collectors.toList()));
        return content;
    }

    private static Map<String, String> parseAdditionalInfo(Map<String, Object> messageHeaders)
    {
        return messageHeaders == null
                ? Collections.emptyMap()
                : messageHeaders.entrySet().stream()
                        .filter(e -> TICKET_RESPONSE_INTERESTING_HEADERS.contains(e.getKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e-> e.getValue().toString()
                        ));
    }
}