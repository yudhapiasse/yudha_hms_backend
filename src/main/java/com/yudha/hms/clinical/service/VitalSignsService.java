package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.VitalSignsChartDto;
import com.yudha.hms.clinical.dto.VitalSignsRequest;
import com.yudha.hms.clinical.dto.VitalSignsResponse;
import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.Shift;
import com.yudha.hms.clinical.entity.VitalSigns;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.clinical.repository.VitalSignsRepository;
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
 * Service for Vital Signs management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VitalSignsService {

    private final VitalSignsRepository vitalSignsRepository;
    private final EncounterRepository encounterRepository;

    /**
     * Record vital signs.
     */
    public VitalSignsResponse recordVitalSigns(UUID encounterId, VitalSignsRequest request) {
        log.info("Recording vital signs for encounter: {}", encounterId);

        // Validate encounter exists
        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter tidak ditemukan dengan ID: " + encounterId));

        // Build vital signs entity
        VitalSigns vitalSigns = buildVitalSignsFromRequest(encounter, request);

        // Auto-calculations
        vitalSigns.calculateBmi();
        vitalSigns.calculateMap();
        vitalSigns.calculateGcsTotal();
        vitalSigns.calculateFluidBalance();

        // Check if within normal limits
        boolean isNormal = vitalSigns.isWithinNormalLimits();

        // Check if requires urgent notification
        if (vitalSigns.requiresUrgentNotification()) {
            vitalSigns.setRequiresNotification(true);
            log.warn("CRITICAL VITALS DETECTED for encounter: {} - Requires urgent notification", encounterId);
        }

        // Set shift if not provided
        if (vitalSigns.getShift() == null) {
            vitalSigns.setShift(Shift.getCurrentShift());
        }

        // Save vital signs
        vitalSigns = vitalSignsRepository.save(vitalSigns);
        log.info("Vital signs recorded for encounter: {} - Abnormal: {}, Requires notification: {}",
            encounterId, vitalSigns.getIsAbnormal(), vitalSigns.getRequiresNotification());

        // If critical, send notification (future implementation)
        if (Boolean.TRUE.equals(vitalSigns.getRequiresNotification())) {
            // TODO: Send notification to attending physician
            log.info("Notification should be sent for critical vitals");
        }

        return mapToResponse(vitalSigns);
    }

    /**
     * Get vital signs by ID.
     */
    public VitalSignsResponse getVitalSignsById(UUID id) {
        log.info("Retrieving vital signs: {}", id);

        VitalSigns vitalSigns = vitalSignsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vital signs tidak ditemukan dengan ID: " + id));

        return mapToResponse(vitalSigns);
    }

    /**
     * Get all vital signs for an encounter.
     */
    public List<VitalSignsResponse> getVitalSignsByEncounter(UUID encounterId) {
        log.info("Retrieving vital signs for encounter: {}", encounterId);

        List<VitalSigns> vitalSignsList = vitalSignsRepository.findByEncounterId(encounterId);
        return vitalSignsList.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get latest vital signs for an encounter.
     */
    public VitalSignsResponse getLatestVitalSigns(UUID encounterId) {
        log.info("Retrieving latest vital signs for encounter: {}", encounterId);

        VitalSigns vitalSigns = vitalSignsRepository.findLatestByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Belum ada vital signs untuk encounter ini"));

        return mapToResponse(vitalSigns);
    }

    /**
     * Get vital signs for charting (last 24 hours).
     */
    public List<VitalSignsChartDto> getVitalSignsForCharting(UUID encounterId) {
        log.info("Retrieving vital signs for charting - encounter: {}", encounterId);

        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<VitalSigns> vitalSignsList = vitalSignsRepository.findForCharting(encounterId, since);

        return vitalSignsList.stream()
            .map(this::mapToChartDto)
            .collect(Collectors.toList());
    }

    /**
     * Get vital signs by date range.
     */
    public List<VitalSignsResponse> getVitalSignsByDateRange(
        UUID encounterId,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        log.info("Retrieving vital signs for encounter: {} from {} to {}", encounterId, startDate, endDate);

        List<VitalSigns> vitalSignsList = vitalSignsRepository.findByEncounterIdAndDateRange(
            encounterId, startDate, endDate
        );

        return vitalSignsList.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get abnormal vital signs for an encounter.
     */
    public List<VitalSignsResponse> getAbnormalVitalSigns(UUID encounterId) {
        log.info("Retrieving abnormal vital signs for encounter: {}", encounterId);

        List<VitalSigns> vitalSignsList = vitalSignsRepository.findAbnormalByEncounterId(encounterId);
        return vitalSignsList.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get vital signs requiring notification.
     */
    public List<VitalSignsResponse> getVitalSignsRequiringNotification(UUID encounterId) {
        log.info("Retrieving vital signs requiring notification for encounter: {}", encounterId);

        List<VitalSigns> vitalSignsList = vitalSignsRepository.findRequiringNotificationByEncounterId(encounterId);
        return vitalSignsList.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Mark notification as sent.
     */
    public VitalSignsResponse markNotificationSent(UUID id, UUID notifiedProviderId) {
        log.info("Marking notification as sent for vital signs: {}", id);

        VitalSigns vitalSigns = vitalSignsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vital signs tidak ditemukan dengan ID: " + id));

        vitalSigns.setNotificationSent(true);
        vitalSigns.setNotifiedProviderId(notifiedProviderId);

        vitalSigns = vitalSignsRepository.save(vitalSigns);
        log.info("Notification marked as sent for vital signs: {}", id);

        return mapToResponse(vitalSigns);
    }

    /**
     * Update vital signs.
     */
    public VitalSignsResponse updateVitalSigns(UUID id, VitalSignsRequest request) {
        log.info("Updating vital signs: {}", id);

        VitalSigns vitalSigns = vitalSignsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vital signs tidak ditemukan dengan ID: " + id));

        // Update fields
        updateVitalSignsFromRequest(vitalSigns, request);

        // Recalculate
        vitalSigns.calculateBmi();
        vitalSigns.calculateMap();
        vitalSigns.calculateGcsTotal();
        vitalSigns.calculateFluidBalance();

        // Recheck abnormal status
        vitalSigns.isWithinNormalLimits();

        // Recheck urgent notification
        if (vitalSigns.requiresUrgentNotification()) {
            vitalSigns.setRequiresNotification(true);
        }

        vitalSigns = vitalSignsRepository.save(vitalSigns);
        log.info("Vital signs updated: {}", id);

        return mapToResponse(vitalSigns);
    }

    /**
     * Delete vital signs.
     */
    public void deleteVitalSigns(UUID id) {
        log.info("Deleting vital signs: {}", id);

        VitalSigns vitalSigns = vitalSignsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vital signs tidak ditemukan dengan ID: " + id));

        vitalSignsRepository.delete(vitalSigns);
        log.info("Vital signs deleted: {}", id);
    }

    /**
     * Get critical GCS scores (GCS < 9).
     */
    public List<VitalSignsResponse> getCriticalGcsScores() {
        log.info("Retrieving critical GCS scores");

        List<VitalSigns> vitalSignsList = vitalSignsRepository.findCriticalGcsScores();
        return vitalSignsList.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ========== Helper Methods ==========

    /**
     * Build VitalSigns entity from request.
     */
    private VitalSigns buildVitalSignsFromRequest(Encounter encounter, VitalSignsRequest request) {
        return VitalSigns.builder()
            .encounter(encounter)
            .patientId(encounter.getPatientId())
            .measurementTime(request.getMeasurementTime() != null ? request.getMeasurementTime() : LocalDateTime.now())
            .shift(request.getShift())
            .measurementType(request.getMeasurementType())
            .systolicBp(request.getSystolicBp())
            .diastolicBp(request.getDiastolicBp())
            .heartRate(request.getHeartRate())
            .respiratoryRate(request.getRespiratoryRate())
            .temperature(request.getTemperature())
            .temperatureRoute(request.getTemperatureRoute())
            .spo2(request.getSpo2())
            .oxygenTherapy(request.getOxygenTherapy())
            .oxygenFlowRate(request.getOxygenFlowRate())
            .oxygenDeliveryMethod(request.getOxygenDeliveryMethod())
            .weight(request.getWeight())
            .height(request.getHeight())
            .headCircumference(request.getHeadCircumference())
            .gcsEye(request.getGcsEye())
            .gcsVerbal(request.getGcsVerbal())
            .gcsMotor(request.getGcsMotor())
            .painScore(request.getPainScore())
            .painLocation(request.getPainLocation())
            .painQuality(request.getPainQuality())
            .fluidIntakeMl(request.getFluidIntakeMl())
            .fluidOutputMl(request.getFluidOutputMl())
            .urineOutputMl(request.getUrineOutputMl())
            .bloodGlucose(request.getBloodGlucose())
            .bloodGlucoseUnit(request.getBloodGlucoseUnit())
            .peripheralPulse(request.getPeripheralPulse())
            .capillaryRefillTime(request.getCapillaryRefillTime())
            .pupilReaction(request.getPupilReaction())
            .notes(request.getNotes())
            .alerts(request.getAlerts())
            .recordedById(request.getRecordedById())
            .recordedByName(request.getRecordedByName())
            .recordedByRole(request.getRecordedByRole())
            .locationName(request.getLocationName())
            .bedNumber(request.getBedNumber())
            .build();
    }

    /**
     * Update VitalSigns from request.
     */
    private void updateVitalSignsFromRequest(VitalSigns vitalSigns, VitalSignsRequest request) {
        if (request.getMeasurementTime() != null) vitalSigns.setMeasurementTime(request.getMeasurementTime());
        if (request.getShift() != null) vitalSigns.setShift(request.getShift());
        if (request.getMeasurementType() != null) vitalSigns.setMeasurementType(request.getMeasurementType());

        vitalSigns.setSystolicBp(request.getSystolicBp());
        vitalSigns.setDiastolicBp(request.getDiastolicBp());
        vitalSigns.setHeartRate(request.getHeartRate());
        vitalSigns.setRespiratoryRate(request.getRespiratoryRate());
        vitalSigns.setTemperature(request.getTemperature());
        vitalSigns.setTemperatureRoute(request.getTemperatureRoute());
        vitalSigns.setSpo2(request.getSpo2());
        vitalSigns.setOxygenTherapy(request.getOxygenTherapy());
        vitalSigns.setOxygenFlowRate(request.getOxygenFlowRate());
        vitalSigns.setOxygenDeliveryMethod(request.getOxygenDeliveryMethod());

        vitalSigns.setWeight(request.getWeight());
        vitalSigns.setHeight(request.getHeight());
        vitalSigns.setHeadCircumference(request.getHeadCircumference());

        vitalSigns.setGcsEye(request.getGcsEye());
        vitalSigns.setGcsVerbal(request.getGcsVerbal());
        vitalSigns.setGcsMotor(request.getGcsMotor());

        vitalSigns.setPainScore(request.getPainScore());
        vitalSigns.setPainLocation(request.getPainLocation());
        vitalSigns.setPainQuality(request.getPainQuality());

        vitalSigns.setFluidIntakeMl(request.getFluidIntakeMl());
        vitalSigns.setFluidOutputMl(request.getFluidOutputMl());
        vitalSigns.setUrineOutputMl(request.getUrineOutputMl());

        vitalSigns.setBloodGlucose(request.getBloodGlucose());
        vitalSigns.setBloodGlucoseUnit(request.getBloodGlucoseUnit());

        vitalSigns.setPeripheralPulse(request.getPeripheralPulse());
        vitalSigns.setCapillaryRefillTime(request.getCapillaryRefillTime());
        vitalSigns.setPupilReaction(request.getPupilReaction());

        vitalSigns.setNotes(request.getNotes());
        vitalSigns.setAlerts(request.getAlerts());

        if (request.getRecordedById() != null) vitalSigns.setRecordedById(request.getRecordedById());
        if (request.getRecordedByName() != null) vitalSigns.setRecordedByName(request.getRecordedByName());
        if (request.getRecordedByRole() != null) vitalSigns.setRecordedByRole(request.getRecordedByRole());

        vitalSigns.setLocationName(request.getLocationName());
        vitalSigns.setBedNumber(request.getBedNumber());
    }

    /**
     * Map entity to response DTO.
     */
    private VitalSignsResponse mapToResponse(VitalSigns vitalSigns) {
        String bloodPressure = null;
        if (vitalSigns.getSystolicBp() != null && vitalSigns.getDiastolicBp() != null) {
            bloodPressure = String.format("%d/%d", vitalSigns.getSystolicBp(), vitalSigns.getDiastolicBp());
        }

        return VitalSignsResponse.builder()
            .id(vitalSigns.getId())
            .encounterId(vitalSigns.getEncounter().getId())
            .patientId(vitalSigns.getPatientId())
            .measurementTime(vitalSigns.getMeasurementTime())
            .shift(vitalSigns.getShift())
            .shiftDisplay(vitalSigns.getShift() != null ? vitalSigns.getShift().getIndonesianName() : null)
            .measurementType(vitalSigns.getMeasurementType())
            .systolicBp(vitalSigns.getSystolicBp())
            .diastolicBp(vitalSigns.getDiastolicBp())
            .bloodPressure(bloodPressure)
            .heartRate(vitalSigns.getHeartRate())
            .respiratoryRate(vitalSigns.getRespiratoryRate())
            .temperature(vitalSigns.getTemperature())
            .temperatureRoute(vitalSigns.getTemperatureRoute())
            .spo2(vitalSigns.getSpo2())
            .oxygenTherapy(vitalSigns.getOxygenTherapy())
            .oxygenFlowRate(vitalSigns.getOxygenFlowRate())
            .oxygenDeliveryMethod(vitalSigns.getOxygenDeliveryMethod())
            .weight(vitalSigns.getWeight())
            .height(vitalSigns.getHeight())
            .bmi(vitalSigns.getBmi())
            .headCircumference(vitalSigns.getHeadCircumference())
            .gcsEye(vitalSigns.getGcsEye())
            .gcsVerbal(vitalSigns.getGcsVerbal())
            .gcsMotor(vitalSigns.getGcsMotor())
            .gcsTotal(vitalSigns.getGcsTotal())
            .gcsSeverity(vitalSigns.getGcsSeverity())
            .painScore(vitalSigns.getPainScore())
            .painLocation(vitalSigns.getPainLocation())
            .painQuality(vitalSigns.getPainQuality())
            .fluidIntakeMl(vitalSigns.getFluidIntakeMl())
            .fluidOutputMl(vitalSigns.getFluidOutputMl())
            .fluidBalanceMl(vitalSigns.getFluidBalanceMl())
            .urineOutputMl(vitalSigns.getUrineOutputMl())
            .bloodGlucose(vitalSigns.getBloodGlucose())
            .bloodGlucoseUnit(vitalSigns.getBloodGlucoseUnit())
            .meanArterialPressure(vitalSigns.getMeanArterialPressure())
            .peripheralPulse(vitalSigns.getPeripheralPulse())
            .capillaryRefillTime(vitalSigns.getCapillaryRefillTime())
            .pupilReaction(vitalSigns.getPupilReaction())
            .isAbnormal(vitalSigns.getIsAbnormal())
            .abnormalFlags(vitalSigns.getAbnormalFlags())
            .requiresNotification(vitalSigns.getRequiresNotification())
            .notificationSent(vitalSigns.getNotificationSent())
            .notifiedProviderId(vitalSigns.getNotifiedProviderId())
            .notes(vitalSigns.getNotes())
            .alerts(vitalSigns.getAlerts())
            .recordedById(vitalSigns.getRecordedById())
            .recordedByName(vitalSigns.getRecordedByName())
            .recordedByRole(vitalSigns.getRecordedByRole())
            .locationName(vitalSigns.getLocationName())
            .bedNumber(vitalSigns.getBedNumber())
            .createdAt(vitalSigns.getCreatedAt())
            .updatedAt(vitalSigns.getUpdatedAt())
            .withinNormalLimits(vitalSigns.isWithinNormalLimits())
            .requiresUrgentNotification(vitalSigns.requiresUrgentNotification())
            .build();
    }

    /**
     * Map entity to chart DTO.
     */
    private VitalSignsChartDto mapToChartDto(VitalSigns vitalSigns) {
        return VitalSignsChartDto.builder()
            .measurementTime(vitalSigns.getMeasurementTime())
            .systolicBp(vitalSigns.getSystolicBp())
            .diastolicBp(vitalSigns.getDiastolicBp())
            .heartRate(vitalSigns.getHeartRate())
            .respiratoryRate(vitalSigns.getRespiratoryRate())
            .temperature(vitalSigns.getTemperature())
            .spo2(vitalSigns.getSpo2())
            .painScore(vitalSigns.getPainScore())
            .gcsTotal(vitalSigns.getGcsTotal())
            .isAbnormal(vitalSigns.getIsAbnormal())
            .requiresNotification(vitalSigns.getRequiresNotification())
            .build();
    }
}
