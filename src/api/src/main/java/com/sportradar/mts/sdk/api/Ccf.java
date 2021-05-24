/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import java.io.Serializable;
import java.util.List;

/**
 * Object containing CCF response data
 */
public interface Ccf extends Serializable {

    /**
     * Gets the customer confidence factor (factor multiplied by 10000)
     *
     * @return customer confidence factor
     */
    long getCcf();

    /**
     * Gets {@link SportCcf} values for sport and prematch/live (if set for customer)
     *
     * @return customer confidence factor per sport
     */
    List<SportCcf> getSportCcfDetails();
}
