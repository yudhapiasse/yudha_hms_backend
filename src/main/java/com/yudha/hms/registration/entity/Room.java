package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Hospital room entity for inpatient care.
 * Contains information about rooms, their class, location, facilities, and pricing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "room", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_room_number", columnList = "room_number"),
        @Index(name = "idx_room_class", columnList = "room_class"),
        @Index(name = "idx_room_available", columnList = "is_available"),
        @Index(name = "idx_room_floor", columnList = "floor")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Hospital rooms for inpatient care")
public class Room extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Room number is required")
    @Size(max = 20)
    private String roomNumber;

    @Column(name = "room_name", length = 100)
    @Size(max = 100)
    private String roomName;

    // Room classification
    @Enumerated(EnumType.STRING)
    @Column(name = "room_class", nullable = false, length = 20)
    @NotNull(message = "Room class is required")
    private RoomClass roomClass;

    @Column(name = "room_type", length = 50)
    @Size(max = 50)
    private String roomType; // STANDARD, ISOLATION, SUITE

    // Location
    @Column(name = "building", length = 50)
    @Size(max = 50)
    private String building;

    @Column(name = "floor", length = 20)
    @Size(max = 20)
    private String floor;

    @Column(name = "wing", length = 50)
    @Size(max = 50)
    private String wing; // North, South, East, West

    // Capacity
    @Column(name = "total_beds", nullable = false)
    @NotNull
    @Min(1)
    @Builder.Default
    private Integer totalBeds = 1;

    @Column(name = "available_beds", nullable = false)
    @NotNull
    @Min(0)
    @Builder.Default
    private Integer availableBeds = 1;

    // Pricing per day
    @Column(name = "base_room_rate", nullable = false, precision = 12, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Builder.Default
    private BigDecimal baseRoomRate = BigDecimal.ZERO;

    // Facilities
    @Column(name = "has_ac")
    @Builder.Default
    private Boolean hasAc = true;

    @Column(name = "has_tv")
    @Builder.Default
    private Boolean hasTv = false;

    @Column(name = "has_bathroom")
    @Builder.Default
    private Boolean hasBathroom = true;

    @Column(name = "has_wifi")
    @Builder.Default
    private Boolean hasWifi = false;

    @Column(name = "has_refrigerator")
    @Builder.Default
    private Boolean hasRefrigerator = false;

    @Column(name = "has_sofa_bed")
    @Builder.Default
    private Boolean hasSofaBed = false;

    // Gender restriction
    @Column(name = "gender_restriction", length = 10)
    @Size(max = 10)
    private String genderRestriction; // MALE, FEMALE, NULL (mixed)

    // Status
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    // Notes
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Relationships
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bed> beds = new ArrayList<>();

    /**
     * Check if room has available beds.
     *
     * @return true if room has available beds
     */
    public boolean hasAvailableBeds() {
        return availableBeds != null && availableBeds > 0 && Boolean.TRUE.equals(isAvailable);
    }

    /**
     * Occupy one bed in the room.
     * Decrements available beds count.
     */
    public void occupyBed() {
        if (availableBeds > 0) {
            availableBeds--;
            if (availableBeds == 0) {
                isAvailable = false;
            }
        }
    }

    /**
     * Release one bed in the room.
     * Increments available beds count.
     */
    public void releaseBed() {
        if (availableBeds < totalBeds) {
            availableBeds++;
            if (availableBeds > 0) {
                isAvailable = true;
            }
        }
    }

    /**
     * Get room full name (building, floor, room number).
     *
     * @return formatted room location
     */
    public String getFullRoomName() {
        StringBuilder name = new StringBuilder();
        if (building != null) {
            name.append(building).append(" - ");
        }
        if (floor != null) {
            name.append("Floor ").append(floor).append(" - ");
        }
        name.append(roomNumber);
        return name.toString();
    }
}
