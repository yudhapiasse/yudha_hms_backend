package com.yudha.hms.patient.controller;

import com.yudha.hms.patient.dto.PatientRequestDto;
import com.yudha.hms.patient.dto.PatientResponseDto;
import com.yudha.hms.patient.service.PatientService;
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
 * Patient REST Controller.
 *
 * Provides RESTful endpoints for patient management:
 * - Patient registration
 * - Patient retrieval (by ID, MRN, NIK)
 * - Patient search
 * - Patient updates
 * - Patient deletion (soft delete)
 *
 * All responses are wrapped in ApiResponse for consistency.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;
    private final com.yudha.hms.patient.service.BarcodeService barcodeService;

    /**
     * Register a new patient.
     *
     * POST /api/patients
     *
     * Validates and registers a new patient with:
     * - NIK validation (16 digits)
     * - BPJS number validation (13 digits)
     * - Duplicate checking
     * - MRN auto-generation (YYYYMM-XXXXX format)
     *
     * @param requestDto patient registration request
     * @return registered patient data with 201 CREATED
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponseDto>> registerPatient(
            @Valid @RequestBody PatientRequestDto requestDto) {

        log.info("POST /api/patients - Registering new patient: {}", requestDto.getFullName());

        PatientResponseDto response = patientService.registerPatient(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Pasien berhasil didaftarkan dengan MRN: " + response.getMrn(),
                    response
                ));
    }

    /**
     * Get patient by ID.
     *
     * GET /api/patients/{id}
     *
     * @param id patient UUID
     * @return patient data with 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponseDto>> getPatientById(
            @PathVariable UUID id) {

        log.info("GET /api/patients/{} - Fetching patient by ID", id);

        PatientResponseDto response = patientService.getPatientById(id);

        return ResponseEntity.ok(
            ApiResponse.success("Pasien ditemukan", response)
        );
    }

    /**
     * Get patient by MRN.
     *
     * GET /api/patients/mrn/{mrn}
     *
     * @param mrn medical record number
     * @return patient data with 200 OK
     */
    @GetMapping("/mrn/{mrn}")
    public ResponseEntity<ApiResponse<PatientResponseDto>> getPatientByMrn(
            @PathVariable String mrn) {

        log.info("GET /api/patients/mrn/{} - Fetching patient by MRN", mrn);

        PatientResponseDto response = patientService.getPatientByMrn(mrn);

        return ResponseEntity.ok(
            ApiResponse.success("Pasien ditemukan", response)
        );
    }

    /**
     * Get patient by NIK.
     *
     * GET /api/patients/nik/{nik}
     *
     * @param nik Indonesian national ID (16 digits)
     * @return patient data with 200 OK
     */
    @GetMapping("/nik/{nik}")
    public ResponseEntity<ApiResponse<PatientResponseDto>> getPatientByNik(
            @PathVariable String nik) {

        log.info("GET /api/patients/nik/{} - Fetching patient by NIK", nik);

        PatientResponseDto response = patientService.getPatientByNik(nik);

        return ResponseEntity.ok(
            ApiResponse.success("Pasien ditemukan", response)
        );
    }

    /**
     * Search patients by name.
     *
     * GET /api/patients/search?name={name}
     *
     * Case-insensitive partial match on patient full name.
     *
     * @param name name to search
     * @return list of matching patients with 200 OK
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientResponseDto>>> searchPatients(
            @RequestParam String name) {

        log.info("GET /api/patients/search?name={} - Searching patients", name);

        List<PatientResponseDto> response = patientService.searchPatientsByName(name);

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Ditemukan %d pasien dengan nama '%s'", response.size(), name),
                response
            )
        );
    }

    /**
     * Get all active patients.
     *
     * GET /api/patients/active
     *
     * Returns all patients with isActive = true.
     *
     * @return list of active patients with 200 OK
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PatientResponseDto>>> getActivePatients() {

        log.info("GET /api/patients/active - Fetching active patients");

        List<PatientResponseDto> response = patientService.getActivePatients();

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Ditemukan %d pasien aktif", response.size()),
                response
            )
        );
    }

    /**
     * Get all VIP patients.
     *
     * GET /api/patients/vip
     *
     * Returns all patients with isVip = true.
     *
     * @return list of VIP patients with 200 OK
     */
    @GetMapping("/vip")
    public ResponseEntity<ApiResponse<List<PatientResponseDto>>> getVipPatients() {

        log.info("GET /api/patients/vip - Fetching VIP patients");

        List<PatientResponseDto> response = patientService.getVipPatients();

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Ditemukan %d pasien VIP", response.size()),
                response
            )
        );
    }

    /**
     * Update patient information.
     *
     * PUT /api/patients/{id}
     *
     * Updates patient data with validation and duplicate checking.
     * MRN cannot be changed.
     *
     * @param id patient UUID
     * @param requestDto update request
     * @return updated patient data with 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponseDto>> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequestDto requestDto) {

        log.info("PUT /api/patients/{} - Updating patient", id);

        PatientResponseDto response = patientService.updatePatient(id, requestDto);

        return ResponseEntity.ok(
            ApiResponse.success("Data pasien berhasil diperbarui", response)
        );
    }

    /**
     * Soft delete patient.
     *
     * DELETE /api/patients/{id}
     *
     * Performs soft delete by setting deletedAt timestamp.
     * Patient data is not physically removed from database.
     *
     * @param id patient UUID
     * @return success message with 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @PathVariable UUID id) {

        log.info("DELETE /api/patients/{} - Soft deleting patient", id);

        patientService.deletePatient(id);

        return ResponseEntity.ok(
            ApiResponse.success("Pasien berhasil dihapus", null)
        );
    }

    // ============================================================================
    // SEARCH ENDPOINTS
    // ============================================================================

    /**
     * Advanced patient search with multiple criteria.
     *
     * POST /api/patients/search
     *
     * Supports:
     * - Quick search across multiple fields
     * - Specific field searches (MRN, NIK, BPJS, name, phone)
     * - Advanced filters (age range, dates, address, status)
     * - Pagination and sorting
     * - Configurable data depth (BASIC, DETAILED, COMPLETE)
     *
     * @param criteria search criteria
     * @return paginated search results
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<com.yudha.hms.patient.dto.PatientSearchResponse<?>>> searchPatients(
            @Valid @RequestBody com.yudha.hms.patient.dto.PatientSearchCriteria criteria) {

        log.info("POST /api/patients/search - Searching with criteria: {}", criteria);

        com.yudha.hms.patient.dto.PatientSearchResponse<?> response = patientService.searchPatients(criteria);

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Ditemukan %d pasien (halaman %d dari %d)",
                    response.getTotalRecords(),
                    response.getCurrentPage() + 1,
                    response.getTotalPages()),
                response
            )
        );
    }

    /**
     * Quick search patients by search term.
     *
     * GET /api/patients/search/quick?term={term}&page={page}&size={size}
     *
     * Searches across MRN, NIK, BPJS number, and patient name.
     * Convenient endpoint for simple searches without complex criteria.
     *
     * @param term search term
     * @param page page number (default: 0)
     * @param size page size (default: 20)
     * @return paginated search results
     */
    @GetMapping("/search/quick")
    public ResponseEntity<ApiResponse<com.yudha.hms.patient.dto.PatientSearchResponse<?>>> quickSearch(
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/patients/search/quick - term: {}, page: {}, size: {}", term, page, size);

        com.yudha.hms.patient.dto.PatientSearchResponse<?> response = patientService.quickSearch(term, page, size);

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Ditemukan %d pasien", response.getTotalRecords()),
                response
            )
        );
    }

    /**
     * Search patients by name.
     *
     * GET /api/patients/search/name?name={name}&page={page}&size={size}
     *
     * Uses full-text search for better name matching.
     * Supports partial matches and case-insensitive search.
     *
     * @param name patient name
     * @param page page number (default: 0)
     * @param size page size (default: 20)
     * @return paginated search results
     */
    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<com.yudha.hms.patient.dto.PatientSearchResponse<?>>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/patients/search/name - name: {}, page: {}, size: {}", name, page, size);

        com.yudha.hms.patient.dto.PatientSearchResponse<?> response = patientService.searchByName(name, page, size);

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Ditemukan %d pasien dengan nama '%s'", response.getTotalRecords(), name),
                response
            )
        );
    }

    // ============================================================================
    // BARCODE/QR CODE ENDPOINTS
    // ============================================================================

    /**
     * Generate QR code for patient MRN.
     *
     * GET /api/patients/{id}/qrcode
     *
     * Generates a QR code containing the patient's MRN.
     * Returns Base64 encoded PNG image that can be embedded in HTML or PDF.
     *
     * Use cases:
     * - Patient identification cards
     * - Laboratory sample labels
     * - Prescription labels
     * - Medical record documents
     *
     * @param id patient UUID
     * @return QR code data with Base64 encoded image
     */
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<ApiResponse<com.yudha.hms.patient.service.BarcodeService.PatientCardData>> generateQRCode(
            @PathVariable UUID id) {

        log.info("GET /api/patients/{}/qrcode - Generating QR code", id);

        // Get patient to retrieve MRN
        PatientResponseDto patient = patientService.getPatientById(id);

        // Generate QR code
        com.yudha.hms.patient.service.BarcodeService.PatientCardData cardData =
            barcodeService.generatePatientCardData(patient.getMrn());

        return ResponseEntity.ok(
            ApiResponse.success("QR code berhasil dibuat", cardData)
        );
    }

    /**
     * Generate QR code by MRN.
     *
     * GET /api/patients/mrn/{mrn}/qrcode
     *
     * Generates QR code directly from MRN without patient ID.
     * Useful when only MRN is available.
     *
     * @param mrn medical record number
     * @return QR code data with Base64 encoded image
     */
    @GetMapping("/mrn/{mrn}/qrcode")
    public ResponseEntity<ApiResponse<com.yudha.hms.patient.service.BarcodeService.PatientCardData>> generateQRCodeByMrn(
            @PathVariable String mrn) {

        log.info("GET /api/patients/mrn/{}/qrcode - Generating QR code", mrn);

        // Verify MRN exists
        patientService.getPatientByMrn(mrn);

        // Generate QR code
        com.yudha.hms.patient.service.BarcodeService.PatientCardData cardData =
            barcodeService.generatePatientCardData(mrn);

        return ResponseEntity.ok(
            ApiResponse.success("QR code berhasil dibuat", cardData)
        );
    }

    /**
     * Generate patient card data with barcode and QR code.
     *
     * GET /api/patients/{id}/card
     *
     * Generates complete patient card data including:
     * - QR code (Base64 encoded)
     * - Barcode (Base64 encoded)
     * - MRN
     *
     * Can be used to generate patient ID cards with embedded codes.
     *
     * @param id patient UUID
     * @return patient card data
     */
    @GetMapping("/{id}/card")
    public ResponseEntity<ApiResponse<com.yudha.hms.patient.service.BarcodeService.PatientCardData>> generatePatientCard(
            @PathVariable UUID id) {

        log.info("GET /api/patients/{}/card - Generating patient card data", id);

        // Get patient to retrieve MRN
        PatientResponseDto patient = patientService.getPatientById(id);

        // Generate card data
        com.yudha.hms.patient.service.BarcodeService.PatientCardData cardData =
            barcodeService.generatePatientCardData(patient.getMrn());

        return ResponseEntity.ok(
            ApiResponse.success("Data kartu pasien berhasil dibuat", cardData)
        );
    }
}
