package com.labtrack.sampletracking.controller;

import com.labtrack.sampletracking.dto.BatchRequest;
import com.labtrack.sampletracking.dto.BatchResponse;
import com.labtrack.sampletracking.model.Batch;
import com.labtrack.sampletracking.service.BatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller class for the Batch operations
 */
@RestController
@RequestMapping("/batch")
@Tag(name = "Batches", description = "Operations for tracking a batch of samples.")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    /**
     * This mapping is to create a Batch
     * @param batchRequest JSON object coming from the frontend as API call
     * @return Created BatchResponse object
     */
    @Operation(summary = "Create a new batch", description = "Creates a batch with requested amount of samples.")
    @PostMapping("/createBatch")
    public ResponseEntity<BatchResponse> createBatch(@Valid @RequestBody BatchRequest batchRequest)
    {
        BatchResponse createdBatch = batchService.createBatch(batchRequest);
        BatchResponse batchResponse = new BatchResponse(createdBatch.getBatchId(), createdBatch.getNoOfSample(), createdBatch.getBatchDesc(),
                createdBatch.getCreateDate(),createdBatch.getSample());
        return ResponseEntity.status(HttpStatus.CREATED).body(batchResponse);
    }

    /**
     * @return All the Batch records available in the format of BatchResponse object.
     */
    @Operation(summary = "List all the Batches")
    @GetMapping("/listBatches")
    public ResponseEntity<List<BatchResponse>> listBatches()
    {
        List<BatchResponse> batches = batchService.listBatches();
        return ResponseEntity.status(HttpStatus.OK).body(batches);
    }

    /**
     * This mapping is to find and get back a specific batch from a batch ID.
     * @param id Batch ID of the batch that needs to be searched
     * @return The BatchResponse object
     */
    @Operation(summary = "Get a batch by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch found."),
            @ApiResponse(responseCode = "404", description = "No batch exists with that ID.")
    })
    @GetMapping("/getBatch/{id}")
    public ResponseEntity<BatchResponse> getBatch(@PathVariable Long id)
    {
        BatchResponse batch = batchService.getBatch(id);
        return ResponseEntity.status(HttpStatus.OK).body(batch);
    }

    /**
     * This mapping is to delete a batch
     * @param id The Batch ID of the batch that needs to be deleted
     * @param forceDelete This takes a boolean value. True if we want to delete the batch and all the samples
     *                    associated with the batch. False if we only want to delete the batch if no samples
     *                    are associated with it.
     * @return Empty body to indicate deletion successful.
     */
    @Operation(summary = "Delete a batch by ID", description = "If there are samples associated with the batch, we need to pass " +
            "forceDelete as true , then it will delete the samples with the batch. If it is false, it will not delete a Batch unless" +
            "it is an empty batch.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch successfully deleted."),
            @ApiResponse(responseCode = "404", description = "No batch exists with that ID")
    })
    @DeleteMapping("/deleteBatch/{id}")
    public ResponseEntity<Batch> deleteBatch(@PathVariable Long id,
                                             @RequestParam(name = "forceDelete", defaultValue = "false") boolean forceDelete)
    {
        batchService.deleteBatch(id, forceDelete);
        return ResponseEntity.noContent().build();
    }

}
