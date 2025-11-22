package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum PtkpStatus {
    TK_0("TK/0", "Tidak Kawin - 0 Tanggungan"),
    TK_1("TK/1", "Tidak Kawin - 1 Tanggungan"),
    TK_2("TK/2", "Tidak Kawin - 2 Tanggungan"),
    TK_3("TK/3", "Tidak Kawin - 3 Tanggungan"),
    K_0("K/0", "Kawin - 0 Tanggungan"),
    K_1("K/1", "Kawin - 1 Tanggungan"),
    K_2("K/2", "Kawin - 2 Tanggungan"),
    K_3("K/3", "Kawin - 3 Tanggungan"),
    K_I_0("K/I/0", "Kawin Istri Kerja - 0 Tanggungan"),
    K_I_1("K/I/1", "Kawin Istri Kerja - 1 Tanggungan"),
    K_I_2("K/I/2", "Kawin Istri Kerja - 2 Tanggungan"),
    K_I_3("K/I/3", "Kawin Istri Kerja - 3 Tanggungan");

    private final String code;
    private final String indonesianName;

    PtkpStatus(String code, String indonesianName) {
        this.code = code;
        this.indonesianName = indonesianName;
    }
}
