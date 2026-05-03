import type { Product, Store } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getStores() {
  const response = await api.get<ApiEnvelope<Store[]>>('/stores?page=0&size=24');
  return response.data.data;
}

export async function getStore(storeId: string) {
  const response = await api.get<ApiEnvelope<Store>>(`/stores/${storeId}`);
  return response.data.data;
}

export async function getStoreProducts(storeId: string) {
  const response = await api.get<ApiEnvelope<Product[]>>(`/stores/${storeId}/products?page=0&size=50`);
  return response.data.data;
}

export async function getProduct(storeId: string, productId: string) {
  const response = await api.get<ApiEnvelope<Product>>(`/stores/${storeId}/products/${productId}`);
  return response.data.data;
}
