package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.service.MedicationAdministrationService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Medication Administration Controller.
 * REST API endpoints for Medication Administration Record (MAR).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical")
@RequiredArgsConstructor
@Slf4j
public class MedicationAdministrationController {

    private final MedicationAdministrationService medicationAdministrationService;

    /**
     * POST /api/clinical/encounters/{encounterId}/medications
     * Create medication administration record.
     */
    @PostMapping("/encounters/{encounterId}/medications")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> createMedication(
        @PathVariable UUID encounterId,
        @Valid @RequestBody MedicationAdministrationRequest request
    ) {
        log.info("POST /api/clinical/encounters/{}/medications - Creating MAR entry: {}",
            encounterId, request.getMedicationName());

        MedicationAdministrationResponse response = medicationAdministrationService
            .createMedicationAdministration(encounterId, request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<MedicationAdministrationResponse>builder()
                .success(true)
                .message("MAR entry berhasil dibuat: " + response.getMarNumber())
                .data(response)
                .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/medications
     * Get all medications for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/medications")
    public ResponseEntity<ApiResponse<List<MedicationAdministrationResponse>>> getMedicationsByEncounter(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/medications", encounterId);

        List<MedicationAdministrationResponse> medications = medicationAdministrationService
            .getMedicationsByEncounter(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<MedicationAdministrationResponse>>builder()
            .success(true)
            .message("Daftar medications berhasil diambil")
            .data(medications)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/medications/due
     * Get due medications for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/medications/due")
    public ResponseEntity<ApiResponse<List<MedicationAdministrationResponse>>> getDueMedications(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/medications/due", encounterId);

        List<MedicationAdministrationResponse> medications = medicationAdministrationService
            .getDueMedications(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<MedicationAdministrationResponse>>builder()
            .success(true)
            .message("Medications yang due berhasil diambil")
            .data(medications)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/medications/overdue
     * Get overdue medications for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/medications/overdue")
    public ResponseEntity<ApiResponse<List<MedicationAdministrationResponse>>> getOverdueMedications(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/medications/overdue", encounterId);

        List<MedicationAdministrationResponse> medications = medicationAdministrationService
            .getOverdueMedications(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<MedicationAdministrationResponse>>builder()
            .success(true)
            .message("Medications yang overdue berhasil diambil")
            .data(medications)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/medications/prn
     * Get PRN medications for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/medications/prn")
    public ResponseEntity<ApiResponse<List<MedicationAdministrationResponse>>> getPrnMedications(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/medications/prn", encounterId);

        List<MedicationAdministrationResponse> medications = medicationAdministrationService
            .getPrnMedications(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<MedicationAdministrationResponse>>builder()
            .success(true)
            .message("PRN medications berhasil diambil")
            .data(medications)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/medications/high-alert
     * Get high-alert medications for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/medications/high-alert")
    public ResponseEntity<ApiResponse<List<MedicationAdministrationResponse>>> getHighAlertMedications(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/medications/high-alert", encounterId);

        List<MedicationAdministrationResponse> medications = medicationAdministrationService
            .getHighAlertMedications(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<MedicationAdministrationResponse>>builder()
            .success(true)
            .message("High-alert medications berhasil diambil")
            .data(medications)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/medications/adverse-reactions
     * Get medications with adverse reactions for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/medications/adverse-reactions")
    public ResponseEntity<ApiResponse<List<MedicationAdministrationResponse>>> getMedicationsWithAdverseReactions(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/medications/adverse-reactions", encounterId);

        List<MedicationAdministrationResponse> medications = medicationAdministrationService
            .getMedicationsWithAdverseReactions(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<MedicationAdministrationResponse>>builder()
            .success(true)
            .message("Medications dengan adverse reactions berhasil diambil")
            .data(medications)
            .build());
    }

    /**
     * GET /api/clinical/medications/requiring-witness
     * Get all medications requiring witness verification.
     */
    @GetMapping("/medications/requiring-witness")
    public ResponseEntity<ApiResponse<List<MedicationAdministrationResponse>>> getMedicationsRequiringWitness() {
        log.info("GET /api/clinical/medications/requiring-witness");

        List<MedicationAdministrationResponse> medications = medicationAdministrationService
            .getMedicationsRequiringWitness();

        return ResponseEntity.ok(ApiResponse.<List<MedicationAdministrationResponse>>builder()
            .success(true)
            .message("Medications requiring witness berhasil diambil")
            .data(medications)
            .build());
    }

    /**
     * GET /api/clinical/medications/{id}
     * Get medication by ID.
     */
    @GetMapping("/medications/{id}")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> getMedicationById(
        @PathVariable UUID id
    ) {
        log.info("GET /api/clinical/medications/{}", id);

        MedicationAdministrationResponse response = medicationAdministrationService.getMedicationById(id);

        return ResponseEntity.ok(ApiResponse.<MedicationAdministrationResponse>builder()
            .success(true)
            .message("Medication berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * PATCH /api/clinical/medications/{id}/administer
     * Administer medication.
     */
    @PatchMapping("/medications/{id}/administer")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> administerMedication(
        @PathVariable UUID id,
        @Valid @RequestBody AdministrationConfirmRequest request
    ) {
        log.info("PATCH /api/clinical/medications/{}/administer - By: {}", id, request.getAdministeredByName());

        MedicationAdministrationResponse response = medicationAdministrationService
            .administerMedication(id, request);

        return ResponseEntity.ok(ApiResponse.<MedicationAdministrationResponse>builder()
            .success(true)
            .message("Medication berhasil diberikan")
            .data(response)
            .build());
    }

    /**
     * PATCH /api/clinical/medications/{id}/refuse
     * Record medication refusal.
     */
    @PatchMapping("/medications/{id}/refuse")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> refuseMedication(
        @PathVariable UUID id,
        @Valid @RequestBody MedicationRefusalRequest request
    ) {
        log.info("PATCH /api/clinical/medications/{}/refuse", id);

        MedicationAdministrationResponse response = medicationAdministrationService
            .refuseMedication(id, request);

        return ResponseEntity.ok(ApiResponse.<MedicationAdministrationResponse>builder()
            .success(true)
            .message("Medication refusal berhasil dicatat")
            .data(response)
            .build());
    }

    /**
     * PATCH /api/clinical/medications/{id}/hold
     * Hold medication.
     */
    @PatchMapping("/medications/{id}/hold")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> holdMedication(
        @PathVariable UUID id,
        @Valid @RequestBody MedicationHoldRequest request
    ) {
        log.info("PATCH /api/clinical/medications/{}/hold", id);

        MedicationAdministrationResponse response = medicationAdministrationService
            .holdMedication(id, request);

        return ResponseEntity.ok(ApiResponse.<MedicationAdministrationResponse>builder()
            .success(true)
            .message("Medication berhasil di-hold")
            .data(response)
            .build());
    }

    /**
     * PATCH /api/clinical/medications/{id}/missed
     * Mark medication as missed.
     */
    @PatchMapping("/medications/{id}/missed")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> markAsMissed(
        @PathVariable UUID id
    ) {
        log.info("PATCH /api/clinical/medications/{}/missed", id);

        MedicationAdministrationResponse response = medicationAdministrationService.markAsMissed(id);

        return ResponseEntity.ok(ApiResponse.<MedicationAdministrationResponse>builder()
            .success(true)
            .message("Medication berhasil ditandai sebagai missed")
            .data(response)
            .build());
    }

    /**
     * POST /api/clinical/medications/{id}/adverse-reaction
     * Report adverse reaction.
     */
    @PostMapping("/medications/{id}/adverse-reaction")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> reportAdverseReaction(
        @PathVariable UUID id,
        @Valid @RequestBody AdverseReactionRequest request
    ) {
        log.info("POST /api/clinical/medications/{}/adverse-reaction - Type: {}",
            id, request.getAdverseReactionType());

        MedicationAdministrationResponse response = medicationAdministrationService
            .reportAdverseReaction(id, request);

        return ResponseEntity.ok(ApiResponse.<MedicationAdministrationResponse>builder()
            .success(true)
            .message("Adverse reaction berhasil dilaporkan")
            .data(response)
            .build());
    }

    /**
     * POST /api/clinical/medications/{id}/witness
     * Add witness verification.
     */
    @PostMapping("/medications/{id}/witness")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> addWitnessVerification(
        @PathVariable UUID id,
        @Valid @RequestBody WitnessVerificationRequest request
    ) {
        log.info("POST /api/clinical/medications/{}/witness - By: {}", id, request.getWitnessedByName());

        MedicationAdministrationResponse response = medicationAdministrationService
            .addWitnessVerification(id, request);

        return ResponseEntity.ok(ApiResponse.<MedicationAdministrationResponse>builder()
            .success(true)
            .message("Witness verification berhasil ditambahkan")
            .data(response)
            .build());
    }
}
