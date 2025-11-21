-- =============================================================================
-- Migration: V31__create_prescription_tables.sql
-- Description: Creates prescription, prescription_item, and prescription_verification tables
--              for e-Prescribing system (Phase 9.2)
-- Author: HMS Development Team
-- Date: 2025-01-21
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Table: prescription
-- Description: Main prescription table for e-prescribing
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.prescription (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_number VARCHAR(50) NOT NULL UNIQUE,
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200),
    encounter_id UUID,
    doctor_id UUID NOT NULL,
    doctor_name VARCHAR(200),
    prescription_date DATE NOT NULL,
    prescription_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    valid_until DATE NOT NULL,
    diagnosis VARCHAR(500),
    icd10_codes VARCHAR(500),
    special_instructions TEXT,
    allergies TEXT,
    has_interactions BOOLEAN DEFAULT FALSE,
    interaction_warnings TEXT,
    submitted_at TIMESTAMP,
    verified_at TIMESTAMP,
    verified_by UUID,
    verified_by_name VARCHAR(200),
    dispensed_at TIMESTAMP,
    dispensed_by UUID,
    dispensed_by_name VARCHAR(200),
    is_controlled BOOLEAN DEFAULT FALSE,
    requires_authorization BOOLEAN DEFAULT FALSE,
    authorization_number VARCHAR(100),
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

-- Indexes for prescription table
CREATE INDEX idx_prescription_number ON pharmacy_schema.prescription(prescription_number);
CREATE INDEX idx_prescription_patient ON pharmacy_schema.prescription(patient_id);
CREATE INDEX idx_prescription_encounter ON pharmacy_schema.prescription(encounter_id);
CREATE INDEX idx_prescription_doctor ON pharmacy_schema.prescription(doctor_id);
CREATE INDEX idx_prescription_date ON pharmacy_schema.prescription(prescription_date);
CREATE INDEX idx_prescription_status ON pharmacy_schema.prescription(status);
CREATE INDEX idx_prescription_type ON pharmacy_schema.prescription(prescription_type);
CREATE INDEX idx_prescription_controlled ON pharmacy_schema.prescription(is_controlled);
CREATE INDEX idx_prescription_active ON pharmacy_schema.prescription(active);
CREATE INDEX idx_prescription_deleted ON pharmacy_schema.prescription(deleted_at);
CREATE INDEX idx_prescription_submitted_at ON pharmacy_schema.prescription(submitted_at);
CREATE INDEX idx_prescription_verified_at ON pharmacy_schema.prescription(verified_at);

COMMENT ON TABLE pharmacy_schema.prescription IS 'Main prescription table for e-prescribing system';
COMMENT ON COLUMN pharmacy_schema.prescription.prescription_number IS 'Unique prescription number (e.g., RX-20250121-0001)';
COMMENT ON COLUMN pharmacy_schema.prescription.prescription_type IS 'Type: REGULAR, NARCOTIC, PSYCHOTROPIC, HIGH_ALERT, EMERGENCY, STANDING_ORDER, STAT, DISCHARGE';
COMMENT ON COLUMN pharmacy_schema.prescription.status IS 'Status: DRAFT, PENDING_VERIFICATION, VERIFIED, REJECTED, DISPENSED, PARTIALLY_DISPENSED, CANCELLED, EXPIRED';
COMMENT ON COLUMN pharmacy_schema.prescription.has_interactions IS 'Whether prescription contains drug interactions';
COMMENT ON COLUMN pharmacy_schema.prescription.is_controlled IS 'Whether prescription contains controlled substances (narcotics/psychotropics)';

-- -----------------------------------------------------------------------------
-- Table: prescription_item
-- Description: Individual medication items in prescription
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.prescription_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    drug_id UUID NOT NULL,
    drug_code VARCHAR(50) NOT NULL,
    drug_name VARCHAR(200) NOT NULL,
    strength VARCHAR(100),
    dosage_form VARCHAR(200),
    dose_quantity DECIMAL(10,2) NOT NULL,
    dose_unit VARCHAR(50) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    custom_frequency VARCHAR(200),
    route VARCHAR(50) NOT NULL,
    duration_days INTEGER,
    quantity_to_dispense DECIMAL(10,2) NOT NULL,
    quantity_dispensed DECIMAL(10,2) DEFAULT 0,
    unit_price DECIMAL(15,2),
    total_price DECIMAL(15,2),
    instructions TEXT,
    special_instructions TEXT,
    is_prn BOOLEAN DEFAULT FALSE,
    prn_indication VARCHAR(200),
    substitution_allowed BOOLEAN DEFAULT TRUE,
    substituted_drug_id UUID,
    substituted_drug_name VARCHAR(200),
    substitution_reason VARCHAR(500),
    is_controlled BOOLEAN DEFAULT FALSE,
    is_high_alert BOOLEAN DEFAULT FALSE,
    interaction_warnings TEXT,
    label_printed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,

    CONSTRAINT fk_prescription_item_prescription FOREIGN KEY (prescription_id)
        REFERENCES pharmacy_schema.prescription(id) ON DELETE CASCADE,
    CONSTRAINT fk_prescription_item_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id),
    CONSTRAINT fk_prescription_item_substituted_drug FOREIGN KEY (substituted_drug_id)
        REFERENCES pharmacy_schema.drug(id)
);

-- Indexes for prescription_item table
CREATE INDEX idx_prescription_item_prescription ON pharmacy_schema.prescription_item(prescription_id);
CREATE INDEX idx_prescription_item_drug ON pharmacy_schema.prescription_item(drug_id);
CREATE INDEX idx_prescription_item_controlled ON pharmacy_schema.prescription_item(is_controlled);
CREATE INDEX idx_prescription_item_high_alert ON pharmacy_schema.prescription_item(is_high_alert);
CREATE INDEX idx_prescription_item_substituted ON pharmacy_schema.prescription_item(substituted_drug_id);

COMMENT ON TABLE pharmacy_schema.prescription_item IS 'Individual medication items within prescriptions';
COMMENT ON COLUMN pharmacy_schema.prescription_item.frequency IS 'Dosage frequency: ONCE_DAILY, TWICE_DAILY, THREE_TIMES_DAILY, etc.';
COMMENT ON COLUMN pharmacy_schema.prescription_item.route IS 'Route of administration: ORAL, INTRAVENOUS, INTRAMUSCULAR, etc.';
COMMENT ON COLUMN pharmacy_schema.prescription_item.is_prn IS 'Whether medication is PRN (as needed)';
COMMENT ON COLUMN pharmacy_schema.prescription_item.substitution_allowed IS 'Whether generic substitution is allowed';
COMMENT ON COLUMN pharmacy_schema.prescription_item.is_high_alert IS 'Whether drug is high-alert/LASA medication';

-- -----------------------------------------------------------------------------
-- Table: prescription_verification
-- Description: Pharmacist verification records for prescriptions
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.prescription_verification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID NOT NULL,
    pharmacist_id UUID NOT NULL,
    pharmacist_name VARCHAR(200) NOT NULL,
    status VARCHAR(50) NOT NULL,
    verified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Interaction check
    interaction_check_performed BOOLEAN DEFAULT FALSE,
    interactions_found BOOLEAN DEFAULT FALSE,
    interaction_details TEXT,

    -- Dosage validation
    dosage_validation_performed BOOLEAN DEFAULT FALSE,
    dosage_issues_found BOOLEAN DEFAULT FALSE,
    dosage_issues TEXT,

    -- Allergy check
    allergy_check_performed BOOLEAN DEFAULT FALSE,
    allergies_found BOOLEAN DEFAULT FALSE,
    allergy_details TEXT,

    -- Verification results
    changes_made TEXT,
    rejection_reason TEXT,
    clarification_needed TEXT,
    comments TEXT,
    checklist_completed BOOLEAN DEFAULT FALSE,

    -- Dual verification (for controlled substances and high-alert medications)
    dual_verification_required BOOLEAN DEFAULT FALSE,
    second_pharmacist_id UUID,
    second_pharmacist_name VARCHAR(200),
    second_verification_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,

    CONSTRAINT fk_prescription_verification_prescription FOREIGN KEY (prescription_id)
        REFERENCES pharmacy_schema.prescription(id) ON DELETE CASCADE
);

-- Indexes for prescription_verification table
CREATE INDEX idx_prescription_verification_prescription ON pharmacy_schema.prescription_verification(prescription_id);
CREATE INDEX idx_prescription_verification_pharmacist ON pharmacy_schema.prescription_verification(pharmacist_id);
CREATE INDEX idx_prescription_verification_date ON pharmacy_schema.prescription_verification(verified_at);
CREATE INDEX idx_prescription_verification_status ON pharmacy_schema.prescription_verification(status);
CREATE INDEX idx_prescription_verification_dual ON pharmacy_schema.prescription_verification(dual_verification_required);
CREATE INDEX idx_prescription_verification_interactions ON pharmacy_schema.prescription_verification(interactions_found);
CREATE INDEX idx_prescription_verification_dosage ON pharmacy_schema.prescription_verification(dosage_issues_found);
CREATE INDEX idx_prescription_verification_allergy ON pharmacy_schema.prescription_verification(allergies_found);

COMMENT ON TABLE pharmacy_schema.prescription_verification IS 'Pharmacist verification records for prescription quality assurance';
COMMENT ON COLUMN pharmacy_schema.prescription_verification.status IS 'Verification status: PENDING, APPROVED, APPROVED_WITH_CHANGES, REJECTED, REQUIRES_CLARIFICATION';
COMMENT ON COLUMN pharmacy_schema.prescription_verification.dual_verification_required IS 'Whether dual pharmacist verification is required (controlled substances, high-alert meds)';
COMMENT ON COLUMN pharmacy_schema.prescription_verification.interaction_check_performed IS 'Whether drug interaction check was performed';
COMMENT ON COLUMN pharmacy_schema.prescription_verification.dosage_validation_performed IS 'Whether dosage validation was performed';
COMMENT ON COLUMN pharmacy_schema.prescription_verification.allergy_check_performed IS 'Whether allergy check was performed';

-- =============================================================================
-- End of Migration V31
-- =============================================================================
