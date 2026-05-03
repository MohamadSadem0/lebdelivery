import type {
  CashCollectionRequest,
  CashCollectionResponse,
  CreateDriverProfileRequest,
  Delivery,
  DeliverySummary,
  DriverEarningsSummary,
  DriverProfile,
  Notification,
  SupportTicket,
  CreateSupportTicketRequest
} from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function createDriverProfile(request: CreateDriverProfileRequest) {
  const response = await api.post<ApiEnvelope<DriverProfile>>('/driver/profile', request);
  return response.data.data;
}

export async function getDriverProfile() {
  const response = await api.get<ApiEnvelope<DriverProfile>>('/driver/profile');
  return response.data.data;
}

export async function getAvailableJobs() {
  const response = await api.get<ApiEnvelope<DeliverySummary[]>>('/driver/jobs/available?page=0&size=30');
  return response.data.data;
}

export async function getDriverJob(deliveryId: string) {
  const response = await api.get<ApiEnvelope<Delivery>>(`/driver/jobs/${deliveryId}`);
  return response.data.data;
}

export async function acceptJob(deliveryId: string) {
  const response = await api.post<ApiEnvelope<Delivery>>(`/driver/jobs/${deliveryId}/accept`);
  return response.data.data;
}

export async function getActiveDeliveries() {
  const response = await api.get<ApiEnvelope<DeliverySummary[]>>('/driver/deliveries/active');
  return response.data.data;
}

export async function getDeliveryHistory() {
  const response = await api.get<ApiEnvelope<DeliverySummary[]>>('/driver/deliveries/history?page=0&size=30');
  return response.data.data;
}

export async function getDriverEarnings() {
  const response = await api.get<ApiEnvelope<DriverEarningsSummary>>('/driver/earnings');
  return response.data.data;
}

export async function updateDeliveryStatus(deliveryId: string, action: 'arrived-pickup' | 'picked-up' | 'on-the-way' | 'arrived-destination' | 'delivered') {
  const response = await api.post<ApiEnvelope<Delivery>>(`/driver/deliveries/${deliveryId}/${action}`);
  return response.data.data;
}

export async function failDelivery(deliveryId: string, reason: string) {
  const response = await api.post<ApiEnvelope<Delivery>>(`/driver/deliveries/${deliveryId}/failed`, { reason });
  return response.data.data;
}

export async function markCashCollected(deliveryId: string, request: CashCollectionRequest) {
  const response = await api.post<ApiEnvelope<CashCollectionResponse>>(`/driver/deliveries/${deliveryId}/cash-collected`, request);
  return response.data.data;
}

export async function getNotifications() {
  const response = await api.get<ApiEnvelope<Notification[]>>('/notifications?page=0&size=30');
  return response.data.data;
}

export async function getSupportTickets() {
  const response = await api.get<ApiEnvelope<SupportTicket[]>>('/support/tickets?page=0&size=30');
  return response.data.data;
}

export async function createSupportTicket(request: CreateSupportTicketRequest) {
  const response = await api.post<ApiEnvelope<SupportTicket>>('/support/tickets', request);
  return response.data.data;
}
