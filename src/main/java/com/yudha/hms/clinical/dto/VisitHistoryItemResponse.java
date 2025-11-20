package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Visit History Item Response DTO.
 *
 * Represents a single encounter in the patient's visit history.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitHistoryItemResponse {

    private UUID id;
    private String encounterNumber;

    // Encounter Type and Class
    private String encounterType;
    private String encounterTypeDisplay;
    private String encounterClass;
    private String encounterClassDisplay;

    // Timing
    private LocalDateTime encounterStart;
    private LocalDateTime encounterEnd;
    private Integer lengthOfStayDays;
    private Integer lengthOfStayHours;

    // Status
    private String status;
    private String statusDisplay;

    // Department and Location
    private String currentDepartment;
    private String currentLocation;
    private String admittingDepartment;

    // Care Team
    private UUID attendingDoctorId;
    private String attendingDoctorName;
    private String primaryNurseName;

    // Clinical Information
    private String chiefComplaint;
    private String reasonForVisit;

    // Primary Diagnosis
    private String primaryDiagnosisCode;
    private String primaryDiagnosisText;

    // Additional Diagnoses Count
    private Integer additionalDiagnosesCount;

    // Discharge Information
    private String dischargeDisposition;
    private LocalDateTime dischargeDate;

    // Priority
    private String priority;
    private String priorityDisplay;

    // Insurance
    private String insuranceType;
    private Boolean isBpjs;

    // Outcome/Status Flags
    private Boolean isReadmission;
    private Integer daysSincePreviousDischarge;
    private Boolean isActive;
    private Boolean isCompleted;

    // Audit
    private LocalDateTime createdAt;
}