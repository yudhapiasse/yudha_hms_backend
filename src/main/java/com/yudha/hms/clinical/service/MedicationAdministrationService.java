package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.MedicationAdministration;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.clinical.repository.MedicationAdministrationRepository;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import com.yudha.hms.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for Medication Administration Record (MAR) management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MedicationAdministrationService {

    private final MedicationAdministrationRepository medicationAdministrationRepository;
    private final EncounterRepository encounterRepository;

    /**
     * Create medication administration record.
     */
    public MedicationAdministrationResponse createMedicationAdministration(
        UUID encounterId,
        MedicationAdministrationRequest request
    ) {
        log.info("Creating MAR entry for encounter: {} - Medication: {}", encounterId, request.getMedicationName());

        // Validate encounter exists
        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter tidak ditemukan dengan ID: " + encounterId));

        // Build medication administration entity
        MedicationAdministration medication = buildMedicationFromRequest(encounter, request);

        // Generate MAR number
        medication.setMarNumber(generateMarNumber());

        // Set scheduled date time if not provided
        if (medication.getScheduledDateTime() == null && medication.getScheduledDate() != null && medication.getScheduledTime() != null) {
            medication.setScheduledDateTime(
                LocalDateTime.of(medication.getScheduledDate(), medication.getScheduledTime())
            );
        }

        // Save medication
        medication = medicationAdministrationRepository.save(medication);
        log.info("MAR entry created: {} - {}", medication.getMarNumber(), medication.getMedicationName());

        return mapToResponse(medication);
    }

    /**
     * Get medication by ID.
     */
    public MedicationAdministrationResponse getMedicationById(UUID id) {
        log.info("Retrieving medication: {}", id);

        MedicationAdministration medication = medicationAdministrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medication tidak ditemukan dengan ID: " + id));

        return mapToResponse(medication);
    }

    /**
     * Get all medications for an encounter.
     */
    public List<MedicationAdministrationResponse> getMedicationsByEncounter(UUID encounterId) {
        log.info("Retrieving medications for encounter: {}", encounterId);

        List<MedicationAdministration> medications = medicationAdministrationRepository.findByEncounterId(encounterId);
        return medications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get due medications for an encounter.
     */
    public List<MedicationAdministrationResponse> getDueMedications(UUID encounterId) {
        log.info("Retrieving due medications for encounter: {}", encounterId);

        LocalDateTime now = LocalDateTime.now();
        List<MedicationAdministration> medications = medicationAdministrationRepository.findDueByEncounterId(
            encounterId, now
        );

        return medications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get overdue medications for an encounter.
     */
    public List<MedicationAdministrationResponse> getOverdueMedications(UUID encounterId) {
        log.info("Retrieving overdue medications for encounter: {}", encounterId);

        LocalDateTime overdueTime = LocalDateTime.now().minusHours(1);
        List<MedicationAdministration> medications = medicationAdministrationRepository.findOverdueByEncounterId(
            encounterId, overdueTime
        );

        return medications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get PRN medications for an encounter.
     */
    public List<MedicationAdministrationResponse> getPrnMedications(UUID encounterId) {
        log.info("Retrieving PRN medications for encounter: {}", encounterId);

        List<MedicationAdministration> medications = medicationAdministrationRepository.findPrnByEncounterId(encounterId);
        return medications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Administer medication.
     */
    public MedicationAdministrationResponse administerMedication(UUID id, AdministrationConfirmRequest request) {
        log.info("Administering medication: {}", id);

        MedicationAdministration medication = medicationAdministrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medication tidak ditemukan dengan ID: " + id));

        // Validate if not already administered
        if (Boolean.TRUE.equals(medication.getAdministered())) {
            throw new BusinessException("Medication sudah diberikan sebelumnya");
        }

        // Check if requires witness and if witness is present
        if (Boolean.TRUE.equals(medication.getRequiresWitness()) && medication.getWitnessedById() == null) {
            throw new ValidationException("Medication high-alert memerlukan witness verification sebelum diberikan");
        }

        // Mark as administered
        medication.markAsAdministered(request.getAdministeredById(), request.getAdministeredByName());

        // Set additional details
        if (request.getActualAdministrationDateTime() != null) {
            medication.setActualAdministrationDateTime(request.getActualAdministrationDateTime());
        }
        if (request.getAdministrationSite() != null) {
            medication.setAdministrationSite(request.getAdministrationSite());
        }
        if (request.getPatientResponse() != null) {
            medication.setPatientResponse(request.getPatientResponse());
        }
        if (request.getAdministrationNotes() != null) {
            medication.setAdministrationNotes(request.getAdministrationNotes());
        }

        // PRN specific
        if (request.getPrnReason() != null) {
            medication.setPrnReason(request.getPrnReason());
        }
        if (request.getPrnEffectiveness() != null) {
            medication.setPrnEffectiveness(request.getPrnEffectiveness());
        }

        medication.setAdministeredByRole(request.getAdministeredByRole());

        medication = medicationAdministrationRepository.save(medication);
        log.info("Medication administered: {} by {}", medication.getMarNumber(), request.getAdministeredByName());

        return mapToResponse(medication);
    }

    /**
     * Record medication refusal.
     */
    public MedicationAdministrationResponse refuseMedication(UUID id, MedicationRefusalRequest request) {
        log.info("Recording medication refusal: {}", id);

        MedicationAdministration medication = medicationAdministrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medication tidak ditemukan dengan ID: " + id));

        // Validate if not already administered
        if (Boolean.TRUE.equals(medication.getAdministered())) {
            throw new BusinessException("Medication sudah diberikan, tidak dapat ditolak");
        }

        // Mark as refused
        medication.markAsRefused(request.getNotGivenReason());

        medication = medicationAdministrationRepository.save(medication);
        log.info("Medication refused: {} - Reason: {}", medication.getMarNumber(), request.getNotGivenReason());

        return mapToResponse(medication);
    }

    /**
     * Hold medication.
     */
    public MedicationAdministrationResponse holdMedication(UUID id, MedicationHoldRequest request) {
        log.info("Holding medication: {}", id);

        MedicationAdministration medication = medicationAdministrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medication tidak ditemukan dengan ID: " + id));

        // Validate if not already administered
        if (Boolean.TRUE.equals(medication.getAdministered())) {
            throw new BusinessException("Medication sudah diberikan, tidak dapat di-hold");
        }

        // Hold medication
        medication.hold(request.getHoldReason());

        medication = medicationAdministrationRepository.save(medication);
        log.info("Medication held: {} - Reason: {}", medication.getMarNumber(), request.getHoldReason());

        return mapToResponse(medication);
    }

    /**
     * Mark medication as missed.
     */
    public MedicationAdministrationResponse markAsMissed(UUID id) {
        log.info("Marking medication as missed: {}", id);

        MedicationAdministration medication = medicationAdministrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medication tidak ditemukan dengan ID: " + id));

        // Validate if not already administered
        if (Boolean.TRUE.equals(medication.getAdministered())) {
            throw new BusinessException("Medication sudah diberikan, tidak dapat di-mark sebagai missed");
        }

        // Mark as missed
        medication.markAsMissed();

        medication = medicationAdministrationRepository.save(medication);
        log.info("Medication marked as missed: {}", medication.getMarNumber());

        return mapToResponse(medication);
    }

    /**
     * Report adverse reaction.
     */
    public MedicationAdministrationResponse reportAdverseReaction(UUID id, AdverseReactionRequest request) {
        log.info("Reporting adverse reaction for medication: {}", id);

        MedicationAdministration medication = medicationAdministrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medication tidak ditemukan dengan ID: " + id));

        // Report adverse reaction
        medication.reportAdverseReaction(
            request.getAdverseReactionType(),
            request.getAdverseReactionDetails(),
            request.getAdverseReactionSeverity()
        );

        medication = medicationAdministrationRepository.save(medication);
        log.warn("ADVERSE REACTION REPORTED for {}: {} - Severity: {}",
            medication.getMedicationName(),
            request.getAdverseReactionType(),
            request.getAdverseReactionSeverity()
        );

        // TODO: Send notification to pharmacy and attending physician

        return mapToResponse(medication);
    }

    /**
     * Add witness verification.
     */
    public MedicationAdministrationResponse addWitnessVerification(UUID id, WitnessVerificationRequest request) {
        log.info("Adding witness verification for medication: {}", id);

        MedicationAdministration medication = medicationAdministrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medication tidak ditemukan dengan ID: " + id));

        // Validate if requires witness
        if (!Boolean.TRUE.equals(medication.getRequiresWitness())) {
            throw new ValidationException("Medication ini tidak memerlukan witness verification");
        }

        // Validate if not already witnessed
        if (medication.getWitnessedById() != null) {
            throw new BusinessException("Medication sudah di-witness sebelumnya");
        }

        // Add witness
        medication.addWitness(
            request.getWitnessedById(),
            request.getWitnessedByName(),
            request.getWitnessSignature()
        );

        medication = medicationAdministrationRepository.save(medication);
        log.info("Witness verification added for {}: {}", medication.getMarNumber(), request.getWitnessedByName());

        return mapToResponse(medication);
    }

    /**
     * Get medications with adverse reactions.
     */
    public List<MedicationAdministrationResponse> getMedicationsWithAdverseReactions(UUID encounterId) {
        log.info("Retrieving medications with adverse reactions for encounter: {}", encounterId);

        List<MedicationAdministration> medications = medicationAdministrationRepository
            .findWithAdverseReactionsByEncounterId(encounterId);

        return medications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get high-alert medications.
     */
    public List<MedicationAdministrationResponse> getHighAlertMedications(UUID encounterId) {
        log.info("Retrieving high-alert medications for encounter: {}", encounterId);

        List<MedicationAdministration> medications = medicationAdministrationRepository
            .findHighAlertByEncounterId(encounterId);

        return medications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get medications requiring witness.
     */
    public List<MedicationAdministrationResponse> getMedicationsRequiringWitness() {
        log.info("Retrieving medications requiring witness verification");

        List<MedicationAdministration> medications = medicationAdministrationRepository.findRequiringWitness();
        return medications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ========== Helper Methods ==========

    /**
     * Build MedicationAdministration entity from request.
     */
    private MedicationAdministration buildMedicationFromRequest(
        Encounter encounter,
        MedicationAdministrationRequest request
    ) {
        return MedicationAdministration.builder()
            .encounter(encounter)
            .patientId(encounter.getPatientId())
            .medicationOrderId(request.getMedicationOrderId())
            .medicationName(request.getMedicationName())
            .genericName(request.getGenericName())
            .brandName(request.getBrandName())
            .medicationCode(request.getMedicationCode())
            .medicationClass(request.getMedicationClass())
            .dose(request.getDose())
            .doseUnit(request.getDoseUnit())
            .strength(request.getStrength())
            .totalDoseDescription(request.getTotalDoseDescription())
            .route(request.getRoute())
            .frequency(request.getFrequency())
            .frequencyTimesPerDay(request.getFrequencyTimesPerDay())
            .scheduleType(request.getScheduleType())
            .scheduledDate(request.getScheduledDate())
            .scheduledTime(request.getScheduledTime())
            .scheduledDateTime(request.getScheduledDateTime())
            .administrationSite(request.getAdministrationSite())
            .requiresWitness(request.getRequiresWitness())
            .prescribedById(request.getPrescribedById())
            .prescribedByName(request.getPrescribedByName())
            .prescriptionDate(request.getPrescriptionDate())
            .ivSolution(request.getIvSolution())
            .ivVolumeMl(request.getIvVolumeMl())
            .ivRateMlPerHour(request.getIvRateMlPerHour())
            .ivDurationMinutes(request.getIvDurationMinutes())
            .ivSiteLocation(request.getIvSiteLocation())
            .administrationNotes(request.getAdministrationNotes())
            .specialInstructions(request.getSpecialInstructions())
            .isHighAlertMedication(request.getIsHighAlertMedication())
            .highAlertType(request.getHighAlertType())
            .build();
    }

    /**
     * Generate MAR number.
     */
    private String generateMarNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = medicationAdministrationRepository.count() + 1;

        return String.format("MAR-%s-%04d", dateStr, count);
    }

    /**
     * Map entity to response DTO.
     */
    private MedicationAdministrationResponse mapToResponse(MedicationAdministration medication) {
        return MedicationAdministrationResponse.builder()
            .id(medication.getId())
            .marNumber(medication.getMarNumber())
            .encounterId(medication.getEncounter().getId())
            .patientId(medication.getPatientId())
            .medicationOrderId(medication.getMedicationOrderId())
            .medicationName(medication.getMedicationName())
            .genericName(medication.getGenericName())
            .brandName(medication.getBrandName())
            .medicationCode(medication.getMedicationCode())
            .medicationClass(medication.getMedicationClass())
            .dose(medication.getDose())
            .doseUnit(medication.getDoseUnit())
            .strength(medication.getStrength())
            .totalDoseDescription(medication.getTotalDoseDescription())
            .route(medication.getRoute())
            .frequency(medication.getFrequency())
            .frequencyTimesPerDay(medication.getFrequencyTimesPerDay())
            .scheduleType(medication.getScheduleType())
            .scheduleTypeDisplay(medication.getScheduleType() != null ? medication.getScheduleType().getIndonesianName() : null)
            .scheduledDate(medication.getScheduledDate())
            .scheduledTime(medication.getScheduledTime())
            .scheduledDateTime(medication.getScheduledDateTime())
            .actualAdministrationDateTime(medication.getActualAdministrationDateTime())
            .administered(medication.getAdministered())
            .administrationStatus(medication.getAdministrationStatus())
            .administrationStatusDisplay(medication.getAdministrationStatus() != null ? medication.getAdministrationStatus().getIndonesianName() : null)
            .administrationSite(medication.getAdministrationSite())
            .administeredById(medication.getAdministeredById())
            .administeredByName(medication.getAdministeredByName())
            .administeredByRole(medication.getAdministeredByRole())
            .requiresWitness(medication.getRequiresWitness())
            .witnessedById(medication.getWitnessedById())
            .witnessedByName(medication.getWitnessedByName())
            .witnessSignature(medication.getWitnessSignature())
            .notGivenReason(medication.getNotGivenReason())
            .holdReason(medication.getHoldReason())
            .discontinueReason(medication.getDiscontinueReason())
            .patientResponse(medication.getPatientResponse())
            .adverseReaction(medication.getAdverseReaction())
            .adverseReactionType(medication.getAdverseReactionType())
            .adverseReactionDetails(medication.getAdverseReactionDetails())
            .adverseReactionSeverity(medication.getAdverseReactionSeverity())
            .adverseReactionReported(medication.getAdverseReactionReported())
            .prnReason(medication.getPrnReason())
            .prnEffectiveness(medication.getPrnEffectiveness())
            .prescribedById(medication.getPrescribedById())
            .prescribedByName(medication.getPrescribedByName())
            .prescriptionDate(medication.getPrescriptionDate())
            .ivSolution(medication.getIvSolution())
            .ivVolumeMl(medication.getIvVolumeMl())
            .ivRateMlPerHour(medication.getIvRateMlPerHour())
            .ivDurationMinutes(medication.getIvDurationMinutes())
            .ivSiteLocation(medication.getIvSiteLocation())
            .administrationNotes(medication.getAdministrationNotes())
            .specialInstructions(medication.getSpecialInstructions())
            .isHighAlertMedication(medication.getIsHighAlertMedication())
            .highAlertType(medication.getHighAlertType())
            .createdAt(medication.getCreatedAt())
            .updatedAt(medication.getUpdatedAt())
            .createdBy(medication.getCreatedBy())
            .updatedBy(medication.getUpdatedBy())
            .isDue(medication.isDue())
            .isOverdue(medication.isOverdue())
            .needsWitnessVerification(medication.needsWitnessVerification())
            .build();
    }
}
