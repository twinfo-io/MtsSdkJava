/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Base for any SDK ticket
 */
public interface SdkTicket extends Serializable {

    /**
     * Gets ticket id
     * Mandatory
     * @return ticket id
     */
    String getTicketId();

    /**
     * Gets timestamp
     *
     * @return timestamp
     */
    Date getTimestampUtc();

    /**
     * Gets version
     * Mandatory
     * @return version
     */
    String getVersion();

    /**
     * Gets the correlation id
     * @return correlation id
     */
    String getCorrelationId();

    /**
     * Returns the associated ticket in the required MTS JSON format
     *
     * @return the associated ticket in the required MTS JSON format
     */
    String getJsonValue();
}
