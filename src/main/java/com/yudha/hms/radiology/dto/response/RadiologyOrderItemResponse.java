package com.yudha.hms.radiology.dto.response;

import com.yudha.hms.radiology.constant.Laterality;
import com.yudha.hms.radiology.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Order Item Response DTO.
 *
 * Response for individual order item information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyOrderItemResponse {

    /**
     * Order item ID
     */
    private UUID id;

    /**
     * Order ID
     */
    private UUID orderId;

    /**
     * Order number
     */
    private String orderNumber;

    /**
     * Examination ID
     */
    private UUID examinationId;

    /**
     * Examination code
     */
    private String examCode;

    /**
     * Examination name
     */
    private String examName;

    /**
     * Modality code
     */
    private String modalityCode;

    /**
     * Modality name
     */
    private String modalityName;

    /**
     * Laterality (for applicable examinations)
     */
    private Laterality laterality;

    /**
     * Body part
     */
    private String bodyPart;

    /**
     * Quantity
     */
    private Integer quantity;

    /**
     * Unit price
     */
    private BigDecimal unitPrice;

    /**
     * Discount amount
     */
    private BigDecimal discountAmount;

    /**
     * Final price
     */
    private BigDecimal finalPrice;

    /**
     * Status
     */
    private OrderStatus status;

    /**
     * Result ID
     */
    private UUID resultId;

    /**
     * Has result
     */
    private Boolean hasResult;

    /**
     * Requires contrast
     */
    private Boolean requiresContrast;

    /**
     * Requires fasting
     */
    private Boolean requiresFasting;

    /**
     * Notes
     */
    private String notes;

    /**
     * Created at
     */
    private LocalDateTime createdAt;

    /**
     * Updated at
     */
    private LocalDateTime updatedAt;
}
