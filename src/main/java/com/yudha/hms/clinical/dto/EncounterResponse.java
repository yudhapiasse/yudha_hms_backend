package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Encounter Response DTO.
 * Used for returning encounter data.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterResponse {

    private UUID id;

    private String encounterNumber;

    // Patient
    private UUID patientId;

    // Type and Class
    private EncounterType encounterType;
    private EncounterClass encounterClass;

    // Registration References
    private UUID outpatientRegistrationId;
    private UUID inpatientAdmissionId;
    private UUID emergencyRegistrationId;

    // Timing
    private LocalDateTime encounterStart;
    private LocalDateTime encounterEnd;
    private Long durationHours;
    private Integer lengthOfStayHours;
    private Integer lengthOfStayDays;

    // Status
    private EncounterStatus status;

    // Department/Location
    private UUID departmentId;
    private UUID locationId;
    private String currentDepartment;
    private String currentLocation;
    private String admittingDepartment;

    // Care Team
    private UUID practitionerId;
    private UUID referringPractitionerId;
    private UUID attendingDoctorId;
    private String attendingDoctorName;
    private UUID primaryNurseId;
    private String primaryNurseName;

    // Priority and Service
    private Priority priority;
    private String serviceType;

    // Visit Details
    private String reasonForVisit;
    private String chiefComplaint;

    // Discharge
    private String dischargeDisposition;
    private LocalDateTime dischargeDate;
    private UUID dischargeSummaryId;

    // Referral
    private String referredFrom;
    private String referredTo;
    private UUID referralId;

    // Insurance
    private InsuranceType insuranceType;
    private String insuranceNumber;
    private Boolean isBpjs;
    private String sepNumber;
    private LocalDate sepDate;
    private String insuranceProvider;

    // SATUSEHAT
    private String satusehatEncounterId;
    private Boolean satusehatSynced;
    private LocalDateTime satusehatSyncedAt;

    // Billing
    private String billingStatus;
    private BigDecimal totalCharges;

    // Notes
    private String encounterNotes;

    // Cancellation
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    private String cancellationReason;

    // Relationships
    private List<EncounterParticipantDto> participants;
    private List<EncounterDiagnosisDto> diagnoses;
    private List<EncounterStatusHistoryDto> statusHistory;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
