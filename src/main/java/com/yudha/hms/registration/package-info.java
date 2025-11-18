/**
 * Registration Module
 *
 * This module handles patient registration workflows including:
 * - Outpatient (Rawat Jalan) registration
 * - Inpatient (Rawat Inap) admission
 * - Emergency room (IGD) registration
 * - Appointment scheduling and management
 * - Queue management
 * - Bed assignment and management
 * - Discharge processing
 * - Transfer between wards/departments
 *
 * Indonesian Specific Features:
 * - BPJS patient registration (VClaim integration)
 * - SEP (Surat Eligibilitas Peserta) creation
 * - PRB (Program Rujuk Balik) patient handling
 * - BPJS claim type selection
 * - Indonesian patient classification (BPJS/Umum/Asuransi Lain)
 *
 * Registration Types:
 * - OUTPATIENT: Rawat Jalan (polyclinic visits)
 * - INPATIENT: Rawat Inap (hospital admission)
 * - EMERGENCY: IGD/ER registration
 * - DAY_CARE: One Day Care procedures
 *
 * Package Structure:
 * - controller: REST API endpoints for registration operations
 * - service: Business logic for registration workflows
 * - repository: Data access layer for registration entities
 * - entity: JPA entities (Registration, Appointment, BedAssignment, etc.)
 * - dto: Data Transfer Objects for API requests/responses
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.registration;