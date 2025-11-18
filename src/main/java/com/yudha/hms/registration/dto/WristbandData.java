package com.yudha.hms.registration.dto;

import com.yudha.hms.registration.entity.RoomClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for patient wristband data.
 * Contains all information needed to print a patient identification wristband.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WristbandData {

    // Patient identification
    private String patientName;
    private String mrn; // Medical Record Number
    private LocalDate birthDate;
    private Integer age;
    private String gender;

    // Admission information
    private String admissionNumber;
    private LocalDateTime admissionDate;
    private RoomClass roomClass;

    // Location
    private String roomNumber;
    private String bedNumber;
    private String building;
    private String floor;

    // Medical team
    private String attendingDoctorName;

    // Alerts
    private Boolean hasAllergies;
    private String allergyAlert; // Brief allergy summary
    private Boolean requiresIsolation;
    private String isolationType;
    private Boolean hasFallRisk;
    private Boolean hasDnr; // Do Not Resuscitate

    // Barcodes/QR codes (Base64 encoded PNG images)
    private String mrnBarcode;
    private String mrnQrCode;
    private String admissionBarcode;
    private String admissionQrCode;

    // Wristband color code (based on alerts/risk)
    private String wristbandColor; // RED (allergy), YELLOW (fall risk), PURPLE (DNR), WHITE (standard)

    // Generated timestamp
    private LocalDateTime generatedAt;

    /**
     * Get full room location string.
     *
     * @return formatted location
     */
    public String getFullLocation() {
        StringBuilder location = new StringBuilder();
        if (building != null) {
            location.append(building);
        }
        if (floor != null) {
            if (location.length() > 0) location.append(" - ");
            location.append("Lt. ").append(floor);
        }
        if (roomNumber != null) {
            if (location.length() > 0) location.append(" - ");
            location.append(roomNumber);
        }
        if (bedNumber != null) {
            location.append(" / ").append(bedNumber);
        }
        return location.toString();
    }

    /**
     * Determine wristband color based on alerts.
     *
     * @return wristband color code
     */
    public String determineWristbandColor() {
        if (Boolean.TRUE.equals(hasDnr)) {
            return "PURPLE";
        }
        if (Boolean.TRUE.equals(hasAllergies)) {
            return "RED";
        }
        if (Boolean.TRUE.equals(hasFallRisk)) {
            return "YELLOW";
        }
        if (Boolean.TRUE.equals(requiresIsolation)) {
            return "ORANGE";
        }
        return "WHITE";
    }

    /**
     * Get age display string.
     *
     * @return formatted age
     */
    public String getAgeDisplay() {
        if (age != null) {
            return age + " tahun";
        }
        return "N/A";
    }

    /**
     * Get gender display (Indonesian).
     *
     * @return gender in Indonesian
     */
    public String getGenderDisplay() {
        if ("MALE".equals(gender)) {
            return "Laki-laki";
        } else if ("FEMALE".equals(gender)) {
            return "Perempuan";
        }
        return gender != null ? gender : "N/A";
    }
}
