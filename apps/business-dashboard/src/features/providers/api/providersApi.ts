import type { CreateServiceProviderRequest, ServiceProvider } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getMyProviders() {
  const response = await api.get<ApiEnvelope<ServiceProvider[]>>('/provider-owner/providers');
  return response.data.data;
}

export async function createProvider(request: CreateServiceProviderRequest) {
  const response = await api.post<ApiEnvelope<ServiceProvider>>('/provider-owner/providers', request);
  return response.data.data;
}
