package com.labtrack.sampletracking.repository;

import com.labtrack.sampletracking.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {


    Batch findByBatchId(Long batchId);

    List<Batch> findBy();
}
