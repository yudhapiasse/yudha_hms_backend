package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.Medication;
import com.yudha.hms.integration.satusehat.dto.fhir.MedicationDispense;
import com.yudha.hms.integration.satusehat.dto.fhir.MedicationRequest;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing Medication, MedicationRequest, and MedicationDispense FHIR resources.
 *
 * Handles:
 * - Medication catalog management (KFA codes)
 * - MedicationRequest (Prescriptions)
 * - MedicationDispense (Pharmacy dispensing events)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationResourceService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    // ========================================================================
    // MEDICATION OPERATIONS
    // ========================================================================

    /**
     * Create a medication in SATUSEHAT.
     */
    public Medication createMedication(String organizationId, Medication medication, UUID userId) {
        log.info("Creating medication in SATUSEHAT for organization: {}", organizationId);

        if (medication.getCode() == null) {
            throw new SatusehatValidationException("Medication code is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Medication createdMedication = httpClient.post(
            "/Medication",
            medication,
            config,
            Medication.class,
            userId
        );

        log.info("Medication created successfully with ID: {}", createdMedication.getId());
        return createdMedication;
    }

    /**
     * Get medication by ID.
     */
    public Medication getMedicationById(String organizationId, String medicationId, UUID userId) {
        log.info("Retrieving medication {} from SATUSEHAT", medicationId);

        var config = authService.getActiveConfig(organizationId);

        Medication medication = httpClient.get(
            "/Medication/" + medicationId,
            config,
            Medication.class,
            userId
        );

        return medication;
    }

    /**
     * Search medications by KFA code.
     */
    public ClinicalResourceService.SearchBundle<Medication> searchMedicationsByCode(
        String organizationId,
        String kfaCode,
        UUID userId
    ) {
        log.info("Searching medications with KFA code: {}", kfaCode);

        Map<String, String> params = new HashMap<>();
        params.put("code", kfaCode);

        return searchMedications(organizationId, params, userId);
    }

    /**
     * Generic medication search.
     */
    private ClinicalResourceService.SearchBundle<Medication> searchMedications(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Medication?");
        params.forEach((key, value) -> {
            if (queryString.length() > 12) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Medication> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // MEDICATION REQUEST OPERATIONS
    // ========================================================================

    /**
     * Create a medication request (prescription) in SATUSEHAT.
     */
    public MedicationRequest createMedicationRequest(
        String organizationId,
        MedicationRequest medicationRequest,
        UUID userId
    ) {
        log.info("Creating medication request in SATUSEHAT for organization: {}", organizationId);

        if (medicationRequest.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }

        if (medicationRequest.getMedicationReference() == null &&
            medicationRequest.getMedicationCodeableConcept() == null) {
            throw new SatusehatValidationException("Medication reference or code is required");
        }

        var config = authService.getActiveConfig(organizationId);

        MedicationRequest createdRequest = httpClient.post(
            "/MedicationRequest",
            medicationRequest,
            config,
            MedicationRequest.class,
            userId
        );

        log.info("MedicationRequest created successfully with ID: {}", createdRequest.getId());
        return createdRequest;
    }

    /**
     * Update a medication request in SATUSEHAT.
     */
    public MedicationRequest updateMedicationRequest(
        String organizationId,
        String requestId,
        MedicationRequest medicationRequest,
        UUID userId
    ) {
        log.info("Updating medication request {} in SATUSEHAT", requestId);

        if (medicationRequest.getId() == null) {
            medicationRequest.setId(requestId);
        }

        var config = authService.getActiveConfig(organizationId);

        MedicationRequest updatedRequest = httpClient.put(
            "/MedicationRequest/" + requestId,
            medicationRequest,
            config,
            MedicationRequest.class,
            userId
        );

        log.info("MedicationRequest {} updated successfully", requestId);
        return updatedRequest;
    }

    /**
     * Get medication request by ID.
     */
    public MedicationRequest getMedicationRequestById(
        String organizationId,
        String requestId,
        UUID userId
    ) {
        log.info("Retrieving medication request {} from SATUSEHAT", requestId);

        var config = authService.getActiveConfig(organizationId);

        MedicationRequest request = httpClient.get(
            "/MedicationRequest/" + requestId,
            config,
            MedicationRequest.class,
            userId
        );

        return request;
    }

    /**
     * Search medication requests by patient.
     */
    public ClinicalResourceService.SearchBundle<MedicationRequest> searchMedicationRequestsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching medication requests for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchMedicationRequests(organizationId, params, userId);
    }

    /**
     * Search medication requests by encounter.
     */
    public ClinicalResourceService.SearchBundle<MedicationRequest> searchMedicationRequestsByEncounter(
        String organizationId,
        String encounterId,
        UUID userId
    ) {
        log.info("Searching medication requests for encounter {}", encounterId);

        Map<String, String> params = new HashMap<>();
        params.put("encounter", "Encounter/" + encounterId);

        return searchMedicationRequests(organizationId, params, userId);
    }

    /**
     * Search medication requests by status.
     */
    public ClinicalResourceService.SearchBundle<MedicationRequest> searchMedicationRequestsByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching medication requests with status {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchMedicationRequests(organizationId, params, userId);
    }

    /**
     * Generic medication request search.
     */
    private ClinicalResourceService.SearchBundle<MedicationRequest> searchMedicationRequests(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/MedicationRequest?");
        params.forEach((key, value) -> {
            if (queryString.length() > 19) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<MedicationRequest> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // MEDICATION DISPENSE OPERATIONS
    // ========================================================================

    /**
     * Create a medication dispense event in SATUSEHAT.
     */
    public MedicationDispense createMedicationDispense(
        String organizationId,
        MedicationDispense medicationDispense,
        UUID userId
    ) {
        log.info("Creating medication dispense in SATUSEHAT for organization: {}", organizationId);

        if (medicationDispense.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }

        if (medicationDispense.getMedicationReference() == null &&
            medicationDispense.getMedicationCodeableConcept() == null) {
            throw new SatusehatValidationException("Medication reference or code is required");
        }

        var config = authService.getActiveConfig(organizationId);

        MedicationDispense createdDispense = httpClient.post(
            "/MedicationDispense",
            medicationDispense,
            config,
            MedicationDispense.class,
            userId
        );

        log.info("MedicationDispense created successfully with ID: {}", createdDispense.getId());
        return createdDispense;
    }

    /**
     * Update a medication dispense in SATUSEHAT.
     */
    public MedicationDispense updateMedicationDispense(
        String organizationId,
        String dispenseId,
        MedicationDispense medicationDispense,
        UUID userId
    ) {
        log.info("Updating medication dispense {} in SATUSEHAT", dispenseId);

        if (medicationDispense.getId() == null) {
            medicationDispense.setId(dispenseId);
        }

        var config = authService.getActiveConfig(organizationId);

        MedicationDispense updatedDispense = httpClient.put(
            "/MedicationDispense/" + dispenseId,
            medicationDispense,
            config,
            MedicationDispense.class,
            userId
        );

        log.info("MedicationDispense {} updated successfully", dispenseId);
        return updatedDispense;
    }

    /**
     * Get medication dispense by ID.
     */
    public MedicationDispense getMedicationDispenseById(
        String organizationId,
        String dispenseId,
        UUID userId
    ) {
        log.info("Retrieving medication dispense {} from SATUSEHAT", dispenseId);

        var config = authService.getActiveConfig(organizationId);

        MedicationDispense dispense = httpClient.get(
            "/MedicationDispense/" + dispenseId,
            config,
            MedicationDispense.class,
            userId
        );

        return dispense;
    }

    /**
     * Search medication dispenses by patient.
     */
    public ClinicalResourceService.SearchBundle<MedicationDispense> searchMedicationDispensesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching medication dispenses for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchMedicationDispenses(organizationId, params, userId);
    }

    /**
     * Search medication dispenses by prescription.
     */
    public ClinicalResourceService.SearchBundle<MedicationDispense> searchMedicationDispensesByPrescription(
        String organizationId,
        String medicationRequestId,
        UUID userId
    ) {
        log.info("Searching medication dispenses for prescription {}", medicationRequestId);

        Map<String, String> params = new HashMap<>();
        params.put("prescription", "MedicationRequest/" + medicationRequestId);

        return searchMedicationDispenses(organizationId, params, userId);
    }

    /**
     * Search medication dispenses by status.
     */
    public ClinicalResourceService.SearchBundle<MedicationDispense> searchMedicationDispensesByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching medication dispenses with status {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchMedicationDispenses(organizationId, params, userId);
    }

    /**
     * Generic medication dispense search.
     */
    private ClinicalResourceService.SearchBundle<MedicationDispense> searchMedicationDispenses(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/MedicationDispense?");
        params.forEach((key, value) -> {
            if (queryString.length() > 20) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<MedicationDispense> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }
}
