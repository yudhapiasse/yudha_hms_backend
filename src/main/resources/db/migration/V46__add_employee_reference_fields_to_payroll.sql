-- V46: Add employee reference fields to employee_payroll table
-- These fields store position and employment type from employee record for reporting

ALTER TABLE workforce_schema.employee_payroll
ADD COLUMN IF NOT EXISTS position_id UUID,
ADD COLUMN IF NOT EXISTS employment_type VARCHAR(20);

-- Add comments
COMMENT ON COLUMN workforce_schema.employee_payroll.position_id IS 'Position ID from employee record for reporting purposes';
COMMENT ON COLUMN workforce_schema.employee_payroll.employment_type IS 'Employment type (PERMANENT, CONTRACT, etc.) from employee record';
