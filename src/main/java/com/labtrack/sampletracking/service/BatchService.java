package com.labtrack.sampletracking.service;


import com.labtrack.sampletracking.dto.BatchRequest;
import com.labtrack.sampletracking.model.Batch;
import com.labtrack.sampletracking.model.Sample;
import com.labtrack.sampletracking.repository.BatchRepository;
import com.labtrack.sampletracking.repository.SampleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for all Batch operations
 */

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final SampleRepository sampleRepository;

    public BatchService(BatchRepository batchRepository, SampleRepository sampleRepository) {
        this.batchRepository = batchRepository;
        this.sampleRepository = sampleRepository;
    }

    /**
     *
     * @param batchRequest
     * @return
     */
    public Batch createBatch(BatchRequest batchRequest) {
        Batch batch = new Batch(batchRequest.getNoOfSample(), batchRequest.getBatchDesc(), "admin", "admin");
        Batch newBatch = batchRepository.save(batch);
        List<Sample> newSamples = createBatchSamples(newBatch.getNoOfSample(), newBatch, batchRequest );
        newBatch.setSample(newSamples);
        return batchRepository.save(newBatch);
    }

    /**
     *
     * @param noOfSamples
     * @param newBatch
     * @param batchRequest
     * @return
     */

    private List<Sample> createBatchSamples(int noOfSamples, Batch newBatch, BatchRequest batchRequest)
    {
        ArrayList<Sample> samplesToBeAdded = new ArrayList<>();
        for (int i = 0; i < noOfSamples; i++) {
            Sample sample = new Sample(batchRequest.getSampleDesc(),batchRequest.getSampleType(),
                    "admin", batchRequest.getSampleParameterList());
            sample.setBatchId(newBatch);
            samplesToBeAdded.add(sample);
        }
        return sampleRepository.saveAll(samplesToBeAdded);
    }
}
