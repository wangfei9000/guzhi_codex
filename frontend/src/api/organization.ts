import client from './client';
import type { ApiResponse, OrganizationRecord, PageResponse } from './types';

export async function fetchOrganizations(params: { page?: number; size?: number } = {}): Promise<PageResponse<OrganizationRecord>> {
  const res = await client.get<ApiResponse<PageResponse<OrganizationRecord>>>('/organization', { params });
  return res.data.data;
}

export async function fetchOrganizationOptions(): Promise<OrganizationRecord[]> {
  const res = await client.get<ApiResponse<OrganizationRecord[]>>('/organization/options');
  return res.data.data;
}

export async function createOrganization(data: Partial<OrganizationRecord>): Promise<OrganizationRecord> {
  const res = await client.post<ApiResponse<OrganizationRecord>>('/organization', data);
  return res.data.data;
}

export async function updateOrganization(id: number, data: Partial<OrganizationRecord>): Promise<OrganizationRecord> {
  const res = await client.put<ApiResponse<OrganizationRecord>>(`/organization/${id}`, data);
  return res.data.data;
}

export async function deleteOrganization(id: number): Promise<void> {
  await client.delete(`/organization/${id}`);
}
