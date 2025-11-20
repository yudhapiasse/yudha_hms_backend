package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.clinical.repository.ProgressNoteRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import com.yudha.hms.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for Progress Note management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgressNoteService {

    private final ProgressNoteRepository progressNoteRepository;
    private final EncounterRepository encounterRepository;

    /**
     * Create a new progress note.
     */
    public ProgressNoteResponse createProgressNote(UUID encounterId, ProgressNoteRequest request) {
        log.info("Creating progress note for encounter: {}", encounterId);

        // Validate encounter exists
        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter tidak ditemukan dengan ID: " + encounterId));

        // Build progress note
        ProgressNote progressNote = buildProgressNoteFromRequest(encounter, request);

        // Generate note number
        progressNote.setNoteNumber(generateNoteNumber(request.getNoteType()));

        // Set shift if not provided
        if (progressNote.getShift() == null && progressNote.getNoteDateTime() != null) {
            progressNote.setShift(determineShift(progressNote.getNoteDateTime()));
        }

        // Save progress note
        progressNote = progressNoteRepository.save(progressNote);
        log.info("Progress note created: {}", progressNote.getNoteNumber());

        return mapToResponse(progressNote);
    }

    /**
     * Get progress note by ID.
     */
    public ProgressNoteResponse getProgressNoteById(UUID id) {
        log.info("Retrieving progress note: {}", id);

        ProgressNote progressNote = progressNoteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Progress note tidak ditemukan dengan ID: " + id));

        return mapToResponse(progressNote);
    }

    /**
     * Get all progress notes for an encounter.
     */
    public List<ProgressNoteResponse> getProgressNotesByEncounter(UUID encounterId) {
        log.info("Retrieving progress notes for encounter: {}", encounterId);

        List<ProgressNote> notes = progressNoteRepository.findByEncounterId(encounterId);
        return notes.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get latest progress note for an encounter.
     */
    public ProgressNoteResponse getLatestProgressNote(UUID encounterId) {
        log.info("Retrieving latest progress note for encounter: {}", encounterId);

        ProgressNote progressNote = progressNoteRepository.findLatestByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Belum ada progress note untuk encounter ini"));

        return mapToResponse(progressNote);
    }

    /**
     * Get progress notes by type.
     */
    public List<ProgressNoteResponse> getProgressNotesByType(UUID encounterId, NoteType noteType) {
        log.info("Retrieving {} notes for encounter: {}", noteType, encounterId);

        List<ProgressNote> notes = progressNoteRepository.findByEncounterIdAndNoteType(encounterId, noteType);
        return notes.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get shift handover notes for a specific date.
     */
    public List<ProgressNoteResponse> getShiftHandoverNotes(UUID encounterId, LocalDate date) {
        log.info("Retrieving shift handover notes for encounter: {} on {}", encounterId, date);

        LocalDateTime dateTime = date.atStartOfDay();
        List<ProgressNote> notes = progressNoteRepository.findShiftHandoverNotesByDate(encounterId, dateTime);

        return notes.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get notes requiring cosign.
     */
    public List<ProgressNoteResponse> getNotesRequiringCosign() {
        log.info("Retrieving notes requiring cosign");

        List<ProgressNote> notes = progressNoteRepository.findNotesRequiringCosign();
        return notes.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get notes with critical findings.
     */
    public List<ProgressNoteResponse> getCriticalFindings(UUID encounterId) {
        log.info("Retrieving critical findings for encounter: {}", encounterId);

        List<ProgressNote> notes = progressNoteRepository.findCriticalFindingsByEncounterId(encounterId);
        return notes.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Update progress note.
     */
    public ProgressNoteResponse updateProgressNote(UUID id, ProgressNoteRequest request) {
        log.info("Updating progress note: {}", id);

        ProgressNote progressNote = progressNoteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Progress note tidak ditemukan dengan ID: " + id));

        // Validate if note can be updated (e.g., not cosigned yet)
        if (Boolean.TRUE.equals(progressNote.getCosigned())) {
            throw new ValidationException("Progress note yang sudah di-cosign tidak dapat diubah");
        }

        // Update fields
        updateProgressNoteFromRequest(progressNote, request);

        progressNote = progressNoteRepository.save(progressNote);
        log.info("Progress note updated: {}", progressNote.getNoteNumber());

        return mapToResponse(progressNote);
    }

    /**
     * Cosign a progress note.
     */
    public ProgressNoteResponse cosignProgressNote(UUID id, CosignRequest request) {
        log.info("Cosigning progress note: {}", id);

        ProgressNote progressNote = progressNoteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Progress note tidak ditemukan dengan ID: " + id));

        // Validate if note requires cosign
        if (!Boolean.TRUE.equals(progressNote.getRequiresCosign())) {
            throw new ValidationException("Progress note ini tidak memerlukan cosign");
        }

        // Validate if not already cosigned
        if (Boolean.TRUE.equals(progressNote.getCosigned())) {
            throw new ValidationException("Progress note sudah di-cosign");
        }

        // Cosign the note
        progressNote.cosign(request.getCosignedById(), request.getCosignedByName());

        progressNote = progressNoteRepository.save(progressNote);
        log.info("Progress note cosigned by: {}", request.getCosignedByName());

        return mapToResponse(progressNote);
    }

    /**
     * Delete progress note.
     */
    public void deleteProgressNote(UUID id) {
        log.info("Deleting progress note: {}", id);

        ProgressNote progressNote = progressNoteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Progress note tidak ditemukan dengan ID: " + id));

        // Validate if note can be deleted
        if (Boolean.TRUE.equals(progressNote.getCosigned())) {
            throw new ValidationException("Progress note yang sudah di-cosign tidak dapat dihapus");
        }

        progressNoteRepository.delete(progressNote);
        log.info("Progress note deleted: {}", progressNote.getNoteNumber());
    }

    /**
     * Check if encounter has SOAP note for today.
     */
    public boolean hasSOAPNoteToday(UUID encounterId) {
        LocalDateTime today = LocalDateTime.now();
        return progressNoteRepository.hasSOAPNoteForDate(encounterId, today);
    }

    // ========== Helper Methods ==========

    /**
     * Build ProgressNote entity from request.
     */
    private ProgressNote buildProgressNoteFromRequest(Encounter encounter, ProgressNoteRequest request) {
        return ProgressNote.builder()
            .encounter(encounter)
            .patientId(encounter.getPatientId())
            .noteType(request.getNoteType())
            .noteDateTime(request.getNoteDateTime() != null ? request.getNoteDateTime() : LocalDateTime.now())
            .shift(request.getShift())
            .subjective(request.getSubjective())
            .objective(request.getObjective())
            .assessment(request.getAssessment())
            .plan(request.getPlan())
            .additionalNotes(request.getAdditionalNotes())
            .followUpRequired(request.getFollowUpRequired())
            .followUpInstructions(request.getFollowUpInstructions())
            .criticalFindings(request.getCriticalFindings())
            .providerId(request.getProviderId())
            .providerName(request.getProviderName())
            .providerType(request.getProviderType())
            .providerSpecialty(request.getProviderSpecialty())
            .requiresCosign(request.getRequiresCosign())
            .build();
    }

    /**
     * Update ProgressNote from request.
     */
    private void updateProgressNoteFromRequest(ProgressNote progressNote, ProgressNoteRequest request) {
        if (request.getNoteType() != null) progressNote.setNoteType(request.getNoteType());
        if (request.getNoteDateTime() != null) progressNote.setNoteDateTime(request.getNoteDateTime());
        if (request.getShift() != null) progressNote.setShift(request.getShift());

        progressNote.setSubjective(request.getSubjective());
        progressNote.setObjective(request.getObjective());
        progressNote.setAssessment(request.getAssessment());
        progressNote.setPlan(request.getPlan());
        progressNote.setAdditionalNotes(request.getAdditionalNotes());
        progressNote.setFollowUpRequired(request.getFollowUpRequired());
        progressNote.setFollowUpInstructions(request.getFollowUpInstructions());
        progressNote.setCriticalFindings(request.getCriticalFindings());

        if (request.getProviderId() != null) progressNote.setProviderId(request.getProviderId());
        if (request.getProviderName() != null) progressNote.setProviderName(request.getProviderName());
        if (request.getProviderType() != null) progressNote.setProviderType(request.getProviderType());
        if (request.getProviderSpecialty() != null) progressNote.setProviderSpecialty(request.getProviderSpecialty());
    }

    /**
     * Generate note number.
     */
    private String generateNoteNumber(NoteType noteType) {
        String prefix = switch (noteType) {
            case SOAP -> "PN-SOAP";
            case OUTPATIENT_CONSULTATION -> "PN-OUTPT";
            case SHIFT_HANDOVER -> "PN-HAND";
            case CRITICAL_CARE -> "PN-CRIT";
            case NURSING -> "PN-NURS";
            case PROCEDURE -> "PN-PROC";
            case CONSULTATION -> "PN-CONS";
            case DISCHARGE_PLANNING -> "PN-DISC";
        };

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = progressNoteRepository.count() + 1;

        return String.format("%s-%s-%04d", prefix, dateStr, count);
    }

    /**
     * Determine shift based on time.
     */
    private Shift determineShift(LocalDateTime dateTime) {
        int hour = dateTime.getHour();

        if (hour >= 7 && hour < 15) {
            return Shift.MORNING;
        } else if (hour >= 15 && hour < 23) {
            return Shift.AFTERNOON;
        } else {
            return Shift.NIGHT;
        }
    }

    /**
     * Map entity to response DTO.
     */
    private ProgressNoteResponse mapToResponse(ProgressNote progressNote) {
        return ProgressNoteResponse.builder()
            .id(progressNote.getId())
            .noteNumber(progressNote.getNoteNumber())
            .encounterId(progressNote.getEncounter().getId())
            .patientId(progressNote.getPatientId())
            .noteType(progressNote.getNoteType())
            .noteTypeDisplay(progressNote.getNoteType() != null ? progressNote.getNoteType().getIndonesianName() : null)
            .noteDateTime(progressNote.getNoteDateTime())
            .shift(progressNote.getShift())
            .shiftDisplay(progressNote.getShift() != null ? progressNote.getShift().getIndonesianName() : null)
            .subjective(progressNote.getSubjective())
            .objective(progressNote.getObjective())
            .assessment(progressNote.getAssessment())
            .plan(progressNote.getPlan())
            .additionalNotes(progressNote.getAdditionalNotes())
            .followUpRequired(progressNote.getFollowUpRequired())
            .followUpInstructions(progressNote.getFollowUpInstructions())
            .criticalFindings(progressNote.getCriticalFindings())
            .providerId(progressNote.getProviderId())
            .providerName(progressNote.getProviderName())
            .providerType(progressNote.getProviderType())
            .providerTypeDisplay(progressNote.getProviderType() != null ? progressNote.getProviderType().getIndonesianName() : null)
            .providerSpecialty(progressNote.getProviderSpecialty())
            .requiresCosign(progressNote.getRequiresCosign())
            .cosigned(progressNote.getCosigned())
            .cosignedById(progressNote.getCosignedById())
            .cosignedByName(progressNote.getCosignedByName())
            .cosignedAt(progressNote.getCosignedAt())
            .createdAt(progressNote.getCreatedAt())
            .updatedAt(progressNote.getUpdatedAt())
            .createdBy(progressNote.getCreatedBy())
            .updatedBy(progressNote.getUpdatedBy())
            .isComplete(progressNote.isComplete())
            .needsCosign(progressNote.needsCosign())
            .hasCriticalFindings(progressNote.hasCriticalFindings())
            .build();
    }
}
