/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.sportradar.mts.sdk.api.enums.*;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticket.*;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.Reoffer;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.Result;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Implements methods for converting between DTOs
 */
public class MtsTicketHelper {
    private static final String BET_ID_PATTERN = "^[0-9A-Za-z:_-]*";
    private static final String USER_ID_PATTERN = "^[0-9A-Za-z_-]*";

    public static String GenerateTicketCorrelationId()
    {
        return "j" + UUID.randomUUID().toString();
    }

    public static Ticket.OddsChange convert(OddsChangeType type)
    {
        if(type == OddsChangeType.NONE)
        {
            return Ticket.OddsChange.NONE;
        }
        else if (type == OddsChangeType.ANY)
        {
            return Ticket.OddsChange.ANY;
        }
        else if(type == OddsChangeType.HIGHER)
        {
            return Ticket.OddsChange.HIGHER;
        }
        throw new IllegalArgumentException(String.format("Invalid OddsChangeType value: %s", type.toString())) ;
    }

    public static Stake.Type convertStakeType(StakeType type)
    {
        if(type == StakeType.TOTAL)
        {
            return Stake.Type.TOTAL;
        }
        if(type == StakeType.UNIT)
        {
            return Stake.Type.UNIT;
        }
        throw new IllegalArgumentException(String.format("Invalid StakeType value: %s", type.toString())) ;
    }

    public static EntireStake.Type convertEntireStakeType(StakeType type)
    {
        if(type == StakeType.TOTAL)
        {
            return EntireStake.Type.TOTAL;
        }
        if(type == StakeType.UNIT)
        {
            return EntireStake.Type.UNIT;
        }
        throw new IllegalArgumentException(String.format("Invalid EntireStakeType value: %s", type.toString())) ;
    }

    @SuppressWarnings("SameReturnValue")
    public static Bonus.Mode convert(BetBonusMode mode)
    {
        if(mode == BetBonusMode.ALL)
        {
            return  Bonus.Mode.ALL;
        }
        throw new IllegalArgumentException(String.format("Invalid BetBonusMode value: %s", mode.toString())) ;
    }

    @SuppressWarnings("SameReturnValue")
    public static Bonus.Type convert(BetBonusType type)
    {
        if(type == BetBonusType.TOTAL)
        {
            return Bonus.Type.TOTAL;
        }
        throw new IllegalArgumentException(String.format("Invalid BetBonusType value: %s", type.toString())) ;
    }

    public static Sender.Channel convert(SenderChannel channel)
    {
        switch (channel) {
            case INTERNET:
                return Sender.Channel.INTERNET;
            case RETAIL:
                return Sender.Channel.RETAIL;
            case TERMINAL:
                return Sender.Channel.TERMINAL;
            case MOBILE:
                return Sender.Channel.MOBILE;
            case SMS:
                return Sender.Channel.SMS;
            case CALLCENTRE:
                return Sender.Channel.CALL_CENTRE;
            case TVAPP:
                return Sender.Channel.TV_APP;
            case AGENT:
                return Sender.Channel.AGENT;
            default:
                throw new IllegalArgumentException(String.format("Invalid SenderChannel value: %s", channel.toString()));
        }
    }

    public static TicketAcceptance convert(Result.Status status)
    {
        if(status == Result.Status.ACCEPTED)
        {
            return TicketAcceptance.ACCEPTED;
        }
        if(status == Result.Status.REJECTED)
        {
            return TicketAcceptance.REJECTED;
        }
        throw new IllegalArgumentException(String.format("Invalid response Status value: %s", status.toString())) ;
    }

    public static TicketCancelAcceptance convert(com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelresponse.Result.Status status)
    {
        if(status == com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelresponse.Result.Status.CANCELLED)
        {
            return TicketCancelAcceptance.Cancelled;
        }
        if(status == com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelresponse.Result.Status.NOT_CANCELLED)
        {
            return TicketCancelAcceptance.NotCancelled;
        }
        throw new IllegalArgumentException(String.format("Invalid response Status value: %s", status.toString())) ;
    }

    public static TicketAcceptance convert(com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.Result.Status status) {
        if(status == com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.Result.Status.ACCEPTED)
        {
            return TicketAcceptance.ACCEPTED;
        }
        if(status == com.sportradar.mts.sdk.api.impl.mtsdto.ticketcashoutresponse.Result.Status.REJECTED)
        {
            return TicketAcceptance.REJECTED;
        }
        throw new IllegalArgumentException(String.format("Invalid response Status value: %s", status.toString())) ;
    }

    public static BetReofferType convert(Reoffer.Type type)
    {
        if(type == Reoffer.Type.AUTO)
        {
            return BetReofferType.AUTO;
        }
        if(type == Reoffer.Type.MANUAL)
        {
            return BetReofferType.MANUAL;
        }
        throw new IllegalArgumentException(String.format("Invalid BetReofferType value: %s", type.toString())) ;
    }

    public static TicketAcceptance convert(com.sportradar.mts.sdk.api.impl.mtsdto.ticketnonsrsettle.Result.Status status) {
        if(status == com.sportradar.mts.sdk.api.impl.mtsdto.ticketnonsrsettle.Result.Status.ACCEPTED)
        {
            return TicketAcceptance.ACCEPTED;
        }
        if(status == com.sportradar.mts.sdk.api.impl.mtsdto.ticketnonsrsettle.Result.Status.REJECTED)
        {
            return TicketAcceptance.REJECTED;
        }
        throw new IllegalArgumentException(String.format("Invalid response Status value: %s", status.toString())) ;
    }

    /**
     * Validate if the id is between 1-128 and matches IdPattern
     * @param id to be validated
     * @return bool if check passed
     */
    public static boolean validateTicketId(String id)
    {
        return validateId(id, false);
    }

    /**
     * Validate if the id is between 1-128 and matches IdPattern
     * @param id to be validated
     * @return bool if check passed
     */
    public static boolean validateUserId(String id)
    {
        return validateId(id, true);
    }

    /**
     * Validate if the value is valid percent or null
     * @param percent to be validated
     * @return bool if check passed
     */
    public static boolean validatePercent(Integer percent)
    {
        return percent == null || (percent > 0 && percent <= 1000000);
    }

    /**
     * Validate if the id is between 1-128 and matches IdPattern
     * @param id to be validated
     * @return bool if check passed
     */
    public static boolean validateId(String id, boolean useUserIdPattern)
    {
        String idPattern = useUserIdPattern ? USER_ID_PATTERN : BET_ID_PATTERN;
        return id != null
                && !id.isEmpty()
                && id.length() > 0
                && id.length() <= 128
                && id.matches(idPattern);
    }

    public static boolean validateId(String id, boolean checkIdPattern, boolean useUserIdPattern)
    {
        return validateId(id, checkIdPattern, useUserIdPattern, -1, -1);
    }

    public static boolean validateId(String id, boolean checkIdPattern, boolean useUserIdPattern, int minLength)
    {
        return validateId(id, checkIdPattern, useUserIdPattern, minLength, -1);
    }

    public static boolean validateId(String id, boolean checkIdPattern, boolean useUserIdPattern, int minLength, int maxLength)
    {
        boolean valid = true;
        if(checkIdPattern)
        {
            String idPattern = useUserIdPattern ? USER_ID_PATTERN : BET_ID_PATTERN;
            valid = id.matches(idPattern);
        }
        if(valid && minLength >= 0)
        {
            valid = id.length() >= minLength;
        }
        if(valid && maxLength >= 0)
        {
            valid = id.length() <= maxLength;
        }
        return valid;
    }

    public static boolean validateTimestamp(Date date)
    {
        return date.after(new GregorianCalendar(2017, 1, 1).getTime());
    }
}
