import client from './client';
import type { ApiResponse, RoleInfo } from './types';

export async function fetchRoles(): Promise<RoleInfo[]> {
  const res = await client.get<ApiResponse<RoleInfo[]>>('/role');
  return res.data.data;
}

export async function createRole(data: {
  roleName: string;
  roleCode: string;
  description?: string;
  permissionIds?: number[];
}): Promise<RoleInfo> {
  const res = await client.post<ApiResponse<RoleInfo>>('/role', data);
  return res.data.data;
}

export async function updateRole(id: number, data: {
  roleName: string;
  roleCode: string;
  description?: string;
  permissionIds?: number[];
}): Promise<RoleInfo> {
  const res = await client.put<ApiResponse<RoleInfo>>(`/role/${id}`, data);
  return res.data.data;
}

export async function deleteRole(id: number): Promise<void> {
  await client.delete(`/role/${id}`);
}

export async function assignPermissions(roleId: number, permissionIds: number[]): Promise<void> {
  await client.put(`/role/${roleId}/permissions`, permissionIds);
}
