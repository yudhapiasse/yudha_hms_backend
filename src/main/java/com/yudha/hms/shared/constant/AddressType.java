package com.yudha.hms.shared.constant;

import lombok.Getter;

/**
 * Address Type enumeration for Indonesian patient addresses.
 *
 * Indonesian law requires both KTP (ID card) address and domicile (current residence) address.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Getter
public enum AddressType {
    /**
     * KTP Address - Address on Indonesian ID card (Kartu Tanda Penduduk)
     * This is the official registered address
     */
    KTP("Alamat KTP", "Alamat sesuai KTP"),

    /**
     * Domicile Address - Current residence address
     * This is where the patient actually lives
     */
    DOMICILE("Alamat Domisili", "Alamat tempat tinggal saat ini");

    private final String displayName;
    private final String description;

    AddressType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}