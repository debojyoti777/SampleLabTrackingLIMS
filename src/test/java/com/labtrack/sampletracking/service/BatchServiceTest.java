package com.labtrack.sampletracking.service;

import com.labtrack.sampletracking.Exceptions.BatchNotFoundException;
import com.labtrack.sampletracking.Exceptions.IllegalUpdateException;
import com.labtrack.sampletracking.dto.BatchRequest;
import com.labtrack.sampletracking.dto.BatchResponse;
import com.labtrack.sampletracking.model.Batch;
import com.labtrack.sampletracking.model.Sample;
import com.labtrack.sampletracking.repository.BatchRepository;
import com.labtrack.sampletracking.repository.SampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BatchService}.
 * <p>
 * @author Debojyoti Mallick
 */
@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private SampleRepository sampleRepository;

    @InjectMocks
    private BatchService batchService;

    private Batch batch;
    private BatchRequest batchRequest;

    @BeforeEach
    void setUp() {
        batch = new Batch(2, "Morning blood panel run", "admin", "admin");
        ReflectionTestUtils.setField(batch, "batchId", 10L);

        batchRequest = new BatchRequest();
        batchRequest.setNoOfSample(2);
        batchRequest.setBatchDesc("Morning blood panel run");
        batchRequest.setSampleType("Blood");
        batchRequest.setSampleParameterList("Glucose,Cholesterol");
        batchRequest.setSampleDesc("Patient panel");
    }

    private Sample sampleFor(Batch owningBatch, long id) {
        Sample sample = new Sample("Patient panel", "Blood", "admin", "Glucose,Cholesterol");
        ReflectionTestUtils.setField(sample, "sampleId", id);
        sample.setBatchId(owningBatch);
        return sample;
    }

    // ---------------------------------------------------------------
    // createBatch
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("createBatch")
    class CreateBatch {

        @Test
        @DisplayName("creates the batch, creates exactly noOfSample samples linked to it, and returns a BatchResponse")
        void createBatch_createsBatchAndLinkedSamples() {
            when(batchRepository.save(any(Batch.class))).thenAnswer(inv -> {
                Batch b = inv.getArgument(0);
                ReflectionTestUtils.setField(b, "batchId", 10L);
                return b;
            });
            when(sampleRepository.saveAll(any())).thenAnswer(inv -> {
                List<Sample> passedIn = inv.getArgument(0);
                ArrayList<Sample> saved = new ArrayList<>();
                long nextId = 1L;
                for (Sample s : passedIn) {
                    ReflectionTestUtils.setField(s, "sampleId", nextId++);
                    saved.add(s);
                }
                return saved;
            });

            BatchResponse response = batchService.createBatch(batchRequest);

            assertNotNull(response);
            assertEquals(10L, response.getBatchId());
            assertEquals(2, response.getNoOfSample());
            assertEquals("Morning blood panel run", response.getBatchDesc());
            assertEquals(2, response.getSample().size());
            assertEquals("Blood", response.getSample().get(0).getSampleType());

            // batchRepository.save is called twice: once to create the batch
            // (to get a generated id), once again after samples are linked
            // and attached via newBatch.setSample(...).
            verify(batchRepository, times(2)).save(any(Batch.class));
            verify(sampleRepository, times(1)).saveAll(any());
        }

        @Test
        @DisplayName("each created sample is linked back to the newly created batch")
        void createBatch_samplesAreLinkedToTheBatch() {
            when(batchRepository.save(any(Batch.class))).thenAnswer(inv -> {
                Batch b = inv.getArgument(0);
                ReflectionTestUtils.setField(b, "batchId", 10L);
                return b;
            });
            when(sampleRepository.saveAll(any())).thenAnswer(inv -> new ArrayList<>((List<Sample>) inv.getArgument(0)));

            batchService.createBatch(batchRequest);

            ArgumentCaptor<List<Sample>> captor = ArgumentCaptor.forClass(List.class);
            verify(sampleRepository).saveAll(captor.capture());
            List<Sample> samplesSaved = captor.getValue();

            assertEquals(2, samplesSaved.size());
            for (Sample s : samplesSaved) {
                assertEquals(10L, s.getBatchId().getBatchId());
            }
        }

        @Test
        @DisplayName("with noOfSample = 0, creates the batch but no samples")
        void createBatch_withZeroSamples_createsNoSamples() {
            batchRequest.setNoOfSample(0);

            when(batchRepository.save(any(Batch.class))).thenAnswer(inv -> {
                Batch b = inv.getArgument(0);
                ReflectionTestUtils.setField(b, "batchId", 11L);
                return b;
            });
            when(sampleRepository.saveAll(any())).thenReturn(new ArrayList<>());

            BatchResponse response = batchService.createBatch(batchRequest);

            assertEquals(0, response.getSample().size());
            verify(sampleRepository).saveAll(List.of());
        }
    }

    // ---------------------------------------------------------------
    // listBatches
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("listBatches")
    class ListBatches {

        @Test
        @DisplayName("returns every batch, each converted with its samples")
        void listBatches_returnsAllConverted() {
            Sample s1 = sampleFor(batch, 1L);
            batch.setSample(List.of(s1));

            ArrayList<Batch> batches = new ArrayList<>();
            batches.add(batch);
            when(batchRepository.findBy()).thenReturn(batches);

            List<BatchResponse> results = batchService.listBatches();

            assertEquals(1, results.size());
            assertEquals(10L, results.get(0).getBatchId());
            assertEquals(1, results.get(0).getSample().size());
        }
    }

    // ---------------------------------------------------------------
    // getBatch
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("getBatch")
    class GetBatch {

        @Test
        @DisplayName("returns the batch as a BatchResponse when it exists")
        void getBatch_whenExists_returnsResponse() {
            Sample s1 = sampleFor(batch, 1L);
            batch.setSample(List.of(s1));

            when(batchRepository.existsById(10L)).thenReturn(true);
            when(batchRepository.findByBatchId(10L)).thenReturn(batch);

            BatchResponse response = batchService.getBatch(10L);

            assertEquals(10L, response.getBatchId());
            assertEquals(1, response.getSample().size());
        }

        @Test
        @DisplayName("throws BatchNotFoundException when the batch doesn't exist")
        void getBatch_whenNotExists_throwsException() {
            when(batchRepository.existsById(99L)).thenReturn(false);

            BatchNotFoundException ex = assertThrows(BatchNotFoundException.class,
                    () -> batchService.getBatch(99L));
            assertTrue(ex.getMessage().contains("99"));
            verify(batchRepository, never()).findByBatchId(anyLong());
        }
    }

    // ---------------------------------------------------------------
    // deleteBatch
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("deleteBatch")
    class DeleteBatch {

        @Test
        @DisplayName("throws BatchNotFoundException when the batch doesn't exist")
        void deleteBatch_whenNotExists_throwsException() {
            when(batchRepository.existsById(99L)).thenReturn(false);

            assertThrows(BatchNotFoundException.class,
                    () -> batchService.deleteBatch(99L, false));
            verify(batchRepository, never()).delete(any(Batch.class));
        }

        @Test
        @DisplayName("soft delete (forceDelete=false): deletes the batch when no samples are associated")
        void deleteBatch_softDelete_noSamples_deletesBatch() {
            when(batchRepository.existsById(10L)).thenReturn(true);
            when(batchRepository.findByBatchId(10L)).thenReturn(batch);
            when(sampleRepository.countSampleByBatchId(batch)).thenReturn(0);

            batchService.deleteBatch(10L, false);

            verify(batchRepository).delete(batch);
            verify(sampleRepository, never()).deleteAll(any());
        }

        @Test
        @DisplayName("soft delete (forceDelete=false): throws IllegalUpdateException when samples ARE associated")
        void deleteBatch_softDelete_hasSamples_throwsException() {
            when(batchRepository.existsById(10L)).thenReturn(true);
            when(batchRepository.findByBatchId(10L)).thenReturn(batch);
            when(sampleRepository.countSampleByBatchId(batch)).thenReturn(3);

            assertThrows(IllegalUpdateException.class,
                    () -> batchService.deleteBatch(10L, false));
            verify(batchRepository, never()).delete(any(Batch.class));
        }

        @Test
        @DisplayName("force delete (forceDelete=true): deletes all associated samples and the batch, " +
                "without checking the sample count first")
        void deleteBatch_forceDelete_deletesSamplesAndBatch() {
            Sample s1 = sampleFor(batch, 1L);
            Sample s2 = sampleFor(batch, 2L);
            batch.setSample(List.of(s1, s2));

            when(batchRepository.existsById(10L)).thenReturn(true);
            when(batchRepository.findByBatchId(10L)).thenReturn(batch);

            batchService.deleteBatch(10L, true);

            verify(sampleRepository).deleteAll(List.of(s1, s2));
            verify(batchRepository).delete(batch);
            // force delete skips the count check entirely
            verify(sampleRepository, never()).countSampleByBatchId(any(Batch.class));
        }
    }
}
