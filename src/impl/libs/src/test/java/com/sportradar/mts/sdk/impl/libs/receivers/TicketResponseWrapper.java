/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import com.sportradar.mts.sdk.api.impl.ResponseReasonImpl;
import com.sportradar.mts.sdk.api.impl.TicketResponseImpl;
import com.sportradar.mts.sdk.api.utils.SdkInfo;
import com.sportradar.mts.sdk.api.utils.StaticRandom;

import java.util.Date;

/**
 * @author andrej.resnik on 16/06/16 at 09:09
 */
public class TicketResponseWrapper extends TicketResponseImpl {

    private Integer code;
    private TicketAcceptance result;
    private String ticketId;
    private String exceptionMessage;
    private String intExceptionMessage;
    private long exchangeRate;
    private String betAcceptanceId;
    private long alternativeStake;

    public TicketResponseWrapper() {
        super("ticket-" + System.currentTimeMillis(), new ResponseReasonImpl(101, "Response message"), TicketAcceptance.ACCEPTED, null, "123", 123, new Date(), SdkInfo.MTS_TICKET_VERSION, StaticRandom.S1000, null, null, "{response-payload}");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public TicketAcceptance getStatus() {
        return result;
    }

    public void setResult(TicketAcceptance result) {
        this.result = result;
    }

    @Override
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public void setIntExceptionMessage(String intExceptionMessage) {
        this.intExceptionMessage = intExceptionMessage;
    }

    @Override
    public long getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(long exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String getSignature() {
        return betAcceptanceId;
    }

    public void setBetAcceptanceId(String betAcceptanceId) {
        this.betAcceptanceId = betAcceptanceId;
    }

    public long getAlternativeStake() {
        return alternativeStake;
    }

    public void setAlternativeStake(long alternativeStake) {
        this.alternativeStake = alternativeStake;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketResponseWrapper)) return false;

        TicketResponseWrapper that = (TicketResponseWrapper) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (result != that.result) return false;
        if (ticketId != null ? !ticketId.equals(that.ticketId) : that.ticketId != null) return false;
        if (exceptionMessage != null ? !exceptionMessage.equals(that.exceptionMessage) : that.exceptionMessage != null)
            return false;
        if (intExceptionMessage != null ? !intExceptionMessage.equals(that.intExceptionMessage) : that.intExceptionMessage != null)
            return false;
        if (exchangeRate != 0 ? exchangeRate != that.exchangeRate : that.exchangeRate != 0) return false;
        if (betAcceptanceId != null ? !betAcceptanceId.equals(that.betAcceptanceId) : that.betAcceptanceId != null)
            return false;
        return alternativeStake != 0 ? alternativeStake == that.alternativeStake : that.alternativeStake == 0;

    }

    @Override
    public int hashCode() {
        int result1 = code != null ? code.hashCode() : 0;
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (ticketId != null ? ticketId.hashCode() : 0);
        result1 = 31 * result1 + (exceptionMessage != null ? exceptionMessage.hashCode() : 0);
        result1 = 31 * result1 + (intExceptionMessage != null ? intExceptionMessage.hashCode() : 0);
        result1 = 31 * result1 + (exchangeRate != 0 ? (int)exchangeRate : 0);
        result1 = 31 * result1 + (betAcceptanceId != null ? betAcceptanceId.hashCode() : 0);
        result1 = 31 * result1 + (alternativeStake != 0 ? (int)alternativeStake : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "TicketResponseWrapper{" +
                "code=" + code +
                ", result=" + result +
                ", ticketId='" + ticketId + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", intExceptionMessage='" + intExceptionMessage + '\'' +
                ", exchangeRate=" + exchangeRate +
                ", betAcceptanceId='" + betAcceptanceId + '\'' +
                ", alternativeStake=" + alternativeStake +
                '}';
    }
}
