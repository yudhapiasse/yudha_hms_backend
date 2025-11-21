package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.constant.PregnancyTestResult;
import com.yudha.hms.radiology.entity.PatientPreparationChecklist;
import com.yudha.hms.radiology.entity.RadiologyExamination;
import com.yudha.hms.radiology.entity.RadiologyOrder;
import com.yudha.hms.radiology.repository.PatientPreparationChecklistRepository;
import com.yudha.hms.radiology.repository.RadiologyExaminationRepository;
import com.yudha.hms.radiology.repository.RadiologyOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Patient Preparation Checklist Service.
 *
 * Manages patient preparation requirements and verification for radiology examinations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PatientPreparationChecklistService {

    private final PatientPreparationChecklistRepository checklistRepository;
    private final RadiologyOrderRepository radiologyOrderRepository;
    private final RadiologyExaminationRepository radiologyExaminationRepository;

    /**
     * Create preparation checklist for an order
     */
    @Transactional
    public PatientPreparationChecklist createChecklist(UUID orderId, UUID examinationId, String preparationInstructions) {
        log.info("Creating preparation checklist for order: {}, examination: {}", orderId, examinationId);

        // Check if checklist already exists
        if (checklistRepository.existsByOrderId(orderId)) {
            throw new IllegalStateException("Preparation checklist already exists for this order");
        }

        RadiologyOrder order = radiologyOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        RadiologyExamination examination = radiologyExaminationRepository.findById(examinationId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found: " + examinationId));

        PatientPreparationChecklist checklist = PatientPreparationChecklist.builder()
                .order(order)
                .examination(examination)
                .preparationInstructions(preparationInstructions)
                .fastingRequired(examination.getFastingRequired())
                .fastingHoursRequired(examination.getFastingDurationHours())
                .ivAccessRequired(examination.getRequiresContrast())
                .pregnancyTestRequired(examination.getModality().getRequiresRadiation())
                .consentObtained(false)
                .allItemsCompleted(false)
                .build();

        return checklistRepository.save(checklist);
    }

    /**
     * Verify fasting status
     */
    @Transactional
    public PatientPreparationChecklist verifyFasting(UUID checklistId, UUID verifiedBy, boolean verified) {
        log.info("Verifying fasting for checklist: {}, verified: {}", checklistId, verified);

        PatientPreparationChecklist checklist = getChecklistById(checklistId);

        if (!checklist.getFastingRequired()) {
            throw new IllegalStateException("Fasting verification not required for this examination");
        }

        checklist.setFastingVerified(verified);
        checklist.setFastingVerifiedBy(verifiedBy);
        checklist.setFastingVerifiedAt(LocalDateTime.now());

        updateCompletionStatus(checklist);

        return checklistRepository.save(checklist);
    }

    /**
     * Verify medication hold
     */
    @Transactional
    public PatientPreparationChecklist verifyMedicationHold(UUID checklistId, UUID verifiedBy,
                                                            boolean verified, String medicationDetails) {
        log.info("Verifying medication hold for checklist: {}", checklistId);

        PatientPreparationChecklist checklist = getChecklistById(checklistId);

        checklist.setMedicationHoldVerified(verified);
        checklist.setMedicationHoldVerifiedBy(verifiedBy);
        checklist.setMedicationHoldVerifiedAt(LocalDateTime.now());
        checklist.setMedicationHoldDetails(medicationDetails);

        updateCompletionStatus(checklist);

        return checklistRepository.save(checklist);
    }

    /**
     * Verify IV access
     */
    @Transactional
    public PatientPreparationChecklist verifyIVAccess(UUID checklistId, UUID verifiedBy,
                                                      boolean verified, String ivGauge) {
        log.info("Verifying IV access for checklist: {}, gauge: {}", checklistId, ivGauge);

        PatientPreparationChecklist checklist = getChecklistById(checklistId);

        if (!checklist.getIvAccessRequired()) {
            throw new IllegalStateException("IV access verification not required for this examination");
        }

        checklist.setIvAccessVerified(verified);
        checklist.setIvAccessVerifiedBy(verifiedBy);
        checklist.setIvAccessVerifiedAt(LocalDateTime.now());
        checklist.setIvGauge(ivGauge);

        updateCompletionStatus(checklist);

        return checklistRepository.save(checklist);
    }

    /**
     * Record pregnancy test
     */
    @Transactional
    public PatientPreparationChecklist recordPregnancyTest(UUID checklistId, PregnancyTestResult result,
                                                           LocalDate testDate) {
        log.info("Recording pregnancy test for checklist: {}, result: {}", checklistId, result);

        PatientPreparationChecklist checklist = getChecklistById(checklistId);

        if (!checklist.getPregnancyTestRequired()) {
            throw new IllegalStateException("Pregnancy test not required for this examination");
        }

        checklist.setPregnancyTestDone(true);
        checklist.setPregnancyTestResult(result);
        checklist.setPregnancyTestDate(testDate);

        updateCompletionStatus(checklist);

        return checklistRepository.save(checklist);
    }

    /**
     * Obtain consent
     */
    @Transactional
    public PatientPreparationChecklist obtainConsent(UUID checklistId, UUID obtainedBy, String consentFormId) {
        log.info("Recording consent for checklist: {}, form: {}", checklistId, consentFormId);

        PatientPreparationChecklist checklist = getChecklistById(checklistId);

        checklist.setConsentObtained(true);
        checklist.setConsentObtainedBy(obtainedBy);
        checklist.setConsentObtainedAt(LocalDateTime.now());
        checklist.setConsentFormId(consentFormId);

        updateCompletionStatus(checklist);

        return checklistRepository.save(checklist);
    }

    /**
     * Mark checklist as complete
     */
    @Transactional
    public PatientPreparationChecklist markComplete(UUID checklistId, UUID completedBy) {
        log.info("Marking checklist complete: {}", checklistId);

        PatientPreparationChecklist checklist = getChecklistById(checklistId);

        // Verify all required items are completed
        if (checklist.getFastingRequired() && !checklist.getFastingVerified()) {
            throw new IllegalStateException("Fasting verification is required but not completed");
        }

        if (checklist.getIvAccessRequired() && !checklist.getIvAccessVerified()) {
            throw new IllegalStateException("IV access verification is required but not completed");
        }

        if (checklist.getPregnancyTestRequired() && !checklist.getPregnancyTestDone()) {
            throw new IllegalStateException("Pregnancy test is required but not completed");
        }

        if (!checklist.getConsentObtained()) {
            throw new IllegalStateException("Consent must be obtained before completing checklist");
        }

        checklist.setAllItemsCompleted(true);
        checklist.setCompletedBy(completedBy);
        checklist.setCompletedAt(LocalDateTime.now());

        return checklistRepository.save(checklist);
    }

    /**
     * Get checklist by ID
     */
    public PatientPreparationChecklist getChecklistById(UUID id) {
        return checklistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found: " + id));
    }

    /**
     * Get checklist by order ID
     */
    public PatientPreparationChecklist getChecklistByOrderId(UUID orderId) {
        return checklistRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found for order: " + orderId));
    }

    /**
     * Get incomplete checklists
     */
    public List<PatientPreparationChecklist> getIncompleteChecklists() {
        return checklistRepository.findIncompleteChecklists();
    }

    /**
     * Get checklists awaiting fasting verification
     */
    public List<PatientPreparationChecklist> getAwaitingFastingVerification() {
        return checklistRepository.findAwaitingFastingVerification();
    }

    /**
     * Get checklists awaiting consent
     */
    public List<PatientPreparationChecklist> getAwaitingConsent() {
        return checklistRepository.findAwaitingConsent();
    }

    /**
     * Get checklists awaiting pregnancy test
     */
    public List<PatientPreparationChecklist> getAwaitingPregnancyTest() {
        return checklistRepository.findAwaitingPregnancyTest();
    }

    /**
     * Get checklists awaiting IV access
     */
    public List<PatientPreparationChecklist> getAwaitingIVAccess() {
        return checklistRepository.findAwaitingIVAccess();
    }

    /**
     * Update overall completion status based on requirements
     */
    private void updateCompletionStatus(PatientPreparationChecklist checklist) {
        boolean allComplete = true;

        // Check fasting
        if (checklist.getFastingRequired() && !Boolean.TRUE.equals(checklist.getFastingVerified())) {
            allComplete = false;
        }

        // Check IV access
        if (checklist.getIvAccessRequired() && !Boolean.TRUE.equals(checklist.getIvAccessVerified())) {
            allComplete = false;
        }

        // Check pregnancy test
        if (checklist.getPregnancyTestRequired() && !Boolean.TRUE.equals(checklist.getPregnancyTestDone())) {
            allComplete = false;
        }

        // Check consent
        if (!Boolean.TRUE.equals(checklist.getConsentObtained())) {
            allComplete = false;
        }

        if (allComplete && !Boolean.TRUE.equals(checklist.getAllItemsCompleted())) {
            checklist.setAllItemsCompleted(true);
            checklist.setCompletedAt(LocalDateTime.now());
        } else if (!allComplete && Boolean.TRUE.equals(checklist.getAllItemsCompleted())) {
            checklist.setAllItemsCompleted(false);
            checklist.setCompletedAt(null);
            checklist.setCompletedBy(null);
        }
    }
}
