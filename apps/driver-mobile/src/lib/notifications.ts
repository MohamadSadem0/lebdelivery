import type { Notification, UnreadNotificationCount } from '@lebanon-platform/shared-types';
import { api } from './api';

type ApiEnvelope<T> = {
  data: T;
};

export async function getNotifications() {
  const response = await api.get<ApiEnvelope<Notification[]>>('/notifications?page=0&size=30');
  return response.data.data;
}

export async function getUnreadNotificationCount() {
  const response = await api.get<ApiEnvelope<UnreadNotificationCount>>('/notifications/unread-count');
  return response.data.data;
}

export async function markNotificationRead(notificationId: string) {
  const response = await api.post<ApiEnvelope<Notification>>(`/notifications/${notificationId}/read`);
  return response.data.data;
}

export async function markAllNotificationsRead() {
  const response = await api.post<ApiEnvelope<UnreadNotificationCount>>('/notifications/read-all');
  return response.data.data;
}
