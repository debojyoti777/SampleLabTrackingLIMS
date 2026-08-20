package com.labtrack.sampletracking.service;


import com.labtrack.sampletracking.Exceptions.BatchNotFoundException;
import com.labtrack.sampletracking.Exceptions.IllegalUpdateException;
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
     * This method creates a new batch and then creates and associates the given no of samples.
     * @param batchRequest Batch Request JSON object from the Controller layer.
     * @return The created Batch.
     */
    public Batch createBatch(BatchRequest batchRequest) {
        Batch batch = new Batch(batchRequest.getNoOfSample(), batchRequest.getBatchDesc(), "admin", "admin");
        Batch newBatch = batchRepository.save(batch);
        List<Sample> newSamples = createBatchSamples(newBatch.getNoOfSample(), newBatch, batchRequest );
        newBatch.setSample(newSamples);
        return batchRepository.save(newBatch);
    }

    /**
     * This method deletes a batch and conditionally deletes all the samples associated with it as well.
     * @param batchId The Batch ID of the Batch that needs to be deleted.
     * @param forceDelete - This takes a boolean value. True if we want to delete the batch and all the samples
     *                      associated with the batch. False if we only want to delete the batch if no samples
     *                      are associated with it.
     */
    public void deleteBatch(Long batchId, boolean forceDelete) {
        int noOfSamples;
        Batch batch = batchRepository.findByBatchId(batchId);
        if (!forceDelete) {
            noOfSamples = sampleRepository.countSampleByBatchId(batch);
            if (noOfSamples == 0)
                batchRepository.delete(batch);
            else
                throw new IllegalUpdateException("This deletion cannot be done as Samples are associated with it.\n" +
                        "If you want to force delete , send Yes from force delete to delete the Batch and the Samples.");
        }
        else
        {
            List<Sample> samplesToBeDeleted = batch.getSample();
            sampleRepository.deleteAll(samplesToBeDeleted);
            batchRepository.delete(batch);
        }
    }

    /**
     * @return All the batches available.
     */
    public List<Batch> listBatches()
    {
        return batchRepository.findBy();
    }

    /**
     * This is used to search and retrieve a single Batch on the basis of Batch ID.
     * @param batchId The Batch Id that needs to be returned
     * @return The Batch
     */
    public Batch getBatch(Long batchId)
    {
        if(!batchRepository.existsById(batchId))
            throw new BatchNotFoundException(batchId);
        return batchRepository.findByBatchId(batchId);
    }

    /**
     * This is a helper method to create the new samples that will be associated with the batch.
     * @param noOfSamples The no of samples that are needed to be associate with the batch
     * @param newBatch The newly created batch
     * @param batchRequest The Batch Request JSON object from the Controller layer
     * @return List of all the new samples that got added for this batch in the DB.
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
