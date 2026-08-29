package com.labtrack.sampletracking.Exceptions;

/**
 * Thrown when a Batch lookup by ID finds no matching record.
 *
 */

public class BatchNotFoundException extends RuntimeException{

    public BatchNotFoundException(Long batchId)
    {
        super("Batch with " + batchId + " Batch ID was not found");
    }

    public String getErrorMessage()
    {
        return super.getMessage();
    }
}
