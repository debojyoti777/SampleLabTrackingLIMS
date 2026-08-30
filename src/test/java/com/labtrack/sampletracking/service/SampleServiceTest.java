package com.labtrack.sampletracking.service;

import com.labtrack.sampletracking.Exceptions.IllegalUpdateException;
import com.labtrack.sampletracking.Exceptions.SampleNotFoundException;
import com.labtrack.sampletracking.dto.SampleRequest;
import com.labtrack.sampletracking.dto.SampleSummary;
import com.labtrack.sampletracking.model.Sample;
import com.labtrack.sampletracking.model.SampleStatus;
import com.labtrack.sampletracking.repository.SampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SampleService}.
 * @author Debojyoti Mallick
 */
@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SampleRepository sampleRepository;

    @InjectMocks
    private SampleService sampleService;

    private Sample sample;

    @BeforeEach
    void setUp() {
        sample = new Sample("Routine blood panel", "Blood", "admin", "Glucose,Cholesterol");
        ReflectionTestUtils.setField(sample, "sampleId", 1L);
    }

    // ---------------------------------------------------------------
    // createSample
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("createSample")
    class CreateSample {

        @Test
        @DisplayName("builds a Sample from the request, saves it, and returns it as a SampleSummary")
        void createSample_savesAndReturnsSummary() {
            SampleRequest request = new SampleRequest();
            request.setSampleType("Blood");
            request.setParameterList("Glucose,Cholesterol");
            request.setSampleDesc("Routine blood panel");

            when(sampleRepository.save(any(Sample.class))).thenAnswer(inv -> inv.getArgument(0));

            SampleSummary created = sampleService.createSample(request);

            assertNotNull(created);
            assertEquals("Blood", created.getSampleType());
            assertEquals("Glucose,Cholesterol", created.getParameterList());
            assertEquals("Routine blood panel", created.getSampleDesc());
            assertEquals(SampleStatus.received, created.getSampleStatus());
            assertEquals(0.0, created.getValue());
            verify(sampleRepository, times(1)).save(any(Sample.class));
        }
    }

    // ---------------------------------------------------------------
    // getSample
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("getSample")
    class GetSample {

        @Test
        @DisplayName("returns the sample as a SampleSummary when the id exists")
        void getSample_whenExists_returnsSummary() {
            when(sampleRepository.existsById(1L)).thenReturn(true);
            when(sampleRepository.findBySampleId(1L)).thenReturn(sample);

            SampleSummary result = sampleService.getSample(1L);

            assertNotNull(result);
            assertEquals(1L, result.getSampleId());
            verify(sampleRepository).findBySampleId(1L);
        }

        @Test
        @DisplayName("throws SampleNotFoundException when the id does not exist")
        void getSample_whenNotExists_throwsException() {
            when(sampleRepository.existsById(99L)).thenReturn(false);

            SampleNotFoundException ex = assertThrows(SampleNotFoundException.class,
                    () -> sampleService.getSample(99L));
            assertTrue(ex.getMessage().contains("99"));
            verify(sampleRepository, never()).findBySampleId(anyLong());
        }
    }

    // ---------------------------------------------------------------
    // searchSamples
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("searchSamples")
    class SearchSamples {

        @Test
        @DisplayName("searches by sample status (uppercases the value)")
        void searchSamples_bySampleStatus() {
            when(sampleRepository.findBySampleStatus("RECEIVED")).thenReturn(List.of(sample));

            List<SampleSummary> results = sampleService.searchSamples("sampleStatus", "received");

            assertEquals(1, results.size());
            verify(sampleRepository).findBySampleStatus("RECEIVED");
        }

        @Test
        @DisplayName("searches by sample type")
        void searchSamples_bySampleType() {
            when(sampleRepository.findBySampleType("Blood")).thenReturn(List.of(sample));

            List<SampleSummary> results = sampleService.searchSamples("sampleType", "Blood");

            assertEquals(1, results.size());
            assertEquals("Blood", results.get(0).getSampleType());
            verify(sampleRepository).findBySampleType("Blood");
        }

        @Test
        @DisplayName("searches by createdBy")
        void searchSamples_byCreatedBy() {
            when(sampleRepository.findByCreatedBy("admin")).thenReturn(List.of(sample));

            List<SampleSummary> results = sampleService.searchSamples("createdBy", "admin");

            assertEquals(1, results.size());
            verify(sampleRepository).findByCreatedBy("admin");
        }

        @Test
        @DisplayName("searches by parameterList")
        void searchSamples_byParameterList() {
            when(sampleRepository.findByParameterList("Glucose,Cholesterol")).thenReturn(List.of(sample));

            List<SampleSummary> results = sampleService.searchSamples("parameterList", "Glucose,Cholesterol");

            assertEquals(1, results.size());
            verify(sampleRepository).findByParameterList("Glucose,Cholesterol");
        }

        @Test
        @DisplayName("searches by sampleDesc (starts-with match)")
        void searchSamples_bySampleDesc() {
            when(sampleRepository.findBySampleDescStartingWith("Routine")).thenReturn(List.of(sample));

            List<SampleSummary> results = sampleService.searchSamples("sampleDesc", "Routine");

            assertEquals(1, results.size());
            verify(sampleRepository).findBySampleDescStartingWith("Routine");
        }

        @Test
        @DisplayName("is case-insensitive and ignores spaces in the column name")
        void searchSamples_columnNameIsNormalized() {
            when(sampleRepository.findBySampleType("Blood")).thenReturn(List.of(sample));

            List<SampleSummary> results = sampleService.searchSamples("Sample Type", "Blood");

            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("throws IllegalUpdateException for a column that isn't in isValidColumn at all")
        void searchSamples_invalidColumn_throwsException() {
            assertThrows(IllegalUpdateException.class,
                    () -> sampleService.searchSamples("notARealColumn", "whatever"));
            verifyNoInteractions(sampleRepository);
        }

        @Test
        @DisplayName("Returns all the samples in this situation as these columns are not yet eligible for search")
        void searchSamples_validButUnhandledColumn_returnsAllSamplesInstead() {
            when(sampleRepository.findBy()).thenReturn(List.of(sample));

            List<SampleSummary> results = sampleService.searchSamples("value", "0.0");

            assertEquals(1, results.size());
            verify(sampleRepository).findBy();
        }
    }

    // ---------------------------------------------------------------
    // listSamples
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("listSamples")
    class ListSamples {

        @Test
        @DisplayName("returns every sample from the repository as SampleSummaries")
        void listSamples_returnsAll() {
            when(sampleRepository.findBy()).thenReturn(List.of(sample));

            List<SampleSummary> results = sampleService.listSamples();

            assertEquals(1, results.size());
            verify(sampleRepository).findBy();
        }
    }

    // ---------------------------------------------------------------
    // updateStatus
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("updateStatus")
    class UpdateStatus {

        @Test
        @DisplayName("updates to a normal (non-completed) status")
        void updateStatus_toInProgress_succeeds() {
            when(sampleRepository.findBySampleId(1L)).thenReturn(sample);
            when(sampleRepository.save(any(Sample.class))).thenAnswer(inv -> inv.getArgument(0));

            SampleSummary updated = sampleService.updateStatus(1L, SampleStatus.inProgress);

            assertEquals(SampleStatus.inProgress, updated.getSampleStatus());
            verify(sampleRepository).save(sample);
        }

        @Test
        @DisplayName("throws when moving to COMPLETED while value is still 0.0")
        void updateStatus_toCompleted_withZeroValue_throwsException() {
            when(sampleRepository.findBySampleId(1L)).thenReturn(sample);

            assertThrows(IllegalUpdateException.class,
                    () -> sampleService.updateStatus(1L, SampleStatus.completed));
            verify(sampleRepository, never()).save(any(Sample.class));
        }

        @Test
        @DisplayName("succeeds moving to COMPLETED once a real value has been entered")
        void updateStatus_toCompleted_withValueSet_succeeds() {
            sample.setValue(45.5);
            when(sampleRepository.findBySampleId(1L)).thenReturn(sample);
            when(sampleRepository.save(any(Sample.class))).thenAnswer(inv -> inv.getArgument(0));

            SampleSummary updated = sampleService.updateStatus(1L, SampleStatus.completed);

            assertEquals(SampleStatus.completed, updated.getSampleStatus());
        }

        @Test
        @DisplayName("throws SampleNotFoundException when the Sample ID does not exist")
        void updateStatus_sampleNotFound() {
            when(sampleRepository.findBySampleId(99L)).thenReturn(null);

            assertThrows(SampleNotFoundException.class,
                    () -> sampleService.updateStatus(99L, SampleStatus.inProgress));
        }
    }

    // ---------------------------------------------------------------
    // enterOrUpdateValue
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("enterOrUpdateValue")
    class EnterOrUpdateValue {

        @Test
        @DisplayName("first value entry (blank -> value) auto-transitions status to IN_PROGRESS")
        void enterOrUpdateValue_firstEntry_transitionsToInProgress() {
            // sample.value starts at 0.0 from the constructor ("blank")
            when(sampleRepository.getSampleBySampleIdAndParameterList(1L, "Glucose"))
                    .thenReturn(sample);
            // updateStatus(...) is called internally and re-fetches via findBySampleId
            when(sampleRepository.findBySampleId(1L)).thenReturn(sample);
            when(sampleRepository.save(any(Sample.class))).thenAnswer(inv -> inv.getArgument(0));

            SampleSummary updated = sampleService.enterOrUpdateValue(1L, "Glucose", 99.9);

            assertEquals(99.9, updated.getValue());
            assertEquals(SampleStatus.inProgress, updated.getSampleStatus());
            // saved twice: once inside updateStatus, once for the value itself
            verify(sampleRepository, times(2)).save(any(Sample.class));
        }

        @Test
        @DisplayName("a later value correction does not re-trigger the status transition")
        void enterOrUpdateValue_subsequentEntry_doesNotRetriggerTransition() {
            sample.setValue(50.0);
            sample.setStatus(SampleStatus.inProgress);
            when(sampleRepository.getSampleBySampleIdAndParameterList(1L, "Glucose"))
                    .thenReturn(sample);
            when(sampleRepository.save(any(Sample.class))).thenAnswer(inv -> inv.getArgument(0));

            SampleSummary updated = sampleService.enterOrUpdateValue(1L, "Glucose", 55.0);

            assertEquals(55.0, updated.getValue());
            assertEquals(SampleStatus.inProgress, updated.getSampleStatus());
            // only one save this time - updateStatus's extra save path never runs
            verify(sampleRepository, times(1)).save(any(Sample.class));
            verify(sampleRepository, never()).findBySampleId(anyLong());
        }

        @Test
        @DisplayName("throws SampleNotFoundException when the Sample ID does not exist")
        void enterOrUpdateValue_noMatch() {
            when(sampleRepository.getSampleBySampleIdAndParameterList(1L, "Unknown"))
                    .thenReturn(null);

            assertThrows(SampleNotFoundException.class,
                    () -> sampleService.enterOrUpdateValue(1L, "Unknown", 10.0));
        }
    }

    // ---------------------------------------------------------------
    // deleteSample
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("deleteSample")
    class DeleteSample {

        @Test
        @DisplayName("deletes the sample when it exists")
        void deleteSample_whenExists_deletesIt() {
            when(sampleRepository.findBySampleId(1L)).thenReturn(sample);

            sampleService.deleteSample(1L);

            verify(sampleRepository).delete(sample);
        }

        @Test
        @DisplayName("throws SampleNotFoundException instead of deleting when the sample doesn't exist")
        void deleteSample_whenNotFound_throwsAndNeverDeletes() {
            when(sampleRepository.findBySampleId(99L)).thenReturn(null);

            assertThrows(SampleNotFoundException.class,
                    () -> sampleService.deleteSample(99L));
            verify(sampleRepository, never()).delete(any(Sample.class));
        }
    }
}
