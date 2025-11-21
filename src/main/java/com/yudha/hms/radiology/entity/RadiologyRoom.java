package com.yudha.hms.radiology.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Radiology Room Entity.
 *
 * Imaging rooms/equipment locations
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "radiology_room", schema = "radiology_schema", indexes = {
        @Index(name = "idx_radiology_room_code", columnList = "room_code", unique = true),
        @Index(name = "idx_radiology_room_name", columnList = "room_name"),
        @Index(name = "idx_radiology_room_modality", columnList = "modality_id"),
        @Index(name = "idx_radiology_room_operational", columnList = "is_operational"),
        @Index(name = "idx_radiology_room_available", columnList = "is_available")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RadiologyRoom extends SoftDeletableEntity {

    /**
     * Room code
     */
    @Column(name = "room_code", nullable = false, unique = true, length = 50)
    private String roomCode;

    /**
     * Room name
     */
    @Column(name = "room_name", nullable = false, length = 200)
    private String roomName;

    /**
     * Location
     */
    @Column(name = "location", length = 200)
    private String location;

    /**
     * Floor
     */
    @Column(name = "floor", length = 50)
    private String floor;

    /**
     * Modality reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modality_id", nullable = false)
    private RadiologyModality modality;

    /**
     * Equipment name
     */
    @Column(name = "equipment_name", length = 200)
    private String equipmentName;

    /**
     * Equipment model
     */
    @Column(name = "equipment_model", length = 200)
    private String equipmentModel;

    /**
     * Manufacturer
     */
    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    /**
     * Installation date
     */
    @Column(name = "installation_date")
    private LocalDate installationDate;

    /**
     * Last calibration date
     */
    @Column(name = "last_calibration_date")
    private LocalDate lastCalibrationDate;

    /**
     * Next calibration date
     */
    @Column(name = "next_calibration_date")
    private LocalDate nextCalibrationDate;

    /**
     * Whether equipment is operational
     */
    @Column(name = "is_operational")
    @Builder.Default
    private Boolean isOperational = true;

    /**
     * Whether equipment is available for booking
     */
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    /**
     * Maximum bookings per day
     */
    @Column(name = "max_bookings_per_day")
    private Integer maxBookingsPerDay;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
