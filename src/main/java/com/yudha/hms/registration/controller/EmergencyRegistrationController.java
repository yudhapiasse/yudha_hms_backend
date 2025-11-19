package com.yudha.hms.registration.controller;

import com.yudha.hms.registration.dto.AdmissionRequest;
import com.yudha.hms.registration.dto.EmergencyRegistrationRequest;
import com.yudha.hms.registration.dto.EmergencyRegistrationResponse;
import com.yudha.hms.registration.entity.EmergencyStatus;
import com.yudha.hms.registration.entity.TriageLevel;
import com.yudha.hms.registration.service.EmergencyRegistrationService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Emergency Registration Management.
 *
 * Provides endpoints for:
 * - Fast-track emergency registration (known and unknown patients)
 * - Triage management
 * - Auto-conversion to inpatient admission
 * - ER discharge and disposition
 * - Critical patient monitoring
 * - Police case tracking
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
@Slf4j
public class EmergencyRegistrationController {

    private final EmergencyRegistrationService emergencyService;

    /**
     * Register new emergency patient (fast-track).
     * Supports both identified patients and unknown/unconscious patients.
     *
     * POST /api/emergency/register
     *
     * @param request emergency registration request
     * @return created emergency registration
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'REGISTRATION_STAFF')")
    public ResponseEntity<ApiResponse<EmergencyRegistrationResponse>> registerEmergency(
            @Valid @RequestBody EmergencyRegistrationRequest request) {
        log.info("Registering emergency patient - Unknown: {}, Police Case: {}",
            request.getIsUnknownPatient(), request.getIsPoliceCase());

        EmergencyRegistrationResponse response = emergencyService.registerEmergency(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Emergency patient registered successfully: " + response.getEmergencyNumber(),
                response
            ));
    }

    /**
     * Get emergency registration by ID.
     *
     * GET /api/emergency/{id}
     *
     * @param id emergency registration ID
     * @return emergency registration details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'REGISTRATION_STAFF')")
    public ResponseEntity<ApiResponse<EmergencyRegistrationResponse>> getEmergencyById(
            @PathVariable UUID id) {
        log.info("Fetching emergency registration: {}", id);

        EmergencyRegistrationResponse response = emergencyService.getEmergencyById(id);

        return ResponseEntity.ok(ApiResponse.success("Emergency registration retrieved", response));
    }

    /**
     * Get emergency registration by emergency number.
     *
     * GET /api/emergency/number/{emergencyNumber}
     *
     * @param emergencyNumber emergency number (e.g., ER-20250119-0001)
     * @return emergency registration details
     */
    @GetMapping("/number/{emergencyNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'REGISTRATION_STAFF')")
    public ResponseEntity<ApiResponse<EmergencyRegistrationResponse>> getEmergencyByNumber(
            @PathVariable String emergencyNumber) {
        log.info("Fetching emergency registration by number: {}", emergencyNumber);

        EmergencyRegistrationResponse response = emergencyService.getEmergencyByNumber(emergencyNumber);

        return ResponseEntity.ok(ApiResponse.success("Emergency registration retrieved", response));
    }

    /**
     * Get all active emergency registrations (in ER).
     * Returns sorted by priority: critical first, then by triage priority, then by arrival time.
     *
     * GET /api/emergency/active
     *
     * @return list of active emergency registrations
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getAllActive() {
        log.info("Fetching all active emergency registrations");

        List<EmergencyRegistrationResponse> responses = emergencyService.getAllActive();

        return ResponseEntity.ok(ApiResponse.success(String.format("Retrieved %d active emergency registrations", responses.size(), responses)
        ));
    }

    /**
     * Get all critical patients in ER.
     * Returns RED and BLACK triage levels, sorted by priority.
     *
     * GET /api/emergency/critical
     *
     * @return list of critical emergency registrations
     */
    @GetMapping("/critical")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getAllCritical() {
        log.info("Fetching all critical emergency registrations");

        List<EmergencyRegistrationResponse> responses = emergencyService.getAllCritical();

        return ResponseEntity.ok(ApiResponse.success(String.format("Retrieved %d critical patients", responses.size(), responses)
        ));
    }

    /**
     * Get emergency registrations by triage level.
     *
     * GET /api/emergency/triage/{level}
     *
     * @param level triage level (RED, YELLOW, GREEN, WHITE, BLACK)
     * @return list of emergency registrations with specified triage level
     */
    @GetMapping("/triage/{level}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getByTriageLevel(
            @PathVariable TriageLevel level) {
        log.info("Fetching emergency registrations by triage level: {}", level);

        List<EmergencyRegistrationResponse> responses = emergencyService.getByTriageLevel(level);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Retrieved %d %s triage patients", responses.size(), level),
            responses
        ));
    }

    /**
     * Get emergency registrations by status.
     *
     * GET /api/emergency/status/{status}
     *
     * @param status emergency status
     * @return list of emergency registrations with specified status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getByStatus(
            @PathVariable EmergencyStatus status) {
        log.info("Fetching emergency registrations by status: {}", status);

        List<EmergencyRegistrationResponse> responses = emergencyService.getByStatus(status);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Retrieved %d %s registrations", responses.size(), status),
            responses
        ));
    }

    /**
     * Get all unknown/unidentified patients.
     *
     * GET /api/emergency/unknown
     *
     * @return list of unknown patient registrations
     */
    @GetMapping("/unknown")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'SOCIAL_WORKER')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getAllUnknownPatients() {
        log.info("Fetching all unknown patients");

        List<EmergencyRegistrationResponse> responses = emergencyService.getAllUnknownPatients();

        return ResponseEntity.ok(ApiResponse.success(String.format("Retrieved %d unknown patients", responses.size(), responses)
        ));
    }

    /**
     * Get all police cases.
     *
     * GET /api/emergency/police-cases
     *
     * @return list of police case registrations
     */
    @GetMapping("/police-cases")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'SECURITY')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getAllPoliceCases() {
        log.info("Fetching all police cases");

        List<EmergencyRegistrationResponse> responses = emergencyService.getAllPoliceCases();

        return ResponseEntity.ok(ApiResponse.success(String.format("Retrieved %d police cases", responses.size(), responses)
        ));
    }

    /**
     * Get all trauma cases.
     *
     * GET /api/emergency/trauma-cases
     *
     * @return list of trauma case registrations
     */
    @GetMapping("/trauma-cases")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getAllTraumaCases() {
        log.info("Fetching all trauma cases");

        List<EmergencyRegistrationResponse> responses = emergencyService.getAllTraumaCases();

        return ResponseEntity.ok(ApiResponse.success(String.format("Retrieved %d trauma cases", responses.size(), responses)
        ));
    }

    /**
     * Get emergency registrations by ER zone.
     *
     * GET /api/emergency/zone/{zone}
     *
     * @param zone ER zone (RED_ZONE, YELLOW_ZONE, GREEN_ZONE, RESUS_ROOM, ISOLATION)
     * @return list of emergency registrations in specified zone
     */
    @GetMapping("/zone/{zone}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getByErZone(
            @PathVariable String zone) {
        log.info("Fetching emergency registrations in zone: {}", zone);

        List<EmergencyRegistrationResponse> responses = emergencyService.getByErZone(zone);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Retrieved %d patients in %s", responses.size(), zone),
            responses
        ));
    }

    /**
     * Get patients waiting for triage.
     *
     * GET /api/emergency/waiting-triage
     *
     * @return list of emergency registrations waiting for triage
     */
    @GetMapping("/waiting-triage")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getWaitingForTriage() {
        log.info("Fetching patients waiting for triage");

        List<EmergencyRegistrationResponse> responses = emergencyService.getWaitingForTriage();

        return ResponseEntity.ok(ApiResponse.success(String.format("%d patients waiting for triage", responses.size(), responses)
        ));
    }

    /**
     * Get emergency registrations by date range.
     *
     * GET /api/emergency/date-range?startDate={start}&endDate={end}
     *
     * @param startDate start date (yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate end date (yyyy-MM-dd'T'HH:mm:ss)
     * @return list of emergency registrations within date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'MEDICAL_RECORDS')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching emergency registrations from {} to {}", startDate, endDate);

        List<EmergencyRegistrationResponse> responses = emergencyService.getByDateRange(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(String.format("Retrieved %d registrations", responses.size(), responses)
        ));
    }

    /**
     * Get emergency registrations by patient ID.
     *
     * GET /api/emergency/patient/{patientId}
     *
     * @param patientId patient ID
     * @return list of emergency registrations for patient
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'REGISTRATION_STAFF')")
    public ResponseEntity<ApiResponse<List<EmergencyRegistrationResponse>>> getByPatientId(
            @PathVariable UUID patientId) {
        log.info("Fetching emergency registrations for patient: {}", patientId);

        List<EmergencyRegistrationResponse> responses = emergencyService.getByPatientId(patientId);

        return ResponseEntity.ok(ApiResponse.success(String.format("Retrieved %d emergency visits for patient", responses.size(), responses)
        ));
    }

    /**
     * Convert emergency registration to inpatient admission.
     * Auto-creates inpatient admission and links to emergency registration.
     *
     * POST /api/emergency/{id}/admit
     *
     * @param id emergency registration ID
     * @param admissionRequest inpatient admission request
     * @return updated emergency registration with inpatient link
     */
    @PostMapping("/{id}/admit")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_DOCTOR', 'ATTENDING_DOCTOR')")
    public ResponseEntity<ApiResponse<EmergencyRegistrationResponse>> convertToInpatient(
            @PathVariable UUID id,
            @Valid @RequestBody AdmissionRequest admissionRequest) {
        log.info("Converting emergency registration {} to inpatient", id);

        EmergencyRegistrationResponse response = emergencyService.convertToInpatient(id, admissionRequest);

        return ResponseEntity.ok(ApiResponse.success(String.format("Patient admitted to inpatient: %s", response.getInpatientAdmissionNumber(), response)
        ));
    }

    /**
     * Discharge patient from emergency room.
     *
     * PUT /api/emergency/{id}/discharge
     *
     * @param id emergency registration ID
     * @param disposition discharge disposition
     * @param dischargeNotes discharge notes
     * @return updated emergency registration
     */
    @PutMapping("/{id}/discharge")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<EmergencyRegistrationResponse>> dischargeFromEr(
            @PathVariable UUID id,
            @RequestParam String disposition,
            @RequestParam(required = false) String dischargeNotes) {
        log.info("Discharging patient from ER: {}, disposition: {}", id, disposition);

        EmergencyRegistrationResponse response = emergencyService.dischargeFromEr(id, disposition, dischargeNotes);

        return ResponseEntity.ok(ApiResponse.success(
            "Patient discharged from emergency room",
            response
        ));
    }

    /**
     * Link unknown patient to identified patient record.
     * Used when unknown patient is later identified.
     *
     * PUT /api/emergency/{id}/link-patient
     *
     * @param id emergency registration ID
     * @param patientId identified patient ID
     * @return updated emergency registration
     */
    @PutMapping("/{id}/link-patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'SOCIAL_WORKER')")
    public ResponseEntity<ApiResponse<EmergencyRegistrationResponse>> linkToPatient(
            @PathVariable UUID id,
            @RequestParam UUID patientId) {
        log.info("Linking unknown patient {} to patient {}", id, patientId);

        EmergencyRegistrationResponse response = emergencyService.linkToPatient(id, patientId);

        return ResponseEntity.ok(ApiResponse.success(
            "Unknown patient linked to patient record",
            response
        ));
    }

    /**
     * Update ER location/bed assignment.
     *
     * PUT /api/emergency/{id}/location
     *
     * @param id emergency registration ID
     * @param erZone ER zone
     * @param bedNumber bed number
     * @return updated emergency registration
     */
    @PutMapping("/{id}/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<EmergencyRegistrationResponse>> updateErLocation(
            @PathVariable UUID id,
            @RequestParam(required = false) String erZone,
            @RequestParam(required = false) String bedNumber) {
        log.info("Updating ER location for {}: zone={}, bed={}", id, erZone, bedNumber);

        EmergencyRegistrationResponse response = emergencyService.updateErLocation(id, erZone, bedNumber);

        return ResponseEntity.ok(ApiResponse.success(
            "ER location updated",
            response
        ));
    }

    /**
     * Update emergency registration details.
     *
     * PUT /api/emergency/{id}
     *
     * @param id emergency registration ID
     * @param request updated registration request
     * @return updated emergency registration
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<EmergencyRegistrationResponse>> updateEmergency(
            @PathVariable UUID id,
            @Valid @RequestBody EmergencyRegistrationRequest request) {
        log.info("Updating emergency registration: {}", id);

        EmergencyRegistrationResponse response = emergencyService.updateEmergency(id, request);

        return ResponseEntity.ok(ApiResponse.success(
            "Emergency registration updated",
            response
        ));
    }

    /**
     * Delete emergency registration (soft delete).
     *
     * DELETE /api/emergency/{id}
     *
     * @param id emergency registration ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmergency(@PathVariable UUID id) {
        log.info("Deleting emergency registration: {}", id);

        emergencyService.deleteEmergency(id);

        return ResponseEntity.ok(ApiResponse.success("Emergency registration deleted"));
    }

    /**
     * Get ER statistics.
     *
     * GET /api/emergency/stats
     *
     * @return ER statistics summary
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'MANAGEMENT')")
    public ResponseEntity<ApiResponse<Object>> getErStatistics() {
        log.info("Fetching ER statistics");

        // This would return a statistics DTO with counts by triage level, average wait times, etc.
        // For now, returning a simple response
        return ResponseEntity.ok(ApiResponse.success("ER statistics endpoint - to be implemented with detailed stats DTO"));
    }
}
