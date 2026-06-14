import client from './client';
import type { ApiResponse, PageResponse, NotificationItem } from './types';

export async function fetchNotifications(params: { page?: number; size?: number }): Promise<PageResponse<NotificationItem>> {
  const res = await client.get<ApiResponse<PageResponse<NotificationItem>>>('/notification', { params });
  return res.data.data;
}

export async function getUnreadCount(): Promise<number> {
  const res = await client.get<ApiResponse<number>>('/notification/unread-count');
  return res.data.data;
}

export async function markAsRead(id: number): Promise<void> {
  await client.put(`/notification/${id}/read`);
}

export async function markAllAsRead(): Promise<void> {
  await client.put('/notification/read-all');
}

export interface SendNotificationParams {
  recipientId: number;
  title: string;
  content: string;
}

export async function sendNotification(data: SendNotificationParams): Promise<NotificationItem> {
  const res = await client.post<ApiResponse<NotificationItem>>('/notification/send', data);
  return res.data.data;
}
