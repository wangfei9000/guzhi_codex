import client from './client';
import type { ApiResponse, PageResponse, UserInfo } from './types';

export interface UserQuery {
  keyword?: string;
  page?: number;
  size?: number;
}

export async function fetchUsers(params: UserQuery): Promise<PageResponse<UserInfo>> {
  const res = await client.get<ApiResponse<PageResponse<UserInfo>>>('/user', { params });
  return res.data.data;
}

export async function createUser(data: {
  username: string;
  password: string;
  email?: string;
  phone?: string;
  nickname?: string;
  status?: number;
  organizationId?: number | null;
  roleIds?: number[];
}): Promise<UserInfo> {
  const res = await client.post<ApiResponse<UserInfo>>('/user', data);
  return res.data.data;
}

export async function updateUser(id: number, data: {
  email?: string;
  phone?: string;
  nickname?: string;
  status?: number;
  organizationId?: number | null;
  roleIds?: number[];
}): Promise<UserInfo> {
  const res = await client.put<ApiResponse<UserInfo>>(`/user/${id}`, data);
  return res.data.data;
}

export async function deleteUser(id: number): Promise<void> {
  await client.delete(`/user/${id}`);
}

export async function resetPassword(id: number, password: string): Promise<void> {
  await client.put(`/user/${id}/reset-password`, { password });
}

export interface UserOption {
  id: number;
  username: string;
  nickname: string;
}

export async function fetchUserOptions(): Promise<UserOption[]> {
  const res = await client.get<ApiResponse<UserOption[]>>('/user/options');
  return res.data.data;
}

export async function fetchUsersByRole(roleCode: string): Promise<UserOption[]> {
  const res = await client.get<ApiResponse<UserOption[]>>(`/user/by-role?roleCode=${roleCode}`);
  return res.data.data;
}
