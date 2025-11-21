package com.yudha.hms.pharmacy.controller;

import com.yudha.hms.pharmacy.dto.*;
import com.yudha.hms.pharmacy.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Prescription Controller.
 *
 * REST API endpoints for e-Prescribing system including:
 * - Prescription creation and management
 * - Verification workflow
 * - Dispensing workflow
 * - Prescription history
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    /**
     * Create a new prescription (draft)
     *
     * @param request Prescription creation request
     * @param doctorId Doctor ID (from authentication context)
     * @param doctorName Doctor name (from authentication context)
     * @return Created prescription
     */
    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request,
            @RequestHeader("X-User-Id") UUID doctorId,
            @RequestHeader("X-User-Name") String doctorName) {

        PrescriptionResponse response = prescriptionService.createPrescription(request, doctorId, doctorName);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Submit prescription for verification
     *
     * @param id Prescription ID
     * @return Updated prescription
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<PrescriptionResponse> submitPrescription(@PathVariable UUID id) {
        PrescriptionResponse response = prescriptionService.submitPrescription(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify prescription (pharmacist)
     *
     * @param id Prescription ID
     * @param request Verification details
     * @param pharmacistId Pharmacist ID (from authentication context)
     * @param pharmacistName Pharmacist name (from authentication context)
     * @return Updated prescription
     */
    @PostMapping("/{id}/verify")
    public ResponseEntity<PrescriptionResponse> verifyPrescription(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyPrescriptionRequest request,
            @RequestHeader("X-User-Id") UUID pharmacistId,
            @RequestHeader("X-User-Name") String pharmacistName) {

        PrescriptionResponse response = prescriptionService.verifyPrescription(
                id, request, pharmacistId, pharmacistName);
        return ResponseEntity.ok(response);
    }

    /**
     * Dispense prescription items
     *
     * @param id Prescription ID
     * @param itemQuantities Map of item ID to quantity dispensed
     * @param pharmacistId Pharmacist ID (from authentication context)
     * @param pharmacistName Pharmacist name (from authentication context)
     * @return Updated prescription
     */
    @PostMapping("/{id}/dispense")
    public ResponseEntity<PrescriptionResponse> dispensePrescription(
            @PathVariable UUID id,
            @RequestBody Map<UUID, BigDecimal> itemQuantities,
            @RequestHeader("X-User-Id") UUID pharmacistId,
            @RequestHeader("X-User-Name") String pharmacistName) {

        PrescriptionResponse response = prescriptionService.dispensePrescription(
                id, itemQuantities, pharmacistId, pharmacistName);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel prescription
     *
     * @param id Prescription ID
     * @param reason Cancellation reason
     * @return Updated prescription
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PrescriptionResponse> cancelPrescription(
            @PathVariable UUID id,
            @RequestParam String reason) {

        PrescriptionResponse response = prescriptionService.cancelPrescription(id, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Get prescription by ID
     *
     * @param id Prescription ID
     * @return Prescription details
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(@PathVariable UUID id) {
        PrescriptionResponse response = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get prescription by prescription number
     *
     * @param number Prescription number
     * @return Prescription details
     */
    @GetMapping("/number/{number}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionByNumber(@PathVariable String number) {
        PrescriptionResponse response = prescriptionService.getPrescriptionByNumber(number);
        return ResponseEntity.ok(response);
    }

    /**
     * Get prescriptions for patient
     *
     * @param patientId Patient ID
     * @param pageable Pagination parameters
     * @return List of prescriptions
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsForPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20, sort = "prescriptionDate", direction = Sort.Direction.DESC) Pageable pageable) {

        List<PrescriptionResponse> responses = prescriptionService.getPrescriptionsForPatient(patientId, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get prescriptions for doctor
     *
     * @param doctorId Doctor ID
     * @param pageable Pagination parameters
     * @return List of prescriptions
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsForDoctor(
            @PathVariable UUID doctorId,
            @PageableDefault(size = 20, sort = "prescriptionDate", direction = Sort.Direction.DESC) Pageable pageable) {

        List<PrescriptionResponse> responses = prescriptionService.getPrescriptionsForDoctor(doctorId, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get pending verifications (for pharmacists)
     *
     * @return List of prescriptions awaiting verification
     */
    @GetMapping("/pending-verification")
    public ResponseEntity<List<PrescriptionResponse>> getPendingVerifications() {
        List<PrescriptionResponse> responses = prescriptionService.getPendingVerifications();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get verified prescriptions ready to dispense
     *
     * @return List of verified prescriptions
     */
    @GetMapping("/ready-to-dispense")
    public ResponseEntity<List<PrescriptionResponse>> getReadyToDispense() {
        List<PrescriptionResponse> responses = prescriptionService.getVerifiedNotDispensed();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get prescription history for patient
     *
     * @param patientId Patient ID
     * @param startDate Start date (optional)
     * @param endDate End date (optional)
     * @return List of prescriptions
     */
    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionHistory(
            @PathVariable UUID patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to last 90 days if not specified
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(90);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<PrescriptionResponse> responses = prescriptionService.getPrescriptionHistory(patientId, start, end);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get controlled drug prescriptions (for regulatory reporting)
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of controlled drug prescriptions
     */
    @GetMapping("/controlled-drugs")
    public ResponseEntity<List<PrescriptionResponse>> getControlledDrugPrescriptions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<PrescriptionResponse> responses = prescriptionService.getControlledDrugPrescriptions(startDate, endDate);
        return ResponseEntity.ok(responses);
    }
}
