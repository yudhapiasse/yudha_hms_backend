package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.entity.LabTestParameter;
import com.yudha.hms.laboratory.repository.LabTestParameterRepository;
import com.yudha.hms.laboratory.repository.LabTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Laboratory Test Parameter operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LabTestParameterService {

    private final LabTestParameterRepository labTestParameterRepository;
    private final LabTestRepository labTestRepository;

    /**
     * Create new test parameter
     */
    public LabTestParameter createParameter(LabTestParameter parameter) {
        log.info("Creating new test parameter: {}", parameter.getParameterName());

        // Validate test exists
        labTestRepository.findByIdAndDeletedAtIsNull(parameter.getLabTest().getId())
                .orElseThrow(() -> new IllegalArgumentException("Lab test not found: " + parameter.getLabTest().getId()));

        // Validate unique parameter code within test
        if (labTestParameterRepository.findByLabTestIdAndParameterCodeAndDeletedAtIsNull(
                parameter.getLabTest().getId(), parameter.getParameterCode()).isPresent()) {
            throw new IllegalArgumentException("Parameter code already exists for this test: " + parameter.getParameterCode());
        }

        LabTestParameter saved = labTestParameterRepository.save(parameter);
        log.info("Test parameter created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Update existing test parameter
     */
    public LabTestParameter updateParameter(UUID id, LabTestParameter parameterUpdate) {
        log.info("Updating test parameter: {}", id);

        LabTestParameter existing = labTestParameterRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Parameter not found: " + id));

        // Check parameter code uniqueness within test
        if (!existing.getParameterCode().equals(parameterUpdate.getParameterCode())) {
            if (labTestParameterRepository.findByLabTestIdAndParameterCodeAndDeletedAtIsNull(
                    existing.getLabTest().getId(), parameterUpdate.getParameterCode()).isPresent()) {
                throw new IllegalArgumentException("Parameter code already exists for this test: " + parameterUpdate.getParameterCode());
            }
        }

        // Update fields
        existing.setParameterCode(parameterUpdate.getParameterCode());
        existing.setParameterName(parameterUpdate.getParameterName());
        existing.setParameterShortName(parameterUpdate.getParameterShortName());
        existing.setDataType(parameterUpdate.getDataType());
        existing.setUnit(parameterUpdate.getUnit());
        existing.setDisplayOrder(parameterUpdate.getDisplayOrder());
        existing.setIsMandatory(parameterUpdate.getIsMandatory());
        existing.setNormalRangeLow(parameterUpdate.getNormalRangeLow());
        existing.setNormalRangeHigh(parameterUpdate.getNormalRangeHigh());
        existing.setNormalRangeText(parameterUpdate.getNormalRangeText());
        existing.setAgeGenderRanges(parameterUpdate.getAgeGenderRanges());
        existing.setCriticalLow(parameterUpdate.getCriticalLow());
        existing.setCriticalHigh(parameterUpdate.getCriticalHigh());
        existing.setPanicLow(parameterUpdate.getPanicLow());
        existing.setPanicHigh(parameterUpdate.getPanicHigh());
        existing.setAllowedValues(parameterUpdate.getAllowedValues());
        existing.setDeltaCheckEnabled(parameterUpdate.getDeltaCheckEnabled());
        existing.setDeltaCheckPercentage(parameterUpdate.getDeltaCheckPercentage());
        existing.setDeltaCheckAbsolute(parameterUpdate.getDeltaCheckAbsolute());
        existing.setIsCalculated(parameterUpdate.getIsCalculated());
        existing.setCalculationFormula(parameterUpdate.getCalculationFormula());
        existing.setNotes(parameterUpdate.getNotes());
        existing.setActive(parameterUpdate.getActive());

        LabTestParameter updated = labTestParameterRepository.save(existing);
        log.info("Test parameter updated successfully: {}", id);
        return updated;
    }

    /**
     * Delete (soft delete) test parameter
     */
    public void deleteParameter(UUID id, String deletedBy) {
        log.info("Deleting test parameter: {}", id);

        LabTestParameter parameter = labTestParameterRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Parameter not found: " + id));

        // Soft delete
        parameter.setDeletedAt(LocalDateTime.now());
        parameter.setDeletedBy(deletedBy);
        labTestParameterRepository.save(parameter);

        log.info("Test parameter deleted successfully: {}", id);
    }

    /**
     * Get parameter by ID
     */
    @Transactional(readOnly = true)
    public LabTestParameter getParameterById(UUID id) {
        return labTestParameterRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Parameter not found: " + id));
    }

    /**
     * Get parameters by test
     */
    @Transactional(readOnly = true)
    public List<LabTestParameter> getParametersByTest(UUID testId) {
        return labTestParameterRepository.findByLabTestIdAndActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc(testId);
    }

    /**
     * Get mandatory parameters for test
     */
    @Transactional(readOnly = true)
    public List<LabTestParameter> getMandatoryParametersByTest(UUID testId) {
        return labTestParameterRepository.findByLabTestIdAndIsMandatoryTrueAndActiveTrueAndDeletedAtIsNull(testId);
    }

    /**
     * Get parameters with delta check enabled
     */
    @Transactional(readOnly = true)
    public List<LabTestParameter> getParametersWithDeltaCheck(UUID testId) {
        return labTestParameterRepository.findByLabTestIdAndDeltaCheckEnabledTrueAndActiveTrueAndDeletedAtIsNull(testId);
    }

    /**
     * Get calculated parameters
     */
    @Transactional(readOnly = true)
    public List<LabTestParameter> getCalculatedParameters(UUID testId) {
        return labTestParameterRepository.findByLabTestIdAndIsCalculatedTrueAndActiveTrueAndDeletedAtIsNull(testId);
    }

    /**
     * Activate parameter
     */
    public void activateParameter(UUID id) {
        log.info("Activating test parameter: {}", id);
        LabTestParameter parameter = getParameterById(id);
        parameter.setActive(true);
        labTestParameterRepository.save(parameter);
        log.info("Test parameter activated: {}", id);
    }

    /**
     * Deactivate parameter
     */
    public void deactivateParameter(UUID id) {
        log.info("Deactivating test parameter: {}", id);
        LabTestParameter parameter = getParameterById(id);
        parameter.setActive(false);
        labTestParameterRepository.save(parameter);
        log.info("Test parameter deactivated: {}", id);
    }

    /**
     * Update parameter normal ranges
     */
    public LabTestParameter updateNormalRanges(UUID id, BigDecimal low, BigDecimal high, String text) {
        log.info("Updating normal ranges for parameter: {}", id);
        LabTestParameter parameter = getParameterById(id);
        parameter.setNormalRangeLow(low);
        parameter.setNormalRangeHigh(high);
        parameter.setNormalRangeText(text);
        LabTestParameter updated = labTestParameterRepository.save(parameter);
        log.info("Normal ranges updated: {}", id);
        return updated;
    }

    /**
     * Update parameter critical values
     */
    public LabTestParameter updateCriticalValues(UUID id, BigDecimal criticalLow, BigDecimal criticalHigh,
                                                   BigDecimal panicLow, BigDecimal panicHigh) {
        log.info("Updating critical values for parameter: {}", id);
        LabTestParameter parameter = getParameterById(id);
        parameter.setCriticalLow(criticalLow);
        parameter.setCriticalHigh(criticalHigh);
        parameter.setPanicLow(panicLow);
        parameter.setPanicHigh(panicHigh);
        LabTestParameter updated = labTestParameterRepository.save(parameter);
        log.info("Critical values updated: {}", id);
        return updated;
    }

    /**
     * Update delta check configuration
     */
    public LabTestParameter updateDeltaCheckConfig(UUID id, Boolean enabled, BigDecimal percentage, BigDecimal absolute) {
        log.info("Updating delta check configuration for parameter: {}", id);
        LabTestParameter parameter = getParameterById(id);
        parameter.setDeltaCheckEnabled(enabled);
        parameter.setDeltaCheckPercentage(percentage);
        parameter.setDeltaCheckAbsolute(absolute);
        LabTestParameter updated = labTestParameterRepository.save(parameter);
        log.info("Delta check configuration updated: {}", id);
        return updated;
    }

    /**
     * Reorder parameters
     */
    public void reorderParameters(List<UUID> parameterIds) {
        log.info("Reordering {} parameters", parameterIds.size());

        for (int i = 0; i < parameterIds.size(); i++) {
            UUID parameterId = parameterIds.get(i);
            LabTestParameter parameter = getParameterById(parameterId);
            parameter.setDisplayOrder(i + 1);
            labTestParameterRepository.save(parameter);
        }

        log.info("Parameters reordered successfully");
    }

    /**
     * Check if value is within normal range
     */
    @Transactional(readOnly = true)
    public boolean isValueNormal(UUID parameterId, BigDecimal value) {
        LabTestParameter parameter = getParameterById(parameterId);
        return parameter.isWithinNormalRange(value);
    }

    /**
     * Check if value is critical
     */
    @Transactional(readOnly = true)
    public boolean isValueCritical(UUID parameterId, BigDecimal value) {
        LabTestParameter parameter = getParameterById(parameterId);
        return parameter.isCriticalValue(value);
    }

    /**
     * Check if value is panic level
     */
    @Transactional(readOnly = true)
    public boolean isValuePanic(UUID parameterId, BigDecimal value) {
        LabTestParameter parameter = getParameterById(parameterId);
        return parameter.isPanicValue(value);
    }
}
