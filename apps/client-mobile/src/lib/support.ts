import type { CreateSupportTicketRequest, SupportTicket } from '@lebanon-platform/shared-types';
import { api } from './api';

type ApiEnvelope<T> = {
  data: T;
};

export async function getSupportTickets() {
  const response = await api.get<ApiEnvelope<SupportTicket[]>>('/support/tickets?page=0&size=30');
  return response.data.data;
}

export async function createSupportTicket(request: CreateSupportTicketRequest) {
  const response = await api.post<ApiEnvelope<SupportTicket>>('/support/tickets', request);
  return response.data.data;
}
