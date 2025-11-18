package com.yudha.hms.patient.entity;

import com.yudha.hms.shared.constant.AddressType;
import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Patient Address Entity.
 *
 * Stores patient addresses with Indonesian-specific requirements:
 * - KTP Address (address on ID card) - Required by law
 * - Domicile Address (current residence) - Where patient actually lives
 *
 * Indonesian address hierarchy:
 * Province (Provinsi) → City/Regency (Kota/Kabupaten) → District (Kecamatan) →
 * Village (Kelurahan/Desa) → RT/RW
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "patient_address", schema = "patient_schema", indexes = {
    @Index(name = "idx_patient_address_patient", columnList = "patient_id"),
    @Index(name = "idx_patient_address_type", columnList = "address_type"),
    @Index(name = "idx_patient_address_primary", columnList = "is_primary")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientAddress extends AuditableEntity {

    /**
     * Patient reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    /**
     * Address type: KTP or DOMICILE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 20, nullable = false)
    @NotNull(message = "Address type is required")
    private AddressType addressType;

    // ========================================================================
    // ADDRESS FIELDS
    // ========================================================================

    /**
     * Address line 1
     * Street address, house number, building name
     */
    @Column(name = "address_line1", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    /**
     * Address line 2
     * Additional address information (optional)
     */
    @Column(name = "address_line2", columnDefinition = "TEXT")
    private String addressLine2;

    // ========================================================================
    // INDONESIAN ADMINISTRATIVE DIVISIONS
    // ========================================================================

    /**
     * Village/Kelurahan/Desa ID
     * Reference to master_schema.village
     */
    @Column(name = "village_id")
    private java.util.UUID villageId;

    /**
     * District/Kecamatan ID
     * Reference to master_schema.district
     */
    @Column(name = "district_id")
    private java.util.UUID districtId;

    /**
     * City/Regency (Kota/Kabupaten) ID
     * Reference to master_schema.city
     */
    @Column(name = "city_id")
    private java.util.UUID cityId;

    /**
     * Province ID
     * Reference to master_schema.province
     */
    @Column(name = "province_id")
    private java.util.UUID provinceId;

    /**
     * Postal code (5 digits)
     */
    @Column(name = "postal_code", length = 5)
    @Pattern(regexp = "^[0-9]{5}$", message = "Postal code must be exactly 5 digits")
    private String postalCode;

    // ========================================================================
    // RT/RW (INDONESIAN NEIGHBORHOOD IDENTIFIERS)
    // ========================================================================

    /**
     * RT - Rukun Tetangga (Neighborhood Association)
     * Smallest administrative division in Indonesia
     * Usually 1-3 digits
     */
    @Column(name = "rt", length = 3)
    @Pattern(regexp = "^[0-9]{1,3}$", message = "RT must be 1-3 digits")
    private String rt;

    /**
     * RW - Rukun Warga (Hamlet)
     * Group of RT, larger than RT
     * Usually 1-3 digits
     */
    @Column(name = "rw", length = 3)
    @Pattern(regexp = "^[0-9]{1,3}$", message = "RW must be 1-3 digits")
    private String rw;

    // ========================================================================
    // FLAGS
    // ========================================================================

    /**
     * Primary address flag
     * True if this is the primary address for correspondence
     */
    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Get full address as single string
     */
    @Transient
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();

        if (addressLine1 != null) {
            address.append(addressLine1);
        }

        if (addressLine2 != null && !addressLine2.isBlank()) {
            if (address.length() > 0) address.append(", ");
            address.append(addressLine2);
        }

        if (rt != null && rw != null) {
            if (address.length() > 0) address.append(", ");
            address.append("RT ").append(rt).append("/RW ").append(rw);
        }

        // Note: Village, District, City, Province names would need to be fetched from master data
        // This is a simplified version

        if (postalCode != null) {
            if (address.length() > 0) address.append(", ");
            address.append(postalCode);
        }

        return address.toString();
    }

    /**
     * Check if address is complete
     */
    @Transient
    public boolean isComplete() {
        return addressLine1 != null &&
               villageId != null &&
               districtId != null &&
               cityId != null &&
               provinceId != null;
    }
}