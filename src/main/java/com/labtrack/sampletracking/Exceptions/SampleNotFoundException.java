package com.labtrack.sampletracking.Exceptions;

/**
 * Thrown when a Sample lookup by ID finds no matching record.
 *
 */

public class SampleNotFoundException extends RuntimeException{

    Long sampleId;

    public SampleNotFoundException(Long sampleId)
    {
        super("Sample with " + sampleId + " Sample ID is not found");
        this.sampleId = sampleId;
    }

    public String getErrorMessage()
    {
        return super.getMessage();
    }

}
