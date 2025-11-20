package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.LocationHistoryRequest;
import com.yudha.hms.clinical.dto.LocationHistoryResponse;
import com.yudha.hms.clinical.service.EncounterLocationHistoryService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Encounter Location History Controller.
 * REST API endpoints for location and bed tracking.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical")
@RequiredArgsConstructor
@Slf4j
public class EncounterLocationHistoryController {

    private final EncounterLocationHistoryService locationHistoryService;

    /**
     * POST /api/clinical/encounters/{encounterId}/location-history
     * Record location change.
     */
    @PostMapping("/encounters/{encounterId}/location-history")
    public ResponseEntity<ApiResponse<LocationHistoryResponse>> recordLocationChange(
        @PathVariable UUID encounterId,
        @Valid @RequestBody LocationHistoryRequest request
    ) {
        log.info("POST /api/clinical/encounters/{}/location-history - New location: {}",
            encounterId, request.getLocationName());

        LocationHistoryResponse response = locationHistoryService.recordLocationChange(encounterId, request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<LocationHistoryResponse>builder()
                .success(true)
                .message("Location change berhasil dicatat: " + response.getFullLocationDescription())
                .data(response)
                .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/location-history
     * Get location history for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/location-history")
    public ResponseEntity<ApiResponse<List<LocationHistoryResponse>>> getLocationHistory(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/location-history", encounterId);

        List<LocationHistoryResponse> history = locationHistoryService.getLocationHistory(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<LocationHistoryResponse>>builder()
            .success(true)
            .message("Location history berhasil diambil")
            .data(history)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/current-location
     * Get current location for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/current-location")
    public ResponseEntity<ApiResponse<LocationHistoryResponse>> getCurrentLocation(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/current-location", encounterId);

        LocationHistoryResponse response = locationHistoryService.getCurrentLocation(encounterId);

        return ResponseEntity.ok(ApiResponse.<LocationHistoryResponse>builder()
            .success(true)
            .message("Current location berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/icu-stays
     * Get ICU stays for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/icu-stays")
    public ResponseEntity<ApiResponse<List<LocationHistoryResponse>>> getIcuStays(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/icu-stays", encounterId);

        List<LocationHistoryResponse> stays = locationHistoryService.getIcuStays(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<LocationHistoryResponse>>builder()
            .success(true)
            .message("ICU stays berhasil diambil")
            .data(stays)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/icu-hours
     * Get total ICU hours for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/icu-hours")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getTotalIcuHours(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/icu-hours", encounterId);

        Integer totalHours = locationHistoryService.calculateTotalIcuHours(encounterId);

        Map<String, Integer> result = new HashMap<>();
        result.put("totalIcuHours", totalHours);
        result.put("totalIcuDays", totalHours / 24);

        return ResponseEntity.ok(ApiResponse.<Map<String, Integer>>builder()
            .success(true)
            .message("Total ICU hours berhasil dihitung")
            .data(result)
            .build());
    }

    /**
     * GET /api/clinical/icu/current-patients
     * Get all current ICU patients.
     */
    @GetMapping("/icu/current-patients")
    public ResponseEntity<ApiResponse<List<LocationHistoryResponse>>> getCurrentIcuPatients() {
        log.info("GET /api/clinical/icu/current-patients");

        List<LocationHistoryResponse> patients = locationHistoryService.getCurrentIcuPatients();

        return ResponseEntity.ok(ApiResponse.<List<LocationHistoryResponse>>builder()
            .success(true)
            .message("Current ICU patients berhasil diambil")
            .data(patients)
            .build());
    }

    /**
     * GET /api/clinical/isolation/current-patients
     * Get all patients in isolation.
     */
    @GetMapping("/isolation/current-patients")
    public ResponseEntity<ApiResponse<List<LocationHistoryResponse>>> getPatientsInIsolation() {
        log.info("GET /api/clinical/isolation/current-patients");

        List<LocationHistoryResponse> patients = locationHistoryService.getPatientsInIsolation();

        return ResponseEntity.ok(ApiResponse.<List<LocationHistoryResponse>>builder()
            .success(true)
            .message("Patients in isolation berhasil diambil")
            .data(patients)
            .build());
    }

    /**
     * GET /api/clinical/departments/{departmentId}/current-patients
     * Get current patients in a department.
     */
    @GetMapping("/departments/{departmentId}/current-patients")
    public ResponseEntity<ApiResponse<List<LocationHistoryResponse>>> getCurrentPatientsInDepartment(
        @PathVariable UUID departmentId
    ) {
        log.info("GET /api/clinical/departments/{}/current-patients", departmentId);

        List<LocationHistoryResponse> patients = locationHistoryService
            .getCurrentPatientsInDepartment(departmentId);

        return ResponseEntity.ok(ApiResponse.<List<LocationHistoryResponse>>builder()
            .success(true)
            .message("Current patients in department berhasil diambil")
            .data(patients)
            .build());
    }

    /**
     * GET /api/clinical/departments/{departmentId}/census
     * Get department census (patient count).
     */
    @GetMapping("/departments/{departmentId}/census")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDepartmentCensus(
        @PathVariable UUID departmentId
    ) {
        log.info("GET /api/clinical/departments/{}/census", departmentId);

        long census = locationHistoryService.getDepartmentCensus(departmentId);

        Map<String, Long> result = new HashMap<>();
        result.put("patientCount", census);

        return ResponseEntity.ok(ApiResponse.<Map<String, Long>>builder()
            .success(true)
            .message("Department census berhasil dihitung")
            .data(result)
            .build());
    }

    /**
     * GET /api/clinical/beds/{bedId}/current-occupant
     * Get current bed occupant.
     */
    @GetMapping("/beds/{bedId}/current-occupant")
    public ResponseEntity<ApiResponse<LocationHistoryResponse>> getCurrentBedOccupant(
        @PathVariable UUID bedId
    ) {
        log.info("GET /api/clinical/beds/{}/current-occupant", bedId);

        LocationHistoryResponse response = locationHistoryService.getCurrentBedOccupant(bedId);

        return ResponseEntity.ok(ApiResponse.<LocationHistoryResponse>builder()
            .success(true)
            .message("Current bed occupant berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/admission-event
     * Get admission event for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/admission-event")
    public ResponseEntity<ApiResponse<LocationHistoryResponse>> getAdmissionEvent(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/admission-event", encounterId);

        LocationHistoryResponse response = locationHistoryService.getAdmissionEvent(encounterId);

        return ResponseEntity.ok(ApiResponse.<LocationHistoryResponse>builder()
            .success(true)
            .message("Admission event berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/discharge-event
     * Get discharge event for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/discharge-event")
    public ResponseEntity<ApiResponse<LocationHistoryResponse>> getDischargeEvent(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/discharge-event", encounterId);

        LocationHistoryResponse response = locationHistoryService.getDischargeEvent(encounterId);

        return ResponseEntity.ok(ApiResponse.<LocationHistoryResponse>builder()
            .success(true)
            .message("Discharge event berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * PUT /api/clinical/location-history/{id}
     * Update location history.
     */
    @PutMapping("/location-history/{id}")
    public ResponseEntity<ApiResponse<LocationHistoryResponse>> updateLocationHistory(
        @PathVariable UUID id,
        @Valid @RequestBody LocationHistoryRequest request
    ) {
        log.info("PUT /api/clinical/location-history/{} - Updating", id);

        LocationHistoryResponse response = locationHistoryService.updateLocationHistory(id, request);

        return ResponseEntity.ok(ApiResponse.<LocationHistoryResponse>builder()
            .success(true)
            .message("Location history berhasil diperbarui")
            .data(response)
            .build());
    }

    /**
     * PATCH /api/clinical/location-history/{id}/end
     * End location stay.
     */
    @PatchMapping("/location-history/{id}/end")
    public ResponseEntity<ApiResponse<LocationHistoryResponse>> endLocationStay(
        @PathVariable UUID id
    ) {
        log.info("PATCH /api/clinical/location-history/{}/end", id);

        LocationHistoryResponse response = locationHistoryService.endLocationStay(id);

        return ResponseEntity.ok(ApiResponse.<LocationHistoryResponse>builder()
            .success(true)
            .message("Location stay berhasil diakhiri")
            .data(response)
            .build());
    }
}
