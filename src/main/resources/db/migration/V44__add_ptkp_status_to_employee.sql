-- V44: Add ptkp_status column to employee table for PPh 21 tax calculation
-- PTKP (Penghasilan Tidak Kena Pajak) status determines tax-free income threshold
-- Common values: TK/0 (single, no dependents), K/1 (married, 1 dependent), etc.

ALTER TABLE workforce_schema.employee
ADD COLUMN IF NOT EXISTS ptkp_status VARCHAR(10) DEFAULT 'TK/0';

-- Add comment
COMMENT ON COLUMN workforce_schema.employee.ptkp_status IS 'PTKP tax status code (e.g., TK/0, K/1) for Indonesian PPh 21 tax calculation';
