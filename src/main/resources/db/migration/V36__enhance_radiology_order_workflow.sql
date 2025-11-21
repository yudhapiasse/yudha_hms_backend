-- V36: Enhance Radiology Order Workflow (Phase 11.2)
-- Description: Add patient safety checks, preparation checklist, and transportation coordination
-- Date: 2025-11-22

-- ========================================
-- Phase 11.2: Enhanced Order Workflow
-- ========================================

-- Add new columns to radiology_order table for safety checks and coordination
ALTER TABLE radiology_schema.radiology_order
    ADD COLUMN is_pregnant BOOLEAN DEFAULT false,
    ADD COLUMN pregnancy_verified_by UUID,
    ADD COLUMN pregnancy_verified_at TIMESTAMP,
    ADD COLUMN has_contrast_allergy BOOLEAN DEFAULT false,
    ADD COLUMN contrast_allergy_details TEXT,
    ADD COLUMN contrast_allergy_verified_by UUID,
    ADD COLUMN contrast_allergy_verified_at TIMESTAMP,
    ADD COLUMN requires_transportation BOOLEAN DEFAULT false,
    ADD COLUMN transportation_status VARCHAR(50) DEFAULT 'NOT_REQUIRED', -- NOT_REQUIRED, REQUESTED, IN_TRANSIT, ARRIVED, RETURNED
    ADD COLUMN transportation_requested_at TIMESTAMP,
    ADD COLUMN transportation_completed_at TIMESTAMP,
    ADD COLUMN transportation_notes TEXT;

-- Create patient_preparation_checklist table
CREATE TABLE radiology_schema.patient_preparation_checklist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    examination_id UUID NOT NULL,

    -- Preparation instructions
    preparation_instructions TEXT,

    -- Fasting verification
    fasting_required BOOLEAN DEFAULT false,
    fasting_verified BOOLEAN DEFAULT false,
    fasting_verified_by UUID,
    fasting_verified_at TIMESTAMP,
    fasting_hours_required INTEGER,

    -- Medication hold verification
    medication_hold_required BOOLEAN DEFAULT false,
    medication_hold_verified BOOLEAN DEFAULT false,
    medication_hold_verified_by UUID,
    medication_hold_verified_at TIMESTAMP,
    medication_hold_details TEXT,

    -- IV access verification (for contrast)
    iv_access_required BOOLEAN DEFAULT false,
    iv_access_verified BOOLEAN DEFAULT false,
    iv_access_verified_by UUID,
    iv_access_verified_at TIMESTAMP,
    iv_gauge VARCHAR(20),

    -- Pregnancy test verification (for females of childbearing age)
    pregnancy_test_required BOOLEAN DEFAULT false,
    pregnancy_test_done BOOLEAN DEFAULT false,
    pregnancy_test_result VARCHAR(20), -- NEGATIVE, POSITIVE, NOT_APPLICABLE
    pregnancy_test_date DATE,

    -- Consent verification
    consent_obtained BOOLEAN DEFAULT false,
    consent_obtained_by UUID,
    consent_obtained_at TIMESTAMP,
    consent_form_id VARCHAR(100),

    -- Flexible checklist items (JSONB for additional items)
    checklist_items JSONB,

    -- Overall completion status
    all_items_completed BOOLEAN DEFAULT false,
    completed_by UUID,
    completed_at TIMESTAMP,

    -- Notes
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),

    -- Foreign keys
    CONSTRAINT fk_prep_checklist_order FOREIGN KEY (order_id)
        REFERENCES radiology_schema.radiology_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_prep_checklist_examination FOREIGN KEY (examination_id)
        REFERENCES radiology_schema.radiology_examination(id)
);

-- Create indexes for patient_preparation_checklist
CREATE INDEX idx_prep_checklist_order ON radiology_schema.patient_preparation_checklist(order_id);
CREATE INDEX idx_prep_checklist_examination ON radiology_schema.patient_preparation_checklist(examination_id);
CREATE INDEX idx_prep_checklist_completed ON radiology_schema.patient_preparation_checklist(all_items_completed);
CREATE INDEX idx_prep_checklist_fasting_verified ON radiology_schema.patient_preparation_checklist(fasting_verified) WHERE fasting_required = true;
CREATE INDEX idx_prep_checklist_consent ON radiology_schema.patient_preparation_checklist(consent_obtained);

-- Create trigger for updated_at
CREATE TRIGGER update_patient_preparation_checklist_updated_at
    BEFORE UPDATE ON radiology_schema.patient_preparation_checklist
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create indexes for new radiology_order columns
CREATE INDEX idx_radiology_order_pregnancy ON radiology_schema.radiology_order(is_pregnant) WHERE is_pregnant = true;
CREATE INDEX idx_radiology_order_contrast_allergy ON radiology_schema.radiology_order(has_contrast_allergy) WHERE has_contrast_allergy = true;
CREATE INDEX idx_radiology_order_transportation_status ON radiology_schema.radiology_order(transportation_status);
CREATE INDEX idx_radiology_order_requires_transport ON radiology_schema.radiology_order(requires_transportation) WHERE requires_transportation = true;

-- Add comments
COMMENT ON TABLE radiology_schema.patient_preparation_checklist IS 'Patient preparation checklist for radiology examinations';
COMMENT ON COLUMN radiology_schema.radiology_order.is_pregnant IS 'Patient pregnancy status (for female patients)';
COMMENT ON COLUMN radiology_schema.radiology_order.has_contrast_allergy IS 'Patient has known contrast media allergy';
COMMENT ON COLUMN radiology_schema.radiology_order.requires_transportation IS 'Patient requires transportation assistance (inpatient)';
COMMENT ON COLUMN radiology_schema.radiology_order.transportation_status IS 'Transportation coordination status';
