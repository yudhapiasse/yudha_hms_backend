package com.yudha.hms.registration.service;

import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.patient.repository.PatientRepository;
import com.yudha.hms.registration.dto.AdmissionRequest;
import com.yudha.hms.registration.dto.EmergencyRegistrationRequest;
import com.yudha.hms.registration.dto.EmergencyRegistrationResponse;
import com.yudha.hms.registration.entity.*;
import com.yudha.hms.registration.repository.EmergencyRegistrationRepository;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
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
 * Service for Emergency Department registration.
 * Handles fast-track registration, unknown patients, and auto-conversion to inpatient.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmergencyRegistrationService {

    private final EmergencyRegistrationRepository emergencyRepository;
    private final PatientRepository patientRepository;
    private final InpatientAdmissionService inpatientAdmissionService;

    /**
     * Register emergency patient with fast-track support.
     * Supports unknown/unconscious patients without patient ID.
     *
     * @param request registration request
     * @return emergency registration response
     */
    @Transactional
    public EmergencyRegistrationResponse registerEmergency(EmergencyRegistrationRequest request) {
        log.info("Registering emergency patient. Unknown patient: {}", request.getIsUnknownPatient());

        // Validate patient if not unknown
        if (!Boolean.TRUE.equals(request.getIsUnknownPatient())) {
            if (request.getPatientId() == null) {
                throw new BusinessException("Patient ID is required for known patients");
            }
            validatePatientExists(request.getPatientId());
        }

        // Generate emergency number
        String emergencyNumber = generateEmergencyNumber();

        // Generate unknown patient identifier if needed
        String unknownIdentifier = null;
        if (Boolean.TRUE.equals(request.getIsUnknownPatient())) {
            unknownIdentifier = generateUnknownPatientIdentifier();
            log.info("Generated unknown patient identifier: {}", unknownIdentifier);
        }

        // Build emergency registration
        EmergencyRegistration emergency = buildEmergencyRegistration(request, emergencyNumber, unknownIdentifier);

        // Auto-triage if provided
        if (request.getTriageLevel() != null) {
            emergency.setTriageLevel(request.getTriageLevel());
            emergency.setTriagePriority(request.getTriagePriority() != null ? request.getTriagePriority() : mapTriageLevelToPriority(request.getTriageLevel()));
            emergency.setTriageTime(LocalDateTime.now());
            emergency.setStatus(EmergencyStatus.TRIAGED);

            if (request.getTriageLevel().isCritical()) {
                emergency.setIsCritical(true);
            }
        }

        // Set arrival time if not provided
        if (emergency.getArrivalTime() == null) {
            emergency.setArrivalTime(LocalDateTime.now());
        }

        // Save emergency registration
        EmergencyRegistration saved = emergencyRepository.save(emergency);

        log.info("Emergency registration created: {} for {}",
            emergencyNumber,
            saved.isPatientIdentified() ? "Patient ID: " + saved.getPatientId() : saved.getTemporaryName());

        return convertToResponse(saved);
    }

    /**
     * Get emergency registration by ID.
     */
    public EmergencyRegistrationResponse getEmergencyRegistration(UUID id) {
        EmergencyRegistration emergency = emergencyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "ID", id));
        return convertToResponse(emergency);
    }

    /**
     * Get emergency registration by emergency number.
     */
    public EmergencyRegistrationResponse getByEmergencyNumber(String emergencyNumber) {
        EmergencyRegistration emergency = emergencyRepository.findByEmergencyNumber(emergencyNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "Emergency Number", emergencyNumber));
        return convertToResponse(emergency);
    }

    /**
     * Get all active emergency patients (currently in ER).
     */
    public List<EmergencyRegistrationResponse> getAllActive() {
        log.info("Fetching all active emergency patients");
        return emergencyRepository.findAllActive().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get all critical patients in ER.
     */
    public List<EmergencyRegistrationResponse> getAllCritical() {
        log.info("Fetching all critical emergency patients");
        return emergencyRepository.findAllCritical().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get patients by triage level.
     */
    public List<EmergencyRegistrationResponse> getByTriageLevel(TriageLevel triageLevel) {
        return emergencyRepository.findByTriageLevel(triageLevel).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get patients waiting for triage.
     */
    public List<EmergencyRegistrationResponse> getWaitingForTriage() {
        return emergencyRepository.findWaitingForTriage().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get all unknown patients.
     */
    public List<EmergencyRegistrationResponse> getAllUnknownPatients() {
        return emergencyRepository.findAllUnknownPatients().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get all police cases.
     */
    public List<EmergencyRegistrationResponse> getAllPoliceCases() {
        return emergencyRepository.findAllPoliceCases().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get patient's emergency history.
     */
    public List<EmergencyRegistrationResponse> getPatientEmergencyHistory(UUID patientId) {
        return emergencyRepository.findByPatientIdAndDeletedAtIsNullOrderByRegistrationDateDesc(patientId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Convert emergency patient to inpatient admission.
     * Auto-creates inpatient admission and updates ER status.
     *
     * @param emergencyId emergency registration ID
     * @param admissionRequest inpatient admission request
     * @return updated emergency registration
     */
    @Transactional
    public EmergencyRegistrationResponse convertToInpatient(UUID emergencyId, AdmissionRequest admissionRequest) {
        log.info("Converting emergency patient to inpatient: {}", emergencyId);

        EmergencyRegistration emergency = emergencyRepository.findById(emergencyId)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "ID", emergencyId));

        // Validate can be admitted
        if (emergency.getConvertedToInpatient()) {
            throw new BusinessException("Patient has already been converted to inpatient");
        }

        if (!emergency.isPatientIdentified()) {
            throw new BusinessException("Cannot admit unknown patient. Please identify patient first.");
        }

        // Create inpatient admission
        var admissionResponse = inpatientAdmissionService.createAdmission(admissionRequest);

        // Update emergency registration
        emergency.convertToInpatient(UUID.fromString(admissionResponse.getId().toString()));
        emergencyRepository.save(emergency);

        log.info("Emergency patient {} converted to inpatient admission: {}",
            emergency.getEmergencyNumber(), admissionResponse.getAdmissionNumber());

        return convertToResponse(emergency);
    }

    /**
     * Discharge patient from ER.
     *
     * @param emergencyId emergency registration ID
     * @param disposition disposition type (DISCHARGED_HOME, TRANSFERRED, etc.)
     * @param notes discharge notes
     * @return updated emergency registration
     */
    @Transactional
    public EmergencyRegistrationResponse dischargeFromEr(UUID emergencyId, String disposition, String notes) {
        log.info("Discharging patient from ER: {}", emergencyId);

        EmergencyRegistration emergency = emergencyRepository.findById(emergencyId)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "ID", emergencyId));

        emergency.discharge(disposition, notes);
        emergencyRepository.save(emergency);

        log.info("Patient discharged from ER: {} with disposition: {}",
            emergency.getEmergencyNumber(), disposition);

        return convertToResponse(emergency);
    }

    /**
     * Link unknown patient to identified patient record.
     *
     * @param emergencyId emergency registration ID
     * @param patientId identified patient ID
     * @return updated emergency registration
     */
    @Transactional
    public EmergencyRegistrationResponse linkToPatient(UUID emergencyId, UUID patientId) {
        log.info("Linking unknown patient to patient record: {}", patientId);

        EmergencyRegistration emergency = emergencyRepository.findById(emergencyId)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "ID", emergencyId));

        if (!emergency.getIsUnknownPatient()) {
            throw new BusinessException("This is not an unknown patient");
        }

        validatePatientExists(patientId);

        emergency.setPatientId(patientId);
        emergency.setIsUnknownPatient(false);
        emergencyRepository.save(emergency);

        log.info("Unknown patient {} linked to patient ID: {}",
            emergency.getUnknownPatientIdentifier(), patientId);

        return convertToResponse(emergency);
    }

    /**
     * Update ER zone/bed assignment.
     */
    @Transactional
    public EmergencyRegistrationResponse updateErLocation(UUID emergencyId, String zone, String bedNumber) {
        EmergencyRegistration emergency = emergencyRepository.findById(emergencyId)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "ID", emergencyId));

        emergency.setErZone(zone);
        emergency.setErBedNumber(bedNumber);
        emergencyRepository.save(emergency);

        return convertToResponse(emergency);
    }

    /**
     * Get emergency registration by ID (alias for getEmergencyRegistration).
     */
    public EmergencyRegistrationResponse getEmergencyById(UUID emergencyId) {
        return getEmergencyRegistration(emergencyId);
    }

    /**
     * Get emergency registration by emergency number (alias for getByEmergencyNumber).
     */
    public EmergencyRegistrationResponse getEmergencyByNumber(String emergencyNumber) {
        return getByEmergencyNumber(emergencyNumber);
    }

    /**
     * Get emergency registrations by status.
     */
    public List<EmergencyRegistrationResponse> getByStatus(EmergencyStatus status) {
        log.info("Fetching emergency registrations by status: {}", status);
        return emergencyRepository.findByStatusAndDeletedAtIsNull(status)
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    /**
     * Get all trauma cases.
     */
    public List<EmergencyRegistrationResponse> getAllTraumaCases() {
        log.info("Fetching all trauma cases");
        return emergencyRepository.findAllTraumaCases()
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    /**
     * Get emergency registrations by ER zone.
     */
    public List<EmergencyRegistrationResponse> getByErZone(String zone) {
        log.info("Fetching emergency registrations in zone: {}", zone);
        List<EmergencyStatus> activeStatuses = List.of(
            EmergencyStatus.REGISTERED,
            EmergencyStatus.TRIAGED,
            EmergencyStatus.IN_TREATMENT,
            EmergencyStatus.WAITING_RESULTS
        );
        return emergencyRepository.findByErZoneAndStatusInAndDeletedAtIsNull(zone, activeStatuses)
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    /**
     * Get emergency registrations by date range.
     */
    public List<EmergencyRegistrationResponse> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching emergency registrations from {} to {}", startDate, endDate);
        return emergencyRepository.findByDateRange(startDate, endDate)
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    /**
     * Get emergency registrations by patient ID.
     */
    public List<EmergencyRegistrationResponse> getByPatientId(UUID patientId) {
        return getPatientEmergencyHistory(patientId);
    }

    /**
     * Update emergency registration.
     */
    @Transactional
    public EmergencyRegistrationResponse updateEmergency(UUID emergencyId, EmergencyRegistrationRequest request) {
        log.info("Updating emergency registration: {}", emergencyId);

        EmergencyRegistration emergency = emergencyRepository.findById(emergencyId)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "ID", emergencyId));

        // Update fields
        emergency.setChiefComplaint(request.getChiefComplaint());
        emergency.setPresentingProblem(request.getPresentingProblem());
        emergency.setSymptoms(request.getSymptoms());
        emergency.setCompanionName(request.getCompanionName());
        emergency.setCompanionRelationship(request.getCompanionRelationship());
        emergency.setCompanionPhone(request.getCompanionPhone());
        emergency.setCompanionAddress(request.getCompanionAddress());
        emergency.setRegistrationNotes(request.getRegistrationNotes());

        // Update police case info if applicable
        if (Boolean.TRUE.equals(request.getIsPoliceCase())) {
            emergency.setIsPoliceCase(true);
            emergency.setPoliceCaseType(request.getPoliceCaseType());
            emergency.setPoliceReportNumber(request.getPoliceReportNumber());
            emergency.setPoliceStation(request.getPoliceStation());
            emergency.setPoliceOfficerName(request.getPoliceOfficerName());
            emergency.setPoliceOfficerContact(request.getPoliceOfficerContact());
        }

        // Update trauma info if applicable
        if (Boolean.TRUE.equals(request.getIsTraumaCase())) {
            emergency.setIsTraumaCase(true);
            emergency.setTraumaType(request.getTraumaType());
            emergency.setAccidentLocation(request.getAccidentLocation());
            emergency.setAccidentTime(request.getAccidentTime());
            emergency.setMechanismOfInjury(request.getMechanismOfInjury());
        }

        emergencyRepository.save(emergency);
        return convertToResponse(emergency);
    }

    /**
     * Delete emergency registration.
     */
    @Transactional
    public void deleteEmergency(UUID emergencyId) {
        log.info("Deleting emergency registration: {}", emergencyId);

        EmergencyRegistration emergency = emergencyRepository.findById(emergencyId)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "ID", emergencyId));

        emergencyRepository.delete(emergency);
    }

    // ========== Private Helper Methods ==========

    /**
     * Generate emergency number: ER-YYYYMMDD-NNNN
     */
    private String generateEmergencyNumber() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "ER-" + today + "-";

        String lastNumber = emergencyRepository.findLatestEmergencyNumberWithPrefix(prefix)
            .orElse(prefix + "0000");

        int sequence = Integer.parseInt(lastNumber.substring(lastNumber.lastIndexOf("-") + 1)) + 1;
        return String.format("ER-%s-%04d", today, sequence);
    }

    /**
     * Generate unknown patient identifier: UNKNOWN-YYYYMMDD-NNN
     */
    private String generateUnknownPatientIdentifier() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = emergencyRepository.findAllUnknownPatients().size() + 1;
        return String.format("UNKNOWN-%s-%03d", today, count);
    }

    /**
     * Map triage level to priority number.
     */
    private Integer mapTriageLevelToPriority(TriageLevel level) {
        return level.getPriority();
    }

    /**
     * Validate patient exists.
     */
    private void validatePatientExists(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "ID", patientId);
        }
    }

    /**
     * Build emergency registration from request.
     */
    private EmergencyRegistration buildEmergencyRegistration(
        EmergencyRegistrationRequest request,
        String emergencyNumber,
        String unknownIdentifier
    ) {
        return EmergencyRegistration.builder()
            .emergencyNumber(emergencyNumber)
            // Patient info
            .patientId(request.getPatientId())
            .isUnknownPatient(request.getIsUnknownPatient())
            .unknownPatientIdentifier(unknownIdentifier)
            .temporaryName(request.getTemporaryName())
            .estimatedAge(request.getEstimatedAge())
            .estimatedGender(request.getEstimatedGender())
            // Registration
            .registrationDate(LocalDateTime.now())
            .registrationTime(LocalDateTime.now())
            .arrivalMode(request.getArrivalMode())
            .arrivalTime(request.getArrivalTime())
            // Ambulance
            .ambulanceType(request.getAmbulanceType())
            .ambulanceNumber(request.getAmbulanceNumber())
            .ambulanceOrigin(request.getAmbulanceOrigin())
            .paramedicName(request.getParamedicName())
            .paramedicPhone(request.getParamedicPhone())
            // Chief complaint
            .chiefComplaint(request.getChiefComplaint())
            .presentingProblem(request.getPresentingProblem())
            .symptoms(request.getSymptoms())
            .onsetTime(request.getOnsetTime())
            .durationMinutes(request.getDurationMinutes())
            // Initial vitals
            .initialBloodPressureSystolic(request.getInitialBloodPressureSystolic())
            .initialBloodPressureDiastolic(request.getInitialBloodPressureDiastolic())
            .initialHeartRate(request.getInitialHeartRate())
            .initialRespiratoryRate(request.getInitialRespiratoryRate())
            .initialTemperature(request.getInitialTemperature())
            .initialOxygenSaturation(request.getInitialOxygenSaturation())
            .initialGcsScore(request.getInitialGcsScore())
            .initialPainScore(request.getInitialPainScore())
            // Police case
            .isPoliceCase(request.getIsPoliceCase())
            .policeCaseType(request.getPoliceCaseType())
            .policeReportNumber(request.getPoliceReportNumber())
            .policeStation(request.getPoliceStation())
            .policeOfficerName(request.getPoliceOfficerName())
            .policeOfficerContact(request.getPoliceOfficerContact())
            // Trauma
            .isTraumaCase(request.getIsTraumaCase())
            .traumaType(request.getTraumaType())
            .accidentLocation(request.getAccidentLocation())
            .accidentTime(request.getAccidentTime())
            .mechanismOfInjury(request.getMechanismOfInjury())
            // Companion
            .companionName(request.getCompanionName())
            .companionRelationship(request.getCompanionRelationship())
            .companionPhone(request.getCompanionPhone())
            .companionAddress(request.getCompanionAddress())
            // Referral
            .referredFrom(request.getReferredFrom())
            .referralDoctor(request.getReferralDoctor())
            .referralDiagnosis(request.getReferralDiagnosis())
            .referralLetterNumber(request.getReferralLetterNumber())
            // Payment
            .paymentMethod(request.getPaymentMethod())
            .insuranceName(request.getInsuranceName())
            .insuranceNumber(request.getInsuranceNumber())
            .guaranteeLetterNumber(request.getGuaranteeLetterNumber())
            // Medical history
            .medicalHistorySummary(request.getMedicalHistorySummary())
            .currentMedications(request.getCurrentMedications())
            .allergies(request.getAllergies())
            .specialNeeds(request.getSpecialNeeds())
            .registrationNotes(request.getRegistrationNotes())
            // Priority flags
            .isCritical(request.getIsCritical())
            .requiresIsolation(request.getRequiresIsolation())
            .isolationReason(request.getIsolationReason())
            .isInfectious(request.getIsInfectious())
            .infectiousDisease(request.getInfectiousDisease())
            // Status
            .status(EmergencyStatus.REGISTERED)
            .build();
    }

    /**
     * Convert entity to response DTO.
     */
    private EmergencyRegistrationResponse convertToResponse(EmergencyRegistration emergency) {
        // Get patient info if available
        String patientName = null;
        String patientMrn = null;
        if (emergency.getPatientId() != null) {
            Patient patient = patientRepository.findById(emergency.getPatientId()).orElse(null);
            if (patient != null) {
                patientName = patient.getFullName();
                patientMrn = patient.getMrn();
            }
        }

        // Format vital signs
        String vitalSigns = formatVitalSigns(emergency);

        // Format ER location
        String erLocation = formatErLocation(emergency);

        // Format wait time
        String waitTimeDisplay = formatWaitTime(emergency);

        return EmergencyRegistrationResponse.builder()
            .id(emergency.getId())
            .emergencyNumber(emergency.getEmergencyNumber())
            // Patient
            .patientId(emergency.getPatientId())
            .patientName(patientName)
            .patientMrn(patientMrn)
            .isUnknownPatient(emergency.getIsUnknownPatient())
            .unknownPatientIdentifier(emergency.getUnknownPatientIdentifier())
            .temporaryName(emergency.getTemporaryName())
            .estimatedAge(emergency.getEstimatedAge())
            .estimatedGender(emergency.getEstimatedGender())
            // Registration
            .registrationDate(emergency.getRegistrationDate())
            .registrationTime(emergency.getRegistrationTime())
            .arrivalMode(emergency.getArrivalMode())
            .arrivalTime(emergency.getArrivalTime())
            // Ambulance
            .ambulanceType(emergency.getAmbulanceType())
            .ambulanceNumber(emergency.getAmbulanceNumber())
            .ambulanceOrigin(emergency.getAmbulanceOrigin())
            .paramedicName(emergency.getParamedicName())
            // Complaint
            .chiefComplaint(emergency.getChiefComplaint())
            .presentingProblem(emergency.getPresentingProblem())
            .symptoms(emergency.getSymptoms())
            // Triage
            .triageLevel(emergency.getTriageLevel())
            .triageLevelColor(emergency.getTriageLevel() != null ? emergency.getTriageLevel().name() : null)
            .triagePriority(emergency.getTriagePriority())
            .triageTime(emergency.getTriageTime())
            .triagedByName(emergency.getTriaged_byName())
            // Vitals
            .vitalSigns(vitalSigns)
            .initialGcsScore(emergency.getInitialGcsScore())
            .initialPainScore(emergency.getInitialPainScore())
            // Police
            .isPoliceCase(emergency.getIsPoliceCase())
            .policeCaseType(emergency.getPoliceCaseType())
            .policeReportNumber(emergency.getPoliceReportNumber())
            .policeOfficerName(emergency.getPoliceOfficerName())
            // Trauma
            .isTraumaCase(emergency.getIsTraumaCase())
            .traumaType(emergency.getTraumaType())
            .accidentLocation(emergency.getAccidentLocation())
            // Team
            .attendingDoctorName(emergency.getAttendingDoctorName())
            .assignedNurseName(emergency.getAssignedNurseName())
            // Location
            .erZone(emergency.getErZone())
            .erBedNumber(emergency.getErBedNumber())
            .erLocation(erLocation)
            // Status
            .status(emergency.getStatus())
            .statusDisplay(emergency.getStatus().getDisplayName())
            .disposition(emergency.getDisposition())
            .dispositionTime(emergency.getDispositionTime())
            // Inpatient conversion
            .convertedToInpatient(emergency.getConvertedToInpatient())
            .inpatientAdmissionId(emergency.getInpatientAdmissionId())
            .conversionTime(emergency.getConversionTime())
            // Timing
            .doorToTriageMinutes(emergency.getDoorToTriageMinutes())
            .doorToDoctorMinutes(emergency.getDoorToDoctorMinutes())
            .totalErTimeMinutes(emergency.getTotalErTimeMinutes())
            .waitTimeDisplay(waitTimeDisplay)
            // Flags
            .isCritical(emergency.getIsCritical())
            .requiresIsolation(emergency.getRequiresIsolation())
            .isolationReason(emergency.getIsolationReason())
            // Companion
            .companionName(emergency.getCompanionName())
            .companionPhone(emergency.getCompanionPhone())
            // Payment
            .paymentMethod(emergency.getPaymentMethod())
            .insuranceName(emergency.getInsuranceName())
            // Audit
            .createdAt(emergency.getCreatedAt())
            .updatedAt(emergency.getUpdatedAt())
            .createdBy(emergency.getCreatedBy())
            .build();
    }

    private String formatVitalSigns(EmergencyRegistration e) {
        StringBuilder sb = new StringBuilder();
        if (e.getInitialBloodPressureSystolic() != null) {
            sb.append(String.format("BP: %d/%d", e.getInitialBloodPressureSystolic(), e.getInitialBloodPressureDiastolic()));
        }
        if (e.getInitialHeartRate() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(String.format("HR: %d", e.getInitialHeartRate()));
        }
        if (e.getInitialRespiratoryRate() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(String.format("RR: %d", e.getInitialRespiratoryRate()));
        }
        if (e.getInitialTemperature() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(String.format("Temp: %.1f°C", e.getInitialTemperature()));
        }
        if (e.getInitialOxygenSaturation() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(String.format("SpO2: %d%%", e.getInitialOxygenSaturation()));
        }
        return sb.length() > 0 ? sb.toString() : "Not recorded";
    }

    private String formatErLocation(EmergencyRegistration e) {
        if (e.getErZone() != null && e.getErBedNumber() != null) {
            return e.getErZone().replace("_", " ") + " - Bed " + e.getErBedNumber();
        } else if (e.getErZone() != null) {
            return e.getErZone().replace("_", " ");
        }
        return "Not assigned";
    }

    private String formatWaitTime(EmergencyRegistration e) {
        if (e.getTotalErTimeMinutes() != null) {
            return e.getTotalErTimeMinutes() + " minutes in ER";
        } else if (e.getArrivalTime() != null) {
            long minutes = java.time.Duration.between(e.getArrivalTime(), LocalDateTime.now()).toMinutes();
            return minutes + " minutes in ER";
        }
        return null;
    }
}
