package com.yudha.hms.registration.controller.outpatient;

import com.yudha.hms.registration.dto.outpatient.OutpatientRegistrationRequest;
import com.yudha.hms.registration.dto.outpatient.OutpatientRegistrationResponse;
import com.yudha.hms.registration.dto.outpatient.QueueTicketResponse;
import com.yudha.hms.registration.service.outpatient.OutpatientRegistrationService;
import com.yudha.hms.registration.service.outpatient.QueueTicketService;
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
 * REST Controller for outpatient registration endpoints.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/api/outpatient/registrations")
@RequiredArgsConstructor
@Slf4j
public class OutpatientRegistrationController {

    private final OutpatientRegistrationService registrationService;
    private final QueueTicketService ticketService;

    /**
     * Register walk-in patient.
     * POST /api/outpatient/registrations/walk-in
     */
    @PostMapping("/walk-in")
    public ResponseEntity<ApiResponse<OutpatientRegistrationResponse>> registerWalkIn(
        @Valid @RequestBody OutpatientRegistrationRequest request
    ) {
        log.info("POST /api/outpatient/registrations/walk-in - Walk-in registration request received");

        OutpatientRegistrationResponse response = registrationService.registerWalkIn(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Walk-in registration completed successfully",
                response
            ));
    }

    /**
     * Book appointment.
     * POST /api/outpatient/registrations/appointment
     */
    @PostMapping("/appointment")
    public ResponseEntity<ApiResponse<OutpatientRegistrationResponse>> bookAppointment(
        @Valid @RequestBody OutpatientRegistrationRequest request
    ) {
        log.info("POST /api/outpatient/registrations/appointment - Appointment booking request received");

        OutpatientRegistrationResponse response = registrationService.bookAppointment(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Appointment booked successfully",
                response
            ));
    }

    /**
     * Get registration by ID.
     * GET /api/outpatient/registrations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OutpatientRegistrationResponse>> getRegistration(
        @PathVariable UUID id
    ) {
        log.info("GET /api/outpatient/registrations/{} - Fetch registration", id);

        OutpatientRegistrationResponse response = registrationService.getRegistration(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Registration retrieved successfully",
            response
        ));
    }

    /**
     * Get registration by registration number.
     * GET /api/outpatient/registrations/number/{registrationNumber}
     */
    @GetMapping("/number/{registrationNumber}")
    public ResponseEntity<ApiResponse<OutpatientRegistrationResponse>> getByRegistrationNumber(
        @PathVariable String registrationNumber
    ) {
        log.info("GET /api/outpatient/registrations/number/{} - Fetch registration by number",
            registrationNumber);

        OutpatientRegistrationResponse response = registrationService
            .getByRegistrationNumber(registrationNumber);

        return ResponseEntity.ok(ApiResponse.success(
            "Registration retrieved successfully",
            response
        ));
    }

    /**
     * Get today's registrations by polyclinic.
     * GET /api/outpatient/registrations/polyclinic/{polyclinicId}/today
     */
    @GetMapping("/polyclinic/{polyclinicId}/today")
    public ResponseEntity<ApiResponse<List<OutpatientRegistrationResponse>>> getTodaysRegistrations(
        @PathVariable UUID polyclinicId
    ) {
        log.info("GET /api/outpatient/registrations/polyclinic/{}/today - Fetch today's registrations",
            polyclinicId);

        List<OutpatientRegistrationResponse> registrations = registrationService
            .getTodaysRegistrations(polyclinicId);

        return ResponseEntity.ok(ApiResponse.success(
            "Today's registrations retrieved successfully",
            registrations
        ));
    }

    /**
     * Check-in patient.
     * PUT /api/outpatient/registrations/{id}/check-in
     */
    @PutMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse<OutpatientRegistrationResponse>> checkInPatient(
        @PathVariable UUID id
    ) {
        log.info("PUT /api/outpatient/registrations/{}/check-in - Check-in patient", id);

        OutpatientRegistrationResponse response = registrationService.checkInPatient(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Patient checked in successfully",
            response
        ));
    }

    /**
     * Cancel registration.
     * PUT /api/outpatient/registrations/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OutpatientRegistrationResponse>> cancelRegistration(
        @PathVariable UUID id,
        @RequestParam String reason,
        @RequestParam(required = false) String cancelledBy
    ) {
        log.info("PUT /api/outpatient/registrations/{}/cancel - Cancel registration", id);

        OutpatientRegistrationResponse response = registrationService.cancelRegistration(
            id,
            reason,
            cancelledBy != null ? cancelledBy : "System"
        );

        return ResponseEntity.ok(ApiResponse.success(
            "Registration cancelled successfully",
            response
        ));
    }

    /**
     * Generate queue ticket.
     * GET /api/outpatient/registrations/{id}/ticket
     */
    @GetMapping("/{id}/ticket")
    public ResponseEntity<ApiResponse<QueueTicketResponse>> generateTicket(
        @PathVariable UUID id
    ) {
        log.info("GET /api/outpatient/registrations/{}/ticket - Generate queue ticket", id);

        QueueTicketResponse ticket = ticketService.generateQueueTicket(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Queue ticket generated successfully",
            ticket
        ));
    }

    /**
     * Generate queue ticket by registration number.
     * GET /api/outpatient/registrations/ticket/{registrationNumber}
     */
    @GetMapping("/ticket/{registrationNumber}")
    public ResponseEntity<ApiResponse<QueueTicketResponse>> generateTicketByNumber(
        @PathVariable String registrationNumber
    ) {
        log.info("GET /api/outpatient/registrations/ticket/{} - Generate queue ticket by number",
            registrationNumber);

        QueueTicketResponse ticket = ticketService.generateQueueTicketByNumber(registrationNumber);

        return ResponseEntity.ok(ApiResponse.success(
            "Queue ticket generated successfully",
            ticket
        ));
    }
}