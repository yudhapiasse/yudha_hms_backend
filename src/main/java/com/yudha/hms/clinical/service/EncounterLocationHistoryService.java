package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.LocationHistoryRequest;
import com.yudha.hms.clinical.dto.LocationHistoryResponse;
import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.EncounterLocationHistory;
import com.yudha.hms.clinical.repository.EncounterLocationHistoryRepository;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for Encounter Location History management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EncounterLocationHistoryService {

    private final EncounterLocationHistoryRepository locationHistoryRepository;
    private final EncounterRepository encounterRepository;

    /**
     * Record location change.
     */
    public LocationHistoryResponse recordLocationChange(UUID encounterId, LocationHistoryRequest request) {
        log.info("Recording location change for encounter: {} to {}", encounterId, request.getLocationName());

        // Validate encounter exists
        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter tidak ditemukan dengan ID: " + encounterId));

        // End current location stay
        endCurrentLocationStay(encounterId);

        // Build location history entity
        EncounterLocationHistory locationHistory = buildLocationHistoryFromRequest(encounter, request);

        // Set as current
        locationHistory.setAsCurrent();

        // Save location history
        locationHistory = locationHistoryRepository.save(locationHistory);
        log.info("Location change recorded for encounter: {} - New location: {}",
            encounterId, locationHistory.getLocationName());

        // Update encounter's current location
        updateEncounterLocation(encounter, locationHistory);

        return mapToResponse(locationHistory);
    }

    /**
     * Get location history for an encounter.
     */
    public List<LocationHistoryResponse> getLocationHistory(UUID encounterId) {
        log.info("Retrieving location history for encounter: {}", encounterId);

        List<EncounterLocationHistory> history = locationHistoryRepository.findByEncounterId(encounterId);
        return history.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get current location for an encounter.
     */
    public LocationHistoryResponse getCurrentLocation(UUID encounterId) {
        log.info("Retrieving current location for encounter: {}", encounterId);

        EncounterLocationHistory locationHistory = locationHistoryRepository.findCurrentByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Belum ada location history untuk encounter ini"));

        return mapToResponse(locationHistory);
    }

    /**
     * Get ICU stays for an encounter.
     */
    public List<LocationHistoryResponse> getIcuStays(UUID encounterId) {
        log.info("Retrieving ICU stays for encounter: {}", encounterId);

        List<EncounterLocationHistory> history = locationHistoryRepository.findIcuStaysByEncounterId(encounterId);
        return history.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get current ICU patients.
     */
    public List<LocationHistoryResponse> getCurrentIcuPatients() {
        log.info("Retrieving current ICU patients");

        List<EncounterLocationHistory> patients = locationHistoryRepository.findCurrentIcuPatients();
        return patients.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get patients in isolation.
     */
    public List<LocationHistoryResponse> getPatientsInIsolation() {
        log.info("Retrieving patients in isolation");

        List<EncounterLocationHistory> patients = locationHistoryRepository.findPatientsInIsolation();
        return patients.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get current patients in a department.
     */
    public List<LocationHistoryResponse> getCurrentPatientsInDepartment(UUID departmentId) {
        log.info("Retrieving current patients in department: {}", departmentId);

        List<EncounterLocationHistory> patients = locationHistoryRepository.findCurrentByDepartmentId(departmentId);
        return patients.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get department census (count).
     */
    public long getDepartmentCensus(UUID departmentId) {
        log.info("Calculating department census for: {}", departmentId);

        return locationHistoryRepository.countCurrentPatientsByDepartmentId(departmentId);
    }

    /**
     * Get current bed occupant.
     */
    public LocationHistoryResponse getCurrentBedOccupant(UUID bedId) {
        log.info("Retrieving current bed occupant for bed: {}", bedId);

        EncounterLocationHistory locationHistory = locationHistoryRepository.findCurrentByBedId(bedId)
            .orElseThrow(() -> new ResourceNotFoundException("Bed saat ini tidak terisi"));

        return mapToResponse(locationHistory);
    }

    /**
     * Calculate total ICU hours for an encounter.
     */
    public Integer calculateTotalIcuHours(UUID encounterId) {
        log.info("Calculating total ICU hours for encounter: {}", encounterId);

        Integer totalHours = locationHistoryRepository.calculateTotalIcuHours(encounterId);
        return totalHours != null ? totalHours : 0;
    }

    /**
     * Get admission event for an encounter.
     */
    public LocationHistoryResponse getAdmissionEvent(UUID encounterId) {
        log.info("Retrieving admission event for encounter: {}", encounterId);

        EncounterLocationHistory locationHistory = locationHistoryRepository.findAdmissionByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Admission event tidak ditemukan untuk encounter ini"));

        return mapToResponse(locationHistory);
    }

    /**
     * Get discharge event for an encounter.
     */
    public LocationHistoryResponse getDischargeEvent(UUID encounterId) {
        log.info("Retrieving discharge event for encounter: {}", encounterId);

        EncounterLocationHistory locationHistory = locationHistoryRepository.findDischargeByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Discharge event tidak ditemukan untuk encounter ini"));

        return mapToResponse(locationHistory);
    }

    /**
     * Update location history.
     */
    public LocationHistoryResponse updateLocationHistory(UUID id, LocationHistoryRequest request) {
        log.info("Updating location history: {}", id);

        EncounterLocationHistory locationHistory = locationHistoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Location history tidak ditemukan dengan ID: " + id));

        // Validate if not current (cannot update historical records)
        if (!Boolean.TRUE.equals(locationHistory.getIsCurrent())) {
            throw new BusinessException("Hanya location history yang sedang aktif yang dapat diubah");
        }

        // Update fields
        updateLocationHistoryFromRequest(locationHistory, request);

        locationHistory = locationHistoryRepository.save(locationHistory);
        log.info("Location history updated: {}", id);

        return mapToResponse(locationHistory);
    }

    /**
     * End location stay manually.
     */
    public LocationHistoryResponse endLocationStay(UUID id) {
        log.info("Ending location stay: {}", id);

        EncounterLocationHistory locationHistory = locationHistoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Location history tidak ditemukan dengan ID: " + id));

        // Validate if still current
        if (!Boolean.TRUE.equals(locationHistory.getIsCurrent())) {
            throw new BusinessException("Location stay sudah berakhir sebelumnya");
        }

        // End stay
        locationHistory.endStay();

        locationHistory = locationHistoryRepository.save(locationHistory);
        log.info("Location stay ended: {} - Duration: {} hours", id, locationHistory.getDurationHours());

        return mapToResponse(locationHistory);
    }

    // ========== Helper Methods ==========

    /**
     * End current location stay for encounter.
     */
    private void endCurrentLocationStay(UUID encounterId) {
        locationHistoryRepository.findCurrentByEncounterId(encounterId)
            .ifPresent(currentLocation -> {
                currentLocation.endStay();
                locationHistoryRepository.save(currentLocation);
                log.info("Previous location stay ended for encounter: {}", encounterId);
            });
    }

    /**
     * Update encounter's current location fields.
     */
    private void updateEncounterLocation(Encounter encounter, EncounterLocationHistory locationHistory) {
        encounter.setLocationId(locationHistory.getLocationId());
        encounter.setCurrentLocation(locationHistory.getLocationName());
        encounter.setCurrentDepartment(locationHistory.getDepartmentName());

        encounterRepository.save(encounter);
        log.info("Encounter location updated: {}", encounter.getId());
    }

    /**
     * Build EncounterLocationHistory entity from request.
     */
    private EncounterLocationHistory buildLocationHistoryFromRequest(
        Encounter encounter,
        LocationHistoryRequest request
    ) {
        return EncounterLocationHistory.builder()
            .encounter(encounter)
            .patientId(encounter.getPatientId())
            .locationId(request.getLocationId())
            .locationName(request.getLocationName())
            .locationType(request.getLocationType())
            .departmentId(request.getDepartmentId())
            .departmentName(request.getDepartmentName())
            .roomId(request.getRoomId())
            .roomNumber(request.getRoomNumber())
            .roomType(request.getRoomType())
            .bedId(request.getBedId())
            .bedNumber(request.getBedNumber())
            .startTime(request.getStartTime() != null ? request.getStartTime() : LocalDateTime.now())
            .locationEventType(request.getLocationEventType())
            .changeReason(request.getChangeReason())
            .changeNotes(request.getChangeNotes())
            .changedById(request.getChangedById())
            .changedByName(request.getChangedByName())
            .authorizedById(request.getAuthorizedById())
            .authorizedByName(request.getAuthorizedByName())
            .bedAssignmentId(request.getBedAssignmentId())
            .isIcu(request.getIsIcu())
            .isolationRequired(request.getIsolationRequired())
            .isolationType(request.getIsolationType())
            .build();
    }

    /**
     * Update EncounterLocationHistory from request.
     */
    private void updateLocationHistoryFromRequest(
        EncounterLocationHistory locationHistory,
        LocationHistoryRequest request
    ) {
        if (request.getLocationId() != null) locationHistory.setLocationId(request.getLocationId());
        if (request.getLocationName() != null) locationHistory.setLocationName(request.getLocationName());
        if (request.getLocationType() != null) locationHistory.setLocationType(request.getLocationType());

        if (request.getDepartmentId() != null) locationHistory.setDepartmentId(request.getDepartmentId());
        if (request.getDepartmentName() != null) locationHistory.setDepartmentName(request.getDepartmentName());

        if (request.getRoomId() != null) locationHistory.setRoomId(request.getRoomId());
        if (request.getRoomNumber() != null) locationHistory.setRoomNumber(request.getRoomNumber());
        if (request.getRoomType() != null) locationHistory.setRoomType(request.getRoomType());

        if (request.getBedId() != null) locationHistory.setBedId(request.getBedId());
        if (request.getBedNumber() != null) locationHistory.setBedNumber(request.getBedNumber());

        if (request.getChangeReason() != null) locationHistory.setChangeReason(request.getChangeReason());
        if (request.getChangeNotes() != null) locationHistory.setChangeNotes(request.getChangeNotes());

        if (request.getIsIcu() != null) locationHistory.setIsIcu(request.getIsIcu());
        if (request.getIsolationRequired() != null) locationHistory.setIsolationRequired(request.getIsolationRequired());
        if (request.getIsolationType() != null) locationHistory.setIsolationType(request.getIsolationType());
    }

    /**
     * Map entity to response DTO.
     */
    private LocationHistoryResponse mapToResponse(EncounterLocationHistory locationHistory) {
        return LocationHistoryResponse.builder()
            .id(locationHistory.getId())
            .encounterId(locationHistory.getEncounter().getId())
            .patientId(locationHistory.getPatientId())
            .locationId(locationHistory.getLocationId())
            .locationName(locationHistory.getLocationName())
            .locationType(locationHistory.getLocationType())
            .departmentId(locationHistory.getDepartmentId())
            .departmentName(locationHistory.getDepartmentName())
            .roomId(locationHistory.getRoomId())
            .roomNumber(locationHistory.getRoomNumber())
            .roomType(locationHistory.getRoomType())
            .bedId(locationHistory.getBedId())
            .bedNumber(locationHistory.getBedNumber())
            .startTime(locationHistory.getStartTime())
            .endTime(locationHistory.getEndTime())
            .durationHours(locationHistory.getDurationHours())
            .durationDays(locationHistory.getDurationDays())
            .locationEventType(locationHistory.getLocationEventType())
            .locationEventTypeDisplay(locationHistory.getLocationEventType() != null ?
                locationHistory.getLocationEventType().getIndonesianName() : null)
            .changeReason(locationHistory.getChangeReason())
            .changeNotes(locationHistory.getChangeNotes())
            .changedById(locationHistory.getChangedById())
            .changedByName(locationHistory.getChangedByName())
            .authorizedById(locationHistory.getAuthorizedById())
            .authorizedByName(locationHistory.getAuthorizedByName())
            .bedAssignmentId(locationHistory.getBedAssignmentId())
            .isCurrent(locationHistory.getIsCurrent())
            .isIcu(locationHistory.getIsIcu())
            .isolationRequired(locationHistory.getIsolationRequired())
            .isolationType(locationHistory.getIsolationType())
            .createdAt(locationHistory.getCreatedAt())
            .updatedAt(locationHistory.getUpdatedAt())
            .fullLocationDescription(locationHistory.getFullLocationDescription())
            .isAdmission(locationHistory.isAdmission())
            .isDischarge(locationHistory.isDischarge())
            .isIcuEvent(locationHistory.isIcuEvent())
            .isInIcu(locationHistory.isInIcu())
            .isInIsolation(locationHistory.isInIsolation())
            .build();
    }
}
