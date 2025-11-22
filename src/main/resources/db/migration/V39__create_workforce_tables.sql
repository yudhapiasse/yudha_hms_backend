-- V39: Workforce Management - Employee Master Data
-- Phase 12.1: Comprehensive employee management for Indonesian hospital

-- Employee master table
CREATE TABLE workforce_schema.employee (
    -- Primary key and audit fields inherited from soft_deletable pattern
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Employee identification
    employee_number VARCHAR(50) NOT NULL UNIQUE,
    nik VARCHAR(16) NOT NULL UNIQUE, -- Nomor Induk Kependudukan (Indonesian National ID)
    npwp VARCHAR(20), -- Nomor Pokok Wajib Pajak (Tax ID)
    bpjs_kesehatan_number VARCHAR(20), -- BPJS Kesehatan number
    bpjs_ketenagakerjaan_number VARCHAR(20), -- BPJS Ketenagakerjaan number

    -- Personal information
    title VARCHAR(20), -- Dr., Prof., etc.
    full_name VARCHAR(200) NOT NULL,
    nickname VARCHAR(100),
    gender VARCHAR(10) NOT NULL, -- MALE, FEMALE
    place_of_birth VARCHAR(100),
    date_of_birth DATE NOT NULL,
    blood_type VARCHAR(5),
    religion VARCHAR(30),
    marital_status VARCHAR(20),
    nationality VARCHAR(50) DEFAULT 'Indonesian',

    -- Contact information
    email VARCHAR(200),
    phone_number VARCHAR(20),
    mobile_number VARCHAR(20) NOT NULL,
    address TEXT,
    rt_rw VARCHAR(20), -- RT/RW
    kelurahan VARCHAR(100),
    kecamatan VARCHAR(100),
    city VARCHAR(100),
    province VARCHAR(100),
    postal_code VARCHAR(10),
    country VARCHAR(50) DEFAULT 'Indonesia',

    -- Emergency contact
    emergency_contact_name VARCHAR(200),
    emergency_contact_relationship VARCHAR(50),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_address TEXT,

    -- Employment details
    department_id UUID,
    position_id UUID,
    employment_status VARCHAR(30) NOT NULL, -- PERMANENT, CONTRACT, OUTSOURCE, PROBATION
    employment_type VARCHAR(30), -- FULL_TIME, PART_TIME, SHIFT
    join_date DATE NOT NULL,
    contract_start_date DATE,
    contract_end_date DATE,
    probation_end_date DATE,
    resignation_date DATE,
    resignation_reason TEXT,
    last_working_date DATE,

    -- Work schedule
    shift_group VARCHAR(50),
    work_hours_per_week INTEGER,
    work_days_per_week INTEGER,

    -- Salary and benefits
    basic_salary DECIMAL(15,2),
    currency VARCHAR(10) DEFAULT 'IDR',
    salary_grade VARCHAR(20),
    bank_name VARCHAR(100),
    bank_account_number VARCHAR(50),
    bank_account_holder_name VARCHAR(200),
    bank_branch VARCHAR(100),

    -- Professional information (for medical staff)
    is_medical_staff BOOLEAN DEFAULT false,
    specialization VARCHAR(100),
    sub_specialization VARCHAR(100),

    -- Account and access
    user_account_id UUID, -- Link to system user account
    active BOOLEAN DEFAULT true,
    can_login BOOLEAN DEFAULT false,

    -- Photo and signature
    photo_url VARCHAR(500),
    signature_url VARCHAR(500),

    -- Additional information
    notes TEXT,

    -- Indexes for performance
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES shared_schema.department(id),
    CONSTRAINT fk_employee_position FOREIGN KEY (position_id) REFERENCES workforce_schema.employee_position(id)
);

-- Employee position/job title table
CREATE TABLE workforce_schema.employee_position (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    position_code VARCHAR(50) NOT NULL UNIQUE,
    position_name VARCHAR(200) NOT NULL,
    position_name_id VARCHAR(200) NOT NULL, -- Indonesian name
    position_level VARCHAR(30), -- STAFF, SUPERVISOR, MANAGER, DIRECTOR
    department_id UUID,
    parent_position_id UUID, -- For hierarchy
    
    -- Job description
    job_description TEXT,
    responsibilities TEXT,
    requirements TEXT,
    qualifications TEXT,

    -- Professional requirements
    requires_medical_license BOOLEAN DEFAULT false,
    required_license_type VARCHAR(50), -- STR, SIP, etc.
    requires_certification BOOLEAN DEFAULT false,
    required_certifications TEXT[],

    -- Employment
    min_experience_years INTEGER,
    min_education_level VARCHAR(30),

    active BOOLEAN DEFAULT true,
    notes TEXT,

    CONSTRAINT fk_position_department FOREIGN KEY (department_id) REFERENCES shared_schema.department(id),
    CONSTRAINT fk_position_parent FOREIGN KEY (parent_position_id) REFERENCES workforce_schema.employee_position(id)
);

-- Professional licenses (STR, SIP, etc.)
CREATE TABLE workforce_schema.professional_license (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    license_type VARCHAR(50) NOT NULL, -- STR, SIP, SIPP, SIKP, etc.
    license_number VARCHAR(100) NOT NULL,
    issued_by VARCHAR(200) NOT NULL, -- Issuing authority
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    
    -- Renewal tracking
    is_expired BOOLEAN DEFAULT false,
    renewal_status VARCHAR(30), -- VALID, EXPIRING_SOON, EXPIRED, RENEWAL_IN_PROGRESS
    renewal_reminder_sent BOOLEAN DEFAULT false,
    last_reminder_date DATE,

    -- Professional details
    profession VARCHAR(100), -- Dokter, Perawat, Bidan, Apoteker, etc.
    specialization VARCHAR(100),
    practice_location VARCHAR(200),
    scope_of_practice TEXT,

    -- Document
    document_url VARCHAR(500),
    verified BOOLEAN DEFAULT false,
    verified_by UUID,
    verified_at TIMESTAMP,

    notes TEXT,

    CONSTRAINT fk_license_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id)
);

-- Education records
CREATE TABLE workforce_schema.employee_education (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    education_level VARCHAR(30) NOT NULL, -- SD, SMP, SMA, D3, S1, S2, S3
    institution_name VARCHAR(200) NOT NULL,
    major_field_of_study VARCHAR(200),
    degree_title VARCHAR(100),
    
    start_year INTEGER,
    graduation_year INTEGER NOT NULL,
    gpa DECIMAL(4,2),
    gpa_scale DECIMAL(4,2) DEFAULT 4.00,

    country VARCHAR(50) DEFAULT 'Indonesia',
    city VARCHAR(100),

    -- Certificate/diploma
    certificate_number VARCHAR(100),
    certificate_url VARCHAR(500),
    
    is_highest_education BOOLEAN DEFAULT false,
    verified BOOLEAN DEFAULT false,

    notes TEXT,

    CONSTRAINT fk_education_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id)
);

-- Training and certification records
CREATE TABLE workforce_schema.employee_training (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    training_type VARCHAR(50) NOT NULL, -- INTERNAL, EXTERNAL, CERTIFICATION, WORKSHOP, SEMINAR
    training_name VARCHAR(200) NOT NULL,
    training_provider VARCHAR(200),
    training_category VARCHAR(100),
    
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    duration_hours INTEGER,
    location VARCHAR(200),
    
    -- Certification
    issues_certificate BOOLEAN DEFAULT false,
    certificate_number VARCHAR(100),
    certificate_url VARCHAR(500),
    certificate_expiry_date DATE,

    -- Compliance and mandatory training
    is_mandatory BOOLEAN DEFAULT false,
    is_regulatory_required BOOLEAN DEFAULT false,
    
    -- Cost
    training_cost DECIMAL(15,2),
    currency VARCHAR(10) DEFAULT 'IDR',
    paid_by VARCHAR(50), -- COMPANY, EMPLOYEE, SHARED

    -- Evaluation
    attended BOOLEAN DEFAULT true,
    completion_status VARCHAR(30), -- COMPLETED, IN_PROGRESS, CANCELLED
    evaluation_score DECIMAL(5,2),
    pass_fail_status VARCHAR(20),

    notes TEXT,

    CONSTRAINT fk_training_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id)
);

-- Family members for benefits
CREATE TABLE workforce_schema.employee_family (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    relationship VARCHAR(50) NOT NULL, -- SPOUSE, CHILD, PARENT, SIBLING
    full_name VARCHAR(200) NOT NULL,
    nik VARCHAR(16), -- NIK for family member
    
    gender VARCHAR(10),
    place_of_birth VARCHAR(100),
    date_of_birth DATE NOT NULL,
    age INTEGER,

    -- Dependent status
    is_dependent BOOLEAN DEFAULT false,
    dependent_start_date DATE,
    dependent_end_date DATE,

    -- Education (for children)
    education_level VARCHAR(30),
    school_name VARCHAR(200),

    -- Employment (for spouse)
    occupation VARCHAR(100),
    employer_name VARCHAR(200),

    -- Health insurance
    covered_by_health_insurance BOOLEAN DEFAULT false,
    bpjs_number VARCHAR(20),

    -- Contact
    phone_number VARCHAR(20),
    address TEXT,

    -- Emergency contact designation
    is_emergency_contact BOOLEAN DEFAULT false,

    notes TEXT,

    CONSTRAINT fk_family_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id)
);

-- Document storage
CREATE TABLE workforce_schema.employee_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- KTP, KK, NPWP, IJAZAH, TRANSKRIP, CV, etc.
    document_name VARCHAR(200) NOT NULL,
    document_number VARCHAR(100),
    
    issue_date DATE,
    expiry_date DATE,
    is_expired BOOLEAN DEFAULT false,

    -- File information
    file_url VARCHAR(500) NOT NULL,
    file_name VARCHAR(200),
    file_size_bytes BIGINT,
    file_type VARCHAR(50),
    
    -- Verification
    is_original BOOLEAN DEFAULT false,
    verified BOOLEAN DEFAULT false,
    verified_by UUID,
    verified_at TIMESTAMP,

    -- Access control
    is_confidential BOOLEAN DEFAULT false,
    access_level VARCHAR(30) DEFAULT 'INTERNAL',

    description TEXT,
    notes TEXT,

    CONSTRAINT fk_document_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id)
);

-- Create indexes for performance
CREATE INDEX idx_employee_number ON workforce_schema.employee(employee_number);
CREATE INDEX idx_employee_nik ON workforce_schema.employee(nik);
CREATE INDEX idx_employee_name ON workforce_schema.employee(full_name);
CREATE INDEX idx_employee_department ON workforce_schema.employee(department_id);
CREATE INDEX idx_employee_position ON workforce_schema.employee(position_id);
CREATE INDEX idx_employee_status ON workforce_schema.employee(employment_status);
CREATE INDEX idx_employee_active ON workforce_schema.employee(active);

CREATE INDEX idx_position_code ON workforce_schema.employee_position(position_code);
CREATE INDEX idx_position_name ON workforce_schema.employee_position(position_name);
CREATE INDEX idx_position_department ON workforce_schema.employee_position(department_id);

CREATE INDEX idx_license_employee ON workforce_schema.professional_license(employee_id);
CREATE INDEX idx_license_type ON workforce_schema.professional_license(license_type);
CREATE INDEX idx_license_expiry ON workforce_schema.professional_license(expiry_date);
CREATE INDEX idx_license_status ON workforce_schema.professional_license(renewal_status);

CREATE INDEX idx_education_employee ON workforce_schema.employee_education(employee_id);
CREATE INDEX idx_training_employee ON workforce_schema.employee_training(employee_id);
CREATE INDEX idx_family_employee ON workforce_schema.employee_family(employee_id);
CREATE INDEX idx_document_employee ON workforce_schema.employee_document(employee_id);
CREATE INDEX idx_document_type ON workforce_schema.employee_document(document_type);
