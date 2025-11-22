-- V45: Add department_id column to employee_payroll table
-- This field stores the department for reporting and analytics purposes

ALTER TABLE workforce_schema.employee_payroll
ADD COLUMN IF NOT EXISTS department_id UUID;

-- Add comment
COMMENT ON COLUMN workforce_schema.employee_payroll.department_id IS 'Department ID from employee record for reporting purposes';
