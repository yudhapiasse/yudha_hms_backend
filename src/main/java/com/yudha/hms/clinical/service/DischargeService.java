package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.repository.*;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import com.yudha.hms.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Discharge Service.
 *
 * Handles complete discharge workflow including readiness assessment,
 * discharge summary creation, prescriptions, and instructions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DischargeService {

    private final DischargeSummaryRepository dischargeSummaryRepository;
    private final DischargeReadinessRepository dischargeReadinessRepository;
    private final DischargePrescriptionRepository dischargePrescriptionRepository;
    private final DischargeInstructionRepository dischargeInstructionRepository;
    private final EncounterRepository encounterRepository;

    // ========== Discharge Summary Operations ==========

    public DischargeSummaryResponse createDischargeSummary(DischargeSummaryRequest request) {
        log.info("Creating discharge summary for encounter: {}", request.getEncounterId());

        // Validate encounter
        Encounter encounter = encounterRepository.findById(request.getEncounterId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Encounter tidak ditemukan dengan ID: " + request.getEncounterId()
            ));

        // Check if discharge summary already exists
        if (dischargeSummaryRepository.existsByEncounterId(request.getEncounterId())) {
            throw new ValidationException(
                "Discharge summary sudah ada untuk encounter ini"
            );
        }

        // Check if discharge readiness assessment exists and is ready
        DischargeReadiness readiness = dischargeReadinessRepository
            .findByEncounterId(request.getEncounterId())
            .orElse(null);

        if (readiness != null && !Boolean.TRUE.equals(readiness.getReadyForDischarge())) {
            log.warn("Discharge readiness not met for encounter: {}", request.getEncounterId());
        }

        // Create discharge summary
        DischargeSummary summary = DischargeSummary.builder()
            .dischargeNumber(generateDischargeNumber())
            .encounterId(request.getEncounterId())
            .patientId(request.getPatientId())
            .dischargeDate(request.getDischargeDate())
            .dischargeDisposition(DischargeDisposition.valueOf(request.getDischargeDisposition()))
            .dischargeCondition(DischargeCondition.valueOf(request.getDischargeCondition()))
            .admissionDate(request.getAdmissionDate())
            .reasonForAdmission(request.getReasonForAdmission())
            .hospitalCourse(request.getHospitalCourse())
            .proceduresPerformed(request.getProceduresPerformed())
            .primaryDiagnosisCode(request.getPrimaryDiagnosisCode())
            .primaryDiagnosisText(request.getPrimaryDiagnosisText())
            .secondaryDiagnoses(request.getSecondaryDiagnoses())
            .vitalSignsAtDischarge(request.getVitalSignsAtDischarge())
            .dischargeMedications(request.getDischargeMedications())
            .medicationsDiscontinued(request.getMedicationsDiscontinued())
            .newMedications(request.getNewMedications())
            .followUpInstructions(request.getFollowUpInstructions())
            .followUpAppointmentDate(request.getFollowUpAppointmentDate())
            .followUpDoctor(request.getFollowUpDoctor())
            .followUpDepartment(request.getFollowUpDepartment())
            .dietInstructions(request.getDietInstructions())
            .activityRestrictions(request.getActivityRestrictions())
            .woundCareInstructions(request.getWoundCareInstructions())
            .warningSigns(request.getWarningSigns())
            .emergencyContact(request.getEmergencyContact())
            .referralTo(request.getReferralTo())
            .referralReason(request.getReferralReason())
            .dischargeDoctorId(request.getDischargeDoctorId())
            .dischargeDoctorName(request.getDischargeDoctorName())
            .attendingDoctorName(request.getAttendingDoctorName())
            .signed(false)
            .documentGenerated(false)
            .satusehatSubmitted(false)
            .build();

        summary = dischargeSummaryRepository.save(summary);
        log.info("Discharge summary created: {}", summary.getDischargeNumber());

        // Add prescriptions if provided
        if (request.getPrescriptions() != null && !request.getPrescriptions().isEmpty()) {
            for (DischargePrescriptionRequest rxRequest : request.getPrescriptions()) {
                addPrescription(summary.getId(), rxRequest);
            }
        }

        // Add instructions if provided
        if (request.getInstructions() != null && !request.getInstructions().isEmpty()) {
            for (DischargeInstructionRequest instrRequest : request.getInstructions()) {
                addInstruction(summary.getId(), instrRequest);
            }
        }

        // Update encounter status to FINISHED
        encounter.setStatus(EncounterStatus.FINISHED);
        encounter.setDischargeDate(request.getDischargeDate());
        encounterRepository.save(encounter);

        return mapToResponse(dischargeSummaryRepository.findById(summary.getId()).get());
    }

    public DischargeSummaryResponse getDischargeSummaryById(UUID id) {
        DischargeSummary summary = findDischargeSummaryById(id);
        return mapToResponse(summary);
    }

    public DischargeSummaryResponse getDischargeSummaryByEncounterId(UUID encounterId) {
        DischargeSummary summary = dischargeSummaryRepository.findByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Discharge summary tidak ditemukan untuk encounter: " + encounterId
            ));
        return mapToResponse(summary);
    }

    public List<DischargeSummaryResponse> getDischargeSummariesByPatientId(UUID patientId) {
        return dischargeSummaryRepository.findByPatientIdOrderByDischargeDateDesc(patientId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public DischargeSummaryResponse signDischargeSummary(UUID id, UUID doctorId, String doctorName) {
        log.info("Signing discharge summary: {} by doctor: {}", id, doctorName);

        DischargeSummary summary = findDischargeSummaryById(id);

        if (Boolean.TRUE.equals(summary.getSigned())) {
            throw new ValidationException("Discharge summary sudah ditandatangani");
        }

        summary.sign(doctorId, doctorName);
        dischargeSummaryRepository.save(summary);

        log.info("Discharge summary signed: {}", summary.getDischargeNumber());
        return mapToResponse(summary);
    }

    public DischargeSummaryResponse generateDocument(UUID id) {
        log.info("Generating discharge summary document: {}", id);

        DischargeSummary summary = findDischargeSummaryById(id);

        if (!Boolean.TRUE.equals(summary.getSigned())) {
            throw new ValidationException(
                "Discharge summary harus ditandatangani sebelum generate dokumen"
            );
        }

        // TODO: Implement PDF generation logic
        String documentUrl = "/documents/discharge/" + summary.getDischargeNumber() + ".pdf";

        summary.setDocumentGenerated(true);
        summary.setDocumentUrl(documentUrl);
        summary.setDocumentGeneratedAt(LocalDateTime.now());

        dischargeSummaryRepository.save(summary);

        log.info("Discharge summary document generated: {}", documentUrl);
        return mapToResponse(summary);
    }

    // ========== Discharge Readiness Operations ==========

    public DischargeReadinessResponse createDischargeReadiness(UUID encounterId, UUID patientId) {
        log.info("Creating discharge readiness assessment for encounter: {}", encounterId);

        if (dischargeReadinessRepository.existsByEncounterId(encounterId)) {
            throw new ValidationException(
                "Discharge readiness assessment sudah ada untuk encounter ini"
            );
        }

        DischargeReadiness readiness = DischargeReadiness.builder()
            .encounterId(encounterId)
            .patientId(patientId)
            .medicalStabilityMet(false)
            .homeCareArranged(false)
            .medicationsReconciled(false)
            .followUpScheduled(false)
            .patientEducationCompleted(false)
            .dmeOrdered(false)
            .medicalSuppliesProvided(false)
            .hasDischargeBarriers(false)
            .barriersResolved(false)
            .readyForDischarge(false)
            .build();

        readiness = dischargeReadinessRepository.save(readiness);
        log.info("Discharge readiness assessment created for encounter: {}", encounterId);

        return mapToReadinessResponse(readiness);
    }

    public DischargeReadinessResponse getDischargeReadinessByEncounterId(UUID encounterId) {
        DischargeReadiness readiness = dischargeReadinessRepository.findByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Discharge readiness assessment tidak ditemukan untuk encounter: " + encounterId
            ));
        return mapToReadinessResponse(readiness);
    }

    public DischargeReadinessResponse assessMedicalStability(
        UUID encounterId,
        boolean met,
        String notes,
        String assessedBy
    ) {
        DischargeReadiness readiness = findReadinessByEncounterId(encounterId);
        readiness.assessMedicalStability(met, notes, assessedBy);
        dischargeReadinessRepository.save(readiness);

        log.info("Medical stability assessed for encounter: {} - Met: {}", encounterId, met);
        return mapToReadinessResponse(readiness);
    }

    public DischargeReadinessResponse reconcileMedications(
        UUID encounterId,
        String notes,
        String reconciledBy
    ) {
        DischargeReadiness readiness = findReadinessByEncounterId(encounterId);
        readiness.reconcileMedications(notes, reconciledBy);
        dischargeReadinessRepository.save(readiness);

        log.info("Medications reconciled for encounter: {}", encounterId);
        return mapToReadinessResponse(readiness);
    }

    public DischargeReadinessResponse scheduleFollowUp(
        UUID encounterId,
        LocalDateTime appointmentDate,
        String provider,
        String department
    ) {
        DischargeReadiness readiness = findReadinessByEncounterId(encounterId);
        readiness.scheduleFollowUp(appointmentDate, provider, department);
        dischargeReadinessRepository.save(readiness);

        log.info("Follow-up scheduled for encounter: {} on {}", encounterId, appointmentDate);
        return mapToReadinessResponse(readiness);
    }

    public DischargeReadinessResponse markReadyForDischarge(
        UUID encounterId,
        UUID assessedById,
        String assessedByName
    ) {
        DischargeReadiness readiness = findReadinessByEncounterId(encounterId);
        readiness.markReadyForDischarge(assessedById, assessedByName);
        dischargeReadinessRepository.save(readiness);

        log.info("Encounter {} marked as ready for discharge", encounterId);
        return mapToReadinessResponse(readiness);
    }

    // ========== Prescription Operations ==========

    public DischargePrescriptionResponse addPrescription(
        UUID summaryId,
        DischargePrescriptionRequest request
    ) {
        DischargeSummary summary = findDischargeSummaryById(summaryId);

        DischargePrescription prescription = DischargePrescription.builder()
            .dischargeSummary(summary)
            .medicationId(request.getMedicationId())
            .medicationName(request.getMedicationName())
            .genericName(request.getGenericName())
            .medicationCategory(request.getMedicationCategory())
            .dosage(request.getDosage())
            .route(request.getRoute())
            .frequency(request.getFrequency())
            .duration(request.getDuration())
            .quantity(request.getQuantity())
            .unit(request.getUnit())
            .timing(request.getTiming())
            .specialInstructions(request.getSpecialInstructions())
            .foodInteraction(request.getFoodInteraction())
            .purpose(request.getPurpose())
            .sideEffects(request.getSideEffects())
            .warnings(request.getWarnings())
            .isNewMedication(request.getIsNewMedication())
            .isChangedMedication(request.getIsChangedMedication())
            .changeNotes(request.getChangeNotes())
            .refillsAllowed(request.getRefillsAllowed())
            .pharmacyNotes(request.getPharmacyNotes())
            .substitutionAllowed(request.getSubstitutionAllowed())
            .prescriberId(request.getPrescriberId())
            .prescriberName(request.getPrescriberName())
            .displayOrder(request.getDisplayOrder())
            .prescriptionStatus("ACTIVE")
            .build();

        prescription = dischargePrescriptionRepository.save(prescription);
        log.info("Prescription added to discharge summary: {}", summaryId);

        return mapToPrescriptionResponse(prescription);
    }

    public List<DischargePrescriptionResponse> getPrescriptionsBySummaryId(UUID summaryId) {
        return dischargePrescriptionRepository.findByDischargeSummaryId(summaryId)
            .stream()
            .map(this::mapToPrescriptionResponse)
            .collect(Collectors.toList());
    }

    // ========== Instruction Operations ==========

    public DischargeInstructionResponse addInstruction(
        UUID summaryId,
        DischargeInstructionRequest request
    ) {
        DischargeSummary summary = findDischargeSummaryById(summaryId);

        DischargeInstruction instruction = DischargeInstruction.builder()
            .dischargeSummary(summary)
            .instructionCategory(request.getInstructionCategory())
            .instructionTitle(request.getInstructionTitle())
            .instructionDetails(request.getInstructionDetails())
            .instructionFrequency(request.getInstructionFrequency())
            .instructionDuration(request.getInstructionDuration())
            .doInstructions(request.getDoInstructions())
            .dontInstructions(request.getDontInstructions())
            .whenToCallDoctor(request.getWhenToCallDoctor())
            .hasVideoTutorial(request.getHasVideoTutorial())
            .videoUrl(request.getVideoUrl())
            .hasPrintedMaterial(request.getHasPrintedMaterial())
            .printedMaterialUrl(request.getPrintedMaterialUrl())
            .diagramUrl(request.getDiagramUrl())
            .isCriticalInstruction(request.getIsCriticalInstruction())
            .displayOrder(request.getDisplayOrder())
            .additionalNotes(request.getAdditionalNotes())
            .patientEducated(false)
            .patientDemonstratesUnderstanding(false)
            .build();

        instruction = dischargeInstructionRepository.save(instruction);
        log.info("Instruction added to discharge summary: {}", summaryId);

        return mapToInstructionResponse(instruction);
    }

    public List<DischargeInstructionResponse> getInstructionsBySummaryId(UUID summaryId) {
        return dischargeInstructionRepository.findByDischargeSummaryId(summaryId)
            .stream()
            .map(this::mapToInstructionResponse)
            .collect(Collectors.toList());
    }

    public DischargeInstructionResponse markInstructionAsEducated(
        UUID instructionId,
        String educatorName,
        boolean understanding
    ) {
        DischargeInstruction instruction = dischargeInstructionRepository.findById(instructionId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Discharge instruction tidak ditemukan dengan ID: " + instructionId
            ));

        instruction.markAsEducated(educatorName, understanding);
        dischargeInstructionRepository.save(instruction);

        log.info("Instruction {} marked as educated", instructionId);
        return mapToInstructionResponse(instruction);
    }

    // ========== Helper Methods ==========

    private DischargeSummary findDischargeSummaryById(UUID id) {
        return dischargeSummaryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Discharge summary tidak ditemukan dengan ID: " + id
            ));
    }

    private DischargeReadiness findReadinessByEncounterId(UUID encounterId) {
        return dischargeReadinessRepository.findByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Discharge readiness tidak ditemukan untuk encounter: " + encounterId
            ));
    }

    private String generateDischargeNumber() {
        // Format: DIS-YYYYMMDD-NNNN
        String dateStr = java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        long count = dischargeSummaryRepository.count() + 1;
        return String.format("DIS-%s-%04d", dateStr, count);
    }

    // ========== Mapping Methods ==========

    private DischargeSummaryResponse mapToResponse(DischargeSummary summary) {
        DischargeSummaryResponse response = DischargeSummaryResponse.builder()
            .id(summary.getId())
            .dischargeNumber(summary.getDischargeNumber())
            .encounterId(summary.getEncounterId())
            .patientId(summary.getPatientId())
            .dischargeDate(summary.getDischargeDate())
            .dischargeTime(summary.getDischargeTime())
            .dischargeDisposition(summary.getDischargeDisposition().name())
            .dischargeDispositionDisplay(summary.getDischargeDisposition().getDisplayName())
            .dischargeCondition(summary.getDischargeCondition().name())
            .dischargeConditionDisplay(summary.getDischargeCondition().getDisplayName())
            .admissionDate(summary.getAdmissionDate())
            .lengthOfStayDays(summary.getLengthOfStayDays())
            .reasonForAdmission(summary.getReasonForAdmission())
            .hospitalCourse(summary.getHospitalCourse())
            .proceduresPerformed(summary.getProceduresPerformed())
            .primaryDiagnosisCode(summary.getPrimaryDiagnosisCode())
            .primaryDiagnosisText(summary.getPrimaryDiagnosisText())
            .secondaryDiagnoses(summary.getSecondaryDiagnoses())
            .vitalSignsAtDischarge(summary.getVitalSignsAtDischarge())
            .dischargeMedications(summary.getDischargeMedications())
            .medicationsDiscontinued(summary.getMedicationsDiscontinued())
            .newMedications(summary.getNewMedications())
            .followUpInstructions(summary.getFollowUpInstructions())
            .followUpAppointmentDate(summary.getFollowUpAppointmentDate())
            .followUpDoctor(summary.getFollowUpDoctor())
            .followUpDepartment(summary.getFollowUpDepartment())
            .dietInstructions(summary.getDietInstructions())
            .activityRestrictions(summary.getActivityRestrictions())
            .woundCareInstructions(summary.getWoundCareInstructions())
            .warningSigns(summary.getWarningSigns())
            .emergencyContact(summary.getEmergencyContact())
            .referralTo(summary.getReferralTo())
            .referralReason(summary.getReferralReason())
            .dischargeDoctorId(summary.getDischargeDoctorId())
            .dischargeDoctorName(summary.getDischargeDoctorName())
            .attendingDoctorName(summary.getAttendingDoctorName())
            .signed(summary.getSigned())
            .signedAt(summary.getSignedAt())
            .signedById(summary.getSignedById())
            .signedByName(summary.getSignedByName())
            .satusehatSubmitted(summary.getSatusehatSubmitted())
            .satusehatSubmissionDate(summary.getSatusehatSubmissionDate())
            .documentGenerated(summary.getDocumentGenerated())
            .documentUrl(summary.getDocumentUrl())
            .documentGeneratedAt(summary.getDocumentGeneratedAt())
            .isReadyForDischarge(summary.isReadyForDischarge())
            .hasAllDocuments(Boolean.TRUE.equals(summary.getDocumentGenerated()))
            .needsFollowUp(summary.getFollowUpAppointmentDate() != null)
            .createdAt(summary.getCreatedAt())
            .updatedAt(summary.getUpdatedAt())
            .createdBy(summary.getCreatedBy())
            .updatedBy(summary.getUpdatedBy())
            .build();

        // Load related data
        List<DischargePrescription> prescriptions =
            dischargePrescriptionRepository.findByDischargeSummaryId(summary.getId());
        response.setPrescriptions(prescriptions.stream()
            .map(this::mapToPrescriptionResponse)
            .collect(Collectors.toList()));

        List<DischargeInstruction> instructions =
            dischargeInstructionRepository.findByDischargeSummaryId(summary.getId());
        response.setInstructions(instructions.stream()
            .map(this::mapToInstructionResponse)
            .collect(Collectors.toList()));

        // Load readiness assessment if exists
        dischargeReadinessRepository.findByEncounterId(summary.getEncounterId())
            .ifPresent(readiness -> response.setReadinessAssessment(
                mapToReadinessResponse(readiness)
            ));

        return response;
    }

    private DischargeReadinessResponse mapToReadinessResponse(DischargeReadiness readiness) {
        return DischargeReadinessResponse.builder()
            .id(readiness.getId())
            .encounterId(readiness.getEncounterId())
            .patientId(readiness.getPatientId())
            .medicalStabilityMet(readiness.getMedicalStabilityMet())
            .medicalStabilityNotes(readiness.getMedicalStabilityNotes())
            .medicalStabilityAssessedAt(readiness.getMedicalStabilityAssessedAt())
            .medicalStabilityAssessedBy(readiness.getMedicalStabilityAssessedBy())
            .homeCareArranged(readiness.getHomeCareArranged())
            .homeCareNotes(readiness.getHomeCareNotes())
            .caregiverName(readiness.getCaregiverName())
            .caregiverContact(readiness.getCaregiverContact())
            .medicationsReconciled(readiness.getMedicationsReconciled())
            .medicationReconciliationNotes(readiness.getMedicationReconciliationNotes())
            .medicationReconciledAt(readiness.getMedicationReconciledAt())
            .medicationReconciledBy(readiness.getMedicationReconciledBy())
            .followUpScheduled(readiness.getFollowUpScheduled())
            .followUpAppointmentDate(readiness.getFollowUpAppointmentDate())
            .followUpProvider(readiness.getFollowUpProvider())
            .followUpDepartment(readiness.getFollowUpDepartment())
            .patientEducationCompleted(readiness.getPatientEducationCompleted())
            .patientEducationTopics(readiness.getPatientEducationTopics())
            .patientUnderstandingVerified(readiness.getPatientUnderstandingVerified())
            .dmeOrdered(readiness.getDmeOrdered())
            .dmeDescription(readiness.getDmeDescription())
            .medicalSuppliesProvided(readiness.getMedicalSuppliesProvided())
            .medicalSuppliesList(readiness.getMedicalSuppliesList())
            .hasDischargeBarriers(readiness.getHasDischargeBarriers())
            .dischargeBarriers(readiness.getDischargeBarriers())
            .barriersResolved(readiness.getBarriersResolved())
            .readyForDischarge(readiness.getReadyForDischarge())
            .readinessAssessedAt(readiness.getReadinessAssessedAt())
            .readinessAssessedById(readiness.getReadinessAssessedById())
            .readinessAssessedByName(readiness.getReadinessAssessedByName())
            .additionalNotes(readiness.getAdditionalNotes())
            .readinessPercentage(readiness.calculateReadinessPercentage())
            .createdAt(readiness.getCreatedAt())
            .updatedAt(readiness.getUpdatedAt())
            .build();
    }

    private DischargePrescriptionResponse mapToPrescriptionResponse(DischargePrescription prescription) {
        return DischargePrescriptionResponse.builder()
            .id(prescription.getId())
            .dischargeSummaryId(prescription.getDischargeSummary().getId())
            .medicationId(prescription.getMedicationId())
            .medicationName(prescription.getMedicationName())
            .genericName(prescription.getGenericName())
            .medicationCategory(prescription.getMedicationCategory())
            .dosage(prescription.getDosage())
            .route(prescription.getRoute())
            .frequency(prescription.getFrequency())
            .duration(prescription.getDuration())
            .quantity(prescription.getQuantity())
            .unit(prescription.getUnit())
            .timing(prescription.getTiming())
            .specialInstructions(prescription.getSpecialInstructions())
            .foodInteraction(prescription.getFoodInteraction())
            .purpose(prescription.getPurpose())
            .sideEffects(prescription.getSideEffects())
            .warnings(prescription.getWarnings())
            .isNewMedication(prescription.getIsNewMedication())
            .isChangedMedication(prescription.getIsChangedMedication())
            .changeNotes(prescription.getChangeNotes())
            .refillsAllowed(prescription.getRefillsAllowed())
            .pharmacyNotes(prescription.getPharmacyNotes())
            .substitutionAllowed(prescription.getSubstitutionAllowed())
            .prescriberId(prescription.getPrescriberId())
            .prescriberName(prescription.getPrescriberName())
            .prescriptionStatus(prescription.getPrescriptionStatus())
            .discontinuedReason(prescription.getDiscontinuedReason())
            .displayOrder(prescription.getDisplayOrder())
            .fullDosageInstructions(prescription.getFullDosageInstructions())
            .isCriticalMedication(prescription.isCriticalMedication())
            .createdAt(prescription.getCreatedAt())
            .updatedAt(prescription.getUpdatedAt())
            .createdBy(prescription.getCreatedBy())
            .updatedBy(prescription.getUpdatedBy())
            .build();
    }

    private DischargeInstructionResponse mapToInstructionResponse(DischargeInstruction instruction) {
        return DischargeInstructionResponse.builder()
            .id(instruction.getId())
            .dischargeSummaryId(instruction.getDischargeSummary().getId())
            .instructionCategory(instruction.getInstructionCategory())
            .instructionTitle(instruction.getInstructionTitle())
            .instructionDetails(instruction.getInstructionDetails())
            .instructionFrequency(instruction.getInstructionFrequency())
            .instructionDuration(instruction.getInstructionDuration())
            .doInstructions(instruction.getDoInstructions())
            .dontInstructions(instruction.getDontInstructions())
            .whenToCallDoctor(instruction.getWhenToCallDoctor())
            .hasVideoTutorial(instruction.getHasVideoTutorial())
            .videoUrl(instruction.getVideoUrl())
            .hasPrintedMaterial(instruction.getHasPrintedMaterial())
            .printedMaterialUrl(instruction.getPrintedMaterialUrl())
            .diagramUrl(instruction.getDiagramUrl())
            .patientEducated(instruction.getPatientEducated())
            .patientDemonstratesUnderstanding(instruction.getPatientDemonstratesUnderstanding())
            .educationNotes(instruction.getEducationNotes())
            .educatorName(instruction.getEducatorName())
            .isCriticalInstruction(instruction.getIsCriticalInstruction())
            .displayOrder(instruction.getDisplayOrder())
            .additionalNotes(instruction.getAdditionalNotes())
            .isWoundCare(instruction.isWoundCare())
            .isDietInstruction(instruction.isDietInstruction())
            .isActivityInstruction(instruction.isActivityInstruction())
            .isPhysicalTherapy(instruction.isPhysicalTherapy())
            .createdAt(instruction.getCreatedAt())
            .updatedAt(instruction.getUpdatedAt())
            .build();
    }
}
