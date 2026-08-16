package com.labtrack.sampletracking.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private String batchDesc;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private String updatedBy;

    @Column(updatable = false, nullable = false)
    private String createdBy;

    @Column(updatable = false, nullable = false)
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "batchId")
    private List<Sample> sample;


    public Batch(int noOfSample, String batchDesc, String updatedBy, String createdBy) {
        this.noOfSample = noOfSample;
        this.batchDesc = batchDesc;
        this.updatedBy = updatedBy;
        this.createdBy = createdBy;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

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

    public List<Sample> getSample() {
        return sample;
    }

    public void setSample(List<Sample> sample) {
        this.sample = sample;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }


}
