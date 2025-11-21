-- V35: Create Radiology Module Tables
-- Description: Create radiology schema with examination master, order management, result entry, and reporting
-- Author: HMS Development Team
-- Date: 2025-01-21
-- Phase 11: Radiology Module

-- Create radiology schema if not exists
CREATE SCHEMA IF NOT EXISTS radiology_schema;

-- =====================================================
-- Phase 11.1: Radiology Examination Master
-- =====================================================

-- =====================================================
-- Table: radiology_modality
-- Description: Imaging modality master (X-Ray, CT, MRI, USG, etc.)
-- =====================================================
CREATE TABLE radiology_schema.radiology_modality (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,

    -- Modality characteristics
    requires_radiation BOOLEAN DEFAULT false,
    average_duration_minutes INTEGER,

    -- UI configuration
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER,
    icon VARCHAR(100),
    color VARCHAR(50),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

-- Indexes for radiology_modality
CREATE UNIQUE INDEX idx_radiology_modality_code ON radiology_schema.radiology_modality(code);
CREATE INDEX idx_radiology_modality_name ON radiology_schema.radiology_modality(name);
CREATE INDEX idx_radiology_modality_active ON radiology_schema.radiology_modality(is_active);

-- Trigger for updated_at
CREATE TRIGGER update_radiology_modality_updated_at
    BEFORE UPDATE ON radiology_schema.radiology_modality
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: radiology_examination
-- Description: Examination catalog (like lab tests)
-- =====================================================
CREATE TABLE radiology_schema.radiology_examination (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_code VARCHAR(50) NOT NULL UNIQUE,
    exam_name VARCHAR(200) NOT NULL,
    short_name VARCHAR(100),
    modality_id UUID NOT NULL,

    -- Medical coding
    cpt_code VARCHAR(20),
    icd_procedure_code VARCHAR(20),

    -- Preparation requirements
    preparation_instructions TEXT,
    fasting_required BOOLEAN DEFAULT false,
    fasting_duration_hours INTEGER,

    -- Contrast media
    requires_contrast BOOLEAN DEFAULT false,
    contrast_type VARCHAR(50), -- IODINE_BASED, GADOLINIUM_BASED, BARIUM_BASED, NONE
    contrast_volume_ml DECIMAL(10, 2),

    -- Timing
    exam_duration_minutes INTEGER,
    reporting_time_minutes INTEGER,

    -- Cost information
    base_cost DECIMAL(15, 2) NOT NULL,
    contrast_cost DECIMAL(15, 2) DEFAULT 0,
    bpjs_tariff DECIMAL(15, 2),

    -- Anatomical information
    body_part VARCHAR(200),
    laterality_applicable BOOLEAN DEFAULT false,
    positioning_notes TEXT,

    -- Clinical information
    clinical_indication TEXT,
    interpretation_guide TEXT,

    -- Configuration
    is_active BOOLEAN NOT NULL DEFAULT true,
    requires_approval BOOLEAN DEFAULT false,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_radiology_examination_modality FOREIGN KEY (modality_id)
        REFERENCES radiology_schema.radiology_modality(id),

    -- Constraints
    CONSTRAINT chk_radiology_examination_fasting_duration CHECK (fasting_duration_hours IS NULL OR fasting_duration_hours >= 0),
    CONSTRAINT chk_radiology_examination_contrast_volume CHECK (contrast_volume_ml IS NULL OR contrast_volume_ml > 0),
    CONSTRAINT chk_radiology_examination_duration CHECK (exam_duration_minutes IS NULL OR exam_duration_minutes > 0),
    CONSTRAINT chk_radiology_examination_reporting_time CHECK (reporting_time_minutes IS NULL OR reporting_time_minutes > 0),
    CONSTRAINT chk_radiology_examination_base_cost CHECK (base_cost >= 0),
    CONSTRAINT chk_radiology_examination_contrast_cost CHECK (contrast_cost >= 0)
);

-- Indexes for radiology_examination
CREATE UNIQUE INDEX idx_radiology_examination_code ON radiology_schema.radiology_examination(exam_code);
CREATE INDEX idx_radiology_examination_name ON radiology_schema.radiology_examination(exam_name);
CREATE INDEX idx_radiology_examination_modality ON radiology_schema.radiology_examination(modality_id);
CREATE INDEX idx_radiology_examination_cpt_code ON radiology_schema.radiology_examination(cpt_code);
CREATE INDEX idx_radiology_examination_body_part ON radiology_schema.radiology_examination(body_part);
CREATE INDEX idx_radiology_examination_active ON radiology_schema.radiology_examination(is_active);

-- Trigger for updated_at
CREATE TRIGGER update_radiology_examination_updated_at
    BEFORE UPDATE ON radiology_schema.radiology_examination
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: radiology_room
-- Description: Imaging rooms/equipment locations
-- =====================================================
CREATE TABLE radiology_schema.radiology_room (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_code VARCHAR(50) NOT NULL UNIQUE,
    room_name VARCHAR(200) NOT NULL,
    location VARCHAR(200),
    floor VARCHAR(50),

    -- Equipment information
    modality_id UUID NOT NULL,
    equipment_name VARCHAR(200),
    equipment_model VARCHAR(200),
    manufacturer VARCHAR(200),

    -- Maintenance tracking
    installation_date DATE,
    last_calibration_date DATE,
    next_calibration_date DATE,

    -- Operational status
    is_operational BOOLEAN DEFAULT true,
    is_available BOOLEAN DEFAULT true,
    max_bookings_per_day INTEGER,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_radiology_room_modality FOREIGN KEY (modality_id)
        REFERENCES radiology_schema.radiology_modality(id),

    -- Constraints
    CONSTRAINT chk_radiology_room_max_bookings CHECK (max_bookings_per_day IS NULL OR max_bookings_per_day > 0)
);

-- Indexes for radiology_room
CREATE UNIQUE INDEX idx_radiology_room_code ON radiology_schema.radiology_room(room_code);
CREATE INDEX idx_radiology_room_name ON radiology_schema.radiology_room(room_name);
CREATE INDEX idx_radiology_room_modality ON radiology_schema.radiology_room(modality_id);
CREATE INDEX idx_radiology_room_operational ON radiology_schema.radiology_room(is_operational);
CREATE INDEX idx_radiology_room_available ON radiology_schema.radiology_room(is_available);

-- Trigger for updated_at
CREATE TRIGGER update_radiology_room_updated_at
    BEFORE UPDATE ON radiology_schema.radiology_room
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: reporting_template
-- Description: Report templates per examination
-- =====================================================
CREATE TABLE radiology_schema.reporting_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    examination_id UUID NOT NULL,
    template_name VARCHAR(200) NOT NULL,
    template_code VARCHAR(50) NOT NULL,

    -- Template structure (JSONB for flexibility)
    -- Format: {"findings": "template text", "impression": "template text", "recommendations": "template text"}
    sections JSONB,

    -- Configuration
    is_default BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_reporting_template_examination FOREIGN KEY (examination_id)
        REFERENCES radiology_schema.radiology_examination(id),

    -- Constraints
    CONSTRAINT uq_reporting_template_exam_code UNIQUE (examination_id, template_code)
);

-- Indexes for reporting_template
CREATE INDEX idx_reporting_template_examination ON radiology_schema.reporting_template(examination_id);
CREATE INDEX idx_reporting_template_code ON radiology_schema.reporting_template(template_code);
CREATE INDEX idx_reporting_template_default ON radiology_schema.reporting_template(is_default) WHERE is_default = true;
CREATE INDEX idx_reporting_template_active ON radiology_schema.reporting_template(is_active);

-- Trigger for updated_at
CREATE TRIGGER update_reporting_template_updated_at
    BEFORE UPDATE ON radiology_schema.reporting_template
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Phase 11.2: Radiology Order Management
-- =====================================================

-- =====================================================
-- Table: radiology_order
-- Description: Radiology examination orders
-- =====================================================
CREATE TABLE radiology_schema.radiology_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(50) NOT NULL UNIQUE,

    -- Patient and encounter information
    patient_id UUID NOT NULL,
    encounter_id UUID NOT NULL,

    -- Ordering information
    ordering_doctor_id UUID NOT NULL,
    ordering_department VARCHAR(100),
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Priority
    priority VARCHAR(50) NOT NULL DEFAULT 'ROUTINE', -- ROUTINE, URGENT, EMERGENCY

    -- Clinical information
    clinical_indication TEXT,
    diagnosis_text VARCHAR(500),

    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    -- Scheduling
    scheduled_date DATE,
    scheduled_time TIME,
    room_id UUID,
    technician_id UUID,

    -- Special instructions
    notes TEXT,
    special_instructions TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_radiology_order_patient FOREIGN KEY (patient_id)
        REFERENCES patient_schema.patient(id),
    CONSTRAINT fk_radiology_order_encounter FOREIGN KEY (encounter_id)
        REFERENCES clinical_schema.encounter(id),
    CONSTRAINT fk_radiology_order_room FOREIGN KEY (room_id)
        REFERENCES radiology_schema.radiology_room(id)
);

-- Indexes for radiology_order
CREATE UNIQUE INDEX idx_radiology_order_number ON radiology_schema.radiology_order(order_number);
CREATE INDEX idx_radiology_order_patient ON radiology_schema.radiology_order(patient_id);
CREATE INDEX idx_radiology_order_encounter ON radiology_schema.radiology_order(encounter_id);
CREATE INDEX idx_radiology_order_doctor ON radiology_schema.radiology_order(ordering_doctor_id);
CREATE INDEX idx_radiology_order_status ON radiology_schema.radiology_order(status);
CREATE INDEX idx_radiology_order_priority ON radiology_schema.radiology_order(priority);
CREATE INDEX idx_radiology_order_date ON radiology_schema.radiology_order(order_date);
CREATE INDEX idx_radiology_order_scheduled_date ON radiology_schema.radiology_order(scheduled_date);
CREATE INDEX idx_radiology_order_room ON radiology_schema.radiology_order(room_id);

-- Trigger for updated_at
CREATE TRIGGER update_radiology_order_updated_at
    BEFORE UPDATE ON radiology_schema.radiology_order
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: radiology_order_item
-- Description: Order items (similar to lab order items)
-- =====================================================
CREATE TABLE radiology_schema.radiology_order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    examination_id UUID NOT NULL,

    -- Denormalized for performance
    exam_code VARCHAR(50) NOT NULL,
    exam_name VARCHAR(200) NOT NULL,

    -- Laterality (for applicable examinations)
    laterality VARCHAR(50), -- LEFT, RIGHT, BILATERAL, NOT_APPLICABLE
    quantity INTEGER NOT NULL DEFAULT 1,

    -- Pricing
    unit_price DECIMAL(15, 2) NOT NULL,
    discount_amount DECIMAL(15, 2) DEFAULT 0,
    final_price DECIMAL(15, 2) NOT NULL,

    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    -- Result reference
    result_id UUID,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_radiology_order_item_order FOREIGN KEY (order_id)
        REFERENCES radiology_schema.radiology_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_radiology_order_item_examination FOREIGN KEY (examination_id)
        REFERENCES radiology_schema.radiology_examination(id),

    -- Constraints
    CONSTRAINT chk_radiology_order_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_radiology_order_item_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_radiology_order_item_discount CHECK (discount_amount >= 0),
    CONSTRAINT chk_radiology_order_item_final_price CHECK (final_price >= 0)
);

-- Indexes for radiology_order_item
CREATE INDEX idx_radiology_order_item_order ON radiology_schema.radiology_order_item(order_id);
CREATE INDEX idx_radiology_order_item_examination ON radiology_schema.radiology_order_item(examination_id);
CREATE INDEX idx_radiology_order_item_status ON radiology_schema.radiology_order_item(status);
CREATE INDEX idx_radiology_order_item_result ON radiology_schema.radiology_order_item(result_id);

-- Trigger for updated_at
CREATE TRIGGER update_radiology_order_item_updated_at
    BEFORE UPDATE ON radiology_schema.radiology_order_item
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Phase 11.3: Result Entry and Reporting
-- =====================================================

-- =====================================================
-- Table: radiology_result
-- Description: Examination results/reports
-- =====================================================
CREATE TABLE radiology_schema.radiology_result (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    result_number VARCHAR(50) NOT NULL UNIQUE,
    order_item_id UUID NOT NULL,
    examination_id UUID NOT NULL,
    patient_id UUID NOT NULL,

    -- Examination performance
    performed_date TIMESTAMP,
    performed_by_technician_id UUID,

    -- Report content
    findings TEXT,
    impression TEXT,
    recommendations TEXT,

    -- Radiologist information
    radiologist_id UUID,
    reported_date TIMESTAMP,

    -- Finalization
    is_finalized BOOLEAN DEFAULT false,
    finalized_date TIMESTAMP,

    -- Amendment
    is_amended BOOLEAN DEFAULT false,
    amendment_reason TEXT,

    -- Image tracking
    image_count INTEGER DEFAULT 0,
    dicom_study_id VARCHAR(100),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_radiology_result_order_item FOREIGN KEY (order_item_id)
        REFERENCES radiology_schema.radiology_order_item(id),
    CONSTRAINT fk_radiology_result_examination FOREIGN KEY (examination_id)
        REFERENCES radiology_schema.radiology_examination(id),
    CONSTRAINT fk_radiology_result_patient FOREIGN KEY (patient_id)
        REFERENCES patient_schema.patient(id),

    -- Constraints
    CONSTRAINT chk_radiology_result_image_count CHECK (image_count >= 0)
);

-- Indexes for radiology_result
CREATE UNIQUE INDEX idx_radiology_result_number ON radiology_schema.radiology_result(result_number);
CREATE INDEX idx_radiology_result_order_item ON radiology_schema.radiology_result(order_item_id);
CREATE INDEX idx_radiology_result_examination ON radiology_schema.radiology_result(examination_id);
CREATE INDEX idx_radiology_result_patient ON radiology_schema.radiology_result(patient_id);
CREATE INDEX idx_radiology_result_performed_date ON radiology_schema.radiology_result(performed_date);
CREATE INDEX idx_radiology_result_technician ON radiology_schema.radiology_result(performed_by_technician_id);
CREATE INDEX idx_radiology_result_radiologist ON radiology_schema.radiology_result(radiologist_id);
CREATE INDEX idx_radiology_result_finalized ON radiology_schema.radiology_result(is_finalized);
CREATE INDEX idx_radiology_result_dicom_study ON radiology_schema.radiology_result(dicom_study_id);

-- Trigger for updated_at
CREATE TRIGGER update_radiology_result_updated_at
    BEFORE UPDATE ON radiology_schema.radiology_result
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: radiology_image
-- Description: DICOM images/studies
-- =====================================================
CREATE TABLE radiology_schema.radiology_image (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    result_id UUID NOT NULL,
    image_number INTEGER NOT NULL,

    -- DICOM identifiers
    dicom_study_uid VARCHAR(100),
    dicom_series_uid VARCHAR(100),
    dicom_instance_uid VARCHAR(100),

    -- Image information
    modality VARCHAR(50),
    body_part_examined VARCHAR(200),
    image_type VARCHAR(100),

    -- File information
    file_path VARCHAR(500),
    file_size_bytes BIGINT,

    -- Acquisition details
    acquisition_date TIMESTAMP,
    view_position VARCHAR(100),

    -- Marking
    is_key_image BOOLEAN DEFAULT false,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_radiology_image_result FOREIGN KEY (result_id)
        REFERENCES radiology_schema.radiology_result(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_radiology_image_file_size CHECK (file_size_bytes IS NULL OR file_size_bytes > 0)
);

-- Indexes for radiology_image
CREATE INDEX idx_radiology_image_result ON radiology_schema.radiology_image(result_id);
CREATE INDEX idx_radiology_image_dicom_study ON radiology_schema.radiology_image(dicom_study_uid);
CREATE INDEX idx_radiology_image_dicom_series ON radiology_schema.radiology_image(dicom_series_uid);
CREATE INDEX idx_radiology_image_dicom_instance ON radiology_schema.radiology_image(dicom_instance_uid);
CREATE INDEX idx_radiology_image_key ON radiology_schema.radiology_image(is_key_image) WHERE is_key_image = true;

-- Trigger for updated_at
CREATE TRIGGER update_radiology_image_updated_at
    BEFORE UPDATE ON radiology_schema.radiology_image
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Phase 11.4: Contrast Administration and Equipment Maintenance
-- =====================================================

-- =====================================================
-- Table: contrast_administration
-- Description: Contrast media tracking
-- =====================================================
CREATE TABLE radiology_schema.contrast_administration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_item_id UUID NOT NULL,
    patient_id UUID NOT NULL,

    -- Contrast details
    contrast_name VARCHAR(200) NOT NULL,
    contrast_type VARCHAR(50) NOT NULL, -- IODINE_BASED, GADOLINIUM_BASED, BARIUM_BASED
    volume_ml DECIMAL(10, 2) NOT NULL,
    batch_number VARCHAR(100),

    -- Administration
    administered_by UUID NOT NULL,
    administered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Reaction monitoring
    reaction_observed BOOLEAN DEFAULT false,
    reaction_severity VARCHAR(50), -- NONE, MILD, MODERATE, SEVERE
    reaction_description TEXT,
    treatment_given TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_contrast_administration_order_item FOREIGN KEY (order_item_id)
        REFERENCES radiology_schema.radiology_order_item(id),
    CONSTRAINT fk_contrast_administration_patient FOREIGN KEY (patient_id)
        REFERENCES patient_schema.patient(id),

    -- Constraints
    CONSTRAINT chk_contrast_administration_volume CHECK (volume_ml > 0)
);

-- Indexes for contrast_administration
CREATE INDEX idx_contrast_administration_order_item ON radiology_schema.contrast_administration(order_item_id);
CREATE INDEX idx_contrast_administration_patient ON radiology_schema.contrast_administration(patient_id);
CREATE INDEX idx_contrast_administration_administered_by ON radiology_schema.contrast_administration(administered_by);
CREATE INDEX idx_contrast_administration_administered_at ON radiology_schema.contrast_administration(administered_at);
CREATE INDEX idx_contrast_administration_reaction ON radiology_schema.contrast_administration(reaction_observed) WHERE reaction_observed = true;
CREATE INDEX idx_contrast_administration_batch ON radiology_schema.contrast_administration(batch_number);

-- Trigger for updated_at
CREATE TRIGGER update_contrast_administration_updated_at
    BEFORE UPDATE ON radiology_schema.contrast_administration
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: equipment_maintenance
-- Description: Equipment maintenance log
-- =====================================================
CREATE TABLE radiology_schema.equipment_maintenance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL,

    -- Maintenance details
    maintenance_type VARCHAR(50) NOT NULL, -- PREVENTIVE, CORRECTIVE, CALIBRATION
    scheduled_date DATE NOT NULL,
    performed_date DATE,

    -- Personnel
    performed_by VARCHAR(200),
    vendor_name VARCHAR(200),

    -- Results
    findings TEXT,
    actions_taken TEXT,

    -- Follow-up
    next_maintenance_date DATE,
    cost DECIMAL(15, 2),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_equipment_maintenance_room FOREIGN KEY (room_id)
        REFERENCES radiology_schema.radiology_room(id),

    -- Constraints
    CONSTRAINT chk_equipment_maintenance_cost CHECK (cost IS NULL OR cost >= 0)
);

-- Indexes for equipment_maintenance
CREATE INDEX idx_equipment_maintenance_room ON radiology_schema.equipment_maintenance(room_id);
CREATE INDEX idx_equipment_maintenance_type ON radiology_schema.equipment_maintenance(maintenance_type);
CREATE INDEX idx_equipment_maintenance_scheduled_date ON radiology_schema.equipment_maintenance(scheduled_date);
CREATE INDEX idx_equipment_maintenance_performed_date ON radiology_schema.equipment_maintenance(performed_date);
CREATE INDEX idx_equipment_maintenance_next_date ON radiology_schema.equipment_maintenance(next_maintenance_date);

-- Trigger for updated_at
CREATE TRIGGER update_equipment_maintenance_updated_at
    BEFORE UPDATE ON radiology_schema.equipment_maintenance
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Initial Data: Radiology Modalities
-- =====================================================
INSERT INTO radiology_schema.radiology_modality (code, name, description, requires_radiation, average_duration_minutes, display_order) VALUES
('XRAY', 'X-Ray', 'Conventional radiography using X-ray radiation', true, 15, 1),
('CT_SCAN', 'CT Scan', 'Computed Tomography - cross-sectional imaging', true, 30, 2),
('MRI', 'MRI', 'Magnetic Resonance Imaging - non-ionizing imaging', false, 45, 3),
('ULTRASOUND', 'Ultrasound', 'Ultrasonography - sound wave imaging', false, 20, 4),
('MAMMOGRAPHY', 'Mammography', 'Breast imaging for cancer screening', true, 20, 5),
('FLUOROSCOPY', 'Fluoroscopy', 'Real-time X-ray imaging', true, 30, 6),
('DEXA', 'DEXA Scan', 'Bone density measurement', true, 15, 7),
('ANGIOGRAPHY', 'Angiography', 'Vascular imaging with contrast', true, 60, 8);

-- =====================================================
-- Comments on Tables
-- =====================================================
COMMENT ON SCHEMA radiology_schema IS 'Radiology Module Schema - Phase 11';

COMMENT ON TABLE radiology_schema.radiology_modality IS 'Imaging modality master (X-Ray, CT, MRI, USG, etc.)';
COMMENT ON TABLE radiology_schema.radiology_examination IS 'Radiology examination catalog with CPT codes';
COMMENT ON TABLE radiology_schema.radiology_room IS 'Imaging rooms and equipment locations';
COMMENT ON TABLE radiology_schema.reporting_template IS 'Report templates per examination';

COMMENT ON TABLE radiology_schema.radiology_order IS 'Radiology examination orders';
COMMENT ON TABLE radiology_schema.radiology_order_item IS 'Individual examinations in orders';

COMMENT ON TABLE radiology_schema.radiology_result IS 'Radiology examination results and reports';
COMMENT ON TABLE radiology_schema.radiology_image IS 'DICOM images and studies';

COMMENT ON TABLE radiology_schema.contrast_administration IS 'Contrast media administration tracking';
COMMENT ON TABLE radiology_schema.equipment_maintenance IS 'Equipment maintenance log';
