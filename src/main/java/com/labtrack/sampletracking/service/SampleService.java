package com.labtrack.sampletracking.service;

import com.labtrack.sampletracking.Exceptions.SampleRuntimeException;
import com.labtrack.sampletracking.dto.SampleRequest;
import com.labtrack.sampletracking.model.*;
import com.labtrack.sampletracking.model.SampleStatus;
import com.labtrack.sampletracking.repository.SampleRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This is the <b>Main Business Logic </b> layer code of the application. <br>
 * Many things go on here that is not visible to the user but does whatever the user wants it to do.
 *
 * @author Debojyoti Mallick
 */
@Service
public class SampleService {

    private final SampleRepository sampleRepository;
    SampleRuntimeException sre;
    private final String sampleDescStartsWith = "sampledescstartswith";

    public SampleService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    /**
     * @param request Request from controller class to create a new Sample
     * @return Created Sample
     */
    public Sample createSample(SampleRequest request) {
//        sampleRepository.findByBarcode(request.getBarcode()).ifPresent(s -> {
//            throw new IllegalArgumentException("Sample with barcode " + request.getBarcode() + " already exists");
//        });
        Sample sample = new Sample(request.getSampleDesc(), request.getSampleType(), "admin", request.getParameterList());
        return sampleRepository.save(sample);
    }

    /**
     * @param sampleId The Sample ID that the user wants to get
     * @return The Sample object
     */
    @NotNull
    public Sample getSample(Long sampleId) {
        if (sampleRepository.existsById(sampleId))
            return sampleRepository.findBySampleId(sampleId);
        else
            sre.sampleNotFoundException(sampleId);
        return null;
    }

    /**
     * @param columnToSearch The column that the client want to search. User has the option to search any column for the info
     *                       except ofc the value and Sample ID column.
     * @param value          The value against which the user wants to search.
     * @return List of the Samples coming back from the search.
     */
    @NotNull
    public List<Sample> searchSamples(String columnToSearch, String value) {
        String column = columnToSearch.toLowerCase().replace(" ", "");
        if(!isValidColumn(column))
            sre.illegalUpdateException();
        return switch (column) {
            case Columns.sampleStatus -> sampleRepository.findBySampleStatus(value.toUpperCase());
            case Columns.sampleType -> sampleRepository.findBySampleType(value);
            case Columns.createdBy -> sampleRepository.findByCreatedBy(value);
            case Columns.parameterList -> sampleRepository.findByParameterList(value);
            case sampleDescStartsWith -> sampleRepository.findBySampleDescStartingWith(value);
            default -> sampleRepository.findBy();
        };
    }

    /**
     * @return all the samples available
     */
    public List<Sample> listSamples(){
        return sampleRepository.findBy();
    }

    /**
     * @param id Sample ID of the sample to be updated
     * @param newStatus The new status of the sample
     * @return - sample with the new status
     */
    public Sample updateStatus(Long id, String newStatus) {
        Sample sample = getSample(id);
        if (newStatus.equalsIgnoreCase(SampleStatus.completed)) {
            if (sample.getValue() == 0.0)
                sre.illegalUpdateException();
        }
        sample.setStatus(newStatus);
        return sampleRepository.save(sample);
    }

    /**
     * @param id Sample ID of the sample whose values needs to be entered
     * @param value The value
     * @return - sample with the saved status
     */
    public Sample enterOrUpdateValue(Long id,String parameterList, double value) {
        Sample sample = sampleRepository.getSampleBySampleIdAndParameterList(id,parameterList);
        if (sample.getValue() == 0.0)
            updateStatus(sample.getSampleId(), SampleStatus.inProgress);
        sample.setValue(value);
        return sampleRepository.save(sample);
    }

    /**
     *
     * @param id
     */
    public void deleteSample(Long id) {
        Sample sample = getSample(id);
        sampleRepository.delete(sample);
    }

    /**
     *
     * @param column
     * @return
     */
    private boolean isValidColumn(String column)
    {
        return switch (column) {
            case Columns.sampleId, Columns.sampleStatus, Columns.sampleDesc, Columns.createDate, Columns.createdBy,
                 Columns.parameterList, Columns.updatedBy, Columns.value, Columns.sampleType -> true;
            default -> false;
        };
    }
}
