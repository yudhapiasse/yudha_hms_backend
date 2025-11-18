/**
 * Pharmacy Module
 *
 * This module handles all pharmacy operations including:
 * - Drug inventory management
 * - Electronic prescribing (e-Prescribing)
 * - Drug dispensing workflow
 * - Stock management (FIFO/FEFO)
 * - Expiry date tracking
 * - Drug interaction checking
 * - Formulary management
 * - Prescription validation
 * - Drug usage reports
 * - Controlled substance tracking (Narkotika, Psikotropika)
 *
 * Indonesian Specific Features:
 * - BPJS e-Prescribing format
 * - Fornas (Formularium Nasional) compliance
 * - Narkotika and Psikotropika regulations (UU 35/2009)
 * - Drug pricing based on e-Katalog
 * - Indonesian drug classifications
 * - LPLPO (Laporan Pemakaian dan Lembar Permintaan Obat) reporting
 * - Reporting to Dinas Kesehatan
 *
 * Drug Categories:
 * - GENERIC: Generic medications
 * - BRANDED: Brand name medications
 * - OTC: Over-the-counter medications
 * - NARCOTIC: Narkotika (controlled substances)
 * - PSYCHOTROPIC: Psikotropika (psychotropic substances)
 * - PRECURSOR: Precursor chemicals
 * - HERBAL: Traditional/herbal medicines
 *
 * Package Structure:
 * - controller: REST API endpoints for pharmacy operations
 * - service: Business logic for pharmacy workflows
 * - repository: Data access layer for pharmacy entities
 * - entity: JPA entities (Drug, Prescription, Dispensing, Stock, etc.)
 * - dto: Data Transfer Objects for API requests/responses
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.pharmacy;