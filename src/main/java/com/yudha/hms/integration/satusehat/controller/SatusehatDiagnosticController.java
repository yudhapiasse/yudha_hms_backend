package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.service.ClinicalResourceService;
import com.yudha.hms.integration.satusehat.service.DiagnosticResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API controller for SATUSEHAT diagnostic & imaging resource operations.
 *
 * Provides endpoints for:
 * - DiagnosticReport (lab results, imaging reports, pathology reports)
 * - DocumentReference (clinical documents, PDFs, images)
 * - ServiceRequest (laboratory, imaging, and referral requests)
 * - Specimen (laboratory specimen tracking)
 * - ImagingStudy (radiology and diagnostic imaging studies)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/diagnostic")
@RequiredArgsConstructor
public class SatusehatDiagnosticController {

    private final DiagnosticResourceService diagnosticResourceService;

    // ========================================================================
    // DIAGNOSTIC REPORT ENDPOINTS
    // ========================================================================

    /**
     * Create a diagnostic report in SATUSEHAT.
     *
     * POST /api/v1/satusehat/diagnostic/report
     */
    @PostMapping("/report")
    public ResponseEntity<DiagnosticReport> createDiagnosticReport(
        @RequestBody DiagnosticReport diagnosticReport,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create diagnostic report request for organization: {}", organizationId);

        try {
            DiagnosticReport createdReport = diagnosticResourceService.createDiagnosticReport(
                organizationId,
                diagnosticReport,
                userId
            );

            return ResponseEntity.ok(createdReport);

        } catch (Exception e) {
            log.error("Failed to create diagnostic report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a diagnostic report in SATUSEHAT.
     *
     * PUT /api/v1/satusehat/diagnostic/report/{reportId}
     */
    @PutMapping("/report/{reportId}")
    public ResponseEntity<DiagnosticReport> updateDiagnosticReport(
        @PathVariable String reportId,
        @RequestBody DiagnosticReport diagnosticReport,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update diagnostic report request for ID: {}", reportId);

        try {
            DiagnosticReport updatedReport = diagnosticResourceService.updateDiagnosticReport(
                organizationId,
                reportId,
                diagnosticReport,
                userId
            );

            return ResponseEntity.ok(updatedReport);

        } catch (Exception e) {
            log.error("Failed to update diagnostic report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get diagnostic report by ID.
     *
     * GET /api/v1/satusehat/diagnostic/report/{reportId}
     */
    @GetMapping("/report/{reportId}")
    public ResponseEntity<DiagnosticReport> getDiagnosticReport(
        @PathVariable String reportId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get diagnostic report request for ID: {}", reportId);

        try {
            DiagnosticReport report = diagnosticResourceService.getDiagnosticReportById(
                organizationId,
                reportId,
                userId
            );

            return ResponseEntity.ok(report);

        } catch (Exception e) {
            log.error("Failed to get diagnostic report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search diagnostic reports by patient.
     *
     * GET /api/v1/satusehat/diagnostic/report/patient/{ihsNumber}
     */
    @GetMapping("/report/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DiagnosticReport>> searchDiagnosticReportsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search diagnostic reports for patient: {}", ihsNumber);

        try {
            ClinicalResourceService.SearchBundle<DiagnosticReport> bundle =
                diagnosticResourceService.searchDiagnosticReportsByPatient(organizationId, ihsNumber, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search diagnostic reports: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search diagnostic reports by encounter.
     *
     * GET /api/v1/satusehat/diagnostic/report/encounter/{encounterId}
     */
    @GetMapping("/report/encounter/{encounterId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DiagnosticReport>> searchDiagnosticReportsByEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search diagnostic reports for encounter: {}", encounterId);

        try {
            ClinicalResourceService.SearchBundle<DiagnosticReport> bundle =
                diagnosticResourceService.searchDiagnosticReportsByEncounter(organizationId, encounterId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search diagnostic reports: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search diagnostic reports by category.
     *
     * GET /api/v1/satusehat/diagnostic/report/category/{category}
     */
    @GetMapping("/report/category/{category}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DiagnosticReport>> searchDiagnosticReportsByCategory(
        @PathVariable String category,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search diagnostic reports by category: {}", category);

        try {
            ClinicalResourceService.SearchBundle<DiagnosticReport> bundle =
                diagnosticResourceService.searchDiagnosticReportsByCategory(organizationId, category, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search diagnostic reports: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search diagnostic reports by status.
     *
     * GET /api/v1/satusehat/diagnostic/report/status/{status}
     */
    @GetMapping("/report/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DiagnosticReport>> searchDiagnosticReportsByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search diagnostic reports by status: {}", status);

        try {
            ClinicalResourceService.SearchBundle<DiagnosticReport> bundle =
                diagnosticResourceService.searchDiagnosticReportsByStatus(organizationId, status, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search diagnostic reports: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // DOCUMENT REFERENCE ENDPOINTS
    // ========================================================================

    /**
     * Create a document reference in SATUSEHAT.
     *
     * POST /api/v1/satusehat/diagnostic/document
     */
    @PostMapping("/document")
    public ResponseEntity<DocumentReference> createDocumentReference(
        @RequestBody DocumentReference documentReference,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create document reference request for organization: {}", organizationId);

        try {
            DocumentReference createdDocument = diagnosticResourceService.createDocumentReference(
                organizationId,
                documentReference,
                userId
            );

            return ResponseEntity.ok(createdDocument);

        } catch (Exception e) {
            log.error("Failed to create document reference: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a document reference in SATUSEHAT.
     *
     * PUT /api/v1/satusehat/diagnostic/document/{documentId}
     */
    @PutMapping("/document/{documentId}")
    public ResponseEntity<DocumentReference> updateDocumentReference(
        @PathVariable String documentId,
        @RequestBody DocumentReference documentReference,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update document reference request for ID: {}", documentId);

        try {
            DocumentReference updatedDocument = diagnosticResourceService.updateDocumentReference(
                organizationId,
                documentId,
                documentReference,
                userId
            );

            return ResponseEntity.ok(updatedDocument);

        } catch (Exception e) {
            log.error("Failed to update document reference: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get document reference by ID.
     *
     * GET /api/v1/satusehat/diagnostic/document/{documentId}
     */
    @GetMapping("/document/{documentId}")
    public ResponseEntity<DocumentReference> getDocumentReference(
        @PathVariable String documentId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get document reference request for ID: {}", documentId);

        try {
            DocumentReference document = diagnosticResourceService.getDocumentReferenceById(
                organizationId,
                documentId,
                userId
            );

            return ResponseEntity.ok(document);

        } catch (Exception e) {
            log.error("Failed to get document reference: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search document references by patient.
     *
     * GET /api/v1/satusehat/diagnostic/document/patient/{ihsNumber}
     */
    @GetMapping("/document/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DocumentReference>> searchDocumentReferencesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search document references for patient: {}", ihsNumber);

        try {
            ClinicalResourceService.SearchBundle<DocumentReference> bundle =
                diagnosticResourceService.searchDocumentReferencesByPatient(organizationId, ihsNumber, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search document references: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search document references by encounter.
     *
     * GET /api/v1/satusehat/diagnostic/document/encounter/{encounterId}
     */
    @GetMapping("/document/encounter/{encounterId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DocumentReference>> searchDocumentReferencesByEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search document references for encounter: {}", encounterId);

        try {
            ClinicalResourceService.SearchBundle<DocumentReference> bundle =
                diagnosticResourceService.searchDocumentReferencesByEncounter(organizationId, encounterId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search document references: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search document references by type.
     *
     * GET /api/v1/satusehat/diagnostic/document/type/{typeCode}
     */
    @GetMapping("/document/type/{typeCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DocumentReference>> searchDocumentReferencesByType(
        @PathVariable String typeCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search document references by type: {}", typeCode);

        try {
            ClinicalResourceService.SearchBundle<DocumentReference> bundle =
                diagnosticResourceService.searchDocumentReferencesByType(organizationId, typeCode, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search document references: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search document references by category.
     *
     * GET /api/v1/satusehat/diagnostic/document/category/{category}
     */
    @GetMapping("/document/category/{category}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DocumentReference>> searchDocumentReferencesByCategory(
        @PathVariable String category,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search document references by category: {}", category);

        try {
            ClinicalResourceService.SearchBundle<DocumentReference> bundle =
                diagnosticResourceService.searchDocumentReferencesByCategory(organizationId, category, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search document references: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search document references by status.
     *
     * GET /api/v1/satusehat/diagnostic/document/status/{status}
     */
    @GetMapping("/document/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<DocumentReference>> searchDocumentReferencesByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search document references by status: {}", status);

        try {
            ClinicalResourceService.SearchBundle<DocumentReference> bundle =
                diagnosticResourceService.searchDocumentReferencesByStatus(organizationId, status, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search document references: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // SERVICE REQUEST ENDPOINTS
    // ========================================================================

    /**
     * Create a service request (lab, imaging, referral).
     *
     * POST /api/v1/satusehat/diagnostic/service-request
     */
    @PostMapping("/service-request")
    public ResponseEntity<ServiceRequest> createServiceRequest(
        @RequestBody ServiceRequest serviceRequest,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create ServiceRequest for organization: {}", organizationId);
        ServiceRequest created = diagnosticResourceService.createServiceRequest(organizationId, serviceRequest, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update a service request.
     *
     * PUT /api/v1/satusehat/diagnostic/service-request/{serviceRequestId}
     */
    @PutMapping("/service-request/{serviceRequestId}")
    public ResponseEntity<ServiceRequest> updateServiceRequest(
        @PathVariable String serviceRequestId,
        @RequestBody ServiceRequest serviceRequest,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update ServiceRequest: {}", serviceRequestId);
        ServiceRequest updated = diagnosticResourceService.updateServiceRequest(organizationId, serviceRequestId, serviceRequest, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get service request by ID.
     *
     * GET /api/v1/satusehat/diagnostic/service-request/{serviceRequestId}
     */
    @GetMapping("/service-request/{serviceRequestId}")
    public ResponseEntity<ServiceRequest> getServiceRequestById(
        @PathVariable String serviceRequestId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get ServiceRequest: {}", serviceRequestId);
        ServiceRequest serviceRequest = diagnosticResourceService.getServiceRequestById(organizationId, serviceRequestId, userId);
        return ResponseEntity.ok(serviceRequest);
    }

    /**
     * Search service requests by patient.
     *
     * GET /api/v1/satusehat/diagnostic/service-request/patient/{ihsNumber}
     */
    @GetMapping("/service-request/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ServiceRequest>> searchServiceRequestsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ServiceRequests for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<ServiceRequest> results =
            diagnosticResourceService.searchServiceRequestsByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search service requests by encounter.
     *
     * GET /api/v1/satusehat/diagnostic/service-request/encounter/{encounterId}
     */
    @GetMapping("/service-request/encounter/{encounterId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ServiceRequest>> searchServiceRequestsByEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ServiceRequests for encounter: {}", encounterId);
        ClinicalResourceService.SearchBundle<ServiceRequest> results =
            diagnosticResourceService.searchServiceRequestsByEncounter(organizationId, encounterId, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search service requests by status.
     *
     * GET /api/v1/satusehat/diagnostic/service-request/status/{status}
     */
    @GetMapping("/service-request/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ServiceRequest>> searchServiceRequestsByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ServiceRequests by status: {}", status);
        ClinicalResourceService.SearchBundle<ServiceRequest> results =
            diagnosticResourceService.searchServiceRequestsByStatus(organizationId, status, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search service requests by category.
     *
     * GET /api/v1/satusehat/diagnostic/service-request/category/{categoryCode}
     */
    @GetMapping("/service-request/category/{categoryCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ServiceRequest>> searchServiceRequestsByCategory(
        @PathVariable String categoryCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ServiceRequests by category: {}", categoryCode);
        ClinicalResourceService.SearchBundle<ServiceRequest> results =
            diagnosticResourceService.searchServiceRequestsByCategory(organizationId, categoryCode, userId);
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // SPECIMEN ENDPOINTS
    // ========================================================================

    /**
     * Create a specimen record.
     *
     * POST /api/v1/satusehat/diagnostic/specimen
     */
    @PostMapping("/specimen")
    public ResponseEntity<Specimen> createSpecimen(
        @RequestBody Specimen specimen,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create Specimen for organization: {}", organizationId);
        Specimen created = diagnosticResourceService.createSpecimen(organizationId, specimen, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update a specimen record.
     *
     * PUT /api/v1/satusehat/diagnostic/specimen/{specimenId}
     */
    @PutMapping("/specimen/{specimenId}")
    public ResponseEntity<Specimen> updateSpecimen(
        @PathVariable String specimenId,
        @RequestBody Specimen specimen,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update Specimen: {}", specimenId);
        Specimen updated = diagnosticResourceService.updateSpecimen(organizationId, specimenId, specimen, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get specimen by ID.
     *
     * GET /api/v1/satusehat/diagnostic/specimen/{specimenId}
     */
    @GetMapping("/specimen/{specimenId}")
    public ResponseEntity<Specimen> getSpecimenById(
        @PathVariable String specimenId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get Specimen: {}", specimenId);
        Specimen specimen = diagnosticResourceService.getSpecimenById(organizationId, specimenId, userId);
        return ResponseEntity.ok(specimen);
    }

    /**
     * Search specimens by patient.
     *
     * GET /api/v1/satusehat/diagnostic/specimen/patient/{ihsNumber}
     */
    @GetMapping("/specimen/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Specimen>> searchSpecimensByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Specimens for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<Specimen> results =
            diagnosticResourceService.searchSpecimensByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search specimens by service request.
     *
     * GET /api/v1/satusehat/diagnostic/specimen/service-request/{serviceRequestId}
     */
    @GetMapping("/specimen/service-request/{serviceRequestId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Specimen>> searchSpecimensByServiceRequest(
        @PathVariable String serviceRequestId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Specimens for service request: {}", serviceRequestId);
        ClinicalResourceService.SearchBundle<Specimen> results =
            diagnosticResourceService.searchSpecimensByServiceRequest(organizationId, serviceRequestId, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search specimens by type.
     *
     * GET /api/v1/satusehat/diagnostic/specimen/type/{typeCode}
     */
    @GetMapping("/specimen/type/{typeCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Specimen>> searchSpecimensByType(
        @PathVariable String typeCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Specimens by type: {}", typeCode);
        ClinicalResourceService.SearchBundle<Specimen> results =
            diagnosticResourceService.searchSpecimensByType(organizationId, typeCode, userId);
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // IMAGING STUDY ENDPOINTS
    // ========================================================================

    /**
     * Create an imaging study record.
     *
     * POST /api/v1/satusehat/diagnostic/imaging-study
     */
    @PostMapping("/imaging-study")
    public ResponseEntity<ImagingStudy> createImagingStudy(
        @RequestBody ImagingStudy imagingStudy,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create ImagingStudy for organization: {}", organizationId);
        ImagingStudy created = diagnosticResourceService.createImagingStudy(organizationId, imagingStudy, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an imaging study record.
     *
     * PUT /api/v1/satusehat/diagnostic/imaging-study/{imagingStudyId}
     */
    @PutMapping("/imaging-study/{imagingStudyId}")
    public ResponseEntity<ImagingStudy> updateImagingStudy(
        @PathVariable String imagingStudyId,
        @RequestBody ImagingStudy imagingStudy,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update ImagingStudy: {}", imagingStudyId);
        ImagingStudy updated = diagnosticResourceService.updateImagingStudy(organizationId, imagingStudyId, imagingStudy, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get imaging study by ID.
     *
     * GET /api/v1/satusehat/diagnostic/imaging-study/{imagingStudyId}
     */
    @GetMapping("/imaging-study/{imagingStudyId}")
    public ResponseEntity<ImagingStudy> getImagingStudyById(
        @PathVariable String imagingStudyId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get ImagingStudy: {}", imagingStudyId);
        ImagingStudy imagingStudy = diagnosticResourceService.getImagingStudyById(organizationId, imagingStudyId, userId);
        return ResponseEntity.ok(imagingStudy);
    }

    /**
     * Search imaging studies by patient.
     *
     * GET /api/v1/satusehat/diagnostic/imaging-study/patient/{ihsNumber}
     */
    @GetMapping("/imaging-study/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ImagingStudy>> searchImagingStudiesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ImagingStudies for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<ImagingStudy> results =
            diagnosticResourceService.searchImagingStudiesByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search imaging studies by encounter.
     *
     * GET /api/v1/satusehat/diagnostic/imaging-study/encounter/{encounterId}
     */
    @GetMapping("/imaging-study/encounter/{encounterId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ImagingStudy>> searchImagingStudiesByEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ImagingStudies for encounter: {}", encounterId);
        ClinicalResourceService.SearchBundle<ImagingStudy> results =
            diagnosticResourceService.searchImagingStudiesByEncounter(organizationId, encounterId, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search imaging studies by modality.
     *
     * GET /api/v1/satusehat/diagnostic/imaging-study/modality/{modalityCode}
     */
    @GetMapping("/imaging-study/modality/{modalityCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ImagingStudy>> searchImagingStudiesByModality(
        @PathVariable String modalityCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ImagingStudies by modality: {}", modalityCode);
        ClinicalResourceService.SearchBundle<ImagingStudy> results =
            diagnosticResourceService.searchImagingStudiesByModality(organizationId, modalityCode, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search imaging studies by accession number.
     *
     * GET /api/v1/satusehat/diagnostic/imaging-study/accession/{accessionNumber}
     */
    @GetMapping("/imaging-study/accession/{accessionNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ImagingStudy>> searchImagingStudiesByAccession(
        @PathVariable String accessionNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ImagingStudies by accession: {}", accessionNumber);
        ClinicalResourceService.SearchBundle<ImagingStudy> results =
            diagnosticResourceService.searchImagingStudiesByAccession(organizationId, accessionNumber, userId);
        return ResponseEntity.ok(results);
    }
}
