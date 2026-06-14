import client from './client';
import type { ApiResponse, PageResponse, ProjectDetail, ProjectRecord } from './types';

export interface ProjectQuery {
  projectCode?: string;
  projectName?: string;
  clientName?: string;
  city?: string;
  address?: string;
  status?: string;
  onlyValuation?: boolean;
  page?: number;
  size?: number;
  sort?: string | string[];
}

export interface BankValuationQuery {
  city?: string;
  district?: string;
  valuationType?: string;
  status?: string;
  valuationTime?: string;
  page?: number;
  size?: number;
  sort?: string | string[];
}

export async function fetchProjects(params: ProjectQuery): Promise<PageResponse<ProjectRecord>> {
  const res = await client.get<ApiResponse<PageResponse<ProjectRecord>>>('/project', { params });
  return res.data.data;
}

export async function fetchBankValuations(params: BankValuationQuery): Promise<PageResponse<ProjectRecord>> {
  const res = await client.get<ApiResponse<PageResponse<ProjectRecord>>>('/project/bank-valuations', { params });
  return res.data.data;
}

export async function fetchProjectClientNames(): Promise<string[]> {
  const res = await client.get<ApiResponse<string[]>>('/project/client-names');
  return res.data.data;
}

export async function fetchValuationCities(): Promise<string[]> {
  const res = await client.get<ApiResponse<string[]>>('/project/valuation-cities');
  return res.data.data;
}

export async function fetchValuationDistricts(city: string): Promise<string[]> {
  const res = await client.get<ApiResponse<string[]>>('/project/valuation-districts', { params: { city } });
  return res.data.data;
}

export async function fetchProjectById(id: number): Promise<ProjectRecord> {
  const res = await client.get<ApiResponse<ProjectRecord>>(`/project/${id}`);
  return res.data.data;
}

export async function fetchProjectDetail(id: number): Promise<ProjectDetail> {
  const res = await client.get<ApiResponse<ProjectDetail>>(`/project/${id}/detail`);
  return res.data.data;
}

export async function saveProjectDetail(id: number, detail: ProjectDetail): Promise<void> {
  await client.put(`/project/${id}/detail`, detail);
}

export async function createProject(data: Partial<ProjectRecord>): Promise<ProjectRecord> {
  const res = await client.post<ApiResponse<ProjectRecord>>('/project', data);
  return res.data.data;
}

export async function updateProject(id: number, data: Partial<ProjectRecord>): Promise<ProjectRecord> {
  const res = await client.put<ApiResponse<ProjectRecord>>(`/project/${id}`, data);
  return res.data.data;
}

export async function saveAutoValuation(data: Partial<ProjectRecord>): Promise<ProjectRecord> {
  const res = await client.post<ApiResponse<ProjectRecord>>('/project/auto-valuation', data);
  return res.data.data;
}
