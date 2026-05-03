import type { CreateOrderRequest, OrderDetails, OrderSummary, OrderTimelineEvent } from '@lebanon-platform/shared-types';
import { api } from './api';

type ApiEnvelope<T> = {
  data: T;
  message: string;
  success: boolean;
};

export async function createOrder(request: CreateOrderRequest) {
  const response = await api.post<ApiEnvelope<OrderDetails>>('/client/orders', request);
  return response.data.data;
}

export async function getClientOrders() {
  const response = await api.get<ApiEnvelope<OrderSummary[]>>('/client/orders?page=0&size=20');
  return response.data.data;
}

export async function getClientOrder(orderId: string) {
  const response = await api.get<ApiEnvelope<OrderDetails>>(`/client/orders/${orderId}`);
  return response.data.data;
}

export async function getOrderTimeline(orderId: string) {
  const response = await api.get<ApiEnvelope<{ orderId: string; events: OrderTimelineEvent[] }>>(`/client/orders/${orderId}/timeline`);
  return response.data.data;
}

export async function cancelOrder(orderId: string) {
  const response = await api.post<ApiEnvelope<OrderDetails>>(`/client/orders/${orderId}/cancel`);
  return response.data.data;
}

export async function reorderOrder(orderId: string) {
  const response = await api.post<ApiEnvelope<OrderDetails>>(`/client/orders/${orderId}/reorder`);
  return response.data.data;
}
