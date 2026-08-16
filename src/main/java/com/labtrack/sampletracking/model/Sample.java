package com.labtrack.sampletracking.model;

import jakarta.persistence.*;
import com.labtrack.sampletracking.model.SampleStatus;

import java.time.LocalDateTime;

/**
 * Database table schema object class that is handled by the Hibernate entity to create the Sample table in the database.
 */
@Entity
@Table(name = "samples")
public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long sampleId;

    @Column()
    private String sampleDesc;

    @Column(updatable = false, nullable = false)
    private String sampleType;

    @Column(nullable = false)
    private String sampleStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private String updatedBy;

    @Column(updatable = false, nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String parameterList;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batchId", nullable = false)
    private Batch batchId;

    @Column
    private double value ;

    @Column(updatable = false, nullable = false)
    private LocalDateTime lastUpdated;

    protected Sample() {
        // required by JPA
    }

    public Sample(String sampleDesc, String sampleType, String createdBy, String parameterList) {
        this.sampleType = sampleType;
        this.sampleDesc = sampleDesc;
        this.sampleStatus = SampleStatus.received;
        this.createDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
        this.parameterList = parameterList;
        this.value=0.0; // initializing the values for checking
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getSampleId() {
        return sampleId;
    }

    public String getSampleDesc() {
        return sampleDesc;
    }

    public void setSampleDesc(String sampleDesc) {
        this.sampleDesc = sampleDesc;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getSampleType() {
        return sampleType;
    }

    public String getStatus() {
        return sampleStatus;
    }

    public void setStatus(String status) {
        this.sampleStatus = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getParameterList() {
        return parameterList;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Batch getBatchId() {
        return batchId;
    }

    public void setBatchId(Batch batchId) {
        this.batchId = batchId;
    }
}
