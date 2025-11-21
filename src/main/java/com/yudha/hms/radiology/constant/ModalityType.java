package com.yudha.hms.radiology.constant;

/**
 * Radiology Modality Type Enumeration.
 *
 * Types of imaging modalities available in the radiology department.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ModalityType {
    /**
     * Conventional X-Ray radiography
     */
    XRAY("X-Ray", "Radiografi konvensional menggunakan sinar-X", "Conventional radiography using X-ray radiation"),

    /**
     * Computed Tomography scan
     */
    CT_SCAN("CT Scan", "Tomografi Terkomputasi - pencitraan potongan melintang", "Computed Tomography - cross-sectional imaging"),

    /**
     * Magnetic Resonance Imaging
     */
    MRI("MRI", "Pencitraan Resonansi Magnetik - pencitraan tanpa radiasi", "Magnetic Resonance Imaging - non-ionizing imaging"),

    /**
     * Ultrasound/Ultrasonography
     */
    ULTRASOUND("Ultrasound", "Ultrasonografi - pencitraan gelombang suara", "Ultrasonography - sound wave imaging"),

    /**
     * Mammography for breast imaging
     */
    MAMMOGRAPHY("Mammography", "Pencitraan payudara untuk skrining kanker", "Breast imaging for cancer screening"),

    /**
     * Fluoroscopy - real-time X-ray
     */
    FLUOROSCOPY("Fluoroscopy", "Pencitraan sinar-X real-time", "Real-time X-ray imaging"),

    /**
     * DEXA scan for bone density
     */
    DEXA("DEXA Scan", "Pengukuran kepadatan tulang", "Bone density measurement"),

    /**
     * Angiography for vascular imaging
     */
    ANGIOGRAPHY("Angiography", "Pencitraan pembuluh darah dengan kontras", "Vascular imaging with contrast"),

    /**
     * Nuclear Medicine
     */
    NUCLEAR_MEDICINE("Nuclear Medicine", "Kedokteran Nuklir - pencitraan radiofarmaka", "Nuclear medicine - radiopharmaceutical imaging"),

    /**
     * PET Scan (Positron Emission Tomography)
     */
    PET_SCAN("PET Scan", "Tomografi Emisi Positron", "Positron Emission Tomography");

    private final String displayName;
    private final String displayNameId;
    private final String description;

    ModalityType(String displayName, String displayNameId, String description) {
        this.displayName = displayName;
        this.displayNameId = displayNameId;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameId() {
        return displayNameId;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this modality uses ionizing radiation
     */
    public boolean usesRadiation() {
        return this == XRAY || this == CT_SCAN || this == MAMMOGRAPHY ||
               this == FLUOROSCOPY || this == DEXA || this == ANGIOGRAPHY ||
               this == NUCLEAR_MEDICINE || this == PET_SCAN;
    }
}
