package com.labtrack.sampletracking.util;

import com.labtrack.sampletracking.dto.BatchResponse;
import com.labtrack.sampletracking.dto.SampleSummary;
import com.labtrack.sampletracking.model.Batch;
import com.labtrack.sampletracking.model.Sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting Batch and Sample objects to a Standard API response. This is required to not expose the DB
 * schema to the controller class.
 */
public class ConvertUtil {
    /**
     * Overloaded util method to change only 1 Batch Object to the API response.
     * @param batchToSearch The Batch that needs to be converted
     * @return The BatchResponse object
     */
    public BatchResponse convertToBatchResponse(Batch batchToSearch)
    {
        ArrayList<Batch> batch = new ArrayList<>(1);
        batch.add(batchToSearch);
        List<BatchResponse> batchReturned = convertToBatchResponse(batch);
        return batchReturned.get(0);
    }

    /**
     * This is a util method to convert the Batch object to a standard API response , to not expose the model schema
     * to the Controller class
     * @param batches List of the Batches that need to be converted
     * @return List of BatchResponse objects
     */
    public List<BatchResponse> convertToBatchResponse(ArrayList<Batch> batches)
    {
        ArrayList<BatchResponse> batchList = new ArrayList<>();
        for(Batch batch : batches)
        {
            List<Sample> samples = batch.getSample();
            batchList.add(new BatchResponse(batch.getBatchId(), batch.getNoOfSample(), batch.getBatchDesc(),
                    batch.getCreateDate(), convertToSampleSummary(samples)));
        }
        return batchList;
    }

    /**
     * Overloaded util method to change only 1 Sample Object to the API response.
     * @param newSamples The Sample that needs to be converted
     * @return The SampleSummary object
     */
    public SampleSummary convertToSampleSummary(Sample newSamples)
    {
        ArrayList<Sample> sample = new ArrayList<>(1);
        sample.add(newSamples);
        List<SampleSummary> sampleReturned = convertToSampleSummary(sample);
        return sampleReturned.get(0);
    }


    /**
     * This is a util method to convert the Sample object to a standard API response , to not expose the model schema
     * to the Controller class
     * @param newSamples List of the Samples that need to be converted
     * @return List of SampleResponse objects
     */
    public List<SampleSummary> convertToSampleSummary(List<Sample> newSamples)
    {
        ArrayList <SampleSummary> samples = new ArrayList<>();
        for (Sample sample : newSamples) {
            samples.add(new SampleSummary(sample.getSampleId(), sample.getSampleDesc(), sample.getSampleType(),
                    sample.getStatus(), sample.getCreateDate(), sample.getParameterList() , sample.getValue()));
        }
        return samples;
    }
}
