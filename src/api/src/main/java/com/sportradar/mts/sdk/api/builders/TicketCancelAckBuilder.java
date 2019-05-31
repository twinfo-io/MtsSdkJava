/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.TicketCancelAck;
import com.sportradar.mts.sdk.api.enums.TicketCancelAckStatus;
import com.sportradar.mts.sdk.api.impl.builders.TicketCancelAckBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationImpl;


/**
 * Builder used to create a new instance of {@link TicketCancelAck}
 */
public interface TicketCancelAckBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static TicketCancelAckBuilder create()
    {
        SdkConfiguration config = SdkConfigurationImpl.getConfiguration();
        return new TicketCancelAckBuilderImpl(config);
    }

    /**
     * Sets bookmaker id
     *
     * @param bookmakerId bookmaker id
     * @return current builder reference
     */
    TicketCancelAckBuilder setBookmakerId(Integer bookmakerId);

    /**
     * Sets ticket id
     *
     * @param ticketId ticket id
     * @return current builder reference
     */
    TicketCancelAckBuilder setTicketId(String ticketId);

    /**
     * Sets source code
     *
     * @param sourceCode source code
     * @return current builder reference
     */
    TicketCancelAckBuilder setSourceCode(Integer sourceCode);

    /**
     * Sets source message
     *
     * @param sourceMessage source message
     * @return current builder reference
     */
    TicketCancelAckBuilder setSourceMessage(String sourceMessage);

    /**
     * Sets acknowledgment status
     *
     * @param ackStatus acknowledgment status
     * @return current builder reference
     */
    TicketCancelAckBuilder setAckStatus(TicketCancelAckStatus ackStatus);

    /**
     * Creates a new {@link TicketCancelAck}  instance using builder parameters
     *
     * @return new {@link TicketCancelAck} instance
     */
    TicketCancelAck build();
}
