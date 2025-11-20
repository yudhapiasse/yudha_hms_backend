package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.ReferralStatus;
import com.yudha.hms.clinical.entity.ReferralType;
import com.yudha.hms.clinical.entity.ReferralUrgency;
import com.yudha.hms.clinical.service.ReferralService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Referral Controller.
 *
 * REST API endpoints for referral letter workflow management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical/referral")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReferralController {

    private final ReferralService referralService;

    // ========== CRUD Endpoints ==========

    /**
     * Create referral letter.
     *
     * POST /api/clinical/referral
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> createReferralLetter(
        @Valid @RequestBody ReferralLetterRequest request
    ) {
        log.info("REST: Creating referral letter for patient: {}", request.getPatientId());

        ReferralLetterResponse response = referralService.createReferralLetter(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Referral letter berhasil dibuat",
                response
            ));
    }

    /**
     * Get referral letter by ID.
     *
     * GET /api/clinical/referral/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> getReferralLetterById(
        @PathVariable UUID id
    ) {
        log.info("REST: Fetching referral letter: {}", id);

        ReferralLetterResponse response = referralService.getReferralLetterById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get referral letter by referral number.
     *
     * GET /api/clinical/referral/number/{referralNumber}
     */
    @GetMapping("/number/{referralNumber}")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> getReferralLetterByNumber(
        @PathVariable String referralNumber
    ) {
        log.info("REST: Fetching referral letter by number: {}", referralNumber);

        ReferralLetterResponse response = referralService.getReferralLetterByNumber(referralNumber);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get referral letter by encounter ID.
     *
     * GET /api/clinical/referral/encounter/{encounterId}
     */
    @GetMapping("/encounter/{encounterId}")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> getReferralLetterByEncounterId(
        @PathVariable UUID encounterId
    ) {
        log.info("REST: Fetching referral letter for encounter: {}", encounterId);

        ReferralLetterResponse response = referralService.getReferralLetterByEncounterId(encounterId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get referral letters by patient ID.
     *
     * GET /api/clinical/referral/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<ReferralLetterResponse>>> getReferralLettersByPatientId(
        @PathVariable UUID patientId
    ) {
        log.info("REST: Fetching referral letters for patient: {}", patientId);

        List<ReferralLetterResponse> referrals = referralService.getReferralLettersByPatientId(patientId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d referral letters", referrals.size()),
            referrals
        ));
    }

    /**
     * Update referral letter.
     *
     * PUT /api/clinical/referral/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> updateReferralLetter(
        @PathVariable UUID id,
        @Valid @RequestBody ReferralLetterRequest request
    ) {
        log.info("REST: Updating referral letter: {}", id);

        ReferralLetterResponse response = referralService.updateReferralLetter(id, request);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral letter berhasil diperbarui",
            response
        ));
    }

    // ========== Workflow Endpoints ==========

    /**
     * Sign referral letter.
     *
     * POST /api/clinical/referral/{id}/sign
     */
    @PostMapping("/{id}/sign")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> signReferralLetter(
        @PathVariable UUID id,
        @Valid @RequestBody SignReferralRequest request
    ) {
        log.info("REST: Signing referral letter: {} by: {}", id, request.getDoctorName());

        ReferralLetterResponse response = referralService.signReferralLetter(id, request);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral letter berhasil ditandatangani",
            response
        ));
    }

    /**
     * Send referral letter.
     *
     * POST /api/clinical/referral/{id}/send
     */
    @PostMapping("/{id}/send")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> sendReferralLetter(
        @PathVariable UUID id
    ) {
        log.info("REST: Sending referral letter: {}", id);

        ReferralLetterResponse response = referralService.sendReferralLetter(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral letter berhasil dikirim",
            response
        ));
    }

    /**
     * Accept referral letter.
     *
     * POST /api/clinical/referral/{id}/accept
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> acceptReferralLetter(
        @PathVariable UUID id,
        @Valid @RequestBody AcceptReferralRequest request
    ) {
        log.info("REST: Accepting referral letter: {}", id);

        ReferralLetterResponse response = referralService.acceptReferralLetter(id, request);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral letter berhasil diterima",
            response
        ));
    }

    /**
     * Reject referral letter.
     *
     * POST /api/clinical/referral/{id}/reject
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> rejectReferralLetter(
        @PathVariable UUID id,
        @Valid @RequestBody RejectReferralRequest request
    ) {
        log.info("REST: Rejecting referral letter: {}", id);

        ReferralLetterResponse response = referralService.rejectReferralLetter(id, request);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral letter ditolak",
            response
        ));
    }

    /**
     * Mark patient as transferred.
     *
     * POST /api/clinical/referral/{id}/transfer
     */
    @PostMapping("/{id}/transfer")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> markPatientTransferred(
        @PathVariable UUID id
    ) {
        log.info("REST: Marking patient as transferred for referral: {}", id);

        ReferralLetterResponse response = referralService.markPatientTransferred(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Pasien berhasil ditandai sebagai transferred",
            response
        ));
    }

    /**
     * Complete referral.
     *
     * POST /api/clinical/referral/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> completeReferral(
        @PathVariable UUID id
    ) {
        log.info("REST: Completing referral: {}", id);

        ReferralLetterResponse response = referralService.completeReferral(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral berhasil diselesaikan",
            response
        ));
    }

    /**
     * Cancel referral.
     *
     * POST /api/clinical/referral/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> cancelReferral(
        @PathVariable UUID id
    ) {
        log.info("REST: Cancelling referral: {}", id);

        ReferralLetterResponse response = referralService.cancelReferral(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral berhasil dibatalkan",
            response
        ));
    }

    // ========== Document Generation ==========

    /**
     * Generate referral document.
     *
     * POST /api/clinical/referral/{id}/generate-document
     */
    @PostMapping("/{id}/generate-document")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> generateDocument(
        @PathVariable UUID id
    ) {
        log.info("REST: Generating referral document: {}", id);

        ReferralLetterResponse response = referralService.generateDocument(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Dokumen referral berhasil dibuat",
            response
        ));
    }

    // ========== Integration Endpoints ==========

    /**
     * Submit referral to BPJS VClaim.
     *
     * POST /api/clinical/referral/{id}/vclaim
     */
    @PostMapping("/{id}/vclaim")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> submitToVClaim(
        @PathVariable UUID id,
        @RequestBody VClaimSubmissionRequest request
    ) {
        log.info("REST: Submitting referral to BPJS VClaim: {}", id);

        ReferralLetterResponse response = referralService.submitToVClaim(id, request);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral berhasil disubmit ke BPJS VClaim",
            response
        ));
    }

    /**
     * Submit referral to PCare.
     *
     * POST /api/clinical/referral/{id}/pcare
     */
    @PostMapping("/{id}/pcare")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> submitToPCare(
        @PathVariable UUID id,
        @RequestBody PCareSubmissionRequest request
    ) {
        log.info("REST: Submitting referral to PCare: {}", id);

        ReferralLetterResponse response = referralService.submitToPCare(id, request);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral berhasil disubmit ke PCare",
            response
        ));
    }

    /**
     * Submit referral to SATUSEHAT.
     *
     * POST /api/clinical/referral/{id}/satusehat
     */
    @PostMapping("/{id}/satusehat")
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> submitToSatusehat(
        @PathVariable UUID id,
        @RequestParam String referenceId
    ) {
        log.info("REST: Submitting referral to SATUSEHAT: {}", id);

        ReferralLetterResponse response = referralService.submitToSatusehat(id, referenceId);

        return ResponseEntity.ok(ApiResponse.success(
            "Referral berhasil disubmit ke SATUSEHAT",
            response
        ));
    }

    // ========== Query Endpoints ==========

    /**
     * Get referrals by status.
     *
     * GET /api/clinical/referral/by-status/{status}
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<List<ReferralLetterResponse>>> getReferralsByStatus(
        @PathVariable ReferralStatus status
    ) {
        log.info("REST: Fetching referrals by status: {}", status);

        List<ReferralLetterResponse> referrals = referralService.getReferralsByStatus(status);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d referrals with status %s", referrals.size(), status.getDisplayName()),
            referrals
        ));
    }

    /**
     * Get referrals by type.
     *
     * GET /api/clinical/referral/by-type/{type}
     */
    @GetMapping("/by-type/{type}")
    public ResponseEntity<ApiResponse<List<ReferralLetterResponse>>> getReferralsByType(
        @PathVariable ReferralType type
    ) {
        log.info("REST: Fetching referrals by type: {}", type);

        List<ReferralLetterResponse> referrals = referralService.getReferralsByType(type);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d referrals of type %s", referrals.size(), type.getDisplayName()),
            referrals
        ));
    }

    /**
     * Get referrals by urgency.
     *
     * GET /api/clinical/referral/by-urgency/{urgency}
     */
    @GetMapping("/by-urgency/{urgency}")
    public ResponseEntity<ApiResponse<List<ReferralLetterResponse>>> getReferralsByUrgency(
        @PathVariable ReferralUrgency urgency
    ) {
        log.info("REST: Fetching referrals by urgency: {}", urgency);

        List<ReferralLetterResponse> referrals = referralService.getReferralsByUrgency(urgency);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d referrals with urgency %s", referrals.size(), urgency.getDisplayName()),
            referrals
        ));
    }

    /**
     * Get pending VClaim submissions.
     *
     * GET /api/clinical/referral/pending-vclaim
     */
    @GetMapping("/pending-vclaim")
    public ResponseEntity<ApiResponse<List<ReferralLetterResponse>>> getPendingVClaimSubmissions() {
        log.info("REST: Fetching pending VClaim submissions");

        List<ReferralLetterResponse> referrals = referralService.getPendingVClaimSubmissions();

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d pending VClaim submissions", referrals.size()),
            referrals
        ));
    }

    /**
     * Get pending PCare submissions.
     *
     * GET /api/clinical/referral/pending-pcare
     */
    @GetMapping("/pending-pcare")
    public ResponseEntity<ApiResponse<List<ReferralLetterResponse>>> getPendingPCareSubmissions() {
        log.info("REST: Fetching pending PCare submissions");

        List<ReferralLetterResponse> referrals = referralService.getPendingPCareSubmissions();

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d pending PCare submissions", referrals.size()),
            referrals
        ));
    }

    /**
     * Get expired referrals.
     *
     * GET /api/clinical/referral/expired
     */
    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<List<ReferralLetterResponse>>> getExpiredReferrals() {
        log.info("REST: Fetching expired referrals");

        List<ReferralLetterResponse> referrals = referralService.getExpiredReferrals();

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d expired referrals", referrals.size()),
            referrals
        ));
    }

    /**
     * Get urgent and emergency referrals.
     *
     * GET /api/clinical/referral/urgent
     */
    @GetMapping("/urgent")
    public ResponseEntity<ApiResponse<List<ReferralLetterResponse>>> getUrgentAndEmergencyReferrals() {
        log.info("REST: Fetching urgent and emergency referrals");

        List<ReferralLetterResponse> referrals = referralService.getUrgentAndEmergencyReferrals();

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d urgent/emergency referrals", referrals.size()),
            referrals
        ));
    }

    // ========== Reference Data Endpoints ==========

    /**
     * Get all referral types.
     *
     * GET /api/clinical/referral/types
     */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getReferralTypes() {
        log.info("REST: Fetching referral types");

        List<Map<String, String>> types = Arrays.stream(ReferralType.values())
            .map(type -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("value", type.name());
                map.put("displayName", type.getDisplayName());
                map.put("indonesianName", type.getIndonesianName());
                map.put("description", type.getDescription());
                map.put("isInternal", String.valueOf(type.isInternal()));
                map.put("isExternal", String.valueOf(type.isExternal()));
                map.put("isBpjs", String.valueOf(type.isBpjs()));
                map.put("requiresVClaimIntegration", String.valueOf(type.requiresVClaimIntegration()));
                return map;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(
            "Referral types",
            types
        ));
    }

    /**
     * Get all referral statuses.
     *
     * GET /api/clinical/referral/statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReferralStatuses() {
        log.info("REST: Fetching referral statuses");

        List<Map<String, Object>> statuses = Arrays.stream(ReferralStatus.values())
            .map(status -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("value", status.name());
                map.put("displayName", status.getDisplayName());
                map.put("indonesianName", status.getIndonesianName());
                map.put("description", status.getDescription());
                map.put("order", status.getOrder());
                map.put("canBeCancelled", status.canBeCancelled());
                map.put("isPending", status.isPending());
                map.put("isCompleted", status.isCompleted());
                map.put("isTerminated", status.isTerminated());
                map.put("isActive", status.isActive());
                return map;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(
            "Referral statuses",
            statuses
        ));
    }

    /**
     * Get all urgency levels.
     *
     * GET /api/clinical/referral/urgencies
     */
    @GetMapping("/urgencies")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReferralUrgencies() {
        log.info("REST: Fetching referral urgency levels");

        List<Map<String, Object>> urgencies = Arrays.stream(ReferralUrgency.values())
            .map(urgency -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("value", urgency.name());
                map.put("displayName", urgency.getDisplayName());
                map.put("indonesianName", urgency.getIndonesianName());
                map.put("description", urgency.getDescription());
                map.put("maxDaysUntilTransfer", urgency.getMaxDaysUntilTransfer());
                map.put("isEmergency", urgency.isEmergency());
                map.put("requiresImmediateAction", urgency.requiresImmediateAction());
                return map;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(
            "Referral urgency levels",
            urgencies
        ));
    }
}
