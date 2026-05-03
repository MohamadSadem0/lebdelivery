import type { QuoteServiceRequestRequest, ServiceRequest } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getProviderServiceRequests(providerId: string) {
  const response = await api.get<ApiEnvelope<ServiceRequest[]>>(`/provider-owner/providers/${providerId}/service-requests?page=0&size=50`);
  return response.data.data;
}

export async function acceptServiceRequest(providerId: string, requestId: string) {
  const response = await api.post<ApiEnvelope<ServiceRequest>>(`/provider-owner/providers/${providerId}/service-requests/${requestId}/accept`);
  return response.data.data;
}

export async function rejectServiceRequest(providerId: string, requestId: string, reason: string) {
  const response = await api.post<ApiEnvelope<ServiceRequest>>(`/provider-owner/providers/${providerId}/service-requests/${requestId}/reject`, { reason });
  return response.data.data;
}

export async function quoteServiceRequest(providerId: string, requestId: string, request: QuoteServiceRequestRequest) {
  const response = await api.post<ApiEnvelope<ServiceRequest>>(`/provider-owner/providers/${providerId}/service-requests/${requestId}/quote`, request);
  return response.data.data;
}

export async function markServiceRequestInProgress(providerId: string, requestId: string) {
  const response = await api.post<ApiEnvelope<ServiceRequest>>(`/provider-owner/providers/${providerId}/service-requests/${requestId}/mark-in-progress`);
  return response.data.data;
}

export async function markServiceRequestReady(providerId: string, requestId: string) {
  const response = await api.post<ApiEnvelope<ServiceRequest>>(`/provider-owner/providers/${providerId}/service-requests/${requestId}/mark-ready`);
  return response.data.data;
}

export async function markServiceRequestCompleted(providerId: string, requestId: string) {
  const response = await api.post<ApiEnvelope<ServiceRequest>>(`/provider-owner/providers/${providerId}/service-requests/${requestId}/mark-completed`);
  return response.data.data;
}
