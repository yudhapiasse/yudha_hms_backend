package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing Diagnostic & Imaging FHIR resources in SATUSEHAT.
 *
 * Handles:
 * - DiagnosticReport CRUD operations and search (lab results, imaging reports, etc.)
 * - DocumentReference CRUD operations and search (clinical documents, PDFs, images)
 * - ServiceRequest CRUD operations and search (lab, imaging, and referral requests)
 * - Specimen CRUD operations and search (laboratory specimen tracking)
 * - ImagingStudy CRUD operations and search (radiology and diagnostic imaging studies)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosticResourceService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    // ========================================================================
    // DIAGNOSTIC REPORT OPERATIONS
    // ========================================================================

    /**
     * Create a diagnostic report in SATUSEHAT.
     */
    public DiagnosticReport createDiagnosticReport(
        String organizationId,
        DiagnosticReport diagnosticReport,
        UUID userId
    ) {
        log.info("Creating diagnostic report in SATUSEHAT for organization: {}", organizationId);

        if (diagnosticReport.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }

        if (diagnosticReport.getCode() == null) {
            throw new SatusehatValidationException("Code is required");
        }

        var config = authService.getActiveConfig(organizationId);

        DiagnosticReport createdReport = httpClient.post(
            "/DiagnosticReport",
            diagnosticReport,
            config,
            DiagnosticReport.class,
            userId
        );

        log.info("DiagnosticReport created successfully with ID: {}", createdReport.getId());
        return createdReport;
    }

    /**
     * Update a diagnostic report in SATUSEHAT.
     */
    public DiagnosticReport updateDiagnosticReport(
        String organizationId,
        String reportId,
        DiagnosticReport diagnosticReport,
        UUID userId
    ) {
        log.info("Updating diagnostic report {} in SATUSEHAT", reportId);

        if (diagnosticReport.getId() == null) {
            diagnosticReport.setId(reportId);
        }

        var config = authService.getActiveConfig(organizationId);

        DiagnosticReport updatedReport = httpClient.put(
            "/DiagnosticReport/" + reportId,
            diagnosticReport,
            config,
            DiagnosticReport.class,
            userId
        );

        log.info("DiagnosticReport {} updated successfully", reportId);
        return updatedReport;
    }

    /**
     * Get diagnostic report by ID.
     */
    public DiagnosticReport getDiagnosticReportById(String organizationId, String reportId, UUID userId) {
        log.info("Retrieving diagnostic report {} from SATUSEHAT", reportId);

        var config = authService.getActiveConfig(organizationId);

        DiagnosticReport report = httpClient.get(
            "/DiagnosticReport/" + reportId,
            config,
            DiagnosticReport.class,
            userId
        );

        return report;
    }

    /**
     * Search diagnostic reports by patient.
     */
    public ClinicalResourceService.SearchBundle<DiagnosticReport> searchDiagnosticReportsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching diagnostic reports for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchDiagnosticReports(organizationId, params, userId);
    }

    /**
     * Search diagnostic reports by encounter.
     */
    public ClinicalResourceService.SearchBundle<DiagnosticReport> searchDiagnosticReportsByEncounter(
        String organizationId,
        String encounterId,
        UUID userId
    ) {
        log.info("Searching diagnostic reports for encounter {}", encounterId);

        Map<String, String> params = new HashMap<>();
        params.put("encounter", "Encounter/" + encounterId);

        return searchDiagnosticReports(organizationId, params, userId);
    }

    /**
     * Search diagnostic reports by category (LAB, RAD, etc.).
     */
    public ClinicalResourceService.SearchBundle<DiagnosticReport> searchDiagnosticReportsByCategory(
        String organizationId,
        String category,
        UUID userId
    ) {
        log.info("Searching diagnostic reports with category {}", category);

        Map<String, String> params = new HashMap<>();
        params.put("category", category);

        return searchDiagnosticReports(organizationId, params, userId);
    }

    /**
     * Search diagnostic reports by status.
     */
    public ClinicalResourceService.SearchBundle<DiagnosticReport> searchDiagnosticReportsByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching diagnostic reports with status {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchDiagnosticReports(organizationId, params, userId);
    }

    /**
     * Search diagnostic reports by date range.
     */
    public ClinicalResourceService.SearchBundle<DiagnosticReport> searchDiagnosticReportsByDate(
        String organizationId,
        String dateFrom,
        String dateTo,
        UUID userId
    ) {
        log.info("Searching diagnostic reports from {} to {}", dateFrom, dateTo);

        Map<String, String> params = new HashMap<>();
        if (dateFrom != null) {
            params.put("date", "ge" + dateFrom);
        }
        if (dateTo != null) {
            params.put("date", "le" + dateTo);
        }

        return searchDiagnosticReports(organizationId, params, userId);
    }

    /**
     * Generic diagnostic report search.
     */
    private ClinicalResourceService.SearchBundle<DiagnosticReport> searchDiagnosticReports(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/DiagnosticReport?");
        params.forEach((key, value) -> {
            if (queryString.length() > 18) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<DiagnosticReport> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // DOCUMENT REFERENCE OPERATIONS
    // ========================================================================

    /**
     * Create a document reference in SATUSEHAT.
     */
    public DocumentReference createDocumentReference(
        String organizationId,
        DocumentReference documentReference,
        UUID userId
    ) {
        log.info("Creating document reference in SATUSEHAT for organization: {}", organizationId);

        if (documentReference.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }

        if (documentReference.getContent() == null || documentReference.getContent().isEmpty()) {
            throw new SatusehatValidationException("Content is required");
        }

        var config = authService.getActiveConfig(organizationId);

        DocumentReference createdDocument = httpClient.post(
            "/DocumentReference",
            documentReference,
            config,
            DocumentReference.class,
            userId
        );

        log.info("DocumentReference created successfully with ID: {}", createdDocument.getId());
        return createdDocument;
    }

    /**
     * Update a document reference in SATUSEHAT.
     */
    public DocumentReference updateDocumentReference(
        String organizationId,
        String documentId,
        DocumentReference documentReference,
        UUID userId
    ) {
        log.info("Updating document reference {} in SATUSEHAT", documentId);

        if (documentReference.getId() == null) {
            documentReference.setId(documentId);
        }

        var config = authService.getActiveConfig(organizationId);

        DocumentReference updatedDocument = httpClient.put(
            "/DocumentReference/" + documentId,
            documentReference,
            config,
            DocumentReference.class,
            userId
        );

        log.info("DocumentReference {} updated successfully", documentId);
        return updatedDocument;
    }

    /**
     * Get document reference by ID.
     */
    public DocumentReference getDocumentReferenceById(String organizationId, String documentId, UUID userId) {
        log.info("Retrieving document reference {} from SATUSEHAT", documentId);

        var config = authService.getActiveConfig(organizationId);

        DocumentReference document = httpClient.get(
            "/DocumentReference/" + documentId,
            config,
            DocumentReference.class,
            userId
        );

        return document;
    }

    /**
     * Search document references by patient.
     */
    public ClinicalResourceService.SearchBundle<DocumentReference> searchDocumentReferencesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching document references for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchDocumentReferences(organizationId, params, userId);
    }

    /**
     * Search document references by encounter.
     */
    public ClinicalResourceService.SearchBundle<DocumentReference> searchDocumentReferencesByEncounter(
        String organizationId,
        String encounterId,
        UUID userId
    ) {
        log.info("Searching document references for encounter {}", encounterId);

        Map<String, String> params = new HashMap<>();
        params.put("encounter", "Encounter/" + encounterId);

        return searchDocumentReferences(organizationId, params, userId);
    }

    /**
     * Search document references by type.
     */
    public ClinicalResourceService.SearchBundle<DocumentReference> searchDocumentReferencesByType(
        String organizationId,
        String typeCode,
        UUID userId
    ) {
        log.info("Searching document references with type {}", typeCode);

        Map<String, String> params = new HashMap<>();
        params.put("type", typeCode);

        return searchDocumentReferences(organizationId, params, userId);
    }

    /**
     * Search document references by category.
     */
    public ClinicalResourceService.SearchBundle<DocumentReference> searchDocumentReferencesByCategory(
        String organizationId,
        String category,
        UUID userId
    ) {
        log.info("Searching document references with category {}", category);

        Map<String, String> params = new HashMap<>();
        params.put("category", category);

        return searchDocumentReferences(organizationId, params, userId);
    }

    /**
     * Search document references by status.
     */
    public ClinicalResourceService.SearchBundle<DocumentReference> searchDocumentReferencesByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching document references with status {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchDocumentReferences(organizationId, params, userId);
    }

    /**
     * Generic document reference search.
     */
    private ClinicalResourceService.SearchBundle<DocumentReference> searchDocumentReferences(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/DocumentReference?");
        params.forEach((key, value) -> {
            if (queryString.length() > 19) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<DocumentReference> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // SERVICE REQUEST OPERATIONS
    // ========================================================================

    /**
     * Create a service request (lab, imaging, referral).
     */
    public ServiceRequest createServiceRequest(
        String organizationId,
        ServiceRequest serviceRequest,
        UUID userId
    ) {
        log.info("Creating ServiceRequest in SATUSEHAT for organization: {}", organizationId);

        if (serviceRequest.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }
        if (serviceRequest.getCode() == null) {
            throw new SatusehatValidationException("Code (requested service) is required");
        }
        if (serviceRequest.getIntent() == null) {
            throw new SatusehatValidationException("Intent is required");
        }

        var config = authService.getActiveConfig(organizationId);

        ServiceRequest created = httpClient.post(
            "/ServiceRequest",
            serviceRequest,
            config,
            ServiceRequest.class,
            userId
        );

        log.info("ServiceRequest created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update a service request.
     */
    public ServiceRequest updateServiceRequest(
        String organizationId,
        String serviceRequestId,
        ServiceRequest serviceRequest,
        UUID userId
    ) {
        log.info("Updating ServiceRequest {} in SATUSEHAT", serviceRequestId);

        if (serviceRequest.getId() == null) {
            serviceRequest.setId(serviceRequestId);
        }

        var config = authService.getActiveConfig(organizationId);

        ServiceRequest updated = httpClient.put(
            "/ServiceRequest/" + serviceRequestId,
            serviceRequest,
            config,
            ServiceRequest.class,
            userId
        );

        log.info("ServiceRequest {} updated successfully", serviceRequestId);
        return updated;
    }

    /**
     * Get service request by ID.
     */
    public ServiceRequest getServiceRequestById(
        String organizationId,
        String serviceRequestId,
        UUID userId
    ) {
        log.info("Retrieving ServiceRequest {} from SATUSEHAT", serviceRequestId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/ServiceRequest/" + serviceRequestId,
            config,
            ServiceRequest.class,
            userId
        );
    }

    /**
     * Search service requests by patient.
     */
    public ClinicalResourceService.SearchBundle<ServiceRequest> searchServiceRequestsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching ServiceRequests for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchServiceRequests(organizationId, params, userId);
    }

    /**
     * Search service requests by encounter.
     */
    public ClinicalResourceService.SearchBundle<ServiceRequest> searchServiceRequestsByEncounter(
        String organizationId,
        String encounterId,
        UUID userId
    ) {
        log.info("Searching ServiceRequests for encounter {}", encounterId);

        Map<String, String> params = new HashMap<>();
        params.put("encounter", "Encounter/" + encounterId);

        return searchServiceRequests(organizationId, params, userId);
    }

    /**
     * Search service requests by status.
     */
    public ClinicalResourceService.SearchBundle<ServiceRequest> searchServiceRequestsByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching ServiceRequests by status {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchServiceRequests(organizationId, params, userId);
    }

    /**
     * Search service requests by category.
     */
    public ClinicalResourceService.SearchBundle<ServiceRequest> searchServiceRequestsByCategory(
        String organizationId,
        String categoryCode,
        UUID userId
    ) {
        log.info("Searching ServiceRequests by category {}", categoryCode);

        Map<String, String> params = new HashMap<>();
        params.put("category", categoryCode);

        return searchServiceRequests(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<ServiceRequest> searchServiceRequests(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/ServiceRequest?");
        params.forEach((key, value) -> {
            if (queryString.length() > 16) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<ServiceRequest> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // SPECIMEN OPERATIONS
    // ========================================================================

    /**
     * Create a specimen record.
     */
    public Specimen createSpecimen(
        String organizationId,
        Specimen specimen,
        UUID userId
    ) {
        log.info("Creating Specimen in SATUSEHAT for organization: {}", organizationId);

        if (specimen.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }
        if (specimen.getType() == null) {
            throw new SatusehatValidationException("Specimen type is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Specimen created = httpClient.post(
            "/Specimen",
            specimen,
            config,
            Specimen.class,
            userId
        );

        log.info("Specimen created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update a specimen record.
     */
    public Specimen updateSpecimen(
        String organizationId,
        String specimenId,
        Specimen specimen,
        UUID userId
    ) {
        log.info("Updating Specimen {} in SATUSEHAT", specimenId);

        if (specimen.getId() == null) {
            specimen.setId(specimenId);
        }

        var config = authService.getActiveConfig(organizationId);

        Specimen updated = httpClient.put(
            "/Specimen/" + specimenId,
            specimen,
            config,
            Specimen.class,
            userId
        );

        log.info("Specimen {} updated successfully", specimenId);
        return updated;
    }

    /**
     * Get specimen by ID.
     */
    public Specimen getSpecimenById(
        String organizationId,
        String specimenId,
        UUID userId
    ) {
        log.info("Retrieving Specimen {} from SATUSEHAT", specimenId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/Specimen/" + specimenId,
            config,
            Specimen.class,
            userId
        );
    }

    /**
     * Search specimens by patient.
     */
    public ClinicalResourceService.SearchBundle<Specimen> searchSpecimensByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching Specimens for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchSpecimens(organizationId, params, userId);
    }

    /**
     * Search specimens by service request.
     */
    public ClinicalResourceService.SearchBundle<Specimen> searchSpecimensByServiceRequest(
        String organizationId,
        String serviceRequestId,
        UUID userId
    ) {
        log.info("Searching Specimens for service request {}", serviceRequestId);

        Map<String, String> params = new HashMap<>();
        params.put("request", "ServiceRequest/" + serviceRequestId);

        return searchSpecimens(organizationId, params, userId);
    }

    /**
     * Search specimens by type.
     */
    public ClinicalResourceService.SearchBundle<Specimen> searchSpecimensByType(
        String organizationId,
        String typeCode,
        UUID userId
    ) {
        log.info("Searching Specimens by type {}", typeCode);

        Map<String, String> params = new HashMap<>();
        params.put("type", typeCode);

        return searchSpecimens(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<Specimen> searchSpecimens(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Specimen?");
        params.forEach((key, value) -> {
            if (queryString.length() > 10) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Specimen> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // IMAGING STUDY OPERATIONS
    // ========================================================================

    /**
     * Create an imaging study record.
     */
    public ImagingStudy createImagingStudy(
        String organizationId,
        ImagingStudy imagingStudy,
        UUID userId
    ) {
        log.info("Creating ImagingStudy in SATUSEHAT for organization: {}", organizationId);

        if (imagingStudy.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }
        if (imagingStudy.getStarted() == null) {
            throw new SatusehatValidationException("Started date/time is required");
        }

        var config = authService.getActiveConfig(organizationId);

        ImagingStudy created = httpClient.post(
            "/ImagingStudy",
            imagingStudy,
            config,
            ImagingStudy.class,
            userId
        );

        log.info("ImagingStudy created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update an imaging study record.
     */
    public ImagingStudy updateImagingStudy(
        String organizationId,
        String imagingStudyId,
        ImagingStudy imagingStudy,
        UUID userId
    ) {
        log.info("Updating ImagingStudy {} in SATUSEHAT", imagingStudyId);

        if (imagingStudy.getId() == null) {
            imagingStudy.setId(imagingStudyId);
        }

        var config = authService.getActiveConfig(organizationId);

        ImagingStudy updated = httpClient.put(
            "/ImagingStudy/" + imagingStudyId,
            imagingStudy,
            config,
            ImagingStudy.class,
            userId
        );

        log.info("ImagingStudy {} updated successfully", imagingStudyId);
        return updated;
    }

    /**
     * Get imaging study by ID.
     */
    public ImagingStudy getImagingStudyById(
        String organizationId,
        String imagingStudyId,
        UUID userId
    ) {
        log.info("Retrieving ImagingStudy {} from SATUSEHAT", imagingStudyId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/ImagingStudy/" + imagingStudyId,
            config,
            ImagingStudy.class,
            userId
        );
    }

    /**
     * Search imaging studies by patient.
     */
    public ClinicalResourceService.SearchBundle<ImagingStudy> searchImagingStudiesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching ImagingStudies for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchImagingStudies(organizationId, params, userId);
    }

    /**
     * Search imaging studies by encounter.
     */
    public ClinicalResourceService.SearchBundle<ImagingStudy> searchImagingStudiesByEncounter(
        String organizationId,
        String encounterId,
        UUID userId
    ) {
        log.info("Searching ImagingStudies for encounter {}", encounterId);

        Map<String, String> params = new HashMap<>();
        params.put("encounter", "Encounter/" + encounterId);

        return searchImagingStudies(organizationId, params, userId);
    }

    /**
     * Search imaging studies by modality.
     */
    public ClinicalResourceService.SearchBundle<ImagingStudy> searchImagingStudiesByModality(
        String organizationId,
        String modalityCode,
        UUID userId
    ) {
        log.info("Searching ImagingStudies by modality {}", modalityCode);

        Map<String, String> params = new HashMap<>();
        params.put("modality", modalityCode);

        return searchImagingStudies(organizationId, params, userId);
    }

    /**
     * Search imaging studies by accession number.
     */
    public ClinicalResourceService.SearchBundle<ImagingStudy> searchImagingStudiesByAccession(
        String organizationId,
        String accessionNumber,
        UUID userId
    ) {
        log.info("Searching ImagingStudies by accession number {}", accessionNumber);

        Map<String, String> params = new HashMap<>();
        params.put("identifier", accessionNumber);

        return searchImagingStudies(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<ImagingStudy> searchImagingStudies(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/ImagingStudy?");
        params.forEach((key, value) -> {
            if (queryString.length() > 14) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<ImagingStudy> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }
}
