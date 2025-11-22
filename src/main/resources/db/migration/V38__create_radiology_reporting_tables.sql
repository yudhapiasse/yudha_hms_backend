-- ========================================
-- Phase 11.4: Radiology Reporting Module
-- Version: V38
-- Description: Create tables for radiology reporting system with structured templates,
--              report verification workflow, critical findings communication, amendments,
--              previous study comparison, and report distribution
-- ========================================

-- ========================================
-- Report Template Table
-- ========================================
CREATE TABLE radiology_schema.report_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Template identification
    template_code VARCHAR(50) NOT NULL UNIQUE,
    template_name VARCHAR(200) NOT NULL,
    template_type VARCHAR(50) NOT NULL, -- STRUCTURED, SEMI_STRUCTURED, FREE_TEXT

    -- Template details
    modality_code VARCHAR(10) NOT NULL,
    body_part VARCHAR(100),
    procedure_type VARCHAR(100),

    -- Template content
    template_structure JSONB NOT NULL, -- JSON structure with sections and fields
    default_sections JSONB, -- Default section definitions
    required_fields TEXT[], -- Array of required field names

    -- Macros and snippets
    macros JSONB, -- Predefined text macros
    common_findings JSONB, -- Common findings library

    -- Usage statistics
    usage_count INTEGER DEFAULT 0,
    last_used_at TIMESTAMP,

    -- Status
    is_active BOOLEAN DEFAULT true,
    version_number INTEGER DEFAULT 1,
    parent_template_id UUID, -- For template versioning

    -- Metadata
    created_by_radiologist UUID,
    approved_by UUID,
    approved_at TIMESTAMP,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_template_parent FOREIGN KEY (parent_template_id)
        REFERENCES radiology_schema.report_template(id)
);

-- ========================================
-- Radiology Report Table
-- ========================================
CREATE TABLE radiology_schema.radiology_report (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Report identification
    report_number VARCHAR(50) NOT NULL UNIQUE,
    accession_number VARCHAR(50) NOT NULL,

    -- Order and study references
    order_id UUID NOT NULL,
    study_id UUID, -- PACS study reference

    -- Patient and encounter
    patient_id UUID NOT NULL,
    encounter_id UUID,

    -- Template reference
    template_id UUID,

    -- Report metadata
    examination_date DATE NOT NULL,
    examination_time TIME,
    modality_code VARCHAR(10) NOT NULL,
    body_part VARCHAR(100),
    procedure_name VARCHAR(200),

    -- Clinical information
    clinical_indication TEXT,
    clinical_history TEXT,
    relevant_previous_imaging TEXT,

    -- Report content
    technique TEXT, -- Examination technique
    findings JSONB, -- Structured findings JSON
    findings_text TEXT, -- Free text findings
    impression TEXT, -- Report impression/conclusion
    recommendations TEXT, -- Recommendations

    -- Comparison
    comparison_text TEXT,
    compared_to_study_id UUID, -- Reference to previous study
    comparison_summary TEXT,

    -- Critical findings
    has_critical_findings BOOLEAN DEFAULT false,
    critical_findings_text TEXT,
    critical_findings_communicated BOOLEAN DEFAULT false,
    critical_findings_communicated_at TIMESTAMP,
    critical_findings_communicated_to VARCHAR(200),

    -- Reporting radiologist
    reported_by UUID NOT NULL,
    reported_at TIMESTAMP,

    -- Verification workflow
    report_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT', -- DRAFT, PRELIMINARY, VERIFIED, AMENDED, CANCELLED
    verified_by UUID,
    verified_at TIMESTAMP,
    verification_notes TEXT,

    -- Transcription
    transcription_id UUID,
    transcribed_from_audio BOOLEAN DEFAULT false,

    -- Quality metrics
    report_complexity VARCHAR(20), -- SIMPLE, MODERATE, COMPLEX
    time_to_report_minutes INTEGER,

    -- Distribution
    auto_distributed BOOLEAN DEFAULT false,
    distributed_at TIMESTAMP,

    -- Billing
    billing_code VARCHAR(20),
    billing_status VARCHAR(20),

    -- Additional data
    custom_fields JSONB,
    attachments JSONB, -- References to attached files

    -- Cancellation
    cancelled BOOLEAN DEFAULT false,
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP,
    cancelled_by UUID,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_report_order FOREIGN KEY (order_id)
        REFERENCES radiology_schema.radiology_order(id),
    CONSTRAINT fk_report_template FOREIGN KEY (template_id)
        REFERENCES radiology_schema.report_template(id),
    CONSTRAINT fk_report_comparison FOREIGN KEY (compared_to_study_id)
        REFERENCES radiology_schema.pacs_study(id)
);

-- ========================================
-- Report Amendment Table
-- ========================================
CREATE TABLE radiology_schema.report_amendment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Report reference
    report_id UUID NOT NULL,
    amendment_number INTEGER NOT NULL,

    -- Amendment details
    amendment_reason VARCHAR(50) NOT NULL, -- ERROR_CORRECTION, ADDITIONAL_FINDINGS, CLARIFICATION, TECHNICAL_ISSUE
    amendment_type VARCHAR(30) NOT NULL, -- ADDENDUM, CORRECTION, SUPPLEMENTAL

    -- Original and amended content
    original_findings TEXT,
    original_impression TEXT,
    amended_findings TEXT,
    amended_impression TEXT,
    amendment_notes TEXT NOT NULL,

    -- Amendment workflow
    amended_by UUID NOT NULL,
    amended_at TIMESTAMP NOT NULL,
    verified_by UUID,
    verified_at TIMESTAMP,

    -- Communication
    referring_physician_notified BOOLEAN DEFAULT false,
    notified_at TIMESTAMP,
    notification_method VARCHAR(30),

    -- Significance
    is_significant BOOLEAN DEFAULT false,
    significance_notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_amendment_report FOREIGN KEY (report_id)
        REFERENCES radiology_schema.radiology_report(id) ON DELETE CASCADE,
    CONSTRAINT uk_amendment_number UNIQUE (report_id, amendment_number)
);

-- ========================================
-- Critical Finding Notification Table
-- ========================================
CREATE TABLE radiology_schema.critical_finding_notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Report reference
    report_id UUID NOT NULL,

    -- Finding details
    finding_description TEXT NOT NULL,
    finding_severity VARCHAR(20) NOT NULL, -- URGENT, HIGH, MODERATE
    finding_category VARCHAR(50), -- ACUTE_STROKE, PE, PNEUMOTHORAX, etc.

    -- Notification urgency
    priority VARCHAR(20) NOT NULL DEFAULT 'HIGH', -- STAT, HIGH, ROUTINE
    requires_immediate_action BOOLEAN DEFAULT true,
    recommended_action TEXT,

    -- Communication tracking
    notified_to VARCHAR(200) NOT NULL, -- Name of person notified
    notified_to_role VARCHAR(50), -- REFERRING_PHYSICIAN, ATTENDING, RESIDENT, NURSE
    notified_to_contact VARCHAR(100),

    notification_method VARCHAR(30) NOT NULL, -- PHONE, SMS, EMAIL, IN_PERSON, PAGING_SYSTEM
    notification_attempts INTEGER DEFAULT 1,

    -- Acknowledgment
    acknowledged BOOLEAN DEFAULT false,
    acknowledged_by VARCHAR(200),
    acknowledged_at TIMESTAMP,
    acknowledgment_method VARCHAR(30),

    -- Follow-up
    follow_up_required BOOLEAN DEFAULT false,
    follow_up_instructions TEXT,
    follow_up_completed BOOLEAN DEFAULT false,
    follow_up_completed_at TIMESTAMP,

    -- Documentation
    communication_notes TEXT,
    read_back_verified BOOLEAN DEFAULT false,

    -- Compliance tracking
    time_to_notification_minutes INTEGER,
    notification_within_policy BOOLEAN,

    -- Radiologist
    notified_by UUID NOT NULL,
    notified_at TIMESTAMP NOT NULL,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_critical_report FOREIGN KEY (report_id)
        REFERENCES radiology_schema.radiology_report(id) ON DELETE CASCADE
);

-- ========================================
-- Report Comparison Table
-- ========================================
CREATE TABLE radiology_schema.report_comparison (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Current and prior reports
    current_report_id UUID NOT NULL,
    prior_report_id UUID NOT NULL,
    prior_study_id UUID,

    -- Comparison metadata
    comparison_date DATE NOT NULL,
    time_interval_days INTEGER,

    -- Comparison categories
    comparison_category VARCHAR(50), -- FOLLOW_UP, BASELINE, SURVEILLANCE, CLINICAL_CHANGE
    comparison_type VARCHAR(30), -- ROUTINE, TARGETED, COMPREHENSIVE

    -- Findings comparison
    new_findings JSONB,
    resolved_findings JSONB,
    stable_findings JSONB,
    progressed_findings JSONB,

    -- Overall assessment
    overall_change VARCHAR(30), -- IMPROVED, STABLE, PROGRESSED, MIXED
    change_summary TEXT,
    clinical_significance TEXT,

    -- Key changes
    significant_changes TEXT[],
    measurements_comparison JSONB,

    -- Recommendations
    follow_up_recommendations TEXT,
    recommended_interval_days INTEGER,

    -- Performed by
    compared_by UUID NOT NULL,
    compared_at TIMESTAMP NOT NULL,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_comparison_current FOREIGN KEY (current_report_id)
        REFERENCES radiology_schema.radiology_report(id) ON DELETE CASCADE,
    CONSTRAINT fk_comparison_prior FOREIGN KEY (prior_report_id)
        REFERENCES radiology_schema.radiology_report(id),
    CONSTRAINT fk_comparison_study FOREIGN KEY (prior_study_id)
        REFERENCES radiology_schema.pacs_study(id)
);

-- ========================================
-- Report Distribution Table
-- ========================================
CREATE TABLE radiology_schema.report_distribution (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Report reference
    report_id UUID NOT NULL,

    -- Recipient information
    recipient_type VARCHAR(50) NOT NULL, -- REFERRING_PHYSICIAN, PRIMARY_CARE, SPECIALIST, PATIENT, OTHER
    recipient_id UUID, -- User/practitioner ID
    recipient_name VARCHAR(200) NOT NULL,
    recipient_email VARCHAR(200),
    recipient_fax VARCHAR(50),

    -- Distribution details
    distribution_method VARCHAR(30) NOT NULL, -- EMAIL, FAX, PORTAL, PRINT, HL7, API
    distribution_format VARCHAR(20), -- PDF, DICOM_SR, HL7_ORU, JSON
    distribution_priority VARCHAR(20) DEFAULT 'NORMAL', -- STAT, URGENT, NORMAL

    -- Distribution status
    distribution_status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, DELIVERED, FAILED, CANCELLED
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,

    -- Delivery tracking
    delivery_confirmed BOOLEAN DEFAULT false,
    delivery_confirmation_method VARCHAR(30),
    read_receipt BOOLEAN DEFAULT false,
    read_at TIMESTAMP,

    -- Failure handling
    failed BOOLEAN DEFAULT false,
    failure_reason TEXT,
    retry_count INTEGER DEFAULT 0,
    last_retry_at TIMESTAMP,
    max_retries INTEGER DEFAULT 3,

    -- Content
    include_images BOOLEAN DEFAULT false,
    include_measurements BOOLEAN DEFAULT true,
    include_comparisons BOOLEAN DEFAULT true,
    watermark BOOLEAN DEFAULT false,

    -- Security
    encrypted BOOLEAN DEFAULT false,
    password_protected BOOLEAN DEFAULT false,
    access_code VARCHAR(50),
    expires_at TIMESTAMP,

    -- Portal access
    portal_access_link VARCHAR(500),
    portal_accessed BOOLEAN DEFAULT false,
    portal_accessed_at TIMESTAMP,

    -- Additional options
    auto_distribution BOOLEAN DEFAULT false,
    distribution_rule_id UUID,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_distribution_report FOREIGN KEY (report_id)
        REFERENCES radiology_schema.radiology_report(id) ON DELETE CASCADE
);

-- ========================================
-- Voice Transcription Table
-- ========================================
CREATE TABLE radiology_schema.voice_transcription (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Report reference
    report_id UUID NOT NULL,

    -- Audio file information
    audio_file_path VARCHAR(500),
    audio_file_size_bytes BIGINT,
    audio_duration_seconds INTEGER,
    audio_format VARCHAR(20),

    -- Transcription details
    transcription_engine VARCHAR(50), -- MANUAL, GOOGLE_SPEECH, AMAZON_TRANSCRIBE, AZURE_SPEECH, NUANCE_DRAGON
    transcription_model VARCHAR(50),
    transcription_language VARCHAR(10) DEFAULT 'id-ID',

    -- Transcription content
    raw_transcription TEXT,
    edited_transcription TEXT,

    -- Processing status
    transcription_status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, FAILED, EDITED
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    processing_time_seconds INTEGER,

    -- Confidence and quality
    confidence_score DECIMAL(5,2), -- 0.00 to 100.00
    word_error_rate DECIMAL(5,2),
    requires_editing BOOLEAN DEFAULT false,

    -- Editing
    edited_by UUID,
    edited_at TIMESTAMP,
    edit_count INTEGER DEFAULT 0,

    -- Speaker identification
    speaker_id VARCHAR(50),
    speaker_name VARCHAR(200),
    multi_speaker BOOLEAN DEFAULT false,

    -- Error handling
    failed BOOLEAN DEFAULT false,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,

    -- Integration
    integration_request_id VARCHAR(100),
    callback_url VARCHAR(500),

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_transcription_report FOREIGN KEY (report_id)
        REFERENCES radiology_schema.radiology_report(id) ON DELETE CASCADE
);

-- ========================================
-- Report Statistics Table
-- ========================================
CREATE TABLE radiology_schema.report_statistics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Time period
    statistics_date DATE NOT NULL,
    period_type VARCHAR(20) NOT NULL, -- DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,

    -- Radiologist reference (null for aggregate)
    radiologist_id UUID,
    department_id UUID,
    modality_code VARCHAR(10),

    -- Volume metrics
    total_reports INTEGER DEFAULT 0,
    preliminary_reports INTEGER DEFAULT 0,
    final_reports INTEGER DEFAULT 0,
    amended_reports INTEGER DEFAULT 0,

    -- Timing metrics
    avg_reporting_time_minutes DECIMAL(10,2),
    median_reporting_time_minutes DECIMAL(10,2),
    reports_within_24_hours INTEGER DEFAULT 0,
    reports_over_24_hours INTEGER DEFAULT 0,

    -- Critical findings
    critical_findings_count INTEGER DEFAULT 0,
    critical_findings_notified INTEGER DEFAULT 0,
    avg_notification_time_minutes DECIMAL(10,2),

    -- Quality metrics
    amendment_rate DECIMAL(5,2),
    addendum_count INTEGER DEFAULT 0,
    correction_count INTEGER DEFAULT 0,

    -- Complexity distribution
    simple_reports INTEGER DEFAULT 0,
    moderate_reports INTEGER DEFAULT 0,
    complex_reports INTEGER DEFAULT 0,

    -- Transcription usage
    transcribed_reports INTEGER DEFAULT 0,
    transcription_success_rate DECIMAL(5,2),

    -- Distribution metrics
    reports_distributed INTEGER DEFAULT 0,
    avg_distribution_time_minutes DECIMAL(10,2),
    failed_distributions INTEGER DEFAULT 0,

    -- Comparison metrics
    reports_with_comparison INTEGER DEFAULT 0,

    -- Template usage
    template_usage JSONB, -- JSON with template usage statistics

    -- Computed at
    computed_at TIMESTAMP NOT NULL,
    computed_by VARCHAR(100),

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_statistics_period UNIQUE (statistics_date, period_type, radiologist_id, modality_code)
);

-- ========================================
-- Indexes for Performance
-- ========================================

-- Report Template Indexes
CREATE INDEX idx_template_modality ON radiology_schema.report_template(modality_code) WHERE is_active = true;
CREATE INDEX idx_template_type ON radiology_schema.report_template(template_type) WHERE is_active = true;
CREATE INDEX idx_template_usage ON radiology_schema.report_template(usage_count DESC, last_used_at DESC);

-- Radiology Report Indexes
CREATE INDEX idx_report_order ON radiology_schema.radiology_report(order_id);
CREATE INDEX idx_report_study ON radiology_schema.radiology_report(study_id);
CREATE INDEX idx_report_patient ON radiology_schema.radiology_report(patient_id);
CREATE INDEX idx_report_status ON radiology_schema.radiology_report(report_status);
CREATE INDEX idx_report_radiologist ON radiology_schema.radiology_report(reported_by);
CREATE INDEX idx_report_date ON radiology_schema.radiology_report(examination_date DESC);
CREATE INDEX idx_report_critical ON radiology_schema.radiology_report(has_critical_findings) WHERE has_critical_findings = true;
CREATE INDEX idx_report_verification ON radiology_schema.radiology_report(report_status, verified_at);
CREATE INDEX idx_report_accession ON radiology_schema.radiology_report(accession_number);

-- Amendment Indexes
CREATE INDEX idx_amendment_report ON radiology_schema.report_amendment(report_id);
CREATE INDEX idx_amendment_date ON radiology_schema.report_amendment(amended_at DESC);
CREATE INDEX idx_amendment_type ON radiology_schema.report_amendment(amendment_type);

-- Critical Finding Indexes
CREATE INDEX idx_critical_report ON radiology_schema.critical_finding_notification(report_id);
CREATE INDEX idx_critical_priority ON radiology_schema.critical_finding_notification(priority);
CREATE INDEX idx_critical_acknowledged ON radiology_schema.critical_finding_notification(acknowledged);
CREATE INDEX idx_critical_date ON radiology_schema.critical_finding_notification(notified_at DESC);

-- Comparison Indexes
CREATE INDEX idx_comparison_current ON radiology_schema.report_comparison(current_report_id);
CREATE INDEX idx_comparison_prior ON radiology_schema.report_comparison(prior_report_id);
CREATE INDEX idx_comparison_date ON radiology_schema.report_comparison(comparison_date DESC);

-- Distribution Indexes
CREATE INDEX idx_distribution_report ON radiology_schema.report_distribution(report_id);
CREATE INDEX idx_distribution_status ON radiology_schema.report_distribution(distribution_status);
CREATE INDEX idx_distribution_recipient ON radiology_schema.report_distribution(recipient_id);
CREATE INDEX idx_distribution_scheduled ON radiology_schema.report_distribution(scheduled_at) WHERE distribution_status = 'PENDING';

-- Transcription Indexes
CREATE INDEX idx_transcription_report ON radiology_schema.voice_transcription(report_id);
CREATE INDEX idx_transcription_status ON radiology_schema.voice_transcription(transcription_status);
CREATE INDEX idx_transcription_date ON radiology_schema.voice_transcription(created_at DESC);

-- Statistics Indexes
CREATE INDEX idx_statistics_date ON radiology_schema.report_statistics(statistics_date DESC);
CREATE INDEX idx_statistics_radiologist ON radiology_schema.report_statistics(radiologist_id, statistics_date DESC);
CREATE INDEX idx_statistics_period ON radiology_schema.report_statistics(period_type, period_start_date, period_end_date);
