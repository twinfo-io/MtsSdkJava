/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.impl.libs.receivers;

import com.sportradar.mts.sdk.api.impl.TicketCancelResponseImpl;
import com.sportradar.mts.sdk.api.utils.StaticRandom;

import java.util.Date;

/**
 * @author andrej.resnik on 16/06/16 at 09:30
 */
public class TicketCancelResponseWrapper extends TicketCancelResponseImpl {

    private int cancellationReason;
    private int code;
    private String extTicket;
    private String message;
    private String reasonMessage;

    public TicketCancelResponseWrapper() {
        super(null, null, null, "123", new Date(), "2.0", StaticRandom.S1000, null,"{response-payload}");
    }

    /**
     * Gets cancellation reason, same as in sendBlocking ticket cancel
     *
     * @return cancellation reason
     */
    public int getCancellationReason() {
        return cancellationReason;
    }

    /**
     * Gets response code. 1024 if ok
     *
     * @return response code
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets ext ticket, same as in sendBlocking ticket cancel
     *
     * @return ext ticket
     */
    @Override
    public String getTicketId() {
        return extTicket;
    }

    /**
     * Gets response message
     *
     * @return response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets reason message, same as in sendBlocking ticket cancel
     *
     * @return reason message
     */
    public String getReasonMessage() {
        return reasonMessage;
    }

    public void setCancellationReason(int cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setExtTicket(String extTicket) {
        this.extTicket = extTicket;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReasonMessage(String reasonMessage) {
        this.reasonMessage = reasonMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TicketCancelResponseWrapper that = (TicketCancelResponseWrapper) o;

        if (cancellationReason != that.cancellationReason) return false;
        if (code != that.code) return false;
        if (extTicket != null ? !extTicket.equals(that.extTicket) : that.extTicket != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return reasonMessage != null ? reasonMessage.equals(that.reasonMessage) : that.reasonMessage == null;
    }

    @Override
    public int hashCode() {
        int result = cancellationReason;
        result = 31 * result + code;
        result = 31 * result + (extTicket != null ? extTicket.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (reasonMessage != null ? reasonMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TicketCancelResponseWrapper{" +
                "cancellationReason=" + cancellationReason +
                ", code=" + code +
                ", extTicket='" + extTicket + '\'' +
                ", message='" + message + '\'' +
                ", reasonMessage='" + reasonMessage + '\'' +
                '}';
    }
}
