package com.yudha.hms.laboratory.dto.response;

import com.yudha.hms.laboratory.constant.SampleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Panel Test Item Response DTO.
 *
 * Response for test items within a panel.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanelTestItemResponse {

    /**
     * Item ID
     */
    private UUID id;

    /**
     * Test ID
     */
    private UUID testId;

    /**
     * Test code
     */
    private String testCode;

    /**
     * Test name
     */
    private String testName;

    /**
     * Short name
     */
    private String shortName;

    /**
     * Sample type
     */
    private SampleType sampleType;

    /**
     * Base cost
     */
    private BigDecimal baseCost;

    /**
     * Is mandatory test
     */
    private Boolean isMandatory;

    /**
     * Display order
     */
    private Integer displayOrder;

    /**
     * Notes
     */
    private String notes;
}
