package com.labtrack.sampletracking.controller;

import com.labtrack.sampletracking.dto.SampleRequest;
import com.labtrack.sampletracking.dto.SampleSummary;
import com.labtrack.sampletracking.model.Sample;
import com.labtrack.sampletracking.service.SampleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
/**
 * Controller class that receives the API call from the frontend and execute the logic inside.
 *
 * @author Debojyoti Mallick
 */
@RestController
@RequestMapping("/samples")
@Tag(name = "Samples", description = "Operations for tracking individual lab samples.")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService ) {
        this.sampleService = sampleService;
    }

    /**
     * This mapping is to create a new sample
     * @param request JSON object from the frontend with the required fields
     * @return Response is the HTTP status code from this operation with the SampleSummary body.
     */
    @Operation(summary = "Create a new sample", description = "Creates a sample with status RECEIVED.")
    @PostMapping("/create")
    public ResponseEntity<SampleSummary> createSample(@Valid @RequestBody SampleRequest request) {
        SampleSummary created = sampleService.createSample(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * This mapping is get a specific sample against the Sample ID
     * @param id Sample ID that needs to be returned
     * @return The SampleSummary JSON object to the frontend
     */
    @Operation(summary = "Get a sample by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sample found."),
            @ApiResponse(responseCode = "404", description = "No sample exists with that ID.")
    })
    @GetMapping("/getsample/{id}")
    public ResponseEntity<SampleSummary> getSample(@PathVariable Long id) {
        return ResponseEntity.ok(sampleService.getSample(id));
    }

    /**
     * This mapping is to get all the samples available in the database
     * @return - The list of SampleSummary objects retrieved
     */
    @Operation(summary = "List all the Samples")
    @GetMapping("/listsamples")
    public ResponseEntity<List<SampleSummary>> listSamples() {
        return ResponseEntity.ok(sampleService.listSamples());
    }

    /**
     * This mapping is to search for a Sample against a specific column and a specific value
     * @param columnToSearch Column name of the column that needs to be searched
     * @param searchValue Value against which the search happens
     * @return List of the SampleSummary objects that gets returned from the search
     */
    @Operation(summary = "Search for a sample using a specific column and value")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Samples are successfully searched."),
            @ApiResponse(responseCode = "403", description = "Wrong Column value has been entered.")
    })
    @GetMapping("/search")
    public ResponseEntity<List<SampleSummary>> searchSamples(
            @RequestParam String columnToSearch, @RequestParam String searchValue){
        return ResponseEntity.ok(sampleService.searchSamples(columnToSearch,searchValue));
    }

    /**
     * This mapping is to update the status of a Sample according to the Sample ID.
     * @param id Sample ID of the sample that requires status update
     * @param updatedStatus The updated status that needs to be entered
     * @return SampleSummary JSON object with the updated status
     */
    @Operation(summary = "Update the status of a specific sample")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sample status successfully updated."),
            @ApiResponse(responseCode = "403", description = "Invalid status requested."),
            @ApiResponse(responseCode = "404", description = "No sample exists with that ID")
    })
    @PatchMapping("/updatestatus/{id}")
    public ResponseEntity<SampleSummary> updateStatus(
            @PathVariable Long id,
            @RequestParam String updatedStatus ) {
        return ResponseEntity.ok(sampleService.updateStatus(id, updatedStatus));
    }

    /**
     * This mapping is to enter or the update the reading values of a Sample , against a Sample ID and a parameter list.
     * @param id Sample ID of the sample
     * @param value The reading value which needs to be entered
     * @param parameterList Parameter List inside the sample against which the reading needs to be entered
     * @return SampleSummary JSON object with the updated value
     */
    @Operation(summary = "Enter or Update the value of a sample")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Value has been successfully updated."),
            @ApiResponse(responseCode = "404", description = "No sample exists with that ID.")
    })
    @PatchMapping("/updatevalue/{id}")
    public ResponseEntity<SampleSummary> enterOrUpdateValue(
            @PathVariable Long id, @RequestParam String parameterList, @RequestParam double value){
        return ResponseEntity.ok(sampleService.enterOrUpdateValue(id,parameterList,value));
    }

    /**
     * This mapping is to delete a specific sample with their Sample ID
     * @param id Sample ID of the sample that needs to be deleted
     * @return A no content body is returned signaling the success of the operation
     */
    @Operation(summary = "Delete a sample by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sample successfully deleted."),
            @ApiResponse(responseCode = "404", description = "No sample exists with that ID")
    })
    @DeleteMapping("/deletesample/{id}")
    public ResponseEntity<Sample> deleteSample(@PathVariable Long id) {
        sampleService.deleteSample(id);
        return ResponseEntity.noContent().build();
    }
}
