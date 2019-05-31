/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.impl;

import com.sportradar.mts.sdk.api.Ccf;
import com.sportradar.mts.sdk.api.SportCcf;

import java.util.List;

/**
 * Implementation of the user exposed entity {@link Ccf}
 */
public class CcfImpl implements Ccf {
    /**
     * The customer confidence factor
     */
    private long ccf;

    /**
     * The customer confidence factor per sport
     */
    private List<SportCcf> sportCcfDetails;

    /**
     * Initializes a new instance of the {@link CcfImpl}
     *
     * @param ccf customer confidence factor
     * @param sportCcfDetails customer confidence factor per sport
     */
    public CcfImpl(long ccf, List<SportCcf> sportCcfDetails) {
        this.ccf = ccf;
        this.sportCcfDetails = sportCcfDetails;
    }

    /**
     * Gets the customer confidence factor (factor multiplied by 10000)
     *
     * @return customer confidence factor
     */
    @Override
    public long getCcf() {
        return ccf;
    }

    /**
     * Gets {@link SportCcf} values for sport and prematch/live (if set for customer)
     *
     * @return customer confidence factor per sport
     */
    public List<SportCcf> getSportCcfDetails() {
        return sportCcfDetails;
    }

}
