-- =============================================================================
-- Migration: V33__create_dispensing_tables.sql
-- Description: Creates dispensing management tables with queue, counseling, and return support
--              for Phase 9.4 (Prescription queue, barcode verification, counseling, returns)
-- Author: HMS Development Team
-- Date: 2025-01-21
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Table: dispensing
-- Description: Main dispensing transactions with queue management and workflow
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.dispensing (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dispensing_number VARCHAR(50) NOT NULL UNIQUE,
    prescription_id UUID,
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200),
    patient_medical_record_number VARCHAR(50),
    location_id UUID NOT NULL,
    location_name VARCHAR(200),
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    priority INTEGER DEFAULT 0,
    is_urgent BOOLEAN DEFAULT FALSE,
    dispensing_date TIMESTAMP,
    queue_number VARCHAR(20),
    queue_position INTEGER,
    estimated_ready_time TIMESTAMP,
    actual_ready_time TIMESTAMP,
    total_items INTEGER DEFAULT 0,
    total_quantity DECIMAL(15,2) DEFAULT 0,
    total_amount DECIMAL(15,2) DEFAULT 0,
    subtotal DECIMAL(15,2) DEFAULT 0,
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    discount_amount DECIMAL(15,2) DEFAULT 0,
    tax_percentage DECIMAL(5,2) DEFAULT 0,
    tax_amount DECIMAL(15,2) DEFAULT 0,
    prepared_by_id UUID,
    prepared_by_name VARCHAR(200),
    prepared_at TIMESTAMP,
    verified_by_id UUID,
    verified_by_name VARCHAR(200),
    verified_at TIMESTAMP,
    dispensed_by_id UUID,
    dispensed_by_name VARCHAR(200),
    dispensed_at TIMESTAMP,
    received_by_name VARCHAR(200),
    received_by_relationship VARCHAR(100),
    barcode_scanned BOOLEAN DEFAULT FALSE,
    verification_passed BOOLEAN,
    verification_notes TEXT,
    counseling_required BOOLEAN DEFAULT FALSE,
    counseling_completed BOOLEAN DEFAULT FALSE,
    labels_printed BOOLEAN DEFAULT FALSE,
    labels_printed_at TIMESTAMP,
    payment_status VARCHAR(50),
    payment_method VARCHAR(50),
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),

    CONSTRAINT fk_dispensing_prescription FOREIGN KEY (prescription_id)
        REFERENCES pharmacy_schema.prescription(id)
);

CREATE INDEX idx_dispensing_number ON pharmacy_schema.dispensing(dispensing_number);
CREATE INDEX idx_dispensing_prescription ON pharmacy_schema.dispensing(prescription_id);
CREATE INDEX idx_dispensing_patient ON pharmacy_schema.dispensing(patient_id);
CREATE INDEX idx_dispensing_status ON pharmacy_schema.dispensing(status);
CREATE INDEX idx_dispensing_type ON pharmacy_schema.dispensing(type);
CREATE INDEX idx_dispensing_location ON pharmacy_schema.dispensing(location_id);
CREATE INDEX idx_dispensing_date ON pharmacy_schema.dispensing(dispensing_date);

COMMENT ON TABLE pharmacy_schema.dispensing IS 'Main dispensing transactions with queue management';
COMMENT ON COLUMN pharmacy_schema.dispensing.status IS 'Status: QUEUE, PREPARING, VERIFICATION, READY, DISPENSED, PARTIALLY_DISPENSED, ON_HOLD, CANCELLED, RETURNED';
COMMENT ON COLUMN pharmacy_schema.dispensing.type IS 'Type: PRESCRIPTION, OTC, UNIT_DOSE, EMERGENCY, OUTPATIENT, INPATIENT, NARCOTIC, PSYCHOTROPIC';

-- -----------------------------------------------------------------------------
-- Table: dispensing_item
-- Description: Individual items in dispensing with batch tracking and verification
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.dispensing_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dispensing_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    prescription_item_id UUID,
    drug_id UUID NOT NULL,
    batch_id UUID,
    batch_number VARCHAR(50),
    quantity_prescribed DECIMAL(10,2),
    quantity_dispensed DECIMAL(10,2) NOT NULL DEFAULT 0,
    quantity_returned DECIMAL(10,2) DEFAULT 0,
    unit_price DECIMAL(15,2) NOT NULL DEFAULT 0,
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    discount_amount DECIMAL(15,2) DEFAULT 0,
    tax_percentage DECIMAL(5,2) DEFAULT 0,
    tax_amount DECIMAL(15,2) DEFAULT 0,
    total_amount DECIMAL(15,2) DEFAULT 0,
    dosage_instruction TEXT,
    frequency VARCHAR(100),
    duration VARCHAR(100),
    route VARCHAR(50),
    special_instructions TEXT,
    barcode_verified BOOLEAN DEFAULT FALSE,
    barcode_scanned_at TIMESTAMP,
    substituted BOOLEAN DEFAULT FALSE,
    substitution_reason TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,

    CONSTRAINT fk_dispensing_item_dispensing FOREIGN KEY (dispensing_id)
        REFERENCES pharmacy_schema.dispensing(id) ON DELETE CASCADE,
    CONSTRAINT fk_dispensing_item_prescription_item FOREIGN KEY (prescription_item_id)
        REFERENCES pharmacy_schema.prescription_item(id),
    CONSTRAINT fk_dispensing_item_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id),
    CONSTRAINT fk_dispensing_item_batch FOREIGN KEY (batch_id)
        REFERENCES pharmacy_schema.stock_batch(id)
);

CREATE INDEX idx_dispensing_item_dispensing ON pharmacy_schema.dispensing_item(dispensing_id);
CREATE INDEX idx_dispensing_item_drug ON pharmacy_schema.dispensing_item(drug_id);
CREATE INDEX idx_dispensing_item_batch ON pharmacy_schema.dispensing_item(batch_id);
CREATE INDEX idx_dispensing_item_prescription_item ON pharmacy_schema.dispensing_item(prescription_item_id);

COMMENT ON TABLE pharmacy_schema.dispensing_item IS 'Line items in dispensing with batch tracking and barcode verification';

-- -----------------------------------------------------------------------------
-- Table: patient_counseling
-- Description: Patient counseling documentation for medication education
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.patient_counseling (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dispensing_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200),
    status VARCHAR(50) NOT NULL,
    counseling_date TIMESTAMP,
    scheduled_date TIMESTAMP,
    pharmacist_id UUID,
    pharmacist_name VARCHAR(200),
    duration_minutes INTEGER,
    counseling_location VARCHAR(100),
    drug_information_provided BOOLEAN DEFAULT FALSE,
    dosage_instructions_explained BOOLEAN DEFAULT FALSE,
    side_effects_discussed BOOLEAN DEFAULT FALSE,
    interactions_discussed BOOLEAN DEFAULT FALSE,
    storage_instructions_given BOOLEAN DEFAULT FALSE,
    adherence_counseling_provided BOOLEAN DEFAULT FALSE,
    lifestyle_modifications_discussed BOOLEAN DEFAULT FALSE,
    patient_understands BOOLEAN,
    patient_has_questions BOOLEAN,
    patient_questions TEXT,
    patient_concerns TEXT,
    patient_signature_obtained BOOLEAN DEFAULT FALSE,
    patient_consent_given BOOLEAN DEFAULT FALSE,
    written_materials_provided BOOLEAN DEFAULT FALSE,
    materials_provided TEXT,
    follow_up_required BOOLEAN DEFAULT FALSE,
    follow_up_date TIMESTAMP,
    follow_up_reason TEXT,
    counseling_notes TEXT,
    decline_reason TEXT,
    completed_by_id UUID,
    completed_by_name VARCHAR(200),
    completed_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,

    CONSTRAINT fk_counseling_dispensing FOREIGN KEY (dispensing_id)
        REFERENCES pharmacy_schema.dispensing(id)
);

CREATE INDEX idx_counseling_dispensing ON pharmacy_schema.patient_counseling(dispensing_id);
CREATE INDEX idx_counseling_patient ON pharmacy_schema.patient_counseling(patient_id);
CREATE INDEX idx_counseling_status ON pharmacy_schema.patient_counseling(status);
CREATE INDEX idx_counseling_date ON pharmacy_schema.patient_counseling(counseling_date);
CREATE INDEX idx_counseling_pharmacist ON pharmacy_schema.patient_counseling(pharmacist_id);

COMMENT ON TABLE pharmacy_schema.patient_counseling IS 'Patient counseling documentation for medication education';
COMMENT ON COLUMN pharmacy_schema.patient_counseling.status IS 'Status: PENDING, IN_PROGRESS, COMPLETED, DECLINED, NOT_REQUIRED, RESCHEDULED';

-- -----------------------------------------------------------------------------
-- Table: drug_return
-- Description: Drug returns and exchanges with reason tracking
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.drug_return (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_number VARCHAR(50) NOT NULL UNIQUE,
    dispensing_id UUID NOT NULL,
    dispensing_item_id UUID NOT NULL,
    drug_id UUID NOT NULL,
    batch_id UUID,
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200),
    location_id UUID NOT NULL,
    location_name VARCHAR(200),
    return_date TIMESTAMP NOT NULL,
    reason VARCHAR(50) NOT NULL,
    reason_details TEXT,
    quantity_returned DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    refund_amount DECIMAL(15,2),
    refund_processed BOOLEAN DEFAULT FALSE,
    refund_method VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING',
    can_restock BOOLEAN,
    restocked BOOLEAN DEFAULT FALSE,
    restocked_at TIMESTAMP,
    restocked_by_id UUID,
    restocked_by_name VARCHAR(200),
    is_pharmacy_error BOOLEAN,
    incident_report_required BOOLEAN DEFAULT FALSE,
    incident_report_number VARCHAR(50),
    quality_issue BOOLEAN DEFAULT FALSE,
    supplier_notified BOOLEAN DEFAULT FALSE,
    returned_by_name VARCHAR(200),
    returned_by_relationship VARCHAR(100),
    received_by_id UUID,
    received_by_name VARCHAR(200),
    approved_by_id UUID,
    approved_by_name VARCHAR(200),
    approved_at TIMESTAMP,
    is_approved BOOLEAN DEFAULT FALSE,
    rejection_reason TEXT,
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),

    CONSTRAINT fk_return_dispensing FOREIGN KEY (dispensing_id)
        REFERENCES pharmacy_schema.dispensing(id),
    CONSTRAINT fk_return_dispensing_item FOREIGN KEY (dispensing_item_id)
        REFERENCES pharmacy_schema.dispensing_item(id),
    CONSTRAINT fk_return_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id),
    CONSTRAINT fk_return_batch FOREIGN KEY (batch_id)
        REFERENCES pharmacy_schema.stock_batch(id)
);

CREATE INDEX idx_return_number ON pharmacy_schema.drug_return(return_number);
CREATE INDEX idx_return_dispensing ON pharmacy_schema.drug_return(dispensing_id);
CREATE INDEX idx_return_dispensing_item ON pharmacy_schema.drug_return(dispensing_item_id);
CREATE INDEX idx_return_patient ON pharmacy_schema.drug_return(patient_id);
CREATE INDEX idx_return_reason ON pharmacy_schema.drug_return(reason);
CREATE INDEX idx_return_date ON pharmacy_schema.drug_return(return_date);
CREATE INDEX idx_return_status ON pharmacy_schema.drug_return(status);

COMMENT ON TABLE pharmacy_schema.drug_return IS 'Drug returns and exchanges with reason tracking';
COMMENT ON COLUMN pharmacy_schema.drug_return.reason IS 'Reason: WRONG_DRUG, WRONG_DOSAGE, WRONG_QUANTITY, EXPIRED, DAMAGED, PATIENT_REFUSED, DOCTOR_CANCELLED, DUPLICATE, ADVERSE_REACTION, PATIENT_DISCHARGED, THERAPEUTIC_CHANGE, PATIENT_DECEASED, OTHER';

-- -----------------------------------------------------------------------------
-- Table: drug_label
-- Description: Generated drug labels with instructions for patients
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.drug_label (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dispensing_id UUID NOT NULL,
    dispensing_item_id UUID NOT NULL,
    drug_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200),
    patient_age INTEGER,
    patient_weight DOUBLE PRECISION,
    drug_name VARCHAR(500) NOT NULL,
    drug_strength VARCHAR(100),
    drug_form VARCHAR(100),
    quantity_dispensed VARCHAR(50),
    batch_number VARCHAR(50),
    expiry_date VARCHAR(50),
    dosage_instruction TEXT,
    frequency VARCHAR(200),
    duration VARCHAR(100),
    route VARCHAR(50),
    special_instructions TEXT,
    warnings TEXT,
    precautions TEXT,
    storage_instructions TEXT,
    side_effects TEXT,
    prescriber_name VARCHAR(200),
    prescription_date TIMESTAMP,
    prescription_number VARCHAR(50),
    pharmacy_name VARCHAR(200),
    pharmacy_address TEXT,
    pharmacy_phone VARCHAR(50),
    pharmacist_name VARCHAR(200),
    label_format VARCHAR(50) DEFAULT 'STANDARD',
    label_size VARCHAR(50),
    barcode_data VARCHAR(200),
    qr_code_data TEXT,
    language VARCHAR(10) DEFAULT 'id',
    print_count INTEGER DEFAULT 0,
    printed_at TIMESTAMP,
    printed_by_id UUID,
    printed_by_name VARCHAR(200),
    template_name VARCHAR(100),
    additional_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),

    CONSTRAINT fk_label_dispensing FOREIGN KEY (dispensing_id)
        REFERENCES pharmacy_schema.dispensing(id),
    CONSTRAINT fk_label_dispensing_item FOREIGN KEY (dispensing_item_id)
        REFERENCES pharmacy_schema.dispensing_item(id),
    CONSTRAINT fk_label_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id)
);

CREATE INDEX idx_label_dispensing ON pharmacy_schema.drug_label(dispensing_id);
CREATE INDEX idx_label_dispensing_item ON pharmacy_schema.drug_label(dispensing_item_id);
CREATE INDEX idx_label_patient ON pharmacy_schema.drug_label(patient_id);
CREATE INDEX idx_label_printed_at ON pharmacy_schema.drug_label(printed_at);

COMMENT ON TABLE pharmacy_schema.drug_label IS 'Generated drug labels with instructions for patients';

-- =============================================================================
-- End of Migration V33
-- =============================================================================
