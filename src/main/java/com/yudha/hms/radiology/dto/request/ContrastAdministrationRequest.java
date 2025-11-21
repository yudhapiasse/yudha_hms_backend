package com.yudha.hms.radiology.dto.request;

import com.yudha.hms.radiology.constant.ContrastType;
import com.yudha.hms.radiology.constant.ReactionSeverity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Contrast Administration Request DTO.
 *
 * Used for recording contrast administration and reactions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContrastAdministrationRequest {

    /**
     * Order item ID
     */
    @NotNull(message = "ID item pemeriksaan harus diisi")
    private UUID orderItemId;

    /**
     * Patient ID
     */
    @NotNull(message = "ID pasien harus diisi")
    private UUID patientId;

    // ========== Contrast Information ==========

    /**
     * Contrast name
     */
    @Size(max = 200, message = "Nama kontras maksimal 200 karakter")
    private String contrastName;

    /**
     * Contrast type
     */
    @NotNull(message = "Tipe kontras harus dipilih")
    private ContrastType contrastType;

    /**
     * Volume in ml
     */
    @NotNull(message = "Volume harus diisi")
    @DecimalMin(value = "0.0", inclusive = false, message = "Volume harus lebih dari 0")
    private BigDecimal volumeMl;

    /**
     * Batch number
     */
    @Size(max = 100, message = "Nomor batch maksimal 100 karakter")
    private String batchNumber;

    /**
     * Administered by (user ID)
     */
    private UUID administeredBy;

    // ========== Reaction Monitoring ==========

    /**
     * Reaction observed
     */
    @Builder.Default
    private Boolean reactionObserved = false;

    /**
     * Reaction severity
     */
    private ReactionSeverity reactionSeverity;

    /**
     * Reaction description
     */
    private String reactionDescription;

    /**
     * Treatment given
     */
    private String treatmentGiven;

    /**
     * Notes
     */
    private String notes;
}
