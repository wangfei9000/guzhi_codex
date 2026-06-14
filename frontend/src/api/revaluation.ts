import client from './client';
import type { ApiResponse, PageResponse } from './types';

export interface RevaluationRecord {
  id: number;
  organizationId?: number | null;
  revaluationDate: string;
  result: string;
  fileUrl?: string;
  remark?: string;
  createdAt?: string;
}

export interface RevaluationQuery {
  page?: number;
  size?: number;
  sort?: string | string[];
}

export async function fetchRevaluations(params: RevaluationQuery): Promise<PageResponse<RevaluationRecord>> {
  const res = await client.get<ApiResponse<PageResponse<RevaluationRecord>>>('/revaluation', { params });
  return res.data.data;
}

export async function startRevaluation(data: { projectCodes: string[]; remark?: string }): Promise<RevaluationRecord> {
  const res = await client.post<ApiResponse<RevaluationRecord>>('/revaluation', data);
  return res.data.data;
}
