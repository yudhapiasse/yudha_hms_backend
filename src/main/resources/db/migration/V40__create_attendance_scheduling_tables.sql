-- V40: Workforce Management - Attendance and Scheduling
-- Phase 12.2: Comprehensive attendance and scheduling for Indonesian hospital
-- Includes Indonesian labor law compliance (UU Ketenagakerjaan)

-- Create workforce schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS workforce_schema;

-- Shift pattern master table
CREATE TABLE IF NOT EXISTS workforce_schema.shift_pattern (
    -- Primary key and audit fields
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Shift identification
    shift_code VARCHAR(50) NOT NULL UNIQUE,
    shift_name VARCHAR(100) NOT NULL, -- Shift Pagi, Shift Siang, Shift Malam, Libur
    shift_name_id VARCHAR(100) NOT NULL, -- Indonesian name
    shift_type VARCHAR(30) NOT NULL, -- MORNING, AFTERNOON, NIGHT, OFF, FLEXIBLE

    -- Shift timing
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_hours DECIMAL(4,2) NOT NULL,
    is_overnight BOOLEAN DEFAULT false, -- If shift spans midnight

    -- Break times
    break_duration_minutes INTEGER DEFAULT 60,
    break_start_time TIME,
    break_end_time TIME,

    -- Effective working hours (excluding breaks)
    effective_hours DECIMAL(4,2) NOT NULL,

    -- Overtime rules
    overtime_threshold_minutes INTEGER, -- Minutes after end_time considered overtime
    overtime_multiplier DECIMAL(4,2) DEFAULT 1.5, -- Indonesian law: 1.5x for first hour
    holiday_multiplier DECIMAL(4,2) DEFAULT 2.0, -- Indonesian law: 2x on holidays

    -- Classification
    is_default BOOLEAN DEFAULT false,
    department_id UUID, -- Department-specific shift (null = hospital-wide)
    color_code VARCHAR(20), -- For UI display

    active BOOLEAN DEFAULT true,
    notes TEXT,

    CONSTRAINT fk_shift_department FOREIGN KEY (department_id) REFERENCES master_schema.department(id)
);

-- Shift group/rotation pattern
CREATE TABLE IF NOT EXISTS workforce_schema.shift_rotation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    rotation_code VARCHAR(50) NOT NULL UNIQUE,
    rotation_name VARCHAR(100) NOT NULL,
    rotation_name_id VARCHAR(100) NOT NULL,

    -- Rotation pattern (e.g., 3 days morning, 2 days afternoon, 2 days night, 1 day off)
    pattern_description TEXT,
    cycle_length_days INTEGER NOT NULL, -- How many days before pattern repeats

    department_id UUID,
    active BOOLEAN DEFAULT true,
    notes TEXT,

    CONSTRAINT fk_rotation_department FOREIGN KEY (department_id) REFERENCES master_schema.department(id)
);

-- Shift rotation details (defines the pattern)
CREATE TABLE IF NOT EXISTS workforce_schema.shift_rotation_detail (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    rotation_id UUID NOT NULL,
    day_sequence INTEGER NOT NULL, -- 1, 2, 3, etc.
    shift_pattern_id UUID NOT NULL,

    notes TEXT,

    CONSTRAINT fk_rotation_detail_rotation FOREIGN KEY (rotation_id) REFERENCES workforce_schema.shift_rotation(id),
    CONSTRAINT fk_rotation_detail_shift FOREIGN KEY (shift_pattern_id) REFERENCES workforce_schema.shift_pattern(id),
    CONSTRAINT uq_rotation_day UNIQUE (rotation_id, day_sequence)
);

-- Employee shift assignment (which rotation/pattern an employee follows)
CREATE TABLE IF NOT EXISTS workforce_schema.employee_shift_assignment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    shift_rotation_id UUID, -- If following a rotation pattern
    fixed_shift_pattern_id UUID, -- If assigned to fixed shift

    effective_from DATE NOT NULL,
    effective_to DATE,

    -- Rotation start reference
    rotation_start_date DATE, -- When employee started this rotation cycle
    current_day_in_cycle INTEGER, -- Current position in rotation

    is_current BOOLEAN DEFAULT true,
    assigned_by UUID,
    notes TEXT,

    CONSTRAINT fk_emp_shift_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_emp_shift_rotation FOREIGN KEY (shift_rotation_id) REFERENCES workforce_schema.shift_rotation(id),
    CONSTRAINT fk_emp_shift_pattern FOREIGN KEY (fixed_shift_pattern_id) REFERENCES workforce_schema.shift_pattern(id)
);

-- Duty roster (actual schedule for each employee per day)
CREATE TABLE IF NOT EXISTS workforce_schema.duty_roster (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    department_id UUID NOT NULL,

    roster_date DATE NOT NULL,
    shift_pattern_id UUID NOT NULL,

    -- Actual times (may differ from shift pattern due to adjustments)
    scheduled_start_time TIME NOT NULL,
    scheduled_end_time TIME NOT NULL,
    scheduled_hours DECIMAL(4,2) NOT NULL,

    -- Status
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, CONFIRMED, CANCELLED, COMPLETED

    -- Location/assignment
    assigned_location VARCHAR(200), -- Ward, Department, Clinic, etc.
    assigned_role VARCHAR(100), -- Special role for this roster

    -- Approval
    approved BOOLEAN DEFAULT false,
    approved_by UUID,
    approved_at TIMESTAMP,

    -- Publishing
    published BOOLEAN DEFAULT false, -- Published to employee
    published_at TIMESTAMP,

    notes TEXT,

    CONSTRAINT fk_roster_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_roster_department FOREIGN KEY (department_id) REFERENCES master_schema.department(id),
    CONSTRAINT fk_roster_shift FOREIGN KEY (shift_pattern_id) REFERENCES workforce_schema.shift_pattern(id),
    CONSTRAINT uq_employee_date_shift UNIQUE (employee_id, roster_date, shift_pattern_id)
);

-- Public holidays (Indonesian holidays)
CREATE TABLE IF NOT EXISTS workforce_schema.public_holiday (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    holiday_date DATE NOT NULL UNIQUE,
    holiday_name VARCHAR(200) NOT NULL,
    holiday_name_id VARCHAR(200) NOT NULL, -- Indonesian name

    -- Types: NATIONAL, RELIGIOUS, REGIONAL, JOINT_LEAVE (cuti bersama)
    holiday_type VARCHAR(30) NOT NULL,

    -- Religious classification
    religion VARCHAR(30), -- For religious holidays

    -- Regional
    is_national BOOLEAN DEFAULT true,
    province VARCHAR(100), -- If regional holiday
    city VARCHAR(100),

    -- Leave policy
    is_paid_leave BOOLEAN DEFAULT true,
    requires_compensation BOOLEAN DEFAULT false, -- If employee works, needs compensation
    compensation_multiplier DECIMAL(4,2) DEFAULT 2.0, -- Indonesian law

    -- Replacement date (for joint leave that replaces Saturday/Sunday)
    is_substitute_holiday BOOLEAN DEFAULT false,
    original_date DATE,

    active BOOLEAN DEFAULT true,
    year INTEGER NOT NULL,
    notes TEXT
);

-- Attendance records
CREATE TABLE IF NOT EXISTS workforce_schema.attendance_record (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    duty_roster_id UUID, -- Link to scheduled roster

    attendance_date DATE NOT NULL,
    department_id UUID NOT NULL,

    -- Check-in
    check_in_time TIMESTAMP,
    check_in_method VARCHAR(30), -- FINGERPRINT, FACE_RECOGNITION, MANUAL, RFID, MOBILE_APP
    check_in_device_id VARCHAR(100),
    check_in_location VARCHAR(200),
    check_in_latitude DECIMAL(10, 8),
    check_in_longitude DECIMAL(11, 8),
    check_in_photo_url VARCHAR(500),

    -- Check-out
    check_out_time TIMESTAMP,
    check_out_method VARCHAR(30),
    check_out_device_id VARCHAR(100),
    check_out_location VARCHAR(200),
    check_out_latitude DECIMAL(10, 8),
    check_out_longitude DECIMAL(11, 8),
    check_out_photo_url VARCHAR(500),

    -- Calculated durations
    working_hours DECIMAL(5,2),
    break_hours DECIMAL(4,2),
    effective_hours DECIMAL(5,2),

    -- Overtime
    overtime_hours DECIMAL(5,2) DEFAULT 0,
    overtime_approved BOOLEAN DEFAULT false,
    overtime_approved_by UUID,
    overtime_approved_at TIMESTAMP,

    -- Status
    attendance_status VARCHAR(30) NOT NULL, -- PRESENT, LATE, EARLY_LEAVE, ABSENT, LEAVE, SICK, HOLIDAY, OFF_DUTY
    is_late BOOLEAN DEFAULT false,
    late_minutes INTEGER DEFAULT 0,
    is_early_leave BOOLEAN DEFAULT false,
    early_leave_minutes INTEGER DEFAULT 0,

    -- Validation
    is_valid BOOLEAN DEFAULT true,
    validation_notes TEXT,
    validated_by UUID,
    validated_at TIMESTAMP,

    -- Special cases
    is_holiday_work BOOLEAN DEFAULT false,
    is_weekend_work BOOLEAN DEFAULT false,

    notes TEXT,

    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_attendance_roster FOREIGN KEY (duty_roster_id) REFERENCES workforce_schema.duty_roster(id),
    CONSTRAINT fk_attendance_department FOREIGN KEY (department_id) REFERENCES master_schema.department(id)
);

-- Leave types master
CREATE TABLE IF NOT EXISTS workforce_schema.leave_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    leave_code VARCHAR(50) NOT NULL UNIQUE,
    leave_name VARCHAR(100) NOT NULL,
    leave_name_id VARCHAR(100) NOT NULL, -- Cuti Tahunan, Cuti Sakit, Cuti Melahirkan, etc.

    -- Indonesian leave types: ANNUAL, SICK, MATERNITY, PATERNITY, MARRIAGE,
    -- BEREAVEMENT, HAJJ, STUDY, UNPAID, COMPASSIONATE
    leave_category VARCHAR(30) NOT NULL,

    -- Entitlement
    is_paid BOOLEAN DEFAULT true,
    requires_approval BOOLEAN DEFAULT true,
    max_days_per_year INTEGER,
    max_consecutive_days INTEGER,

    -- Documentation requirements
    requires_medical_certificate BOOLEAN DEFAULT false,
    medical_cert_after_days INTEGER, -- Require cert after X days
    requires_attachment BOOLEAN DEFAULT false,

    -- Indonesian labor law compliance
    is_statutory BOOLEAN DEFAULT false, -- Required by law
    legal_reference TEXT, -- Reference to UU Ketenagakerjaan

    -- Carry forward rules
    can_carry_forward BOOLEAN DEFAULT false,
    carry_forward_max_days INTEGER,
    carry_forward_expiry_months INTEGER,

    -- Accrual
    accrual_method VARCHAR(30), -- YEARLY, MONTHLY, WORKED_DAYS
    accrual_rate DECIMAL(5,2), -- Days per month/year

    -- Gender specific
    gender_specific VARCHAR(10), -- MALE, FEMALE, ALL

    -- Waiting period
    min_service_months INTEGER DEFAULT 0, -- Months of service required

    active BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0,
    notes TEXT
);

-- Leave balance tracking
CREATE TABLE IF NOT EXISTS workforce_schema.leave_balance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    leave_type_id UUID NOT NULL,

    balance_year INTEGER NOT NULL,

    -- Balance tracking
    opening_balance DECIMAL(5,2) DEFAULT 0, -- From previous year carry forward
    accrued_days DECIMAL(5,2) DEFAULT 0, -- Earned this year
    adjustment_days DECIMAL(5,2) DEFAULT 0, -- Manual adjustments
    taken_days DECIMAL(5,2) DEFAULT 0, -- Leave taken
    pending_days DECIMAL(5,2) DEFAULT 0, -- Pending approval
    available_days DECIMAL(5,2) DEFAULT 0, -- Current available

    -- Carry forward
    carried_forward_from_previous DECIMAL(5,2) DEFAULT 0,
    carry_forward_expiry_date DATE,

    -- Calculation
    last_calculated_at TIMESTAMP,

    notes TEXT,

    CONSTRAINT fk_balance_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_balance_leave_type FOREIGN KEY (leave_type_id) REFERENCES workforce_schema.leave_type(id),
    CONSTRAINT uq_employee_leave_year UNIQUE (employee_id, leave_type_id, balance_year)
);

-- Leave requests
CREATE TABLE IF NOT EXISTS workforce_schema.leave_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    request_number VARCHAR(50) NOT NULL UNIQUE,
    employee_id UUID NOT NULL,
    leave_type_id UUID NOT NULL,

    -- Leave period
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days DECIMAL(4,1) NOT NULL, -- Can be half-day (0.5)

    -- Reason
    reason TEXT NOT NULL,
    emergency_contact_during_leave VARCHAR(200),
    emergency_phone_during_leave VARCHAR(20),

    -- Documentation
    medical_certificate_url VARCHAR(500),
    supporting_document_url VARCHAR(500),

    -- Approval workflow
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, CANCELLED, WITHDRAWN

    -- Multi-level approval
    immediate_supervisor_id UUID,
    immediate_supervisor_status VARCHAR(30), -- PENDING, APPROVED, REJECTED
    immediate_supervisor_comments TEXT,
    immediate_supervisor_action_at TIMESTAMP,

    hrd_approver_id UUID,
    hrd_status VARCHAR(30),
    hrd_comments TEXT,
    hrd_action_at TIMESTAMP,

    final_approver_id UUID,
    final_status VARCHAR(30),
    final_comments TEXT,
    final_action_at TIMESTAMP,

    -- Rejection
    rejection_reason TEXT,

    -- Substitute coverage
    substitute_employee_id UUID,
    substitute_confirmed BOOLEAN DEFAULT false,
    substitute_notes TEXT,

    -- Balance verification
    balance_before_request DECIMAL(5,2),
    balance_after_request DECIMAL(5,2),

    -- Actual leave taken (may differ from request)
    actual_start_date DATE,
    actual_end_date DATE,
    actual_days_taken DECIMAL(4,1),

    -- Cancellation
    cancelled_by UUID,
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP,

    notes TEXT,

    CONSTRAINT fk_leave_req_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_leave_req_type FOREIGN KEY (leave_type_id) REFERENCES workforce_schema.leave_type(id),
    CONSTRAINT fk_leave_req_substitute FOREIGN KEY (substitute_employee_id) REFERENCES workforce_schema.employee(id)
);

-- On-call schedule (for doctors and critical staff)
CREATE TABLE IF NOT EXISTS workforce_schema.on_call_schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    department_id UUID NOT NULL,

    -- Schedule
    on_call_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    -- Type
    on_call_type VARCHAR(30) NOT NULL, -- PRIMARY, BACKUP, TERTIARY
    specialization VARCHAR(100), -- Medical specialization if applicable

    -- Response requirements
    required_response_time_minutes INTEGER, -- How fast must respond to call
    must_be_on_premises BOOLEAN DEFAULT false,

    -- Compensation
    on_call_rate DECIMAL(15,2), -- Payment rate for on-call
    call_out_rate DECIMAL(15,2), -- Rate if actually called in
    minimum_call_out_hours DECIMAL(4,2), -- Minimum billable hours if called

    -- Status
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, ACTIVE, COMPLETED, CANCELLED

    -- Call tracking
    was_called_out BOOLEAN DEFAULT false,
    call_out_time TIMESTAMP,
    call_out_reason TEXT,
    response_time_minutes INTEGER,

    -- Completion
    actual_end_time TIMESTAMP,
    total_on_call_hours DECIMAL(5,2),
    total_call_out_hours DECIMAL(5,2),

    notes TEXT,

    CONSTRAINT fk_oncall_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_oncall_department FOREIGN KEY (department_id) REFERENCES master_schema.department(id)
);

-- Shift substitution/replacement
CREATE TABLE IF NOT EXISTS workforce_schema.shift_substitution (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    request_number VARCHAR(50) NOT NULL UNIQUE,

    -- Original assignment
    original_roster_id UUID NOT NULL,
    original_employee_id UUID NOT NULL,
    roster_date DATE NOT NULL,
    shift_pattern_id UUID NOT NULL,

    -- Substitute
    substitute_employee_id UUID NOT NULL,

    -- Request details
    substitution_reason VARCHAR(30) NOT NULL, -- SICK, EMERGENCY, LEAVE, SWAP, OTHER
    reason_details TEXT,
    request_type VARCHAR(30) NOT NULL, -- REPLACEMENT, SWAP

    -- If SWAP, the reciprocal arrangement
    swap_roster_id UUID, -- The roster being swapped

    -- Approval
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, COMPLETED, CANCELLED

    requested_by UUID,
    approved_by UUID,
    approved_at TIMESTAMP,
    approval_comments TEXT,

    rejection_reason TEXT,

    -- Confirmation
    substitute_confirmed BOOLEAN DEFAULT false,
    substitute_confirmed_at TIMESTAMP,

    -- Completion
    completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMP,
    completion_notes TEXT,

    notes TEXT,

    CONSTRAINT fk_substitution_roster FOREIGN KEY (original_roster_id) REFERENCES workforce_schema.duty_roster(id),
    CONSTRAINT fk_substitution_employee FOREIGN KEY (original_employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_substitution_substitute FOREIGN KEY (substitute_employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_substitution_shift FOREIGN KEY (shift_pattern_id) REFERENCES workforce_schema.shift_pattern(id),
    CONSTRAINT fk_substitution_swap_roster FOREIGN KEY (swap_roster_id) REFERENCES workforce_schema.duty_roster(id)
);

-- Overtime records
CREATE TABLE IF NOT EXISTS workforce_schema.overtime_record (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    overtime_number VARCHAR(50) NOT NULL UNIQUE,
    employee_id UUID NOT NULL,
    attendance_record_id UUID, -- Link to attendance if applicable

    overtime_date DATE NOT NULL,
    department_id UUID NOT NULL,

    -- Overtime period
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,

    -- Duration
    total_hours DECIMAL(5,2) NOT NULL,
    break_hours DECIMAL(4,2) DEFAULT 0,
    effective_overtime_hours DECIMAL(5,2) NOT NULL,

    -- Classification (Indonesian labor law)
    overtime_type VARCHAR(30) NOT NULL, -- WEEKDAY, WEEKEND, HOLIDAY, AFTER_HOURS

    -- Compensation calculation
    base_rate DECIMAL(15,2),
    overtime_multiplier DECIMAL(4,2), -- 1.5x first hour, 2x subsequent (weekday)
    total_overtime_pay DECIMAL(15,2),

    -- Reason
    overtime_reason TEXT NOT NULL,
    work_description TEXT,
    project_code VARCHAR(50),

    -- Approval workflow
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, PAID

    requested_by UUID,
    supervisor_approved BOOLEAN DEFAULT false,
    supervisor_id UUID,
    supervisor_comments TEXT,
    supervisor_approved_at TIMESTAMP,

    hrd_approved BOOLEAN DEFAULT false,
    hrd_approver_id UUID,
    hrd_comments TEXT,
    hrd_approved_at TIMESTAMP,

    finance_approved BOOLEAN DEFAULT false,
    finance_approver_id UUID,
    finance_approved_at TIMESTAMP,

    rejection_reason TEXT,

    -- Payment
    paid BOOLEAN DEFAULT false,
    payment_date DATE,
    payment_reference VARCHAR(100),

    -- Indonesian labor law limits
    exceeds_daily_limit BOOLEAN DEFAULT false, -- Max 3 hours/day
    exceeds_weekly_limit BOOLEAN DEFAULT false, -- Max 14 hours/week
    compliance_notes TEXT,

    notes TEXT,

    CONSTRAINT fk_overtime_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_overtime_attendance FOREIGN KEY (attendance_record_id) REFERENCES workforce_schema.attendance_record(id),
    CONSTRAINT fk_overtime_department FOREIGN KEY (department_id) REFERENCES master_schema.department(id)
);

-- Attendance summary (monthly aggregation for reporting)
CREATE TABLE IF NOT EXISTS workforce_schema.attendance_summary (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    employee_id UUID NOT NULL,
    department_id UUID NOT NULL,

    summary_year INTEGER NOT NULL,
    summary_month INTEGER NOT NULL,

    -- Attendance statistics
    total_working_days INTEGER NOT NULL,
    days_present INTEGER DEFAULT 0,
    days_absent INTEGER DEFAULT 0,
    days_late INTEGER DEFAULT 0,
    days_early_leave INTEGER DEFAULT 0,
    days_on_leave INTEGER DEFAULT 0,
    days_sick INTEGER DEFAULT 0,
    days_holiday INTEGER DEFAULT 0,
    days_off INTEGER DEFAULT 0,

    -- Hours
    total_scheduled_hours DECIMAL(7,2) DEFAULT 0,
    total_actual_hours DECIMAL(7,2) DEFAULT 0,
    total_overtime_hours DECIMAL(7,2) DEFAULT 0,
    total_late_minutes INTEGER DEFAULT 0,
    total_early_leave_minutes INTEGER DEFAULT 0,

    -- Percentages
    attendance_rate DECIMAL(5,2), -- Percentage
    punctuality_rate DECIMAL(5,2), -- Percentage

    -- Leave summary
    annual_leave_taken DECIMAL(4,1) DEFAULT 0,
    sick_leave_taken DECIMAL(4,1) DEFAULT 0,
    other_leave_taken DECIMAL(4,1) DEFAULT 0,

    -- Calculated
    calculated_at TIMESTAMP,
    finalized BOOLEAN DEFAULT false,
    finalized_by UUID,
    finalized_at TIMESTAMP,

    notes TEXT,

    CONSTRAINT fk_summary_employee FOREIGN KEY (employee_id) REFERENCES workforce_schema.employee(id),
    CONSTRAINT fk_summary_department FOREIGN KEY (department_id) REFERENCES master_schema.department(id),
    CONSTRAINT uq_employee_month_year UNIQUE (employee_id, summary_year, summary_month)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_shift_pattern_code ON workforce_schema.shift_pattern(shift_code);
CREATE INDEX IF NOT EXISTS idx_shift_pattern_type ON workforce_schema.shift_pattern(shift_type);
CREATE INDEX IF NOT EXISTS idx_shift_pattern_dept ON workforce_schema.shift_pattern(department_id);

CREATE INDEX IF NOT EXISTS idx_roster_employee ON workforce_schema.duty_roster(employee_id);
CREATE INDEX IF NOT EXISTS idx_roster_date ON workforce_schema.duty_roster(roster_date);
CREATE INDEX IF NOT EXISTS idx_roster_dept ON workforce_schema.duty_roster(department_id);
CREATE INDEX IF NOT EXISTS idx_roster_status ON workforce_schema.duty_roster(status);
CREATE INDEX IF NOT EXISTS idx_roster_employee_date ON workforce_schema.duty_roster(employee_id, roster_date);

CREATE INDEX IF NOT EXISTS idx_holiday_date ON workforce_schema.public_holiday(holiday_date);
CREATE INDEX IF NOT EXISTS idx_holiday_year ON workforce_schema.public_holiday(year);
CREATE INDEX IF NOT EXISTS idx_holiday_type ON workforce_schema.public_holiday(holiday_type);

CREATE INDEX IF NOT EXISTS idx_attendance_employee ON workforce_schema.attendance_record(employee_id);
CREATE INDEX IF NOT EXISTS idx_attendance_date ON workforce_schema.attendance_record(attendance_date);
CREATE INDEX IF NOT EXISTS idx_attendance_status ON workforce_schema.attendance_record(attendance_status);
CREATE INDEX IF NOT EXISTS idx_attendance_employee_date ON workforce_schema.attendance_record(employee_id, attendance_date);

CREATE INDEX IF NOT EXISTS idx_leave_type_code ON workforce_schema.leave_type(leave_code);
CREATE INDEX IF NOT EXISTS idx_leave_type_category ON workforce_schema.leave_type(leave_category);

CREATE INDEX IF NOT EXISTS idx_leave_balance_employee ON workforce_schema.leave_balance(employee_id);
CREATE INDEX IF NOT EXISTS idx_leave_balance_year ON workforce_schema.leave_balance(balance_year);

CREATE INDEX IF NOT EXISTS idx_leave_request_number ON workforce_schema.leave_request(request_number);
CREATE INDEX IF NOT EXISTS idx_leave_request_employee ON workforce_schema.leave_request(employee_id);
CREATE INDEX IF NOT EXISTS idx_leave_request_status ON workforce_schema.leave_request(status);
CREATE INDEX IF NOT EXISTS idx_leave_request_dates ON workforce_schema.leave_request(start_date, end_date);

CREATE INDEX IF NOT EXISTS idx_oncall_employee ON workforce_schema.on_call_schedule(employee_id);
CREATE INDEX IF NOT EXISTS idx_oncall_date ON workforce_schema.on_call_schedule(on_call_date);
CREATE INDEX IF NOT EXISTS idx_oncall_dept ON workforce_schema.on_call_schedule(department_id);

CREATE INDEX IF NOT EXISTS idx_substitution_number ON workforce_schema.shift_substitution(request_number);
CREATE INDEX IF NOT EXISTS idx_substitution_original_emp ON workforce_schema.shift_substitution(original_employee_id);
CREATE INDEX IF NOT EXISTS idx_substitution_substitute_emp ON workforce_schema.shift_substitution(substitute_employee_id);
CREATE INDEX IF NOT EXISTS idx_substitution_status ON workforce_schema.shift_substitution(status);

CREATE INDEX IF NOT EXISTS idx_overtime_number ON workforce_schema.overtime_record(overtime_number);
CREATE INDEX IF NOT EXISTS idx_overtime_employee ON workforce_schema.overtime_record(employee_id);
CREATE INDEX IF NOT EXISTS idx_overtime_date ON workforce_schema.overtime_record(overtime_date);
CREATE INDEX IF NOT EXISTS idx_overtime_status ON workforce_schema.overtime_record(status);

CREATE INDEX IF NOT EXISTS idx_summary_employee ON workforce_schema.attendance_summary(employee_id);
CREATE INDEX IF NOT EXISTS idx_summary_year_month ON workforce_schema.attendance_summary(summary_year, summary_month);
CREATE INDEX IF NOT EXISTS idx_summary_dept ON workforce_schema.attendance_summary(department_id);