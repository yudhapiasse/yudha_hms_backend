package com.yudha.hms.pharmacy.dto;

import com.yudha.hms.pharmacy.constant.InteractionSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Drug Interaction Response DTO.
 *
 * Response object for drug interaction information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugInteractionResponse {

    /**
     * Interaction ID
     */
    private UUID id;

    /**
     * First drug ID
     */
    private UUID drug1Id;

    /**
     * First drug name
     */
    private String drug1Name;

    /**
     * Second drug ID
     */
    private UUID drug2Id;

    /**
     * Second drug name
     */
    private String drug2Name;

    /**
     * Interaction severity
     */
    private InteractionSeverity severity;

    /**
     * Interaction description
     */
    private String description;

    /**
     * Clinical effects
     */
    private String clinicalEffects;

    /**
     * Management recommendations
     */
    private String management;

    /**
     * Evidence level
     */
    private String evidenceLevel;

    /**
     * References
     */
    private String references;

    /**
     * Active status
     */
    private Boolean active;

    /**
     * Created timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
