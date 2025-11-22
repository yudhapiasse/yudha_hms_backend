package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum NotificationMethod {

    PHONE("Phone", "Telepon"),
    SMS("SMS", "SMS"),
    EMAIL("Email", "Email"),
    IN_PERSON("In Person", "Langsung"),
    PAGING_SYSTEM("Paging System", "Sistem Paging");

    private final String englishName;
    private final String indonesianName;

    NotificationMethod(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
