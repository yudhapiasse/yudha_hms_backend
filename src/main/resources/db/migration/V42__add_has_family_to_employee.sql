-- V42: Add has_family column to employee table for BPJS family coverage
-- This field is required for payroll BPJS calculations

ALTER TABLE workforce_schema.employee
ADD COLUMN IF NOT EXISTS has_family BOOLEAN NOT NULL DEFAULT false;

-- Add comment
COMMENT ON COLUMN workforce_schema.employee.has_family IS 'Indicates if employee has family members covered under BPJS';
