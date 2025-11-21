package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.constant.EntryMethod;
import com.yudha.hms.laboratory.constant.ResultStatus;
import com.yudha.hms.laboratory.entity.*;
import com.yudha.hms.laboratory.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing laboratory result operations.
 *
 * Handles result entry, validation, critical value detection, delta checks,
 * and result lifecycle management (amendment, cancellation).
 *
 * Features:
 * - Multiple entry methods (Manual, LIS Interface, Imported)
 * - Automatic result number generation
 * - Parameter entry with automatic flagging
 * - Delta check integration
 * - Critical/panic value detection
 * - Result validation workflow
 * - Amendment and cancellation tracking
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LabResultService {

    private final LabResultRepository labResultRepository;
    private final LabResultParameterRepository labResultParameterRepository;
    private final LabOrderRepository labOrderRepository;
    private final LabOrderItemRepository labOrderItemRepository;
    private final SpecimenRepository specimenRepository;
    private final LabTestRepository labTestRepository;
    private final LabTestParameterRepository labTestParameterRepository;
    private final ResultValidationService resultValidationService;

    /**
     * DTO for result parameter entry
     */
    public static class ResultParameterEntry {
        public UUID testParameterId;
        public String resultValue;
        public BigDecimal numericValue;
        public String textValue;
        public String notes;
    }

    /**
     * Create a new result for an order item.
     * Auto-populates test and specimen information.
     *
     * @param orderItemId Order item ID
     * @param specimenId Specimen ID
     * @param enteredBy User ID who entered the result
     * @param entryMethod Entry method (MANUAL, INTERFACE, IMPORTED)
     * @return Created lab result
     * @throws IllegalArgumentException if order item or specimen not found
     */
    public LabResult createResult(UUID orderItemId, UUID specimenId, UUID enteredBy, EntryMethod entryMethod) {
        log.info("Creating result for order item: {}, specimen: {}, entry method: {}",
                orderItemId, specimenId, entryMethod);

        // Validate order item exists
        LabOrderItem orderItem = labOrderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found with ID: " + orderItemId));

        // Validate specimen exists
        Specimen specimen = specimenRepository.findById(specimenId)
                .orElseThrow(() -> new IllegalArgumentException("Specimen not found with ID: " + specimenId));

        // Validate test exists
        if (orderItem.getTest() == null) {
            throw new IllegalArgumentException("Order item does not have an associated test");
        }

        LabTest test = orderItem.getTest();
        LabOrder order = orderItem.getOrder();

        // Generate result number
        String resultNumber = generateResultNumber();

        // Create result
        LabResult result = LabResult.builder()
                .resultNumber(resultNumber)
                .order(order)
                .orderItem(orderItem)
                .specimen(specimen)
                .test(test)
                .testName(test.getName())
                .testCode(test.getTestCode())
                .status(ResultStatus.PENDING)
                .enteredAt(LocalDateTime.now())
                .enteredBy(enteredBy)
                .entryMethod(entryMethod)
                .deltaCheckPerformed(false)
                .deltaCheckFlagged(false)
                .hasPanicValues(false)
                .panicValueNotified(false)
                .requiresPathologistReview(test.getRequiresPathologistReview())
                .reviewedByPathologist(false)
                .isAmended(false)
                .reportGenerated(false)
                .reportSentToClinical(false)
                .build();

        result = labResultRepository.save(result);

        // Update order item with result ID
        orderItem.setResultId(result.getId());
        labOrderItemRepository.save(orderItem);

        log.info("Result created successfully with number: {}", resultNumber);
        return result;
    }

    /**
     * Enter result parameters with automatic flagging.
     * Performs interpretation flagging, delta check, and critical value detection.
     *
     * @param resultId Result ID
     * @param parameters List of parameter entries
     * @param enteredBy User ID who entered the parameters
     * @return Updated lab result
     * @throws IllegalArgumentException if result not found
     */
    public LabResult enterResultParameters(UUID resultId, List<ResultParameterEntry> parameters, UUID enteredBy) {
        log.info("Entering result parameters for result: {}, parameter count: {}", resultId, parameters.size());

        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        // Validate result can be edited
        if (result.getStatus() == ResultStatus.CANCELLED || result.getStatus() == ResultStatus.ENTERED_IN_ERROR) {
            throw new IllegalStateException("Cannot enter parameters for cancelled or error results");
        }

        boolean hasAbnormalValues = false;
        boolean hasCriticalValues = false;
        boolean hasDeltaCheckFlags = false;

        // Process each parameter
        for (ResultParameterEntry paramEntry : parameters) {
            LabTestParameter testParameter = labTestParameterRepository.findById(paramEntry.testParameterId)
                    .orElseThrow(() -> new IllegalArgumentException("Test parameter not found: " + paramEntry.testParameterId));

            // Create result parameter
            LabResultParameter resultParam = LabResultParameter.builder()
                    .result(result)
                    .testParameter(testParameter)
                    .parameterCode(testParameter.getParameterCode())
                    .parameterName(testParameter.getParameterName())
                    .resultValue(paramEntry.resultValue)
                    .numericValue(paramEntry.numericValue)
                    .textValue(paramEntry.textValue)
                    .unit(testParameter.getUnit())
                    .referenceRangeLow(testParameter.getNormalRangeLow())
                    .referenceRangeHigh(testParameter.getNormalRangeHigh())
                    .referenceRangeText(testParameter.getNormalRangeText())
                    .notes(paramEntry.notes)
                    .build();

            // Calculate interpretation flag for numeric values
            if (paramEntry.numericValue != null && testParameter.isNumeric()) {
                resultParam.calculateInterpretationFlag(
                        testParameter.getCriticalLow(),
                        testParameter.getCriticalHigh()
                );

                if (resultParam.isAbnormal()) {
                    hasAbnormalValues = true;
                }
                if (resultParam.isCritical()) {
                    hasCriticalValues = true;
                }

                // Perform delta check if enabled
                if (testParameter.isDeltaCheckEnabled()) {
                    performParameterDeltaCheck(resultParam, testParameter, result.getOrder().getPatientId());
                    if (resultParam.isDeltaCheckFlagged()) {
                        hasDeltaCheckFlags = true;
                    }
                }
            }

            labResultParameterRepository.save(resultParam);
        }

        // Update result status and flags
        result.setStatus(ResultStatus.PRELIMINARY);
        result.setHasPanicValues(hasCriticalValues);
        result.setDeltaCheckFlagged(hasDeltaCheckFlags);

        if (hasAbnormalValues) {
            result.setOverallInterpretation("ABNORMAL");
        } else {
            result.setOverallInterpretation("NORMAL");
        }

        if (hasCriticalValues) {
            result.setOverallInterpretation("CRITICAL");
        }

        result = labResultRepository.save(result);

        log.info("Result parameters entered successfully. Abnormal: {}, Critical: {}, Delta Check: {}",
                hasAbnormalValues, hasCriticalValues, hasDeltaCheckFlags);

        return result;
    }

    /**
     * Validate a result (move to FINAL status).
     *
     * @param resultId Result ID
     * @param validatedBy User ID who validated
     * @return Updated lab result
     * @throws IllegalArgumentException if result not found
     * @throws IllegalStateException if result cannot be validated
     */
    public LabResult validateResult(UUID resultId, UUID validatedBy) {
        log.info("Validating result: {}", resultId);

        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        if (result.getStatus() != ResultStatus.PRELIMINARY) {
            throw new IllegalStateException("Only preliminary results can be validated");
        }

        // Check if pathologist review is required and completed
        if (result.needsPathologistReview()) {
            throw new IllegalStateException("Result requires pathologist review before validation");
        }

        result.setStatus(ResultStatus.FINAL);
        result.setValidatedAt(LocalDateTime.now());
        result.setValidatedBy(validatedBy);

        result = labResultRepository.save(result);

        // Update order item
        LabOrderItem orderItem = result.getOrderItem();
        orderItem.setStatus("COMPLETED");
        orderItem.setResultCompletedAt(LocalDateTime.now());
        labOrderItemRepository.save(orderItem);

        log.info("Result validated successfully: {}", resultId);
        return result;
    }

    /**
     * Finalize a result (alias for validate for consistency).
     *
     * @param resultId Result ID
     * @param finalizedBy User ID who finalized
     * @return Updated lab result
     */
    public LabResult finalizeResult(UUID resultId, UUID finalizedBy) {
        return validateResult(resultId, finalizedBy);
    }

    /**
     * Amend a result with a new version.
     *
     * @param resultId Original result ID
     * @param amendmentReason Reason for amendment
     * @param amendedBy User ID who amended
     * @return Amended lab result
     * @throws IllegalArgumentException if result not found
     * @throws IllegalStateException if result cannot be amended
     */
    public LabResult amendResult(UUID resultId, String amendmentReason, UUID amendedBy) {
        log.info("Amending result: {}, reason: {}", resultId, amendmentReason);

        LabResult originalResult = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        if (!originalResult.canBeAmended()) {
            throw new IllegalStateException("Result cannot be amended in current status: " + originalResult.getStatus());
        }

        // Mark original as amended
        originalResult.setStatus(ResultStatus.AMENDED);
        originalResult.setIsAmended(true);
        originalResult.setAmendedAt(LocalDateTime.now());
        originalResult.setAmendedBy(amendedBy);
        originalResult.setAmendmentReason(amendmentReason);
        labResultRepository.save(originalResult);

        // Create new result as corrected version
        String newResultNumber = generateResultNumber();

        LabResult amendedResult = LabResult.builder()
                .resultNumber(newResultNumber)
                .order(originalResult.getOrder())
                .orderItem(originalResult.getOrderItem())
                .specimen(originalResult.getSpecimen())
                .test(originalResult.getTest())
                .testName(originalResult.getTestName())
                .testCode(originalResult.getTestCode())
                .status(ResultStatus.PRELIMINARY)
                .enteredAt(LocalDateTime.now())
                .enteredBy(amendedBy)
                .entryMethod(originalResult.getEntryMethod())
                .originalResultId(resultId)
                .amendmentReason(amendmentReason)
                .requiresPathologistReview(originalResult.getRequiresPathologistReview())
                .build();

        amendedResult = labResultRepository.save(amendedResult);

        log.info("Result amended successfully. Original: {}, New: {}", resultId, amendedResult.getId());
        return amendedResult;
    }

    /**
     * Cancel a result.
     *
     * @param resultId Result ID
     * @param cancellationReason Reason for cancellation
     * @param cancelledBy User ID who cancelled
     * @return Cancelled lab result
     * @throws IllegalArgumentException if result not found
     */
    public LabResult cancelResult(UUID resultId, String cancellationReason, UUID cancelledBy) {
        log.info("Cancelling result: {}, reason: {}", resultId, cancellationReason);

        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        result.setStatus(ResultStatus.CANCELLED);
        result.setNotes((result.getNotes() != null ? result.getNotes() + "\n\n" : "") +
                "CANCELLED: " + cancellationReason + " by user " + cancelledBy + " at " + LocalDateTime.now());

        result = labResultRepository.save(result);

        log.info("Result cancelled successfully: {}", resultId);
        return result;
    }

    /**
     * Get result by ID.
     *
     * @param id Result ID
     * @return Lab result
     * @throws IllegalArgumentException if result not found
     */
    @Transactional(readOnly = true)
    public LabResult getResultById(UUID id) {
        return labResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + id));
    }

    /**
     * Get result by order item.
     *
     * @param orderItemId Order item ID
     * @return Lab result
     */
    @Transactional(readOnly = true)
    public Optional<LabResult> getResultByOrderItem(UUID orderItemId) {
        return labResultRepository.findByOrderItemId(orderItemId);
    }

    /**
     * Get all results for an order.
     *
     * @param orderId Order ID
     * @return List of lab results
     */
    @Transactional(readOnly = true)
    public List<LabResult> getResultsByOrder(UUID orderId) {
        return labResultRepository.findByOrderIdOrderByEnteredAtDesc(orderId);
    }

    /**
     * Get patient result history for a specific test.
     *
     * @param patientId Patient ID
     * @param testId Test ID
     * @return List of historical results
     */
    @Transactional(readOnly = true)
    public List<LabResult> getPatientResultHistory(UUID patientId, UUID testId) {
        return labResultRepository.findPatientResultHistory(patientId, testId);
    }

    /**
     * Get results awaiting validation.
     *
     * @param pageable Pagination parameters
     * @return Page of results awaiting validation
     */
    @Transactional(readOnly = true)
    public Page<LabResult> getResultsAwaitingValidation(Pageable pageable) {
        return labResultRepository.findResultsAwaitingValidation(pageable);
    }

    /**
     * Get results with unnotified panic values.
     *
     * @return List of results with panic values
     */
    @Transactional(readOnly = true)
    public List<LabResult> getResultsWithPanicValues() {
        return labResultRepository.findResultsWithUnnotifiedPanicValues();
    }

    /**
     * Perform delta check on a result.
     * Compares current result with previous results to detect unusual changes.
     *
     * @param resultId Result ID
     * @return Updated lab result
     * @throws IllegalArgumentException if result not found
     */
    public LabResult performDeltaCheck(UUID resultId) {
        log.info("Performing delta check for result: {}", resultId);

        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        // Get patient ID
        UUID patientId = result.getOrder().getPatientId();
        UUID testId = result.getTest().getId();

        // Find previous result
        Optional<LabResult> previousResultOpt = labResultRepository.findPreviousResult(patientId, testId, resultId);

        if (previousResultOpt.isEmpty()) {
            log.info("No previous result found for delta check");
            result.setDeltaCheckPerformed(true);
            result.setDeltaCheckFlagged(false);
            result.setDeltaCheckNotes("No previous result available for comparison");
            return labResultRepository.save(result);
        }

        LabResult previousResult = previousResultOpt.get();
        result.setPreviousResultId(previousResult.getId());

        // Get current and previous parameters
        List<LabResultParameter> currentParams = labResultParameterRepository.findByResultId(resultId);
        List<LabResultParameter> previousParams = labResultParameterRepository.findByResultId(previousResult.getId());

        // Create map of previous parameters by code
        Map<String, LabResultParameter> previousParamMap = previousParams.stream()
                .collect(Collectors.toMap(LabResultParameter::getParameterCode, p -> p));

        boolean anyDeltaFlagged = false;
        StringBuilder deltaCheckNotes = new StringBuilder();

        // Compare parameters
        for (LabResultParameter currentParam : currentParams) {
            LabResultParameter previousParam = previousParamMap.get(currentParam.getParameterCode());

            if (previousParam != null && currentParam.getNumericValue() != null && previousParam.getNumericValue() != null) {
                // Get delta check configuration from test parameter
                LabTestParameter testParam = currentParam.getTestParameter();

                if (testParam.isDeltaCheckEnabled()) {
                    currentParam.calculateDeltaCheck(
                            previousParam.getNumericValue(),
                            testParam.getDeltaCheckPercentage(),
                            testParam.getDeltaCheckAbsolute()
                    );

                    if (currentParam.isDeltaCheckFlagged()) {
                        anyDeltaFlagged = true;
                        deltaCheckNotes.append(String.format(
                                "%s: Previous=%.2f, Current=%.2f, Change=%.2f%%\n",
                                currentParam.getParameterName(),
                                previousParam.getNumericValue(),
                                currentParam.getNumericValue(),
                                currentParam.getDeltaPercentage()
                        ));
                    }

                    labResultParameterRepository.save(currentParam);
                }
            }
        }

        result.setDeltaCheckPerformed(true);
        result.setDeltaCheckFlagged(anyDeltaFlagged);
        result.setDeltaCheckNotes(deltaCheckNotes.toString());

        result = labResultRepository.save(result);

        log.info("Delta check completed. Flagged: {}", anyDeltaFlagged);
        return result;
    }

    /**
     * Detect critical values in a result.
     *
     * @param resultId Result ID
     * @return Updated lab result
     * @throws IllegalArgumentException if result not found
     */
    public LabResult detectCriticalValues(UUID resultId) {
        log.info("Detecting critical values for result: {}", resultId);

        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        List<LabResultParameter> parameters = labResultParameterRepository.findByResultId(resultId);
        List<LabResultParameter> criticalParams = labResultParameterRepository.findCriticalParameters(resultId);

        boolean hasCritical = !criticalParams.isEmpty();
        result.setHasPanicValues(hasCritical);

        if (hasCritical) {
            log.warn("Critical values detected in result: {}. Count: {}", resultId, criticalParams.size());
        }

        return labResultRepository.save(result);
    }

    /**
     * Perform delta check for a specific parameter.
     *
     * @param resultParam Current result parameter
     * @param testParam Test parameter configuration
     * @param patientId Patient ID
     */
    private void performParameterDeltaCheck(LabResultParameter resultParam, LabTestParameter testParam, UUID patientId) {
        // Find previous result for this patient and test
        UUID testId = testParam.getLabTest().getId();
        Optional<LabResult> previousResultOpt = labResultRepository.findPreviousResult(
                patientId, testId, resultParam.getResult().getId());

        if (previousResultOpt.isEmpty()) {
            return;
        }

        // Find previous parameter value
        List<LabResultParameter> previousParams = labResultParameterRepository.findByResultId(previousResultOpt.get().getId());
        Optional<LabResultParameter> previousParamOpt = previousParams.stream()
                .filter(p -> p.getParameterCode().equals(resultParam.getParameterCode()))
                .findFirst();

        if (previousParamOpt.isPresent() && previousParamOpt.get().getNumericValue() != null) {
            resultParam.calculateDeltaCheck(
                    previousParamOpt.get().getNumericValue(),
                    testParam.getDeltaCheckPercentage(),
                    testParam.getDeltaCheckAbsolute()
            );
        }
    }

    /**
     * Generate unique result number.
     * Format: LR + YYYYMMDD + 6-digit sequence
     *
     * @return Generated result number
     */
    private String generateResultNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "LR" + datePart;

        // Find highest sequence number for today
        // Note: In production, this should use a database sequence or atomic counter
        long count = labResultRepository.count();
        String sequence = String.format("%06d", (count % 1000000) + 1);

        return prefix + sequence;
    }
}
