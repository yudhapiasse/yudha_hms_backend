-- V34: Create Laboratory Module Tables
-- Description: Create laboratory schema with test master, order management, result entry, and reporting
-- Author: HMS Development Team
-- Date: 2025-01-21
-- Phase 10: Laboratory Module

-- Create laboratory schema if not exists
CREATE SCHEMA IF NOT EXISTS laboratory_schema;

-- =====================================================
-- Phase 10.1: Laboratory Test Master
-- =====================================================

-- =====================================================
-- Table: lab_test_category
-- Description: Test categories (Hematology, Chemistry, Microbiology, etc.)
-- =====================================================
CREATE TABLE laboratory_schema.lab_test_category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    parent_id UUID,
    level INTEGER NOT NULL DEFAULT 0,
    display_order INTEGER,
    icon VARCHAR(100),
    color VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_test_category_parent FOREIGN KEY (parent_id)
        REFERENCES laboratory_schema.lab_test_category(id)
);

-- Indexes for lab_test_category
CREATE UNIQUE INDEX idx_lab_test_category_code ON laboratory_schema.lab_test_category(code);
CREATE INDEX idx_lab_test_category_name ON laboratory_schema.lab_test_category(name);
CREATE INDEX idx_lab_test_category_parent ON laboratory_schema.lab_test_category(parent_id);
CREATE INDEX idx_lab_test_category_active ON laboratory_schema.lab_test_category(active);
CREATE INDEX idx_lab_test_category_level ON laboratory_schema.lab_test_category(level);

-- Trigger for updated_at
CREATE TRIGGER update_lab_test_category_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_test_category
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: lab_test
-- Description: Laboratory test catalog
-- =====================================================
CREATE TABLE laboratory_schema.lab_test (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    short_name VARCHAR(100),
    category_id UUID NOT NULL,

    -- LOINC coding for international standard
    loinc_code VARCHAR(20),
    loinc_display_name VARCHAR(500),

    -- Sample requirements
    sample_type VARCHAR(50) NOT NULL, -- BLOOD, URINE, STOOL, TISSUE, SWAB, BODY_FLUID
    sample_volume_ml DECIMAL(10, 2),
    sample_volume_unit VARCHAR(20),
    sample_container VARCHAR(100),
    sample_preservation VARCHAR(200),
    fasting_required BOOLEAN DEFAULT false,
    fasting_duration_hours INTEGER,

    -- Processing information
    processing_time_minutes INTEGER, -- TAT (Turnaround Time)
    urgency_available BOOLEAN DEFAULT true, -- Can be urgent/cito
    cito_processing_time_minutes INTEGER,

    -- Cost information
    base_cost DECIMAL(15, 2) NOT NULL,
    urgent_cost DECIMAL(15, 2),
    bpjs_tariff DECIMAL(15, 2),

    -- Test configuration
    test_method VARCHAR(200),
    test_methodology TEXT,
    reference_method VARCHAR(200),
    requires_approval BOOLEAN DEFAULT false,
    requires_pathologist_review BOOLEAN DEFAULT false,

    -- Critical values
    has_critical_values BOOLEAN DEFAULT false,
    critical_low_value DECIMAL(15, 4),
    critical_high_value DECIMAL(15, 4),

    -- Quality control
    qc_required BOOLEAN DEFAULT true,
    qc_frequency_hours INTEGER,
    calibration_required BOOLEAN DEFAULT false,
    calibration_frequency_days INTEGER,

    -- Additional information
    clinical_indication TEXT,
    preparation_instructions TEXT,
    interpretation_guide TEXT,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_test_category FOREIGN KEY (category_id)
        REFERENCES laboratory_schema.lab_test_category(id),

    -- Constraints
    CONSTRAINT chk_lab_test_sample_volume CHECK (sample_volume_ml IS NULL OR sample_volume_ml > 0),
    CONSTRAINT chk_lab_test_processing_time CHECK (processing_time_minutes IS NULL OR processing_time_minutes > 0),
    CONSTRAINT chk_lab_test_base_cost CHECK (base_cost >= 0),
    CONSTRAINT chk_lab_test_urgent_cost CHECK (urgent_cost IS NULL OR urgent_cost >= 0)
);

-- Indexes for lab_test
CREATE UNIQUE INDEX idx_lab_test_code ON laboratory_schema.lab_test(test_code);
CREATE INDEX idx_lab_test_name ON laboratory_schema.lab_test(name);
CREATE INDEX idx_lab_test_category ON laboratory_schema.lab_test(category_id);
CREATE INDEX idx_lab_test_loinc ON laboratory_schema.lab_test(loinc_code);
CREATE INDEX idx_lab_test_sample_type ON laboratory_schema.lab_test(sample_type);
CREATE INDEX idx_lab_test_active ON laboratory_schema.lab_test(active);

-- Trigger for updated_at
CREATE TRIGGER update_lab_test_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_test
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: lab_test_parameter
-- Description: Test parameters and normal ranges (age/gender specific)
-- =====================================================
CREATE TABLE laboratory_schema.lab_test_parameter (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lab_test_id UUID NOT NULL,
    parameter_code VARCHAR(50) NOT NULL,
    parameter_name VARCHAR(200) NOT NULL,
    parameter_short_name VARCHAR(100),

    -- Parameter properties
    data_type VARCHAR(50) NOT NULL, -- NUMERIC, TEXT, BOOLEAN, OPTION
    unit VARCHAR(50),
    display_order INTEGER,
    is_mandatory BOOLEAN DEFAULT true,

    -- Normal range (general)
    normal_range_low DECIMAL(15, 4),
    normal_range_high DECIMAL(15, 4),
    normal_range_text VARCHAR(500),

    -- Age-specific ranges (stored as JSONB for flexibility)
    -- Format: [{"ageMin": 0, "ageMax": 1, "gender": "MALE", "low": 10.0, "high": 14.0}, ...]
    age_gender_ranges JSONB,

    -- Critical values
    critical_low DECIMAL(15, 4),
    critical_high DECIMAL(15, 4),
    panic_low DECIMAL(15, 4),
    panic_high DECIMAL(15, 4),

    -- For option-based parameters (e.g., Blood Group)
    allowed_values TEXT[], -- Array of allowed values

    -- Delta check (for detecting unusual changes from previous results)
    delta_check_enabled BOOLEAN DEFAULT false,
    delta_check_percentage DECIMAL(5, 2),
    delta_check_absolute DECIMAL(15, 4),

    -- Calculation formula (if calculated from other parameters)
    is_calculated BOOLEAN DEFAULT false,
    calculation_formula TEXT,

    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_test_parameter_test FOREIGN KEY (lab_test_id)
        REFERENCES laboratory_schema.lab_test(id),

    -- Constraints
    CONSTRAINT chk_lab_test_parameter_delta_check_percentage CHECK (
        delta_check_percentage IS NULL OR (delta_check_percentage >= 0 AND delta_check_percentage <= 100)
    )
);

-- Indexes for lab_test_parameter
CREATE INDEX idx_lab_test_parameter_test ON laboratory_schema.lab_test_parameter(lab_test_id);
CREATE INDEX idx_lab_test_parameter_code ON laboratory_schema.lab_test_parameter(parameter_code);
CREATE INDEX idx_lab_test_parameter_name ON laboratory_schema.lab_test_parameter(parameter_name);
CREATE INDEX idx_lab_test_parameter_active ON laboratory_schema.lab_test_parameter(active);

-- Trigger for updated_at
CREATE TRIGGER update_lab_test_parameter_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_test_parameter
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: lab_panel
-- Description: Test panels/packages (group of related tests)
-- =====================================================
CREATE TABLE laboratory_schema.lab_panel (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    panel_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id UUID,

    -- Pricing
    package_price DECIMAL(15, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2),
    bpjs_package_tariff DECIMAL(15, 2),

    -- Configuration
    is_popular BOOLEAN DEFAULT false,
    display_order INTEGER,
    clinical_indication TEXT,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_panel_category FOREIGN KEY (category_id)
        REFERENCES laboratory_schema.lab_test_category(id),

    -- Constraints
    CONSTRAINT chk_lab_panel_package_price CHECK (package_price >= 0),
    CONSTRAINT chk_lab_panel_discount CHECK (discount_percentage IS NULL OR (discount_percentage >= 0 AND discount_percentage <= 100))
);

-- Indexes for lab_panel
CREATE UNIQUE INDEX idx_lab_panel_code ON laboratory_schema.lab_panel(panel_code);
CREATE INDEX idx_lab_panel_name ON laboratory_schema.lab_panel(name);
CREATE INDEX idx_lab_panel_category ON laboratory_schema.lab_panel(category_id);
CREATE INDEX idx_lab_panel_active ON laboratory_schema.lab_panel(active);
CREATE INDEX idx_lab_panel_popular ON laboratory_schema.lab_panel(is_popular) WHERE is_popular = true;

-- Trigger for updated_at
CREATE TRIGGER update_lab_panel_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_panel
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: lab_panel_item
-- Description: Tests included in a panel
-- =====================================================
CREATE TABLE laboratory_schema.lab_panel_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    panel_id UUID NOT NULL,
    test_id UUID NOT NULL,
    display_order INTEGER,
    is_mandatory BOOLEAN DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_panel_item_panel FOREIGN KEY (panel_id)
        REFERENCES laboratory_schema.lab_panel(id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_panel_item_test FOREIGN KEY (test_id)
        REFERENCES laboratory_schema.lab_test(id),

    -- Constraints
    CONSTRAINT uq_lab_panel_item_panel_test UNIQUE (panel_id, test_id)
);

-- Indexes for lab_panel_item
CREATE INDEX idx_lab_panel_item_panel ON laboratory_schema.lab_panel_item(panel_id);
CREATE INDEX idx_lab_panel_item_test ON laboratory_schema.lab_panel_item(test_id);

-- =====================================================
-- Phase 10.2: Lab Order Management
-- =====================================================

-- =====================================================
-- Table: lab_order
-- Description: Laboratory orders from clinical modules
-- =====================================================
CREATE TABLE laboratory_schema.lab_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(50) NOT NULL UNIQUE,

    -- Patient and encounter information
    patient_id UUID NOT NULL,
    encounter_id UUID NOT NULL,

    -- Ordering information
    ordering_doctor_id UUID NOT NULL,
    ordering_department VARCHAR(100),
    ordering_location VARCHAR(100),
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Priority
    priority VARCHAR(50) NOT NULL DEFAULT 'ROUTINE', -- ROUTINE, URGENT, CITO
    urgency_reason TEXT,

    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SCHEDULED, COLLECTED, RECEIVED, IN_PROGRESS, COMPLETED, CANCELLED
    status_reason TEXT,

    -- Clinical information
    clinical_indication TEXT,
    diagnosis_code VARCHAR(20),
    diagnosis_text VARCHAR(500),

    -- Sample collection
    collection_scheduled_at TIMESTAMP,
    collection_location VARCHAR(200),

    -- Order type
    is_recurring BOOLEAN DEFAULT false,
    recurrence_pattern VARCHAR(100), -- DAILY, WEEKLY, etc.
    recurrence_end_date DATE,
    parent_order_id UUID, -- For recurring orders

    -- Billing
    payment_method VARCHAR(50), -- CASH, INSURANCE, BPJS, etc.
    insurance_company_id UUID,
    coverage_type VARCHAR(50),

    -- Cancellation
    cancelled_at TIMESTAMP,
    cancelled_by VARCHAR(100),
    cancellation_reason TEXT,

    -- Completion
    completed_at TIMESTAMP,
    result_verified_at TIMESTAMP,
    result_verified_by UUID,

    -- Integration
    external_order_id VARCHAR(100),
    external_system VARCHAR(100),

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
    CONSTRAINT fk_lab_order_patient FOREIGN KEY (patient_id)
        REFERENCES patient_schema.patient(id),
    CONSTRAINT fk_lab_order_encounter FOREIGN KEY (encounter_id)
        REFERENCES clinical_schema.encounter(id),
    CONSTRAINT fk_lab_order_parent FOREIGN KEY (parent_order_id)
        REFERENCES laboratory_schema.lab_order(id)
);

-- Indexes for lab_order
CREATE UNIQUE INDEX idx_lab_order_number ON laboratory_schema.lab_order(order_number);
CREATE INDEX idx_lab_order_patient ON laboratory_schema.lab_order(patient_id);
CREATE INDEX idx_lab_order_encounter ON laboratory_schema.lab_order(encounter_id);
CREATE INDEX idx_lab_order_doctor ON laboratory_schema.lab_order(ordering_doctor_id);
CREATE INDEX idx_lab_order_status ON laboratory_schema.lab_order(status);
CREATE INDEX idx_lab_order_priority ON laboratory_schema.lab_order(priority);
CREATE INDEX idx_lab_order_date ON laboratory_schema.lab_order(order_date);
CREATE INDEX idx_lab_order_parent ON laboratory_schema.lab_order(parent_order_id);

-- Trigger for updated_at
CREATE TRIGGER update_lab_order_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_order
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: lab_order_item
-- Description: Individual tests in an order
-- =====================================================
CREATE TABLE laboratory_schema.lab_order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,

    -- Test or panel
    test_id UUID,
    panel_id UUID,
    item_type VARCHAR(50) NOT NULL, -- TEST, PANEL

    -- Item details
    test_name VARCHAR(200) NOT NULL,
    test_code VARCHAR(50) NOT NULL,

    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, COLLECTED, RECEIVED, IN_PROGRESS, COMPLETED, CANCELLED

    -- Pricing
    unit_price DECIMAL(15, 2) NOT NULL,
    discount_amount DECIMAL(15, 2) DEFAULT 0,
    final_price DECIMAL(15, 2) NOT NULL,

    -- Sample tracking
    specimen_id UUID,

    -- Result
    result_id UUID,
    result_completed_at TIMESTAMP,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_order_item_order FOREIGN KEY (order_id)
        REFERENCES laboratory_schema.lab_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_order_item_test FOREIGN KEY (test_id)
        REFERENCES laboratory_schema.lab_test(id),
    CONSTRAINT fk_lab_order_item_panel FOREIGN KEY (panel_id)
        REFERENCES laboratory_schema.lab_panel(id),

    -- Constraints
    CONSTRAINT chk_lab_order_item_test_or_panel CHECK (
        (test_id IS NOT NULL AND panel_id IS NULL) OR
        (test_id IS NULL AND panel_id IS NOT NULL)
    ),
    CONSTRAINT chk_lab_order_item_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_lab_order_item_discount CHECK (discount_amount >= 0),
    CONSTRAINT chk_lab_order_item_final_price CHECK (final_price >= 0)
);

-- Indexes for lab_order_item
CREATE INDEX idx_lab_order_item_order ON laboratory_schema.lab_order_item(order_id);
CREATE INDEX idx_lab_order_item_test ON laboratory_schema.lab_order_item(test_id);
CREATE INDEX idx_lab_order_item_panel ON laboratory_schema.lab_order_item(panel_id);
CREATE INDEX idx_lab_order_item_status ON laboratory_schema.lab_order_item(status);
CREATE INDEX idx_lab_order_item_specimen ON laboratory_schema.lab_order_item(specimen_id);
CREATE INDEX idx_lab_order_item_result ON laboratory_schema.lab_order_item(result_id);

-- Trigger for updated_at
CREATE TRIGGER update_lab_order_item_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_order_item
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: specimen
-- Description: Sample collection and tracking
-- =====================================================
CREATE TABLE laboratory_schema.specimen (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    specimen_number VARCHAR(50) NOT NULL UNIQUE,
    barcode VARCHAR(100) UNIQUE,

    -- Order reference
    order_id UUID NOT NULL,
    order_item_id UUID NOT NULL,

    -- Specimen details
    specimen_type VARCHAR(50) NOT NULL, -- BLOOD, URINE, STOOL, TISSUE, SWAB, BODY_FLUID
    specimen_source VARCHAR(200),
    volume_ml DECIMAL(10, 2),
    container_type VARCHAR(100),

    -- Collection information
    collected_at TIMESTAMP,
    collected_by UUID,
    collection_method VARCHAR(200),
    collection_site VARCHAR(200),

    -- Reception
    received_at TIMESTAMP,
    received_by UUID,

    -- Quality checks
    quality_status VARCHAR(50) DEFAULT 'ACCEPTABLE', -- ACCEPTABLE, REJECTED, COMPROMISED
    quality_notes TEXT,
    rejection_reason VARCHAR(500),

    -- Pre-analytical validations
    fasting_status_met BOOLEAN,
    volume_adequate BOOLEAN,
    container_appropriate BOOLEAN,
    labeling_correct BOOLEAN,
    temperature_appropriate BOOLEAN,
    hemolysis_detected BOOLEAN,
    lipemia_detected BOOLEAN,
    icterus_detected BOOLEAN,

    -- Storage
    storage_location VARCHAR(200),
    storage_temperature DECIMAL(5, 2),
    stored_at TIMESTAMP,

    -- Processing
    processing_started_at TIMESTAMP,
    processing_completed_at TIMESTAMP,
    processed_by UUID,

    -- Status tracking
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, COLLECTED, RECEIVED, PROCESSING, COMPLETED, REJECTED, DISCARDED

    -- Disposal
    disposed_at TIMESTAMP,
    disposed_by UUID,
    disposal_method VARCHAR(100),

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_specimen_order FOREIGN KEY (order_id)
        REFERENCES laboratory_schema.lab_order(id),
    CONSTRAINT fk_specimen_order_item FOREIGN KEY (order_item_id)
        REFERENCES laboratory_schema.lab_order_item(id),

    -- Constraints
    CONSTRAINT chk_specimen_volume CHECK (volume_ml IS NULL OR volume_ml > 0)
);

-- Indexes for specimen
CREATE UNIQUE INDEX idx_specimen_number ON laboratory_schema.specimen(specimen_number);
CREATE UNIQUE INDEX idx_specimen_barcode ON laboratory_schema.specimen(barcode) WHERE barcode IS NOT NULL;
CREATE INDEX idx_specimen_order ON laboratory_schema.specimen(order_id);
CREATE INDEX idx_specimen_order_item ON laboratory_schema.specimen(order_item_id);
CREATE INDEX idx_specimen_status ON laboratory_schema.specimen(status);
CREATE INDEX idx_specimen_quality ON laboratory_schema.specimen(quality_status);
CREATE INDEX idx_specimen_collected_at ON laboratory_schema.specimen(collected_at);
CREATE INDEX idx_specimen_received_at ON laboratory_schema.specimen(received_at);

-- Trigger for updated_at
CREATE TRIGGER update_specimen_updated_at
    BEFORE UPDATE ON laboratory_schema.specimen
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: order_status_history
-- Description: Track order status changes and notifications
-- =====================================================
CREATE TABLE laboratory_schema.order_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,

    -- Status change
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    status_changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100),
    change_reason TEXT,

    -- Notification
    notification_sent BOOLEAN DEFAULT false,
    notification_sent_at TIMESTAMP,
    notification_recipients TEXT[],

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_order_status_history_order FOREIGN KEY (order_id)
        REFERENCES laboratory_schema.lab_order(id) ON DELETE CASCADE
);

-- Indexes for order_status_history
CREATE INDEX idx_order_status_history_order ON laboratory_schema.order_status_history(order_id);
CREATE INDEX idx_order_status_history_status ON laboratory_schema.order_status_history(new_status);
CREATE INDEX idx_order_status_history_changed_at ON laboratory_schema.order_status_history(status_changed_at);

-- =====================================================
-- Phase 10.3: Result Entry and Validation
-- =====================================================

-- =====================================================
-- Table: lab_result
-- Description: Laboratory test results
-- =====================================================
CREATE TABLE laboratory_schema.lab_result (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    result_number VARCHAR(50) NOT NULL UNIQUE,

    -- Order reference
    order_id UUID NOT NULL,
    order_item_id UUID NOT NULL,
    specimen_id UUID NOT NULL,

    -- Test information
    test_id UUID NOT NULL,
    test_name VARCHAR(200) NOT NULL,
    test_code VARCHAR(50) NOT NULL,

    -- Result status
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, PRELIMINARY, FINAL, AMENDED, CORRECTED, CANCELLED, ENTERED_IN_ERROR

    -- Entry information
    entered_at TIMESTAMP,
    entered_by UUID,
    entry_method VARCHAR(50), -- MANUAL, INTERFACE, IMPORTED

    -- Validation
    validated_at TIMESTAMP,
    validated_by UUID,
    validation_notes TEXT,

    -- Pathologist review (for critical results)
    requires_pathologist_review BOOLEAN DEFAULT false,
    reviewed_by_pathologist BOOLEAN DEFAULT false,
    pathologist_id UUID,
    pathologist_reviewed_at TIMESTAMP,
    pathologist_comments TEXT,

    -- Result interpretation
    overall_interpretation VARCHAR(50), -- NORMAL, ABNORMAL, CRITICAL
    clinical_significance TEXT,
    recommendations TEXT,

    -- Delta check
    delta_check_performed BOOLEAN DEFAULT false,
    delta_check_flagged BOOLEAN DEFAULT false,
    delta_check_notes TEXT,
    previous_result_id UUID,

    -- Panic/critical value
    has_panic_values BOOLEAN DEFAULT false,
    panic_value_notified BOOLEAN DEFAULT false,
    panic_value_notified_at TIMESTAMP,
    panic_value_notified_to VARCHAR(200),

    -- Amendment
    is_amended BOOLEAN DEFAULT false,
    amended_at TIMESTAMP,
    amended_by UUID,
    amendment_reason TEXT,
    original_result_id UUID,

    -- LIS interface
    lis_result_id VARCHAR(100),
    lis_imported_at TIMESTAMP,

    -- QC information
    qc_result_id UUID,
    qc_status VARCHAR(50),

    -- Report
    report_generated BOOLEAN DEFAULT false,
    report_generated_at TIMESTAMP,
    report_sent_to_clinical BOOLEAN DEFAULT false,
    report_sent_at TIMESTAMP,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_result_order FOREIGN KEY (order_id)
        REFERENCES laboratory_schema.lab_order(id),
    CONSTRAINT fk_lab_result_order_item FOREIGN KEY (order_item_id)
        REFERENCES laboratory_schema.lab_order_item(id),
    CONSTRAINT fk_lab_result_specimen FOREIGN KEY (specimen_id)
        REFERENCES laboratory_schema.specimen(id),
    CONSTRAINT fk_lab_result_test FOREIGN KEY (test_id)
        REFERENCES laboratory_schema.lab_test(id),
    CONSTRAINT fk_lab_result_previous FOREIGN KEY (previous_result_id)
        REFERENCES laboratory_schema.lab_result(id),
    CONSTRAINT fk_lab_result_original FOREIGN KEY (original_result_id)
        REFERENCES laboratory_schema.lab_result(id)
);

-- Indexes for lab_result
CREATE UNIQUE INDEX idx_lab_result_number ON laboratory_schema.lab_result(result_number);
CREATE INDEX idx_lab_result_order ON laboratory_schema.lab_result(order_id);
CREATE INDEX idx_lab_result_order_item ON laboratory_schema.lab_result(order_item_id);
CREATE INDEX idx_lab_result_specimen ON laboratory_schema.lab_result(specimen_id);
CREATE INDEX idx_lab_result_test ON laboratory_schema.lab_result(test_id);
CREATE INDEX idx_lab_result_status ON laboratory_schema.lab_result(status);
CREATE INDEX idx_lab_result_entered_at ON laboratory_schema.lab_result(entered_at);
CREATE INDEX idx_lab_result_validated_at ON laboratory_schema.lab_result(validated_at);
CREATE INDEX idx_lab_result_panic ON laboratory_schema.lab_result(has_panic_values) WHERE has_panic_values = true;
CREATE INDEX idx_lab_result_lis ON laboratory_schema.lab_result(lis_result_id) WHERE lis_result_id IS NOT NULL;

-- Trigger for updated_at
CREATE TRIGGER update_lab_result_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_result
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: lab_result_parameter
-- Description: Individual parameter results within a test
-- =====================================================
CREATE TABLE laboratory_schema.lab_result_parameter (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    result_id UUID NOT NULL,
    test_parameter_id UUID NOT NULL,

    -- Parameter details
    parameter_code VARCHAR(50) NOT NULL,
    parameter_name VARCHAR(200) NOT NULL,

    -- Result value
    result_value VARCHAR(1000),
    numeric_value DECIMAL(15, 4),
    text_value TEXT,
    unit VARCHAR(50),

    -- Reference range (copied at time of result for historical accuracy)
    reference_range_low DECIMAL(15, 4),
    reference_range_high DECIMAL(15, 4),
    reference_range_text VARCHAR(500),

    -- Interpretation flags
    interpretation_flag VARCHAR(50), -- NORMAL, LOW, HIGH, CRITICAL_LOW, CRITICAL_HIGH, ABNORMAL
    is_abnormal BOOLEAN DEFAULT false,
    is_critical BOOLEAN DEFAULT false,

    -- Delta check
    delta_check_flagged BOOLEAN DEFAULT false,
    previous_value DECIMAL(15, 4),
    delta_percentage DECIMAL(10, 2),
    delta_absolute DECIMAL(15, 4),

    -- Method and equipment
    test_method VARCHAR(200),
    equipment_id VARCHAR(100),
    equipment_name VARCHAR(200),

    -- QC reference
    qc_level VARCHAR(50),
    qc_within_range BOOLEAN,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_result_parameter_result FOREIGN KEY (result_id)
        REFERENCES laboratory_schema.lab_result(id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_result_parameter_test_param FOREIGN KEY (test_parameter_id)
        REFERENCES laboratory_schema.lab_test_parameter(id)
);

-- Indexes for lab_result_parameter
CREATE INDEX idx_lab_result_parameter_result ON laboratory_schema.lab_result_parameter(result_id);
CREATE INDEX idx_lab_result_parameter_test_param ON laboratory_schema.lab_result_parameter(test_parameter_id);
CREATE INDEX idx_lab_result_parameter_abnormal ON laboratory_schema.lab_result_parameter(is_abnormal) WHERE is_abnormal = true;
CREATE INDEX idx_lab_result_parameter_critical ON laboratory_schema.lab_result_parameter(is_critical) WHERE is_critical = true;
CREATE INDEX idx_lab_result_parameter_delta ON laboratory_schema.lab_result_parameter(delta_check_flagged) WHERE delta_check_flagged = true;

-- Trigger for updated_at
CREATE TRIGGER update_lab_result_parameter_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_result_parameter
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: result_validation
-- Description: Multi-step result validation workflow
-- =====================================================
CREATE TABLE laboratory_schema.result_validation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    result_id UUID NOT NULL,

    -- Validation step
    validation_level VARCHAR(50) NOT NULL, -- TECHNICIAN, SENIOR_TECH, PATHOLOGIST, CLINICAL_REVIEWER
    validation_step INTEGER NOT NULL,

    -- Validator information
    validated_by UUID NOT NULL,
    validator_name VARCHAR(200) NOT NULL,
    validated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Validation decision
    validation_status VARCHAR(50) NOT NULL, -- APPROVED, REJECTED, NEEDS_REVIEW, NEEDS_REPEAT
    validation_notes TEXT,

    -- Issues identified
    issues_identified TEXT[],
    corrective_action TEXT,

    -- Digital signature
    signature_data TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_result_validation_result FOREIGN KEY (result_id)
        REFERENCES laboratory_schema.lab_result(id) ON DELETE CASCADE
);

-- Indexes for result_validation
CREATE INDEX idx_result_validation_result ON laboratory_schema.result_validation(result_id);
CREATE INDEX idx_result_validation_level ON laboratory_schema.result_validation(validation_level);
CREATE INDEX idx_result_validation_status ON laboratory_schema.result_validation(validation_status);
CREATE INDEX idx_result_validation_validated_at ON laboratory_schema.result_validation(validated_at);
CREATE INDEX idx_result_validation_validator ON laboratory_schema.result_validation(validated_by);

-- =====================================================
-- Table: critical_value_alert
-- Description: Track critical value notifications
-- =====================================================
CREATE TABLE laboratory_schema.critical_value_alert (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    result_id UUID NOT NULL,
    result_parameter_id UUID,

    -- Alert details
    alert_type VARCHAR(50) NOT NULL, -- PANIC_VALUE, CRITICAL_VALUE, DELTA_CHECK
    severity VARCHAR(50) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL

    -- Test and parameter
    test_name VARCHAR(200) NOT NULL,
    parameter_name VARCHAR(200),
    result_value VARCHAR(500) NOT NULL,
    critical_threshold VARCHAR(200),

    -- Patient information
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200) NOT NULL,

    -- Notification
    notified_to UUID NOT NULL,
    notified_to_name VARCHAR(200) NOT NULL,
    notified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notification_method VARCHAR(50), -- PHONE, SMS, EMAIL, IN_PERSON, SYSTEM_ALERT

    -- Acknowledgment
    acknowledged BOOLEAN DEFAULT false,
    acknowledged_by UUID,
    acknowledged_at TIMESTAMP,
    acknowledgment_notes TEXT,

    -- Clinical action
    action_taken TEXT,
    action_taken_by UUID,
    action_taken_at TIMESTAMP,

    -- Alert resolution
    resolved BOOLEAN DEFAULT false,
    resolved_at TIMESTAMP,
    resolution_notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_critical_value_alert_result FOREIGN KEY (result_id)
        REFERENCES laboratory_schema.lab_result(id),
    CONSTRAINT fk_critical_value_alert_result_param FOREIGN KEY (result_parameter_id)
        REFERENCES laboratory_schema.lab_result_parameter(id)
);

-- Indexes for critical_value_alert
CREATE INDEX idx_critical_value_alert_result ON laboratory_schema.critical_value_alert(result_id);
CREATE INDEX idx_critical_value_alert_patient ON laboratory_schema.critical_value_alert(patient_id);
CREATE INDEX idx_critical_value_alert_type ON laboratory_schema.critical_value_alert(alert_type);
CREATE INDEX idx_critical_value_alert_severity ON laboratory_schema.critical_value_alert(severity);
CREATE INDEX idx_critical_value_alert_acknowledged ON laboratory_schema.critical_value_alert(acknowledged);
CREATE INDEX idx_critical_value_alert_resolved ON laboratory_schema.critical_value_alert(resolved);
CREATE INDEX idx_critical_value_alert_created_at ON laboratory_schema.critical_value_alert(created_at);

-- =====================================================
-- Phase 10.4: Lab Reporting
-- =====================================================

-- =====================================================
-- Table: lab_report
-- Description: Generated laboratory reports
-- =====================================================
CREATE TABLE laboratory_schema.lab_report (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_number VARCHAR(50) NOT NULL UNIQUE,

    -- Report type
    report_type VARCHAR(50) NOT NULL, -- SINGLE_TEST, CUMULATIVE, TREND_ANALYSIS, QUALITY_CONTROL, UTILIZATION

    -- Order and patient reference
    order_id UUID,
    patient_id UUID NOT NULL,
    encounter_id UUID,

    -- Report content
    report_title VARCHAR(500) NOT NULL,
    report_description TEXT,

    -- Date range (for cumulative/trend reports)
    report_start_date TIMESTAMP,
    report_end_date TIMESTAMP,

    -- Report generation
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    generated_by UUID NOT NULL,

    -- Report format
    format VARCHAR(50) NOT NULL DEFAULT 'PDF', -- PDF, HTML, JSON
    file_path VARCHAR(500),
    file_size_kb INTEGER,

    -- Report status
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT', -- DRAFT, FINAL, REVISED, CANCELLED
    finalized_at TIMESTAMP,

    -- Digital signature
    signed BOOLEAN DEFAULT false,
    signed_by UUID,
    signed_at TIMESTAMP,
    digital_signature TEXT,

    -- Distribution
    printed BOOLEAN DEFAULT false,
    printed_at TIMESTAMP,
    printed_by UUID,

    emailed BOOLEAN DEFAULT false,
    emailed_at TIMESTAMP,
    email_recipients TEXT[],

    accessed_by_clinical BOOLEAN DEFAULT false,
    first_accessed_at TIMESTAMP,

    -- Template
    template_id UUID,
    template_name VARCHAR(200),

    -- Letterhead
    include_letterhead BOOLEAN DEFAULT true,
    letterhead_logo_path VARCHAR(500),

    -- Additional content
    clinical_interpretation TEXT,
    recommendations TEXT,
    disclaimers TEXT,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_report_order FOREIGN KEY (order_id)
        REFERENCES laboratory_schema.lab_order(id),
    CONSTRAINT fk_lab_report_patient FOREIGN KEY (patient_id)
        REFERENCES patient_schema.patient(id),
    CONSTRAINT fk_lab_report_encounter FOREIGN KEY (encounter_id)
        REFERENCES clinical_schema.encounter(id)
);

-- Indexes for lab_report
CREATE UNIQUE INDEX idx_lab_report_number ON laboratory_schema.lab_report(report_number);
CREATE INDEX idx_lab_report_type ON laboratory_schema.lab_report(report_type);
CREATE INDEX idx_lab_report_order ON laboratory_schema.lab_report(order_id);
CREATE INDEX idx_lab_report_patient ON laboratory_schema.lab_report(patient_id);
CREATE INDEX idx_lab_report_encounter ON laboratory_schema.lab_report(encounter_id);
CREATE INDEX idx_lab_report_status ON laboratory_schema.lab_report(status);
CREATE INDEX idx_lab_report_generated_at ON laboratory_schema.lab_report(generated_at);

-- Trigger for updated_at
CREATE TRIGGER update_lab_report_updated_at
    BEFORE UPDATE ON laboratory_schema.lab_report
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: lab_report_result
-- Description: Results included in a report
-- =====================================================
CREATE TABLE laboratory_schema.lab_report_result (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id UUID NOT NULL,
    result_id UUID NOT NULL,

    -- Display configuration
    display_order INTEGER,
    include_parameters BOOLEAN DEFAULT true,
    include_interpretation BOOLEAN DEFAULT true,
    include_reference_ranges BOOLEAN DEFAULT true,

    -- Grouping
    section_name VARCHAR(200),
    section_order INTEGER,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_lab_report_result_report FOREIGN KEY (report_id)
        REFERENCES laboratory_schema.lab_report(id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_report_result_result FOREIGN KEY (result_id)
        REFERENCES laboratory_schema.lab_result(id)
);

-- Indexes for lab_report_result
CREATE INDEX idx_lab_report_result_report ON laboratory_schema.lab_report_result(report_id);
CREATE INDEX idx_lab_report_result_result ON laboratory_schema.lab_report_result(result_id);

-- =====================================================
-- Table: tat_monitoring
-- Description: Turnaround Time (TAT) monitoring and statistics
-- =====================================================
CREATE TABLE laboratory_schema.tat_monitoring (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    test_id UUID NOT NULL,

    -- Test information
    test_name VARCHAR(200) NOT NULL,
    test_category VARCHAR(100),
    priority VARCHAR(50) NOT NULL,

    -- Time stamps
    order_placed_at TIMESTAMP NOT NULL,
    sample_collected_at TIMESTAMP,
    sample_received_at TIMESTAMP,
    processing_started_at TIMESTAMP,
    result_entered_at TIMESTAMP,
    result_validated_at TIMESTAMP,
    result_reported_at TIMESTAMP,

    -- TAT calculations (in minutes)
    collection_tat INTEGER, -- order to collection
    reception_tat INTEGER, -- collection to reception
    processing_tat INTEGER, -- reception to processing start
    result_entry_tat INTEGER, -- processing start to result entry
    validation_tat INTEGER, -- result entry to validation
    reporting_tat INTEGER, -- validation to reporting
    total_tat INTEGER, -- order to reporting

    -- Expected vs actual
    expected_tat_minutes INTEGER,
    tat_met BOOLEAN,
    tat_variance_minutes INTEGER,

    -- Delay analysis
    delayed BOOLEAN DEFAULT false,
    delay_reason VARCHAR(500),
    delay_category VARCHAR(100), -- SPECIMEN_COLLECTION, TRANSPORTATION, EQUIPMENT, REAGENT, STAFFING, OTHER

    -- Quality indicator
    critical_tat BOOLEAN DEFAULT false,
    urgent_tat BOOLEAN DEFAULT false,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_tat_monitoring_order FOREIGN KEY (order_id)
        REFERENCES laboratory_schema.lab_order(id),
    CONSTRAINT fk_tat_monitoring_test FOREIGN KEY (test_id)
        REFERENCES laboratory_schema.lab_test(id)
);

-- Indexes for tat_monitoring
CREATE INDEX idx_tat_monitoring_order ON laboratory_schema.tat_monitoring(order_id);
CREATE INDEX idx_tat_monitoring_test ON laboratory_schema.tat_monitoring(test_id);
CREATE INDEX idx_tat_monitoring_priority ON laboratory_schema.tat_monitoring(priority);
CREATE INDEX idx_tat_monitoring_delayed ON laboratory_schema.tat_monitoring(delayed) WHERE delayed = true;
CREATE INDEX idx_tat_monitoring_tat_met ON laboratory_schema.tat_monitoring(tat_met);
CREATE INDEX idx_tat_monitoring_order_placed ON laboratory_schema.tat_monitoring(order_placed_at);

-- =====================================================
-- Table: test_utilization
-- Description: Track test utilization for statistics and reporting
-- =====================================================
CREATE TABLE laboratory_schema.test_utilization (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Period
    period_type VARCHAR(50) NOT NULL, -- DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,

    -- Test information
    test_id UUID NOT NULL,
    test_name VARCHAR(200) NOT NULL,
    test_category VARCHAR(100),

    -- Utilization metrics
    total_orders INTEGER NOT NULL DEFAULT 0,
    completed_tests INTEGER NOT NULL DEFAULT 0,
    cancelled_tests INTEGER NOT NULL DEFAULT 0,

    -- By priority
    routine_orders INTEGER NOT NULL DEFAULT 0,
    urgent_orders INTEGER NOT NULL DEFAULT 0,
    cito_orders INTEGER NOT NULL DEFAULT 0,

    -- By patient type
    inpatient_orders INTEGER NOT NULL DEFAULT 0,
    outpatient_orders INTEGER NOT NULL DEFAULT 0,
    emergency_orders INTEGER NOT NULL DEFAULT 0,

    -- Financial
    total_revenue DECIMAL(15, 2) DEFAULT 0,

    -- Quality metrics
    rejected_specimens INTEGER NOT NULL DEFAULT 0,
    repeat_tests INTEGER NOT NULL DEFAULT 0,
    critical_values_reported INTEGER NOT NULL DEFAULT 0,

    -- TAT metrics
    average_tat_minutes DECIMAL(10, 2),
    median_tat_minutes DECIMAL(10, 2),
    tat_compliance_percentage DECIMAL(5, 2),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_test_utilization_test FOREIGN KEY (test_id)
        REFERENCES laboratory_schema.lab_test(id),

    -- Constraints
    CONSTRAINT uq_test_utilization_period_test UNIQUE (period_type, period_start, period_end, test_id)
);

-- Indexes for test_utilization
CREATE INDEX idx_test_utilization_test ON laboratory_schema.test_utilization(test_id);
CREATE INDEX idx_test_utilization_period ON laboratory_schema.test_utilization(period_type, period_start, period_end);
CREATE INDEX idx_test_utilization_category ON laboratory_schema.test_utilization(test_category);

-- Trigger for updated_at
CREATE TRIGGER update_test_utilization_updated_at
    BEFORE UPDATE ON laboratory_schema.test_utilization
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Initial Data: Lab Test Categories
-- =====================================================
INSERT INTO laboratory_schema.lab_test_category (code, name, description, level, display_order) VALUES
('HEMATOLOGY', 'Hematology', 'Blood cell counts, coagulation studies, blood typing', 0, 1),
('CHEMISTRY', 'Clinical Chemistry', 'Blood chemistry, enzymes, metabolites', 0, 2),
('IMMUNOLOGY', 'Immunology & Serology', 'Antibodies, antigens, immunoglobulins', 0, 3),
('MICROBIOLOGY', 'Microbiology', 'Culture, sensitivity testing, microscopy', 0, 4),
('MOLECULAR', 'Molecular Diagnostics', 'PCR, genetic testing, molecular biology', 0, 5),
('PATHOLOGY', 'Anatomical Pathology', 'Histopathology, cytology, tissue examination', 0, 6),
('URINALYSIS', 'Urinalysis', 'Urine analysis, urine chemistry', 0, 7),
('BLOOD_BANK', 'Blood Bank', 'Blood typing, cross-matching, antibody screening', 0, 8),
('TOXICOLOGY', 'Toxicology', 'Drug screening, therapeutic drug monitoring', 0, 9),
('ENDOCRINE', 'Endocrinology', 'Hormone assays, thyroid function tests', 0, 10);

-- =====================================================
-- Comments on Tables
-- =====================================================
COMMENT ON SCHEMA laboratory_schema IS 'Laboratory Module Schema - Phase 10';

COMMENT ON TABLE laboratory_schema.lab_test_category IS 'Laboratory test categories (Hematology, Chemistry, etc.)';
COMMENT ON TABLE laboratory_schema.lab_test IS 'Laboratory test catalog with LOINC coding';
COMMENT ON TABLE laboratory_schema.lab_test_parameter IS 'Test parameters with age/gender-specific normal ranges';
COMMENT ON TABLE laboratory_schema.lab_panel IS 'Test panels/packages for bundled tests';
COMMENT ON TABLE laboratory_schema.lab_panel_item IS 'Tests included in panels';

COMMENT ON TABLE laboratory_schema.lab_order IS 'Laboratory orders from clinical modules';
COMMENT ON TABLE laboratory_schema.lab_order_item IS 'Individual tests in orders';
COMMENT ON TABLE laboratory_schema.specimen IS 'Specimen collection and tracking with barcode';
COMMENT ON TABLE laboratory_schema.order_status_history IS 'Order status change tracking';

COMMENT ON TABLE laboratory_schema.lab_result IS 'Laboratory test results';
COMMENT ON TABLE laboratory_schema.lab_result_parameter IS 'Individual parameter results with delta checks';
COMMENT ON TABLE laboratory_schema.result_validation IS 'Multi-step result validation workflow';
COMMENT ON TABLE laboratory_schema.critical_value_alert IS 'Critical value and panic value alerts';

COMMENT ON TABLE laboratory_schema.lab_report IS 'Generated laboratory reports with PDF';
COMMENT ON TABLE laboratory_schema.lab_report_result IS 'Results included in reports';
COMMENT ON TABLE laboratory_schema.tat_monitoring IS 'Turnaround Time monitoring and analysis';
COMMENT ON TABLE laboratory_schema.test_utilization IS 'Test utilization statistics';