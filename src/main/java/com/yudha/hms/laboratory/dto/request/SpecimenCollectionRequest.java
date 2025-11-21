package com.yudha.hms.laboratory.dto.request;

import com.yudha.hms.laboratory.constant.SampleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Specimen Collection Request DTO.
 *
 * Used for recording specimen collection.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecimenCollectionRequest {

    /**
     * Order item ID
     */
    @NotNull(message = "ID item order harus diisi")
    private UUID orderItemId;

    /**
     * Specimen type
     */
    @NotNull(message = "Tipe spesimen harus dipilih")
    private SampleType specimenType;

    /**
     * Specimen source
     */
    @Size(max = 200, message = "Sumber spesimen maksimal 200 karakter")
    private String specimenSource;

    // ========== Collection Information ==========

    /**
     * Collection timestamp
     */
    @NotNull(message = "Waktu pengambilan harus diisi")
    private LocalDateTime collectedAt;

    /**
     * Collected by user ID
     */
    @NotNull(message = "ID petugas pengambil harus diisi")
    private UUID collectedBy;

    /**
     * Collection method
     */
    @Size(max = 200, message = "Metode pengambilan maksimal 200 karakter")
    private String collectionMethod;

    /**
     * Collection site
     */
    @Size(max = 200, message = "Lokasi pengambilan maksimal 200 karakter")
    private String collectionSite;

    // ========== Specimen Details ==========

    /**
     * Volume in ml
     */
    @DecimalMin(value = "0.0", message = "Volume tidak boleh negatif")
    private BigDecimal volumeMl;

    /**
     * Container type
     */
    @Size(max = 100, message = "Tipe kontainer maksimal 100 karakter")
    private String containerType;

    /**
     * Barcode
     */
    @Size(max = 100, message = "Barcode maksimal 100 karakter")
    private String barcode;

    // ========== Fasting Status ==========

    /**
     * Fasting status met
     */
    private Boolean fastingStatusMet;

    /**
     * Notes
     */
    private String notes;
}
