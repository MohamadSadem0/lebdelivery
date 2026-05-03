import type {
  ClientAddress,
  CreateClientAddressRequest,
  CreateOrderRequest,
  CreateSupportTicketRequest,
  Notification,
  OrderDetails,
  OrderSummary,
  OrderTimelineEvent,
  Receipt,
  SupportTicket
} from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getAddresses() {
  const response = await api.get<ApiEnvelope<ClientAddress[]>>('/client/addresses');
  return response.data.data;
}

export async function createAddress(request: CreateClientAddressRequest) {
  const response = await api.post<ApiEnvelope<ClientAddress>>('/client/addresses', request);
  return response.data.data;
}

export async function setDefaultAddress(addressId: string) {
  const response = await api.post<ApiEnvelope<ClientAddress>>(`/client/addresses/${addressId}/default`);
  return response.data.data;
}

export async function createOrder(request: CreateOrderRequest) {
  const response = await api.post<ApiEnvelope<OrderDetails>>('/client/orders', request);
  return response.data.data;
}

export async function getOrders() {
  const response = await api.get<ApiEnvelope<OrderSummary[]>>('/client/orders?page=0&size=30');
  return response.data.data;
}

export async function getOrder(orderId: string) {
  const response = await api.get<ApiEnvelope<OrderDetails>>(`/client/orders/${orderId}`);
  return response.data.data;
}

export async function getOrderTimeline(orderId: string) {
  const response = await api.get<ApiEnvelope<{ orderId: string; events: OrderTimelineEvent[] }>>(`/client/orders/${orderId}/timeline`);
  return response.data.data;
}

export async function reorder(orderId: string) {
  const response = await api.post<ApiEnvelope<OrderDetails>>(`/client/orders/${orderId}/reorder`);
  return response.data.data;
}

export async function getReceipts() {
  const response = await api.get<ApiEnvelope<Receipt[]>>('/client/receipts?page=0&size=30');
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
