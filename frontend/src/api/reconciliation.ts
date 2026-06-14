import client from './client';
import type { ApiResponse, PageResponse } from './types';

export interface ReconciliationRecord {
  id: number;
  organizationId?: number | null;
  startTime: string;
  endTime: string;
  reconciliationDate: string;
  result: string;
  fileUrl?: string;
  remark?: string;
  createdAt?: string;
}

export interface ReconciliationQuery {
  startTime?: string;
  endTime?: string;
  page?: number;
  size?: number;
  sort?: string | string[];
}

export interface ReconciliationRequest {
  startTime: string;
  endTime: string;
  remark?: string;
}

export async function fetchReconciliations(params: ReconciliationQuery): Promise<PageResponse<ReconciliationRecord>> {
  const res = await client.get<ApiResponse<PageResponse<ReconciliationRecord>>>('/reconciliation', { params });
  return res.data.data;
}

export async function startReconciliation(data: ReconciliationRequest): Promise<ReconciliationRecord> {
  const res = await client.post<ApiResponse<ReconciliationRecord>>('/reconciliation', data);
  return res.data.data;
}
