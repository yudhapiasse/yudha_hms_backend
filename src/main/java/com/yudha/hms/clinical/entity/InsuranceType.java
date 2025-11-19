package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Insurance Type Enum.
 * Defines the type of insurance/payment method for an encounter.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum InsuranceType {
    BPJS("BPJS", "BPJS Kesehatan"),
    PRIVATE_INSURANCE("Private Insurance", "Asuransi Swasta"),
    SELF_PAY("Self Pay", "Umum/Bayar Sendiri"),
    GOVERNMENT("Government", "Pemerintah"),
    CORPORATE("Corporate", "Perusahaan");

    private final String displayName;
    private final String indonesianName;

    InsuranceType(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }

    public boolean isBpjs() {
        return this == BPJS;
    }
}