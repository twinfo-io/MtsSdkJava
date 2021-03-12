package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.enums.SourceType;

import java.util.Date;

/**
 * Object containing CCF change data
 */
public interface CcfChange {
    /**
     * Gets the timestamp of the ccf value change
     *
     * @return Date of change
     */
    Date getTimestamp();

    /**
     * Gets the bookmaker id of the ccf value change
     *
     * @return Id of the bookmaker
     */
    Integer getBookmakerId();

    /**
     * Gets the sub bookmaker id of the ccf value change
     *
     * @return Id of the sub bookmaker
     */
    Integer getSubBookmakerId();

    /**
     * Gets the source id of the ccf value change
     *
     * @return Id of the customer
     */
    String getSourceId();

    /**
     * Gets the source type customer of the ccf value change
     *
     * @return SourceType of the customer
     */
    SourceType getSourceType();

    /**
     * Gets customer confidence factor for the customer
     *
     * @return customer confidence factor
     */
    Double getCcf();

    /**
     * Gets previous customer confidence factor for the customer
     *
     * @return previous customer confidence factor
     */
    Double getPreviousCcf();

    /**
     * Gets sport ID
     *
     * @return sport ID
     */
    String getSportId();

    /**
     * Gets sport name
     *
     * @return sport name
     */
    String getSportName();

    /**
     * If the change was for live only
     *
     * @return if live only change
     */
    Boolean getLive();
}
