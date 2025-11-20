package com.yudha.hms.integration.eklaim.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.eklaim.dto.*;
import com.yudha.hms.integration.eklaim.entity.EklaimClaim;
import com.yudha.hms.integration.eklaim.entity.EklaimConfig;
import com.yudha.hms.integration.eklaim.exception.EklaimIntegrationException;
import com.yudha.hms.integration.eklaim.repository.EklaimClaimRepository;
import com.yudha.hms.integration.eklaim.repository.EklaimConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * E-Klaim Claim Service.
 *
 * Implements E-Klaim 5.10.x Web Service operations for INA-CBGs claim processing.
 * This service orchestrates the complete claim lifecycle:
 *
 * 1. Claim Creation (new_claim)
 * 2. Data Entry (set_claim_data, diagnosa_set, procedure_set)
 * 3. Grouping (grouper_1 for iDRG, grouper_2 for INACBG)
 * 4. Special Items (special_cmg, claim_prosthesis)
 * 5. Finalization (claim_final)
 * 6. Submission (send_claim_individual, send_claim_reconsider)
 * 7. Monitoring (get_claim_status, monitoring_klaim)
 * 8. Payment (claim_ba)
 * 9. SITB Integration for TB cases
 *
 * All methods:
 * - Handle encryption/decryption
 * - Create audit logs
 * - Update local claim status
 * - Manage transactions
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EklaimClaimService {

    private final EklaimClaimRepository claimRepository;
    private final EklaimConfigRepository configRepository;
    private final EklaimHttpClient httpClient;
    private final EklaimAuditService auditService;
    private final ObjectMapper objectMapper;

    /**
     * 1. Create new claim (new_claim).
     *
     * Creates a draft claim from SEP number.
     * SEP must be valid and not already used.
     *
     * Status transition: None → 1 (Draft)
     */
    @Transactional
    public EklaimClaim newClaim(String nomorSep, String hospitalCode, UUID userId) {
        log.info("Creating new E-Klaim claim for SEP: {}", nomorSep);

        // Check if SEP already used
        if (claimRepository.existsByNomorSep(nomorSep)) {
            throw new EklaimIntegrationException(
                "SEP " + nomorSep + " already used for another claim",
                "E2016"
            );
        }

        // Get configuration
        EklaimConfig config = getActiveConfig(hospitalCode);

        // Prepare request
        NewClaimRequest request = new NewClaimRequest();
        NewClaimRequest.ClaimData data = new NewClaimRequest.ClaimData();
        data.setNomorSep(nomorSep);
        data.setHospitalCode(hospitalCode);
        request.setData(data);

        // Call E-Klaim API
        long startTime = System.currentTimeMillis();
        try {
            EklaimBaseResponse<Object> response = httpClient.post(
                "/claim/new",
                request,
                config,
                Object.class
            );

            long executionTime = System.currentTimeMillis() - startTime;

            // Extract claim number from response
            @SuppressWarnings("unchecked")
            Map<String, Object> responseData = (Map<String, Object>) response.getResponse();
            String claimNumber = (String) responseData.get("claim_number");

            // Create local claim record
            EklaimClaim claim = new EklaimClaim();
            claim.setClaimNumber(claimNumber);
            claim.setNomorSep(nomorSep);
            claim.setStatus(1); // Draft
            claim.setSepData(objectMapper.writeValueAsString(responseData.get("sep_data")));
            claim.setCreatedBy(userId);
            claim = claimRepository.save(claim);

            // Audit log
            auditService.logApiCall(
                claim,
                "new_claim",
                "POST",
                objectMapper.writeValueAsString(request),
                null,
                objectMapper.writeValueAsString(response),
                null,
                "200",
                (int) executionTime,
                userId
            );

            log.info("Created E-Klaim claim: {} for SEP: {}", claimNumber, nomorSep);
            return claim;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            auditService.logError(null, "new_claim", "POST",
                nomorSep, e.getMessage(), (int) executionTime, userId);
            throw new EklaimIntegrationException("Failed to create claim: " + e.getMessage(), e);
        }
    }

    /**
     * 2. Set claim data (set_claim_data).
     *
     * Sets comprehensive claim information including patient demographics,
     * admission/discharge details, and billing data.
     *
     * Status: Must be in Draft (1) or Ungrouped (2)
     */
    @Transactional
    public void setClaimData(String claimNumber, ClaimDataRequest request, UUID userId) {
        log.info("Setting claim data for: {}", claimNumber);

        EklaimClaim claim = getClaimByNumber(claimNumber);
        validateClaimStatus(claim, 1, 2); // Must be Draft or Ungrouped

        EklaimConfig config = getActiveConfigForClaim(claim);

        long startTime = System.currentTimeMillis();
        try {
            EklaimBaseResponse<Object> response = httpClient.put(
                "/claim/data",
                request,
                config,
                Object.class
            );

            long executionTime = System.currentTimeMillis() - startTime;

            // Update local claim
            claim.setPatientData(objectMapper.writeValueAsString(request.getData()));
            claim.setStatus(2); // Ungrouped
            claim.setUpdatedBy(userId);
            claimRepository.save(claim);

            // Audit log
            auditService.logApiCall(
                claim,
                "set_claim_data",
                "PUT",
                objectMapper.writeValueAsString(request),
                null,
                objectMapper.writeValueAsString(response),
                null,
                "200",
                (int) executionTime,
                userId
            );

            log.info("Claim data set for: {}", claimNumber);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            auditService.logError(claim, "set_claim_data", "PUT",
                claimNumber, e.getMessage(), (int) executionTime, userId);
            throw new EklaimIntegrationException("Failed to set claim data: " + e.getMessage(), e);
        }
    }

    /**
     * 10. Set diagnoses (diagnosa_set).
     *
     * Sets ICD-10 diagnoses (principal and secondary).
     */
    @Transactional
    public void setDiagnoses(String claimNumber, DiagnosisRequest request, UUID userId) {
        log.info("Setting diagnoses for claim: {}", claimNumber);

        EklaimClaim claim = getClaimByNumber(claimNumber);
        validateClaimStatus(claim, 1, 2); // Must be Draft or Ungrouped

        EklaimConfig config = getActiveConfigForClaim(claim);

        long startTime = System.currentTimeMillis();
        try {
            EklaimBaseResponse<Object> response = httpClient.post(
                "/claim/diagnoses",
                request,
                config,
                Object.class
            );

            long executionTime = System.currentTimeMillis() - startTime;

            // Update local claim
            claim.setDiagnosisData(objectMapper.writeValueAsString(request.getData()));
            claim.setUpdatedBy(userId);
            claimRepository.save(claim);

            // Audit log
            auditService.logApiCall(
                claim,
                "diagnosa_set",
                "POST",
                objectMapper.writeValueAsString(request),
                null,
                objectMapper.writeValueAsString(response),
                null,
                "200",
                (int) executionTime,
                userId
            );

            log.info("Diagnoses set for claim: {}", claimNumber);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            auditService.logError(claim, "diagnosa_set", "POST",
                claimNumber, e.getMessage(), (int) executionTime, userId);
            throw new EklaimIntegrationException("Failed to set diagnoses: " + e.getMessage(), e);
        }
    }

    /**
     * 11. Set procedures (procedure_set).
     *
     * Sets ICD-9-CM procedures.
     */
    @Transactional
    public void setProcedures(String claimNumber, ProcedureRequest request, UUID userId) {
        log.info("Setting procedures for claim: {}", claimNumber);

        EklaimClaim claim = getClaimByNumber(claimNumber);
        validateClaimStatus(claim, 1, 2);

        EklaimConfig config = getActiveConfigForClaim(claim);

        long startTime = System.currentTimeMillis();
        try {
            EklaimBaseResponse<Object> response = httpClient.post(
                "/claim/procedures",
                request,
                config,
                Object.class
            );

            long executionTime = System.currentTimeMillis() - startTime;

            // Update local claim
            claim.setProcedureData(objectMapper.writeValueAsString(request.getData()));
            claim.setUpdatedBy(userId);
            claimRepository.save(claim);

            // Audit log
            auditService.logApiCall(
                claim,
                "procedure_set",
                "POST",
                objectMapper.writeValueAsString(request),
                null,
                objectMapper.writeValueAsString(response),
                null,
                "200",
                (int) executionTime,
                userId
            );

            log.info("Procedures set for claim: {}", claimNumber);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            auditService.logError(claim, "procedure_set", "POST",
                claimNumber, e.getMessage(), (int) executionTime, userId);
            throw new EklaimIntegrationException("Failed to set procedures: " + e.getMessage(), e);
        }
    }

    /**
     * 12-14. Execute grouper (grouper_1 for iDRG, grouper_2 for INACBG).
     *
     * Two-stage grouping:
     * 1. grouper_1: iDRG (Indonesia Diagnosis Related Group)
     * 2. grouper_2: INACBG (Indonesian Case-Based Groups)
     *
     * Status transitions:
     * - grouper_1: 2 (Ungrouped) → 3 (iDRG Grouped)
     * - grouper_2: 3 (iDRG Grouped) → 4 (INACBG Grouped)
     */
    @Transactional
    public GrouperResponse executeGrouper(
        String claimNumber,
        String grouperType,
        UUID userId
    ) {
        log.info("Executing grouper {} for claim: {}", grouperType, claimNumber);

        EklaimClaim claim = getClaimByNumber(claimNumber);

        // Validate status based on grouper type
        if ("1".equals(grouperType)) {
            validateClaimStatus(claim, 2); // Must be Ungrouped for iDRG
        } else if ("2".equals(grouperType)) {
            validateClaimStatus(claim, 3); // Must be iDRG Grouped for INACBG
        }

        EklaimConfig config = getActiveConfigForClaim(claim);

        GrouperRequest request = new GrouperRequest();
        GrouperRequest.GrouperData data = new GrouperRequest.GrouperData();
        data.setClaimNumber(claimNumber);
        data.setGrouperType(grouperType);
        request.setData(data);

        long startTime = System.currentTimeMillis();
        try {
            EklaimBaseResponse<GrouperResponse> response = httpClient.post(
                "/claim/grouper",
                request,
                config,
                GrouperResponse.class
            );

            long executionTime = System.currentTimeMillis() - startTime;
            GrouperResponse result = response.getResponse();

            // Update claim with grouping results
            if ("1".equals(grouperType)) {
                claim.setIdrgCode(result.getCode());
                claim.setIdrgTariff(result.getBaseTariff());
                claim.setIdrgResult(objectMapper.writeValueAsString(result));
                claim.setStatus(3); // iDRG Grouped
            } else {
                claim.setCbgCode(result.getCode());
                claim.setBaseTariff(result.getBaseTariff());
                claim.setTopUpCovid(result.getTopUpCovid() != null ? result.getTopUpCovid() : BigDecimal.ZERO);
                claim.setTopUpChronic(result.getTopUpChronic() != null ? result.getTopUpChronic() : BigDecimal.ZERO);
                claim.setSpecialCmg(result.getSpecialCmg() != null ? result.getSpecialCmg() : BigDecimal.ZERO);
                claim.setSpecialProsthesis(result.getSpecialProsthesis() != null ? result.getSpecialProsthesis() : BigDecimal.ZERO);
                claim.setSpecialDrug(result.getSpecialDrug() != null ? result.getSpecialDrug() : BigDecimal.ZERO);
                claim.setTotalTariff(result.getTotalTariff());
                claim.setInacbgResult(objectMapper.writeValueAsString(result));
                claim.setStatus(4); // INACBG Grouped
            }

            claim.setUpdatedBy(userId);
            claimRepository.save(claim);

            // Audit log
            auditService.logApiCall(
                claim,
                "grouper_" + grouperType,
                "POST",
                objectMapper.writeValueAsString(request),
                null,
                objectMapper.writeValueAsString(response),
                null,
                "200",
                (int) executionTime,
                userId
            );

            log.info("Grouper {} executed for claim: {}, code: {}",
                grouperType, claimNumber, result.getCode());

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            auditService.logError(claim, "grouper_" + grouperType, "POST",
                claimNumber, e.getMessage(), (int) executionTime, userId);
            throw new EklaimIntegrationException("Grouper execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get claim by claim number.
     */
    private EklaimClaim getClaimByNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new EklaimIntegrationException(
                "Claim not found: " + claimNumber,
                "E2005"
            ));
    }

    /**
     * Validate claim status.
     */
    private void validateClaimStatus(EklaimClaim claim, Integer... allowedStatuses) {
        for (Integer status : allowedStatuses) {
            if (claim.getStatus().equals(status)) {
                return;
            }
        }
        throw new EklaimIntegrationException(
            "Invalid claim status: " + claim.getStatus() +
            ". Current operation requires status: " + java.util.Arrays.toString(allowedStatuses),
            "E2006"
        );
    }

    /**
     * Get active configuration by hospital code.
     */
    private EklaimConfig getActiveConfig(String hospitalCode) {
        return configRepository.findByHospitalCodeAndIsActiveTrue(hospitalCode)
            .orElseThrow(() -> new EklaimIntegrationException(
                "E-Klaim configuration not found for hospital: " + hospitalCode,
                "E2003"
            ));
    }

    /**
     * Get active configuration for claim (uses hospital from SEP data).
     */
    private EklaimConfig getActiveConfigForClaim(EklaimClaim claim) {
        // Extract hospital code from SEP data
        // For now, use a default approach - this should be extracted from actual SEP data
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> sepData = objectMapper.readValue(
                claim.getSepData(),
                Map.class
            );
            String hospitalCode = (String) sepData.get("hospital_code");
            return getActiveConfig(hospitalCode);
        } catch (Exception e) {
            throw new EklaimIntegrationException(
                "Failed to extract hospital code from claim",
                e
            );
        }
    }

    // TODO: Implement remaining 28 methods:
    // 3. get_claim_data
    // 4. delete_claim
    // 5. reedit_claim
    // 15. grouper_final
    // 16. special_cmg_option
    // 17. claim_prosthesis
    // 18. claim_final
    // 19. send_claim_individual
    // 20. send_claim_reconsider
    // 21. claim_print
    // 22. get_claim_status
    // 23. monitoring_klaim
    // 24. get_data_plafon
    // 25. claim_ba
    // 26. pantauan_jkn
    // 27-33. SITB methods
}
