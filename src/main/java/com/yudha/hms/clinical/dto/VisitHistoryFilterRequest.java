package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Visit History Filter Request DTO.
 *
 * Filtering options for patient visit history queries.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitHistoryFilterRequest {

    // Date Range
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Encounter Type Filter
    private String encounterType; // OUTPATIENT, INPATIENT, EMERGENCY

    // Department Filter
    private String department;

    // Doctor Filter
    private UUID doctorId;
    private String doctorName;

    // Diagnosis Category/Code Filter
    private String diagnosisCode; // ICD-10 code or prefix (e.g., "A00" for cholera infections)
    private String diagnosisCategory; // Major category (e.g., "A00-B99" for infectious diseases)

    // Status Filter
    private String status;

    // BPJS Filter
    private Boolean isBpjsOnly;

    // Readmission Filter
    private Boolean readmissionsOnly;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy; // encounterStart, department, status
    private String sortDirection; // ASC, DESC
}