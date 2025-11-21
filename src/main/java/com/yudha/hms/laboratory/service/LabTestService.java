package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.entity.LabTest;
import com.yudha.hms.laboratory.entity.LabTestCategory;
import com.yudha.hms.laboratory.entity.LabTestParameter;
import com.yudha.hms.laboratory.repository.LabTestRepository;
import com.yudha.hms.laboratory.repository.LabTestCategoryRepository;
import com.yudha.hms.laboratory.repository.LabTestParameterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Laboratory Test operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LabTestService {

    private final LabTestRepository labTestRepository;
    private final LabTestCategoryRepository labTestCategoryRepository;
    private final LabTestParameterRepository labTestParameterRepository;

    /**
     * Create new lab test
     */
    public LabTest createTest(LabTest test) {
        log.info("Creating new lab test: {}", test.getName());

        // Validate unique code
        if (labTestRepository.findByTestCodeAndDeletedAtIsNull(test.getTestCode()).isPresent()) {
            throw new IllegalArgumentException("Test code already exists: " + test.getTestCode());
        }

        // Validate LOINC code uniqueness if provided
        if (test.getLoincCode() != null && !test.getLoincCode().isEmpty()) {
            if (labTestRepository.findByLoincCodeAndDeletedAtIsNull(test.getLoincCode()).isPresent()) {
                throw new IllegalArgumentException("LOINC code already exists: " + test.getLoincCode());
            }
        }

        // Validate category exists
        labTestCategoryRepository.findByIdAndDeletedAtIsNull(test.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + test.getCategory().getId()));

        LabTest saved = labTestRepository.save(test);
        log.info("Lab test created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Create test with parameters
     */
    public LabTest createTestWithParameters(LabTest test, List<LabTestParameter> parameters) {
        log.info("Creating lab test with {} parameters: {}", parameters.size(), test.getName());

        // Create the test first
        LabTest savedTest = createTest(test);

        // Create parameters
        for (LabTestParameter parameter : parameters) {
            parameter.setLabTest(savedTest);
            labTestParameterRepository.save(parameter);
        }

        log.info("Lab test created with parameters successfully: {}", savedTest.getId());
        return savedTest;
    }

    /**
     * Update existing lab test
     */
    public LabTest updateTest(UUID id, LabTest testUpdate) {
        log.info("Updating lab test: {}", id);

        LabTest existing = labTestRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));

        // Check test code uniqueness
        if (!existing.getTestCode().equals(testUpdate.getTestCode())) {
            if (labTestRepository.findByTestCodeAndDeletedAtIsNull(testUpdate.getTestCode()).isPresent()) {
                throw new IllegalArgumentException("Test code already exists: " + testUpdate.getTestCode());
            }
        }

        // Check LOINC code uniqueness
        if (testUpdate.getLoincCode() != null && !testUpdate.getLoincCode().equals(existing.getLoincCode())) {
            if (labTestRepository.findByLoincCodeAndDeletedAtIsNull(testUpdate.getLoincCode()).isPresent()) {
                throw new IllegalArgumentException("LOINC code already exists: " + testUpdate.getLoincCode());
            }
        }

        // Update fields
        existing.setTestCode(testUpdate.getTestCode());
        existing.setName(testUpdate.getName());
        existing.setShortName(testUpdate.getShortName());
        // TODO: LabTest does not have description field
        // existing.setDescription(testUpdate.getDescription());
        existing.setLoincCode(testUpdate.getLoincCode());
        existing.setSampleType(testUpdate.getSampleType());
        existing.setSampleVolumeMl(testUpdate.getSampleVolumeMl()); // Use sampleVolumeMl, not sampleVolume
        existing.setSampleVolumeUnit(testUpdate.getSampleVolumeUnit());
        existing.setSampleContainer(testUpdate.getSampleContainer()); // Use sampleContainer, not containerType
        existing.setPreparationInstructions(testUpdate.getPreparationInstructions());
        // TODO: LabTest does not have storageTemperature field
        // existing.setStorageTemperature(testUpdate.getStorageTemperature());
        // TODO: LabTest does not have stabilityHours field
        // existing.setStabilityHours(testUpdate.getStabilityHours());
        existing.setProcessingTimeMinutes(testUpdate.getProcessingTimeMinutes());
        // TODO: LabTest does not have reportingTimeDays field
        // existing.setReportingTimeDays(testUpdate.getReportingTimeDays());
        existing.setBaseCost(testUpdate.getBaseCost());
        existing.setBpjsTariff(testUpdate.getBpjsTariff());
        existing.setRequiresApproval(testUpdate.getRequiresApproval());
        existing.setFastingRequired(testUpdate.getFastingRequired()); // Use fastingRequired, not requiresFasting
        existing.setFastingDurationHours(testUpdate.getFastingDurationHours()); // Use fastingDurationHours, not fastingHours
        existing.setTestMethodology(testUpdate.getTestMethodology());
        existing.setActive(testUpdate.getActive());

        // Update category if changed
        if (!existing.getCategory().getId().equals(testUpdate.getCategory().getId())) {
            LabTestCategory newCategory = labTestCategoryRepository.findByIdAndDeletedAtIsNull(testUpdate.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + testUpdate.getCategory().getId()));
            existing.setCategory(newCategory);
        }

        LabTest updated = labTestRepository.save(existing);
        log.info("Lab test updated successfully: {}", id);
        return updated;
    }

    /**
     * Delete (soft delete) lab test
     */
    public void deleteTest(UUID id, String deletedBy) {
        log.info("Deleting lab test: {}", id);

        LabTest test = labTestRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));

        // Soft delete
        test.setDeletedAt(LocalDateTime.now());
        test.setDeletedBy(deletedBy);
        labTestRepository.save(test);

        log.info("Lab test deleted successfully: {}", id);
    }

    /**
     * Get test by ID
     */
    @Transactional(readOnly = true)
    public LabTest getTestById(UUID id) {
        return labTestRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));
    }

    /**
     * Get test by code
     */
    @Transactional(readOnly = true)
    public LabTest getTestByCode(String testCode) {
        return labTestRepository.findByTestCodeAndDeletedAtIsNull(testCode)
                .orElseThrow(() -> new IllegalArgumentException("Test not found with code: " + testCode));
    }

    /**
     * Get test by LOINC code
     */
    @Transactional(readOnly = true)
    public LabTest getTestByLoincCode(String loincCode) {
        return labTestRepository.findByLoincCodeAndDeletedAtIsNull(loincCode)
                .orElseThrow(() -> new IllegalArgumentException("Test not found with LOINC code: " + loincCode));
    }

    /**
     * Get all active tests
     */
    @Transactional(readOnly = true)
    public List<LabTest> getAllActiveTests() {
        // TODO: Repository method may not exist - implement in repository if needed
        return labTestRepository.findByActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get tests by category
     */
    @Transactional(readOnly = true)
    public List<LabTest> getTestsByCategory(UUID categoryId) {
        // TODO: Repository method may not exist - implement in repository if needed
        return labTestRepository.findByCategoryIdAndActiveTrueAndDeletedAtIsNull(categoryId);
    }

    /**
     * Get tests by sample type
     */
    @Transactional(readOnly = true)
    public List<LabTest> getTestsBySampleType(String sampleType) {
        // TODO: Repository method may not exist - implement in repository if needed
        return labTestRepository.findBySampleTypeAndActiveTrueAndDeletedAtIsNull(sampleType);
    }

    /**
     * Search tests
     */
    @Transactional(readOnly = true)
    public Page<LabTest> searchTests(String search, Pageable pageable) {
        // TODO: Repository method may not exist - implement custom query in repository
        // For now, return all tests (simplified)
        return labTestRepository.findAll(pageable);
    }

    /**
     * Get all tests with pagination
     */
    @Transactional(readOnly = true)
    public Page<LabTest> getAllTests(Pageable pageable) {
        // TODO: Repository method may not exist - implement in repository if needed
        return labTestRepository.findAll(pageable);
    }

    /**
     * Get tests requiring approval
     */
    @Transactional(readOnly = true)
    public List<LabTest> getTestsRequiringApproval() {
        // TODO: Repository method may not exist - implement in repository if needed
        return labTestRepository.findByRequiresApprovalTrueAndActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Activate test
     */
    public void activateTest(UUID id) {
        log.info("Activating lab test: {}", id);
        LabTest test = getTestById(id);
        test.setActive(true);
        labTestRepository.save(test);
        log.info("Lab test activated: {}", id);
    }

    /**
     * Deactivate test
     */
    public void deactivateTest(UUID id) {
        log.info("Deactivating lab test: {}", id);
        LabTest test = getTestById(id);
        test.setActive(false);
        labTestRepository.save(test);
        log.info("Lab test deactivated: {}", id);
    }

    /**
     * Update test pricing
     */
    public LabTest updateTestPricing(UUID id, java.math.BigDecimal baseCost, java.math.BigDecimal bpjsTariff) {
        log.info("Updating test pricing for: {}", id);
        LabTest test = getTestById(id);
        test.setBaseCost(baseCost);
        test.setBpjsTariff(bpjsTariff);
        LabTest updated = labTestRepository.save(test);
        log.info("Test pricing updated: {}", id);
        return updated;
    }

    /**
     * Get test parameters
     */
    @Transactional(readOnly = true)
    public List<LabTestParameter> getTestParameters(UUID testId) {
        return labTestParameterRepository.findByLabTestIdAndActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc(testId);
    }

    /**
     * Count active tests
     */
    @Transactional(readOnly = true)
    public long countActiveTests() {
        return labTestRepository.countByActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Count tests by category
     */
    @Transactional(readOnly = true)
    public long countTestsByCategory(UUID categoryId) {
        return labTestRepository.countByCategoryIdAndActiveTrueAndDeletedAtIsNull(categoryId);
    }
}
