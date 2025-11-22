-- Cleanup script to drop partial V41 payroll tables
-- Run this before re-executing V41 migration

-- Drop tables in reverse order (child tables first to avoid FK constraints)
DROP TABLE IF EXISTS workforce_schema.bank_transfer_item CASCADE;
DROP TABLE IF EXISTS workforce_schema.bank_transfer_batch CASCADE;
DROP TABLE IF EXISTS workforce_schema.salary_slip CASCADE;
DROP TABLE IF EXISTS workforce_schema.tax_calculation CASCADE;
DROP TABLE IF EXISTS workforce_schema.loan_deduction CASCADE;
DROP TABLE IF EXISTS workforce_schema.employee_loan CASCADE;
DROP TABLE IF EXISTS workforce_schema.overtime_record CASCADE;
DROP TABLE IF EXISTS workforce_schema.payroll_item CASCADE;
DROP TABLE IF EXISTS workforce_schema.employee_payroll CASCADE;
DROP TABLE IF EXISTS workforce_schema.payroll_component CASCADE;
DROP TABLE IF EXISTS workforce_schema.payroll_period CASCADE;

-- Also remove the failed V41 entry from Flyway schema history (if exists)
DELETE FROM flyway_schema_history WHERE version = '41' AND success = false;

SELECT 'Payroll tables cleanup completed' AS status;
