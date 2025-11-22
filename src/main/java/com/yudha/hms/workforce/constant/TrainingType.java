package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum TrainingType {
    INTERNAL("Internal Training", "Pelatihan Internal"),
    EXTERNAL("External Training", "Pelatihan Eksternal"),
    CERTIFICATION("Certification Course", "Kursus Sertifikasi"),
    WORKSHOP("Workshop", "Workshop"),
    SEMINAR("Seminar", "Seminar"),
    CONFERENCE("Conference", "Konferensi"),
    ORIENTATION("Orientation", "Orientasi"),
    ON_THE_JOB("On-the-Job Training", "Pelatihan di Tempat Kerja");

    private final String englishName;
    private final String indonesianName;

    TrainingType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
