import type { Receipt } from '@lebanon-platform/shared-types';
import { api } from './api';

type ApiEnvelope<T> = {
  data: T;
  message: string;
  success: boolean;
};

export async function getClientReceipts() {
  const response = await api.get<ApiEnvelope<Receipt[]>>('/client/receipts?page=0&size=20');
  return response.data.data;
}
