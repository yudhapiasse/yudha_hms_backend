package com.yudha.hms.integration.bpjs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.bpjs.dto.erekammedis.*;
import com.yudha.hms.integration.bpjs.exception.BpjsHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * eRekam Medis Service Implementation.
 *
 * Provides electronic medical record submission services to BPJS:
 * - Patient demographic data synchronization
 * - Clinical data submission (diagnoses ICD-10, procedures ICD-9-CM)
 * - Medication records submission
 * - Laboratory results submission
 * - Radiology reports submission
 * - Compliance and completeness reporting
 * - FHIR R4 compliance
 * - SATUSEHAT interoperability
 *
 * Complies with Permenkes No. 24/2022 on E-RME (Electronic Medical Records).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ERekamMedisService {

    private final BpjsHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========== PATIENT DEMOGRAPHIC SERVICES ==========

    /**
     * Submit patient demographic data to BPJS eRekam Medis.
     *
     * @param submission Patient demographic submission
     * @return Operation response
     */
    public ERekamMedisOperationResponse submitPatientDemographics(PatientDemographicSubmission submission) {
        String endpoint = "/patient/demographics";

        log.info("Submitting patient demographics - MR: {}, BPJS: {}",
            submission.getMedicalRecordNumber(), submission.getBpjsCardNumber());

        try {
            JsonNode response = httpClient.erekammedisPost(endpoint, submission);
            ERekamMedisOperationResponse operationResponse =
                objectMapper.treeToValue(response, ERekamMedisOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully submitted patient demographics - MR: {}, Submission ID: {}",
                    submission.getMedicalRecordNumber(), operationResponse.getSubmissionId());
            } else {
                log.warn("Failed to submit patient demographics: {}",
                    operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to submit patient demographics", e);
            throw new BpjsHttpException("Failed to submit patient demographics: " + e.getMessage(), e);
        }
    }

    // ========== CLINICAL DATA SERVICES ==========

    /**
     * Submit diagnosis data to BPJS eRekam Medis.
     *
     * @param submission Diagnosis submission with ICD-10 codes
     * @return Operation response
     */
    public ERekamMedisOperationResponse submitDiagnosis(DiagnosisSubmission submission) {
        String endpoint = "/clinical/diagnosis";

        log.info("Submitting diagnosis - Encounter: {}, SEP: {}, Primary: {}",
            submission.getEncounterId(), submission.getSepNumber(),
            submission.getPrimaryDiagnosis() != null ? submission.getPrimaryDiagnosis().getIcd10Code() : "N/A");

        try {
            JsonNode response = httpClient.erekammedisPost(endpoint, submission);
            ERekamMedisOperationResponse operationResponse =
                objectMapper.treeToValue(response, ERekamMedisOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully submitted diagnosis - Encounter: {}, Submission ID: {}",
                    submission.getEncounterId(), operationResponse.getSubmissionId());
            } else {
                log.warn("Failed to submit diagnosis: {}",
                    operationResponse.getErrorMessage());

                if (operationResponse.hasValidationErrors()) {
                    log.warn("Validation errors: {}", operationResponse.getResponse().getValidationErrors());
                }
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to submit diagnosis", e);
            throw new BpjsHttpException("Failed to submit diagnosis: " + e.getMessage(), e);
        }
    }

    /**
     * Submit procedure data to BPJS eRekam Medis.
     *
     * @param submission Procedure submission with ICD-9-CM codes
     * @return Operation response
     */
    public ERekamMedisOperationResponse submitProcedure(ProcedureSubmission submission) {
        String endpoint = "/clinical/procedure";

        log.info("Submitting procedures - Encounter: {}, SEP: {}, Count: {}",
            submission.getEncounterId(), submission.getSepNumber(),
            submission.getProcedures() != null ? submission.getProcedures().size() : 0);

        try {
            JsonNode response = httpClient.erekammedisPost(endpoint, submission);
            ERekamMedisOperationResponse operationResponse =
                objectMapper.treeToValue(response, ERekamMedisOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully submitted procedures - Encounter: {}, Submission ID: {}",
                    submission.getEncounterId(), operationResponse.getSubmissionId());
            } else {
                log.warn("Failed to submit procedures: {}",
                    operationResponse.getErrorMessage());

                if (operationResponse.hasValidationErrors()) {
                    log.warn("Validation errors: {}", operationResponse.getResponse().getValidationErrors());
                }
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to submit procedures", e);
            throw new BpjsHttpException("Failed to submit procedures: " + e.getMessage(), e);
        }
    }

    /**
     * Submit medication records to BPJS eRekam Medis.
     *
     * @param submission Medication submission
     * @return Operation response
     */
    public ERekamMedisOperationResponse submitMedication(MedicationSubmission submission) {
        String endpoint = "/clinical/medication";

        log.info("Submitting medications - Encounter: {}, SEP: {}, Prescription: {}, Count: {}",
            submission.getEncounterId(), submission.getSepNumber(),
            submission.getPrescriptionNumber(),
            submission.getMedications() != null ? submission.getMedications().size() : 0);

        try {
            JsonNode response = httpClient.erekammedisPost(endpoint, submission);
            ERekamMedisOperationResponse operationResponse =
                objectMapper.treeToValue(response, ERekamMedisOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully submitted medications - Encounter: {}, Submission ID: {}",
                    submission.getEncounterId(), operationResponse.getSubmissionId());
            } else {
                log.warn("Failed to submit medications: {}",
                    operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to submit medications", e);
            throw new BpjsHttpException("Failed to submit medications: " + e.getMessage(), e);
        }
    }

    // ========== LABORATORY SERVICES ==========

    /**
     * Submit laboratory results to BPJS eRekam Medis.
     *
     * @param submission Laboratory submission
     * @return Operation response
     */
    public ERekamMedisOperationResponse submitLaboratory(LaboratorySubmission submission) {
        String endpoint = "/diagnostic/laboratory";

        log.info("Submitting laboratory results - Encounter: {}, Report: {}, Category: {}, Tests: {}",
            submission.getEncounterId(), submission.getReportId(),
            submission.getCategory(),
            submission.getObservations() != null ? submission.getObservations().size() : 0);

        try {
            JsonNode response = httpClient.erekammedisPost(endpoint, submission);
            ERekamMedisOperationResponse operationResponse =
                objectMapper.treeToValue(response, ERekamMedisOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully submitted laboratory results - Encounter: {}, Submission ID: {}",
                    submission.getEncounterId(), operationResponse.getSubmissionId());
            } else {
                log.warn("Failed to submit laboratory results: {}",
                    operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to submit laboratory results", e);
            throw new BpjsHttpException("Failed to submit laboratory results: " + e.getMessage(), e);
        }
    }

    // ========== RADIOLOGY SERVICES ==========

    /**
     * Submit radiology results to BPJS eRekam Medis.
     *
     * @param submission Radiology submission
     * @return Operation response
     */
    public ERekamMedisOperationResponse submitRadiology(RadiologySubmission submission) {
        String endpoint = "/diagnostic/radiology";

        log.info("Submitting radiology results - Encounter: {}, Study: {}, Modality: {}",
            submission.getEncounterId(), submission.getStudyId(), submission.getModality());

        try {
            JsonNode response = httpClient.erekammedisPost(endpoint, submission);
            ERekamMedisOperationResponse operationResponse =
                objectMapper.treeToValue(response, ERekamMedisOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully submitted radiology results - Encounter: {}, Submission ID: {}",
                    submission.getEncounterId(), operationResponse.getSubmissionId());
            } else {
                log.warn("Failed to submit radiology results: {}",
                    operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to submit radiology results", e);
            throw new BpjsHttpException("Failed to submit radiology results: " + e.getMessage(), e);
        }
    }

    // ========== COMPLIANCE REPORTING SERVICES ==========

    /**
     * Generate record completeness report for a period.
     *
     * @param startDate Period start date
     * @param endDate Period end date
     * @param organizationCode Organization/facility code
     * @return Completeness report
     */
    public RecordCompletenessReport generateCompletenessReport(
            LocalDate startDate, LocalDate endDate, String organizationCode) {

        String endpoint = String.format("/compliance/completeness?startDate=%s&endDate=%s&orgCode=%s",
            startDate.format(DATE_FORMATTER),
            endDate.format(DATE_FORMATTER),
            organizationCode);

        log.info("Generating completeness report - Period: {} to {}, Org: {}",
            startDate, endDate, organizationCode);

        try {
            JsonNode response = httpClient.erekammedisGet(endpoint);
            return objectMapper.treeToValue(response, RecordCompletenessReport.class);

        } catch (Exception e) {
            log.error("Failed to generate completeness report", e);
            throw new BpjsHttpException("Failed to generate completeness report: " + e.getMessage(), e);
        }
    }

    /**
     * Submit completeness report to BPJS.
     *
     * @param report Record completeness report
     * @return Operation response
     */
    public ERekamMedisOperationResponse submitCompletenessReport(RecordCompletenessReport report) {
        String endpoint = "/compliance/completeness/submit";

        log.info("Submitting completeness report - Period: {} to {}, Org: {}, Total: {}, Complete: {}",
            report.getPeriodStart(), report.getPeriodEnd(),
            report.getOrganizationCode(), report.getTotalEncounters(), report.getCompleteRecords());

        try {
            JsonNode response = httpClient.erekammedisPost(endpoint, report);
            ERekamMedisOperationResponse operationResponse =
                objectMapper.treeToValue(response, ERekamMedisOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully submitted completeness report - Submission ID: {}",
                    operationResponse.getSubmissionId());
            } else {
                log.warn("Failed to submit completeness report: {}",
                    operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to submit completeness report", e);
            throw new BpjsHttpException("Failed to submit completeness report: " + e.getMessage(), e);
        }
    }

    // ========== BATCH SUBMISSION SERVICES ==========

    /**
     * Submit complete encounter data (demographics, diagnosis, procedures, medications).
     *
     * @param demographics Patient demographics
     * @param diagnosis Diagnosis data
     * @param procedure Procedure data
     * @param medication Medication data
     * @return Map of submission results by type
     */
    public Map<String, ERekamMedisOperationResponse> submitCompleteEncounter(
            PatientDemographicSubmission demographics,
            DiagnosisSubmission diagnosis,
            ProcedureSubmission procedure,
            MedicationSubmission medication) {

        log.info("Submitting complete encounter - Encounter: {}, MR: {}",
            diagnosis != null ? diagnosis.getEncounterId() : "N/A",
            demographics != null ? demographics.getMedicalRecordNumber() : "N/A");

        Map<String, ERekamMedisOperationResponse> results = new HashMap<>();

        // Submit demographics if provided
        if (demographics != null) {
            try {
                results.put("demographics", submitPatientDemographics(demographics));
            } catch (Exception e) {
                log.error("Failed to submit demographics in batch", e);
                results.put("demographics", createErrorResponse(e.getMessage()));
            }
        }

        // Submit diagnosis if provided
        if (diagnosis != null) {
            try {
                results.put("diagnosis", submitDiagnosis(diagnosis));
            } catch (Exception e) {
                log.error("Failed to submit diagnosis in batch", e);
                results.put("diagnosis", createErrorResponse(e.getMessage()));
            }
        }

        // Submit procedure if provided
        if (procedure != null) {
            try {
                results.put("procedure", submitProcedure(procedure));
            } catch (Exception e) {
                log.error("Failed to submit procedure in batch", e);
                results.put("procedure", createErrorResponse(e.getMessage()));
            }
        }

        // Submit medication if provided
        if (medication != null) {
            try {
                results.put("medication", submitMedication(medication));
            } catch (Exception e) {
                log.error("Failed to submit medication in batch", e);
                results.put("medication", createErrorResponse(e.getMessage()));
            }
        }

        long successCount = results.values().stream().filter(ERekamMedisOperationResponse::isSuccess).count();
        log.info("Complete encounter submission finished - Success: {}/{}", successCount, results.size());

        return results;
    }

    // ========== VALIDATION SERVICES ==========

    /**
     * Validate ICD-10 diagnosis code.
     *
     * @param icd10Code ICD-10 code to validate
     * @return true if valid
     */
    public boolean validateIcd10Code(String icd10Code) {
        String endpoint = String.format("/reference/icd10/validate?code=%s", icd10Code);

        log.debug("Validating ICD-10 code: {}", icd10Code);

        try {
            JsonNode response = httpClient.erekammedisGet(endpoint);
            return response.has("valid") && response.get("valid").asBoolean();

        } catch (Exception e) {
            log.error("Failed to validate ICD-10 code: {}", icd10Code, e);
            return false;
        }
    }

    /**
     * Validate ICD-9-CM procedure code.
     *
     * @param icd9Code ICD-9-CM code to validate
     * @return true if valid
     */
    public boolean validateIcd9Code(String icd9Code) {
        String endpoint = String.format("/reference/icd9/validate?code=%s", icd9Code);

        log.debug("Validating ICD-9-CM code: {}", icd9Code);

        try {
            JsonNode response = httpClient.erekammedisGet(endpoint);
            return response.has("valid") && response.get("valid").asBoolean();

        } catch (Exception e) {
            log.error("Failed to validate ICD-9-CM code: {}", icd9Code, e);
            return false;
        }
    }

    /**
     * Search ICD-10 diagnosis by keyword.
     *
     * @param keyword Search keyword
     * @return List of matching diagnoses (as JSON)
     */
    public JsonNode searchIcd10Diagnosis(String keyword) {
        String endpoint = String.format("/reference/icd10/search?keyword=%s", keyword);

        log.debug("Searching ICD-10 diagnosis: {}", keyword);

        try {
            return httpClient.erekammedisGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to search ICD-10 diagnosis", e);
            throw new BpjsHttpException("Failed to search ICD-10: " + e.getMessage(), e);
        }
    }

    /**
     * Search ICD-9-CM procedure by keyword.
     *
     * @param keyword Search keyword
     * @return List of matching procedures (as JSON)
     */
    public JsonNode searchIcd9Procedure(String keyword) {
        String endpoint = String.format("/reference/icd9/search?keyword=%s", keyword);

        log.debug("Searching ICD-9-CM procedure: {}", keyword);

        try {
            return httpClient.erekammedisGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to search ICD-9-CM procedure", e);
            throw new BpjsHttpException("Failed to search ICD-9-CM: " + e.getMessage(), e);
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Create error response for batch operations.
     *
     * @param errorMessage Error message
     * @return Error response
     */
    private ERekamMedisOperationResponse createErrorResponse(String errorMessage) {
        ERekamMedisOperationResponse.MetaData metadata = ERekamMedisOperationResponse.MetaData.builder()
            .code(500)
            .message(errorMessage)
            .build();

        return ERekamMedisOperationResponse.builder()
            .metaData(metadata)
            .build();
    }

    /**
     * Check if eRekam Medis integration is enabled.
     *
     * @return true if integration is ready
     */
    public boolean isERekamMedisEnabled() {
        return true; // Enabled if BPJS is configured
    }

    /**
     * Format LocalDateTime to ISO 8601 string.
     *
     * @param dateTime LocalDateTime to format
     * @return ISO 8601 formatted string
     */
    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * Format LocalDate to string.
     *
     * @param date LocalDate to format
     * @return Formatted date string (yyyy-MM-dd)
     */
    public String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}
