-- V43: Add number_of_dependents column to employee table for PTKP status calculation
-- This field is required for PPh 21 tax calculations based on number of dependents

ALTER TABLE workforce_schema.employee
ADD COLUMN IF NOT EXISTS number_of_dependents INTEGER DEFAULT 0;

-- Add comment
COMMENT ON COLUMN workforce_schema.employee.number_of_dependents IS 'Number of tax-dependent family members for PTKP calculation';
