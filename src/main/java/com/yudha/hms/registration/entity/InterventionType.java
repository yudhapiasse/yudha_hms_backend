package com.yudha.hms.registration.entity;

/**
 * Emergency Intervention Type Classification.
 *
 * Categories of critical interventions performed in emergency department.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
public enum InterventionType {
    RESUSCITATION(
        "Resuscitation",
        "Cardiopulmonary resuscitation and code management",
        "Resusitasi",
        true
    ),
    AIRWAY_MANAGEMENT(
        "Airway Management",
        "Intubation, tracheostomy, cricothyroidotomy, LMA insertion",
        "Manajemen Jalan Napas",
        true
    ),
    VASCULAR_ACCESS(
        "Vascular Access",
        "IV line, peripheral access",
        "Akses Vaskular",
        false
    ),
    CENTRAL_LINE(
        "Central Line",
        "Central venous catheter insertion",
        "Kateter Vena Sentral",
        true
    ),
    ARTERIAL_LINE(
        "Arterial Line",
        "Arterial catheter for blood pressure monitoring",
        "Kateter Arteri",
        true
    ),
    CHEST_TUBE(
        "Chest Tube",
        "Thoracostomy tube insertion for pneumothorax/hemothorax",
        "Selang Dada",
        true
    ),
    PROCEDURE(
        "Emergency Procedure",
        "Other emergency procedures",
        "Prosedur Darurat",
        false
    ),
    EMERGENCY_MEDICATION(
        "Emergency Medication",
        "Time-critical medication administration",
        "Obat Darurat",
        false
    ),
    TRANSFUSION(
        "Blood Transfusion",
        "Emergency blood product transfusion",
        "Transfusi Darah",
        false
    ),
    CARDIOVERSION(
        "Cardioversion",
        "Synchronized electrical cardioversion",
        "Kardioversi",
        true
    ),
    DEFIBRILLATION(
        "Defibrillation",
        "Unsynchronized defibrillation for VF/pVT",
        "Defibrilasi",
        true
    ),
    PACING(
        "Cardiac Pacing",
        "Temporary cardiac pacing",
        "Pacu Jantung",
        true
    ),
    IMAGING(
        "Emergency Imaging",
        "Bedside ultrasound, X-ray, CT",
        "Pencitraan Darurat",
        false
    ),
    WOUND_CARE(
        "Wound Care",
        "Laceration repair, wound closure",
        "Perawatan Luka",
        false
    ),
    SPLINTING(
        "Splinting/Immobilization",
        "Fracture stabilization, splinting",
        "Bidai/Imobilisasi",
        false
    ),
    CONSULTATION(
        "Emergency Consultation",
        "Specialist consultation requested",
        "Konsultasi Darurat",
        false
    );

    private final String displayName;
    private final String description;
    private final String indonesianName;
    private final boolean requiresSupervision;

    InterventionType(String displayName, String description, String indonesianName, boolean requiresSupervision) {
        this.displayName = displayName;
        this.description = description;
        this.indonesianName = indonesianName;
        this.requiresSupervision = requiresSupervision;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIndonesianName() {
        return indonesianName;
    }

    public boolean requiresSupervision() {
        return requiresSupervision;
    }

    /**
     * Check if intervention is life-saving.
     */
    public boolean isLifeSaving() {
        return this == RESUSCITATION ||
               this == AIRWAY_MANAGEMENT ||
               this == DEFIBRILLATION ||
               this == CHEST_TUBE;
    }

    /**
     * Check if intervention is invasive procedure.
     */
    public boolean isInvasiveProcedure() {
        return this == AIRWAY_MANAGEMENT ||
               this == CENTRAL_LINE ||
               this == ARTERIAL_LINE ||
               this == CHEST_TUBE ||
               this == PROCEDURE;
    }

    /**
     * Check if intervention is cardiac-related.
     */
    public boolean isCardiacIntervention() {
        return this == RESUSCITATION ||
               this == CARDIOVERSION ||
               this == DEFIBRILLATION ||
               this == PACING;
    }
}
