/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.builders;

import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.TicketReofferCancel;
import com.sportradar.mts.sdk.api.impl.builders.TicketReofferCancelBuilderImpl;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationImpl;


/**
 * Builder used to create a new instance of {@link com.sportradar.mts.sdk.api.TicketReofferCancel}
 */
public interface TicketReofferCancelBuilder {

    /**
     * Gets the builder instance
     * @return the builder instance
     * @deprecated
     * Method create() is obsolete. Please use the appropriate method on BuilderFactory interface which can be obtained through MtsSdk instance
     */
    @Deprecated
    static TicketReofferCancelBuilder create() {
        SdkConfiguration config = SdkConfigurationImpl.getConfiguration();
        return new TicketReofferCancelBuilderImpl(config);
    }

    /**
     * Sets ticket id
     *
     * @param ticketId ticket id
     * @return current builder reference
     */
    TicketReofferCancelBuilder setTicketId(String ticketId);

    /**
     * Sets bookmaker id
     *
     * @param bookmakerId bookmaker id
     * @return current builder reference
     */
    TicketReofferCancelBuilder setBookmakerId(int bookmakerId);

    /**
     * Creates a new {@link TicketCancel} instance
     * @param ticketId ticket id
     * @param bookmakerId bookmaker id
     * @return new {@link TicketCancel} instance
     */
    TicketReofferCancel build(String ticketId, int bookmakerId);

    /**
     * Creates a new {@link TicketReofferCancel}  instance using builder parameters
     *
     * @return new {@link TicketReofferCancel} instance
     */
    TicketReofferCancel build();
}
