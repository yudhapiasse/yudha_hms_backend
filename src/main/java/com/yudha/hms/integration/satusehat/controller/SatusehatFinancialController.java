package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.service.ClinicalResourceService;
import com.yudha.hms.integration.satusehat.service.FinancialResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for BPJS Financial and Coverage Resources.
 *
 * Provides endpoints for:
 * - Coverage (BPJS insurance cards)
 * - CoverageEligibilityRequest (SEP requests)
 * - CoverageEligibilityResponse (SEP approvals/rejections)
 * - Claim (insurance claim submissions)
 * - ClaimResponse (claim adjudication results)
 *
 * BPJS Integration Workflow:
 * 1. Patient registers with Coverage (BPJS card)
 * 2. Before treatment, request SEP via CoverageEligibilityRequest
 * 3. Receive SEP approval via CoverageEligibilityResponse
 * 4. After treatment, submit Claim with SEP reference
 * 5. Receive adjudication via ClaimResponse
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/financial")
@RequiredArgsConstructor
public class SatusehatFinancialController {

    private final FinancialResourceService financialResourceService;

    // ==================== Coverage Endpoints ====================

    /**
     * Create Coverage resource (BPJS insurance card).
     *
     * POST /api/v1/satusehat/financial/coverage
     */
    @PostMapping("/coverage")
    public ResponseEntity<Coverage> createCoverage(
        @RequestBody Coverage coverage,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create coverage for organization: {}, userId: {}", organizationId, userId);
        Coverage created = financialResourceService.createCoverage(organizationId, coverage, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update Coverage resource.
     *
     * PUT /api/v1/satusehat/financial/coverage/{coverageId}
     */
    @PutMapping("/coverage/{coverageId}")
    public ResponseEntity<Coverage> updateCoverage(
        @PathVariable String coverageId,
        @RequestBody Coverage coverage,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update coverage: {} for organization: {}, userId: {}", coverageId, organizationId, userId);
        Coverage updated = financialResourceService.updateCoverage(organizationId, coverageId, coverage, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get Coverage by ID.
     *
     * GET /api/v1/satusehat/financial/coverage/{coverageId}
     */
    @GetMapping("/coverage/{coverageId}")
    public ResponseEntity<Coverage> getCoverageById(
        @PathVariable String coverageId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get coverage: {} for organization: {}", coverageId, organizationId);
        Coverage coverage = financialResourceService.getCoverageById(organizationId, coverageId, userId);
        return ResponseEntity.ok(coverage);
    }

    /**
     * Search Coverage by patient IHS number.
     *
     * GET /api/v1/satusehat/financial/coverage/patient/{ihsNumber}
     */
    @GetMapping("/coverage/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Coverage>> searchCoveragesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search coverage by patient: {} for organization: {}", ihsNumber, organizationId);
        ClinicalResourceService.SearchBundle<Coverage> results = financialResourceService.searchCoveragesByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search Coverage by subscriber ID (BPJS card number).
     *
     * GET /api/v1/satusehat/financial/coverage/subscriber/{subscriberId}
     */
    @GetMapping("/coverage/subscriber/{subscriberId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Coverage>> searchCoveragesBySubscriberId(
        @PathVariable String subscriberId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search coverage by subscriber: {} for organization: {}", subscriberId, organizationId);
        ClinicalResourceService.SearchBundle<Coverage> results = financialResourceService.searchCoveragesBySubscriberId(organizationId, subscriberId, userId);
        return ResponseEntity.ok(results);
    }

    // ==================== CoverageEligibilityRequest Endpoints (SEP Request) ====================

    /**
     * Create CoverageEligibilityRequest (SEP request).
     *
     * POST /api/v1/satusehat/financial/eligibility-request
     */
    @PostMapping("/eligibility-request")
    public ResponseEntity<CoverageEligibilityRequest> createEligibilityRequest(
        @RequestBody CoverageEligibilityRequest request,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create eligibility request for organization: {}, userId: {}", organizationId, userId);
        CoverageEligibilityRequest created = financialResourceService.createEligibilityRequest(organizationId, request, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Get CoverageEligibilityRequest by ID.
     *
     * GET /api/v1/satusehat/financial/eligibility-request/{requestId}
     */
    @GetMapping("/eligibility-request/{requestId}")
    public ResponseEntity<CoverageEligibilityRequest> getEligibilityRequestById(
        @PathVariable String requestId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get eligibility request: {} for organization: {}", requestId, organizationId);
        CoverageEligibilityRequest request = financialResourceService.getEligibilityRequestById(organizationId, requestId, userId);
        return ResponseEntity.ok(request);
    }

    /**
     * Search CoverageEligibilityRequest by patient.
     *
     * GET /api/v1/satusehat/financial/eligibility-request/patient/{ihsNumber}
     */
    @GetMapping("/eligibility-request/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<CoverageEligibilityRequest>> searchEligibilityRequestsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search eligibility requests by patient: {} for organization: {}", ihsNumber, organizationId);
        ClinicalResourceService.SearchBundle<CoverageEligibilityRequest> results = financialResourceService.searchEligibilityRequestsByPatient(
            organizationId, ihsNumber, userId
        );
        return ResponseEntity.ok(results);
    }

    // ==================== CoverageEligibilityResponse Endpoints (SEP Response) ====================

    /**
     * Create CoverageEligibilityResponse (SEP response).
     *
     * POST /api/v1/satusehat/financial/eligibility-response
     */
    @PostMapping("/eligibility-response")
    public ResponseEntity<CoverageEligibilityResponse> createEligibilityResponse(
        @RequestBody CoverageEligibilityResponse response,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create eligibility response for organization: {}, userId: {}", organizationId, userId);
        CoverageEligibilityResponse created = financialResourceService.createEligibilityResponse(organizationId, response, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Get CoverageEligibilityResponse by ID.
     *
     * GET /api/v1/satusehat/financial/eligibility-response/{responseId}
     */
    @GetMapping("/eligibility-response/{responseId}")
    public ResponseEntity<CoverageEligibilityResponse> getEligibilityResponseById(
        @PathVariable String responseId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get eligibility response: {} for organization: {}", responseId, organizationId);
        CoverageEligibilityResponse response = financialResourceService.getEligibilityResponseById(
            organizationId, responseId, userId
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Search CoverageEligibilityResponse by patient.
     *
     * GET /api/v1/satusehat/financial/eligibility-response/patient/{ihsNumber}
     */
    @GetMapping("/eligibility-response/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<CoverageEligibilityResponse>> searchEligibilityResponsesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search eligibility responses by patient: {} for organization: {}", ihsNumber, organizationId);
        ClinicalResourceService.SearchBundle<CoverageEligibilityResponse> results = financialResourceService.searchEligibilityResponsesByPatient(
            organizationId, ihsNumber, userId
        );
        return ResponseEntity.ok(results);
    }

    /**
     * Search CoverageEligibilityResponse by request ID.
     *
     * GET /api/v1/satusehat/financial/eligibility-response/request/{requestId}
     */
    @GetMapping("/eligibility-response/request/{requestId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<CoverageEligibilityResponse>> searchEligibilityResponsesByRequest(
        @PathVariable String requestId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search eligibility responses by request: {} for organization: {}", requestId, organizationId);
        ClinicalResourceService.SearchBundle<CoverageEligibilityResponse> results = financialResourceService.searchEligibilityResponsesByRequest(
            organizationId, requestId, userId
        );
        return ResponseEntity.ok(results);
    }

    // ==================== Claim Endpoints ====================

    /**
     * Create Claim resource.
     *
     * POST /api/v1/satusehat/financial/claim
     */
    @PostMapping("/claim")
    public ResponseEntity<Claim> createClaim(
        @RequestBody Claim claim,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create claim for organization: {}, userId: {}", organizationId, userId);
        Claim created = financialResourceService.createClaim(organizationId, claim, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update Claim resource.
     *
     * PUT /api/v1/satusehat/financial/claim/{claimId}
     */
    @PutMapping("/claim/{claimId}")
    public ResponseEntity<Claim> updateClaim(
        @PathVariable String claimId,
        @RequestBody Claim claim,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update claim: {} for organization: {}, userId: {}", claimId, organizationId, userId);
        Claim updated = financialResourceService.updateClaim(organizationId, claimId, claim, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get Claim by ID.
     *
     * GET /api/v1/satusehat/financial/claim/{claimId}
     */
    @GetMapping("/claim/{claimId}")
    public ResponseEntity<Claim> getClaimById(
        @PathVariable String claimId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get claim: {} for organization: {}", claimId, organizationId);
        Claim claim = financialResourceService.getClaimById(organizationId, claimId, userId);
        return ResponseEntity.ok(claim);
    }

    /**
     * Search Claims by patient.
     *
     * GET /api/v1/satusehat/financial/claim/patient/{ihsNumber}
     */
    @GetMapping("/claim/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Claim>> searchClaimsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search claims by patient: {} for organization: {}", ihsNumber, organizationId);
        ClinicalResourceService.SearchBundle<Claim> results = financialResourceService.searchClaimsByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search Claims by status.
     *
     * GET /api/v1/satusehat/financial/claim/status/{status}
     */
    @GetMapping("/claim/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Claim>> searchClaimsByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search claims by status: {} for organization: {}", status, organizationId);
        ClinicalResourceService.SearchBundle<Claim> results = financialResourceService.searchClaimsByStatus(organizationId, status, userId);
        return ResponseEntity.ok(results);
    }

    // ==================== ClaimResponse Endpoints ====================

    /**
     * Create ClaimResponse resource.
     *
     * POST /api/v1/satusehat/financial/claim-response
     */
    @PostMapping("/claim-response")
    public ResponseEntity<ClaimResponse> createClaimResponse(
        @RequestBody ClaimResponse claimResponse,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create claim response for organization: {}, userId: {}", organizationId, userId);
        ClaimResponse created = financialResourceService.createClaimResponse(organizationId, claimResponse, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Get ClaimResponse by ID.
     *
     * GET /api/v1/satusehat/financial/claim-response/{responseId}
     */
    @GetMapping("/claim-response/{responseId}")
    public ResponseEntity<ClaimResponse> getClaimResponseById(
        @PathVariable String responseId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get claim response: {} for organization: {}", responseId, organizationId);
        ClaimResponse response = financialResourceService.getClaimResponseById(organizationId, responseId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Search ClaimResponse by patient.
     *
     * GET /api/v1/satusehat/financial/claim-response/patient/{ihsNumber}
     */
    @GetMapping("/claim-response/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ClaimResponse>> searchClaimResponsesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search claim responses by patient: {} for organization: {}", ihsNumber, organizationId);
        ClinicalResourceService.SearchBundle<ClaimResponse> results = financialResourceService.searchClaimResponsesByPatient(
            organizationId, ihsNumber, userId
        );
        return ResponseEntity.ok(results);
    }

    /**
     * Search ClaimResponse by claim ID.
     *
     * GET /api/v1/satusehat/financial/claim-response/claim/{claimId}
     */
    @GetMapping("/claim-response/claim/{claimId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ClaimResponse>> searchClaimResponsesByClaim(
        @PathVariable String claimId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search claim responses by claim: {} for organization: {}", claimId, organizationId);
        ClinicalResourceService.SearchBundle<ClaimResponse> results = financialResourceService.searchClaimResponsesByClaim(
            organizationId, claimId, userId
        );
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // ACCOUNT ENDPOINTS
    // ========================================================================

    /**
     * Create Account (patient billing account).
     *
     * POST /api/v1/satusehat/financial/account
     */
    @PostMapping("/account")
    public ResponseEntity<Account> createAccount(
        @RequestBody Account account,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create Account for organization: {}", organizationId);
        Account created = financialResourceService.createAccount(organizationId, account, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update Account.
     *
     * PUT /api/v1/satusehat/financial/account/{accountId}
     */
    @PutMapping("/account/{accountId}")
    public ResponseEntity<Account> updateAccount(
        @PathVariable String accountId,
        @RequestBody Account account,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update Account: {} for organization: {}", accountId, organizationId);
        Account updated = financialResourceService.updateAccount(organizationId, accountId, account, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get Account by ID.
     *
     * GET /api/v1/satusehat/financial/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Account> getAccountById(
        @PathVariable String accountId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get Account: {} for organization: {}", accountId, organizationId);
        Account account = financialResourceService.getAccountById(organizationId, accountId, userId);
        return ResponseEntity.ok(account);
    }

    /**
     * Search Accounts by patient.
     *
     * GET /api/v1/satusehat/financial/account/patient/{ihsNumber}
     */
    @GetMapping("/account/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Account>> searchAccountsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search Accounts by patient: {} for organization: {}", ihsNumber, organizationId);
        ClinicalResourceService.SearchBundle<Account> results = financialResourceService.searchAccountsByPatient(
            organizationId, ihsNumber, userId
        );
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // CHARGE ITEM ENDPOINTS
    // ========================================================================

    /**
     * Create ChargeItem (billable service charge).
     *
     * POST /api/v1/satusehat/financial/charge-item
     */
    @PostMapping("/charge-item")
    public ResponseEntity<ChargeItem> createChargeItem(
        @RequestBody ChargeItem chargeItem,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create ChargeItem for organization: {}", organizationId);
        ChargeItem created = financialResourceService.createChargeItem(organizationId, chargeItem, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update ChargeItem.
     *
     * PUT /api/v1/satusehat/financial/charge-item/{chargeItemId}
     */
    @PutMapping("/charge-item/{chargeItemId}")
    public ResponseEntity<ChargeItem> updateChargeItem(
        @PathVariable String chargeItemId,
        @RequestBody ChargeItem chargeItem,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update ChargeItem: {} for organization: {}", chargeItemId, organizationId);
        ChargeItem updated = financialResourceService.updateChargeItem(organizationId, chargeItemId, chargeItem, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get ChargeItem by ID.
     *
     * GET /api/v1/satusehat/financial/charge-item/{chargeItemId}
     */
    @GetMapping("/charge-item/{chargeItemId}")
    public ResponseEntity<ChargeItem> getChargeItemById(
        @PathVariable String chargeItemId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get ChargeItem: {} for organization: {}", chargeItemId, organizationId);
        ChargeItem chargeItem = financialResourceService.getChargeItemById(organizationId, chargeItemId, userId);
        return ResponseEntity.ok(chargeItem);
    }

    /**
     * Search ChargeItems by patient.
     *
     * GET /api/v1/satusehat/financial/charge-item/patient/{ihsNumber}
     */
    @GetMapping("/charge-item/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ChargeItem>> searchChargeItemsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search ChargeItems by patient: {} for organization: {}", ihsNumber, organizationId);
        ClinicalResourceService.SearchBundle<ChargeItem> results = financialResourceService.searchChargeItemsByPatient(
            organizationId, ihsNumber, userId
        );
        return ResponseEntity.ok(results);
    }

    /**
     * Search ChargeItems by account.
     *
     * GET /api/v1/satusehat/financial/charge-item/account/{accountId}
     */
    @GetMapping("/charge-item/account/{accountId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ChargeItem>> searchChargeItemsByAccount(
        @PathVariable String accountId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search ChargeItems by account: {} for organization: {}", accountId, organizationId);
        ClinicalResourceService.SearchBundle<ChargeItem> results = financialResourceService.searchChargeItemsByAccount(
            organizationId, accountId, userId
        );
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // INVOICE ENDPOINTS
    // ========================================================================

    /**
     * Create Invoice (consolidated billing invoice).
     *
     * POST /api/v1/satusehat/financial/invoice
     */
    @PostMapping("/invoice")
    public ResponseEntity<Invoice> createInvoice(
        @RequestBody Invoice invoice,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create Invoice for organization: {}", organizationId);
        Invoice created = financialResourceService.createInvoice(organizationId, invoice, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update Invoice.
     *
     * PUT /api/v1/satusehat/financial/invoice/{invoiceId}
     */
    @PutMapping("/invoice/{invoiceId}")
    public ResponseEntity<Invoice> updateInvoice(
        @PathVariable String invoiceId,
        @RequestBody Invoice invoice,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update Invoice: {} for organization: {}", invoiceId, organizationId);
        Invoice updated = financialResourceService.updateInvoice(organizationId, invoiceId, invoice, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get Invoice by ID.
     *
     * GET /api/v1/satusehat/financial/invoice/{invoiceId}
     */
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<Invoice> getInvoiceById(
        @PathVariable String invoiceId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get Invoice: {} for organization: {}", invoiceId, organizationId);
        Invoice invoice = financialResourceService.getInvoiceById(organizationId, invoiceId, userId);
        return ResponseEntity.ok(invoice);
    }

    /**
     * Search Invoices by patient.
     *
     * GET /api/v1/satusehat/financial/invoice/patient/{ihsNumber}
     */
    @GetMapping("/invoice/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Invoice>> searchInvoicesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search Invoices by patient: {} for organization: {}", ihsNumber, organizationId);
        ClinicalResourceService.SearchBundle<Invoice> results = financialResourceService.searchInvoicesByPatient(
            organizationId, ihsNumber, userId
        );
        return ResponseEntity.ok(results);
    }

    /**
     * Search Invoices by account.
     *
     * GET /api/v1/satusehat/financial/invoice/account/{accountId}
     */
    @GetMapping("/invoice/account/{accountId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Invoice>> searchInvoicesByAccount(
        @PathVariable String accountId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search Invoices by account: {} for organization: {}", accountId, organizationId);
        ClinicalResourceService.SearchBundle<Invoice> results = financialResourceService.searchInvoicesByAccount(
            organizationId, accountId, userId
        );
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // PAYMENT RECONCILIATION ENDPOINTS
    // ========================================================================

    /**
     * Create PaymentReconciliation (payment tracking and matching).
     *
     * POST /api/v1/satusehat/financial/payment-reconciliation
     */
    @PostMapping("/payment-reconciliation")
    public ResponseEntity<PaymentReconciliation> createPaymentReconciliation(
        @RequestBody PaymentReconciliation reconciliation,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create PaymentReconciliation for organization: {}", organizationId);
        PaymentReconciliation created = financialResourceService.createPaymentReconciliation(organizationId, reconciliation, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Get PaymentReconciliation by ID.
     *
     * GET /api/v1/satusehat/financial/payment-reconciliation/{reconciliationId}
     */
    @GetMapping("/payment-reconciliation/{reconciliationId}")
    public ResponseEntity<PaymentReconciliation> getPaymentReconciliationById(
        @PathVariable String reconciliationId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get PaymentReconciliation: {} for organization: {}", reconciliationId, organizationId);
        PaymentReconciliation reconciliation = financialResourceService.getPaymentReconciliationById(organizationId, reconciliationId, userId);
        return ResponseEntity.ok(reconciliation);
    }

    /**
     * Search PaymentReconciliations by claim.
     *
     * GET /api/v1/satusehat/financial/payment-reconciliation/claim/{claimId}
     */
    @GetMapping("/payment-reconciliation/claim/{claimId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<PaymentReconciliation>> searchPaymentReconciliationsByClaim(
        @PathVariable String claimId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search PaymentReconciliations by claim: {} for organization: {}", claimId, organizationId);
        ClinicalResourceService.SearchBundle<PaymentReconciliation> results = financialResourceService.searchPaymentReconciliationsByClaim(
            organizationId, claimId, userId
        );
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // PAYMENT NOTICE ENDPOINTS
    // ========================================================================

    /**
     * Create PaymentNotice (payment notification).
     *
     * POST /api/v1/satusehat/financial/payment-notice
     */
    @PostMapping("/payment-notice")
    public ResponseEntity<PaymentNotice> createPaymentNotice(
        @RequestBody PaymentNotice paymentNotice,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create PaymentNotice for organization: {}", organizationId);
        PaymentNotice created = financialResourceService.createPaymentNotice(organizationId, paymentNotice, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Get PaymentNotice by ID.
     *
     * GET /api/v1/satusehat/financial/payment-notice/{noticeId}
     */
    @GetMapping("/payment-notice/{noticeId}")
    public ResponseEntity<PaymentNotice> getPaymentNoticeById(
        @PathVariable String noticeId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get PaymentNotice: {} for organization: {}", noticeId, organizationId);
        PaymentNotice paymentNotice = financialResourceService.getPaymentNoticeById(organizationId, noticeId, userId);
        return ResponseEntity.ok(paymentNotice);
    }

    /**
     * Search PaymentNotices by claim.
     *
     * GET /api/v1/satusehat/financial/payment-notice/claim/{claimId}
     */
    @GetMapping("/payment-notice/claim/{claimId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<PaymentNotice>> searchPaymentNoticesByClaim(
        @PathVariable String claimId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search PaymentNotices by claim: {} for organization: {}", claimId, organizationId);
        ClinicalResourceService.SearchBundle<PaymentNotice> results = financialResourceService.searchPaymentNoticesByClaim(
            organizationId, claimId, userId
        );
        return ResponseEntity.ok(results);
    }
}
