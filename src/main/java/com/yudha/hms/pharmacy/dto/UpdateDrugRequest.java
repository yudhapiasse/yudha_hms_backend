package com.yudha.hms.pharmacy.dto;

import com.yudha.hms.pharmacy.constant.DrugUnit;
import com.yudha.hms.pharmacy.constant.FormulariumStatus;
import com.yudha.hms.pharmacy.constant.StorageCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Update Drug Request DTO.
 *
 * Request object for updating existing drug.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDrugRequest {

    /**
     * Brand name
     */
    @Size(max = 200, message = "Brand name must not exceed 200 characters")
    private String brandName;

    /**
     * Drug strength
     */
    @Size(max = 100, message = "Strength must not exceed 100 characters")
    private String strength;

    /**
     * Dosage form
     */
    @Size(max = 100, message = "Dosage form must not exceed 100 characters")
    private String dosageForm;

    /**
     * Route of administration
     */
    @Size(max = 100, message = "Route of administration must not exceed 100 characters")
    private String routeOfAdministration;

    /**
     * Drug category ID
     */
    private UUID categoryId;

    /**
     * Drug unit
     */
    private DrugUnit unit;

    /**
     * Unit description
     */
    @Size(max = 100, message = "Unit description must not exceed 100 characters")
    private String unitDescription;

    /**
     * Storage condition
     */
    private StorageCondition storageCondition;

    /**
     * Storage temperature range
     */
    @Size(max = 50, message = "Storage temperature must not exceed 50 characters")
    private String storageTemperature;

    /**
     * Storage instructions
     */
    @Size(max = 500, message = "Storage instructions must not exceed 500 characters")
    private String storageInstructions;

    /**
     * Formularium status
     */
    private FormulariumStatus formulariumStatus;

    /**
     * BPJS drug code
     */
    @Size(max = 50, message = "BPJS drug code must not exceed 50 characters")
    private String bpjsDrugCode;

    /**
     * Registration number
     */
    @Size(max = 100, message = "Registration number must not exceed 100 characters")
    private String registrationNumber;

    /**
     * Registration expiry date
     */
    private LocalDate registrationExpiryDate;

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
     * Requires prescription
     */
    private Boolean requiresPrescription;

    /**
     * Minimum stock level
     */
    @DecimalMin(value = "0.0", message = "Minimum stock level cannot be negative")
    private BigDecimal minimumStockLevel;

    /**
     * Maximum stock level
     */
    @DecimalMin(value = "0.0", message = "Maximum stock level cannot be negative")
    private BigDecimal maximumStockLevel;

    /**
     * Reorder quantity
     */
    @DecimalMin(value = "0.0", message = "Reorder quantity cannot be negative")
    private BigDecimal reorderQuantity;

    /**
     * Current stock
     */
    @DecimalMin(value = "0.0", message = "Current stock cannot be negative")
    private BigDecimal currentStock;

    /**
     * Unit price
     */
    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    private BigDecimal unitPrice;

    /**
     * BPJS unit price
     */
    @DecimalMin(value = "0.0", message = "BPJS unit price cannot be negative")
    private BigDecimal bpjsUnitPrice;

    /**
     * Primary supplier ID
     */
    private UUID primarySupplierId;

    /**
     * Barcode
     */
    @Size(max = 100, message = "Barcode must not exceed 100 characters")
    private String barcode;

    /**
     * National drug code
     */
    @Size(max = 50, message = "National drug code must not exceed 50 characters")
    private String nationalDrugCode;

    /**
     * ATC code
     */
    @Size(max = 20, message = "ATC code must not exceed 20 characters")
    private String atcCode;

    /**
     * Manufacturer name
     */
    @Size(max = 200, message = "Manufacturer name must not exceed 200 characters")
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
    @Size(max = 10, message = "Pregnancy category must not exceed 10 characters")
    private String pregnancyCategory;

    /**
     * Lactation safety
     */
    @Size(max = 100, message = "Lactation safety must not exceed 100 characters")
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
    @Size(max = 500, message = "Discontinuation reason must not exceed 500 characters")
    private String discontinuationReason;

    /**
     * Active status
     */
    private Boolean active;
}
