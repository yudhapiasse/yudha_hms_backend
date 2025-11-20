package com.yudha.hms.integration.bpjs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.bpjs.dto.apotek.*;
import com.yudha.hms.integration.bpjs.exception.BpjsHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Apotek Service Implementation.
 *
 * Provides comprehensive pharmacy services for BPJS Apotek integration:
 * - Reference data (DPHO formulary, drug search)
 * - Prescription management (save, delete, list)
 * - Drug dispensing (non-racikan, racikan)
 * - PRB (Program Rujuk Balik) tracking
 * - Formulary compliance validation
 *
 * Apotek is BPJS's pharmacy system for tracking drug prescriptions,
 * dispensing, and ensuring formulary compliance.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApotekService {

    private final BpjsHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========== REFERENCE DATA SERVICES ==========

    /**
     * Get DPHO (Daftar Plafon Harga Obat) formulary.
     * Returns complete list of BPJS-covered drugs with price ceilings.
     *
     * @return DPHO response with drug formulary
     */
    public DphoResponse getDpho() {
        String endpoint = "/referensi/dpho";

        log.info("Getting DPHO formulary from BPJS Apotek");

        try {
            JsonNode response = httpClient.apotekGet(endpoint);
            return objectMapper.treeToValue(response, DphoResponse.class);

        } catch (Exception e) {
            log.error("Failed to get DPHO formulary", e);
            throw new BpjsHttpException("Failed to get DPHO: " + e.getMessage(), e);
        }
    }

    /**
     * Search drugs by type, date, and filter.
     *
     * @param kodeJenisObat Drug type code (1=PRB, 2=Chronic, 3=Chemo)
     * @param tglResep Prescription date
     * @param filter Search filter (drug name/code)
     * @return Drug search response
     */
    public DrugSearchResponse searchDrugs(String kodeJenisObat, LocalDate tglResep, String filter) {
        String endpoint = String.format("/referensi/obat/%s/%s/%s",
            kodeJenisObat,
            tglResep.format(DATE_FORMATTER),
            filter);

        log.info("Searching drugs - Type: {}, Date: {}, Filter: {}",
            kodeJenisObat, tglResep, filter);

        try {
            JsonNode response = httpClient.apotekGet(endpoint);
            return objectMapper.treeToValue(response, DrugSearchResponse.class);

        } catch (Exception e) {
            log.error("Failed to search drugs", e);
            throw new BpjsHttpException("Failed to search drugs: " + e.getMessage(), e);
        }
    }

    /**
     * Search drugs by type.
     * Convenience method using DrugType enum.
     *
     * @param drugType Drug type enum
     * @param tglResep Prescription date
     * @param filter Search filter
     * @return Drug search response
     */
    public DrugSearchResponse searchDrugsByType(DrugType drugType, LocalDate tglResep, String filter) {
        return searchDrugs(String.valueOf(drugType.getCode()), tglResep, filter);
    }

    /**
     * Get poli reference for pharmacy.
     *
     * @return Raw JSON poli reference
     */
    public JsonNode getPoliReference() {
        String endpoint = "/referensi/poli";

        log.info("Getting poli reference from BPJS Apotek");

        try {
            return httpClient.apotekGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get poli reference", e);
            throw new BpjsHttpException("Failed to get poli reference: " + e.getMessage(), e);
        }
    }

    /**
     * Get facility reference.
     *
     * @return Raw JSON facility reference
     */
    public JsonNode getFacilityReference() {
        String endpoint = "/referensi/faskes";

        log.info("Getting facility reference from BPJS Apotek");

        try {
            return httpClient.apotekGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get facility reference", e);
            throw new BpjsHttpException("Failed to get facility reference: " + e.getMessage(), e);
        }
    }

    /**
     * Get subspecialist reference.
     *
     * @return Raw JSON subspecialist reference
     */
    public JsonNode getSubspecialistReference() {
        String endpoint = "/referensi/spesialis";

        log.info("Getting subspecialist reference from BPJS Apotek");

        try {
            return httpClient.apotekGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get subspecialist reference", e);
            throw new BpjsHttpException("Failed to get subspecialist reference: " + e.getMessage(), e);
        }
    }

    // ========== PRESCRIPTION SERVICES ==========

    /**
     * Save prescription in BPJS Apotek system.
     *
     * @param request Prescription request
     * @return Prescription response with pharmacy number
     */
    public PrescriptionResponse savePrescription(PrescriptionRequest request) {
        String endpoint = "/sjpresep/v3/insert";

        log.info("Saving prescription - SEP: {}, Resep: {}, Type: {}",
            request.getRefasalsjp(), request.getNoresep(), request.getKdjnsobat());

        try {
            JsonNode response = httpClient.apotekPost(endpoint, request);
            PrescriptionResponse prescriptionResponse =
                objectMapper.treeToValue(response, PrescriptionResponse.class);

            if (prescriptionResponse.getMetaData().getCode().equals("200")) {
                log.info("Successfully saved prescription - No Apotik: {}",
                    prescriptionResponse.getResponse().getNoApotik());
            } else {
                log.warn("Failed to save prescription: {}",
                    prescriptionResponse.getMetaData().getMessage());
            }

            return prescriptionResponse;

        } catch (Exception e) {
            log.error("Failed to save prescription", e);
            throw new BpjsHttpException("Failed to save prescription: " + e.getMessage(), e);
        }
    }

    /**
     * Delete prescription from BPJS system.
     *
     * @param noApotik Pharmacy number (from prescription response)
     * @return Operation response
     */
    public ApotekOperationResponse deletePrescription(String noApotik) {
        String endpoint = "/sjpresep/v3/delete";

        log.info("Deleting prescription - No Apotik: {}", noApotik);

        try {
            var request = new java.util.HashMap<String, String>();
            request.put("noApotik", noApotik);

            JsonNode response = httpClient.apotekPost(endpoint, request);
            ApotekOperationResponse operationResponse =
                objectMapper.treeToValue(response, ApotekOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully deleted prescription: {}", noApotik);
            } else {
                log.warn("Failed to delete prescription: {} - {}",
                    noApotik, operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to delete prescription", e);
            throw new BpjsHttpException("Failed to delete prescription: " + e.getMessage(), e);
        }
    }

    /**
     * Get prescription list by SEP number and date range.
     *
     * @param noSep SEP number
     * @param tglAwal Start date
     * @param tglAkhir End date
     * @return Raw JSON prescription list
     */
    public JsonNode getPrescriptionList(String noSep, LocalDate tglAwal, LocalDate tglAkhir) {
        String endpoint = String.format("/sjpresep/v3/nosep/%s/tglawal/%s/tglakhir/%s",
            noSep,
            tglAwal.format(DATE_FORMATTER),
            tglAkhir.format(DATE_FORMATTER));

        log.info("Getting prescription list - SEP: {}, From: {} To: {}",
            noSep, tglAwal, tglAkhir);

        try {
            return httpClient.apotekGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get prescription list", e);
            throw new BpjsHttpException("Failed to get prescription list: " + e.getMessage(), e);
        }
    }

    // ========== DRUG DISPENSING SERVICES ==========

    /**
     * Save non-racikan (non-compounded) drug dispensing.
     *
     * @param request Drug dispensing request
     * @return Operation response
     */
    public ApotekOperationResponse saveNonRacikanDrug(DrugDispensingRequest request) {
        String endpoint = "/obatnonracikan/v3/insert";

        log.info("Saving non-racikan drug - Resep: {}, Drug: {} ({})",
            request.getNoresep(), request.getKdobt(), request.getNmobat());

        try {
            JsonNode response = httpClient.apotekPost(endpoint, request);
            ApotekOperationResponse operationResponse =
                objectMapper.treeToValue(response, ApotekOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully saved non-racikan drug: {}", request.getKdobt());
            } else {
                log.warn("Failed to save non-racikan drug: {} - {}",
                    request.getKdobt(), operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to save non-racikan drug", e);
            throw new BpjsHttpException("Failed to save drug: " + e.getMessage(), e);
        }
    }

    /**
     * Save racikan (compounded) drug dispensing.
     *
     * @param request Drug dispensing request (racikan format)
     * @return Operation response
     */
    public ApotekOperationResponse saveRacikanDrug(Object request) {
        String endpoint = "/obatracikan/v3/insert";

        log.info("Saving racikan drug");

        try {
            JsonNode response = httpClient.apotekPost(endpoint, request);
            ApotekOperationResponse operationResponse =
                objectMapper.treeToValue(response, ApotekOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully saved racikan drug");
            } else {
                log.warn("Failed to save racikan drug: {}", operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to save racikan drug", e);
            throw new BpjsHttpException("Failed to save racikan drug: " + e.getMessage(), e);
        }
    }

    /**
     * Delete drug dispensing.
     *
     * @param noApotik Pharmacy number
     * @param noResep Prescription number
     * @return Operation response
     */
    public ApotekOperationResponse deleteDrugDispensing(String noApotik, String noResep) {
        String endpoint = "/obat/v3/delete";

        log.info("Deleting drug dispensing - No Apotik: {}, No Resep: {}",
            noApotik, noResep);

        try {
            var request = new java.util.HashMap<String, String>();
            request.put("noApotik", noApotik);
            request.put("noResep", noResep);

            JsonNode response = httpClient.apotekPost(endpoint, request);
            ApotekOperationResponse operationResponse =
                objectMapper.treeToValue(response, ApotekOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully deleted drug dispensing");
            } else {
                log.warn("Failed to delete drug dispensing: {}",
                    operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to delete drug dispensing", e);
            throw new BpjsHttpException("Failed to delete drug dispensing: " + e.getMessage(), e);
        }
    }

    /**
     * Get drug dispensing list by pharmacy number.
     *
     * @param noApotik Pharmacy number
     * @return Raw JSON drug list
     */
    public JsonNode getDrugDispensingList(String noApotik) {
        String endpoint = String.format("/obat/v3/noapotik/%s", noApotik);

        log.info("Getting drug dispensing list - No Apotik: {}", noApotik);

        try {
            return httpClient.apotekGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get drug dispensing list", e);
            throw new BpjsHttpException("Failed to get drug list: " + e.getMessage(), e);
        }
    }

    /**
     * Get drug dispensing history by BPJS card number.
     *
     * @param noKartu BPJS card number
     * @param tglAwal Start date
     * @param tglAkhir End date
     * @return Raw JSON dispensing history
     */
    public JsonNode getDrugDispensingHistory(String noKartu, LocalDate tglAwal, LocalDate tglAkhir) {
        String endpoint = String.format("/obat/v3/nokartu/%s/tglawal/%s/tglakhir/%s",
            noKartu,
            tglAwal.format(DATE_FORMATTER),
            tglAkhir.format(DATE_FORMATTER));

        log.info("Getting drug dispensing history - Card: {}, From: {} To: {}",
            noKartu, tglAwal, tglAkhir);

        try {
            return httpClient.apotekGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get drug dispensing history", e);
            throw new BpjsHttpException("Failed to get drug history: " + e.getMessage(), e);
        }
    }

    // ========== SEP/VISIT SERVICES ==========

    /**
     * Search visit/SEP number for pharmacy.
     *
     * @param noKartu BPJS card number
     * @param tanggal Visit date
     * @return Raw JSON SEP/visit information
     */
    public JsonNode searchVisitNumber(String noKartu, LocalDate tanggal) {
        String endpoint = String.format("/sep/nokartu/%s/tgl/%s",
            noKartu, tanggal.format(DATE_FORMATTER));

        log.info("Searching visit number - Card: {}, Date: {}", noKartu, tanggal);

        try {
            return httpClient.apotekGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to search visit number", e);
            throw new BpjsHttpException("Failed to search visit: " + e.getMessage(), e);
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Validate drug against DPHO formulary.
     *
     * @param drugCode Drug code to validate
     * @return true if drug is in formulary
     */
    public boolean validateDrugInFormulary(String drugCode) {
        try {
            DphoResponse dpho = getDpho();
            return dpho.getResponse().getList().stream()
                .anyMatch(drug -> drug.getKodeobat().equals(drugCode));
        } catch (Exception e) {
            log.error("Failed to validate drug in formulary", e);
            return false;
        }
    }

    /**
     * Check if drug is PRB (Program Rujuk Balik).
     *
     * @param drugCode Drug code
     * @return true if drug is PRB
     */
    public boolean isPrbDrug(String drugCode) {
        try {
            DphoResponse dpho = getDpho();
            return dpho.getResponse().getList().stream()
                .filter(drug -> drug.getKodeobat().equals(drugCode))
                .anyMatch(drug -> "True".equalsIgnoreCase(drug.getPrb()));
        } catch (Exception e) {
            log.error("Failed to check PRB drug status", e);
            return false;
        }
    }

    /**
     * Get drug maximum price from DPHO.
     *
     * @param drugCode Drug code
     * @return Maximum price or null if not found
     */
    public String getDrugMaxPrice(String drugCode) {
        try {
            DphoResponse dpho = getDpho();
            return dpho.getResponse().getList().stream()
                .filter(drug -> drug.getKodeobat().equals(drugCode))
                .map(DphoResponse.Drug::getHarga)
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            log.error("Failed to get drug max price", e);
            return null;
        }
    }

    /**
     * Check if Apotek integration is enabled.
     *
     * @return true if integration is ready
     */
    public boolean isApotekEnabled() {
        return true; // Enabled if BPJS is configured
    }
}
