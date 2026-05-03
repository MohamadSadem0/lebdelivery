import type { CreateServiceOfferingRequest, Service } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getProviderServices(providerId: string) {
  const response = await api.get<ApiEnvelope<Service[]>>(`/provider-owner/providers/${providerId}/services`);
  return response.data.data;
}

export async function createService(providerId: string, request: CreateServiceOfferingRequest) {
  const response = await api.post<ApiEnvelope<Service>>(`/provider-owner/providers/${providerId}/services`, request);
  return response.data.data;
}
