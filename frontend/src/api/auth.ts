import client from './client';
import type { ApiResponse, TokenInfo, UserInfo } from './types';

export async function login(username: string, password: string): Promise<TokenInfo> {
  const res = await client.post<ApiResponse<TokenInfo>>('/auth/login', { username, password });
  return res.data.data;
}

export async function register(data: {
  username: string;
  password: string;
  email?: string;
  phone?: string;
  nickname?: string;
}): Promise<void> {
  await client.post('/auth/register', data);
}

export async function refreshToken(token: string): Promise<TokenInfo> {
  const res = await client.post<ApiResponse<TokenInfo>>('/auth/refresh', { refreshToken: token });
  return res.data.data;
}

export async function getCurrentUser(): Promise<UserInfo> {
  const res = await client.get<ApiResponse<UserInfo>>('/user/me');
  return res.data.data;
}
