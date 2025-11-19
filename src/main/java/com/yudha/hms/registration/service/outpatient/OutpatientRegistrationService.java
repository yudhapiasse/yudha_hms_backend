package com.yudha.hms.registration.service.outpatient;

import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.patient.repository.PatientRepository;
import com.yudha.hms.registration.dto.outpatient.OutpatientRegistrationRequest;
import com.yudha.hms.registration.dto.outpatient.OutpatientRegistrationResponse;
import com.yudha.hms.registration.entity.*;
import com.yudha.hms.registration.repository.*;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for outpatient registration management.
 * Handles walk-in and appointment-based registrations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OutpatientRegistrationService {

    private final OutpatientRegistrationRepository registrationRepository;
    private final PatientRepository patientRepository;
    private final PolyclinicRepository polyclinicRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final QueueService queueService;
    private final DoctorScheduleService doctorScheduleService;

    /**
     * Register walk-in patient.
     * For same-day visits without appointment.
     *
     * @param request registration request
     * @return registration response
     */
    @Transactional
    public OutpatientRegistrationResponse registerWalkIn(OutpatientRegistrationRequest request) {
        log.info("Processing walk-in registration for patient {}", request.getPatientId());

        // Validate request
        validateWalkInRequest(request);

        // Validate patient, polyclinic, and doctor
        Patient patient = validatePatient(request.getPatientId());
        Polyclinic polyclinic = validatePolyclinic(request.getPolyclinicId());
        Doctor doctor = validateDoctor(request.getDoctorId());

        // Validate polyclinic is open and allows walk-in
        validatePolyclinicForWalkIn(polyclinic);

        // Validate doctor is available today
        validateDoctorForWalkIn(doctor.getId(), polyclinic.getId());

        // Generate registration number
        String registrationNumber = generateRegistrationNumber();

        // Generate queue number
        String queueCode = queueService.generateQueueNumber(polyclinic.getId(), LocalDate.now());
        Integer queueNumber = queueService.getCurrentQueueNumber(polyclinic.getId());

        // Calculate fees
        BigDecimal registrationFee = calculateRegistrationFee(polyclinic, request.getIsBpjs());
        BigDecimal consultationFee = calculateConsultationFee(doctor, request.getIsBpjs());
        BigDecimal totalFee = registrationFee.add(consultationFee);

        // Build registration
        OutpatientRegistration registration = OutpatientRegistration.builder()
            .registrationNumber(registrationNumber)
            .patientId(patient.getId())
            .polyclinic(polyclinic)
            .doctor(doctor)
            .registrationDate(LocalDate.now())
            .registrationTime(LocalDateTime.now())
            .registrationType(RegistrationType.WALK_IN)
            .paymentMethod(request.getPaymentMethod())
            .isBpjs(request.getIsBpjs())
            .bpjsCardNumber(request.getBpjsCardNumber())
            .registrationFee(registrationFee)
            .consultationFee(consultationFee)
            .totalFee(totalFee)
            .status(RegistrationStatus.REGISTERED)
            .queueNumber(queueNumber)
            .queueCode(queueCode)
            .chiefComplaint(request.getChiefComplaint())
            .notes(request.getNotes())
            .referralFrom(request.getReferralFrom())
            .referralLetterNumber(request.getReferralLetterNumber())
            .build();

        // Save registration
        OutpatientRegistration saved = registrationRepository.save(registration);

        log.info("Walk-in registration created: {} with queue {}", registrationNumber, queueCode);

        return convertToResponse(saved, patient);
    }

    /**
     * Book appointment for future visit.
     *
     * @param request registration request
     * @return registration response
     */
    @Transactional
    public OutpatientRegistrationResponse bookAppointment(OutpatientRegistrationRequest request) {
        log.info("Processing appointment booking for patient {} on {} at {}",
            request.getPatientId(), request.getAppointmentDate(), request.getAppointmentTime());

        // Validate request
        validateAppointmentRequest(request);

        // Validate patient, polyclinic, and doctor
        Patient patient = validatePatient(request.getPatientId());
        Polyclinic polyclinic = validatePolyclinic(request.getPolyclinicId());
        Doctor doctor = validateDoctor(request.getDoctorId());

        // Validate polyclinic allows appointments
        if (!polyclinic.areAppointmentsAllowed()) {
            throw new BusinessException("Polyclinic does not accept appointments");
        }

        // Validate doctor availability
        doctorScheduleService.validateAppointment(
            doctor.getId(),
            polyclinic.getId(),
            request.getAppointmentDate(),
            request.getAppointmentTime()
        );

        // Get doctor schedule
        DoctorSchedule schedule = scheduleRepository.findScheduleForDate(
            doctor.getId(),
            polyclinic.getId(),
            request.getAppointmentDate().getDayOfWeek().name(),
            request.getAppointmentDate()
        ).orElseThrow(() -> new BusinessException("Doctor schedule not found"));

        // Calculate appointment end time
        LocalTime appointmentEndTime = request.getAppointmentTime()
            .plusMinutes(schedule.getAppointmentDurationMinutes());

        // Generate registration number
        String registrationNumber = generateRegistrationNumber();

        // Calculate fees
        BigDecimal registrationFee = calculateRegistrationFee(polyclinic, request.getIsBpjs());
        BigDecimal consultationFee = calculateConsultationFee(doctor, request.getIsBpjs());
        BigDecimal totalFee = registrationFee.add(consultationFee);

        // Build registration
        OutpatientRegistration registration = OutpatientRegistration.builder()
            .registrationNumber(registrationNumber)
            .patientId(patient.getId())
            .polyclinic(polyclinic)
            .doctor(doctor)
            .doctorSchedule(schedule)
            .registrationDate(LocalDate.now())
            .registrationTime(LocalDateTime.now())
            .registrationType(RegistrationType.APPOINTMENT)
            .appointmentDate(request.getAppointmentDate())
            .appointmentTime(request.getAppointmentTime())
            .appointmentEndTime(appointmentEndTime)
            .paymentMethod(request.getPaymentMethod())
            .isBpjs(request.getIsBpjs())
            .bpjsCardNumber(request.getBpjsCardNumber())
            .registrationFee(registrationFee)
            .consultationFee(consultationFee)
            .totalFee(totalFee)
            .status(RegistrationStatus.REGISTERED)
            .chiefComplaint(request.getChiefComplaint())
            .notes(request.getNotes())
            .referralFrom(request.getReferralFrom())
            .referralLetterNumber(request.getReferralLetterNumber())
            .build();

        // Save registration
        OutpatientRegistration saved = registrationRepository.save(registration);

        log.info("Appointment booked: {} for {} at {}",
            registrationNumber, request.getAppointmentDate(), request.getAppointmentTime());

        return convertToResponse(saved, patient);
    }

    /**
     * Check-in patient (assign queue number for appointments).
     *
     * @param registrationId registration ID
     * @return updated registration
     */
    @Transactional
    public OutpatientRegistrationResponse checkInPatient(UUID registrationId) {
        log.info("Checking in patient: {}", registrationId);

        OutpatientRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "ID", registrationId));

        if (registration.getStatus() != RegistrationStatus.REGISTERED) {
            throw new BusinessException("Patient has already been checked in or registration is not active");
        }

        // Generate queue number if not already assigned (for appointments)
        if (registration.getQueueNumber() == null) {
            LocalDate queueDate = registration.isAppointment() ?
                registration.getAppointmentDate() : registration.getRegistrationDate();

            String queueCode = queueService.generateQueueNumber(
                registration.getPolyclinic().getId(),
                queueDate
            );
            Integer queueNumber = queueService.getCurrentQueueNumber(
                registration.getPolyclinic().getId(),
                queueDate
            );

            registration.setQueueNumber(queueNumber);
            registration.setQueueCode(queueCode);
        }

        // Check in
        registration.checkIn();

        registrationRepository.save(registration);

        log.info("Patient checked in with queue: {}", registration.getQueueCode());

        Patient patient = patientRepository.findById(registration.getPatientId()).orElse(null);
        return convertToResponse(registration, patient);
    }

    /**
     * Get registration by ID.
     *
     * @param registrationId registration ID
     * @return registration response
     */
    public OutpatientRegistrationResponse getRegistration(UUID registrationId) {
        OutpatientRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "ID", registrationId));

        Patient patient = patientRepository.findById(registration.getPatientId()).orElse(null);
        return convertToResponse(registration, patient);
    }

    /**
     * Get registration by registration number.
     *
     * @param registrationNumber registration number
     * @return registration response
     */
    public OutpatientRegistrationResponse getByRegistrationNumber(String registrationNumber) {
        OutpatientRegistration registration = registrationRepository
            .findByRegistrationNumber(registrationNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "Number", registrationNumber));

        Patient patient = patientRepository.findById(registration.getPatientId()).orElse(null);
        return convertToResponse(registration, patient);
    }

    /**
     * Get all registrations for today by polyclinic.
     *
     * @param polyclinicId polyclinic ID
     * @return list of registrations
     */
    public List<OutpatientRegistrationResponse> getTodaysRegistrations(UUID polyclinicId) {
        return registrationRepository.findTodaysByPolyclinic(polyclinicId, LocalDate.now())
            .stream()
            .map(reg -> {
                Patient patient = patientRepository.findById(reg.getPatientId()).orElse(null);
                return convertToResponse(reg, patient);
            })
            .collect(Collectors.toList());
    }

    /**
     * Cancel registration.
     *
     * @param registrationId registration ID
     * @param reason cancellation reason
     * @param cancelledBy user who cancelled
     * @return updated registration
     */
    @Transactional
    public OutpatientRegistrationResponse cancelRegistration(
        UUID registrationId,
        String reason,
        String cancelledBy
    ) {
        log.info("Cancelling registration: {}", registrationId);

        OutpatientRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "ID", registrationId));

        if (!registration.canBeCancelled()) {
            throw new BusinessException("Registration cannot be cancelled in current status: " +
                registration.getStatus());
        }

        registration.cancel(reason, cancelledBy);
        registrationRepository.save(registration);

        log.info("Registration cancelled: {}", registration.getRegistrationNumber());

        Patient patient = patientRepository.findById(registration.getPatientId()).orElse(null);
        return convertToResponse(registration, patient);
    }

    // ========== Private Helper Methods ==========

    private void validateWalkInRequest(OutpatientRegistrationRequest request) {
        if (request.getRegistrationType() != RegistrationType.WALK_IN) {
            throw new BusinessException("Invalid registration type for walk-in");
        }
        if (!request.isBpjsDataValid()) {
            throw new BusinessException("BPJS card number is required for BPJS patients");
        }
    }

    private void validateAppointmentRequest(OutpatientRegistrationRequest request) {
        if (request.getRegistrationType() != RegistrationType.APPOINTMENT) {
            throw new BusinessException("Invalid registration type for appointment");
        }
        if (!request.isAppointmentDataValid()) {
            throw new BusinessException("Appointment date and time are required");
        }
        if (!request.isBpjsDataValid()) {
            throw new BusinessException("BPJS card number is required for BPJS patients");
        }
    }

    private Patient validatePatient(UUID patientId) {
        return patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", patientId));
    }

    private Polyclinic validatePolyclinic(UUID polyclinicId) {
        Polyclinic polyclinic = polyclinicRepository.findById(polyclinicId)
            .orElseThrow(() -> new ResourceNotFoundException("Polyclinic", "ID", polyclinicId));

        if (!Boolean.TRUE.equals(polyclinic.getIsActive())) {
            throw new BusinessException("Polyclinic is not active");
        }

        return polyclinic;
    }

    private Doctor validateDoctor(UUID doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "ID", doctorId));

        if (!doctor.canPractice()) {
            throw new BusinessException("Doctor is not available for practice");
        }

        return doctor;
    }

    private void validatePolyclinicForWalkIn(Polyclinic polyclinic) {
        if (!polyclinic.isWalkInAllowed()) {
            throw new BusinessException("Polyclinic does not accept walk-in registrations");
        }

        if (!polyclinic.isOperatingOnDate(LocalDate.now())) {
            throw new BusinessException("Polyclinic is not operating today");
        }

        // Check capacity
        long todayCount = registrationRepository.countByPolyclinicIdAndRegistrationDate(
            polyclinic.getId(),
            LocalDate.now()
        );

        if (todayCount >= polyclinic.getMaxPatientsPerDay()) {
            throw new BusinessException("Polyclinic has reached maximum capacity for today");
        }
    }

    private void validateDoctorForWalkIn(UUID doctorId, UUID polyclinicId) {
        // Check if doctor has schedule today
        String today = LocalDate.now().getDayOfWeek().name();
        var schedule = scheduleRepository.findScheduleForDate(
            doctorId,
            polyclinicId,
            today,
            LocalDate.now()
        );

        if (schedule.isEmpty()) {
            throw new BusinessException("Doctor does not have a schedule today at this polyclinic");
        }

        // Check if doctor has reached capacity
        long todayCount = registrationRepository.countByDoctorIdAndRegistrationDate(
            doctorId,
            LocalDate.now()
        );

        if (todayCount >= schedule.get().getMaxPatients()) {
            throw new BusinessException("Doctor has reached maximum patient capacity for today");
        }
    }

    private String generateRegistrationNumber() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "REG-" + today + "-";

        String lastNumber = registrationRepository.findLatestRegistrationNumberWithPrefix(prefix)
            .orElse(prefix + "0000");

        int sequence = Integer.parseInt(lastNumber.substring(lastNumber.lastIndexOf("-") + 1)) + 1;
        return String.format("REG-%s-%04d", today, sequence);
    }

    private BigDecimal calculateRegistrationFee(Polyclinic polyclinic, Boolean isBpjs) {
        if (Boolean.TRUE.equals(isBpjs)) {
            return BigDecimal.ZERO;
        }
        return polyclinic.getBaseRegistrationFee() != null ?
            polyclinic.getBaseRegistrationFee() : BigDecimal.ZERO;
    }

    private BigDecimal calculateConsultationFee(Doctor doctor, Boolean isBpjs) {
        return doctor.getConsultationFee(Boolean.TRUE.equals(isBpjs));
    }

    private OutpatientRegistrationResponse convertToResponse(
        OutpatientRegistration registration,
        Patient patient
    ) {
        // Calculate estimated wait time
        Integer estimatedWaitTime = null;
        if (registration.getStatus() == RegistrationStatus.WAITING && registration.getQueueNumber() != null) {
            // Estimate 15 minutes per patient ahead
            estimatedWaitTime = (registration.getQueueNumber() - 1) * 15;
        }

        return OutpatientRegistrationResponse.builder()
            .id(registration.getId())
            .registrationNumber(registration.getRegistrationNumber())
            .patientId(registration.getPatientId())
            .patientName(patient != null ? patient.getFullName() : null)
            .patientMrn(patient != null ? patient.getMrn() : null)
            .polyclinicId(registration.getPolyclinic().getId())
            .polyclinicCode(registration.getPolyclinic().getCode())
            .polyclinicName(registration.getPolyclinic().getName())
            .polyclinicLocation(registration.getPolyclinic().getLocationDisplay())
            .doctorId(registration.getDoctor().getId())
            .doctorName(registration.getDoctor().getFullName())
            .doctorTitle(registration.getDoctor().getTitle())
            .doctorSpecialization(registration.getDoctor().getSpecialization())
            .registrationDate(registration.getRegistrationDate())
            .registrationTime(registration.getRegistrationTime())
            .registrationType(registration.getRegistrationType())
            .registrationTypeDisplay(registration.getRegistrationType().getDisplayName())
            .appointmentDate(registration.getAppointmentDate())
            .appointmentTime(registration.getAppointmentTime())
            .appointmentEndTime(registration.getAppointmentEndTime())
            .queueNumber(registration.getQueueNumber())
            .queueCode(registration.getQueueCode())
            .estimatedWaitTimeMinutes(estimatedWaitTime)
            .paymentMethod(registration.getPaymentMethod())
            .paymentMethodDisplay(registration.getPaymentMethod() != null ?
                registration.getPaymentMethod().getDisplayName() : null)
            .isBpjs(registration.getIsBpjs())
            .bpjsCardNumber(registration.getBpjsCardNumber())
            .registrationFee(registration.getRegistrationFee())
            .consultationFee(registration.getConsultationFee())
            .totalFee(registration.getTotalFee())
            .status(registration.getStatus())
            .statusDisplay(registration.getStatus().getDisplayName())
            .chiefComplaint(registration.getChiefComplaint())
            .notes(registration.getNotes())
            .referralFrom(registration.getReferralFrom())
            .referralLetterNumber(registration.getReferralLetterNumber())
            .checkInTime(registration.getCheckInTime())
            .consultationStartTime(registration.getConsultationStartTime())
            .consultationEndTime(registration.getConsultationEndTime())
            .cancelledAt(registration.getCancelledAt())
            .cancellationReason(registration.getCancellationReason())
            .cancelledBy(registration.getCancelledBy())
            .createdAt(registration.getCreatedAt())
            .updatedAt(registration.getUpdatedAt())
            .createdBy(registration.getCreatedBy())
            .build();
    }
}
