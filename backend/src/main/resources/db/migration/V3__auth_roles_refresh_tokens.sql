ALTER TABLE users
    ADD COLUMN IF NOT EXISTS status VARCHAR(40) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMPTZ;

ALTER TABLE client_profiles
    ADD COLUMN IF NOT EXISTS default_city VARCHAR(255),
    ADD COLUMN IF NOT EXISTS default_address_snapshot TEXT;

ALTER TABLE user_roles
    DROP CONSTRAINT IF EXISTS uk_user_roles_user_role;

ALTER TABLE user_roles
    DROP COLUMN IF EXISTS role_id;

ALTER TABLE user_roles
    ADD COLUMN IF NOT EXISTS role VARCHAR(40),
    ADD COLUMN IF NOT EXISTS entity_type VARCHAR(40),
    ADD COLUMN IF NOT EXISTS entity_id UUID;

UPDATE user_roles
SET role = 'CLIENT',
    entity_type = 'CLIENT'
WHERE role IS NULL;

ALTER TABLE user_roles
    ALTER COLUMN role SET NOT NULL,
    ALTER COLUMN entity_type SET NOT NULL;

ALTER TABLE user_roles
    ADD CONSTRAINT uk_user_roles_user_role_entity UNIQUE (user_id, role, entity_type, entity_id);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON user_roles(role);
CREATE INDEX IF NOT EXISTS idx_user_roles_entity ON user_roles(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
