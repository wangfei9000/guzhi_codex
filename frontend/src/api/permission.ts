import client from './client';
import type { ApiResponse, PermissionInfo } from './types';

export async function fetchPermissionTree(): Promise<PermissionInfo[]> {
  const res = await client.get<ApiResponse<PermissionInfo[]>>('/permission/tree');
  return res.data.data;
}

export async function createPermission(data: {
  permName: string;
  permCode: string;
  parentId?: number | null;
  type: string;
  path?: string;
  icon?: string;
  sortOrder?: number;
}): Promise<PermissionInfo> {
  const res = await client.post<ApiResponse<PermissionInfo>>('/permission', data);
  return res.data.data;
}

export async function updatePermission(id: number, data: {
  permName: string;
  permCode: string;
  parentId?: number | null;
  type: string;
  path?: string;
  icon?: string;
  sortOrder?: number;
}): Promise<PermissionInfo> {
  const res = await client.put<ApiResponse<PermissionInfo>>(`/permission/${id}`, data);
  return res.data.data;
}

export async function deletePermission(id: number): Promise<void> {
  await client.delete(`/permission/${id}`);
}
