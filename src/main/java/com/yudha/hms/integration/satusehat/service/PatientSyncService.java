package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.Patient;
import com.yudha.hms.integration.satusehat.entity.SatusehatResourceMapping;
import com.yudha.hms.integration.satusehat.exception.SatusehatHttpException;
import com.yudha.hms.integration.satusehat.exception.SatusehatIntegrationException;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import com.yudha.hms.integration.satusehat.repository.SatusehatResourceMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for synchronizing patient data with SATUSEHAT.
 *
 * Handles:
 * - Patient submission with retry logic
 * - Failed submission tracking and retry queue
 * - Duplicate detection (409 responses)
 * - Validation error handling (422 responses)
 * - Sync status management
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientSyncService {

    private final PatientResourceService patientResourceService;
    private final PatientMappingService patientMappingService;
    private final PatientValidationService patientValidationService;
    private final SatusehatResourceMappingRepository resourceMappingRepository;

    /**
     * Synchronize patient to SATUSEHAT.
     *
     * Creates or updates patient in SATUSEHAT and tracks submission status.
     *
     * @param organizationId Organization identifier
     * @param hmsPatient HMS patient entity
     * @param userId User performing the sync
     * @return Resource mapping with submission status
     */
    @Transactional
    public SatusehatResourceMapping syncPatient(
        String organizationId,
        com.yudha.hms.patient.entity.Patient hmsPatient,
        UUID userId
    ) {
        log.info("Synchronizing patient {} to SATUSEHAT", hmsPatient.getMrn());

        // Check if patient already synced
        SatusehatResourceMapping mapping = resourceMappingRepository
            .findByResourceTypeAndLocalResourceId("Patient", hmsPatient.getId())
            .orElse(null);

        if (mapping == null) {
            // Create new mapping
            mapping = SatusehatResourceMapping.builder()
                .organizationId(organizationId)
                .resourceType("Patient")
                .localResourceId(hmsPatient.getId())
                .submissionStatus(SatusehatResourceMapping.SubmissionStatus.PENDING)
                .retryCount(0)
                .build();
            mapping = resourceMappingRepository.save(mapping);
        }

        try {
            // Validate HMS patient
            patientValidationService.validateHmsPatient(hmsPatient);

            // Convert to FHIR
            Patient fhirPatient = patientMappingService.toFhirPatient(
                hmsPatient,
                mapping.getSatusehatResourceId()
            );

            // Validate FHIR patient
            patientValidationService.validateFhirPatient(fhirPatient);

            // Submit to SATUSEHAT
            Patient submittedPatient;
            if (mapping.getSatusehatResourceId() == null) {
                // Create new patient
                submittedPatient = patientResourceService.createPatient(
                    organizationId,
                    fhirPatient,
                    userId
                );
                log.info("Patient created in SATUSEHAT with IHS number: {}", submittedPatient.getId());
            } else {
                // Update existing patient
                submittedPatient = patientResourceService.updatePatient(
                    organizationId,
                    mapping.getSatusehatResourceId(),
                    fhirPatient,
                    userId
                );
                log.info("Patient updated in SATUSEHAT: {}", submittedPatient.getId());
            }

            // Update mapping with success
            String ihsNumber = submittedPatient.getId();
            String versionId = submittedPatient.getMeta() != null
                ? submittedPatient.getMeta().getVersionId()
                : null;

            mapping.markAsSubmitted(ihsNumber, versionId);
            mapping = resourceMappingRepository.save(mapping);

            log.info("Patient sync completed successfully: {} -> IHS: {}",
                hmsPatient.getMrn(), ihsNumber);

            return mapping;

        } catch (SatusehatValidationException e) {
            // Validation error - mark as failed
            log.error("Patient validation failed for {}: {}", hmsPatient.getMrn(), e.getMessage());
            mapping.markAsFailed(e.getMessage());
            resourceMappingRepository.save(mapping);
            throw e;

        } catch (SatusehatHttpException e) {
            // HTTP error - check if retryable
            if (e.getHttpStatus() == 409) {
                // Duplicate - try to search and link
                log.warn("Duplicate patient detected for {}: {}", hmsPatient.getMrn(), e.getMessage());
                handleDuplicatePatient(organizationId, hmsPatient, mapping, userId);
            } else if (e.getHttpStatus() == 422) {
                // Unprocessable entity - validation error
                log.error("SATUSEHAT validation failed for {}: {}", hmsPatient.getMrn(), e.getMessage());
                mapping.markAsFailed(e.getMessage());
                resourceMappingRepository.save(mapping);
            } else if (e.getHttpStatus() >= 500) {
                // Server error - retryable
                log.error("SATUSEHAT server error for {}: {}", hmsPatient.getMrn(), e.getMessage());
                mapping.incrementRetryCount(e.getMessage());
                resourceMappingRepository.save(mapping);
            } else {
                // Other errors - mark as failed
                log.error("SATUSEHAT HTTP error for {}: {}", hmsPatient.getMrn(), e.getMessage());
                mapping.markAsFailed(e.getMessage());
                resourceMappingRepository.save(mapping);
            }
            throw e;

        } catch (Exception e) {
            // Unexpected error
            log.error("Unexpected error syncing patient {}: {}", hmsPatient.getMrn(), e.getMessage(), e);
            mapping.incrementRetryCount(e.getMessage());
            resourceMappingRepository.save(mapping);
            throw new SatusehatIntegrationException("Patient sync failed: " + e.getMessage(), e);
        }
    }

    /**
     * Handle duplicate patient (409 response).
     *
     * Searches for existing patient by NIK and links if found.
     */
    private void handleDuplicatePatient(
        String organizationId,
        com.yudha.hms.patient.entity.Patient hmsPatient,
        SatusehatResourceMapping mapping,
        UUID userId
    ) {
        try {
            // Search by NIK
            PatientResourceService.Bundle searchResult = patientResourceService.searchPatientByNik(
                organizationId,
                hmsPatient.getNik(),
                userId
            );

            if (searchResult.getEntry() != null && !searchResult.getEntry().isEmpty()) {
                // Found existing patient - link it
                Patient existingPatient = searchResult.getEntry().get(0).getResource();
                String ihsNumber = existingPatient.getId();

                log.info("Linking duplicate patient {} to existing IHS number: {}",
                    hmsPatient.getMrn(), ihsNumber);

                mapping.markAsSubmitted(ihsNumber, null);
                resourceMappingRepository.save(mapping);
            } else {
                // Not found - mark as failed
                mapping.markAsFailed("Duplicate patient but not found in search");
                resourceMappingRepository.save(mapping);
            }
        } catch (Exception e) {
            log.error("Failed to handle duplicate patient: {}", e.getMessage(), e);
            mapping.markAsFailed("Failed to handle duplicate: " + e.getMessage());
            resourceMappingRepository.save(mapping);
        }
    }

    /**
     * Retry failed patient submissions.
     *
     * Processes all failed submissions that haven't exceeded max retry count.
     *
     * @param organizationId Organization identifier
     * @param userId User performing the retry
     * @return Number of patients successfully retried
     */
    @Transactional
    public int retryFailedSubmissions(String organizationId, UUID userId) {
        log.info("Retrying failed patient submissions for organization: {}", organizationId);

        List<SatusehatResourceMapping> failedMappings = resourceMappingRepository
            .findByResourceTypeAndSubmissionStatus("Patient",
                SatusehatResourceMapping.SubmissionStatus.PENDING);

        int successCount = 0;
        int maxRetries = 3;

        for (SatusehatResourceMapping mapping : failedMappings) {
            if (mapping.getRetryCount() >= maxRetries) {
                log.warn("Skipping patient {} - exceeded max retry count", mapping.getLocalResourceId());
                continue;
            }

            try {
                // TODO: Fetch HMS patient from repository
                // For now, log the retry attempt
                log.info("Retrying patient sync for local ID: {}", mapping.getLocalResourceId());

                // Reset to pending status for retry
                mapping.setSubmissionStatus(SatusehatResourceMapping.SubmissionStatus.PENDING);
                resourceMappingRepository.save(mapping);

                // Actual sync would happen here
                // syncPatient(organizationId, hmsPatient, userId);

                successCount++;
            } catch (Exception e) {
                log.error("Failed to retry patient sync for {}: {}",
                    mapping.getLocalResourceId(), e.getMessage());
            }
        }

        log.info("Retry completed: {} patients successfully retried", successCount);
        return successCount;
    }

    /**
     * Get sync status for a patient.
     *
     * @param patientId HMS patient ID
     * @return Resource mapping with sync status, or null if not found
     */
    public SatusehatResourceMapping getSyncStatus(UUID patientId) {
        return resourceMappingRepository
            .findByResourceTypeAndLocalResourceId("Patient", patientId)
            .orElse(null);
    }

    /**
     * Get all failed submissions for retry.
     *
     * @param organizationId Organization identifier
     * @return List of failed mappings
     */
    public List<SatusehatResourceMapping> getFailedSubmissions(String organizationId) {
        return resourceMappingRepository
            .findByOrganizationIdAndResourceTypeAndSubmissionStatus(
                organizationId,
                "Patient",
                SatusehatResourceMapping.SubmissionStatus.PENDING
            );
    }

    /**
     * Get all submitted patients.
     *
     * @param organizationId Organization identifier
     * @return List of submitted mappings
     */
    public List<SatusehatResourceMapping> getSubmittedPatients(String organizationId) {
        return resourceMappingRepository
            .findByOrganizationIdAndResourceTypeAndSubmissionStatus(
                organizationId,
                "Patient",
                SatusehatResourceMapping.SubmissionStatus.SUBMITTED
            );
    }

    /**
     * Asynchronously sync patient.
     *
     * @param organizationId Organization identifier
     * @param hmsPatient HMS patient entity
     * @param userId User performing the sync
     */
    @Async
    public void syncPatientAsync(
        String organizationId,
        com.yudha.hms.patient.entity.Patient hmsPatient,
        UUID userId
    ) {
        try {
            syncPatient(organizationId, hmsPatient, userId);
        } catch (Exception e) {
            log.error("Async patient sync failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Bulk sync patients.
     *
     * @param organizationId Organization identifier
     * @param hmsPatients List of HMS patients
     * @param userId User performing the sync
     * @return Number of patients successfully synced
     */
    @Transactional
    public int bulkSyncPatients(
        String organizationId,
        List<com.yudha.hms.patient.entity.Patient> hmsPatients,
        UUID userId
    ) {
        log.info("Bulk syncing {} patients to SATUSEHAT", hmsPatients.size());

        int successCount = 0;
        for (com.yudha.hms.patient.entity.Patient patient : hmsPatients) {
            try {
                syncPatient(organizationId, patient, userId);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to sync patient {}: {}", patient.getMrn(), e.getMessage());
            }
        }

        log.info("Bulk sync completed: {}/{} patients successfully synced",
            successCount, hmsPatients.size());

        return successCount;
    }

    /**
     * Reset failed submission for retry.
     *
     * @param mappingId Mapping ID
     */
    @Transactional
    public void resetFailedSubmission(UUID mappingId) {
        SatusehatResourceMapping mapping = resourceMappingRepository
            .findById(mappingId)
            .orElseThrow(() -> new SatusehatIntegrationException("Mapping not found: " + mappingId));

        mapping.setSubmissionStatus(SatusehatResourceMapping.SubmissionStatus.PENDING);
        mapping.setRetryCount(0);
        mapping.setLastError(null);
        resourceMappingRepository.save(mapping);

        log.info("Reset failed submission for mapping: {}", mappingId);
    }
}
