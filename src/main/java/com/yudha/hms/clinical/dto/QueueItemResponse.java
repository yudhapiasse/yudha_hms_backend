package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Queue Item Response DTO.
 *
 * Individual patient in queue.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueItemResponse {

    private UUID registrationId;
    private UUID encounterId;
    private String queueCode;
    private Integer queueNumber;
    private String patientName;
    private String patientMrn;
    private String queueStatus;
    private String priority; // ROUTINE, URGENT, EMERGENCY
    private LocalDateTime registrationTime;
    private LocalDateTime queueCalledAt;
    private LocalDateTime servingStartedAt;
    private Integer waitingTimeMinutes;
    private String doctorName;
    private String consultationRoom;
}
