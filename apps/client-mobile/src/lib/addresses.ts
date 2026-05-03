import type { ClientAddress, CreateClientAddressRequest, UpdateClientAddressRequest } from '@lebanon-platform/shared-types';
import { api } from './api';

type ApiEnvelope<T> = {
  data: T;
  message: string;
  success: boolean;
};

export async function getClientAddresses() {
  const response = await api.get<ApiEnvelope<ClientAddress[]>>('/client/addresses');
  return response.data.data;
}

export async function createClientAddress(request: CreateClientAddressRequest) {
  const response = await api.post<ApiEnvelope<ClientAddress>>('/client/addresses', request);
  return response.data.data;
}

export async function updateClientAddress(addressId: string, request: UpdateClientAddressRequest) {
  const response = await api.patch<ApiEnvelope<ClientAddress>>(`/client/addresses/${addressId}`, request);
  return response.data.data;
}

export async function setDefaultClientAddress(addressId: string) {
  const response = await api.post<ApiEnvelope<ClientAddress>>(`/client/addresses/${addressId}/default`);
  return response.data.data;
}

export async function deleteClientAddress(addressId: string) {
  await api.delete<ApiEnvelope<null>>(`/client/addresses/${addressId}`);
}
