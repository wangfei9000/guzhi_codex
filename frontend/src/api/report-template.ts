import client from './client';
import type { ApiResponse, PageResponse, ReportTemplateRecord } from './types';

export interface ReportTemplateQuery {
  page?: number;
  size?: number;
  sort?: string | string[];
}

export async function fetchReportTemplates(params: ReportTemplateQuery): Promise<PageResponse<ReportTemplateRecord>> {
  const res = await client.get<ApiResponse<PageResponse<ReportTemplateRecord>>>('/report-template', { params });
  return res.data.data;
}

export async function createReportTemplate(data: Partial<ReportTemplateRecord>): Promise<ReportTemplateRecord> {
  const res = await client.post<ApiResponse<ReportTemplateRecord>>('/report-template', data);
  return res.data.data;
}

export async function updateReportTemplate(id: number, data: Partial<ReportTemplateRecord>): Promise<ReportTemplateRecord> {
  const res = await client.put<ApiResponse<ReportTemplateRecord>>(`/report-template/${id}`, data);
  return res.data.data;
}

export async function deleteReportTemplate(id: number): Promise<void> {
  await client.delete(`/report-template/${id}`);
}
