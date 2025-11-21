package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.DispensingStatus;
import com.yudha.hms.pharmacy.constant.DispensingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Dispensing Entity.
 *
 * Represents a drug dispensing transaction with complete workflow tracking.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "dispensing", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_dispensing_number", columnList = "dispensing_number"),
        @Index(name = "idx_dispensing_prescription", columnList = "prescription_id"),
        @Index(name = "idx_dispensing_patient", columnList = "patient_id"),
        @Index(name = "idx_dispensing_status", columnList = "status"),
        @Index(name = "idx_dispensing_type", columnList = "type"),
        @Index(name = "idx_dispensing_location", columnList = "location_id"),
        @Index(name = "idx_dispensing_date", columnList = "dispensing_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dispensing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dispensing_number", nullable = false, unique = true, length = 50)
    private String dispensingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", length = 200)
    private String patientName;

    @Column(name = "patient_medical_record_number", length = 50)
    private String patientMedicalRecordNumber;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DispensingStatus status = DispensingStatus.QUEUE;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private DispensingType type;

    @Column(name = "priority")
    private Integer priority = 0;

    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    @Column(name = "dispensing_date")
    private LocalDateTime dispensingDate;

    @Column(name = "queue_number", length = 20)
    private String queueNumber;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "estimated_ready_time")
    private LocalDateTime estimatedReadyTime;

    @Column(name = "actual_ready_time")
    private LocalDateTime actualReadyTime;

    @OneToMany(mappedBy = "dispensing", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DispensingItem> items = new ArrayList<>();

    @Column(name = "total_items")
    private Integer totalItems = 0;

    @Column(name = "total_quantity", precision = 15, scale = 2)
    private BigDecimal totalQuantity = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "prepared_by_id")
    private UUID preparedById;

    @Column(name = "prepared_by_name", length = 200)
    private String preparedByName;

    @Column(name = "prepared_at")
    private LocalDateTime preparedAt;

    @Column(name = "verified_by_id")
    private UUID verifiedById;

    @Column(name = "verified_by_name", length = 200)
    private String verifiedByName;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "dispensed_by_id")
    private UUID dispensedById;

    @Column(name = "dispensed_by_name", length = 200)
    private String dispensedByName;

    @Column(name = "dispensed_at")
    private LocalDateTime dispensedAt;

    @Column(name = "received_by_name", length = 200)
    private String receivedByName;

    @Column(name = "received_by_relationship", length = 100)
    private String receivedByRelationship;

    @Column(name = "barcode_scanned")
    private Boolean barcodeScanned = false;

    @Column(name = "verification_passed")
    private Boolean verificationPassed;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Column(name = "counseling_required")
    private Boolean counselingRequired = false;

    @Column(name = "counseling_completed")
    private Boolean counselingCompleted = false;

    @Column(name = "labels_printed")
    private Boolean labelsPrinted = false;

    @Column(name = "labels_printed_at")
    private LocalDateTime labelsPrintedAt;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

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
     * Start preparing the dispensing
     */
    public void startPreparing(UUID preparedBy, String preparedByName) {
        if (!status.canBePrepared()) {
            throw new IllegalStateException("Cannot start preparing in current status: " + status);
        }
        this.status = DispensingStatus.PREPARING;
        this.preparedById = preparedBy;
        this.preparedByName = preparedByName;
        this.preparedAt = LocalDateTime.now();
    }

    /**
     * Submit for verification
     */
    public void submitForVerification() {
        if (status != DispensingStatus.PREPARING) {
            throw new IllegalStateException("Cannot submit for verification in current status: " + status);
        }
        this.status = DispensingStatus.VERIFICATION;
    }

    /**
     * Verify the dispensing
     */
    public void verify(UUID verifiedBy, String verifiedByName, boolean passed, String notes) {
        if (!status.canBeVerified()) {
            throw new IllegalStateException("Cannot verify in current status: " + status);
        }
        this.verifiedById = verifiedBy;
        this.verifiedByName = verifiedByName;
        this.verifiedAt = LocalDateTime.now();
        this.verificationPassed = passed;
        this.verificationNotes = notes;

        if (passed) {
            this.status = DispensingStatus.READY;
            this.actualReadyTime = LocalDateTime.now();
        } else {
            this.status = DispensingStatus.PREPARING;
        }
    }

    /**
     * Dispense to patient
     */
    public void dispense(UUID dispensedBy, String dispensedByName, String receivedBy, String relationship) {
        if (!status.canBeDispensed()) {
            throw new IllegalStateException("Cannot dispense in current status: " + status);
        }

        // Check if counseling is required and completed
        if (counselingRequired && !counselingCompleted) {
            throw new IllegalStateException("Counseling must be completed before dispensing");
        }

        this.status = DispensingStatus.DISPENSED;
        this.dispensedById = dispensedBy;
        this.dispensedByName = dispensedByName;
        this.dispensedAt = LocalDateTime.now();
        this.dispensingDate = LocalDateTime.now();
        this.receivedByName = receivedBy;
        this.receivedByRelationship = relationship;
    }

    /**
     * Put dispensing on hold
     */
    public void putOnHold(String reason) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot put on hold - dispensing is already finalized");
        }
        this.status = DispensingStatus.ON_HOLD;
        if (this.notes == null) {
            this.notes = reason;
        } else {
            this.notes += "\nOn Hold: " + reason;
        }
    }

    /**
     * Cancel dispensing
     */
    public void cancel(String reason) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot cancel - dispensing is already finalized");
        }
        this.status = DispensingStatus.CANCELLED;
        if (this.notes == null) {
            this.notes = reason;
        } else {
            this.notes += "\nCancelled: " + reason;
        }
    }

    /**
     * Print labels
     */
    public void printLabels() {
        this.labelsPrinted = true;
        this.labelsPrintedAt = LocalDateTime.now();
    }

    /**
     * Mark counseling as completed
     */
    public void completeCounseling() {
        this.counselingCompleted = true;
    }

    /**
     * Calculate totals from items
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotals() {
        if (items != null && !items.isEmpty()) {
            this.totalItems = items.size();
            this.totalQuantity = items.stream()
                    .map(DispensingItem::getQuantityDispensed)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.subtotal = items.stream()
                    .map(item -> item.getUnitPrice().multiply(item.getQuantityDispensed()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate total with discount and tax
            BigDecimal afterDiscount = subtotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
            this.totalAmount = afterDiscount.add(taxAmount != null ? taxAmount : BigDecimal.ZERO);
        }
    }
}
