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
 * Service for SATUSEHAT Financial and Coverage Resources.
 *
 * Handles comprehensive financial workflow including insurance coverage, billing,
 * claims processing, and payment reconciliation:
 *
 * Coverage Management:
 * - Coverage: Insurance information (BPJS, private, company)
 * - CoverageEligibilityRequest/Response: SEP request and verification
 *
 * Billing & Charging:
 * - Account: Patient billing accounts
 * - ChargeItem: Individual service charges
 * - Invoice: Consolidated billing invoices
 *
 * Claims & Payments:
 * - Claim: Insurance claim submission
 * - ClaimResponse: Claim adjudication results
 * - PaymentReconciliation: Payment tracking and matching
 * - PaymentNotice: Payment notifications
 *
 * @author HMS Development Team
 * @version 2.0.0
 * @since 2025-01-21
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialResourceService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    // ==================== Coverage Operations ====================

    /**
     * Create Coverage resource (BPJS insurance card).
     */
    public Coverage createCoverage(String organizationId, Coverage coverage, UUID userId) {
        log.info("Creating Coverage for organization: {}, userId: {}", organizationId, userId);

        if (coverage.getBeneficiary() == null) {
            throw new SatusehatValidationException("Coverage beneficiary (patient) is required");
        }
        if (coverage.getPayor() == null || coverage.getPayor().isEmpty()) {
            throw new SatusehatValidationException("Coverage payor (BPJS organization) is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Coverage created = httpClient.post(
            "/Coverage",
            coverage,
            config,
            Coverage.class,
            userId
        );

        log.info("Coverage created successfully");
        return created;
    }

    /**
     * Update Coverage resource.
     */
    public Coverage updateCoverage(String organizationId, String coverageId, Coverage coverage, UUID userId) {
        log.info("Updating Coverage: {} for organization: {}, userId: {}", coverageId, organizationId, userId);

        if (coverage.getId() == null) {
            coverage.setId(coverageId);
        }

        var config = authService.getActiveConfig(organizationId);

        Coverage updated = httpClient.put(
            "/Coverage/" + coverageId,
            coverage,
            config,
            Coverage.class,
            userId
        );

        log.info("Coverage updated successfully: {}", coverageId);
        return updated;
    }

    /**
     * Get Coverage by ID.
     */
    public Coverage getCoverageById(String organizationId, String coverageId, UUID userId) {
        log.info("Fetching Coverage: {} for organization: {}", coverageId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/Coverage/" + coverageId,
            config,
            Coverage.class,
            userId
        );
    }

    /**
     * Search Coverage by patient IHS number.
     */
    public ClinicalResourceService.SearchBundle<Coverage> searchCoveragesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching Coverage by patient: {} for organization: {}", ihsNumber, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("patient", ihsNumber);

        return searchCoverages(organizationId, params, userId);
    }

    /**
     * Search Coverage by subscriber ID (BPJS card number).
     */
    public ClinicalResourceService.SearchBundle<Coverage> searchCoveragesBySubscriberId(
        String organizationId,
        String subscriberId,
        UUID userId
    ) {
        log.info("Searching Coverage by subscriber ID: {} for organization: {}", subscriberId, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("subscriber-id", subscriberId);

        return searchCoverages(organizationId, params, userId);
    }

    /**
     * Generic Coverage search.
     */
    private ClinicalResourceService.SearchBundle<Coverage> searchCoverages(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Coverage?");
        params.forEach((key, value) -> {
            if (queryString.length() > 10) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Coverage> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== CoverageEligibilityRequest Operations (SEP Request) ====================

    /**
     * Create CoverageEligibilityRequest (SEP request).
     */
    public CoverageEligibilityRequest createEligibilityRequest(
        String organizationId,
        CoverageEligibilityRequest request,
        UUID userId
    ) {
        log.info("Creating CoverageEligibilityRequest (SEP) for organization: {}, userId: {}", organizationId, userId);

        if (request.getPatient() == null) {
            throw new SatusehatValidationException("Patient reference is required for SEP request");
        }
        if (request.getInsurer() == null) {
            throw new SatusehatValidationException("Insurer (BPJS) reference is required");
        }

        var config = authService.getActiveConfig(organizationId);

        CoverageEligibilityRequest created = httpClient.post(
            "/CoverageEligibilityRequest",
            request,
            config,
            CoverageEligibilityRequest.class,
            userId
        );

        log.info("CoverageEligibilityRequest (SEP) created successfully");
        return created;
    }

    /**
     * Get CoverageEligibilityRequest by ID.
     */
    public CoverageEligibilityRequest getEligibilityRequestById(
        String organizationId,
        String requestId,
        UUID userId
    ) {
        log.info("Fetching CoverageEligibilityRequest: {} for organization: {}", requestId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/CoverageEligibilityRequest/" + requestId,
            config,
            CoverageEligibilityRequest.class,
            userId
        );
    }

    /**
     * Search CoverageEligibilityRequest by patient.
     */
    public ClinicalResourceService.SearchBundle<CoverageEligibilityRequest> searchEligibilityRequestsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching CoverageEligibilityRequest by patient: {} for organization: {}", ihsNumber, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("patient", ihsNumber);

        return searchEligibilityRequests(organizationId, params, userId);
    }

    /**
     * Generic CoverageEligibilityRequest search.
     */
    private ClinicalResourceService.SearchBundle<CoverageEligibilityRequest> searchEligibilityRequests(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/CoverageEligibilityRequest?");
        params.forEach((key, value) -> {
            if (queryString.length() > 29) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<CoverageEligibilityRequest> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== CoverageEligibilityResponse Operations (SEP Response) ====================

    /**
     * Create CoverageEligibilityResponse (SEP response).
     */
    public CoverageEligibilityResponse createEligibilityResponse(
        String organizationId,
        CoverageEligibilityResponse response,
        UUID userId
    ) {
        log.info("Creating CoverageEligibilityResponse (SEP) for organization: {}, userId: {}", organizationId, userId);

        if (response.getPatient() == null) {
            throw new SatusehatValidationException("Patient reference is required");
        }
        if (response.getRequest() == null) {
            throw new SatusehatValidationException("CoverageEligibilityRequest reference is required");
        }

        var config = authService.getActiveConfig(organizationId);

        CoverageEligibilityResponse created = httpClient.post(
            "/CoverageEligibilityResponse",
            response,
            config,
            CoverageEligibilityResponse.class,
            userId
        );

        log.info("CoverageEligibilityResponse (SEP) created successfully");
        return created;
    }

    /**
     * Get CoverageEligibilityResponse by ID.
     */
    public CoverageEligibilityResponse getEligibilityResponseById(
        String organizationId,
        String responseId,
        UUID userId
    ) {
        log.info("Fetching CoverageEligibilityResponse: {} for organization: {}", responseId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/CoverageEligibilityResponse/" + responseId,
            config,
            CoverageEligibilityResponse.class,
            userId
        );
    }

    /**
     * Search CoverageEligibilityResponse by patient.
     */
    public ClinicalResourceService.SearchBundle<CoverageEligibilityResponse> searchEligibilityResponsesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching CoverageEligibilityResponse by patient: {} for organization: {}", ihsNumber, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("patient", ihsNumber);

        return searchEligibilityResponses(organizationId, params, userId);
    }

    /**
     * Search CoverageEligibilityResponse by request ID.
     */
    public ClinicalResourceService.SearchBundle<CoverageEligibilityResponse> searchEligibilityResponsesByRequest(
        String organizationId,
        String requestId,
        UUID userId
    ) {
        log.info("Searching CoverageEligibilityResponse by request: {} for organization: {}", requestId, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("request", requestId);

        return searchEligibilityResponses(organizationId, params, userId);
    }

    /**
     * Generic CoverageEligibilityResponse search.
     */
    private ClinicalResourceService.SearchBundle<CoverageEligibilityResponse> searchEligibilityResponses(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/CoverageEligibilityResponse?");
        params.forEach((key, value) -> {
            if (queryString.length() > 30) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<CoverageEligibilityResponse> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== Claim Operations ====================

    /**
     * Create Claim (insurance claim submission).
     */
    public Claim createClaim(String organizationId, Claim claim, UUID userId) {
        log.info("Creating Claim for organization: {}, userId: {}", organizationId, userId);

        if (claim.getPatient() == null) {
            throw new SatusehatValidationException("Patient reference is required for claim");
        }
        if (claim.getProvider() == null) {
            throw new SatusehatValidationException("Provider (hospital) reference is required");
        }
        if (claim.getInsurer() == null) {
            throw new SatusehatValidationException("Insurer (BPJS) reference is required");
        }
        if (claim.getInsurance() == null || claim.getInsurance().isEmpty()) {
            throw new SatusehatValidationException("Insurance information with SEP reference is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Claim created = httpClient.post(
            "/Claim",
            claim,
            config,
            Claim.class,
            userId
        );

        log.info("Claim created successfully");
        return created;
    }

    /**
     * Update Claim.
     */
    public Claim updateClaim(String organizationId, String claimId, Claim claim, UUID userId) {
        log.info("Updating Claim: {} for organization: {}, userId: {}", claimId, organizationId, userId);

        if (claim.getId() == null) {
            claim.setId(claimId);
        }

        var config = authService.getActiveConfig(organizationId);

        Claim updated = httpClient.put(
            "/Claim/" + claimId,
            claim,
            config,
            Claim.class,
            userId
        );

        log.info("Claim updated successfully: {}", claimId);
        return updated;
    }

    /**
     * Get Claim by ID.
     */
    public Claim getClaimById(String organizationId, String claimId, UUID userId) {
        log.info("Fetching Claim: {} for organization: {}", claimId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/Claim/" + claimId,
            config,
            Claim.class,
            userId
        );
    }

    /**
     * Search Claims by patient.
     */
    public ClinicalResourceService.SearchBundle<Claim> searchClaimsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching Claims by patient: {} for organization: {}", ihsNumber, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("patient", ihsNumber);

        return searchClaims(organizationId, params, userId);
    }

    /**
     * Search Claims by status.
     */
    public ClinicalResourceService.SearchBundle<Claim> searchClaimsByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching Claims by status: {} for organization: {}", status, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchClaims(organizationId, params, userId);
    }

    /**
     * Generic Claim search.
     */
    private ClinicalResourceService.SearchBundle<Claim> searchClaims(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Claim?");
        params.forEach((key, value) -> {
            if (queryString.length() > 7) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Claim> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== ClaimResponse Operations ====================

    /**
     * Create ClaimResponse (claim adjudication result).
     */
    public ClaimResponse createClaimResponse(String organizationId, ClaimResponse claimResponse, UUID userId) {
        log.info("Creating ClaimResponse for organization: {}, userId: {}", organizationId, userId);

        if (claimResponse.getPatient() == null) {
            throw new SatusehatValidationException("Patient reference is required");
        }
        if (claimResponse.getRequest() == null) {
            throw new SatusehatValidationException("Claim reference is required");
        }

        var config = authService.getActiveConfig(organizationId);

        ClaimResponse created = httpClient.post(
            "/ClaimResponse",
            claimResponse,
            config,
            ClaimResponse.class,
            userId
        );

        log.info("ClaimResponse created successfully");
        return created;
    }

    /**
     * Get ClaimResponse by ID.
     */
    public ClaimResponse getClaimResponseById(String organizationId, String responseId, UUID userId) {
        log.info("Fetching ClaimResponse: {} for organization: {}", responseId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/ClaimResponse/" + responseId,
            config,
            ClaimResponse.class,
            userId
        );
    }

    /**
     * Search ClaimResponse by patient.
     */
    public ClinicalResourceService.SearchBundle<ClaimResponse> searchClaimResponsesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching ClaimResponse by patient: {} for organization: {}", ihsNumber, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("patient", ihsNumber);

        return searchClaimResponses(organizationId, params, userId);
    }

    /**
     * Search ClaimResponse by claim ID.
     */
    public ClinicalResourceService.SearchBundle<ClaimResponse> searchClaimResponsesByClaim(
        String organizationId,
        String claimId,
        UUID userId
    ) {
        log.info("Searching ClaimResponse by claim: {} for organization: {}", claimId, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("request", claimId);

        return searchClaimResponses(organizationId, params, userId);
    }

    /**
     * Generic ClaimResponse search.
     */
    private ClinicalResourceService.SearchBundle<ClaimResponse> searchClaimResponses(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/ClaimResponse?");
        params.forEach((key, value) -> {
            if (queryString.length() > 15) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<ClaimResponse> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== Account Operations ====================

    /**
     * Create Account (patient billing account).
     */
    public Account createAccount(String organizationId, Account account, UUID userId) {
        log.info("Creating Account for organization: {}, userId: {}", organizationId, userId);

        if (account.getSubject() == null || account.getSubject().isEmpty()) {
            throw new SatusehatValidationException("Account subject (patient) is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Account created = httpClient.post(
            "/Account",
            account,
            config,
            Account.class,
            userId
        );

        log.info("Account created successfully");
        return created;
    }

    /**
     * Update Account.
     */
    public Account updateAccount(String organizationId, String accountId, Account account, UUID userId) {
        log.info("Updating Account: {} for organization: {}", accountId, organizationId);

        if (account.getId() == null) {
            account.setId(accountId);
        }

        var config = authService.getActiveConfig(organizationId);

        Account updated = httpClient.put(
            "/Account/" + accountId,
            account,
            config,
            Account.class,
            userId
        );

        log.info("Account updated successfully: {}", accountId);
        return updated;
    }

    /**
     * Get Account by ID.
     */
    public Account getAccountById(String organizationId, String accountId, UUID userId) {
        log.info("Fetching Account: {} for organization: {}", accountId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/Account/" + accountId,
            config,
            Account.class,
            userId
        );
    }

    /**
     * Search Accounts by patient.
     */
    public ClinicalResourceService.SearchBundle<Account> searchAccountsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching Accounts by patient: {} for organization: {}", ihsNumber, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchAccounts(organizationId, params, userId);
    }

    /**
     * Generic Account search.
     */
    private ClinicalResourceService.SearchBundle<Account> searchAccounts(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Account?");
        params.forEach((key, value) -> {
            if (queryString.length() > 9) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Account> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== ChargeItem Operations ====================

    /**
     * Create ChargeItem (billable service charge).
     */
    public ChargeItem createChargeItem(String organizationId, ChargeItem chargeItem, UUID userId) {
        log.info("Creating ChargeItem for organization: {}, userId: {}", organizationId, userId);

        if (chargeItem.getSubject() == null) {
            throw new SatusehatValidationException("ChargeItem subject (patient) is required");
        }
        if (chargeItem.getCode() == null) {
            throw new SatusehatValidationException("ChargeItem code is required");
        }

        var config = authService.getActiveConfig(organizationId);

        ChargeItem created = httpClient.post(
            "/ChargeItem",
            chargeItem,
            config,
            ChargeItem.class,
            userId
        );

        log.info("ChargeItem created successfully");
        return created;
    }

    /**
     * Update ChargeItem.
     */
    public ChargeItem updateChargeItem(String organizationId, String chargeItemId, ChargeItem chargeItem, UUID userId) {
        log.info("Updating ChargeItem: {} for organization: {}", chargeItemId, organizationId);

        if (chargeItem.getId() == null) {
            chargeItem.setId(chargeItemId);
        }

        var config = authService.getActiveConfig(organizationId);

        ChargeItem updated = httpClient.put(
            "/ChargeItem/" + chargeItemId,
            chargeItem,
            config,
            ChargeItem.class,
            userId
        );

        log.info("ChargeItem updated successfully: {}", chargeItemId);
        return updated;
    }

    /**
     * Get ChargeItem by ID.
     */
    public ChargeItem getChargeItemById(String organizationId, String chargeItemId, UUID userId) {
        log.info("Fetching ChargeItem: {} for organization: {}", chargeItemId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/ChargeItem/" + chargeItemId,
            config,
            ChargeItem.class,
            userId
        );
    }

    /**
     * Search ChargeItems by patient.
     */
    public ClinicalResourceService.SearchBundle<ChargeItem> searchChargeItemsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching ChargeItems by patient: {} for organization: {}", ihsNumber, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchChargeItems(organizationId, params, userId);
    }

    /**
     * Search ChargeItems by account.
     */
    public ClinicalResourceService.SearchBundle<ChargeItem> searchChargeItemsByAccount(
        String organizationId,
        String accountId,
        UUID userId
    ) {
        log.info("Searching ChargeItems by account: {} for organization: {}", accountId, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("account", "Account/" + accountId);

        return searchChargeItems(organizationId, params, userId);
    }

    /**
     * Generic ChargeItem search.
     */
    private ClinicalResourceService.SearchBundle<ChargeItem> searchChargeItems(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/ChargeItem?");
        params.forEach((key, value) -> {
            if (queryString.length() > 12) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<ChargeItem> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== Invoice Operations ====================

    /**
     * Create Invoice (consolidated billing invoice).
     */
    public Invoice createInvoice(String organizationId, Invoice invoice, UUID userId) {
        log.info("Creating Invoice for organization: {}, userId: {}", organizationId, userId);

        if (invoice.getSubject() == null) {
            throw new SatusehatValidationException("Invoice subject (patient) is required");
        }
        if (invoice.getIssuer() == null) {
            throw new SatusehatValidationException("Invoice issuer (hospital) is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Invoice created = httpClient.post(
            "/Invoice",
            invoice,
            config,
            Invoice.class,
            userId
        );

        log.info("Invoice created successfully");
        return created;
    }

    /**
     * Update Invoice.
     */
    public Invoice updateInvoice(String organizationId, String invoiceId, Invoice invoice, UUID userId) {
        log.info("Updating Invoice: {} for organization: {}", invoiceId, organizationId);

        if (invoice.getId() == null) {
            invoice.setId(invoiceId);
        }

        var config = authService.getActiveConfig(organizationId);

        Invoice updated = httpClient.put(
            "/Invoice/" + invoiceId,
            invoice,
            config,
            Invoice.class,
            userId
        );

        log.info("Invoice updated successfully: {}", invoiceId);
        return updated;
    }

    /**
     * Get Invoice by ID.
     */
    public Invoice getInvoiceById(String organizationId, String invoiceId, UUID userId) {
        log.info("Fetching Invoice: {} for organization: {}", invoiceId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/Invoice/" + invoiceId,
            config,
            Invoice.class,
            userId
        );
    }

    /**
     * Search Invoices by patient.
     */
    public ClinicalResourceService.SearchBundle<Invoice> searchInvoicesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching Invoices by patient: {} for organization: {}", ihsNumber, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchInvoices(organizationId, params, userId);
    }

    /**
     * Search Invoices by account.
     */
    public ClinicalResourceService.SearchBundle<Invoice> searchInvoicesByAccount(
        String organizationId,
        String accountId,
        UUID userId
    ) {
        log.info("Searching Invoices by account: {} for organization: {}", accountId, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("account", "Account/" + accountId);

        return searchInvoices(organizationId, params, userId);
    }

    /**
     * Generic Invoice search.
     */
    private ClinicalResourceService.SearchBundle<Invoice> searchInvoices(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Invoice?");
        params.forEach((key, value) -> {
            if (queryString.length() > 9) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Invoice> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== PaymentReconciliation Operations ====================

    /**
     * Create PaymentReconciliation (payment tracking and matching).
     */
    public PaymentReconciliation createPaymentReconciliation(
        String organizationId,
        PaymentReconciliation reconciliation,
        UUID userId
    ) {
        log.info("Creating PaymentReconciliation for organization: {}, userId: {}", organizationId, userId);

        if (reconciliation.getRequest() == null) {
            throw new SatusehatValidationException("PaymentReconciliation request (claim) is required");
        }

        var config = authService.getActiveConfig(organizationId);

        PaymentReconciliation created = httpClient.post(
            "/PaymentReconciliation",
            reconciliation,
            config,
            PaymentReconciliation.class,
            userId
        );

        log.info("PaymentReconciliation created successfully");
        return created;
    }

    /**
     * Get PaymentReconciliation by ID.
     */
    public PaymentReconciliation getPaymentReconciliationById(
        String organizationId,
        String reconciliationId,
        UUID userId
    ) {
        log.info("Fetching PaymentReconciliation: {} for organization: {}", reconciliationId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/PaymentReconciliation/" + reconciliationId,
            config,
            PaymentReconciliation.class,
            userId
        );
    }

    /**
     * Search PaymentReconciliation by claim.
     */
    public ClinicalResourceService.SearchBundle<PaymentReconciliation> searchPaymentReconciliationsByClaim(
        String organizationId,
        String claimId,
        UUID userId
    ) {
        log.info("Searching PaymentReconciliations by claim: {} for organization: {}", claimId, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("request", "Claim/" + claimId);

        return searchPaymentReconciliations(organizationId, params, userId);
    }

    /**
     * Generic PaymentReconciliation search.
     */
    private ClinicalResourceService.SearchBundle<PaymentReconciliation> searchPaymentReconciliations(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/PaymentReconciliation?");
        params.forEach((key, value) -> {
            if (queryString.length() > 24) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<PaymentReconciliation> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ==================== PaymentNotice Operations ====================

    /**
     * Create PaymentNotice (payment notification).
     */
    public PaymentNotice createPaymentNotice(
        String organizationId,
        PaymentNotice paymentNotice,
        UUID userId
    ) {
        log.info("Creating PaymentNotice for organization: {}, userId: {}", organizationId, userId);

        if (paymentNotice.getRequest() == null) {
            throw new SatusehatValidationException("PaymentNotice request (claim) is required");
        }

        var config = authService.getActiveConfig(organizationId);

        PaymentNotice created = httpClient.post(
            "/PaymentNotice",
            paymentNotice,
            config,
            PaymentNotice.class,
            userId
        );

        log.info("PaymentNotice created successfully");
        return created;
    }

    /**
     * Get PaymentNotice by ID.
     */
    public PaymentNotice getPaymentNoticeById(
        String organizationId,
        String noticeId,
        UUID userId
    ) {
        log.info("Fetching PaymentNotice: {} for organization: {}", noticeId, organizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/PaymentNotice/" + noticeId,
            config,
            PaymentNotice.class,
            userId
        );
    }

    /**
     * Search PaymentNotice by claim.
     */
    public ClinicalResourceService.SearchBundle<PaymentNotice> searchPaymentNoticesByClaim(
        String organizationId,
        String claimId,
        UUID userId
    ) {
        log.info("Searching PaymentNotices by claim: {} for organization: {}", claimId, organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("request", "Claim/" + claimId);

        return searchPaymentNotices(organizationId, params, userId);
    }

    /**
     * Generic PaymentNotice search.
     */
    private ClinicalResourceService.SearchBundle<PaymentNotice> searchPaymentNotices(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/PaymentNotice?");
        params.forEach((key, value) -> {
            if (queryString.length() > 15) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<PaymentNotice> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }
}
