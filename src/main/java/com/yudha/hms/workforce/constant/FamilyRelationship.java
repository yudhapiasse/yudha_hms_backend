package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum FamilyRelationship {
    SPOUSE("Spouse", "Pasangan/Suami/Istri"),
    CHILD("Child", "Anak"),
    PARENT("Parent", "Orang Tua"),
    SIBLING("Sibling", "Saudara Kandung"),
    PARENT_IN_LAW("Parent-in-Law", "Mertua"),
    GRANDPARENT("Grandparent", "Kakek/Nenek"),
    GRANDCHILD("Grandchild", "Cucu");

    private final String englishName;
    private final String indonesianName;

    FamilyRelationship(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
