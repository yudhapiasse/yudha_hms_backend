package com.yudha.hms.laboratory.constant;

/**
 * Laboratory Test Category Type Enumeration.
 *
 * Major categories of laboratory tests.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum LabTestCategoryType {
    /**
     * Hematology - Blood cell counts, coagulation
     */
    HEMATOLOGY("Hematology", "Blood cell counts, coagulation studies, blood typing"),

    /**
     * Clinical Chemistry - Blood chemistry, enzymes
     */
    CHEMISTRY("Clinical Chemistry", "Blood chemistry, enzymes, metabolites"),

    /**
     * Immunology & Serology - Antibodies, antigens
     */
    IMMUNOLOGY("Immunology & Serology", "Antibodies, antigens, immunoglobulins"),

    /**
     * Microbiology - Culture, sensitivity testing
     */
    MICROBIOLOGY("Microbiology", "Culture, sensitivity testing, microscopy"),

    /**
     * Molecular Diagnostics - PCR, genetic testing
     */
    MOLECULAR("Molecular Diagnostics", "PCR, genetic testing, molecular biology"),

    /**
     * Anatomical Pathology - Histopathology, cytology
     */
    PATHOLOGY("Anatomical Pathology", "Histopathology, cytology, tissue examination"),

    /**
     * Urinalysis - Urine analysis
     */
    URINALYSIS("Urinalysis", "Urine analysis, urine chemistry"),

    /**
     * Blood Bank - Blood typing, cross-matching
     */
    BLOOD_BANK("Blood Bank", "Blood typing, cross-matching, antibody screening"),

    /**
     * Toxicology - Drug screening
     */
    TOXICOLOGY("Toxicology", "Drug screening, therapeutic drug monitoring"),

    /**
     * Endocrinology - Hormone assays
     */
    ENDOCRINE("Endocrinology", "Hormone assays, thyroid function tests");

    private final String displayName;
    private final String description;

    LabTestCategoryType(String displayName, String description) {
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