package com.labtrack.sampletracking.repository;

import com.labtrack.sampletracking.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Interface class with all the straightforward queries required by the code logic.
 */
public interface SampleRepository extends JpaRepository<Sample, Long> {

    List<Sample> findBySampleStatus(String sampleStatus);

    Sample findBySampleId(Long sampleId);

    List<Sample> findBySampleType(String sampleType);

    List<Sample> findByCreatedBy(String createdBy);

    List<Sample> findByParameterList(String parameterList);

    List<Sample> findBySampleDescStartingWith(String sampleDescWord);

    List<Sample> findBy();

    Sample getSampleBySampleIdAndParameterList(Long sampleID, String parameterList);

}
