package com.sportradar.mts.sdk.api.impl;

import com.sportradar.mts.sdk.api.CcfChange;
import com.sportradar.mts.sdk.api.enums.SourceType;

import java.util.Date;

public class CcfChangeImpl implements CcfChange {
    private Date timestamp;
    private Integer bookmakerId;
    private Integer subBookmakerId;
    private String sourceId;
    private SourceType sourceType;
    private Double ccf;
    private Double previousCcf;
    private String sportId;
    private String sportName;
    private Boolean live;

    public CcfChangeImpl() {
    }

    @SuppressWarnings("java:S107") // Methods should not have too many parameters
    public CcfChangeImpl(Date timestamp,
                         Integer bookmakerId,
                         Integer subBookmaker,
                         String sourceId,
                         SourceType sourceType,
                         Double ccf,
                         Double previousCcf,
                         String sportId,
                         String sportName,
                         Boolean live) {
        this.timestamp = timestamp;
        this.bookmakerId = bookmakerId;
        this.subBookmakerId = subBookmakerId;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.ccf = ccf;
        this.previousCcf = previousCcf;
        this.sportId = sportId;
        this.sportName = sportName;
        this.live = live;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Integer getBookmakerId() {
        return bookmakerId;
    }

    public void setBookmakerId(Integer bookmakerId) {
        this.bookmakerId = bookmakerId;
    }

    @Override
    public Integer getSubBookmakerId() {
        return subBookmakerId;
    }

    public void setSubBookmakerId(Integer subBookmakerId) {
        this.subBookmakerId = subBookmakerId;
    }

    @Override
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public Double getCcf() {
        return ccf;
    }

    public void setCcf(Double ccf) {
        this.ccf = ccf;
    }

    @Override
    public Double getPreviousCcf() {
        return previousCcf;
    }

    public void setPreviousCcf(Double previousCcf) {
        this.previousCcf = previousCcf;
    }

    @Override
    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    @Override
    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    @Override
    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }
}
