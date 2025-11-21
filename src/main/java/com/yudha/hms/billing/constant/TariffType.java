package com.yudha.hms.billing.constant;

import lombok.Getter;

/**
 * Tariff Type enumeration for Hospital Billing System.
 *
 * Categorizes different types of charges and fees in the hospital:
 * - ROOM: Room charges (daily room rates by class)
 * - DOCTOR_FEE: Professional fees for doctors (consultation, visit, procedures)
 * - PROCEDURE: Medical/surgical procedures
 * - LAB_TEST: Laboratory tests and examinations
 * - RADIOLOGY: Radiology examinations (X-Ray, CT, MRI, USG)
 * - MEDICINE: Pharmacy/medication charges
 * - MEDICAL_DEVICE: Medical equipment and device usage
 * - NUTRITION: Food/nutrition services
 * - AMBULANCE: Ambulance services
 * - ADMINISTRATION: Administrative fees
 * - OTHER: Miscellaneous charges
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum TariffType {
    ROOM("Kamar/Ruangan", "ROOM", "Room and accommodation charges"),
    DOCTOR_FEE("Jasa Dokter", "DOCTOR", "Doctor professional fees"),
    NURSE_FEE("Jasa Perawat", "NURSE", "Nurse professional fees"),
    PROCEDURE("Tindakan Medis", "PROCEDURE", "Medical/surgical procedures"),
    LAB_TEST("Pemeriksaan Lab", "LAB", "Laboratory tests"),
    RADIOLOGY("Pemeriksaan Radiologi", "RADIOLOGY", "Radiology examinations"),
    MEDICINE("Obat & Alkes", "MEDICINE", "Pharmacy and medical supplies"),
    MEDICAL_DEVICE("Alat Kesehatan", "DEVICE", "Medical equipment usage"),
    NUTRITION("Gizi/Makanan", "NUTRITION", "Nutrition and food services"),
    AMBULANCE("Ambulans", "AMBULANCE", "Ambulance services"),
    ADMINISTRATION("Administrasi", "ADMIN", "Administrative fees"),
    CONSULTATION("Konsultasi", "CONSULT", "Medical consultation"),
    EMERGENCY("IGD/Emergency", "EMERGENCY", "Emergency department services"),
    ICU("ICU/NICU/PICU", "ICU", "Intensive care unit"),
    SURGERY_ROOM("Kamar Operasi", "OR", "Operating room charges"),
    ANESTHESIA("Anestesi", "ANESTHESIA", "Anesthesia services"),
    PHYSICAL_THERAPY("Fisioterapi", "PHYSIO", "Physical therapy services"),
    OTHER("Lain-lain", "OTHER", "Other miscellaneous charges");

    private final String displayName;
    private final String code;
    private final String description;

    TariffType(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Get TariffType from code
     *
     * @param code tariff type code
     * @return TariffType enum
     */
    public static TariffType fromCode(String code) {
        for (TariffType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown tariff type code: " + code);
    }

    /**
     * Check if this tariff type is for professional services
     *
     * @return true if professional service
     */
    public boolean isProfessionalService() {
        return this == DOCTOR_FEE ||
               this == NURSE_FEE ||
               this == CONSULTATION;
    }

    /**
     * Check if this tariff type is for facility usage
     *
     * @return true if facility charge
     */
    public boolean isFacilityCharge() {
        return this == ROOM ||
               this == ICU ||
               this == SURGERY_ROOM;
    }

    /**
     * Check if this tariff type is for diagnostic services
     *
     * @return true if diagnostic service
     */
    public boolean isDiagnosticService() {
        return this == LAB_TEST || this == RADIOLOGY;
    }
}
