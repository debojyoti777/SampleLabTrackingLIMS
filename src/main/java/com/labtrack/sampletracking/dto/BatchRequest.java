package com.labtrack.sampletracking.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Batch Request DTO object that is received in the API call as the request body from the frontend.
 */

public class BatchRequest {

    @Min(value = 1, message = "No of samples has to be min 1")
    private int noOfSample;
    private String batchDesc;

    @NotBlank(message = "Sample Parameter is required")
    private String sampleParameterList;

    @NotBlank(message = "Sample Type is required")
    private String sampleType;


    private String sampleDesc;

    public int getNoOfSample() {
        return noOfSample;
    }
    public void setNoOfSample(int noOfSample) {
        this.noOfSample = noOfSample;
    }
    public String getBatchDesc() {
        return batchDesc;
    }
    public void setBatchDesc(String batchDesc) {
        this.batchDesc = batchDesc;
    }

    public String getSampleParameterList() {
        return sampleParameterList;
    }

    public void setSampleParameterList(String sampleParameterList) {
        this.sampleParameterList = sampleParameterList;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getSampleDesc() {
        return sampleDesc;
    }

    public void setSampleDesc(String sampleDesc) {
        this.sampleDesc = sampleDesc;
    }
}
