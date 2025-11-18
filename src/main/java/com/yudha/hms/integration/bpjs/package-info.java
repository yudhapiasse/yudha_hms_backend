/**
 * BPJS Integration Module
 *
 * This module handles integration with BPJS Kesehatan (Badan Penyelenggara Jaminan Sosial Kesehatan)
 * Indonesia's national health insurance system.
 *
 * Main Integration Points:
 * - VClaim Web Service integration
 * - Applicare (Monitoring Klaim) integration
 * - P-Care (Primary Care) integration (for Faskes 1)
 * - Mobile JKN integration
 *
 * VClaim Services:
 * - Participant eligibility verification (cek peserta)
 * - SEP (Surat Eligibilitas Peserta) creation, update, deletion
 * - Rujukan (referral) management
 * - PRB (Program Rujuk Balik) patient handling
 * - Claim submission (klaim individual)
 * - Claim monitoring and finalization
 * - Finger print validation
 * - LPK (Lembar Pengajuan Klaim) generation
 *
 * SEP Management:
 * - SEP creation for new visits
 * - SEP update (diagnosis, procedures)
 * - SEP deletion (same-day cancellation)
 * - SEP inquiry
 * - Internal/external referral SEP
 * - Emergency SEP
 *
 * Claim Types:
 * - RAWAT_JALAN: Outpatient claims (RI 1)
 * - RAWAT_INAP: Inpatient claims (RI 2)
 * - ONE_DAY_CARE: Same-day procedure
 *
 * INA-CBGs Integration:
 * - Grouper calculation
 * - Tariff determination
 * - Special CMG (Case Mix Groups)
 * - Top-up handling for upgraded services
 *
 * Indonesian Regulatory Compliance:
 * - Perpres 82/2018 (BPJS Kesehatan)
 * - Permenkes regarding claim procedures
 * - INA-CBGs tariff regulations
 *
 * Package Structure:
 * - controller: REST API endpoints for BPJS operations
 * - service: Business logic for VClaim integration
 * - repository: Data access layer for BPJS-related entities
 * - entity: JPA entities (SEP, BpjsClaim, Rujukan, etc.)
 * - dto: Data Transfer Objects for VClaim requests/responses
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.integration.bpjs;