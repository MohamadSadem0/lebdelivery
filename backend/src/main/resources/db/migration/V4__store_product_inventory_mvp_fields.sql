ALTER TABLE stores
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS latitude NUMERIC(10, 7),
    ADD COLUMN IF NOT EXISTS longitude NUMERIC(10, 7),
    ADD COLUMN IF NOT EXISTS opening_hours_json TEXT,
    ADD COLUMN IF NOT EXISTS minimum_order_amount NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS average_preparation_minutes INTEGER,
    ADD COLUMN IF NOT EXISTS rating_average NUMERIC(3, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS rating_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE product_variants
    ADD COLUMN IF NOT EXISTS available BOOLEAN NOT NULL DEFAULT TRUE;

INSERT INTO store_types (id, code, display_name, active, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000108', 'FLOWER_SHOP', 'Flower Shop', TRUE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000109', 'BUTCHER', 'Butcher', TRUE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000110', 'PET_SHOP', 'Pet Shop', TRUE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000111', 'COSMETICS', 'Cosmetics Store', TRUE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000112', 'STATIONERY', 'Stationery Shop', TRUE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000113', 'HARDWARE', 'Hardware Store', TRUE, NOW(), NOW())
ON CONFLICT (code) DO NOTHING;

INSERT INTO store_type_configs (
    id,
    store_type_code,
    display_name,
    inventory_mode,
    enabled_features_json,
    required_product_fields_json,
    optional_product_fields_json,
    supports_modifiers,
    supports_variants,
    supports_expiry_date,
    supports_weight_items,
    supports_preparation_time,
    supports_prescription_flag,
    created_at,
    updated_at
) VALUES
('00000000-0000-0000-0000-000000000208', 'FLOWER_SHOP', 'Flower Shop', 'QUANTITY_BASED',
 '["BASIC_PRODUCTS","BASIC_INVENTORY","BASIC_ORDERS"]', '["name","price"]', '["description","imageUrl","occasion","color"]',
 FALSE, TRUE, FALSE, FALSE, FALSE, FALSE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000209', 'BUTCHER', 'Butcher', 'WEIGHT_BASED',
 '["WEIGHT_ITEMS","UNIT_PRICING","ITEM_AVAILABILITY"]', '["name","price","unitType"]', '["description","imageUrl","cut","origin"]',
 FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000210', 'PET_SHOP', 'Pet Shop', 'QUANTITY_BASED',
 '["BASIC_PRODUCTS","LOW_STOCK_ALERTS","BRAND"]', '["name","price"]', '["description","imageUrl","brand","pet_type"]',
 FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000211', 'COSMETICS', 'Cosmetics Store', 'QUANTITY_BASED',
 '["BRAND","EXPIRY_DATE","STOCK_QUANTITY"]', '["name","price"]', '["description","imageUrl","brand","shade","expiryDate"]',
 FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000212', 'STATIONERY', 'Stationery Shop', 'QUANTITY_BASED',
 '["BASIC_PRODUCTS","LOW_STOCK_ALERTS","BRAND"]', '["name","price"]', '["description","imageUrl","brand","packageSize"]',
 FALSE, TRUE, FALSE, FALSE, FALSE, FALSE, NOW(), NOW()),
('00000000-0000-0000-0000-000000000213', 'HARDWARE', 'Hardware Store', 'QUANTITY_BASED',
 '["BASIC_PRODUCTS","LOW_STOCK_ALERTS","SPECIFICATIONS"]', '["name","price"]', '["description","imageUrl","brand","model","specifications"]',
 FALSE, TRUE, FALSE, FALSE, FALSE, FALSE, NOW(), NOW())
ON CONFLICT (store_type_code) DO NOTHING;

CREATE INDEX IF NOT EXISTS idx_stores_owner_id ON stores(owner_id);
CREATE INDEX IF NOT EXISTS idx_stores_status_v4 ON stores(status);
CREATE INDEX IF NOT EXISTS idx_stores_store_type_code ON stores(store_type_code);
CREATE INDEX IF NOT EXISTS idx_products_available ON products(available);
CREATE INDEX IF NOT EXISTS idx_inventory_product_variant_id ON inventory(product_variant_id);
CREATE INDEX IF NOT EXISTS idx_inventory_movements_product_id ON inventory_movements(product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_movements_created_at ON inventory_movements(created_at);
