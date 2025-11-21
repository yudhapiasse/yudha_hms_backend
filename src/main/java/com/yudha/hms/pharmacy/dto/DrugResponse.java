package com.yudha.hms.pharmacy.dto;

import com.yudha.hms.pharmacy.constant.DrugUnit;
import com.yudha.hms.pharmacy.constant.FormulariumStatus;
import com.yudha.hms.pharmacy.constant.StorageCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Drug Response DTO.
 *
 * Response object for complete drug information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugResponse {

    /**
     * Drug ID
     */
    private UUID id;

    /**
     * Drug code
     */
    private String drugCode;

    /**
     * Generic name
     */
    private String genericName;

    /**
     * Brand name
     */
    private String brandName;

    /**
     * Full drug name (computed)
     */
    private String fullName;

    /**
     * Drug strength
     */
    private String strength;

    /**
     * Dosage form
     */
    private String dosageForm;

    /**
     * Route of administration
     */
    private String routeOfAdministration;

    /**
     * Drug category
     */
    private DrugCategoryResponse category;

    /**
     * Drug unit
     */
    private DrugUnit unit;

    /**
     * Unit description
     */
    private String unitDescription;

    /**
     * Storage condition
     */
    private StorageCondition storageCondition;

    /**
     * Storage temperature range
     */
    private String storageTemperature;

    /**
     * Storage instructions
     */
    private String storageInstructions;

    /**
     * Requires cold chain
     */
    private Boolean requiresColdChain;

    /**
     * Formularium status
     */
    private FormulariumStatus formulariumStatus;

    /**
     * BPJS drug code
     */
    private String bpjsDrugCode;

    /**
     * Is BPJS covered
     */
    private Boolean isBpjsCovered;

    /**
     * Registration number
     */
    private String registrationNumber;

    /**
     * Registration expiry date
     */
    private LocalDate registrationExpiryDate;

    /**
     * Is registration expired
     */
    private Boolean isRegistrationExpired;

    /**
     * Is narcotic
     */
    private Boolean isNarcotic;

    /**
     * Is psychotropic
     */
    private Boolean isPsychotropic;

    /**
     * Is high alert medication
     */
    private Boolean isHighAlert;

    /**
     * Is controlled substance
     */
    private Boolean isControlledSubstance;

    /**
     * Requires prescription
     */
    private Boolean requiresPrescription;

    /**
     * Minimum stock level
     */
    private BigDecimal minimumStockLevel;

    /**
     * Maximum stock level
     */
    private BigDecimal maximumStockLevel;

    /**
     * Reorder quantity
     */
    private BigDecimal reorderQuantity;

    /**
     * Current stock
     */
    private BigDecimal currentStock;

    /**
     * Is low stock
     */
    private Boolean isLowStock;

    /**
     * Unit price
     */
    private BigDecimal unitPrice;

    /**
     * BPJS unit price
     */
    private BigDecimal bpjsUnitPrice;

    /**
     * Primary supplier ID
     */
    private UUID primarySupplierId;

    /**
     * Primary supplier name
     */
    private String primarySupplierName;

    /**
     * Barcode
     */
    private String barcode;

    /**
     * National drug code
     */
    private String nationalDrugCode;

    /**
     * ATC code (Anatomical Therapeutic Chemical)
     */
    private String atcCode;

    /**
     * Manufacturer name
     */
    private String manufacturerName;

    /**
     * Indications
     */
    private String indications;

    /**
     * Contraindications
     */
    private String contraindications;

    /**
     * Side effects
     */
    private String sideEffects;

    /**
     * Dosage instructions
     */
    private String dosageInstructions;

    /**
     * Warnings
     */
    private String warnings;

    /**
     * Pregnancy category
     */
    private String pregnancyCategory;

    /**
     * Lactation safety
     */
    private String lactationSafety;

    /**
     * Is discontinued
     */
    private Boolean isDiscontinued;

    /**
     * Discontinuation date
     */
    private LocalDate discontinuationDate;

    /**
     * Discontinuation reason
     */
    private String discontinuationReason;

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
