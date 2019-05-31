/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.TicketAck;
import com.sportradar.mts.sdk.api.enums.TicketAckStatus;
import com.sportradar.mts.sdk.api.impl.builders.TicketAckBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationImpl;


/**
 * Builder used to create a new instance of {@link TicketAck}
 */
public interface TicketAckBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static TicketAckBuilder create()
    {
        SdkConfiguration config = SdkConfigurationImpl.getConfiguration();
        return new TicketAckBuilderImpl(config);
    }

    /**
     * Sets bookmaker id
     *
     * @param bookmakerId bookmaker id
     * @return current builder reference
     */
    TicketAckBuilder setBookmakerId(Integer bookmakerId);

    /**
     * Sets ticket id
     *
     * @param ticketId ticket id
     * @return current builder reference
     */
    TicketAckBuilder setTicketId(String ticketId);

    /**
     * Sets source code
     *
     * @param sourceCode source code
     * @return current builder reference
     */
    TicketAckBuilder setSourceCode(Integer sourceCode);

    /**
     * Sets source message
     *
     * @param sourceMessage source message
     * @return current builder reference
     */
    TicketAckBuilder setSourceMessage(String sourceMessage);

    /**
     * Sets acknowledgment status
     *
     * @param ackStatus acknowledgment status
     * @return current builder reference
     */
    TicketAckBuilder setAckStatus(TicketAckStatus ackStatus);

    /**
     * Creates a new {@link TicketAck}  instance using builder parameters
     *
     * @return new {@link TicketAck} instance
     */
    TicketAck build();
}