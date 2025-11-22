-- ============================================================================
-- Phase 12.3: Payroll Integration Module
-- Indonesian Hospital Payroll System with BPJS and PPh 21 Tax Compliance
-- ============================================================================

-- Payroll Period Table
CREATE TABLE IF NOT EXISTS workforce_schema.payroll_period (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    period_year INTEGER NOT NULL,
    period_month INTEGER NOT NULL,
    period_code VARCHAR(20) NOT NULL UNIQUE,
    period_name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    payment_date DATE,
    cut_off_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    is_thr_period BOOLEAN NOT NULL DEFAULT false,
    thr_type VARCHAR(30),
    processing_started_at TIMESTAMP,
    processing_completed_at TIMESTAMP,
    approved_by UUID,
    approved_at TIMESTAMP,
    notes TEXT,

    CONSTRAINT uq_payroll_period_year_month UNIQUE (period_year, period_month),
    CONSTRAINT chk_period_month CHECK (period_month BETWEEN 1 AND 12)
);

CREATE INDEX IF NOT EXISTS idx_payroll_period_status ON workforce_schema.payroll_period(status);
CREATE INDEX IF NOT EXISTS idx_payroll_period_dates ON workforce_schema.payroll_period(start_date, end_date);

-- Payroll Component Master (Allowances, Deductions, etc.)
CREATE TABLE IF NOT EXISTS workforce_schema.payroll_component (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    component_code VARCHAR(50) NOT NULL UNIQUE,
    component_name VARCHAR(200) NOT NULL,
    component_name_id VARCHAR(200) NOT NULL,
    component_type VARCHAR(30) NOT NULL,
    calculation_method VARCHAR(30) NOT NULL,
    is_taxable BOOLEAN NOT NULL DEFAULT true,
    is_bpjs_kesehatan_included BOOLEAN NOT NULL DEFAULT true,
    is_bpjs_tk_included BOOLEAN NOT NULL DEFAULT true,
    is_recurring BOOLEAN NOT NULL DEFAULT true,
    is_prorated BOOLEAN NOT NULL DEFAULT false,
    default_amount NUMERIC(15,2),
    percentage_base VARCHAR(30),
    percentage_value NUMERIC(5,2),
    priority_order INTEGER NOT NULL DEFAULT 100,
    gl_account_code VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT true,
    notes TEXT,

    CONSTRAINT chk_component_type CHECK (component_type IN (
        'EARNING', 'ALLOWANCE', 'DEDUCTION', 'TAX',
        'INSURANCE', 'BENEFIT', 'OVERTIME', 'BONUS'
    ))
);

CREATE INDEX IF NOT EXISTS idx_payroll_component_type ON workforce_schema.payroll_component(component_type);
CREATE INDEX IF NOT EXISTS idx_payroll_component_active ON workforce_schema.payroll_component(active);

-- Employee Payroll Master
CREATE TABLE IF NOT EXISTS workforce_schema.employee_payroll (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    payroll_period_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    payroll_number VARCHAR(50) NOT NULL UNIQUE,

    -- Attendance Summary
    working_days INTEGER NOT NULL DEFAULT 0,
    actual_working_days INTEGER NOT NULL DEFAULT 0,
    absent_days INTEGER NOT NULL DEFAULT 0,
    leave_days INTEGER NOT NULL DEFAULT 0,
    unpaid_leave_days INTEGER NOT NULL DEFAULT 0,

    -- Overtime Summary
    normal_overtime_hours NUMERIC(8,2) DEFAULT 0,
    weekend_overtime_hours NUMERIC(8,2) DEFAULT 0,
    holiday_overtime_hours NUMERIC(8,2) DEFAULT 0,

    -- Gross Components
    basic_salary NUMERIC(15,2) NOT NULL,
    total_allowances NUMERIC(15,2) DEFAULT 0,
    total_overtime NUMERIC(15,2) DEFAULT 0,
    total_shift_differential NUMERIC(15,2) DEFAULT 0,
    total_incentives NUMERIC(15,2) DEFAULT 0,
    total_bonuses NUMERIC(15,2) DEFAULT 0,
    thr_amount NUMERIC(15,2) DEFAULT 0,
    gross_salary NUMERIC(15,2) NOT NULL,

    -- Deductions
    bpjs_kesehatan_employee NUMERIC(15,2) DEFAULT 0,
    bpjs_kesehatan_family NUMERIC(15,2) DEFAULT 0,
    bpjs_tk_jht NUMERIC(15,2) DEFAULT 0,
    bpjs_tk_jp NUMERIC(15,2) DEFAULT 0,
    total_bpjs_deduction NUMERIC(15,2) DEFAULT 0,

    pph21_amount NUMERIC(15,2) DEFAULT 0,
    loan_deduction NUMERIC(15,2) DEFAULT 0,
    advance_deduction NUMERIC(15,2) DEFAULT 0,
    other_deductions NUMERIC(15,2) DEFAULT 0,
    total_deductions NUMERIC(15,2) DEFAULT 0,

    -- Net Pay
    net_salary NUMERIC(15,2) NOT NULL,

    -- Tax Calculation Details
    taxable_income NUMERIC(15,2) DEFAULT 0,
    non_taxable_income NUMERIC(15,2) DEFAULT 0,
    ptkp_status VARCHAR(10),
    ptkp_amount NUMERIC(15,2) DEFAULT 0,

    -- Status
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    payment_method VARCHAR(30) DEFAULT 'BANK_TRANSFER',
    payment_status VARCHAR(30) DEFAULT 'PENDING',
    paid_at TIMESTAMP,

    -- Bank Details
    bank_name VARCHAR(100),
    bank_account_number VARCHAR(50),
    bank_account_holder_name VARCHAR(200),

    -- Approval
    verified_by UUID,
    verified_at TIMESTAMP,
    approved_by UUID,
    approved_at TIMESTAMP,

    notes TEXT,

    CONSTRAINT fk_employee_payroll_period FOREIGN KEY (payroll_period_id)
        REFERENCES workforce_schema.payroll_period(id),
    CONSTRAINT fk_employee_payroll_employee FOREIGN KEY (employee_id)
        REFERENCES workforce_schema.employee(id),
    CONSTRAINT uq_employee_payroll_period UNIQUE (payroll_period_id, employee_id)
);

CREATE INDEX IF NOT EXISTS idx_employee_payroll_period ON workforce_schema.employee_payroll(payroll_period_id);
CREATE INDEX IF NOT EXISTS idx_employee_payroll_employee ON workforce_schema.employee_payroll(employee_id);
CREATE INDEX IF NOT EXISTS idx_employee_payroll_status ON workforce_schema.employee_payroll(status);
CREATE INDEX IF NOT EXISTS idx_employee_payroll_payment_status ON workforce_schema.employee_payroll(payment_status);

-- Payroll Detail Items
CREATE TABLE IF NOT EXISTS workforce_schema.payroll_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_payroll_id UUID NOT NULL,
    payroll_component_id UUID NOT NULL,
    component_code VARCHAR(50) NOT NULL,
    component_name VARCHAR(200) NOT NULL,
    component_type VARCHAR(30) NOT NULL,

    calculation_base NUMERIC(15,2),
    rate NUMERIC(8,2),
    quantity NUMERIC(8,2) DEFAULT 1,
    amount NUMERIC(15,2) NOT NULL,

    is_taxable BOOLEAN NOT NULL DEFAULT true,
    is_bpjs_included BOOLEAN NOT NULL DEFAULT true,

    description TEXT,

    CONSTRAINT fk_payroll_item_employee_payroll FOREIGN KEY (employee_payroll_id)
        REFERENCES workforce_schema.employee_payroll(id) ON DELETE CASCADE,
    CONSTRAINT fk_payroll_item_component FOREIGN KEY (payroll_component_id)
        REFERENCES workforce_schema.payroll_component(id)
);

CREATE INDEX IF NOT EXISTS idx_payroll_item_employee_payroll ON workforce_schema.payroll_item(employee_payroll_id);
CREATE INDEX IF NOT EXISTS idx_payroll_item_component ON workforce_schema.payroll_item(payroll_component_id);

-- NOTE: overtime_record table already exists from V40__create_attendance_scheduling_tables.sql
-- The existing table has all needed fields for payroll overtime tracking

-- Employee Loans and Advances
CREATE TABLE IF NOT EXISTS workforce_schema.employee_loan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    loan_number VARCHAR(50) NOT NULL UNIQUE,
    loan_type VARCHAR(30) NOT NULL,

    loan_amount NUMERIC(15,2) NOT NULL,
    interest_rate NUMERIC(5,2) DEFAULT 0,
    total_amount NUMERIC(15,2) NOT NULL,

    installment_count INTEGER NOT NULL,
    installment_amount NUMERIC(15,2) NOT NULL,
    paid_installments INTEGER DEFAULT 0,
    remaining_installments INTEGER,

    outstanding_amount NUMERIC(15,2),
    paid_amount NUMERIC(15,2) DEFAULT 0,

    start_date DATE NOT NULL,
    first_deduction_period_id UUID,

    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    approved_by UUID,
    approved_at TIMESTAMP,

    purpose TEXT,
    notes TEXT,

    CONSTRAINT fk_loan_employee FOREIGN KEY (employee_id)
        REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_loan_first_period FOREIGN KEY (first_deduction_period_id)
        REFERENCES workforce_schema.payroll_period(id),
    CONSTRAINT chk_loan_type CHECK (loan_type IN (
        'EMPLOYEE_LOAN', 'SALARY_ADVANCE', 'EMERGENCY_LOAN', 'OTHER'
    ))
);

CREATE INDEX IF NOT EXISTS idx_loan_employee ON workforce_schema.employee_loan(employee_id);
CREATE INDEX IF NOT EXISTS idx_loan_status ON workforce_schema.employee_loan(status);

-- Loan Deduction History
CREATE TABLE IF NOT EXISTS workforce_schema.loan_deduction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_loan_id UUID NOT NULL,
    employee_payroll_id UUID NOT NULL,
    payroll_period_id UUID NOT NULL,

    installment_number INTEGER NOT NULL,
    deduction_amount NUMERIC(15,2) NOT NULL,
    principal_amount NUMERIC(15,2) NOT NULL,
    interest_amount NUMERIC(15,2) DEFAULT 0,

    outstanding_before NUMERIC(15,2),
    outstanding_after NUMERIC(15,2),

    CONSTRAINT fk_loan_deduction_loan FOREIGN KEY (employee_loan_id)
        REFERENCES workforce_schema.employee_loan(id),
    CONSTRAINT fk_loan_deduction_payroll FOREIGN KEY (employee_payroll_id)
        REFERENCES workforce_schema.employee_payroll(id),
    CONSTRAINT fk_loan_deduction_period FOREIGN KEY (payroll_period_id)
        REFERENCES workforce_schema.payroll_period(id)
);

CREATE INDEX IF NOT EXISTS idx_loan_deduction_loan ON workforce_schema.loan_deduction(employee_loan_id);
CREATE INDEX IF NOT EXISTS idx_loan_deduction_payroll ON workforce_schema.loan_deduction(employee_payroll_id);

-- PPh 21 Tax Calculation Details
CREATE TABLE IF NOT EXISTS workforce_schema.tax_calculation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_payroll_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    payroll_period_id UUID NOT NULL,

    -- Income
    gross_income NUMERIC(15,2) NOT NULL,
    non_taxable_income NUMERIC(15,2) DEFAULT 0,
    taxable_income NUMERIC(15,2) NOT NULL,

    -- PTKP (Penghasilan Tidak Kena Pajak)
    ptkp_status VARCHAR(10) NOT NULL,
    ptkp_amount NUMERIC(15,2) NOT NULL,

    -- PKP (Penghasilan Kena Pajak)
    pkp_annual NUMERIC(15,2),
    pkp_monthly NUMERIC(15,2),

    -- Tax Brackets
    tax_bracket_1 NUMERIC(15,2) DEFAULT 0,
    tax_bracket_2 NUMERIC(15,2) DEFAULT 0,
    tax_bracket_3 NUMERIC(15,2) DEFAULT 0,
    tax_bracket_4 NUMERIC(15,2) DEFAULT 0,
    tax_bracket_5 NUMERIC(15,2) DEFAULT 0,

    -- Tax Amounts
    tax_amount_bracket_1 NUMERIC(15,2) DEFAULT 0,
    tax_amount_bracket_2 NUMERIC(15,2) DEFAULT 0,
    tax_amount_bracket_3 NUMERIC(15,2) DEFAULT 0,
    tax_amount_bracket_4 NUMERIC(15,2) DEFAULT 0,
    tax_amount_bracket_5 NUMERIC(15,2) DEFAULT 0,

    total_annual_tax NUMERIC(15,2),
    monthly_tax NUMERIC(15,2),

    -- YTD (Year to Date)
    ytd_gross_income NUMERIC(15,2),
    ytd_tax_paid NUMERIC(15,2),

    calculation_method VARCHAR(30) NOT NULL,
    calculation_details JSONB,

    CONSTRAINT fk_tax_employee_payroll FOREIGN KEY (employee_payroll_id)
        REFERENCES workforce_schema.employee_payroll(id),
    CONSTRAINT fk_tax_employee FOREIGN KEY (employee_id)
        REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_tax_period FOREIGN KEY (payroll_period_id)
        REFERENCES workforce_schema.payroll_period(id),
    CONSTRAINT chk_ptkp_status CHECK (ptkp_status IN (
        'TK/0', 'TK/1', 'TK/2', 'TK/3',
        'K/0', 'K/1', 'K/2', 'K/3',
        'K/I/0', 'K/I/1', 'K/I/2', 'K/I/3'
    ))
);

CREATE INDEX IF NOT EXISTS idx_tax_employee_payroll ON workforce_schema.tax_calculation(employee_payroll_id);
CREATE INDEX IF NOT EXISTS idx_tax_employee ON workforce_schema.tax_calculation(employee_id);
CREATE INDEX IF NOT EXISTS idx_tax_period ON workforce_schema.tax_calculation(payroll_period_id);

-- Salary Slip Records
CREATE TABLE IF NOT EXISTS workforce_schema.salary_slip (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_payroll_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    payroll_period_id UUID NOT NULL,

    slip_number VARCHAR(50) NOT NULL UNIQUE,
    slip_date DATE NOT NULL,

    file_path VARCHAR(500),
    file_url VARCHAR(500),
    file_type VARCHAR(20) DEFAULT 'PDF',

    is_sent BOOLEAN NOT NULL DEFAULT false,
    sent_at TIMESTAMP,
    sent_via VARCHAR(30),
    sent_to VARCHAR(200),

    is_downloaded BOOLEAN NOT NULL DEFAULT false,
    download_count INTEGER DEFAULT 0,
    last_downloaded_at TIMESTAMP,

    CONSTRAINT fk_slip_employee_payroll FOREIGN KEY (employee_payroll_id)
        REFERENCES workforce_schema.employee_payroll(id),
    CONSTRAINT fk_slip_employee FOREIGN KEY (employee_id)
        REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_slip_period FOREIGN KEY (payroll_period_id)
        REFERENCES workforce_schema.payroll_period(id)
);

CREATE INDEX IF NOT EXISTS idx_slip_employee_payroll ON workforce_schema.salary_slip(employee_payroll_id);
CREATE INDEX IF NOT EXISTS idx_slip_employee ON workforce_schema.salary_slip(employee_id);
CREATE INDEX IF NOT EXISTS idx_slip_period ON workforce_schema.salary_slip(payroll_period_id);

-- Bank Transfer Batch
CREATE TABLE IF NOT EXISTS workforce_schema.bank_transfer_batch (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    payroll_period_id UUID NOT NULL,
    batch_number VARCHAR(50) NOT NULL UNIQUE,
    batch_date DATE NOT NULL,

    total_employees INTEGER NOT NULL,
    total_amount NUMERIC(15,2) NOT NULL,

    file_format VARCHAR(30) NOT NULL,
    file_path VARCHAR(500),
    file_url VARCHAR(500),

    status VARCHAR(30) NOT NULL DEFAULT 'GENERATED',
    processed_at TIMESTAMP,
    processed_by UUID,

    bank_name VARCHAR(100),
    bank_code VARCHAR(20),
    company_account_number VARCHAR(50),
    company_account_name VARCHAR(200),

    notes TEXT,

    CONSTRAINT fk_transfer_batch_period FOREIGN KEY (payroll_period_id)
        REFERENCES workforce_schema.payroll_period(id),
    CONSTRAINT chk_file_format CHECK (file_format IN (
        'BCA_TXT', 'MANDIRI_TXT', 'BNI_TXT', 'BRI_TXT', 'PERMATA_TXT', 'CSV', 'EXCEL'
    ))
);

CREATE INDEX IF NOT EXISTS idx_transfer_batch_period ON workforce_schema.bank_transfer_batch(payroll_period_id);
CREATE INDEX IF NOT EXISTS idx_transfer_batch_status ON workforce_schema.bank_transfer_batch(status);

-- Bank Transfer Items
CREATE TABLE IF NOT EXISTS workforce_schema.bank_transfer_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    bank_transfer_batch_id UUID NOT NULL,
    employee_payroll_id UUID NOT NULL,
    employee_id UUID NOT NULL,

    sequence_number INTEGER NOT NULL,
    employee_number VARCHAR(50),
    employee_name VARCHAR(200) NOT NULL,

    bank_code VARCHAR(20),
    bank_name VARCHAR(100),
    account_number VARCHAR(50) NOT NULL,
    account_holder_name VARCHAR(200) NOT NULL,

    transfer_amount NUMERIC(15,2) NOT NULL,

    status VARCHAR(30) DEFAULT 'PENDING',
    transfer_date DATE,
    reference_number VARCHAR(100),

    error_message TEXT,

    CONSTRAINT fk_transfer_item_batch FOREIGN KEY (bank_transfer_batch_id)
        REFERENCES workforce_schema.bank_transfer_batch(id) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_item_payroll FOREIGN KEY (employee_payroll_id)
        REFERENCES workforce_schema.employee_payroll(id),
    CONSTRAINT fk_transfer_item_employee FOREIGN KEY (employee_id)
        REFERENCES workforce_schema.employee(id)
);

CREATE INDEX IF NOT EXISTS idx_transfer_item_batch ON workforce_schema.bank_transfer_item(bank_transfer_batch_id);
CREATE INDEX IF NOT EXISTS idx_transfer_item_payroll ON workforce_schema.bank_transfer_item(employee_payroll_id);
CREATE INDEX IF NOT EXISTS idx_transfer_item_employee ON workforce_schema.bank_transfer_item(employee_id);

-- Comments
COMMENT ON TABLE workforce_schema.payroll_period IS 'Monthly payroll processing periods';
COMMENT ON TABLE workforce_schema.payroll_component IS 'Master data for salary components (allowances, deductions)';
COMMENT ON TABLE workforce_schema.employee_payroll IS 'Main payroll records for employees per period';
COMMENT ON TABLE workforce_schema.payroll_item IS 'Detailed line items for each payroll component';
COMMENT ON TABLE workforce_schema.overtime_record IS 'Overtime hours tracking with Indonesian labor law multipliers';
COMMENT ON TABLE workforce_schema.employee_loan IS 'Employee loans and salary advances';
COMMENT ON TABLE workforce_schema.loan_deduction IS 'Loan installment deduction history';
COMMENT ON TABLE workforce_schema.tax_calculation IS 'PPh 21 (Indonesian income tax) calculation details';
COMMENT ON TABLE workforce_schema.salary_slip IS 'Generated salary slip records';
COMMENT ON TABLE workforce_schema.bank_transfer_batch IS 'Bank transfer batch files for salary payments';
COMMENT ON TABLE workforce_schema.bank_transfer_item IS 'Individual transfer items in a batch';
