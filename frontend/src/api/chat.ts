import client from './client';
import type { ApiResponse, ChatMessage, ChatUser, PageResponse } from './types';

export async function fetchChatUsers(): Promise<ChatUser[]> {
  const res = await client.get<ApiResponse<ChatUser[]>>('/chat/users');
  return res.data.data;
}

export async function fetchChatMessages(
  peerId: number,
  params: { page?: number; size?: number } = {}
): Promise<PageResponse<ChatMessage>> {
  const res = await client.get<ApiResponse<PageResponse<ChatMessage>>>('/chat/messages', {
    params: {
      peerId,
      page: params.page ?? 0,
      size: params.size ?? 100,
    },
  });
  return res.data.data;
}

export async function sendChatMessage(data: {
  recipientId: number;
  content: string;
}): Promise<ChatMessage> {
  const res = await client.post<ApiResponse<ChatMessage>>('/chat/messages', data);
  return res.data.data;
}

export async function markConversationRead(peerId: number): Promise<void> {
  await client.put('/chat/messages/read', null, { params: { peerId } });
}

