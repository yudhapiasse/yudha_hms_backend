-- ============================================================================
-- Flyway Migration V24: Create Clinical Documents Tables
-- Description: Phase 4.4 Clinical Documents - Document templates, generated
--              documents, digital signatures, and comprehensive audit trail
-- Author: HMS Development Team
-- Date: 2025-01-20
-- ============================================================================

-- Create document templates table
CREATE TABLE IF NOT EXISTS clinical_schema.document_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Template Identification
    template_code VARCHAR(50) NOT NULL UNIQUE,
    template_name VARCHAR(300) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    category VARCHAR(50),

    -- Template Content
    template_content TEXT NOT NULL,
    header_template TEXT,
    footer_template TEXT,
    css_styles TEXT,

    -- Placeholders and Auto-Population
    available_placeholders TEXT,
    required_fields TEXT,
    auto_populate_fields TEXT,

    -- Signature Configuration
    requires_doctor_signature BOOLEAN DEFAULT TRUE,
    requires_patient_signature BOOLEAN DEFAULT FALSE,
    requires_witness_signature BOOLEAN DEFAULT FALSE,
    requires_hospital_stamp BOOLEAN DEFAULT TRUE,
    signature_placeholder_positions TEXT,

    -- Document Properties
    page_size VARCHAR(20) DEFAULT 'A4',
    page_orientation VARCHAR(20) DEFAULT 'PORTRAIT',
    number_of_copies INTEGER DEFAULT 1,
    watermark_text VARCHAR(200),
    include_barcode BOOLEAN DEFAULT FALSE,
    include_qr_code BOOLEAN DEFAULT FALSE,

    -- Language and Localization
    language VARCHAR(10) DEFAULT 'id_ID',
    is_bilingual BOOLEAN DEFAULT FALSE,

    -- Validity and Legal
    has_expiry BOOLEAN DEFAULT FALSE,
    default_validity_days INTEGER,
    legal_disclaimer TEXT,
    terms_and_conditions TEXT,

    -- Usage and Status
    usage_count BIGINT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    is_official BOOLEAN DEFAULT FALSE,

    -- Approval and Review
    approved_by_id UUID,
    approved_by_name VARCHAR(200),
    approved_at TIMESTAMP,
    last_reviewed_date DATE,
    review_frequency_months INTEGER,

    -- Metadata
    description TEXT,
    usage_instructions TEXT,
    notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_doc_template_type ON clinical_schema.document_templates(document_type);
CREATE INDEX idx_doc_template_code ON clinical_schema.document_templates(template_code);
CREATE INDEX idx_doc_template_category ON clinical_schema.document_templates(category);
CREATE INDEX idx_doc_template_active ON clinical_schema.document_templates(is_active);

COMMENT ON TABLE clinical_schema.document_templates IS 'Templates for clinical documents with auto-population support';

-- Create clinical documents table
CREATE TABLE IF NOT EXISTS clinical_schema.clinical_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Document Identification
    document_number VARCHAR(50) NOT NULL UNIQUE,
    document_title VARCHAR(300) NOT NULL,
    document_type VARCHAR(50) NOT NULL,

    -- Template Reference
    template_id UUID NOT NULL REFERENCES clinical_schema.document_templates(id),

    -- Patient and Encounter References
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200) NOT NULL,
    patient_medical_record_number VARCHAR(50),
    encounter_id UUID,

    -- Document Content
    document_content TEXT NOT NULL,
    raw_data TEXT,

    -- Versioning
    document_version INTEGER NOT NULL DEFAULT 1,
    parent_document_id UUID,
    is_latest_version BOOLEAN NOT NULL DEFAULT TRUE,
    revision_reason TEXT,

    -- Document Dates
    document_date DATE NOT NULL,
    issued_date TIMESTAMP,
    expiry_date DATE,
    valid_until DATE,

    -- Document Status
    document_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    -- PDF Generation
    pdf_generated BOOLEAN DEFAULT FALSE,
    pdf_file_path VARCHAR(500),
    pdf_file_size BIGINT,
    pdf_generated_at TIMESTAMP,
    pdf_download_count INTEGER DEFAULT 0,

    -- Creator/Issuer Information
    issued_by_id UUID NOT NULL,
    issued_by_name VARCHAR(200) NOT NULL,
    issued_by_title VARCHAR(100),
    issued_by_license_number VARCHAR(50),

    -- Digital Signatures
    requires_signatures BOOLEAN DEFAULT TRUE,
    all_signatures_collected BOOLEAN DEFAULT FALSE,
    signature_count INTEGER DEFAULT 0,

    -- Verification and Validation
    verified BOOLEAN DEFAULT FALSE,
    verified_by_id UUID,
    verified_by_name VARCHAR(200),
    verified_at TIMESTAMP,
    verification_code VARCHAR(50),

    -- Printing and Distribution
    printed BOOLEAN DEFAULT FALSE,
    print_count INTEGER DEFAULT 0,
    last_printed_at TIMESTAMP,
    sent_to_patient BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP,
    delivery_method VARCHAR(50),

    -- Cancellation and Void
    voided BOOLEAN DEFAULT FALSE,
    voided_at TIMESTAMP,
    voided_by_id UUID,
    voided_by_name VARCHAR(200),
    void_reason TEXT,

    -- Security and Access
    is_confidential BOOLEAN DEFAULT FALSE,
    access_level VARCHAR(20) DEFAULT 'NORMAL',
    watermark_applied BOOLEAN DEFAULT FALSE,

    -- External References
    reference_number VARCHAR(100),
    related_documents TEXT,

    -- Metadata
    purpose TEXT,
    notes TEXT,
    tags TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_clinical_doc_number ON clinical_schema.clinical_documents(document_number);
CREATE INDEX idx_clinical_doc_patient ON clinical_schema.clinical_documents(patient_id);
CREATE INDEX idx_clinical_doc_encounter ON clinical_schema.clinical_documents(encounter_id);
CREATE INDEX idx_clinical_doc_template ON clinical_schema.clinical_documents(template_id);
CREATE INDEX idx_clinical_doc_type ON clinical_schema.clinical_documents(document_type);
CREATE INDEX idx_clinical_doc_status ON clinical_schema.clinical_documents(document_status);
CREATE INDEX idx_clinical_doc_date ON clinical_schema.clinical_documents(document_date);
CREATE INDEX idx_clinical_doc_version ON clinical_schema.clinical_documents(parent_document_id, document_version);

COMMENT ON TABLE clinical_schema.clinical_documents IS 'Generated clinical documents with versioning and PDF support';

-- Create document signatures table
CREATE TABLE IF NOT EXISTS clinical_schema.document_signatures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Document Reference
    document_id UUID NOT NULL REFERENCES clinical_schema.clinical_documents(id) ON DELETE CASCADE,

    -- Signer Information
    signer_id UUID NOT NULL,
    signer_name VARCHAR(200) NOT NULL,
    signer_role VARCHAR(50) NOT NULL,
    signer_title VARCHAR(100),
    signer_license_number VARCHAR(50),
    signer_specialization VARCHAR(100),

    -- Signature Details
    signature_data TEXT,
    signature_method VARCHAR(50) DEFAULT 'DIGITAL',
    signature_location VARCHAR(200),
    signature_device VARCHAR(100),

    -- Signature Status
    signature_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    signed_at TIMESTAMP,
    sequence_number INTEGER,
    is_required BOOLEAN NOT NULL DEFAULT TRUE,

    -- PKI and Cryptographic Verification
    certificate_serial_number VARCHAR(100),
    certificate_issuer VARCHAR(200),
    certificate_valid_from TIMESTAMP,
    certificate_valid_until TIMESTAMP,
    signature_hash VARCHAR(256),
    signature_algorithm VARCHAR(50),

    -- Verification
    verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP,
    verification_method VARCHAR(50),
    verification_result TEXT,

    -- Rejection and Revocation
    rejected BOOLEAN DEFAULT FALSE,
    rejected_at TIMESTAMP,
    rejection_reason TEXT,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP,
    revocation_reason TEXT,

    -- Consent and Authorization
    consent_given BOOLEAN DEFAULT FALSE,
    consent_statement TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),

    -- Placeholder Position (for PDF)
    placeholder_x INTEGER,
    placeholder_y INTEGER,
    placeholder_page INTEGER,

    -- Metadata
    notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_doc_signature_document ON clinical_schema.document_signatures(document_id);
CREATE INDEX idx_doc_signature_signer ON clinical_schema.document_signatures(signer_id);
CREATE INDEX idx_doc_signature_status ON clinical_schema.document_signatures(signature_status);
CREATE INDEX idx_doc_signature_role ON clinical_schema.document_signatures(signer_role);
CREATE INDEX idx_doc_signature_date ON clinical_schema.document_signatures(signed_at);

COMMENT ON TABLE clinical_schema.document_signatures IS 'Digital signatures for clinical documents';

-- Create document audit logs table
CREATE TABLE IF NOT EXISTS clinical_schema.document_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Document Reference
    document_id UUID NOT NULL,
    document_number VARCHAR(50),
    document_type VARCHAR(50),

    -- Action Details
    action_type VARCHAR(50) NOT NULL,
    action_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    action_description TEXT,

    -- User Information
    user_id UUID NOT NULL,
    user_name VARCHAR(200) NOT NULL,
    user_role VARCHAR(100),
    user_department VARCHAR(100),

    -- Session and Security
    session_id VARCHAR(100),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    device_type VARCHAR(50),
    location VARCHAR(200),

    -- Change Tracking
    before_value TEXT,
    after_value TEXT,
    changed_fields TEXT,

    -- Operation Context
    operation_status VARCHAR(20) DEFAULT 'SUCCESS',
    error_message TEXT,
    duration_ms BIGINT,

    -- Additional Context
    patient_id UUID,
    encounter_id UUID,
    related_entity_type VARCHAR(50),
    related_entity_id UUID,

    -- Access Control
    access_granted BOOLEAN DEFAULT TRUE,
    access_denial_reason TEXT,
    security_level VARCHAR(20),

    -- Compliance and Regulation
    is_hipaa_relevant BOOLEAN DEFAULT FALSE,
    is_gdpr_relevant BOOLEAN DEFAULT FALSE,
    retention_period_days INTEGER,

    -- Metadata
    tags TEXT,
    notes TEXT,

    -- Timestamps (from BaseEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doc_audit_document ON clinical_schema.document_audit_logs(document_id);
CREATE INDEX idx_doc_audit_user ON clinical_schema.document_audit_logs(user_id);
CREATE INDEX idx_doc_audit_action ON clinical_schema.document_audit_logs(action_type);
CREATE INDEX idx_doc_audit_timestamp ON clinical_schema.document_audit_logs(action_timestamp);
CREATE INDEX idx_doc_audit_ip ON clinical_schema.document_audit_logs(ip_address);

COMMENT ON TABLE clinical_schema.document_audit_logs IS 'Comprehensive audit trail for document operations';

-- ============================================================================
-- Insert Sample Document Templates (Common Indonesian Documents)
-- ============================================================================

-- Surat Keterangan Sehat (Health Certificate)
INSERT INTO clinical_schema.document_templates (
    template_code, template_name, document_type, category,
    template_content, requires_doctor_signature, requires_patient_signature,
    is_active, is_default, is_official, language
) VALUES (
    'SURAT_SEHAT_001',
    'Surat Keterangan Sehat - Standard',
    'SURAT_KETERANGAN_SEHAT',
    'CERTIFICATE',
    '<html><head><style>body { font-family: Arial, sans-serif; }</style></head><body>
    <div class="header"><h2>SURAT KETERANGAN SEHAT</h2></div>
    <p>Yang bertanda tangan di bawah ini, Dokter {{doctor_name}}, menerangkan bahwa:</p>
    <table>
        <tr><td>Nama</td><td>: {{patient_name}}</td></tr>
        <tr><td>Umur</td><td>: {{patient_age}} tahun</td></tr>
        <tr><td>Jenis Kelamin</td><td>: {{patient_gender}}</td></tr>
        <tr><td>Alamat</td><td>: {{patient_address}}</td></tr>
    </table>
    <p>Telah diperiksa pada tanggal {{exam_date}} dalam keadaan SEHAT.</p>
    <p>Demikian surat keterangan ini dibuat untuk dapat dipergunakan sebagaimana mestinya.</p>
    <div class="signature">
        <p>{{issue_place}}, {{issue_date}}</p>
        <p>Dokter Pemeriksa</p>
        <br/><br/>
        <p>{{doctor_name}}<br/>{{doctor_license}}</p>
    </div></body></html>',
    TRUE, FALSE, TRUE, TRUE, TRUE, 'id_ID'
);

-- Surat Keterangan Sakit (Sick Leave Certificate)
INSERT INTO clinical_schema.document_templates (
    template_code, template_name, document_type, category,
    template_content, requires_doctor_signature, requires_patient_signature,
    is_active, is_default, is_official, language, has_expiry, default_validity_days
) VALUES (
    'SURAT_SAKIT_001',
    'Surat Keterangan Sakit - Standard',
    'SURAT_KETERANGAN_SAKIT',
    'CERTIFICATE',
    '<html><head><style>body { font-family: Arial, sans-serif; }</style></head><body>
    <div class="header"><h2>SURAT KETERANGAN SAKIT</h2></div>
    <p>Yang bertanda tangan di bawah ini, Dokter {{doctor_name}}, menerangkan bahwa:</p>
    <table>
        <tr><td>Nama</td><td>: {{patient_name}}</td></tr>
        <tr><td>Umur</td><td>: {{patient_age}} tahun</td></tr>
        <tr><td>Alamat</td><td>: {{patient_address}}</td></tr>
        <tr><td>Pekerjaan</td><td>: {{patient_occupation}}</td></tr>
    </table>
    <p>Perlu istirahat selama {{rest_days}} hari, terhitung mulai tanggal {{rest_start_date}} sampai dengan {{rest_end_date}}.</p>
    <p>Diagnosa: {{diagnosis}}</p>
    <p>Demikian surat keterangan ini dibuat untuk dapat dipergunakan sebagaimana mestinya.</p>
    <div class="signature">
        <p>{{issue_place}}, {{issue_date}}</p>
        <p>Dokter Pemeriksa</p>
        <br/><br/>
        <p>{{doctor_name}}<br/>{{doctor_license}}</p>
    </div></body></html>',
    TRUE, FALSE, TRUE, TRUE, TRUE, 'id_ID', TRUE, 30
);

-- Resume Medis (Medical Resume)
INSERT INTO clinical_schema.document_templates (
    template_code, template_name, document_type, category,
    template_content, requires_doctor_signature, requires_patient_signature,
    is_active, is_default, is_official, language
) VALUES (
    'RESUME_MEDIS_001',
    'Resume Medis Pasien',
    'RESUME_MEDIS',
    'REPORT',
    '<html><head><style>body { font-family: Arial, sans-serif; }</style></head><body>
    <div class="header"><h2>RESUME MEDIS</h2></div>
    <h3>IDENTITAS PASIEN</h3>
    <table>
        <tr><td>Nama</td><td>: {{patient_name}}</td></tr>
        <tr><td>No. Rekam Medis</td><td>: {{patient_mrn}}</td></tr>
        <tr><td>Tanggal Lahir</td><td>: {{patient_dob}}</td></tr>
        <tr><td>Jenis Kelamin</td><td>: {{patient_gender}}</td></tr>
    </table>
    <h3>RIWAYAT PENYAKIT</h3>
    <p><strong>Keluhan Utama:</strong> {{chief_complaint}}</p>
    <p><strong>Riwayat Penyakit Sekarang:</strong> {{present_illness}}</p>
    <p><strong>Riwayat Penyakit Dahulu:</strong> {{past_medical_history}}</p>
    <h3>PEMERIKSAAN FISIK</h3>
    <p>{{physical_examination}}</p>
    <h3>PEMERIKSAAN PENUNJANG</h3>
    <p>{{diagnostic_tests}}</p>
    <h3>DIAGNOSIS</h3>
    <p>{{diagnoses}}</p>
    <h3>TINDAKAN/TERAPI</h3>
    <p>{{treatments}}</p>
    <h3>PROGNOSIS</h3>
    <p>{{prognosis}}</p>
    <div class="signature">
        <p>{{issue_place}}, {{issue_date}}</p>
        <p>Dokter Penanggung Jawab</p>
        <br/><br/>
        <p>{{doctor_name}}<br/>{{doctor_license}}</p>
    </div></body></html>',
    TRUE, FALSE, TRUE, TRUE, TRUE, 'id_ID'
);

-- Surat Rujukan (Referral Letter)
INSERT INTO clinical_schema.document_templates (
    template_code, template_name, document_type, category,
    template_content, requires_doctor_signature, requires_patient_signature,
    is_active, is_default, is_official, language
) VALUES (
    'SURAT_RUJUKAN_001',
    'Surat Rujukan - Standard',
    'SURAT_RUJUKAN',
    'LETTER',
    '<html><head><style>body { font-family: Arial, sans-serif; }</style></head><body>
    <div class="header"><h2>SURAT RUJUKAN</h2></div>
    <p>Kepada Yth.<br/>{{referral_to}}<br/>{{referral_address}}</p>
    <p>Dengan hormat,</p>
    <p>Mohon pemeriksaan dan penanganan lebih lanjut pasien:</p>
    <table>
        <tr><td>Nama</td><td>: {{patient_name}}</td></tr>
        <tr><td>Umur</td><td>: {{patient_age}} tahun</td></tr>
        <tr><td>Alamat</td><td>: {{patient_address}}</td></tr>
    </table>
    <p><strong>Diagnosis Sementara:</strong> {{provisional_diagnosis}}</p>
    <p><strong>Riwayat Singkat:</strong> {{clinical_summary}}</p>
    <p><strong>Terapi yang Sudah Diberikan:</strong> {{previous_treatment}}</p>
    <p><strong>Tujuan Rujukan:</strong> {{referral_purpose}}</p>
    <p>Demikian atas perhatian dan kerjasamanya, kami ucapkan terima kasih.</p>
    <div class="signature">
        <p>{{issue_place}}, {{issue_date}}</p>
        <p>Dokter Perujuk</p>
        <br/><br/>
        <p>{{doctor_name}}<br/>{{doctor_license}}</p>
    </div></body></html>',
    TRUE, FALSE, TRUE, TRUE, TRUE, 'id_ID'
);

-- Informed Consent
INSERT INTO clinical_schema.document_templates (
    template_code, template_name, document_type, category,
    template_content, requires_doctor_signature, requires_patient_signature,
    requires_witness_signature, is_active, is_default, is_official, language
) VALUES (
    'INFORMED_CONSENT_001',
    'Informed Consent - Tindakan Medis',
    'INFORMED_CONSENT',
    'CONSENT',
    '<html><head><style>body { font-family: Arial, sans-serif; }</style></head><body>
    <div class="header"><h2>PERSETUJUAN TINDAKAN MEDIS<br/>(INFORMED CONSENT)</h2></div>
    <p>Saya yang bertanda tangan di bawah ini:</p>
    <table>
        <tr><td>Nama</td><td>: {{patient_name}}</td></tr>
        <tr><td>Umur</td><td>: {{patient_age}} tahun</td></tr>
        <tr><td>Alamat</td><td>: {{patient_address}}</td></tr>
    </table>
    <p>Dengan ini menyatakan SETUJU untuk dilakukan tindakan:</p>
    <p><strong>{{procedure_name}}</strong></p>
    <p>Saya telah mendapatkan penjelasan lengkap mengenai:</p>
    <ol>
        <li>Diagnosis dan rencana tindakan</li>
        <li>Tujuan dan manfaat tindakan: {{benefits}}</li>
        <li>Risiko dan komplikasi: {{risks}}</li>
        <li>Alternatif tindakan: {{alternatives}}</li>
        <li>Prognosis bila tindakan dilakukan/tidak dilakukan</li>
    </ol>
    <p>Saya telah memahami penjelasan tersebut dan berkesempatan bertanya serta mendapat jawaban yang memuaskan.</p>
    <div class="signature-section">
        <table width="100%">
            <tr>
                <td width="50%">
                    <p>Pasien/Keluarga</p>
                    <br/><br/><br/>
                    <p>({{patient_name}})</p>
                </td>
                <td width="50%">
                    <p>Dokter</p>
                    <br/><br/><br/>
                    <p>({{doctor_name}})</p>
                </td>
            </tr>
        </table>
        <p>Saksi: _________________</p>
    </div>
    <p>{{issue_place}}, {{issue_date}}</p>
    </body></html>',
    TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, 'id_ID'
);

-- ============================================================================
-- End of Migration V24
-- ============================================================================
