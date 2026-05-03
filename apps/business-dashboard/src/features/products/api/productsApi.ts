import type { Product } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getStoreProducts(storeId: string) {
  const response = await api.get<ApiEnvelope<Product[]>>(`/store-owner/stores/${storeId}/products?page=0&size=50`);
  return response.data.data;
}
