package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.EncounterStatus;
import com.yudha.hms.clinical.service.EncounterService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Encounter Controller.
 * REST API endpoints for encounter/visit management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/api/clinical/encounters")
@RequiredArgsConstructor
@Slf4j
public class EncounterController {

    private final EncounterService encounterService;

    /**
     * POST /api/clinical/encounters
     * Create a new encounter.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EncounterResponse>> createEncounter(
        @Valid @RequestBody EncounterRequest request
    ) {
        log.info("POST /api/clinical/encounters - Creating encounter for patient: {}", request.getPatientId());

        EncounterResponse response = encounterService.createEncounter(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<EncounterResponse>builder()
                .success(true)
                .message("Encounter berhasil dibuat dengan nomor: " + response.getEncounterNumber())
                .data(response)
                .build());
    }

    /**
     * GET /api/clinical/encounters/{id}
     * Get encounter by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EncounterResponse>> getEncounterById(
        @PathVariable UUID id
    ) {
        log.info("GET /api/clinical/encounters/{} - Retrieving encounter", id);

        EncounterResponse response = encounterService.getEncounterById(id);

        return ResponseEntity.ok(ApiResponse.<EncounterResponse>builder()
            .success(true)
            .message("Encounter berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * GET /api/clinical/encounters/number/{encounterNumber}
     * Get encounter by encounter number.
     */
    @GetMapping("/number/{encounterNumber}")
    public ResponseEntity<ApiResponse<EncounterResponse>> getEncounterByNumber(
        @PathVariable String encounterNumber
    ) {
        log.info("GET /api/clinical/encounters/number/{} - Retrieving encounter", encounterNumber);

        EncounterResponse response = encounterService.getEncounterByNumber(encounterNumber);

        return ResponseEntity.ok(ApiResponse.<EncounterResponse>builder()
            .success(true)
            .message("Encounter berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * GET /api/clinical/encounters/patient/{patientId}
     * Get all encounters for a patient.
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<EncounterSummaryDto>>> getEncountersByPatient(
        @PathVariable UUID patientId
    ) {
        log.info("GET /api/clinical/encounters/patient/{} - Retrieving patient encounters", patientId);

        List<EncounterSummaryDto> encounters = encounterService.getEncountersByPatient(patientId);

        return ResponseEntity.ok(ApiResponse.<List<EncounterSummaryDto>>builder()
            .success(true)
            .message("Daftar encounter pasien berhasil diambil")
            .data(encounters)
            .build());
    }

    /**
     * POST /api/clinical/encounters/search
     * Search encounters with criteria.
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<EncounterSummaryDto>>> searchEncounters(
        @RequestBody EncounterSearchCriteria criteria
    ) {
        log.info("POST /api/clinical/encounters/search - Searching encounters");

        Page<EncounterSummaryDto> encounters = encounterService.searchEncounters(criteria);

        return ResponseEntity.ok(ApiResponse.<Page<EncounterSummaryDto>>builder()
            .success(true)
            .message("Pencarian encounter berhasil")
            .data(encounters)
            .build());
    }

    /**
     * PUT /api/clinical/encounters/{id}
     * Update an encounter.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EncounterResponse>> updateEncounter(
        @PathVariable UUID id,
        @Valid @RequestBody EncounterRequest request
    ) {
        log.info("PUT /api/clinical/encounters/{} - Updating encounter", id);

        EncounterResponse response = encounterService.updateEncounter(id, request);

        return ResponseEntity.ok(ApiResponse.<EncounterResponse>builder()
            .success(true)
            .message("Encounter berhasil diperbarui")
            .data(response)
            .build());
    }

    /**
     * PATCH /api/clinical/encounters/{id}/status
     * Update encounter status.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EncounterResponse>> updateStatus(
        @PathVariable UUID id,
        @RequestParam EncounterStatus status,
        @RequestParam(required = false) String reason
    ) {
        log.info("PATCH /api/clinical/encounters/{}/status - Updating status to: {}", id, status);

        EncounterResponse response = encounterService.updateStatus(id, status, reason);

        return ResponseEntity.ok(ApiResponse.<EncounterResponse>builder()
            .success(true)
            .message("Status encounter berhasil diperbarui menjadi: " + status.getIndonesianName())
            .data(response)
            .build());
    }

    /**
     * POST /api/clinical/encounters/{id}/start
     * Start an encounter.
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<EncounterResponse>> startEncounter(
        @PathVariable UUID id
    ) {
        log.info("POST /api/clinical/encounters/{}/start - Starting encounter", id);

        EncounterResponse response = encounterService.startEncounter(id);

        return ResponseEntity.ok(ApiResponse.<EncounterResponse>builder()
            .success(true)
            .message("Encounter berhasil dimulai")
            .data(response)
            .build());
    }

    /**
     * POST /api/clinical/encounters/{id}/finish
     * Finish an encounter.
     */
    @PostMapping("/{id}/finish")
    public ResponseEntity<ApiResponse<EncounterResponse>> finishEncounter(
        @PathVariable UUID id
    ) {
        log.info("POST /api/clinical/encounters/{}/finish - Finishing encounter", id);

        EncounterResponse response = encounterService.finishEncounter(id);

        return ResponseEntity.ok(ApiResponse.<EncounterResponse>builder()
            .success(true)
            .message("Encounter berhasil diselesaikan")
            .data(response)
            .build());
    }

    /**
     * POST /api/clinical/encounters/{id}/cancel
     * Cancel an encounter.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<EncounterResponse>> cancelEncounter(
        @PathVariable UUID id,
        @RequestParam String reason
    ) {
        log.info("POST /api/clinical/encounters/{}/cancel - Cancelling encounter", id);

        EncounterResponse response = encounterService.cancelEncounter(id, reason);

        return ResponseEntity.ok(ApiResponse.<EncounterResponse>builder()
            .success(true)
            .message("Encounter berhasil dibatalkan")
            .data(response)
            .build());
    }

    /**
     * POST /api/clinical/encounters/{id}/participants
     * Add a participant to an encounter.
     */
    @PostMapping("/{id}/participants")
    public ResponseEntity<ApiResponse<EncounterParticipantDto>> addParticipant(
        @PathVariable UUID id,
        @Valid @RequestBody EncounterParticipantDto participantDto
    ) {
        log.info("POST /api/clinical/encounters/{}/participants - Adding participant", id);

        EncounterParticipantDto response = encounterService.addParticipant(id, participantDto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<EncounterParticipantDto>builder()
                .success(true)
                .message("Partisipan berhasil ditambahkan ke encounter")
                .data(response)
                .build());
    }

    /**
     * GET /api/clinical/encounters/{id}/participants
     * Get all participants for an encounter.
     */
    @GetMapping("/{id}/participants")
    public ResponseEntity<ApiResponse<List<EncounterParticipantDto>>> getParticipants(
        @PathVariable UUID id
    ) {
        log.info("GET /api/clinical/encounters/{}/participants - Retrieving participants", id);

        List<EncounterParticipantDto> participants = encounterService.getEncounterParticipants(id);

        return ResponseEntity.ok(ApiResponse.<List<EncounterParticipantDto>>builder()
            .success(true)
            .message("Daftar partisipan encounter berhasil diambil")
            .data(participants)
            .build());
    }

    /**
     * POST /api/clinical/encounters/{id}/diagnoses
     * Add a diagnosis to an encounter.
     */
    @PostMapping("/{id}/diagnoses")
    public ResponseEntity<ApiResponse<EncounterDiagnosisDto>> addDiagnosis(
        @PathVariable UUID id,
        @Valid @RequestBody EncounterDiagnosisDto diagnosisDto
    ) {
        log.info("POST /api/clinical/encounters/{}/diagnoses - Adding diagnosis", id);

        EncounterDiagnosisDto response = encounterService.addDiagnosis(id, diagnosisDto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<EncounterDiagnosisDto>builder()
                .success(true)
                .message("Diagnosis berhasil ditambahkan ke encounter")
                .data(response)
                .build());
    }

    /**
     * GET /api/clinical/encounters/{id}/diagnoses
     * Get all diagnoses for an encounter.
     */
    @GetMapping("/{id}/diagnoses")
    public ResponseEntity<ApiResponse<List<EncounterDiagnosisDto>>> getDiagnoses(
        @PathVariable UUID id
    ) {
        log.info("GET /api/clinical/encounters/{}/diagnoses - Retrieving diagnoses", id);

        List<EncounterDiagnosisDto> diagnoses = encounterService.getEncounterDiagnoses(id);

        return ResponseEntity.ok(ApiResponse.<List<EncounterDiagnosisDto>>builder()
            .success(true)
            .message("Daftar diagnosis encounter berhasil diambil")
            .data(diagnoses)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{id}/status-history
     * Get status history for an encounter.
     */
    @GetMapping("/{id}/status-history")
    public ResponseEntity<ApiResponse<List<EncounterStatusHistoryDto>>> getStatusHistory(
        @PathVariable UUID id
    ) {
        log.info("GET /api/clinical/encounters/{}/status-history - Retrieving status history", id);

        List<EncounterStatusHistoryDto> history = encounterService.getEncounterStatusHistory(id);

        return ResponseEntity.ok(ApiResponse.<List<EncounterStatusHistoryDto>>builder()
            .success(true)
            .message("Riwayat status encounter berhasil diambil")
            .data(history)
            .build());
    }

    /**
     * DELETE /api/clinical/encounters/{id}
     * Delete an encounter.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEncounter(
        @PathVariable UUID id
    ) {
        log.info("DELETE /api/clinical/encounters/{} - Deleting encounter", id);

        encounterService.deleteEncounter(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true)
            .message("Encounter berhasil dihapus")
            .build());
    }
}
