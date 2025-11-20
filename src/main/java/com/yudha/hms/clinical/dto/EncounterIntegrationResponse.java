package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Encounter Integration Response DTO.
 *
 * Aggregates all module integrations for a single encounter.
 * Provides complete view of clinical documentation, orders, billing, and external integrations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterIntegrationResponse {

    // Encounter Basic Info
    private UUID encounterId;
    private String encounterNumber;
    private String encounterType;
    private LocalDateTime encounterStart;
    private LocalDateTime encounterEnd;
    private String status;

    // Clinical Documentation
    @Builder.Default
    private ClinicalDocumentationSummary clinicalDocumentation = new ClinicalDocumentationSummary();

    // Orders and Results
    @Builder.Default
    private OrdersAndResultsSummary ordersAndResults = new OrdersAndResultsSummary();

    // Billing Integration
    @Builder.Default
    private BillingIntegrationSummary billing = new BillingIntegrationSummary();

    // Pharmacy Integration
    @Builder.Default
    private PharmacyIntegrationSummary pharmacy = new PharmacyIntegrationSummary();

    // BPJS Integration
    @Builder.Default
    private BpjsIntegrationSummary bpjs = new BpjsIntegrationSummary();

    // SATUSEHAT Integration
    @Builder.Default
    private SatusehatIntegrationSummary satusehat = new SatusehatIntegrationSummary();

    // ========== Nested Summary Classes ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicalDocumentationSummary {
        private Integer totalProgressNotes;
        private Integer soapNotes;
        private Integer nursingNotes;
        private Integer criticalCareNotes;
        private LocalDateTime lastNoteDate;
        private Boolean hasCriticalFindings;
        @Builder.Default
        private List<ProgressNoteItemResponse> recentNotes = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdersAndResultsSummary {
        private Integer totalOrders;
        private Integer medicationOrders;
        private Integer laboratoryOrders;
        private Integer radiologyOrders;
        private Integer completedOrders;
        private Integer pendingOrders;
        private Boolean hasAbnormalResults;
        @Builder.Default
        private List<OrderItemResponse> recentOrders = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingIntegrationSummary {
        private Boolean billingGenerated;
        private String billingTransactionId;
        private LocalDateTime billingCreatedAt;
        private Double totalCharges;
        private Double totalPaid;
        private Double outstandingBalance;
        private String paymentStatus; // UNPAID, PARTIAL, PAID, BPJS_PENDING
        private String inaCbgCode; // For inpatient BPJS
        private String inaCbgDescription;
        private Double inaCbgTariff;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PharmacyIntegrationSummary {
        private Integer totalPrescriptions;
        private Integer activePrescriptions;
        private Integer dispensedPrescriptions;
        private Boolean allPrescriptionsValidated;
        private Boolean hasStockIssues;
        private LocalDateTime lastDispensedAt;
        @Builder.Default
        private List<PrescriptionItemResponse> prescriptions = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BpjsIntegrationSummary {
        private Boolean isBpjsEncounter;
        private String sepNumber;
        private LocalDate sepDate;
        private Boolean sepValidated;
        private String sepValidationMessage;
        private Boolean vclaimSubmitted;
        private LocalDateTime vclaimSubmittedAt;
        private String vclaimStatus; // PENDING, APPROVED, REJECTED
        private String claimNumber;
        private Boolean priorAuthorizationRequired;
        private Boolean priorAuthorizationApproved;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SatusehatIntegrationSummary {
        private Boolean synced;
        private String satusehatEncounterId;
        private LocalDateTime syncedAt;
        private String syncStatus; // NOT_SYNCED, PENDING, SYNCED, FAILED
        private String syncMessage;
        private Boolean patientSynced;
        private Boolean diagnosisSynced;
        private Boolean proceduresSynced;
        private Boolean observationsSynced;
        private LocalDateTime lastSyncAttempt;
        private Integer syncAttempts;
    }
}
