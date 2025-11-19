package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.EncounterClass;
import com.yudha.hms.clinical.entity.EncounterType;
import com.yudha.hms.clinical.entity.InsuranceType;
import com.yudha.hms.clinical.entity.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Encounter Request DTO.
 * Used for creating and updating encounters.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterRequest {

    @NotNull(message = "Patient ID wajib diisi")
    private UUID patientId;

    @NotNull(message = "Tipe encounter wajib diisi")
    private EncounterType encounterType;

    @NotNull(message = "Kelas encounter wajib diisi")
    private EncounterClass encounterClass;

    // Registration references (one should be populated based on type)
    private UUID outpatientRegistrationId;
    private UUID inpatientAdmissionId;
    private UUID emergencyRegistrationId;

    // Department/Location
    private UUID departmentId;
    private UUID locationId;
    private String currentDepartment;
    private String currentLocation;

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

    // Insurance
    private InsuranceType insuranceType;
    private String insuranceNumber;
    private String sepNumber; // BPJS SEP number
    private LocalDate sepDate;

    // Notes
    private String encounterNotes;

    // Timing
    private LocalDateTime encounterStart;

    // Participants and Diagnoses
    private List<EncounterParticipantDto> participants;
    private List<EncounterDiagnosisDto> diagnoses;
}
