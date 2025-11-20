package com.yudha.hms.integration.eklaim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * E-Klaim Claim Entity.
 *
 * Represents a claim in the Indonesian E-Klaim 5.10.x Web Service system
 * for INA-CBGs (Indonesian Case-Based Groups) claim processing.
 *
 * Status values:
 * 1 = Draft (new_claim)
 * 2 = Ungrouped (data entry complete, not yet grouped)
 * 3 = iDRG Grouped (grouper_1 executed)
 * 4 = INACBG Grouped (grouper_2 executed)
 * 5 = Finalized (claim_final executed)
 * 6 = Submitted (send_claim_individual)
 * 7 = Verified (verified by BPJS)
 * 8 = Approved with BA (payment batch assigned)
 * 9 = Rejected (rejected by BPJS)
 * 10 = Resubmitted (send_claim_reconsider)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "eklaim_claims", indexes = {
    @Index(name = "idx_eklaim_nomor_sep", columnList = "nomor_sep"),
    @Index(name = "idx_eklaim_claim_number", columnList = "claim_number"),
    @Index(name = "idx_eklaim_status", columnList = "status"),
    @Index(name = "idx_eklaim_submission_date", columnList = "submission_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EklaimClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "claim_number", unique = true)
    private String claimNumber;

    @Column(name = "nomor_sep", nullable = false, unique = true)
    private String nomorSep;

    @Column(name = "status", nullable = false)
    private Integer status = 1; // Default: Draft

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;

    // JSONB fields for flexible data storage
    @Column(name = "sep_data", columnDefinition = "jsonb", nullable = false)
    private String sepData;

    @Column(name = "patient_data", columnDefinition = "jsonb")
    private String patientData;

    @Column(name = "admission_data", columnDefinition = "jsonb")
    private String admissionData;

    @Column(name = "billing_data", columnDefinition = "jsonb")
    private String billingData;

    @Column(name = "diagnosis_data", columnDefinition = "jsonb")
    private String diagnosisData;

    @Column(name = "procedure_data", columnDefinition = "jsonb")
    private String procedureData;

    // iDRG Results
    @Column(name = "idrg_code", length = 50)
    private String idrgCode;

    @Column(name = "idrg_tariff", precision = 15, scale = 2)
    private BigDecimal idrgTariff;

    @Column(name = "idrg_result", columnDefinition = "jsonb")
    private String idrgResult;

    // INA-CBGs Results
    @Column(name = "cbg_code", length = 50)
    private String cbgCode;

    @Column(name = "base_tariff", precision = 15, scale = 2)
    private BigDecimal baseTariff;

    // Top-up and adjustments
    @Column(name = "top_up_covid", precision = 15, scale = 2)
    private BigDecimal topUpCovid = BigDecimal.ZERO;

    @Column(name = "top_up_chronic", precision = 15, scale = 2)
    private BigDecimal topUpChronic = BigDecimal.ZERO;

    @Column(name = "upgrade_class", precision = 15, scale = 2)
    private BigDecimal upgradeClass = BigDecimal.ZERO;

    @Column(name = "special_cmg", precision = 15, scale = 2)
    private BigDecimal specialCmg = BigDecimal.ZERO;

    @Column(name = "special_prosthesis", precision = 15, scale = 2)
    private BigDecimal specialProsthesis = BigDecimal.ZERO;

    @Column(name = "special_drug", precision = 15, scale = 2)
    private BigDecimal specialDrug = BigDecimal.ZERO;

    @Column(name = "total_tariff", precision = 15, scale = 2)
    private BigDecimal totalTariff;

    @Column(name = "inacbg_result", columnDefinition = "jsonb")
    private String inacbgResult;

    @Column(name = "prosthesis_items", columnDefinition = "jsonb")
    private String prosthesisItems;

    @Column(name = "special_cmg_data", columnDefinition = "jsonb")
    private String specialCmgData;

    // SITB (TB Information System) Integration
    @Column(name = "is_tb_case")
    private Boolean isTbCase = false;

    @Column(name = "sitb_tasks", columnDefinition = "jsonb")
    private String sitbTasks;

    @Column(name = "sitb_completed")
    private Boolean sitbCompleted = false;

    // Finalization flags
    @Column(name = "idrg_finalized")
    private Boolean idrgFinalized = false;

    @Column(name = "inacbg_finalized")
    private Boolean inacbgFinalized = false;

    // Submission tracking
    @Column(name = "submission_receipt", length = 100)
    private String submissionReceipt;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    // Verification (from BPJS)
    @Column(name = "verifier_name", length = 200)
    private String verifierName;

    @Column(name = "verifier_notes", columnDefinition = "text")
    private String verifierNotes;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    // Payment
    @Column(name = "ba_number", length = 100)
    private String baNumber; // Berita Acara (Payment Batch Number)

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // Rejection handling
    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_claim_id")
    private EklaimClaim originalClaim; // For reedit_claim tracking

    // Audit fields
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}