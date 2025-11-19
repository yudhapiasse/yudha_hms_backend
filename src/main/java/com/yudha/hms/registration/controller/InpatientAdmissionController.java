package com.yudha.hms.registration.controller;

import com.yudha.hms.registration.dto.AdmissionRequest;
import com.yudha.hms.registration.dto.AdmissionResponse;
import com.yudha.hms.registration.dto.WristbandData;
import com.yudha.hms.registration.service.InpatientAdmissionService;
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
 * REST controller for inpatient admission management.
 *
 * Endpoints:
 * - POST /api/admissions - Create new admission
 * - GET /api/admissions/{id} - Get admission by ID
 * - GET /api/admissions/number/{admissionNumber} - Get admission by number
 * - GET /api/admissions/active - Get all active admissions
 * - GET /api/admissions/patient/{patientId} - Get patient admissions
 * - PUT /api/admissions/{id}/discharge - Discharge patient
 * - PUT /api/admissions/{id}/transfer - Transfer patient to different room
 * - GET /api/admissions/{id}/wristband - Generate wristband data
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/api/admissions")
@RequiredArgsConstructor
@Slf4j
public class InpatientAdmissionController {

    private final InpatientAdmissionService admissionService;

    /**
     * Create a new inpatient admission.
     *
     * POST /api/admissions
     *
     * @param request admission request
     * @return created admission
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AdmissionResponse>> createAdmission(
            @Valid @RequestBody AdmissionRequest request) {
        log.info("POST /api/admissions - Creating admission for patient: {}", request.getPatientId());

        AdmissionResponse response = admissionService.createAdmission(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Admission created successfully: " + response.getAdmissionNumber(),
                response
            ));
    }

    /**
     * Get admission by ID.
     *
     * GET /api/admissions/{id}
     *
     * @param id admission ID
     * @return admission details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdmissionResponse>> getAdmission(@PathVariable UUID id) {
        log.info("GET /api/admissions/{} - Fetching admission", id);

        AdmissionResponse response = admissionService.getAdmission(id);

        return ResponseEntity.ok(
            ApiResponse.success("Admission retrieved successfully", response)
        );
    }

    /**
     * Get admission by admission number.
     *
     * GET /api/admissions/number/{admissionNumber}
     *
     * @param admissionNumber admission number
     * @return admission details
     */
    @GetMapping("/number/{admissionNumber}")
    public ResponseEntity<ApiResponse<AdmissionResponse>> getAdmissionByNumber(
            @PathVariable String admissionNumber) {
        log.info("GET /api/admissions/number/{} - Fetching admission", admissionNumber);

        AdmissionResponse response = admissionService.getAdmissionByNumber(admissionNumber);

        return ResponseEntity.ok(
            ApiResponse.success("Admission retrieved successfully", response)
        );
    }

    /**
     * Get all active admissions.
     *
     * GET /api/admissions/active
     *
     * @return list of active admissions
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AdmissionResponse>>> getAllActiveAdmissions() {
        log.info("GET /api/admissions/active - Fetching all active admissions");

        List<AdmissionResponse> admissions = admissionService.getAllActiveAdmissions();

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Retrieved %d active admissions", admissions.size()),
                admissions
            )
        );
    }

    /**
     * Get all admissions for a patient.
     *
     * GET /api/admissions/patient/{patientId}
     *
     * @param patientId patient ID
     * @return list of patient admissions
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<AdmissionResponse>>> getPatientAdmissions(
            @PathVariable UUID patientId) {
        log.info("GET /api/admissions/patient/{} - Fetching patient admissions", patientId);

        List<AdmissionResponse> admissions = admissionService.getPatientAdmissions(patientId);

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Retrieved %d admissions for patient", admissions.size()),
                admissions
            )
        );
    }

    /**
     * Discharge a patient.
     *
     * PUT /api/admissions/{id}/discharge
     *
     * @param id admission ID
     * @param request discharge request
     * @return updated admission
     */
    @PutMapping("/{id}/discharge")
    public ResponseEntity<ApiResponse<AdmissionResponse>> dischargePatient(
            @PathVariable UUID id,
            @RequestBody DischargeRequest request) {
        log.info("PUT /api/admissions/{}/discharge - Discharging patient", id);

        AdmissionResponse response = admissionService.dischargePatient(
            id,
            request.getDischargeType(),
            request.getDischargeDisposition(),
            request.getDischargeSummary()
        );

        return ResponseEntity.ok(
            ApiResponse.success("Patient discharged successfully", response)
        );
    }

    /**
     * Transfer patient to different room.
     *
     * PUT /api/admissions/{id}/transfer
     *
     * @param id admission ID
     * @param request transfer request
     * @return updated admission
     */
    @PutMapping("/{id}/transfer")
    public ResponseEntity<ApiResponse<AdmissionResponse>> transferPatient(
            @PathVariable UUID id,
            @RequestBody TransferRequest request) {
        log.info("PUT /api/admissions/{}/transfer - Transferring patient to room: {}",
            id, request.getNewRoomId());

        AdmissionResponse response = admissionService.transferPatient(
            id,
            request.getNewRoomId(),
            request.getNewBedId(),
            request.getTransferReason()
        );

        return ResponseEntity.ok(
            ApiResponse.success("Patient transferred successfully", response)
        );
    }

    /**
     * Generate wristband data for a patient.
     *
     * GET /api/admissions/{id}/wristband
     *
     * @param id admission ID
     * @return wristband data with barcodes
     */
    @GetMapping("/{id}/wristband")
    public ResponseEntity<ApiResponse<WristbandData>> generateWristband(@PathVariable UUID id) {
        log.info("GET /api/admissions/{}/wristband - Generating wristband data", id);

        WristbandData wristband = admissionService.generateWristbandData(id);

        return ResponseEntity.ok(
            ApiResponse.success("Wristband data generated successfully", wristband)
        );
    }

    // ========== Inner DTOs for Requests ==========

    /**
     * DTO for discharge request.
     */
    @lombok.Data
    public static class DischargeRequest {
        private String dischargeType; // ROUTINE, AMA, TRANSFER, DECEASED
        private String dischargeDisposition; // HOME, HOME_HEALTH, REHAB, NURSING_HOME, DECEASED
        private String dischargeSummary;
    }

    /**
     * DTO for transfer request.
     */
    @lombok.Data
    public static class TransferRequest {
        private UUID newRoomId;
        private UUID newBedId; // Optional, will find available if null
        private String transferReason;
    }
}
