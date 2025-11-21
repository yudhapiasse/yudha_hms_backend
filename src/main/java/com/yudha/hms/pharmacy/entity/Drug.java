package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.DrugUnit;
import com.yudha.hms.pharmacy.constant.FormulariumStatus;
import com.yudha.hms.pharmacy.constant.StorageCondition;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Drug Entity.
 *
 * Complete drug master data including:
 * - Generic and brand names
 * - Drug categories and classifications
 * - Unit configurations
 * - Storage requirements
 * - Formularium status (BPJS approval)
 * - Stock level management
 * - Supplier information
 * - Barcode support
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "drug", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_drug_code", columnList = "drug_code", unique = true),
        @Index(name = "idx_drug_generic_name", columnList = "generic_name"),
        @Index(name = "idx_drug_brand_name", columnList = "brand_name"),
        @Index(name = "idx_drug_category", columnList = "category_id"),
        @Index(name = "idx_drug_barcode", columnList = "barcode"),
        @Index(name = "idx_drug_formularium", columnList = "formularium_status"),
        @Index(name = "idx_drug_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Drug extends SoftDeletableEntity {

    /**
     * Drug code (internal hospital code)
     */
    @Column(name = "drug_code", nullable = false, unique = true, length = 50)
    private String drugCode;

    /**
     * Generic name (INN - International Nonproprietary Name)
     */
    @Column(name = "generic_name", nullable = false, length = 200)
    private String genericName;

    /**
     * Brand name (proprietary/trade name)
     */
    @Column(name = "brand_name", length = 200)
    private String brandName;

    /**
     * Drug category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private DrugCategory category;

    /**
     * Drug strength/dosage (e.g., "500mg", "250mg/5ml")
     */
    @Column(name = "strength", length = 100)
    private String strength;

    /**
     * Drug unit
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false, length = 50)
    private DrugUnit unit;

    /**
     * Dosage form (e.g., "Film-coated tablet", "Oral suspension")
     */
    @Column(name = "dosage_form", length = 200)
    private String dosageForm;

    /**
     * Route of administration (e.g., "Oral", "IV", "IM", "Topical")
     */
    @Column(name = "route_of_administration", length = 100)
    private String routeOfAdministration;

    /**
     * Manufacturer name
     */
    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    /**
     * Country of origin
     */
    @Column(name = "country_of_origin", length = 100)
    private String countryOfOrigin;

    /**
     * Barcode (for scanning and tracking)
     */
    @Column(name = "barcode", length = 100)
    private String barcode;

    /**
     * National Drug Code (if applicable)
     */
    @Column(name = "national_drug_code", length = 50)
    private String nationalDrugCode;

    /**
     * ATC code (Anatomical Therapeutic Chemical classification)
     */
    @Column(name = "atc_code", length = 20)
    private String atcCode;

    /**
     * Storage condition
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_condition", length = 50)
    private StorageCondition storageCondition;

    /**
     * Storage temperature range
     */
    @Column(name = "storage_temperature", length = 100)
    private String storageTemperature;

    /**
     * Special storage instructions
     */
    @Column(name = "storage_instructions", columnDefinition = "TEXT")
    private String storageInstructions;

    /**
     * Formularium status (BPJS approval)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "formularium_status", length = 50)
    private FormulariumStatus formulariumStatus;

    /**
     * BPJS drug code (for BPJS claims)
     */
    @Column(name = "bpjs_drug_code", length = 50)
    private String bpjsDrugCode;

    /**
     * Is narcotic drug
     */
    @Column(name = "is_narcotic")
    private Boolean isNarcotic;

    /**
     * Is psychotropic drug
     */
    @Column(name = "is_psychotropic")
    private Boolean isPsychotropic;

    /**
     * Is high alert medication
     */
    @Column(name = "is_high_alert")
    private Boolean isHighAlert;

    /**
     * Requires prescription
     */
    @Column(name = "requires_prescription")
    private Boolean requiresPrescription;

    /**
     * Minimum stock level (reorder point)
     */
    @Column(name = "minimum_stock_level", precision = 10, scale = 2)
    private BigDecimal minimumStockLevel;

    /**
     * Maximum stock level
     */
    @Column(name = "maximum_stock_level", precision = 10, scale = 2)
    private BigDecimal maximumStockLevel;

    /**
     * Reorder quantity
     */
    @Column(name = "reorder_quantity", precision = 10, scale = 2)
    private BigDecimal reorderQuantity;

    /**
     * Current stock quantity (denormalized for quick access)
     */
    @Column(name = "current_stock", precision = 10, scale = 2)
    private BigDecimal currentStock;

    /**
     * Unit price
     */
    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    /**
     * BPJS unit price (for BPJS patients)
     */
    @Column(name = "bpjs_unit_price", precision = 15, scale = 2)
    private BigDecimal bpjsUnitPrice;

    /**
     * Primary supplier ID
     */
    @Column(name = "primary_supplier_id")
    private UUID primarySupplierId;

    /**
     * Primary supplier name (denormalized)
     */
    @Column(name = "primary_supplier_name", length = 200)
    private String primarySupplierName;

    /**
     * Registration number (e.g., NIE - Nomor Izin Edar)
     */
    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    /**
     * Registration expiry date
     */
    @Column(name = "registration_expiry_date")
    private LocalDate registrationExpiryDate;

    /**
     * Shelf life in days
     */
    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    /**
     * Indications/therapeutic use
     */
    @Column(name = "indications", columnDefinition = "TEXT")
    private String indications;

    /**
     * Contraindications
     */
    @Column(name = "contraindications", columnDefinition = "TEXT")
    private String contraindications;

    /**
     * Side effects
     */
    @Column(name = "side_effects", columnDefinition = "TEXT")
    private String sideEffects;

    /**
     * Dosage instructions
     */
    @Column(name = "dosage_instructions", columnDefinition = "TEXT")
    private String dosageInstructions;

    /**
     * Special warnings
     */
    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warnings;

    /**
     * Package size (e.g., "10 tablets per strip", "100ml per bottle")
     */
    @Column(name = "package_size", length = 200)
    private String packageSize;

    /**
     * Is discontinued
     */
    @Column(name = "is_discontinued")
    private Boolean isDiscontinued;

    /**
     * Discontinuation date
     */
    @Column(name = "discontinuation_date")
    private LocalDate discontinuationDate;

    /**
     * Replacement drug ID (if discontinued)
     */
    @Column(name = "replacement_drug_id")
    private UUID replacementDrugId;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Check if stock is below minimum level
     */
    public boolean isLowStock() {
        if (currentStock == null || minimumStockLevel == null) {
            return false;
        }
        return currentStock.compareTo(minimumStockLevel) < 0;
    }

    /**
     * Check if requires cold chain
     */
    public boolean requiresColdChain() {
        return storageCondition == StorageCondition.REFRIGERATED ||
               storageCondition == StorageCondition.FROZEN;
    }

    /**
     * Check if is controlled substance
     */
    public boolean isControlledSubstance() {
        return (isNarcotic != null && isNarcotic) ||
               (isPsychotropic != null && isPsychotropic);
    }

    /**
     * Check if registration is expired
     */
    public boolean isRegistrationExpired() {
        if (registrationExpiryDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(registrationExpiryDate);
    }

    /**
     * Get full drug name
     */
    public String getFullName() {
        if (brandName != null && !brandName.isEmpty()) {
            return brandName + " (" + genericName + ") " + strength;
        }
        return genericName + " " + strength;
    }
}
