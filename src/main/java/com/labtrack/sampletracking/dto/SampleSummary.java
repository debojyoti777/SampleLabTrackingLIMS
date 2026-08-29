package com.labtrack.sampletracking.dto;
import java.time.LocalDateTime;

public class SampleSummary {

    private Long sampleId;
    private String sampleDesc;
    private String sampleType;
    private String sampleStatus;
    private LocalDateTime createDate;
    private String parameterList;
    private double value ;

    public SampleSummary(Long sampleId, String sampleDesc, String sampleType, String sampleStatus, LocalDateTime createDate, String parameterList, double value) {
        this.sampleId = sampleId;
        this.sampleDesc = sampleDesc;
        this.sampleType = sampleType;
        this.sampleStatus = sampleStatus;
        this.createDate = createDate;
        this.parameterList = parameterList;
        this.value = value;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleDesc() {
        return sampleDesc;
    }

    public void setSampleDesc(String sampleDesc) {
        this.sampleDesc = sampleDesc;
    }

    public String getSampleStatus() {
        return sampleStatus;
    }

    public void setSampleStatus(String sampleStatus) {
        this.sampleStatus = sampleStatus;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getParameterList() {
        return parameterList;
    }

    public void setParameterList(String parameterList) {
        this.parameterList = parameterList;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
