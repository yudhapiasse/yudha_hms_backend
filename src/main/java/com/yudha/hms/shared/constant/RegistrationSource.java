package com.yudha.hms.shared.constant;

import lombok.Getter;

/**
 * Registration Source enumeration.
 * Indicates how the patient registered with the hospital.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Getter
public enum RegistrationSource {
    WALK_IN("Walk-in", "Pasien datang langsung"),
    ONLINE("Online", "Pendaftaran melalui website"),
    MOBILE_APP("Mobile App", "Pendaftaran melalui aplikasi mobile"),
    MOBILE_JKN("Mobile JKN", "Pendaftaran melalui Mobile JKN BPJS"),
    REFERRAL("Rujukan", "Pasien rujukan dari faskes lain"),
    EMERGENCY("Emergency", "Pasien datang ke IGD"),
    PHONE("Telepon", "Pendaftaran via telepon"),
    WHATSAPP("WhatsApp", "Pendaftaran via WhatsApp");

    private final String displayName;
    private final String description;

    RegistrationSource(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}