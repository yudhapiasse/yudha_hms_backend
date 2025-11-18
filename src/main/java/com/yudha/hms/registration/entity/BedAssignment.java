package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Bed assignment history for inpatient admissions.
 * Tracks bed assignments and transfers during a patient's stay.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "bed_assignment", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_bed_assignment_admission", columnList = "admission_id"),
        @Index(name = "idx_bed_assignment_patient", columnList = "patient_id"),
        @Index(name = "idx_bed_assignment_bed", columnList = "bed_id"),
        @Index(name = "idx_bed_assignment_room", columnList = "room_id"),
        @Index(name = "idx_bed_assignment_current", columnList = "is_current"),
        @Index(name = "idx_bed_assignment_dates", columnList = "assigned_at, released_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Bed assignment history and transfers")
public class BedAssignment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assignment_admission"))
    @NotNull(message = "Admission is required")
    private InpatientAdmission admission;

    @Column(name = "patient_id", nullable = false)
    @NotNull
    private UUID patientId;

    // Bed details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assignment_room"))
    @NotNull
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assignment_bed"))
    @NotNull
    private Bed bed;

    @Column(name = "room_number", nullable = false, length = 20)
    @NotBlank
    @Size(max = 20)
    private String roomNumber;

    @Column(name = "bed_number", nullable = false, length = 20)
    @NotBlank
    @Size(max = 20)
    private String bedNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_class", nullable = false, length = 20)
    @NotNull
    private RoomClass roomClass;

    // Assignment period
    @Column(name = "assigned_at", nullable = false)
    @NotNull
    @Builder.Default
    private LocalDateTime assignedAt = LocalDateTime.now();

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    // Assignment reason
    @Column(name = "assignment_type", nullable = false, length = 30)
    @NotBlank
    @Size(max = 30)
    private String assignmentType; // INITIAL, TRANSFER, UPGRADE, DOWNGRADE

    @Column(name = "transfer_reason", columnDefinition = "TEXT")
    private String transferReason;

    // Rate at time of assignment (historical)
    @Column(name = "room_rate_per_day", precision = 12, scale = 2)
    private BigDecimal roomRatePerDay;

    // Status
    @Column(name = "is_current")
    @Builder.Default
    private Boolean isCurrent = true;

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Assigned by
    @Column(name = "assigned_by", length = 100)
    @Size(max = 100)
    private String assignedBy;

    @Column(name = "released_by", length = 100)
    @Size(max = 100)
    private String releasedBy;

    /**
     * Calculate duration of this bed assignment in days.
     *
     * @return days between assignment and release (or current time if still current)
     */
    public long getDurationInDays() {
        LocalDateTime endDate = releasedAt != null ? releasedAt : LocalDateTime.now();
        return ChronoUnit.DAYS.between(assignedAt, endDate);
    }

    /**
     * Release this bed assignment.
     *
     * @param releasedBy user who released the assignment
     */
    public void release(String releasedBy) {
        this.releasedAt = LocalDateTime.now();
        this.isCurrent = false;
        this.releasedBy = releasedBy;
    }

    /**
     * Check if this is an initial assignment (not a transfer).
     *
     * @return true if initial assignment
     */
    public boolean isInitialAssignment() {
        return "INITIAL".equals(assignmentType);
    }

    /**
     * Check if this is a transfer to a different bed/room.
     *
     * @return true if transfer
     */
    public boolean isTransfer() {
        return "TRANSFER".equals(assignmentType) ||
               "UPGRADE".equals(assignmentType) ||
               "DOWNGRADE".equals(assignmentType);
    }
}
