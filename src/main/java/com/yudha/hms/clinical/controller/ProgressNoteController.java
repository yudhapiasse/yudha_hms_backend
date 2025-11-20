package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.CosignRequest;
import com.yudha.hms.clinical.dto.ProgressNoteRequest;
import com.yudha.hms.clinical.dto.ProgressNoteResponse;
import com.yudha.hms.clinical.entity.NoteType;
import com.yudha.hms.clinical.service.ProgressNoteService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Progress Note Controller.
 * REST API endpoints for SOAP notes and progress documentation.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical")
@RequiredArgsConstructor
@Slf4j
public class ProgressNoteController {

    private final ProgressNoteService progressNoteService;

    /**
     * POST /api/clinical/encounters/{encounterId}/progress-notes
     * Create a new progress note.
     */
    @PostMapping("/encounters/{encounterId}/progress-notes")
    public ResponseEntity<ApiResponse<ProgressNoteResponse>> createProgressNote(
        @PathVariable UUID encounterId,
        @Valid @RequestBody ProgressNoteRequest request
    ) {
        log.info("POST /api/clinical/encounters/{}/progress-notes - Creating progress note", encounterId);

        ProgressNoteResponse response = progressNoteService.createProgressNote(encounterId, request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<ProgressNoteResponse>builder()
                .success(true)
                .message("Progress note berhasil dibuat: " + response.getNoteNumber())
                .data(response)
                .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/progress-notes
     * Get all progress notes for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/progress-notes")
    public ResponseEntity<ApiResponse<List<ProgressNoteResponse>>> getProgressNotesByEncounter(
        @PathVariable UUID encounterId,
        @RequestParam(required = false) NoteType noteType
    ) {
        log.info("GET /api/clinical/encounters/{}/progress-notes - Type: {}", encounterId, noteType);

        List<ProgressNoteResponse> notes;
        if (noteType != null) {
            notes = progressNoteService.getProgressNotesByType(encounterId, noteType);
        } else {
            notes = progressNoteService.getProgressNotesByEncounter(encounterId);
        }

        return ResponseEntity.ok(ApiResponse.<List<ProgressNoteResponse>>builder()
            .success(true)
            .message("Daftar progress notes berhasil diambil")
            .data(notes)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/progress-notes/latest
     * Get latest progress note for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/progress-notes/latest")
    public ResponseEntity<ApiResponse<ProgressNoteResponse>> getLatestProgressNote(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/progress-notes/latest", encounterId);

        ProgressNoteResponse response = progressNoteService.getLatestProgressNote(encounterId);

        return ResponseEntity.ok(ApiResponse.<ProgressNoteResponse>builder()
            .success(true)
            .message("Progress note terbaru berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/progress-notes/critical
     * Get notes with critical findings for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/progress-notes/critical")
    public ResponseEntity<ApiResponse<List<ProgressNoteResponse>>> getCriticalFindings(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/progress-notes/critical", encounterId);

        List<ProgressNoteResponse> notes = progressNoteService.getCriticalFindings(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<ProgressNoteResponse>>builder()
            .success(true)
            .message("Critical findings berhasil diambil")
            .data(notes)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/progress-notes/shift-handover
     * Get shift handover notes for a specific date.
     */
    @GetMapping("/encounters/{encounterId}/progress-notes/shift-handover")
    public ResponseEntity<ApiResponse<List<ProgressNoteResponse>>> getShiftHandoverNotes(
        @PathVariable UUID encounterId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("GET /api/clinical/encounters/{}/progress-notes/shift-handover - Date: {}", encounterId, date);

        List<ProgressNoteResponse> notes = progressNoteService.getShiftHandoverNotes(encounterId, date);

        return ResponseEntity.ok(ApiResponse.<List<ProgressNoteResponse>>builder()
            .success(true)
            .message("Shift handover notes berhasil diambil")
            .data(notes)
            .build());
    }

    /**
     * GET /api/clinical/progress-notes/requiring-cosign
     * Get all notes requiring cosign.
     */
    @GetMapping("/progress-notes/requiring-cosign")
    public ResponseEntity<ApiResponse<List<ProgressNoteResponse>>> getNotesRequiringCosign() {
        log.info("GET /api/clinical/progress-notes/requiring-cosign");

        List<ProgressNoteResponse> notes = progressNoteService.getNotesRequiringCosign();

        return ResponseEntity.ok(ApiResponse.<List<ProgressNoteResponse>>builder()
            .success(true)
            .message("Notes requiring cosign berhasil diambil")
            .data(notes)
            .build());
    }

    /**
     * GET /api/clinical/progress-notes/{id}
     * Get progress note by ID.
     */
    @GetMapping("/progress-notes/{id}")
    public ResponseEntity<ApiResponse<ProgressNoteResponse>> getProgressNoteById(
        @PathVariable UUID id
    ) {
        log.info("GET /api/clinical/progress-notes/{}", id);

        ProgressNoteResponse response = progressNoteService.getProgressNoteById(id);

        return ResponseEntity.ok(ApiResponse.<ProgressNoteResponse>builder()
            .success(true)
            .message("Progress note berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * PUT /api/clinical/progress-notes/{id}
     * Update a progress note.
     */
    @PutMapping("/progress-notes/{id}")
    public ResponseEntity<ApiResponse<ProgressNoteResponse>> updateProgressNote(
        @PathVariable UUID id,
        @Valid @RequestBody ProgressNoteRequest request
    ) {
        log.info("PUT /api/clinical/progress-notes/{} - Updating progress note", id);

        ProgressNoteResponse response = progressNoteService.updateProgressNote(id, request);

        return ResponseEntity.ok(ApiResponse.<ProgressNoteResponse>builder()
            .success(true)
            .message("Progress note berhasil diperbarui")
            .data(response)
            .build());
    }

    /**
     * POST /api/clinical/progress-notes/{id}/cosign
     * Cosign a progress note.
     */
    @PostMapping("/progress-notes/{id}/cosign")
    public ResponseEntity<ApiResponse<ProgressNoteResponse>> cosignProgressNote(
        @PathVariable UUID id,
        @Valid @RequestBody CosignRequest request
    ) {
        log.info("POST /api/clinical/progress-notes/{}/cosign - Cosigning by: {}", id, request.getCosignedByName());

        ProgressNoteResponse response = progressNoteService.cosignProgressNote(id, request);

        return ResponseEntity.ok(ApiResponse.<ProgressNoteResponse>builder()
            .success(true)
            .message("Progress note berhasil di-cosign oleh: " + request.getCosignedByName())
            .data(response)
            .build());
    }

    /**
     * DELETE /api/clinical/progress-notes/{id}
     * Delete a progress note.
     */
    @DeleteMapping("/progress-notes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProgressNote(
        @PathVariable UUID id
    ) {
        log.info("DELETE /api/clinical/progress-notes/{}", id);

        progressNoteService.deleteProgressNote(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true)
            .message("Progress note berhasil dihapus")
            .build());
    }
}
