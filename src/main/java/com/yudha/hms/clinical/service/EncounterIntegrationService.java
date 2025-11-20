package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.NoteType;
import com.yudha.hms.clinical.entity.ProgressNote;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.clinical.repository.ProgressNoteRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Encounter Integration Service.
 *
 * Orchestrates integration between encounters and all other HMS modules:
 * - Clinical Documentation (Progress Notes, SOAP notes)
 * - Orders and Results (Medication, Lab, Radiology)
 * - Billing (Auto-generate transactions, INA-CBG grouping)
 * - Pharmacy (Prescription validation, stock allocation)
 * - BPJS (SEP validation, VClaim submission)
 * - SATUSEHAT (FHIR R4 sync)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EncounterIntegrationService {

    private final EncounterRepository encounterRepository;
    private final ProgressNoteRepository progressNoteRepository;

    /**
     * Get complete integration data for an encounter.
     *
     * Aggregates data from all integrated modules into a single response.
     *
     * @param encounterId Encounter ID
     * @return Complete integration response
     */
    public EncounterIntegrationResponse getEncounterIntegration(UUID encounterId) {
        log.info("Fetching complete integration data for encounter: {}", encounterId);

        // Fetch encounter
        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter not found with ID: " + encounterId));

        // Build integration response
        EncounterIntegrationResponse response = EncounterIntegrationResponse.builder()
            .encounterId(encounter.getId())
            .encounterNumber(encounter.getEncounterNumber())
            .encounterType(encounter.getEncounterType().name())
            .encounterStart(encounter.getEncounterStart())
            .encounterEnd(encounter.getEncounterEnd())
            .status(encounter.getStatus().name())
            .build();

        // Populate each integration module
        response.setClinicalDocumentation(buildClinicalDocumentationSummary(encounterId));
        response.setOrdersAndResults(buildOrdersAndResultsSummary(encounter));
        response.setBilling(buildBillingIntegrationSummary(encounter));
        response.setPharmacy(buildPharmacyIntegrationSummary(encounter));
        response.setBpjs(buildBpjsIntegrationSummary(encounter));
        response.setSatusehat(buildSatusehatIntegrationSummary(encounter));

        log.info("Successfully built integration response for encounter: {}", encounterId);
        return response;
    }

    /**
     * Build clinical documentation summary from progress notes.
     */
    private EncounterIntegrationResponse.ClinicalDocumentationSummary buildClinicalDocumentationSummary(UUID encounterId) {
        List<ProgressNote> allNotes = progressNoteRepository.findByEncounterId(encounterId);

        long soapCount = allNotes.stream().filter(n -> n.getNoteType() == NoteType.SOAP).count();
        long nursingCount = allNotes.stream().filter(n -> n.getNoteType() == NoteType.NURSING).count();
        long criticalCareCount = allNotes.stream().filter(n -> n.getNoteType() == NoteType.CRITICAL_CARE).count();

        boolean hasCriticalFindings = allNotes.stream().anyMatch(ProgressNote::hasCriticalFindings);
        LocalDateTime lastNoteDate = allNotes.isEmpty() ? null : allNotes.get(0).getNoteDateTime();

        // Get recent notes (last 5)
        List<ProgressNoteItemResponse> recentNotes = allNotes.stream()
            .limit(5)
            .map(this::convertToProgressNoteItem)
            .collect(Collectors.toList());

        return EncounterIntegrationResponse.ClinicalDocumentationSummary.builder()
            .totalProgressNotes(allNotes.size())
            .soapNotes((int) soapCount)
            .nursingNotes((int) nursingCount)
            .criticalCareNotes((int) criticalCareCount)
            .lastNoteDate(lastNoteDate)
            .hasCriticalFindings(hasCriticalFindings)
            .recentNotes(recentNotes)
            .build();
    }

    /**
     * Build orders and results summary.
     * TODO: Implement when Laboratory and Radiology modules are ready.
     */
    private EncounterIntegrationResponse.OrdersAndResultsSummary buildOrdersAndResultsSummary(Encounter encounter) {
        // Placeholder implementation - will be implemented when Lab/Radiology modules exist
        return EncounterIntegrationResponse.OrdersAndResultsSummary.builder()
            .totalOrders(0)
            .medicationOrders(0)
            .laboratoryOrders(0)
            .radiologyOrders(0)
            .completedOrders(0)
            .pendingOrders(0)
            .hasAbnormalResults(false)
            .recentOrders(new ArrayList<>())
            .build();
    }

    /**
     * Build billing integration summary.
     * TODO: Implement when Billing module is ready.
     */
    private EncounterIntegrationResponse.BillingIntegrationSummary buildBillingIntegrationSummary(Encounter encounter) {
        // Placeholder implementation - will be implemented when Billing module exists
        boolean isBpjsInpatient = "BPJS".equals(encounter.getInsuranceType().name()) &&
                                  "INPATIENT".equals(encounter.getEncounterType().name());

        return EncounterIntegrationResponse.BillingIntegrationSummary.builder()
            .billingGenerated(false)
            .billingTransactionId(null)
            .billingCreatedAt(null)
            .totalCharges(0.0)
            .totalPaid(0.0)
            .outstandingBalance(0.0)
            .paymentStatus("UNPAID")
            .inaCbgCode(isBpjsInpatient ? "PENDING_GROUPING" : null)
            .inaCbgDescription(isBpjsInpatient ? "Awaiting discharge for INA-CBG grouping" : null)
            .inaCbgTariff(isBpjsInpatient ? 0.0 : null)
            .build();
    }

    /**
     * Build pharmacy integration summary.
     * TODO: Implement when Pharmacy module is ready.
     */
    private EncounterIntegrationResponse.PharmacyIntegrationSummary buildPharmacyIntegrationSummary(Encounter encounter) {
        // Placeholder implementation - will be implemented when Pharmacy module exists
        return EncounterIntegrationResponse.PharmacyIntegrationSummary.builder()
            .totalPrescriptions(0)
            .activePrescriptions(0)
            .dispensedPrescriptions(0)
            .allPrescriptionsValidated(true)
            .hasStockIssues(false)
            .lastDispensedAt(null)
            .prescriptions(new ArrayList<>())
            .build();
    }

    /**
     * Build BPJS integration summary.
     * Checks SEP validation status and VClaim submission.
     */
    private EncounterIntegrationResponse.BpjsIntegrationSummary buildBpjsIntegrationSummary(Encounter encounter) {
        boolean isBpjs = "BPJS".equals(encounter.getInsuranceType().name());

        if (!isBpjs) {
            return EncounterIntegrationResponse.BpjsIntegrationSummary.builder()
                .isBpjsEncounter(false)
                .build();
        }

        // Basic BPJS info from encounter
        boolean hasSep = encounter.getSepNumber() != null && !encounter.getSepNumber().isEmpty();

        return EncounterIntegrationResponse.BpjsIntegrationSummary.builder()
            .isBpjsEncounter(true)
            .sepNumber(encounter.getSepNumber())
            .sepDate(encounter.getSepDate())
            .sepValidated(hasSep)
            .sepValidationMessage(hasSep ? "SEP number recorded" : "SEP number missing")
            .vclaimSubmitted(false) // TODO: Check actual VClaim submission when BPJS module is implemented
            .vclaimSubmittedAt(null)
            .vclaimStatus("PENDING")
            .claimNumber(null)
            .priorAuthorizationRequired(false)
            .priorAuthorizationApproved(false)
            .build();
    }

    /**
     * Build SATUSEHAT integration summary.
     * Checks FHIR R4 sync status.
     */
    private EncounterIntegrationResponse.SatusehatIntegrationSummary buildSatusehatIntegrationSummary(Encounter encounter) {
        boolean isSynced = encounter.getSatusehatSynced() != null && encounter.getSatusehatSynced();
        String syncStatus = determineSatusehatSyncStatus(encounter);

        return EncounterIntegrationResponse.SatusehatIntegrationSummary.builder()
            .synced(isSynced)
            .satusehatEncounterId(encounter.getSatusehatEncounterId())
            .syncedAt(encounter.getSatusehatSyncedAt())
            .syncStatus(syncStatus)
            .syncMessage(isSynced ? "Encounter synced to SATUSEHAT" : "Awaiting SATUSEHAT sync")
            .patientSynced(false) // TODO: Check patient sync status when SATUSEHAT module is implemented
            .diagnosisSynced(false) // TODO: Check diagnosis sync status
            .proceduresSynced(false) // TODO: Check procedures sync status
            .observationsSynced(false) // TODO: Check observations sync status
            .lastSyncAttempt(encounter.getSatusehatSyncedAt())
            .syncAttempts(0) // TODO: Track sync attempts when SATUSEHAT module is implemented
            .build();
    }

    /**
     * Determine SATUSEHAT sync status.
     */
    private String determineSatusehatSyncStatus(Encounter encounter) {
        if (encounter.getSatusehatSynced() != null && encounter.getSatusehatSynced()) {
            return "SYNCED";
        }
        if (encounter.getSatusehatEncounterId() != null) {
            return "PENDING";
        }
        return "NOT_SYNCED";
    }

    /**
     * Convert ProgressNote entity to item response.
     */
    private ProgressNoteItemResponse convertToProgressNoteItem(ProgressNote note) {
        return ProgressNoteItemResponse.builder()
            .id(note.getId())
            .noteNumber(note.getNoteNumber())
            .noteType(note.getNoteType().name())
            .noteDateTime(note.getNoteDateTime())
            .shift(note.getShift() != null ? note.getShift().name() : null)
            .providerName(note.getProviderName())
            .providerType(note.getProviderType() != null ? note.getProviderType().name() : null)
            .isComplete(note.isComplete())
            .hasCriticalFindings(note.hasCriticalFindings())
            .assessment(truncate(note.getAssessment(), 200))
            .plan(truncate(note.getPlan(), 200))
            .build();
    }

    /**
     * Truncate text for summary display.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    /**
     * Get clinical documentation details for an encounter.
     *
     * @param encounterId Encounter ID
     * @return List of all progress notes
     */
    public List<ProgressNoteItemResponse> getClinicalDocumentation(UUID encounterId) {
        log.info("Fetching clinical documentation for encounter: {}", encounterId);

        List<ProgressNote> notes = progressNoteRepository.findByEncounterId(encounterId);

        return notes.stream()
            .map(this::convertToProgressNoteItem)
            .collect(Collectors.toList());
    }

    /**
     * Get SOAP notes specifically for an encounter.
     *
     * @param encounterId Encounter ID
     * @return List of SOAP notes
     */
    public List<ProgressNoteItemResponse> getSOAPNotes(UUID encounterId) {
        log.info("Fetching SOAP notes for encounter: {}", encounterId);

        List<ProgressNote> notes = progressNoteRepository.findByEncounterIdAndNoteType(
            encounterId, NoteType.SOAP
        );

        return notes.stream()
            .map(this::convertToProgressNoteItem)
            .collect(Collectors.toList());
    }

    /**
     * Check if encounter has complete clinical documentation.
     *
     * @param encounterId Encounter ID
     * @return true if encounter has at least one SOAP note
     */
    public boolean hasCompleteClinicalDocumentation(UUID encounterId) {
        long soapNoteCount = progressNoteRepository.findByEncounterIdAndNoteType(
            encounterId, NoteType.SOAP
        ).size();

        return soapNoteCount > 0;
    }
}
