ALTER TABLE orders RENAME COLUMN client_id TO client_user_id;
ALTER TABLE orders RENAME COLUMN subtotal_amount TO subtotal;
ALTER TABLE orders RENAME COLUMN total_amount TO total;
ALTER TABLE orders RENAME COLUMN delivery_address_snapshot TO address_snapshot;

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS order_number VARCHAR(40) UNIQUE,
    ADD COLUMN IF NOT EXISTS discount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS payment_method VARCHAR(40) NOT NULL DEFAULT 'CASH_ON_DELIVERY',
    ADD COLUMN IF NOT EXISTS payment_status VARCHAR(40) NOT NULL DEFAULT 'PENDING_COLLECTION',
    ADD COLUMN IF NOT EXISTS scheduled_for TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS needs_change BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS cash_amount_client_has NUMERIC(12, 2);

UPDATE orders
SET order_number = CONCAT('ORD-', REPLACE(id::text, '-', ''))
WHERE order_number IS NULL;

ALTER TABLE orders
    ALTER COLUMN order_number SET NOT NULL;

ALTER TABLE orders
    ALTER COLUMN store_id SET NOT NULL;

ALTER TABLE order_items RENAME COLUMN line_total TO total_price;

ALTER TABLE order_items
    ADD COLUMN IF NOT EXISTS product_variant_id UUID REFERENCES product_variants(id),
    ADD COLUMN IF NOT EXISTS product_image_snapshot VARCHAR(255);

CREATE TABLE IF NOT EXISTS order_timeline_events (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    status VARCHAR(40) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by_user_id UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_orders_client_user_id ON orders(client_user_id);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_timeline_events_order_id ON order_timeline_events(order_id);
CREATE INDEX IF NOT EXISTS idx_order_timeline_events_created_at ON order_timeline_events(created_at);
