package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum CheckInMethod {
    FINGERPRINT("Fingerprint", "Sidik Jari"),
    FACE_RECOGNITION("Face Recognition", "Pengenalan Wajah"),
    MANUAL("Manual Entry", "Input Manual"),
    RFID("RFID Card", "Kartu RFID"),
    MOBILE_APP("Mobile Application", "Aplikasi Mobile");

    private final String englishName;
    private final String indonesianName;

    CheckInMethod(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
