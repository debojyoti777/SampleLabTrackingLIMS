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
 *
 */
@RestController
@RequestMapping("/samples")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Sample> registerSample(@Valid @RequestBody SampleRequest request) {
        Sample created = sampleService.createSample(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/getsample/{id}")
    public ResponseEntity<Sample> getSample(@PathVariable Long id) {
        return ResponseEntity.ok(sampleService.getSample(id));
    }

    /**
     *
     * @return
     */
    @GetMapping("/listsamples")
    public ResponseEntity<List<Sample>> listSamples() {
        return ResponseEntity.ok(sampleService.listSamples());
    }

    /**
     *
     * @param columnToSearch
     * @param searchValue
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<List<Sample>> searchSamples(
            @RequestParam String columnToSearch, @RequestParam String searchValue){
        List<Sample> sample = sampleService.searchSamples(columnToSearch,searchValue);
        return ResponseEntity.ok(sampleService.searchSamples(columnToSearch,searchValue));
    }

    /**
     *
     * @param id
     * @param updatedStatus
     * @return
     */
    @PatchMapping("/updatestatus/{id}")
    public ResponseEntity<Sample> updateStatus(
            @PathVariable Long id,
            @RequestParam String updatedStatus ) {
        return ResponseEntity.ok(sampleService.updateStatus(id, updatedStatus));
    }

    /**
     *
     * @param id
     * @param value
     * @return
     */
    @PatchMapping("/updatevalue/{id}")
    public ResponseEntity<Sample> enterOrUpdateValue(
            @PathVariable Long id, @RequestParam String parameterList, @RequestParam double value){
        return ResponseEntity.ok(sampleService.enterOrUpdateValue(id,parameterList,value));
    }

    /**
     * @param id
     * @return
     */

    @DeleteMapping("/deletesample/{id}")
    public ResponseEntity<Sample> deleteSample(@PathVariable Long id) {
        sampleService.deleteSample(id);
        return ResponseEntity.noContent().build();
    }
}
