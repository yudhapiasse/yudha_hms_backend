package com.yudha.hms.radiology.dto.response;

import com.yudha.hms.radiology.constant.OrderPriority;
import com.yudha.hms.radiology.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Radiology Order Response DTO.
 *
 * Response for radiology order information with full details.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyOrderResponse {

    /**
     * Order ID
     */
    private UUID id;

    /**
     * Order number
     */
    private String orderNumber;

    // ========== Patient and Encounter Information ==========

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Patient name
     */
    private String patientName;

    /**
     * Patient medical record number
     */
    private String patientMrn;

    /**
     * Patient age
     */
    private Integer patientAge;

    /**
     * Patient gender
     */
    private String patientGender;

    /**
     * Encounter ID
     */
    private UUID encounterId;

    /**
     * Encounter type
     */
    private String encounterType;

    // ========== Ordering Information ==========

    /**
     * Ordering doctor ID
     */
    private UUID orderingDoctorId;

    /**
     * Ordering doctor name
     */
    private String orderingDoctorName;

    /**
     * Ordering department
     */
    private String orderingDepartment;

    /**
     * Order date
     */
    private LocalDateTime orderDate;

    // ========== Priority ==========

    /**
     * Priority level
     */
    private OrderPriority priority;

    // ========== Status ==========

    /**
     * Order status
     */
    private OrderStatus status;

    // ========== Clinical Information ==========

    /**
     * Clinical indication
     */
    private String clinicalIndication;

    /**
     * Diagnosis text
     */
    private String diagnosisText;

    // ========== Scheduling ==========

    /**
     * Scheduled date
     */
    private LocalDate scheduledDate;

    /**
     * Scheduled time
     */
    private LocalTime scheduledTime;

    /**
     * Room ID
     */
    private UUID roomId;

    /**
     * Room code
     */
    private String roomCode;

    /**
     * Room name
     */
    private String roomName;

    /**
     * Technician ID
     */
    private UUID technicianId;

    /**
     * Technician name
     */
    private String technicianName;

    // ========== Order Items ==========

    /**
     * Order items (examinations)
     */
    private List<RadiologyOrderItemResponse> items;

    /**
     * Total items count
     */
    private Integer totalItems;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    private String notes;

    /**
     * Special instructions
     */
    private String specialInstructions;

    // ========== Audit Fields ==========

    /**
     * Created at
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
