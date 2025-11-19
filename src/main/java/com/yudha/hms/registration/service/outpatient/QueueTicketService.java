package com.yudha.hms.registration.service.outpatient;

import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.patient.repository.PatientRepository;
import com.yudha.hms.registration.dto.outpatient.QueueTicketResponse;
import com.yudha.hms.registration.entity.OutpatientRegistration;
import com.yudha.hms.registration.repository.OutpatientRegistrationRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for generating queue tickets.
 * Provides text-based ticket generation for printing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueueTicketService {

    private final OutpatientRegistrationRepository registrationRepository;
    private final PatientRepository patientRepository;

    /**
     * Generate queue ticket for a registration.
     *
     * @param registrationId registration ID
     * @return queue ticket response
     */
    public QueueTicketResponse generateQueueTicket(UUID registrationId) {
        log.info("Generating queue ticket for registration: {}", registrationId);

        OutpatientRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "ID", registrationId));

        Patient patient = patientRepository.findById(registration.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", registration.getPatientId()));

        // Calculate patients ahead
        Integer patientsAhead = null;
        if (registration.getQueueNumber() != null && registration.getQueueNumber() > 1) {
            patientsAhead = registration.getQueueNumber() - 1;
        }

        // Calculate estimated wait time (15 minutes per patient)
        Integer estimatedWaitTime = patientsAhead != null ? patientsAhead * 15 : null;

        // Build ticket response
        QueueTicketResponse ticket = QueueTicketResponse.builder()
            .registrationNumber(registration.getRegistrationNumber())
            .queueCode(registration.getQueueCode())
            .queueNumber(registration.getQueueNumber())
            .patientName(patient.getFullName())
            .patientMrn(patient.getMrn())
            .polyclinicName(registration.getPolyclinic().getName())
            .polyclinicLocation(registration.getPolyclinic().getLocationDisplay())
            .polyclinicBuilding(registration.getPolyclinic().getBuilding())
            .polyclinicFloor(registration.getPolyclinic().getFloorLocation())
            .doctorName(registration.getDoctor().getDisplayName())
            .doctorTitle(registration.getDoctor().getTitle())
            .registrationDate(registration.getRegistrationDate())
            .registrationTime(registration.getRegistrationTime().toLocalTime())
            .appointmentDate(registration.getAppointmentDate())
            .appointmentTime(registration.getAppointmentTime())
            .estimatedWaitTimeMinutes(estimatedWaitTime)
            .patientsAhead(patientsAhead)
            .build();

        // Generate ticket text
        String ticketText = ticket.generatePlainTextTicket();
        ticket.setTicketText(ticketText);

        // TODO: Generate PDF ticket (future enhancement)
        // ticket.setTicketPdfBase64(generatePdfTicket(ticket));

        log.info("Queue ticket generated for {}", registration.getQueueCode());

        return ticket;
    }

    /**
     * Generate queue ticket by registration number.
     *
     * @param registrationNumber registration number
     * @return queue ticket response
     */
    public QueueTicketResponse generateQueueTicketByNumber(String registrationNumber) {
        OutpatientRegistration registration = registrationRepository
            .findByRegistrationNumber(registrationNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "Number", registrationNumber));

        return generateQueueTicket(registration.getId());
    }
}