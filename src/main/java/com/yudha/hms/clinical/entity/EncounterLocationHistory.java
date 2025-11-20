package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Location History Entity.
 *
 * Tracks all location and bed changes during an inpatient encounter.
 * Separate from DepartmentTransfer which handles inter-department transfers.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "encounter_location_history", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_location_history_encounter", columnList = "encounter_id"),
        @Index(name = "idx_location_history_patient", columnList = "patient_id"),
        @Index(name = "idx_location_history_start", columnList = "start_time"),
        @Index(name = "idx_location_history_current", columnList = "is_current"),
        @Index(name = "idx_location_history_bed", columnList = "bed_id"),
        @Index(name = "idx_location_history_type", columnList = "location_event_type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Complete location and bed change history for encounters")
public class EncounterLocationHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Location Details ==========
    @Column(name = "location_id")
    private UUID locationId;

    @Column(name = "location_name", nullable = false, length = 200)
    @NotBlank(message = "Location name is required")
    private String locationName;

    @Column(name = "location_type", length = 50)
    private String locationType; // WARD, ICU, CCU, EMERGENCY, OPERATING_ROOM, RECOVERY

    // ========== Department ==========
    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "department_name", nullable = false, length = 100)
    @NotBlank(message = "Department name is required")
    private String departmentName;

    // ========== Bed/Room Details ==========
    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "room_number", length = 50)
    private String roomNumber;

    @Column(name = "room_type", length = 50)
    private String roomType; // PRIVATE, SEMI_PRIVATE, WARD, ICU_ROOM

    @Column(name = "bed_id")
    private UUID bedId;

    @Column(name = "bed_number", length = 20)
    private String bedNumber;

    // ========== Timing ==========
    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start time is required")
    @Builder.Default
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_hours")
    private Integer durationHours;

    @Column(name = "duration_days")
    private Integer durationDays;

    // ========== Event Type ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "location_event_type", nullable = false, length = 50)
    @NotNull(message = "Location event type is required")
    private LocationEventType locationEventType;

    // ========== Transfer/Change Reason ==========
    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "change_notes", columnDefinition = "TEXT")
    private String changeNotes;

    // ========== Responsible Staff ==========
    @Column(name = "changed_by_id")
    private UUID changedById;

    @Column(name = "changed_by_name", length = 200)
    private String changedByName;

    @Column(name = "authorized_by_id")
    private UUID authorizedById;

    @Column(name = "authorized_by_name", length = 200)
    private String authorizedByName;

    // ========== Bed Assignment Reference ==========
    @Column(name = "bed_assignment_id")
    private UUID bedAssignmentId;

    // ========== Flags ==========
    @Column(name = "is_current")
    @Builder.Default
    private Boolean isCurrent = true;

    @Column(name = "is_icu")
    @Builder.Default
    private Boolean isIcu = false;

    @Column(name = "isolation_required")
    @Builder.Default
    private Boolean isolationRequired = false;

    @Column(name = "isolation_type", length = 50)
    private String isolationType; // CONTACT, DROPLET, AIRBORNE

    // ========== Business Methods ==========

    /**
     * End the location stay.
     */
    public void endStay() {
        this.endTime = LocalDateTime.now();
        this.isCurrent = false;
        calculateDuration();
    }

    /**
     * Calculate duration of stay at this location.
     */
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            this.durationHours = (int) duration.toHours();
            this.durationDays = (int) duration.toDays();
        }
    }

    /**
     * Check if currently in ICU.
     */
    public boolean isInIcu() {
        return Boolean.TRUE.equals(isIcu) && Boolean.TRUE.equals(isCurrent);
    }

    /**
     * Check if isolation is active.
     */
    public boolean isInIsolation() {
        return Boolean.TRUE.equals(isolationRequired) && Boolean.TRUE.equals(isCurrent);
    }

    /**
     * Get full location description.
     */
    public String getFullLocationDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(departmentName);

        if (roomNumber != null) {
            sb.append(" - Room ").append(roomNumber);
        }

        if (bedNumber != null) {
            sb.append(" - Bed ").append(bedNumber);
        }

        return sb.toString();
    }

    /**
     * Check if this is an admission event.
     */
    public boolean isAdmission() {
        return locationEventType == LocationEventType.ADMISSION;
    }

    /**
     * Check if this is a discharge event.
     */
    public boolean isDischarge() {
        return locationEventType == LocationEventType.DISCHARGE;
    }

    /**
     * Check if this is an ICU event.
     */
    public boolean isIcuEvent() {
        return locationEventType == LocationEventType.ICU_ADMISSION ||
               locationEventType == LocationEventType.ICU_DISCHARGE;
    }

    /**
     * Set as current location.
     */
    public void setAsCurrent() {
        this.isCurrent = true;
        this.endTime = null;
        this.durationHours = null;
        this.durationDays = null;
    }

    /**
     * Set as previous location.
     */
    public void setAsPrevious() {
        if (this.endTime == null) {
            endStay();
        }
        this.isCurrent = false;
    }
}
