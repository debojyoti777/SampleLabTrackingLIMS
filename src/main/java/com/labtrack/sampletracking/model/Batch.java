package com.labtrack.sampletracking.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Database table schema object class that is handled by the Hibernate entity to create the Batch table in the database.
 */
@Entity
@Table(name = "batches")
public class Batch {

    protected Batch() {
        //This is there because the JPA layer needs it.
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long batchId;

    @Column(updatable = false, nullable = false)
    private int noOfSample;

    @Column()
    private String sampleDesc;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private String updatedBy;

    @Column(updatable = false, nullable = false)
    private String createdBy;

    @Column(updatable = false, nullable = false)
    private LocalDateTime lastUpdated;

    public Batch( int noOfSample , Long batchId, String sampleDesc, LocalDateTime createDate, String updatedBy, String createdBy) {
        this.batchId = batchId;
        this.sampleDesc = sampleDesc;
        this.noOfSample = noOfSample;
        this.createDate = createDate;
        this.updatedBy = updatedBy;
        this.createdBy = createdBy;
        this.lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public int getNoOfSample() {
        return noOfSample;
    }

    public void setNoOfSample(int noOfSample) {
        this.noOfSample = noOfSample;
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

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

}
