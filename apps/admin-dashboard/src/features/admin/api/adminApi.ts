import type {
  AdminDecisionRequest,
  AdminDriverProfile,
  CashSettlement,
  CreateCashSettlementRequest,
  DeliverySummary,
  OrderSummary,
  Payment,
  Receipt,
  Store,
  SupportTicket,
  SupportTicketStatus,
  UserProfile
} from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getAdminUsers() {
  const response = await api.get<ApiEnvelope<UserProfile[]>>('/admin/users?page=0&size=50');
  return response.data.data;
}

export async function activateUser(userId: string, request: AdminDecisionRequest = {}) {
  const response = await api.post<ApiEnvelope<UserProfile>>(`/admin/users/${userId}/activate`, request);
  return response.data.data;
}

export async function suspendUser(userId: string, request: AdminDecisionRequest = {}) {
  const response = await api.post<ApiEnvelope<UserProfile>>(`/admin/users/${userId}/suspend`, request);
  return response.data.data;
}

export async function getAdminStores() {
  const response = await api.get<ApiEnvelope<Store[]>>('/admin/stores?page=0&size=50');
  return response.data.data;
}

export async function approveStore(storeId: string, request: AdminDecisionRequest = {}) {
  const response = await api.post<ApiEnvelope<Store>>(`/admin/stores/${storeId}/approve`, request);
  return response.data.data;
}

export async function rejectStore(storeId: string, request: AdminDecisionRequest = {}) {
  const response = await api.post<ApiEnvelope<Store>>(`/admin/stores/${storeId}/reject`, request);
  return response.data.data;
}

export async function suspendStore(storeId: string, request: AdminDecisionRequest = {}) {
  const response = await api.post<ApiEnvelope<Store>>(`/admin/stores/${storeId}/suspend`, request);
  return response.data.data;
}

export async function getAdminDrivers() {
  const response = await api.get<ApiEnvelope<AdminDriverProfile[]>>('/admin/drivers?page=0&size=50');
  return response.data.data;
}

export async function approveDriver(driverId: string, request: AdminDecisionRequest = {}) {
  const response = await api.post<ApiEnvelope<AdminDriverProfile>>(`/admin/drivers/${driverId}/approve`, request);
  return response.data.data;
}

export async function rejectDriver(driverId: string, request: AdminDecisionRequest = {}) {
  const response = await api.post<ApiEnvelope<AdminDriverProfile>>(`/admin/drivers/${driverId}/reject`, request);
  return response.data.data;
}

export async function suspendDriver(driverId: string, request: AdminDecisionRequest = {}) {
  const response = await api.post<ApiEnvelope<AdminDriverProfile>>(`/admin/drivers/${driverId}/suspend`, request);
  return response.data.data;
}

export async function getAdminReceipts() {
  const response = await api.get<ApiEnvelope<Receipt[]>>('/admin/receipts?page=0&size=50');
  return response.data.data;
}

export async function getAdminOrders() {
  const response = await api.get<ApiEnvelope<OrderSummary[]>>('/admin/orders?page=0&size=50');
  return response.data.data;
}

export async function getAdminDeliveries() {
  const response = await api.get<ApiEnvelope<DeliverySummary[]>>('/admin/deliveries?page=0&size=50');
  return response.data.data;
}

export async function getAdminPayments() {
  const response = await api.get<ApiEnvelope<Payment[]>>('/admin/payments?page=0&size=50');
  return response.data.data;
}

export async function getAdminCashSettlements() {
  const response = await api.get<ApiEnvelope<CashSettlement[]>>('/admin/cash-settlements?page=0&size=50');
  return response.data.data;
}

export async function createAdminCashSettlement(request: CreateCashSettlementRequest) {
  const response = await api.post<ApiEnvelope<CashSettlement>>('/admin/cash-settlements', request);
  return response.data.data;
}

export async function getAdminSupportTickets() {
  const response = await api.get<ApiEnvelope<SupportTicket[]>>('/admin/support/tickets?page=0&size=50');
  return response.data.data;
}

export async function updateSupportTicketStatus(ticketId: string, status: SupportTicketStatus, adminNote?: string) {
  const response = await api.patch<ApiEnvelope<SupportTicket>>(`/admin/support/tickets/${ticketId}/status`, { status, adminNote });
  return response.data.data;
}
