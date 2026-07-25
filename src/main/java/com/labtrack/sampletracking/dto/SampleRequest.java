package com.labtrack.sampletracking.dto;

import jakarta.validation.constraints.NotBlank;

public class SampleRequest {

    @NotBlank(message = "Sample Type is required")
    private String sampleType;
    @NotBlank(message = "Parameter List is required")
    private String parameterList;
    private String sampleDesc;
    private double value;
//    @NotBlank(message = "Updated By is required")
//    private String updateBy;

//    public String getUpdateBy() {
//        return updateBy;
//    }
//
//    public void setUpdateBy(String updateBy) {
//        this.updateBy = updateBy;
//    }



    public String getSampleStatus() {
        return sampleStatus;
    }

    private String sampleStatus;
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public String getSampleType() {
        return sampleType;
    }
    public String getSampleDesc() {
        return sampleDesc;
    }
    public void setSampleDesc(String sampleDesc) {
        this.sampleDesc = sampleDesc;
    }
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }
    public String getParameterList() {
        return parameterList;
    }
    public void setParameterList(String parameterList) {
        this.parameterList = parameterList;
    }

}
