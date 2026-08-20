package com.labtrack.sampletracking.Exceptions;

/**
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
