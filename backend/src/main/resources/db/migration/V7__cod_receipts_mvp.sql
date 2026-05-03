ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS collected_amount NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS collected_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS cash_mismatch BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS collection_note TEXT;

CREATE UNIQUE INDEX IF NOT EXISTS uk_payments_order_id_not_null
    ON payments(order_id)
    WHERE order_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_payments_delivery_id_not_null
    ON payments(delivery_id)
    WHERE delivery_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_receipts_order_id_not_null
    ON receipts(order_id)
    WHERE order_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_payments_collected_by_driver_id ON payments(collected_by_driver_id);
CREATE INDEX IF NOT EXISTS idx_payments_collected_at ON payments(collected_at);
CREATE INDEX IF NOT EXISTS idx_receipts_order_id ON receipts(order_id);
CREATE INDEX IF NOT EXISTS idx_receipts_created_at ON receipts(created_at);
