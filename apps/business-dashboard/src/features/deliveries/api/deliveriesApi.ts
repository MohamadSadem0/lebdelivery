import type { DeliverySummary } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getStoreDeliveries(storeId: string) {
  const response = await api.get<ApiEnvelope<DeliverySummary[]>>(`/store-owner/stores/${storeId}/deliveries?page=0&size=20`);
  return response.data.data;
}
