package com.sportradar.mts.sdk.impl.libs.clientapi;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;
import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.CcfChange;
import com.sportradar.mts.sdk.api.enums.SourceType;
import com.sportradar.mts.sdk.api.exceptions.MtsReportException;
import com.sportradar.mts.sdk.api.impl.CcChangeImpl;
import com.sportradar.mts.sdk.api.interfaces.ReportManager;
import com.sportradar.mts.sdk.api.rest.DataProvider;
import com.sportradar.mts.sdk.impl.libs.clientapi.filters.CcfChangeFilter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class MtsReportManagerImpl implements ReportManager {
    private static final Logger logger = LoggerFactory.getLogger(MtsReportManagerImpl.class);

    private static final int BUFFER_LENGTH = 8192;

    final DataProvider<InputStream> ccfHistoryChangeExportCsvDataProvider;
    private final String username;
    private final String password;
    private final int defaultBookmakerId;
    private final SimpleDateFormat formatter;
    private final LoadingCache<String, AccessToken> accessTokenCache;

    public MtsReportManagerImpl(int defaultBookmakerId, LoadingCache<String, AccessToken> accessTokenCache, DataProvider<InputStream> ccfHistoryChangeExportCsvDataProvider, String keycloakUsername, String keycloakPassword) {
        this.defaultBookmakerId = defaultBookmakerId;
        this.accessTokenCache = accessTokenCache;
        this.ccfHistoryChangeExportCsvDataProvider = ccfHistoryChangeExportCsvDataProvider;
        this.username = keycloakUsername;
        this.password = keycloakPassword;
        this.formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        this.formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void getHistoryCcfChangeCsvExport(OutputStream outputStream, Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType) throws MtsReportException {
        getHistoryCcfChangeCsvExport(outputStream, startDate, endDate, bookmakerId, subBookmakerIds, sourceId, sourceType, username, password);
    }

    @Override
    public List<CcfChange> getHistoryCcfChangeCsvExport(Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType) throws MtsReportException {
        return getHistoryCcfChangeCsvExport(startDate, endDate, bookmakerId, subBookmakerIds, sourceId, sourceType, username, password);
    }

    @Override
    public void getHistoryCcfChangeCsvExport(OutputStream outputStream, Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType, String username, String password) throws MtsReportException {
        Preconditions.checkNotNull(outputStream);
        Preconditions.checkNotNull(username);
        Preconditions.checkNotNull(password);

        CcfChangeFilter filter = new CcfChangeFilter(startDate, endDate, bookmakerId, subBookmakerIds, sourceId, sourceType, this.defaultBookmakerId);

        try {
            logger.info("Called getHistoryCcfChangeCSVExport with startDatetime={}, endDatetime={}, bookmakerId={}, subBookmakerId={}, sourceType={}, sourceId={}.", filter.getStartDate(), filter.getEndDate(), filter.getBookmakerId(), filter.getSubBookmakers(), filter.getSourceType(), filter.getSourceId());
            getHistoryCcfChangeCsvExport(outputStream, filter, username, password);
        } catch (Exception e) {
            logger.warn("Getting getHistoryCcfChangeCSVExport with startDatetime={}, endDatetime={}, bookmakerId={}, subBookmakerId={}, sourceType={}, sourceId={} failed.", filter.getStartDate(), filter.getEndDate(), filter.getBookmakerId(), filter.getSubBookmakers(), filter.getSourceType(), filter.getSourceId());
            throw new MtsReportException(e.getCause().getMessage());
        }
    }

    @Override
    public List<CcfChange> getHistoryCcfChangeCsvExport(Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType, String username, String password) throws MtsReportException {
        Preconditions.checkNotNull(username);
        Preconditions.checkNotNull(password);

        CcfChangeFilter filter = new CcfChangeFilter(startDate, endDate, bookmakerId, subBookmakerIds, sourceId, sourceType, this.defaultBookmakerId);
        AccessToken token = getAccessToken(username, password);

        try (InputStream inputStream = ccfHistoryChangeExportCsvDataProvider.getData(token, filter.getStartDate(), filter.getEndDate(), filter.getBookmakerId(), filter.getSubBookmakers(), filter.getSourceType(), filter.getSourceId())) {
            if (inputStream == null) {
                throw new MtsReportException("Failed to get history change ccf export list result.");
            }
            return parseInputCcfChangeCsvExport(inputStream);

        } catch (Exception e) {
            throw new MtsReportException(e.getMessage());
        }
    }

    private void getHistoryCcfChangeCsvExport(OutputStream outputStream, CcfChangeFilter filter, String username, String password) throws MtsReportException {
        AccessToken token = getAccessToken(username, password);

        try (InputStream inputStream = ccfHistoryChangeExportCsvDataProvider.getData(token, filter.getStartDate(), filter.getEndDate(), filter.getBookmakerId(), filter.getSubBookmakers(), filter.getSourceType(), filter.getSourceId())) {
            if (inputStream == null) {
                throw new MtsReportException("Failed to get history change ccf export csv file result.");
            }

            byte[] buffer = new byte[BUFFER_LENGTH];
            int totalBytesRead = 0;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            logger.info("Number of {} bytes CSV file read.", totalBytesRead);
        } catch (IOException e) {
            throw new MtsReportException(e.getMessage());
        }
    }

    @SuppressWarnings("java:S3776") //need complexity for readability
    private List<CcfChange> parseInputCcfChangeCsvExport(InputStream inputStream) throws MtsReportException {
        try (CSVParser parser = new CSVParser(new InputStreamReader(inputStream), CSVFormat.EXCEL.withHeader().withDelimiter(','))) {
            List<CcfChange> ccfChanges = new ArrayList<>();

            for (CSVRecord csvRecord : parser) {
                int index = 0;
                CcChangeImpl ccfChange = new CcChangeImpl();
                String timeStampStr = csvRecord.get(index);

                if (timeStampStr != null && !timeStampStr.isEmpty()) {
                    ccfChange.setTimestamp(formatter.parse(timeStampStr));
                }

                String bookmakerIdValue = csvRecord.get(++index);
                if (bookmakerIdValue != null && !bookmakerIdValue.isEmpty()) {
                    ccfChange.setBookmakerId(Integer.parseInt(bookmakerIdValue));
                }

                String subBookmakerIdValue = csvRecord.get(++index);
                if (subBookmakerIdValue != null && !subBookmakerIdValue.isEmpty()) {
                    ccfChange.setSubBookmakerId(Integer.parseInt(subBookmakerIdValue));
                }

                ccfChange.setSourceId(csvRecord.get(++index));

                String sourceTypeValue = csvRecord.get(++index);
                if (sourceTypeValue != null && !sourceTypeValue.isEmpty()) {
                    ccfChange.setSourceType(SourceType.valueOf(sourceTypeValue.toUpperCase()));
                }

                String ccfValue = csvRecord.get(++index);
                if (ccfValue != null && !ccfValue.isEmpty()) {
                    ccfChange.setCcf(Double.valueOf(ccfValue));
                }

                String previousCcfValue = csvRecord.get(index++);
                if (previousCcfValue != null && !previousCcfValue.isEmpty()) {
                    ccfChange.setPreviousCcf(Double.valueOf(previousCcfValue));
                }

                String sportIdValue = csvRecord.get(++index);
                if(sportIdValue != null && !sportIdValue.isEmpty()) {
                    ccfChange.setSportId(sportIdValue);
                }

                String sportNameValue = csvRecord.get(++index);
                if(sportNameValue != null && !sportNameValue.isEmpty()) {
                    ccfChange.setSportName(sportNameValue);
                }

                String liveValue = csvRecord.get(++index);
                if (liveValue != null && !liveValue.isEmpty()) {
                    ccfChange.setLive(Boolean.valueOf(liveValue));
                }
                ccfChanges.add(ccfChange);
            }
            return ccfChanges;
        } catch (Exception e) {
            throw new MtsReportException("Error parsing csv buffer!");
        }
    }

    private String getCacheKey(String username, String password) {
        return username + "\n" + password;
    }

    private AccessToken getAccessToken(String username, String password) throws MtsReportException {
        try {
            return accessTokenCache.get(getCacheKey(username, password));
        } catch (ExecutionException e) {
            logger.error("Error accessing token from cache.");
            throw new MtsReportException(e.getMessage());
        }
    }
}
