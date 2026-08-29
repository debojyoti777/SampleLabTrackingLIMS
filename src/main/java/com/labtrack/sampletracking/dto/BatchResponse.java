package com.labtrack.sampletracking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BatchResponse {

    private Long batchId;
    private int noOfSample;
    private String batchDesc;
    private LocalDateTime createDate;
    private List<SampleSummary> sample;

    public BatchResponse(Long batchId, int noOfSample, String batchDesc, LocalDateTime createDate, List<SampleSummary> sample) {
        this.batchId = batchId;
        this.noOfSample = noOfSample;
        this.batchDesc = batchDesc;
        this.createDate = createDate;
        this.sample = sample;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getBatchDesc() {
        return batchDesc;
    }

    public void setBatchDesc(String batchDesc) {
        this.batchDesc = batchDesc;
    }

    public int getNoOfSample() {
        return noOfSample;
    }

    public void setNoOfSample(int noOfSample) {
        this.noOfSample = noOfSample;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public List<SampleSummary> getSample() {
        return sample;
    }

    public void setSample(List<SampleSummary> sample) {
        this.sample = sample;
    }
}
