/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.interfaces;

/**
 * A context interface representing ability of an object to be opened
 */
public interface Openable {

    /**
     * Opens/starts the object
     */
    void open();

    /**
     * Closes the object and clears all resources associated with it
     */
    void close();

    /**
     * Returns objects state
     *
     * @return true if opened
     */
    boolean isOpen();
}
