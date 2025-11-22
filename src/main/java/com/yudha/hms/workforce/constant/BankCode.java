package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum BankCode {
    BCA("BCA", "Bank Central Asia"),
    MANDIRI("MANDIRI", "Bank Mandiri"),
    BNI("BNI", "Bank Negara Indonesia"),
    BRI("BRI", "Bank Rakyat Indonesia"),
    PERMATA("PERMATA", "Bank Permata"),
    CIMB_NIAGA("CIMB_NIAGA", "Bank CIMB Niaga"),
    DANAMON("DANAMON", "Bank Danamon"),
    BTN("BTN", "Bank Tabungan Negara"),
    PANIN("PANIN", "Bank Panin"),
    OTHER("OTHER", "Bank Lainnya");

    private final String code;
    private final String fullName;

    BankCode(String code, String fullName) {
        this.code = code;
        this.fullName = fullName;
    }
}
