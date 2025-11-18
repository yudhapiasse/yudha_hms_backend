package com.yudha.hms.shared.constant;

import lombok.Getter;

/**
 * Allergen Type enumeration.
 *
 * Categories of allergens for patient allergy tracking.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Getter
public enum AllergenType {
    DRUG("Obat", "Drug/Medication allergy"),
    FOOD("Makanan", "Food allergy"),
    ENVIRONMENTAL("Lingkungan", "Environmental allergy (pollen, dust, etc.)"),
    LATEX("Lateks", "Latex allergy"),
    INSECT("Serangga", "Insect allergy (bee stings, etc.)"),
    ANIMAL("Hewan", "Animal allergy"),
    OTHER("Lainnya", "Other allergens");

    private final String displayName;
    private final String description;

    AllergenType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}