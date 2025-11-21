package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Stock Transfer Item Entity.
 *
 * Individual items in a stock transfer.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "stock_transfer_item", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_transfer_item_transfer", columnList = "transfer_id"),
        @Index(name = "idx_transfer_item_drug", columnList = "drug_id"),
        @Index(name = "idx_transfer_item_batch", columnList = "batch_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockTransferItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = false)
    private StockTransfer stockTransfer;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @Column(name = "quantity_requested", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantityRequested;

    @Column(name = "quantity_sent", precision = 10, scale = 2)
    private BigDecimal quantitySent;

    @Column(name = "quantity_received", precision = 10, scale = 2)
    private BigDecimal quantityReceived;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public boolean isFullySent() {
        return quantitySent != null && quantityRequested != null &&
               quantitySent.compareTo(quantityRequested) >= 0;
    }

    public boolean isFullyReceived() {
        return quantityReceived != null && quantitySent != null &&
               quantityReceived.compareTo(quantitySent) >= 0;
    }
}
