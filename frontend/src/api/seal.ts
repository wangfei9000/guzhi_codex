import client from './client';
import type { ApiResponse, PageResponse, SealListRecord } from './types';

export interface SealQuery {
  reportCode?: string;
  projectCode?: string;
  page?: number;
  size?: number;
}

export async function fetchSeals(params: SealQuery): Promise<PageResponse<SealListRecord>> {
  const res = await client.get<ApiResponse<PageResponse<SealListRecord>>>('/report-seal/seals', { params });
  return res.data.data;
}

export async function uploadSealedReport(sealId: number, sealedReportUrl: string): Promise<void> {
  await client.put(`/report-seal/${sealId}/upload`, { sealedReportUrl });
}
