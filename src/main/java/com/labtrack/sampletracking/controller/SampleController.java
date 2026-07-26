package com.labtrack.sampletracking.controller;

import com.labtrack.sampletracking.dto.SampleRequest;
import com.labtrack.sampletracking.model.Sample;
import com.labtrack.sampletracking.service.SampleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.labtrack.sampletracking.model.Columns.parameterList;

/**
 * Controller class that receives the API call from the frontend and execute the logic inside.
 *
 * @author Debojyoti Mallick
 */
@RestController
@RequestMapping("/samples")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    /**
     * This mapping is to create a new sample
     * @param request JSON object from the frontend with the required fields
     * @return Response is the HTTP status code from this operation
     */
    @PostMapping("/create")
    public ResponseEntity<Sample> registerSample(@Valid @RequestBody SampleRequest request) {
        Sample created = sampleService.createSample(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * This mapping is get a specific sample against the Sample ID
     * @param id Sample ID that needs to be returned
     * @return The Sample JSON object to the frontend
     */
    @GetMapping("/getsample/{id}")
    public ResponseEntity<Sample> getSample(@PathVariable Long id) {
        return ResponseEntity.ok(sampleService.getSample(id));
    }

    /**
     * This mapping is to get all the samples available in the database
     * @return - The list of Sample objects retrieved
     */
    @GetMapping("/listsamples")
    public ResponseEntity<List<Sample>> listSamples() {
        return ResponseEntity.ok(sampleService.listSamples());
    }

    /**
     * This mapping is to search for a Sample against a specific column and a specific value
     * @param columnToSearch Column name of the column that needs to be searched
     * @param searchValue Value against which the search happens
     * @return List of the Sample objects that gets returned from the search
     */
    @GetMapping("/search")
    public ResponseEntity<List<Sample>> searchSamples(
            @RequestParam String columnToSearch, @RequestParam String searchValue){
        List<Sample> sample = sampleService.searchSamples(columnToSearch,searchValue);
        return ResponseEntity.ok(sampleService.searchSamples(columnToSearch,searchValue));
    }

    /**
     * This mapping is to update the status of a Sample according to the Sample ID.
     * @param id Sample ID of the sample that requires status update
     * @param updatedStatus The updated status that needs to be entered
     * @return Sample JSON object with the updated status
     */
    @PatchMapping("/updatestatus/{id}")
    public ResponseEntity<Sample> updateStatus(
            @PathVariable Long id,
            @RequestParam String updatedStatus ) {
        return ResponseEntity.ok(sampleService.updateStatus(id, updatedStatus));
    }

    /**
     * This mapping is to enter or the update the reading values of a Sample , against a Sample ID and a parameter list.
     * @param id Sample ID of the sample
     * @param value The reading value which needs to be entered
     * @param parameterList Parameter List inside the sample against which the reading needs to be entered
     * @return Sample JSON object with the updated value
     */
    @PatchMapping("/updatevalue/{id}")
    public ResponseEntity<Sample> enterOrUpdateValue(
            @PathVariable Long id, @RequestParam String parameterList, @RequestParam double value){
        return ResponseEntity.ok(sampleService.enterOrUpdateValue(id,parameterList,value));
    }

    /**
     * This mapping is to delete a specific sample with their Sample ID
     * @param id Sample ID of the sample that needs to be deleted
     * @return A no content body is returned signaling the success of the operation
     */

    @DeleteMapping("/deletesample/{id}")
    public ResponseEntity<Sample> deleteSample(@PathVariable Long id) {
        sampleService.deleteSample(id);
        return ResponseEntity.noContent().build();
    }
}
