package com.sportradar.mts.sdk.impl.libs.clientapi.filters;

import com.google.common.base.Preconditions;
import com.sportradar.mts.sdk.api.enums.SourceType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CcfChangeFilter {
    private String bookmakerId;
    private String subBookmakers;
    private String sourceType = "";
    private String startDate;
    private String endDate;
    private String sourceId = "";

    private final SimpleDateFormat formatter;

    public CcfChangeFilter(Date startDate, Date endDate, Integer bookmakerId, List<Integer> subBookmakerIds, String sourceId, SourceType sourceType, int defaultBookmakerId) {
        if (bookmakerId == null && defaultBookmakerId > 0) {
            bookmakerId = defaultBookmakerId;
        }
        Preconditions.checkNotNull(startDate);
        Preconditions.checkNotNull(endDate);
        Preconditions.checkNotNull(bookmakerId);

        this.formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        this.formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (sourceId != null) {
            this.sourceId = sourceId;
        }
        if (sourceType != null) {
            this.sourceType = sourceType.name();
        }
        this.bookmakerId = String.valueOf(bookmakerId);
        this.subBookmakers = listToParameter(subBookmakerIds);
        this.startDate = formatter.format(startDate);
        this.endDate = formatter.format(endDate);
    }

    public String getBookmakerId() {
        return bookmakerId;
    }

    public void setBookmakerId(String bookmakerId) {
        this.bookmakerId = bookmakerId;
    }

    public String getSubBookmakers() {
        return subBookmakers;
    }

    public void setSubBookmakers(String subBookmakers) {
        this.subBookmakers = subBookmakers;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    private String listToParameter(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toString());
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
