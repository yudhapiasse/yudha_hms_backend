package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.ReturnReason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Drug Return Entity.
 *
 * Handles returns and exchanges of dispensed medications.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "drug_return", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_return_number", columnList = "return_number"),
        @Index(name = "idx_return_dispensing", columnList = "dispensing_id"),
        @Index(name = "idx_return_dispensing_item", columnList = "dispensing_item_id"),
        @Index(name = "idx_return_patient", columnList = "patient_id"),
        @Index(name = "idx_return_reason", columnList = "reason"),
        @Index(name = "idx_return_date", columnList = "return_date"),
        @Index(name = "idx_return_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "return_number", nullable = false, unique = true, length = 50)
    private String returnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_id", nullable = false)
    private Dispensing dispensing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_item_id", nullable = false)
    private DispensingItem dispensingItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", length = 200)
    private String patientName;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private ReturnReason reason;

    @Column(name = "reason_details", columnDefinition = "TEXT")
    private String reasonDetails;

    @Column(name = "quantity_returned", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityReturned;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "refund_amount", precision = 15, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_processed")
    private Boolean refundProcessed = false;

    @Column(name = "refund_method", length = 50)
    private String refundMethod;

    @Column(name = "status", length = 50)
    private String status = "PENDING";

    @Column(name = "can_restock")
    private Boolean canRestock;

    @Column(name = "restocked")
    private Boolean restocked = false;

    @Column(name = "restocked_at")
    private LocalDateTime restockedAt;

    @Column(name = "restocked_by_id")
    private UUID restockedById;

    @Column(name = "restocked_by_name", length = 200)
    private String restockedByName;

    @Column(name = "is_pharmacy_error")
    private Boolean isPharmacyError;

    @Column(name = "incident_report_required")
    private Boolean incidentReportRequired = false;

    @Column(name = "incident_report_number", length = 50)
    private String incidentReportNumber;

    @Column(name = "quality_issue")
    private Boolean qualityIssue = false;

    @Column(name = "supplier_notified")
    private Boolean supplierNotified = false;

    @Column(name = "returned_by_name", length = 200)
    private String returnedByName;

    @Column(name = "returned_by_relationship", length = 100)
    private String returnedByRelationship;

    @Column(name = "received_by_id")
    private UUID receivedById;

    @Column(name = "received_by_name", length = 200)
    private String receivedByName;

    @Column(name = "approved_by_id")
    private UUID approvedById;

    @Column(name = "approved_by_name", length = 200)
    private String approvedByName;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "active")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    // Business methods

    /**
     * Approve the return
     */
    public void approve(UUID approvedBy, String approvedByName) {
        if (isApproved) {
            throw new IllegalStateException("Return is already approved");
        }
        this.isApproved = true;
        this.approvedById = approvedBy;
        this.approvedByName = approvedByName;
        this.approvedAt = LocalDateTime.now();
        this.status = "APPROVED";

        // Set flags based on reason
        this.isPharmacyError = reason.isPharmacyError();
        this.incidentReportRequired = reason.requiresIncidentReport();
        this.qualityIssue = reason.isQualityIssue();
        this.canRestock = reason.allowsRestock();
    }

    /**
     * Reject the return
     */
    public void reject(String rejectionReason) {
        if (isApproved) {
            throw new IllegalStateException("Return is already approved");
        }
        this.status = "REJECTED";
        this.rejectionReason = rejectionReason;
    }

    /**
     * Process refund
     */
    public void processRefund(BigDecimal amount, String method) {
        if (!isApproved) {
            throw new IllegalStateException("Return must be approved before processing refund");
        }
        this.refundAmount = amount;
        this.refundMethod = method;
        this.refundProcessed = true;
    }

    /**
     * Restock the returned items
     */
    public void restock(UUID restockedBy, String restockedByName) {
        if (!isApproved) {
            throw new IllegalStateException("Return must be approved before restocking");
        }
        if (!canRestock) {
            throw new IllegalStateException("This return cannot be restocked: " + reason);
        }
        if (restocked) {
            throw new IllegalStateException("Items have already been restocked");
        }
        this.restocked = true;
        this.restockedById = restockedBy;
        this.restockedByName = restockedByName;
        this.restockedAt = LocalDateTime.now();
    }

    /**
     * Create incident report
     */
    public void createIncidentReport(String reportNumber) {
        if (!incidentReportRequired) {
            throw new IllegalStateException("Incident report is not required for this return reason");
        }
        this.incidentReportNumber = reportNumber;
    }

    /**
     * Notify supplier about quality issue
     */
    public void notifySupplier() {
        if (!qualityIssue) {
            throw new IllegalStateException("Supplier notification only required for quality issues");
        }
        this.supplierNotified = true;
    }

    /**
     * Calculate total amount
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotal() {
        if (unitPrice != null && quantityReturned != null) {
            this.totalAmount = unitPrice.multiply(quantityReturned);
            // By default, refund amount equals total amount unless specified otherwise
            if (this.refundAmount == null) {
                this.refundAmount = this.totalAmount;
            }
        }
    }
}
