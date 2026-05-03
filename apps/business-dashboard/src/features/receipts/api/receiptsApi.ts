import type { Receipt } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getStoreReceipts(storeId: string) {
  const response = await api.get<ApiEnvelope<Receipt[]>>(`/store-owner/stores/${storeId}/receipts?page=0&size=20`);
  return response.data.data;
}
