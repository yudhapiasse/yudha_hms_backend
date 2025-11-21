package com.yudha.hms.radiology.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reporting Template Response DTO.
 *
 * Response for reporting template information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportingTemplateResponse {

    /**
     * Template ID
     */
    private UUID id;

    /**
     * Examination ID
     */
    private UUID examinationId;

    /**
     * Examination code
     */
    private String examinationCode;

    /**
     * Examination name
     */
    private String examinationName;

    /**
     * Template name
     */
    private String templateName;

    /**
     * Template code
     */
    private String templateCode;

    /**
     * Template sections (JSON structure)
     */
    private String sections;

    /**
     * Is default template
     */
    private Boolean isDefault;

    /**
     * Usage count
     */
    private Long usageCount;

    /**
     * Active status
     */
    private Boolean isActive;

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
