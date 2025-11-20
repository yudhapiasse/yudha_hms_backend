package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.LocationEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for encounter location history.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationHistoryResponse {

    private UUID id;

    private UUID encounterId;

    private UUID patientId;

    // Location details
    private UUID locationId;

    private String locationName;

    private String locationType;

    // Department
    private UUID departmentId;

    private String departmentName;

    // Bed/Room
    private UUID roomId;

    private String roomNumber;

    private String roomType;

    private UUID bedId;

    private String bedNumber;

    // Timing
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer durationHours;

    private Integer durationDays;

    // Event type
    private LocationEventType locationEventType;

    private String locationEventTypeDisplay;

    // Reason
    private String changeReason;

    private String changeNotes;

    // Staff
    private UUID changedById;

    private String changedByName;

    private UUID authorizedById;

    private String authorizedByName;

    // Bed assignment
    private UUID bedAssignmentId;

    // Flags
    private Boolean isCurrent;

    private Boolean isIcu;

    private Boolean isolationRequired;

    private String isolationType;

    // Audit
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Computed fields
    private String fullLocationDescription;

    private Boolean isAdmission;

    private Boolean isDischarge;

    private Boolean isIcuEvent;

    private Boolean isInIcu;

    private Boolean isInIsolation;
}
