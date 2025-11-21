package com.yudha.hms.laboratory.constant;

/**
 * Sample/Specimen Type Enumeration.
 *
 * Types of biological specimens collected for laboratory testing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum SampleType {
    /**
     * Whole blood, serum, plasma samples
     */
    BLOOD("Blood", "Whole blood, serum, or plasma sample"),

    /**
     * Urine samples
     */
    URINE("Urine", "Urine sample"),

    /**
     * Fecal samples
     */
    STOOL("Stool", "Stool/fecal sample"),

    /**
     * Tissue biopsy specimens
     */
    TISSUE("Tissue", "Tissue biopsy specimen"),

    /**
     * Culture swabs (throat, nasal, wound, etc.)
     */
    SWAB("Swab", "Culture swab sample"),

    /**
     * Body fluids (CSF, pleural fluid, synovial fluid, etc.)
     */
    BODY_FLUID("Body Fluid", "Body fluid sample (CSF, pleural, synovial, etc.)"),

    /**
     * Sputum samples
     */
    SPUTUM("Sputum", "Sputum sample for respiratory testing"),

    /**
     * Bone marrow samples
     */
    BONE_MARROW("Bone Marrow", "Bone marrow aspirate or biopsy"),

    /**
     * Hair samples
     */
    HAIR("Hair", "Hair sample for toxicology or trace element testing"),

    /**
     * Saliva samples
     */
    SALIVA("Saliva", "Saliva sample");

    private final String displayName;
    private final String description;

    SampleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}