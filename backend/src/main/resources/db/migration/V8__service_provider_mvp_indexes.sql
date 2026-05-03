CREATE INDEX IF NOT EXISTS idx_service_providers_owner_id ON service_providers(owner_id);
CREATE INDEX IF NOT EXISTS idx_service_providers_active ON service_providers(active);
CREATE INDEX IF NOT EXISTS idx_services_provider_id ON services(service_provider_id);
CREATE INDEX IF NOT EXISTS idx_services_active ON services(active);
CREATE INDEX IF NOT EXISTS idx_service_requests_provider_id ON service_requests(service_provider_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_service_id ON service_requests(service_id);
CREATE INDEX IF NOT EXISTS idx_service_requests_created_at ON service_requests(created_at);
