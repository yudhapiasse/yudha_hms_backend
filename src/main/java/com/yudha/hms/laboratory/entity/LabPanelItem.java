package com.yudha.hms.laboratory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Panel Item Entity.
 *
 * Tests included in a panel.
 * Links panels to individual tests with display configuration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_panel_item", schema = "laboratory_schema",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_lab_panel_item_panel_test", columnNames = {"panel_id", "test_id"})
        },
        indexes = {
                @Index(name = "idx_lab_panel_item_panel", columnList = "panel_id"),
                @Index(name = "idx_lab_panel_item_test", columnList = "test_id")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabPanelItem {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Panel reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_id", nullable = false)
    private LabPanel panel;

    /**
     * Test reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private LabTest test;

    /**
     * Display order in panel
     */
    @Column(name = "display_order")
    private Integer displayOrder;

    /**
     * Is this test mandatory in the panel (or optional)
     */
    @Column(name = "is_mandatory")
    @Builder.Default
    private Boolean isMandatory = true;

    // ========== Audit Fields ==========

    /**
     * Created timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Created by user
     */
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    /**
     * Version for optimistic locking
     */
    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
