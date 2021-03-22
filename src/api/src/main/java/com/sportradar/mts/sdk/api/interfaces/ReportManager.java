package com.sportradar.mts.sdk.api.interfaces;

import com.sportradar.mts.sdk.api.CcfChange;
import com.sportradar.mts.sdk.api.enums.SourceType;
import com.sportradar.mts.sdk.api.exceptions.MtsReportException;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * Entry point for the MTS ReportingManager API
 */
public interface ReportManager {

    /**
     * Gets customer ccf (confidence factor history change) csv output stream for bookmakers with filters
     *
     * <pre>
     *  Examples:
     *
     *  try(OutputStream outputStream = new FileOutputStream("HistoryCcfChanges.csv")) {
     *      mtsSdk.getReportManager().getHistoryCcfChangeCsvExport(outputStream, startDate, endDate);
     *  }</pre>
     *
     * @param outputStream Output stream to store the result data (in csv format)
     * @param startDate    Start date to query changes
     * @param endDate      End date to query changes
     * @throws MtsReportException throws reporting exception with error message
     */
    void getHistoryCcfChangeCsvExport(OutputStream outputStream, Date startDate, Date endDate) throws MtsReportException;

    /**
     * Gets customer ccf (confidence factor history change) csv output stream for bookmakers with filters
     *
     * <pre>
     *  Example:
     *
     *  try(OutputStream outputStream = new FileOutputStream("HistoryCcfChanges.csv")) {
     *      mtsSdk.getReportManager().getHistoryCcfChangeCsvExport(outputStream, startDate, endDate, bookmakerId, subBookmakers, sourceId, sourceType);
     *  }</pre>
     *
     * @param outputStream    Output stream to store the result data (in csv format)
     * @param startDate       Start date to query changes
     * @param endDate         End date to query changes
     * @param bookmakerId     Optional override bookmaker id to filter (if null then value is collected from sdkConfig)
     * @param subBookmakerIds Optional list of sub bookmakers ids to filter
     * @param sourceId        Optional source ID filter which identifies a customer
     * @param sourceType      Optional source type filter to identifies customer type (usually this is always a customer type)
     * @throws MtsReportException throws reporting exception with error message
     */
    void getHistoryCcfChangeCsvExport(OutputStream outputStream, Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType) throws MtsReportException;

    /**
     * Gets customer ccf (confidence factor history change) csv output stream for bookmakers with filters
     *
     * @param outputStream    Output stream to store the result data (in csv format)
     * @param startDate       Start date to query changes
     * @param endDate         End date to query changes
     * @param bookmakerId     Optional override bookmaker id to filter (if null then value is collected from sdkConfig)
     * @param subBookmakerIds Optional list of sub bookmakers ids to filter
     * @param sourceId        Optional source ID filter which identifies a customer
     * @param sourceType      Optional source type filter to identifies customer type (usually this is always a customer type)
     * @param username        A username used for authentication (keycloak username from configuration)
     * @param password        A password used for authentication (keycloak password from configuration)
     * @throws MtsReportException throws reporting exception with error message
     */
    @SuppressWarnings("java:S107")
    void getHistoryCcfChangeCsvExport(OutputStream outputStream, Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType, String username, String password) throws MtsReportException;


    /**
     * Gets customer ccf (customer confidence factor) history change list for bookmakers with filters
     *
     * <pre>{@code
     *  Example:
     *
     *   List<CcfChange> ccfChanges = mtsSdk.getReportManager().getHistoryCcfChangeCsvExport(outputStream, startDate, endDate);
     * }</pre>
     *
     * @param startDate Start date to query changes
     * @param endDate   End date to query changes
     * @return A list {@link CcfChange} values for specified bookmaker id and optional filter values
     * @throws MtsReportException throws reporting exception with error message
     */
    List<CcfChange> getHistoryCcfChangeCsvExport(Date startDate, Date endDate) throws MtsReportException;

    /**
     * Gets customer ccf (customer confidence factor) history change list for bookmakers with filters
     * <pre>{@code
     *  Example:
     *
     *   List<CcfChange> ccfChanges = mtsSdk.getReportManager().getHistoryCcfChangeCsvExport(outputStream, startDate, endDate, bookmakerId, subBookmakers, sourceId, sourceType);
     * }</pre>
     *
     * @param startDate       Start date to query changes
     * @param endDate         End date to query changes
     * @param bookmakerId     Optional override bookmaker id to filter (if null then value is collected from sdkConfig)
     * @param subBookmakerIds Optional list of sub bookmakers ids to filter
     * @param sourceId        Optional source ID filter which identifies a customer
     * @param sourceType      Optional source type filter to identifies customer type (usually this is always a customer type)
     * @return A list {@link CcfChange} values for specified bookmaker id and optional filter values
     * @throws MtsReportException throws reporting exception with error message
     */
    List<CcfChange> getHistoryCcfChangeCsvExport(Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType) throws MtsReportException;

    /**
     * Gets customer ccf (customer confidence factor) history change list for bookmakers with filters
     *
     * @param startDate       Start date to query changes
     * @param endDate         End date to query changes
     * @param bookmakerId     Optional override bookmaker id to filter (if null then value is collected from sdkConfig)
     * @param subBookmakerIds Optional list of sub bookmakers ids to filter
     * @param sourceId        Optional source ID filter which identifies a customer
     * @param sourceType      Optional source type filter to identifies customer type (usually this is always a customer type)
     * @param username        A username used for authentication (keycloak username from configuration)
     * @param password        A password used for authentication (keycloak password from configuration)
     * @return A list {@link CcfChange} values for specified bookmaker id and optional filter values
     * @throws MtsReportException throws reporting exception with error message
     */
    @SuppressWarnings("java:S107")
    List<CcfChange> getHistoryCcfChangeCsvExport(Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType, String username, String password) throws MtsReportException;
}
