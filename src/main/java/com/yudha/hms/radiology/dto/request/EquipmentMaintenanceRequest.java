package com.yudha.hms.radiology.dto.request;

import com.yudha.hms.radiology.constant.MaintenanceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Equipment Maintenance Request DTO.
 *
 * Used for recording equipment maintenance activities.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentMaintenanceRequest {

    /**
     * Room ID
     */
    @NotNull(message = "ID ruangan harus diisi")
    private UUID roomId;

    /**
     * Maintenance type
     */
    @NotNull(message = "Tipe perawatan harus dipilih")
    private MaintenanceType maintenanceType;

    /**
     * Scheduled date
     */
    @NotNull(message = "Tanggal jadwal harus diisi")
    private LocalDate scheduledDate;

    /**
     * Performed date
     */
    private LocalDate performedDate;

    /**
     * Performed by
     */
    @Size(max = 200, message = "Dilakukan oleh maksimal 200 karakter")
    private String performedBy;

    /**
     * Vendor name
     */
    @Size(max = 200, message = "Nama vendor maksimal 200 karakter")
    private String vendorName;

    /**
     * Findings
     */
    private String findings;

    /**
     * Actions taken
     */
    private String actionsTaken;

    /**
     * Next maintenance date
     */
    private LocalDate nextMaintenanceDate;

    /**
     * Cost
     */
    @DecimalMin(value = "0.0", message = "Biaya tidak boleh negatif")
    private BigDecimal cost;

    /**
     * Notes
     */
    private String notes;
}
