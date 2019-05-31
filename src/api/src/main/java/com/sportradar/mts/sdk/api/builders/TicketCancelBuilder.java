/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.enums.TicketCancellationReason;
import com.sportradar.mts.sdk.api.impl.builders.TicketCancelBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationImpl;


/**
 * Builder used to create a new instance of {@link TicketCancel}
 */
public interface TicketCancelBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static TicketCancelBuilder create() {
        SdkConfiguration config = SdkConfigurationImpl.getConfiguration();
        return new TicketCancelBuilderImpl(config);
    }

    /**
     * Sets ticket id
     *
     * @param ticketId ticket id
     * @return current builder reference
     */
    TicketCancelBuilder setTicketId(String ticketId);

    /**
     * Sets bookmaker id
     *
     * @param bookmakerId bookmaker id
     * @return current builder reference
     */
    TicketCancelBuilder setBookmakerId(int bookmakerId);

    /**
     * Sets the cancellation code
     * @param code The {@link TicketCancellationReason}
     * @return current builder reference
     */
    TicketCancelBuilder setCode(TicketCancellationReason code);

    /**
     * Sets the cancel percent (quantity multiplied by 10_000 and rounded to a long value)
     *
     * @param cancelPercent - the cancel percent value of the assigned ticket
     * @return - the current instance reference
     */
    TicketCancelBuilder setCancelPercent(int cancelPercent);

    /**
     * Add the bet cashout
     *
     * @param betId - the bet id
     * @param cancelPercent - the cancel percent value of the assigned bet (quantity multiplied by 10_000 and rounded to a long value)
     * @return - the current instance reference
     */
    TicketCancelBuilder addBetCancel(String betId, Integer cancelPercent);

    /**
     * Creates a new {@link TicketCancel} instance
     * @param ticketId ticket id
     * @param bookmakerId bookmaker id
     * @param code The {@link TicketCancellationReason}
     * @return new {@link TicketCancel} instance
     */
    TicketCancel build(String ticketId, int bookmakerId, TicketCancellationReason code);

    /**
     * Creates a new {@link TicketCancel}  instance using builder parameters
     * @return new {@link TicketCancel} instance
     */
    TicketCancel build();
}
