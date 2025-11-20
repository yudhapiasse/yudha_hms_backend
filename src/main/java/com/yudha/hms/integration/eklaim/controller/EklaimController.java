package com.yudha.hms.integration.eklaim.controller;

import com.yudha.hms.integration.eklaim.dto.*;
import com.yudha.hms.integration.eklaim.entity.EklaimClaim;
import com.yudha.hms.integration.eklaim.service.EklaimClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

/**
 * E-Klaim REST Controller.
 *
 * Provides RESTful API endpoints for E-Klaim operations.
 * All endpoints require JWT authentication.
 *
 * API Groups:
 * - Claim Management (POST /claims, PUT /claims/{id}, GET /claims/{id}, DELETE /claims/{id})
 * - Clinical Data (POST /claims/{id}/diagnoses, POST /claims/{id}/procedures)
 * - Grouping (POST /claims/{id}/grouper)
 * - Finalization (POST /claims/{id}/finalize, POST /claims/{id}/submit)
 * - Monitoring (GET /claims/{id}/status, GET /monitoring)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@RestController
@RequestMapping("/api/v1/eklaim")
@RequiredArgsConstructor
public class EklaimController {

    private final EklaimClaimService claimService;

    /**
     * 1. Create new claim from SEP.
     *
     * POST /api/v1/eklaim/claims
     */
    @PostMapping("/claims")
    public ResponseEntity<EklaimClaim> createClaim(
        @Valid @RequestBody NewClaimRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        EklaimClaim claim = claimService.newClaim(
            request.getData().getNomorSep(),
            request.getData().getHospitalCode(),
            userId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(claim);
    }

    /**
     * 2. Set claim data.
     *
     * PUT /api/v1/eklaim/claims/{claimNumber}/data
     */
    @PutMapping("/claims/{claimNumber}/data")
    public ResponseEntity<Void> setClaimData(
        @PathVariable String claimNumber,
        @Valid @RequestBody ClaimDataRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        claimService.setClaimData(claimNumber, request, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 10. Set diagnoses.
     *
     * POST /api/v1/eklaim/claims/{claimNumber}/diagnoses
     */
    @PostMapping("/claims/{claimNumber}/diagnoses")
    public ResponseEntity<Void> setDiagnoses(
        @PathVariable String claimNumber,
        @Valid @RequestBody DiagnosisRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        claimService.setDiagnoses(claimNumber, request, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 11. Set procedures.
     *
     * POST /api/v1/eklaim/claims/{claimNumber}/procedures
     */
    @PostMapping("/claims/{claimNumber}/procedures")
    public ResponseEntity<Void> setProcedures(
        @PathVariable String claimNumber,
        @Valid @RequestBody ProcedureRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        claimService.setProcedures(claimNumber, request, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 12-14. Execute grouper (iDRG or INACBG).
     *
     * POST /api/v1/eklaim/claims/{claimNumber}/grouper
     */
    @PostMapping("/claims/{claimNumber}/grouper")
    public ResponseEntity<GrouperResponse> executeGrouper(
        @PathVariable String claimNumber,
        @Valid @RequestBody GrouperRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        GrouperResponse result = claimService.executeGrouper(
            claimNumber,
            request.getData().getGrouperType(),
            userId
        );
        return ResponseEntity.ok(result);
    }

    // TODO: Implement remaining endpoints:
    // GET /claims/{claimNumber} - get_claim_data
    // DELETE /claims/{claimNumber} - delete_claim
    // POST /claims/{claimNumber}/reedit - reedit_claim
    // POST /claims/{claimNumber}/grouper/finalize - grouper_final
    // GET /special-cmg/{cbgCode} - special_cmg_option
    // POST /claims/{claimNumber}/prosthesis - claim_prosthesis
    // POST /claims/{claimNumber}/finalize - claim_final
    // POST /claims/{claimNumber}/submit - send_claim_individual
    // POST /claims/{claimNumber}/reconsider - send_claim_reconsider
    // GET /claims/{claimNumber}/print - claim_print
    // GET /claims/{identifier}/status - get_claim_status
    // GET /monitoring - monitoring_klaim
    // GET /plafon - get_data_plafon
    // POST /batch-payment - claim_ba
    // GET /pantauan-jkn - pantauan_jkn
    // GET /sitb/{nomorSep}/tasks - sitb_list_task
    // POST /claims/{claimNumber}/sitb/apply - sitb_apply_task
    // POST /claims/{claimNumber}/sitb/finish - sitb_finish_task
}
