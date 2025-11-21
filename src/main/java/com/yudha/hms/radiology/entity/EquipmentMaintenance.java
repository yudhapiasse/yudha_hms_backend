package com.yudha.hms.radiology.entity;

import com.yudha.hms.radiology.constant.MaintenanceType;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Equipment Maintenance Entity.
 *
 * Equipment maintenance log
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "equipment_maintenance", schema = "radiology_schema", indexes = {
        @Index(name = "idx_equipment_maintenance_room", columnList = "room_id"),
        @Index(name = "idx_equipment_maintenance_type", columnList = "maintenance_type"),
        @Index(name = "idx_equipment_maintenance_scheduled_date", columnList = "scheduled_date"),
        @Index(name = "idx_equipment_maintenance_performed_date", columnList = "performed_date"),
        @Index(name = "idx_equipment_maintenance_next_date", columnList = "next_maintenance_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EquipmentMaintenance extends BaseEntity {

    /**
     * Room reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RadiologyRoom room;

    /**
     * Maintenance type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 50)
    private MaintenanceType maintenanceType;

    /**
     * Scheduled date
     */
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    /**
     * Performed date
     */
    @Column(name = "performed_date")
    private LocalDate performedDate;

    /**
     * Performed by
     */
    @Column(name = "performed_by", length = 200)
    private String performedBy;

    /**
     * Vendor name
     */
    @Column(name = "vendor_name", length = 200)
    private String vendorName;

    /**
     * Findings
     */
    @Column(name = "findings", columnDefinition = "TEXT")
    private String findings;

    /**
     * Actions taken
     */
    @Column(name = "actions_taken", columnDefinition = "TEXT")
    private String actionsTaken;

    /**
     * Next maintenance date
     */
    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    /**
     * Cost
     */
    @Column(name = "cost", precision = 15, scale = 2)
    private BigDecimal cost;
}
