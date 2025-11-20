package com.yudha.hms.integration.bpjs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.bpjs.dto.BpjsParticipantResponse;
import com.yudha.hms.integration.bpjs.dto.vclaim.*;
import com.yudha.hms.integration.bpjs.exception.BpjsHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * VClaim Service Implementation.
 *
 * Provides comprehensive VClaim services for BPJS claim management:
 * - Participant eligibility checking
 * - Referral management
 * - SEP (Surat Eligibilitas Peserta) operations
 * - Monitoring and reporting
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VClaimService {

    private final BpjsHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ========== PARTICIPANT (PESERTA) SERVICES ==========

    /**
     * Check participant eligibility by BPJS card number.
     *
     * @param noKartu BPJS card number
     * @param tglSep Service date
     * @return Participant information
     */
    public BpjsParticipantResponse checkEligibilityByCardNumber(String noKartu, LocalDate tglSep) {
        String endpoint = String.format("/Peserta/nokartu/%s/tglSEP/%s",
            noKartu, tglSep.format(DATE_FORMATTER));

        log.info("Checking BPJS eligibility by card number: {} for date: {}", noKartu, tglSep);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, BpjsParticipantResponse.class);

        } catch (Exception e) {
            log.error("Failed to check eligibility by card number: {}", noKartu, e);
            throw new BpjsHttpException("Failed to check participant eligibility: " + e.getMessage(), e);
        }
    }

    /**
     * Check participant eligibility by NIK (National ID).
     *
     * @param nik National ID number
     * @param tglSep Service date
     * @return Participant information
     */
    public BpjsParticipantResponse checkEligibilityByNIK(String nik, LocalDate tglSep) {
        String endpoint = String.format("/Peserta/nik/%s/tglSEP/%s",
            nik, tglSep.format(DATE_FORMATTER));

        log.info("Checking BPJS eligibility by NIK: {} for date: {}", nik, tglSep);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, BpjsParticipantResponse.class);

        } catch (Exception e) {
            log.error("Failed to check eligibility by NIK: {}", nik, e);
            throw new BpjsHttpException("Failed to check participant eligibility: " + e.getMessage(), e);
        }
    }

    // ========== REFERRAL (RUJUKAN) SERVICES ==========

    /**
     * Search referral by referral number from PCare.
     *
     * @param noRujukan Referral number
     * @return Referral information
     */
    public RujukanResponse searchRujukanByNomorPCare(String noRujukan) {
        String endpoint = String.format("/Rujukan/%s", noRujukan);

        log.info("Searching PCare rujukan by number: {}", noRujukan);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, RujukanResponse.class);

        } catch (Exception e) {
            log.error("Failed to search rujukan by number: {}", noRujukan, e);
            throw new BpjsHttpException("Failed to search rujukan: " + e.getMessage(), e);
        }
    }

    /**
     * Search referral by referral number from Hospital (RS).
     *
     * @param noRujukan Referral number
     * @return Referral information
     */
    public RujukanResponse searchRujukanByNomorRS(String noRujukan) {
        String endpoint = String.format("/Rujukan/RS/%s", noRujukan);

        log.info("Searching RS rujukan by number: {}", noRujukan);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, RujukanResponse.class);

        } catch (Exception e) {
            log.error("Failed to search RS rujukan by number: {}", noRujukan, e);
            throw new BpjsHttpException("Failed to search rujukan: " + e.getMessage(), e);
        }
    }

    /**
     * Search referral by BPJS card number from PCare (single record).
     *
     * @param noKartu BPJS card number
     * @return Referral information
     */
    public RujukanResponse searchRujukanByKartuPCare(String noKartu) {
        String endpoint = String.format("/Rujukan/Peserta/%s", noKartu);

        log.info("Searching PCare rujukan by card number: {}", noKartu);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, RujukanResponse.class);

        } catch (Exception e) {
            log.error("Failed to search rujukan by card number: {}", noKartu, e);
            throw new BpjsHttpException("Failed to search rujukan: " + e.getMessage(), e);
        }
    }

    /**
     * Search referral by BPJS card number from Hospital (RS, single record).
     *
     * @param noKartu BPJS card number
     * @return Referral information
     */
    public RujukanResponse searchRujukanByKartuRS(String noKartu) {
        String endpoint = String.format("/Rujukan/RS/Peserta/%s", noKartu);

        log.info("Searching RS rujukan by card number: {}", noKartu);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, RujukanResponse.class);

        } catch (Exception e) {
            log.error("Failed to search RS rujukan by card number: {}", noKartu, e);
            throw new BpjsHttpException("Failed to search rujukan: " + e.getMessage(), e);
        }
    }

    /**
     * Search multiple referrals by BPJS card number from PCare.
     *
     * @param noKartu BPJS card number
     * @return Raw JSON response with multiple rujukan records
     */
    public JsonNode searchRujukanMultiByKartuPCare(String noKartu) {
        String endpoint = String.format("/Rujukan/List/Peserta/%s", noKartu);

        log.info("Searching multiple PCare rujukan by card number: {}", noKartu);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            return response.get("response");

        } catch (Exception e) {
            log.error("Failed to search multiple rujukan by card number: {}", noKartu, e);
            throw new BpjsHttpException("Failed to search rujukan: " + e.getMessage(), e);
        }
    }

    /**
     * Search multiple referrals by BPJS card number from Hospital (RS).
     *
     * @param noKartu BPJS card number
     * @return Raw JSON response with multiple rujukan records
     */
    public JsonNode searchRujukanMultiByKartuRS(String noKartu) {
        String endpoint = String.format("/Rujukan/RS/List/Peserta/%s", noKartu);

        log.info("Searching multiple RS rujukan by card number: {}", noKartu);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            return response.get("response");

        } catch (Exception e) {
            log.error("Failed to search multiple RS rujukan by card number: {}", noKartu, e);
            throw new BpjsHttpException("Failed to search rujukan: " + e.getMessage(), e);
        }
    }

    // ========== SEP (SURAT ELIGIBILITAS PESERTA) SERVICES ==========

    /**
     * Create new SEP (Version 2.0).
     *
     * @param request SEP creation request
     * @return SEP creation response
     */
    public SepInsertResponse createSep(SepInsertRequest request) {
        String endpoint = "/SEP/2.0/insert";

        log.info("Creating SEP for card number: {}", request.getRequest().getTSep().getNoKartu());

        try {
            JsonNode response = httpClient.vclaimPost(endpoint, request);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, SepInsertResponse.class);

        } catch (Exception e) {
            log.error("Failed to create SEP", e);
            throw new BpjsHttpException("Failed to create SEP: " + e.getMessage(), e);
        }
    }

    /**
     * Update existing SEP (Version 2.0).
     *
     * @param request SEP update request
     * @return Updated SEP response
     */
    public SepInsertResponse updateSep(SepInsertRequest request) {
        String endpoint = "/SEP/2.0/update";

        log.info("Updating SEP for card number: {}", request.getRequest().getTSep().getNoKartu());

        try {
            JsonNode response = httpClient.vclaimPost(endpoint, request);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, SepInsertResponse.class);

        } catch (Exception e) {
            log.error("Failed to update SEP", e);
            throw new BpjsHttpException("Failed to update SEP: " + e.getMessage(), e);
        }
    }

    /**
     * Delete SEP (Version 2.0).
     *
     * @param noSep SEP number to delete
     * @param user User who deletes the SEP
     * @return Deletion response
     */
    public JsonNode deleteSep(String noSep, String user) {
        String endpoint = "/SEP/2.0/delete";

        log.info("Deleting SEP: {} by user: {}", noSep, user);

        try {
            var deleteRequest = new java.util.HashMap<String, Object>();
            deleteRequest.put("request", new java.util.HashMap<String, Object>() {{
                put("t_sep", new java.util.HashMap<String, String>() {{
                    put("noSep", noSep);
                    put("user", user);
                }});
            }});

            JsonNode response = httpClient.vclaimPost(endpoint, deleteRequest);
            return response.get("response");

        } catch (Exception e) {
            log.error("Failed to delete SEP: {}", noSep, e);
            throw new BpjsHttpException("Failed to delete SEP: " + e.getMessage(), e);
        }
    }

    /**
     * Search SEP by SEP number.
     *
     * @param noSep SEP number
     * @return SEP information
     */
    public JsonNode searchSep(String noSep) {
        String endpoint = String.format("/SEP/%s", noSep);

        log.info("Searching SEP by number: {}", noSep);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            return response.get("response");

        } catch (Exception e) {
            log.error("Failed to search SEP: {}", noSep, e);
            throw new BpjsHttpException("Failed to search SEP: " + e.getMessage(), e);
        }
    }

    /**
     * Search last SEP by referral number.
     *
     * @param noRujukan Referral number
     * @return Last SEP information
     */
    public JsonNode searchSepByRujukan(String noRujukan) {
        String endpoint = String.format("/SEP/rujukan/%s", noRujukan);

        log.info("Searching last SEP by rujukan: {}", noRujukan);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            return response.get("response");

        } catch (Exception e) {
            log.error("Failed to search SEP by rujukan: {}", noRujukan, e);
            throw new BpjsHttpException("Failed to search SEP: " + e.getMessage(), e);
        }
    }

    // ========== MONITORING & REPORTING SERVICES ==========

    /**
     * Get visit data (data kunjungan) by date and service type.
     *
     * @param tanggal Visit date
     * @param jnsPelayanan Service type (1=Inpatient, 2=Outpatient)
     * @return Visit monitoring data
     */
    public MonitoringKunjunganResponse getDataKunjungan(LocalDate tanggal, String jnsPelayanan) {
        String endpoint = String.format("/Monitoring/Kunjungan/Tanggal/%s/JnsPelayanan/%s",
            tanggal.format(DATE_FORMATTER), jnsPelayanan);

        log.info("Getting kunjungan data for date: {} and service type: {}", tanggal, jnsPelayanan);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            JsonNode responseData = response.get("response");

            return objectMapper.treeToValue(responseData, MonitoringKunjunganResponse.class);

        } catch (Exception e) {
            log.error("Failed to get kunjungan data for date: {}", tanggal, e);
            throw new BpjsHttpException("Failed to get kunjungan data: " + e.getMessage(), e);
        }
    }

    /**
     * Get claim data (data klaim) by date range and service type.
     *
     * @param tglMulai Start date
     * @param tglAkhir End date
     * @param jnsPelayanan Service type (1=Inpatient, 2=Outpatient)
     * @return Raw JSON claim data
     */
    public JsonNode getDataKlaim(LocalDate tglMulai, LocalDate tglAkhir, String jnsPelayanan) {
        String endpoint = String.format("/Monitoring/Klaim/Tanggal/%s/sampai/%s/JnsPelayanan/%s",
            tglMulai.format(DATE_FORMATTER),
            tglAkhir.format(DATE_FORMATTER),
            jnsPelayanan);

        log.info("Getting klaim data from {} to {} for service type: {}",
            tglMulai, tglAkhir, jnsPelayanan);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            return response.get("response");

        } catch (Exception e) {
            log.error("Failed to get klaim data", e);
            throw new BpjsHttpException("Failed to get klaim data: " + e.getMessage(), e);
        }
    }

    /**
     * Get participant service history.
     *
     * @param noKartu BPJS card number
     * @param tglMulai Start date
     * @param tglAkhir End date
     * @return Raw JSON service history
     */
    public JsonNode getHistoriPelayanan(String noKartu, LocalDate tglMulai, LocalDate tglAkhir) {
        String endpoint = String.format("/Monitoring/HistoriPelayanan/NoKartu/%s/tglMulai/%s/tglAkhir/%s",
            noKartu,
            tglMulai.format(DATE_FORMATTER),
            tglAkhir.format(DATE_FORMATTER));

        log.info("Getting service history for card: {} from {} to {}",
            noKartu, tglMulai, tglAkhir);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            return response.get("response");

        } catch (Exception e) {
            log.error("Failed to get service history for card: {}", noKartu, e);
            throw new BpjsHttpException("Failed to get service history: " + e.getMessage(), e);
        }
    }

    /**
     * Get Jasa Raharja claim data (for traffic accidents).
     *
     * @param tglMulai Start date
     * @param tglAkhir End date
     * @return Raw JSON Jasa Raharja claim data
     */
    public JsonNode getDataKlaimJasaRaharja(LocalDate tglMulai, LocalDate tglAkhir) {
        String endpoint = String.format("/Monitoring/JasaRaharja/Tanggal/%s/sampai/%s",
            tglMulai.format(DATE_FORMATTER),
            tglAkhir.format(DATE_FORMATTER));

        log.info("Getting Jasa Raharja klaim data from {} to {}", tglMulai, tglAkhir);

        try {
            JsonNode response = httpClient.vclaimGet(endpoint);
            return response.get("response");

        } catch (Exception e) {
            log.error("Failed to get Jasa Raharja klaim data", e);
            throw new BpjsHttpException("Failed to get Jasa Raharja data: " + e.getMessage(), e);
        }
    }
}
