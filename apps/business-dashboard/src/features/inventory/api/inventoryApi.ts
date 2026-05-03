import type { Inventory } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getStoreInventory(storeId: string) {
  const response = await api.get<ApiEnvelope<Inventory[]>>(`/store-owner/stores/${storeId}/inventory?page=0&size=50`);
  return response.data.data;
}
