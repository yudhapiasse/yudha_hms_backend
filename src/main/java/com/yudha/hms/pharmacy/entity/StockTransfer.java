package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.TransferStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stock Transfer Entity.
 *
 * Manages stock transfers between pharmacy locations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "stock_transfer", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_transfer_number", columnList = "transfer_number", unique = true),
        @Index(name = "idx_transfer_from_location", columnList = "from_location_id"),
        @Index(name = "idx_transfer_to_location", columnList = "to_location_id"),
        @Index(name = "idx_transfer_status", columnList = "status"),
        @Index(name = "idx_transfer_date", columnList = "transfer_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockTransfer extends SoftDeletableEntity {

    @Column(name = "transfer_number", nullable = false, unique = true, length = 50)
    private String transferNumber;

    @Column(name = "from_location_id", nullable = false)
    private UUID fromLocationId;

    @Column(name = "from_location_name", length = 200)
    private String fromLocationName;

    @Column(name = "to_location_id", nullable = false)
    private UUID toLocationId;

    @Column(name = "to_location_name", length = 200)
    private String toLocationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private TransferStatus status = TransferStatus.DRAFT;

    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Column(name = "expected_arrival_date")
    private LocalDate expectedArrivalDate;

    @Column(name = "actual_arrival_date")
    private LocalDate actualArrivalDate;

    @Column(name = "requested_by_id")
    private UUID requestedById;

    @Column(name = "requested_by_name", length = 200)
    private String requestedByName;

    @Column(name = "approved_by_id")
    private UUID approvedById;

    @Column(name = "approved_by_name", length = 200)
    private String approvedByName;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "sent_by_id")
    private UUID sentById;

    @Column(name = "sent_by_name", length = 200)
    private String sentByName;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "received_by_id")
    private UUID receivedById;

    @Column(name = "received_by_name", length = 200)
    private String receivedByName;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "stockTransfer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockTransferItem> items = new ArrayList<>();

    public void approve(UUID approvedBy, String approvedByName) {
        if (!status.canBeApproved()) {
            throw new IllegalStateException("Cannot approve in current status: " + status);
        }
        this.status = TransferStatus.APPROVED;
        this.approvedById = approvedBy;
        this.approvedByName = approvedByName;
        this.approvedAt = LocalDateTime.now();
    }

    public void send(UUID sentBy, String sentByName) {
        if (!status.canBeSent()) {
            throw new IllegalStateException("Cannot send in current status: " + status);
        }
        this.status = TransferStatus.IN_TRANSIT;
        this.sentById = sentBy;
        this.sentByName = sentByName;
        this.sentAt = LocalDateTime.now();
    }

    public void receive(UUID receivedBy, String receivedByName) {
        if (!status.canBeReceived()) {
            throw new IllegalStateException("Cannot receive in current status: " + status);
        }
        this.status = TransferStatus.RECEIVED;
        this.receivedById = receivedBy;
        this.receivedByName = receivedByName;
        this.receivedAt = LocalDateTime.now();
        this.actualArrivalDate = LocalDate.now();
    }
}
