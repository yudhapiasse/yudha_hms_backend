--
-- Phase 11.3: PACS Integration
-- Database Schema for PACS/DICOM Integration
--
-- Author: HMS Development Team
-- Created: 2025-11-22
-- Version: 1.0.0
--

-- ========================================
-- DICOM Worklist Table
-- ========================================
CREATE TABLE radiology_schema.dicom_worklist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Order reference
    order_id UUID NOT NULL,
    order_item_id UUID NOT NULL,

    -- Patient demographics (for DICOM MWL)
    patient_id VARCHAR(50) NOT NULL,
    patient_name VARCHAR(200) NOT NULL,
    patient_birth_date DATE,
    patient_sex VARCHAR(1),

    -- Study information
    accession_number VARCHAR(50) NOT NULL UNIQUE,
    study_instance_uid VARCHAR(128),
    requested_procedure_id VARCHAR(50),
    scheduled_procedure_step_id VARCHAR(50),

    -- Modality information
    modality_code VARCHAR(10) NOT NULL,
    scheduled_station_ae_title VARCHAR(50),
    scheduled_station_name VARCHAR(100),

    -- Scheduling
    scheduled_procedure_step_start_date DATE NOT NULL,
    scheduled_procedure_step_start_time TIME NOT NULL,
    scheduled_procedure_step_end_date DATE,
    scheduled_procedure_step_end_time TIME,

    -- Procedure details
    requested_procedure_description TEXT,
    scheduled_procedure_step_description TEXT,
    scheduled_procedure_step_status VARCHAR(20) DEFAULT 'SCHEDULED',

    -- Referring physician
    referring_physician_name VARCHAR(200),
    referring_physician_id VARCHAR(50),

    -- Performing physician
    scheduled_performing_physician_name VARCHAR(200),

    -- Study details
    study_description TEXT,
    body_part_examined VARCHAR(100),
    laterality VARCHAR(10),

    -- Worklist status
    worklist_status VARCHAR(20) DEFAULT 'PENDING',
    sent_to_modality BOOLEAN DEFAULT false,
    sent_to_modality_at TIMESTAMP,
    acknowledged_by_modality BOOLEAN DEFAULT false,
    acknowledged_at TIMESTAMP,

    -- Completion tracking
    procedure_started_at TIMESTAMP,
    procedure_completed_at TIMESTAMP,

    -- Actual procedure step tracking
    actual_procedure_step_start_date DATE,
    actual_procedure_step_start_time TIME,
    actual_procedure_step_end_date DATE,
    actual_procedure_step_end_time TIME,

    -- Cancellation
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP,

    -- Additional information
    notes TEXT,
    custom_tags JSONB,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_dicom_worklist_order FOREIGN KEY (order_id)
        REFERENCES radiology_schema.radiology_order(id)
);

-- ========================================
-- PACS Study Table
-- ========================================
CREATE TABLE radiology_schema.pacs_study (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Order reference
    order_id UUID NOT NULL,
    worklist_id UUID,

    -- DICOM identifiers
    study_instance_uid VARCHAR(128) NOT NULL UNIQUE,
    accession_number VARCHAR(50) NOT NULL,
    study_id VARCHAR(50),

    -- Patient information
    patient_id VARCHAR(50) NOT NULL,
    patient_name VARCHAR(200),
    patient_birth_date DATE,
    patient_sex VARCHAR(1),
    patient_age VARCHAR(10),

    -- Study details
    study_date DATE NOT NULL,
    study_time TIME,
    study_description TEXT,
    modality_code VARCHAR(10) NOT NULL,
    body_part_examined VARCHAR(100),

    -- Study metadata
    number_of_series INTEGER DEFAULT 0,
    number_of_instances INTEGER DEFAULT 0,
    study_size_mb DECIMAL(12,2),

    -- Referring physician
    referring_physician_name VARCHAR(200),
    referring_physician_id VARCHAR(50),

    -- Performing physician
    performing_physician_name VARCHAR(200),

    -- Study status
    study_status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    acquisition_complete BOOLEAN DEFAULT false,
    acquisition_completed_at TIMESTAMP,

    -- Quality assurance
    qa_status VARCHAR(20),
    qa_performed_by UUID,
    qa_performed_at TIMESTAMP,
    qa_notes TEXT,

    -- PACS storage
    pacs_location VARCHAR(500),
    archived BOOLEAN DEFAULT false,
    archived_at TIMESTAMP,
    archival_rule_id UUID,

    -- Cloud storage
    cloud_pacs_synced BOOLEAN DEFAULT false,
    cloud_pacs_synced_at TIMESTAMP,
    cloud_pacs_url VARCHAR(500),

    -- Access and sharing
    viewable_externally BOOLEAN DEFAULT false,
    share_expires_at TIMESTAMP,

    -- Additional metadata
    institution_name VARCHAR(200),
    station_name VARCHAR(100),
    manufacturer VARCHAR(100),
    manufacturer_model VARCHAR(100),

    custom_tags JSONB,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_pacs_study_order FOREIGN KEY (order_id)
        REFERENCES radiology_schema.radiology_order(id),
    CONSTRAINT fk_pacs_study_worklist FOREIGN KEY (worklist_id)
        REFERENCES radiology_schema.dicom_worklist(id)
);

-- ========================================
-- PACS Series Table
-- ========================================
CREATE TABLE radiology_schema.pacs_series (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Study reference
    study_id UUID NOT NULL,

    -- DICOM identifiers
    series_instance_uid VARCHAR(128) NOT NULL UNIQUE,
    series_number INTEGER,
    series_description TEXT,

    -- Series details
    modality VARCHAR(10) NOT NULL,
    body_part_examined VARCHAR(100),
    laterality VARCHAR(10),
    patient_position VARCHAR(20),

    -- Series metadata
    number_of_instances INTEGER DEFAULT 0,
    series_size_mb DECIMAL(12,2),

    -- Acquisition details
    series_date DATE,
    series_time TIME,
    protocol_name VARCHAR(200),
    contrast_bolus_agent VARCHAR(100),

    -- Performing physician
    performing_physician_name VARCHAR(200),
    operators_name VARCHAR(200),

    -- Equipment
    station_name VARCHAR(100),
    manufacturer VARCHAR(100),
    manufacturer_model VARCHAR(100),

    -- Image characteristics
    slice_thickness_mm DECIMAL(8,2),
    spacing_between_slices_mm DECIMAL(8,2),
    pixel_spacing VARCHAR(50),
    rows INTEGER,
    columns INTEGER,

    -- Storage
    pacs_location VARCHAR(500),

    custom_tags JSONB,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,

    CONSTRAINT fk_pacs_series_study FOREIGN KEY (study_id)
        REFERENCES radiology_schema.pacs_study(id) ON DELETE CASCADE
);

-- ========================================
-- DICOM Tag Mapping Table
-- ========================================
CREATE TABLE radiology_schema.dicom_tag_mapping (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Mapping details
    tag_group VARCHAR(4) NOT NULL,
    tag_element VARCHAR(4) NOT NULL,
    tag_name VARCHAR(100) NOT NULL,
    tag_vr VARCHAR(4),

    -- Mapping configuration
    source_field VARCHAR(200),
    source_table VARCHAR(100),
    mapping_expression TEXT,
    default_value TEXT,

    -- Applicability
    modality_code VARCHAR(10),
    examination_code VARCHAR(50),
    applies_to_all BOOLEAN DEFAULT true,

    -- Status
    is_active BOOLEAN DEFAULT true,
    priority INTEGER DEFAULT 0,

    -- Description
    description TEXT,
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    CONSTRAINT uk_dicom_tag UNIQUE (tag_group, tag_element, modality_code)
);

-- ========================================
-- Study Archival Rules Table
-- ========================================
CREATE TABLE radiology_schema.study_archival_rule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Rule details
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    rule_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,

    -- Rule criteria
    modality_codes VARCHAR(200),
    examination_types VARCHAR(500),
    body_parts VARCHAR(500),

    -- Retention policy
    retention_days INTEGER NOT NULL,
    auto_archive BOOLEAN DEFAULT true,
    archive_to_nearline BOOLEAN DEFAULT false,
    archive_to_cloud BOOLEAN DEFAULT false,

    -- Priority and importance
    priority_override VARCHAR(20),
    legal_hold BOOLEAN DEFAULT false,
    research_value BOOLEAN DEFAULT false,

    -- Compression
    compress_after_days INTEGER,
    compression_ratio INTEGER,
    lossy_compression_allowed BOOLEAN DEFAULT false,

    -- Deletion policy
    auto_delete_after_days INTEGER,
    require_approval_for_deletion BOOLEAN DEFAULT true,

    -- Status
    is_active BOOLEAN DEFAULT true,
    effective_from DATE,
    effective_to DATE,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

-- ========================================
-- Image Share Link Table
-- ========================================
CREATE TABLE radiology_schema.image_share_link (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Study reference
    study_id UUID NOT NULL,
    order_id UUID NOT NULL,

    -- Share link details
    share_token VARCHAR(100) NOT NULL UNIQUE,
    share_url VARCHAR(500),

    -- Access control
    password_protected BOOLEAN DEFAULT false,
    password_hash VARCHAR(255),

    -- Expiration
    expires_at TIMESTAMP NOT NULL,
    max_views INTEGER,
    current_views INTEGER DEFAULT 0,

    -- Allowed features
    allow_download BOOLEAN DEFAULT false,
    allow_print BOOLEAN DEFAULT false,
    allow_share BOOLEAN DEFAULT false,

    -- Recipient information
    recipient_email VARCHAR(200),
    recipient_name VARCHAR(200),
    recipient_organization VARCHAR(200),

    -- Purpose
    share_purpose VARCHAR(50),
    notes TEXT,

    -- Status
    is_active BOOLEAN DEFAULT true,
    revoked BOOLEAN DEFAULT false,
    revoked_at TIMESTAMP,
    revoked_by UUID,
    revoke_reason TEXT,

    -- Access tracking
    first_accessed_at TIMESTAMP,
    last_accessed_at TIMESTAMP,
    access_log JSONB,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_share_link_study FOREIGN KEY (study_id)
        REFERENCES radiology_schema.pacs_study(id) ON DELETE CASCADE,
    CONSTRAINT fk_share_link_order FOREIGN KEY (order_id)
        REFERENCES radiology_schema.radiology_order(id)
);

-- ========================================
-- CD Burning Request Table
-- ========================================
CREATE TABLE radiology_schema.cd_burning_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Request details
    request_number VARCHAR(50) NOT NULL UNIQUE,
    request_type VARCHAR(20) DEFAULT 'PATIENT_CD',

    -- Studies to burn
    study_ids UUID[] NOT NULL,
    order_ids UUID[],

    -- Patient information
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200),

    -- CD details
    cd_label VARCHAR(200),
    include_viewer BOOLEAN DEFAULT true,
    viewer_type VARCHAR(50),
    include_reports BOOLEAN DEFAULT true,

    -- Format
    output_format VARCHAR(20) DEFAULT 'DICOM',
    anonymize BOOLEAN DEFAULT false,
    compress BOOLEAN DEFAULT false,

    -- Request information
    requested_by UUID NOT NULL,
    requested_for VARCHAR(200),
    request_reason TEXT,

    -- Status tracking
    status VARCHAR(20) DEFAULT 'PENDING',
    priority VARCHAR(20) DEFAULT 'NORMAL',

    -- Processing
    assigned_to UUID,
    processing_started_at TIMESTAMP,
    processing_completed_at TIMESTAMP,

    -- Completion
    cd_count INTEGER DEFAULT 1,
    burned_at TIMESTAMP,
    burned_by UUID,

    -- Delivery
    delivery_method VARCHAR(50),
    delivered_to VARCHAR(200),
    delivered_at TIMESTAMP,
    delivery_notes TEXT,

    -- Error handling
    failed BOOLEAN DEFAULT false,
    failure_reason TEXT,
    retry_count INTEGER DEFAULT 0,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

-- ========================================
-- PACS Configuration Table
-- ========================================
CREATE TABLE radiology_schema.pacs_configuration (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Configuration name
    config_name VARCHAR(100) NOT NULL UNIQUE,
    config_type VARCHAR(50) NOT NULL,

    -- PACS server details
    pacs_ae_title VARCHAR(50),
    pacs_host VARCHAR(200),
    pacs_port INTEGER,
    pacs_protocol VARCHAR(20) DEFAULT 'DICOM',

    -- Local application entity
    local_ae_title VARCHAR(50),
    local_port INTEGER,

    -- Connection settings
    connection_timeout_seconds INTEGER DEFAULT 30,
    max_pdu_length INTEGER DEFAULT 16384,

    -- Cloud PACS
    cloud_pacs_enabled BOOLEAN DEFAULT false,
    cloud_pacs_provider VARCHAR(100),
    cloud_pacs_endpoint VARCHAR(500),
    cloud_pacs_api_key VARCHAR(255),
    cloud_pacs_bucket VARCHAR(200),

    -- Storage settings
    storage_location VARCHAR(500),
    max_storage_gb INTEGER,
    compression_enabled BOOLEAN DEFAULT false,

    -- Worklist settings
    worklist_enabled BOOLEAN DEFAULT true,
    auto_send_worklist BOOLEAN DEFAULT true,
    worklist_retention_days INTEGER DEFAULT 30,

    -- Study routing
    auto_route_studies BOOLEAN DEFAULT false,
    route_to_ae_titles VARCHAR(500),

    -- Archival
    auto_archival_enabled BOOLEAN DEFAULT false,
    default_archival_rule_id UUID,

    -- Security
    use_tls BOOLEAN DEFAULT false,
    require_authentication BOOLEAN DEFAULT false,

    -- Status
    is_active BOOLEAN DEFAULT true,
    is_default BOOLEAN DEFAULT false,

    -- Additional settings
    custom_settings JSONB,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

-- ========================================
-- Indexes
-- ========================================

-- DICOM Worklist indexes
CREATE INDEX idx_dicom_worklist_order ON radiology_schema.dicom_worklist(order_id);
CREATE INDEX idx_dicom_worklist_order_item ON radiology_schema.dicom_worklist(order_item_id);
CREATE INDEX idx_dicom_worklist_accession ON radiology_schema.dicom_worklist(accession_number);
CREATE INDEX idx_dicom_worklist_status ON radiology_schema.dicom_worklist(worklist_status);
CREATE INDEX idx_dicom_worklist_scheduled_date ON radiology_schema.dicom_worklist(scheduled_procedure_step_start_date);
CREATE INDEX idx_dicom_worklist_modality ON radiology_schema.dicom_worklist(modality_code);

-- PACS Study indexes
CREATE INDEX idx_pacs_study_order ON radiology_schema.pacs_study(order_id);
CREATE INDEX idx_pacs_study_uid ON radiology_schema.pacs_study(study_instance_uid);
CREATE INDEX idx_pacs_study_accession ON radiology_schema.pacs_study(accession_number);
CREATE INDEX idx_pacs_study_patient ON radiology_schema.pacs_study(patient_id);
CREATE INDEX idx_pacs_study_date ON radiology_schema.pacs_study(study_date);
CREATE INDEX idx_pacs_study_status ON radiology_schema.pacs_study(study_status);
CREATE INDEX idx_pacs_study_archived ON radiology_schema.pacs_study(archived);

-- PACS Series indexes
CREATE INDEX idx_pacs_series_study ON radiology_schema.pacs_series(study_id);
CREATE INDEX idx_pacs_series_uid ON radiology_schema.pacs_series(series_instance_uid);
CREATE INDEX idx_pacs_series_modality ON radiology_schema.pacs_series(modality);

-- DICOM Tag Mapping indexes
CREATE INDEX idx_dicom_tag_mapping_tag ON radiology_schema.dicom_tag_mapping(tag_group, tag_element);
CREATE INDEX idx_dicom_tag_mapping_modality ON radiology_schema.dicom_tag_mapping(modality_code);
CREATE INDEX idx_dicom_tag_mapping_active ON radiology_schema.dicom_tag_mapping(is_active);

-- Study Archival Rule indexes
CREATE INDEX idx_archival_rule_code ON radiology_schema.study_archival_rule(rule_code);
CREATE INDEX idx_archival_rule_active ON radiology_schema.study_archival_rule(is_active);

-- Image Share Link indexes
CREATE INDEX idx_share_link_study ON radiology_schema.image_share_link(study_id);
CREATE INDEX idx_share_link_token ON radiology_schema.image_share_link(share_token);
CREATE INDEX idx_share_link_expires ON radiology_schema.image_share_link(expires_at);
CREATE INDEX idx_share_link_active ON radiology_schema.image_share_link(is_active);

-- CD Burning Request indexes
CREATE INDEX idx_cd_burning_request_number ON radiology_schema.cd_burning_request(request_number);
CREATE INDEX idx_cd_burning_patient ON radiology_schema.cd_burning_request(patient_id);
CREATE INDEX idx_cd_burning_status ON radiology_schema.cd_burning_request(status);
CREATE INDEX idx_cd_burning_requested_by ON radiology_schema.cd_burning_request(requested_by);

-- PACS Configuration indexes
CREATE INDEX idx_pacs_config_name ON radiology_schema.pacs_configuration(config_name);
CREATE INDEX idx_pacs_config_type ON radiology_schema.pacs_configuration(config_type);
CREATE INDEX idx_pacs_config_active ON radiology_schema.pacs_configuration(is_active);

-- ========================================
-- Comments
-- ========================================
COMMENT ON TABLE radiology_schema.dicom_worklist IS 'DICOM Modality Worklist entries for exam scheduling';
COMMENT ON TABLE radiology_schema.pacs_study IS 'Radiology studies stored in PACS';
COMMENT ON TABLE radiology_schema.pacs_series IS 'Image series within PACS studies';
COMMENT ON TABLE radiology_schema.dicom_tag_mapping IS 'Custom DICOM tag mapping configuration';
COMMENT ON TABLE radiology_schema.study_archival_rule IS 'Study retention and archival policies';
COMMENT ON TABLE radiology_schema.image_share_link IS 'Shareable links for external image access';
COMMENT ON TABLE radiology_schema.cd_burning_request IS 'CD/DVD burning requests for patient images';
COMMENT ON TABLE radiology_schema.pacs_configuration IS 'PACS and Cloud PACS configuration settings';
