import type { Store } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';

export async function getMyStores() {
  const response = await api.get<ApiEnvelope<Store[]>>('/store-owner/stores');
  return response.data.data;
}
