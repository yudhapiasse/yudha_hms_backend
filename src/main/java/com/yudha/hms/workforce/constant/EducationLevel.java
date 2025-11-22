package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum EducationLevel {
    SD("Elementary School", "Sekolah Dasar"),
    SMP("Junior High School", "Sekolah Menengah Pertama"),
    SMA("Senior High School", "Sekolah Menengah Atas"),
    D1("Diploma 1", "Diploma 1"),
    D3("Diploma 3", "Diploma 3"),
    D4("Diploma 4", "Diploma 4"),
    S1("Bachelor's Degree", "Sarjana (S1)"),
    S2("Master's Degree", "Magister (S2)"),
    S3("Doctoral Degree", "Doktor (S3)"),
    SPECIALIST_1("Medical Specialist 1", "Spesialis 1"),
    SPECIALIST_2("Medical Specialist 2", "Spesialis 2");

    private final String englishName;
    private final String indonesianName;

    EducationLevel(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
