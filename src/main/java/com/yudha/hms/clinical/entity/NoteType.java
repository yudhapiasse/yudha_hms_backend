package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Progress Note Type Enum.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum NoteType {
    SOAP("SOAP Note", "Catatan SOAP"),
    OUTPATIENT_CONSULTATION("Outpatient Consultation", "Konsultasi Rawat Jalan"),
    SHIFT_HANDOVER("Shift Handover", "Serah Terima Shift"),
    CRITICAL_CARE("Critical Care Note", "Catatan Perawatan Kritis"),
    NURSING("Nursing Note", "Catatan Keperawatan"),
    PROCEDURE("Procedure Note", "Catatan Prosedur"),
    CONSULTATION("Consultation Note", "Catatan Konsultasi"),
    DISCHARGE_PLANNING("Discharge Planning", "Perencanaan Pulang");

    private final String displayName;
    private final String indonesianName;

    NoteType(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}
