import client from './client';
import type { ApiResponse } from './types';

export interface AssistantGenerateResponse {
  model: string;
  response: string;
  toolCalls?: unknown[] | null;
}

export async function generateAssistantResponse(prompt: string): Promise<AssistantGenerateResponse> {
  const res = await client.post<ApiResponse<AssistantGenerateResponse>>('/assistant/generate', { prompt });
  return res.data.data;
}
