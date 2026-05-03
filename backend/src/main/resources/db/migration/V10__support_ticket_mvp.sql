ALTER TABLE support_tickets
    ADD COLUMN IF NOT EXISTS related_service_request_id UUID REFERENCES service_requests(id),
    ADD COLUMN IF NOT EXISTS admin_note TEXT;

CREATE INDEX IF NOT EXISTS idx_support_tickets_status ON support_tickets(status);
CREATE INDEX IF NOT EXISTS idx_support_tickets_created_at ON support_tickets(created_at);
CREATE INDEX IF NOT EXISTS idx_support_tickets_related_order_id ON support_tickets(related_order_id);
CREATE INDEX IF NOT EXISTS idx_support_tickets_related_delivery_id ON support_tickets(related_delivery_id);
CREATE INDEX IF NOT EXISTS idx_support_tickets_related_service_request_id ON support_tickets(related_service_request_id);
