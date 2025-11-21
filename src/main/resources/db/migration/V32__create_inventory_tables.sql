-- =============================================================================
-- Migration: V32__create_inventory_tables.sql
-- Description: Creates pharmacy inventory management tables with FIFO/FEFO support
--              for Phase 9.3 (Stock receiving, batch tracking, movements, transfers)
-- Author: HMS Development Team
-- Date: 2025-01-21
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Table: stock_receipt
-- Description: Stock receipts from suppliers with inspection workflow
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.stock_receipt (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    receipt_number VARCHAR(50) NOT NULL UNIQUE,
    supplier_id UUID NOT NULL,
    supplier_name VARCHAR(200),
    location_id UUID NOT NULL,
    location_name VARCHAR(200),
    purchase_order_number VARCHAR(50),
    supplier_invoice_number VARCHAR(50),
    supplier_delivery_note VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    receipt_date DATE NOT NULL,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    total_items INTEGER,
    total_quantity DECIMAL(15,2),
    total_value DECIMAL(15,2),
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(15,2),
    tax_percentage DECIMAL(5,2),
    tax_amount DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    delivered_by VARCHAR(200),
    received_by_id UUID,
    received_by_name VARCHAR(200),
    received_at TIMESTAMP,
    inspected_by_id UUID,
    inspected_by_name VARCHAR(200),
    inspected_at TIMESTAMP,
    inspection_notes TEXT,
    approved_by_id UUID,
    approved_by_name VARCHAR(200),
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_receipt_number ON pharmacy_schema.stock_receipt(receipt_number);
CREATE INDEX idx_receipt_supplier ON pharmacy_schema.stock_receipt(supplier_id);
CREATE INDEX idx_receipt_location ON pharmacy_schema.stock_receipt(location_id);
CREATE INDEX idx_receipt_status ON pharmacy_schema.stock_receipt(status);
CREATE INDEX idx_receipt_date ON pharmacy_schema.stock_receipt(receipt_date);

COMMENT ON TABLE pharmacy_schema.stock_receipt IS 'Stock receipts from suppliers with inspection workflow';
COMMENT ON COLUMN pharmacy_schema.stock_receipt.status IS 'Status: DRAFT, PENDING, RECEIVED, INSPECTED, APPROVED, REJECTED, PARTIALLY_RECEIVED, CANCELLED';

-- -----------------------------------------------------------------------------
-- Table: stock_receipt_item
-- Description: Individual items in stock receipts
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.stock_receipt_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    receipt_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    drug_id UUID NOT NULL,
    batch_number VARCHAR(50),
    expiry_date DATE,
    manufacturing_date DATE,
    quantity_ordered DECIMAL(10,2),
    quantity_received DECIMAL(10,2) NOT NULL,
    quantity_accepted DECIMAL(10,2),
    quantity_rejected DECIMAL(10,2),
    unit_price DECIMAL(15,2) NOT NULL,
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(15,2),
    tax_percentage DECIMAL(5,2),
    tax_amount DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    inspection_passed BOOLEAN,
    inspection_notes TEXT,
    storage_location VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,

    CONSTRAINT fk_receipt_item_receipt FOREIGN KEY (receipt_id)
        REFERENCES pharmacy_schema.stock_receipt(id) ON DELETE CASCADE,
    CONSTRAINT fk_receipt_item_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id)
);

CREATE INDEX idx_receipt_item_receipt ON pharmacy_schema.stock_receipt_item(receipt_id);
CREATE INDEX idx_receipt_item_drug ON pharmacy_schema.stock_receipt_item(drug_id);
CREATE INDEX idx_receipt_item_batch ON pharmacy_schema.stock_receipt_item(batch_number);

COMMENT ON TABLE pharmacy_schema.stock_receipt_item IS 'Line items in stock receipts with batch and expiry tracking';

-- -----------------------------------------------------------------------------
-- Table: stock_batch
-- Description: Batch/lot tracking for FIFO/FEFO inventory management
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.stock_batch (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    drug_id UUID NOT NULL,
    location_id UUID NOT NULL,
    location_name VARCHAR(200),
    batch_number VARCHAR(50) NOT NULL,
    expiry_date DATE,
    manufacturing_date DATE,
    received_date DATE,
    quantity_on_hand DECIMAL(15,2) NOT NULL DEFAULT 0,
    quantity_available DECIMAL(15,2) NOT NULL DEFAULT 0,
    quantity_reserved DECIMAL(15,2) DEFAULT 0,
    quantity_quarantined DECIMAL(15,2) DEFAULT 0,
    unit_cost DECIMAL(15,2),
    storage_location VARCHAR(100),
    supplier_id UUID,
    supplier_name VARCHAR(200),
    receipt_number VARCHAR(50),
    is_quarantined BOOLEAN DEFAULT FALSE,
    quarantine_reason TEXT,
    is_expired BOOLEAN DEFAULT FALSE,
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,

    CONSTRAINT fk_batch_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id),
    CONSTRAINT uk_batch_drug_location_batch UNIQUE (drug_id, location_id, batch_number)
);

CREATE INDEX idx_batch_drug ON pharmacy_schema.stock_batch(drug_id);
CREATE INDEX idx_batch_location ON pharmacy_schema.stock_batch(location_id);
CREATE INDEX idx_batch_number ON pharmacy_schema.stock_batch(batch_number);
CREATE INDEX idx_batch_expiry ON pharmacy_schema.stock_batch(expiry_date);
CREATE INDEX idx_batch_active ON pharmacy_schema.stock_batch(active);

COMMENT ON TABLE pharmacy_schema.stock_batch IS 'Batch/lot tracking for FIFO/FEFO inventory management';
COMMENT ON COLUMN pharmacy_schema.stock_batch.quantity_available IS 'Available quantity = on_hand - reserved - quarantined';
COMMENT ON COLUMN pharmacy_schema.stock_batch.expiry_date IS 'Critical for FEFO (First Expiry First Out) allocation';

-- -----------------------------------------------------------------------------
-- Table: stock_movement
-- Description: All stock movements for complete audit trail
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.stock_movement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    drug_id UUID NOT NULL,
    batch_id UUID,
    location_id UUID NOT NULL,
    location_name VARCHAR(200),
    movement_type VARCHAR(50) NOT NULL,
    quantity DECIMAL(15,2) NOT NULL,
    quantity_before DECIMAL(15,2),
    quantity_after DECIMAL(15,2),
    unit_cost DECIMAL(15,2),
    total_cost DECIMAL(15,2),
    movement_date TIMESTAMP NOT NULL,
    reference_type VARCHAR(50),
    reference_id UUID,
    reference_number VARCHAR(50),
    performed_by_id UUID,
    performed_by_name VARCHAR(200),
    from_location_id UUID,
    from_location_name VARCHAR(200),
    to_location_id UUID,
    to_location_name VARCHAR(200),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,

    CONSTRAINT fk_movement_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id),
    CONSTRAINT fk_movement_batch FOREIGN KEY (batch_id)
        REFERENCES pharmacy_schema.stock_batch(id)
);

CREATE INDEX idx_movement_drug ON pharmacy_schema.stock_movement(drug_id);
CREATE INDEX idx_movement_batch ON pharmacy_schema.stock_movement(batch_id);
CREATE INDEX idx_movement_location ON pharmacy_schema.stock_movement(location_id);
CREATE INDEX idx_movement_type ON pharmacy_schema.stock_movement(movement_type);
CREATE INDEX idx_movement_date ON pharmacy_schema.stock_movement(movement_date);
CREATE INDEX idx_movement_reference ON pharmacy_schema.stock_movement(reference_type, reference_id);

COMMENT ON TABLE pharmacy_schema.stock_movement IS 'Complete audit trail of all stock movements';
COMMENT ON COLUMN pharmacy_schema.stock_movement.movement_type IS 'Type: RECEIPT, DISPENSING, ADJUSTMENT_IN, ADJUSTMENT_OUT, TRANSFER_OUT, TRANSFER_IN, etc.';

-- -----------------------------------------------------------------------------
-- Table: stock_adjustment
-- Description: Manual stock adjustments with approval workflow
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.stock_adjustment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    adjustment_number VARCHAR(50) NOT NULL UNIQUE,
    drug_id UUID NOT NULL,
    batch_id UUID,
    location_id UUID NOT NULL,
    location_name VARCHAR(200),
    adjustment_date DATE NOT NULL,
    quantity_before DECIMAL(15,2),
    quantity_adjusted DECIMAL(15,2) NOT NULL,
    quantity_after DECIMAL(15,2),
    reason VARCHAR(50) NOT NULL,
    reason_details TEXT,
    unit_cost DECIMAL(15,2),
    total_cost DECIMAL(15,2),
    adjusted_by_id UUID NOT NULL,
    adjusted_by_name VARCHAR(200),
    approved_by_id UUID,
    approved_by_name VARCHAR(200),
    approved_at TIMESTAMP,
    is_approved BOOLEAN DEFAULT FALSE,
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),

    CONSTRAINT fk_adjustment_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id),
    CONSTRAINT fk_adjustment_batch FOREIGN KEY (batch_id)
        REFERENCES pharmacy_schema.stock_batch(id)
);

CREATE INDEX idx_adjustment_number ON pharmacy_schema.stock_adjustment(adjustment_number);
CREATE INDEX idx_adjustment_drug ON pharmacy_schema.stock_adjustment(drug_id);
CREATE INDEX idx_adjustment_batch ON pharmacy_schema.stock_adjustment(batch_id);
CREATE INDEX idx_adjustment_location ON pharmacy_schema.stock_adjustment(location_id);
CREATE INDEX idx_adjustment_date ON pharmacy_schema.stock_adjustment(adjustment_date);

COMMENT ON TABLE pharmacy_schema.stock_adjustment IS 'Manual stock adjustments with approval workflow';
COMMENT ON COLUMN pharmacy_schema.stock_adjustment.reason IS 'Reason: DAMAGE, EXPIRY, THEFT, COUNT_DIFFERENCE, SYSTEM_ERROR, etc.';

-- -----------------------------------------------------------------------------
-- Table: stock_transfer
-- Description: Stock transfers between pharmacy locations
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.stock_transfer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transfer_number VARCHAR(50) NOT NULL UNIQUE,
    from_location_id UUID NOT NULL,
    from_location_name VARCHAR(200),
    to_location_id UUID NOT NULL,
    to_location_name VARCHAR(200),
    status VARCHAR(50) NOT NULL,
    transfer_date DATE NOT NULL,
    expected_arrival_date DATE,
    actual_arrival_date DATE,
    requested_by_id UUID,
    requested_by_name VARCHAR(200),
    approved_by_id UUID,
    approved_by_name VARCHAR(200),
    approved_at TIMESTAMP,
    sent_by_id UUID,
    sent_by_name VARCHAR(200),
    sent_at TIMESTAMP,
    received_by_id UUID,
    received_by_name VARCHAR(200),
    received_at TIMESTAMP,
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_transfer_number ON pharmacy_schema.stock_transfer(transfer_number);
CREATE INDEX idx_transfer_from_location ON pharmacy_schema.stock_transfer(from_location_id);
CREATE INDEX idx_transfer_to_location ON pharmacy_schema.stock_transfer(to_location_id);
CREATE INDEX idx_transfer_status ON pharmacy_schema.stock_transfer(status);
CREATE INDEX idx_transfer_date ON pharmacy_schema.stock_transfer(transfer_date);

COMMENT ON TABLE pharmacy_schema.stock_transfer IS 'Stock transfers between pharmacy locations';
COMMENT ON COLUMN pharmacy_schema.stock_transfer.status IS 'Status: DRAFT, PENDING, APPROVED, IN_TRANSIT, RECEIVED, PARTIALLY_RECEIVED, REJECTED, CANCELLED';

-- -----------------------------------------------------------------------------
-- Table: stock_transfer_item
-- Description: Individual items in stock transfers
-- -----------------------------------------------------------------------------
CREATE TABLE pharmacy_schema.stock_transfer_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transfer_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    drug_id UUID NOT NULL,
    batch_id UUID,
    quantity_requested DECIMAL(10,2) NOT NULL,
    quantity_sent DECIMAL(10,2),
    quantity_received DECIMAL(10,2),
    unit_cost DECIMAL(15,2),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT,

    CONSTRAINT fk_transfer_item_transfer FOREIGN KEY (transfer_id)
        REFERENCES pharmacy_schema.stock_transfer(id) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_item_drug FOREIGN KEY (drug_id)
        REFERENCES pharmacy_schema.drug(id),
    CONSTRAINT fk_transfer_item_batch FOREIGN KEY (batch_id)
        REFERENCES pharmacy_schema.stock_batch(id)
);

CREATE INDEX idx_transfer_item_transfer ON pharmacy_schema.stock_transfer_item(transfer_id);
CREATE INDEX idx_transfer_item_drug ON pharmacy_schema.stock_transfer_item(drug_id);
CREATE INDEX idx_transfer_item_batch ON pharmacy_schema.stock_transfer_item(batch_id);

COMMENT ON TABLE pharmacy_schema.stock_transfer_item IS 'Line items in stock transfers with batch tracking';

-- =============================================================================
-- End of Migration V32
-- =============================================================================
