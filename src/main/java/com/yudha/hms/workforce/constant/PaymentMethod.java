package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    BANK_TRANSFER("Bank Transfer", "Transfer Bank"),
    CASH("Cash", "Tunai"),
    CHEQUE("Cheque", "Cek");

    private final String englishName;
    private final String indonesianName;

    PaymentMethod(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
