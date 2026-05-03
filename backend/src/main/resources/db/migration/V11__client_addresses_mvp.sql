CREATE TABLE IF NOT EXISTS client_addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    label VARCHAR(80) NOT NULL,
    full_address TEXT NOT NULL,
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    phone_number VARCHAR(32),
    instructions TEXT,
    default_address BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_client_addresses_user_id ON client_addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_client_addresses_user_default ON client_addresses(user_id, default_address);
CREATE INDEX IF NOT EXISTS idx_client_addresses_user_active ON client_addresses(user_id, active);
