CREATE TABLE IF NOT EXISTS cash_settlements (
    id UUID PRIMARY KEY,
    driver_id UUID NOT NULL REFERENCES drivers(id),
    amount NUMERIC(12, 2) NOT NULL,
    note TEXT,
    created_by_user_id UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_cash_settlements_driver_id ON cash_settlements(driver_id);
CREATE INDEX IF NOT EXISTS idx_cash_settlements_created_at ON cash_settlements(created_at);
CREATE INDEX IF NOT EXISTS idx_cash_settlements_created_by_user_id ON cash_settlements(created_by_user_id);
