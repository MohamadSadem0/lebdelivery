import type { OrderDetails, OrderSummary } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getStoreOrders(storeId: string) {
  const response = await api.get<ApiEnvelope<OrderSummary[]>>(`/store-owner/stores/${storeId}/orders?page=0&size=20`);
  return response.data.data;
}

export async function getStoreOrder(storeId: string, orderId: string) {
  const response = await api.get<ApiEnvelope<OrderDetails>>(`/store-owner/stores/${storeId}/orders/${orderId}`);
  return response.data.data;
}

export async function acceptOrder(storeId: string, orderId: string) {
  const response = await api.post<ApiEnvelope<OrderDetails>>(`/store-owner/stores/${storeId}/orders/${orderId}/accept`);
  return response.data.data;
}

export async function rejectOrder(storeId: string, orderId: string, reason: string) {
  const response = await api.post<ApiEnvelope<OrderDetails>>(`/store-owner/stores/${storeId}/orders/${orderId}/reject`, { reason });
  return response.data.data;
}

export async function markPreparing(storeId: string, orderId: string) {
  const response = await api.post<ApiEnvelope<OrderDetails>>(`/store-owner/stores/${storeId}/orders/${orderId}/mark-preparing`);
  return response.data.data;
}

export async function markReady(storeId: string, orderId: string) {
  const response = await api.post<ApiEnvelope<OrderDetails>>(`/store-owner/stores/${storeId}/orders/${orderId}/mark-ready`);
  return response.data.data;
}

export async function requestDelivery(storeId: string, orderId: string) {
  const response = await api.post<ApiEnvelope<unknown>>(`/store-owner/stores/${storeId}/orders/${orderId}/delivery-request`, {});
  return response.data.data;
}
