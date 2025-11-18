/**
 * Billing Module
 *
 * This module handles all billing and financial operations including:
 * - Invoice generation and management
 * - Payment processing (cash, debit, credit, QRIS)
 * - Insurance claims management
 * - BPJS claims submission and tracking
 * - Tariff/pricing management
 * - Discount and package pricing
 * - Billing statements
 * - Payment reconciliation
 * - Refund processing
 * - Billing reports
 *
 * Indonesian Specific Features:
 * - BPJS claim creation and submission
 * - INA-CBGs tariff calculation
 * - SEP (Surat Eligibilitas Peserta) verification
 * - VClaim integration for claim validation
 * - Indonesian payment methods (QRIS, OVO, GoPay, Dana)
 * - PPh 23 tax calculation for services
 * - Indonesian currency (IDR) handling
 *
 * Payment Methods Supported:
 * - CASH: Cash payment
 * - DEBIT: Debit card
 * - CREDIT: Credit card
 * - QRIS: QR Code Indonesian Standard
 * - E_WALLET: OVO, GoPay, Dana, LinkAja
 * - BANK_TRANSFER: Virtual account, transfer
 * - BPJS: National health insurance
 * - INSURANCE: Other insurance companies
 *
 * Package Structure:
 * - controller: REST API endpoints for billing operations
 * - service: Business logic for billing and payment workflows
 * - repository: Data access layer for billing entities
 * - entity: JPA entities (Invoice, Payment, Claim, Tariff, etc.)
 * - dto: Data Transfer Objects for API requests/responses
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.billing;