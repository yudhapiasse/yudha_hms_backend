package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Polyclinic (Poliklinik) Entity.
 *
 * Represents outpatient polyclinics with operating hours, capacity, and fees.
 * Supports various specializations (Poli Umum, Poli Anak, Poli Kandungan, etc.)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "polyclinic", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_polyclinic_code", columnList = "code"),
        @Index(name = "idx_polyclinic_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Polyclinic/Poli master data")
public class Polyclinic extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Basic Information ==========
    @Column(name = "code", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Polyclinic code is required")
    private String code; // POLI-UMUM, POLI-ANAK, etc.

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Polyclinic name is required")
    private String name; // Poli Umum, Poli Anak, etc.

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ========== Location ==========
    @Column(name = "floor_location", length = 50)
    private String floorLocation; // Lantai 1, Lantai 2, etc.

    @Column(name = "building", length = 50)
    private String building; // Gedung A, Gedung B, etc.

    // ========== Contact ==========
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "extension", length = 10)
    private String extension;

    // ========== Operating Hours ==========
    @Column(name = "operating_days", length = 100)
    private String operatingDays; // JSON array: ["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]

    @Column(name = "opening_time")
    private LocalTime openingTime; // 08:00:00

    @Column(name = "closing_time")
    private LocalTime closingTime; // 16:00:00

    // ========== Capacity and Configuration ==========
    @Column(name = "max_patients_per_day")
    @Builder.Default
    private Integer maxPatientsPerDay = 50;

    @Column(name = "appointment_duration_minutes")
    @Builder.Default
    private Integer appointmentDurationMinutes = 15;

    @Column(name = "allow_walk_in")
    @Builder.Default
    private Boolean allowWalkIn = true;

    @Column(name = "allow_appointments")
    @Builder.Default
    private Boolean allowAppointments = true;

    // ========== Status ==========
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_emergency")
    @Builder.Default
    private Boolean isEmergency = false;

    // ========== Registration Fee ==========
    @Column(name = "base_registration_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal baseRegistrationFee = BigDecimal.ZERO;

    // ========== Business Methods ==========

    /**
     * Check if polyclinic is operating on a specific day of week.
     */
    public boolean isOperatingOn(DayOfWeek dayOfWeek) {
        if (operatingDays == null || operatingDays.isEmpty()) {
            return false;
        }
        return operatingDays.contains(dayOfWeek.name());
    }

    /**
     * Check if polyclinic is currently open.
     */
    public boolean isCurrentlyOpen() {
        if (!Boolean.TRUE.equals(isActive)) {
            return false;
        }

        LocalTime now = LocalTime.now();
        DayOfWeek today = DayOfWeek.from(java.time.LocalDate.now());

        return isOperatingOn(today) &&
               openingTime != null &&
               closingTime != null &&
               !now.isBefore(openingTime) &&
               !now.isAfter(closingTime);
    }

    /**
     * Check if polyclinic is operating on a specific day.
     */
    public boolean isOperatingOnDate(java.time.LocalDate date) {
        if (!Boolean.TRUE.equals(isActive)) {
            return false;
        }
        return isOperatingOn(DayOfWeek.from(date));
    }

    /**
     * Get operating days as list.
     */
    public List<String> getOperatingDaysList() {
        if (operatingDays == null || operatingDays.isEmpty()) {
            return new ArrayList<>();
        }
        // Parse JSON array format: ["MONDAY","TUESDAY","WEDNESDAY"]
        String cleanedDays = operatingDays.replace("[", "").replace("]", "").replace("\"", "");
        return List.of(cleanedDays.split(","));
    }

    /**
     * Get location display string.
     */
    public String getLocationDisplay() {
        StringBuilder location = new StringBuilder();
        if (building != null) {
            location.append(building);
        }
        if (floorLocation != null) {
            if (location.length() > 0) {
                location.append(" - ");
            }
            location.append(floorLocation);
        }
        return location.length() > 0 ? location.toString() : "Location not set";
    }

    /**
     * Check if walk-in is allowed.
     */
    public boolean isWalkInAllowed() {
        return Boolean.TRUE.equals(allowWalkIn) && Boolean.TRUE.equals(isActive);
    }

    /**
     * Check if appointments are allowed.
     */
    public boolean areAppointmentsAllowed() {
        return Boolean.TRUE.equals(allowAppointments) && Boolean.TRUE.equals(isActive);
    }
}