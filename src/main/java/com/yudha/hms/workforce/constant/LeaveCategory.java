package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum LeaveCategory {
    ANNUAL("Annual Leave", "Cuti Tahunan"),
    SICK("Sick Leave", "Cuti Sakit"),
    MATERNITY("Maternity Leave (3 months)", "Cuti Melahirkan (3 bulan)"),
    PATERNITY("Paternity Leave (2 days)", "Cuti Ayah (2 hari)"),
    MARRIAGE("Marriage Leave (3 days)", "Cuti Menikah (3 hari)"),
    BEREAVEMENT("Bereavement Leave (2 days)", "Cuti Berkabung (2 hari)"),
    HAJJ("Hajj Leave", "Cuti Ibadah Haji"),
    STUDY("Study Leave", "Cuti Belajar"),
    UNPAID("Unpaid Leave", "Cuti Tidak Dibayar"),
    COMPASSIONATE("Compassionate Leave", "Cuti Karena Alasan Penting");

    private final String englishName;
    private final String indonesianName;

    LeaveCategory(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
