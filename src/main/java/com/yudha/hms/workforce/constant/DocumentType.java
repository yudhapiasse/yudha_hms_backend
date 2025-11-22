package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum DocumentType {
    KTP("National ID Card", "KTP (Kartu Tanda Penduduk)"),
    KK("Family Card", "Kartu Keluarga"),
    NPWP("Tax ID Card", "Kartu NPWP"),
    IJAZAH("Diploma", "Ijazah"),
    TRANSKRIP("Transcript", "Transkrip Nilai"),
    CV("Curriculum Vitae", "Curriculum Vitae"),
    SURAT_LAMARAN("Application Letter", "Surat Lamaran"),
    SKCK("Police Clearance", "Surat Keterangan Catatan Kepolisian"),
    SURAT_SEHAT("Health Certificate", "Surat Keterangan Sehat"),
    FOTO("Photo", "Foto"),
    CONTRACT("Employment Contract", "Kontrak Kerja"),
    APPOINTMENT_LETTER("Appointment Letter", "Surat Pengangkatan"),
    REFERENCE_LETTER("Reference Letter", "Surat Referensi"),
    OTHER("Other Document", "Dokumen Lainnya");

    private final String englishName;
    private final String indonesianName;

    DocumentType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
