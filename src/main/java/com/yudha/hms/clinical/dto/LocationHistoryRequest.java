package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.LocationEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for encounter location history.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationHistoryRequest {

    // Location details
    private UUID locationId;

    @NotBlank(message = "Location name is required")
    private String locationName;

    private String locationType;

    // Department
    private UUID departmentId;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    // Bed/Room
    private UUID roomId;

    private String roomNumber;

    private String roomType;

    private UUID bedId;

    private String bedNumber;

    // Timing
    private LocalDateTime startTime;

    // Event type
    @NotNull(message = "Location event type is required")
    private LocationEventType locationEventType;

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
    private Boolean isIcu;

    private Boolean isolationRequired;

    private String isolationType;
}
