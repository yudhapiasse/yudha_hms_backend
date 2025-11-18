package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Hospital bed entity within a room.
 * Tracks individual bed availability and assignments.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "bed", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_bed_room", columnList = "room_id"),
        @Index(name = "idx_bed_occupied", columnList = "is_occupied"),
        @Index(name = "idx_bed_patient", columnList = "current_patient_id"),
        @Index(name = "idx_bed_admission", columnList = "current_admission_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_bed_number_room", columnNames = {"room_id", "bed_number"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Hospital beds within rooms")
public class Bed extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bed_number", nullable = false, length = 20)
    @NotBlank(message = "Bed number is required")
    @Size(max = 20)
    private String bedNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bed_room"))
    @NotNull(message = "Room is required")
    private Room room;

    // Bed details
    @Column(name = "bed_type", length = 50)
    @Size(max = 50)
    private String bedType; // STANDARD, ELECTRIC, ICU, PEDIATRIC

    @Column(name = "bed_position", length = 20)
    @Size(max = 20)
    private String bedPosition; // WINDOW, DOOR, CENTER, CORNER

    // Equipment
    @Column(name = "has_monitor")
    @Builder.Default
    private Boolean hasMonitor = false;

    @Column(name = "has_ventilator")
    @Builder.Default
    private Boolean hasVentilator = false;

    @Column(name = "has_oxygen")
    @Builder.Default
    private Boolean hasOxygen = true;

    @Column(name = "has_suction")
    @Builder.Default
    private Boolean hasSuction = false;

    // Availability
    @Column(name = "is_occupied")
    @Builder.Default
    private Boolean isOccupied = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_maintenance")
    @Builder.Default
    private Boolean isMaintenance = false;

    // Current patient (denormalized for quick access)
    @Column(name = "current_patient_id")
    private UUID currentPatientId;

    @Column(name = "current_admission_id")
    private UUID currentAdmissionId;

    @Column(name = "occupied_since")
    private LocalDateTime occupiedSince;

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;

    /**
     * Check if bed is available for assignment.
     *
     * @return true if bed is available
     */
    public boolean isAvailable() {
        return Boolean.TRUE.equals(isActive)
            && Boolean.FALSE.equals(isOccupied)
            && Boolean.FALSE.equals(isMaintenance);
    }

    /**
     * Occupy the bed with a patient.
     *
     * @param patientId patient UUID
     * @param admissionId admission UUID
     */
    public void occupy(UUID patientId, UUID admissionId) {
        this.isOccupied = true;
        this.currentPatientId = patientId;
        this.currentAdmissionId = admissionId;
        this.occupiedSince = LocalDateTime.now();
    }

    /**
     * Release the bed (patient discharged/transferred).
     */
    public void release() {
        this.isOccupied = false;
        this.currentPatientId = null;
        this.currentAdmissionId = null;
        this.occupiedSince = null;
    }

    /**
     * Put bed under maintenance.
     *
     * @param reason maintenance reason
     */
    public void setMaintenance(String reason) {
        this.isMaintenance = true;
        this.maintenanceNotes = reason;
        this.isActive = false;
    }

    /**
     * Complete bed maintenance.
     */
    public void completeMaintenance() {
        this.isMaintenance = false;
        this.isActive = true;
    }

    /**
     * Get bed full name (room number + bed number).
     *
     * @return formatted bed identifier
     */
    public String getFullBedName() {
        if (room != null) {
            return room.getRoomNumber() + " - " + bedNumber;
        }
        return bedNumber;
    }
}
