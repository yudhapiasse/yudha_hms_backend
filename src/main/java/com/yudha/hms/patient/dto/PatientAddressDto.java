package com.yudha.hms.patient.dto;

import com.yudha.hms.shared.constant.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Patient Address DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientAddressDto {

    /**
     * Address type: KTP or DOMICILE
     */
    @NotNull(message = "Tipe alamat wajib diisi")
    private AddressType addressType;

    /**
     * Address line 1 (required)
     */
    @NotBlank(message = "Alamat wajib diisi")
    private String addressLine1;

    /**
     * Address line 2 (optional)
     */
    private String addressLine2;

    /**
     * Village/Kelurahan ID
     */
    private UUID villageId;

    /**
     * District/Kecamatan ID
     */
    private UUID districtId;

    /**
     * City/Kota/Kabupaten ID
     */
    private UUID cityId;

    /**
     * Province ID
     */
    private UUID provinceId;

    /**
     * Postal code (5 digits)
     */
    @Pattern(regexp = "^[0-9]{5}$", message = "Kode pos harus 5 digit angka")
    private String postalCode;

    /**
     * RT - Rukun Tetangga
     */
    @Pattern(regexp = "^[0-9]{1,3}$", message = "RT harus 1-3 digit angka")
    private String rt;

    /**
     * RW - Rukun Warga
     */
    @Pattern(regexp = "^[0-9]{1,3}$", message = "RW harus 1-3 digit angka")
    private String rw;

    /**
     * Primary address flag
     */
    private Boolean isPrimary;
}
