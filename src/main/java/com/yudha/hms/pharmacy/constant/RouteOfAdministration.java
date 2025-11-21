package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Route of Administration enumeration.
 *
 * Defines how medication is administered to the patient.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum RouteOfAdministration {
    // Oral routes
    ORAL("Oral", "PO", "Melalui mulut", "ORAL"),
    SUBLINGUAL("Sublingual", "SL", "Di bawah lidah", "ORAL"),
    BUCCAL("Bukal", "BUCCAL", "Antara pipi dan gusi", "ORAL"),

    // Injection routes
    INTRAVENOUS("Intravena", "IV", "Ke dalam pembuluh darah vena", "INJECTION"),
    INTRAMUSCULAR("Intramuskular", "IM", "Ke dalam otot", "INJECTION"),
    SUBCUTANEOUS("Subkutan", "SC", "Di bawah kulit", "INJECTION"),
    INTRADERMAL("Intradermal", "ID", "Ke dalam kulit", "INJECTION"),
    INTRA_ARTERIAL("Intra-arteri", "IA", "Ke dalam arteri", "INJECTION"),
    INTRATHECAL("Intratekal", "IT", "Ke dalam kanal tulang belakang", "INJECTION"),

    // Topical routes
    TOPICAL("Topikal", "TOP", "Pada kulit", "TOPICAL"),
    TRANSDERMAL("Transdermal", "TD", "Melalui kulit (koyo)", "TOPICAL"),

    // Eye routes
    OPHTHALMIC("Oftalmik", "OPHT", "Pada mata", "EYE"),

    // Ear routes
    OTIC("Tetes Telinga", "OTIC", "Pada telinga", "EAR"),

    // Nasal routes
    NASAL("Hidung", "NASAL", "Melalui hidung", "NASAL"),

    // Inhalation routes
    INHALATION("Inhalasi", "INH", "Melalui pernapasan", "INHALATION"),
    NEBULIZATION("Nebulisasi", "NEB", "Melalui nebulizer", "INHALATION"),

    // Rectal/Vaginal routes
    RECTAL("Rektal", "PR", "Ke dalam rektum", "RECTAL"),
    VAGINAL("Vaginal", "PV", "Ke dalam vagina", "VAGINAL"),

    // Other routes
    EPIDURAL("Epidural", "EPIDURAL", "Ke dalam ruang epidural", "INJECTION"),
    PER_TUBE("Melalui Pipa/Selang", "PT", "Melalui selang makanan", "ORAL");

    private final String displayName;
    private final String code;
    private final String description;
    private final String category;

    RouteOfAdministration(String displayName, String code, String description, String category) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
        this.category = category;
    }

    public static RouteOfAdministration fromCode(String code) {
        for (RouteOfAdministration route : values()) {
            if (route.code.equalsIgnoreCase(code)) {
                return route;
            }
        }
        throw new IllegalArgumentException("Unknown route of administration code: " + code);
    }

    /**
     * Check if route is by injection
     */
    public boolean isInjection() {
        return category.equals("INJECTION");
    }

    /**
     * Check if route is oral
     */
    public boolean isOral() {
        return category.equals("ORAL");
    }

    /**
     * Check if route is topical
     */
    public boolean isTopical() {
        return category.equals("TOPICAL");
    }

    /**
     * Check if route requires sterile technique
     */
    public boolean requiresSterileTechnique() {
        return isInjection() || this == OPHTHALMIC || this == INTRATHECAL || this == EPIDURAL;
    }
}
