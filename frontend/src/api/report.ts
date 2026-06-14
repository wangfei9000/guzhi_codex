import client from './client';
import type { ApiResponse, PageResponse, ReportListRecord, ValuationReportRecord } from './types';

const REPORT_GENERATION_TIMEOUT_MS = 120000;

export interface ReportQuery {
  projectCode?: string;
  address?: string;
  page?: number;
  size?: number;
}

export async function fetchReports(params: ReportQuery): Promise<PageResponse<ReportListRecord>> {
  const res = await client.get<ApiResponse<PageResponse<ReportListRecord>>>('/valuation-report/reports', { params });
  return res.data.data;
}

export interface GeneratedReportPdf {
  reportUrl: string;
  endTime?: string;
}

export async function generateReportPdf(reportId: number): Promise<GeneratedReportPdf> {
  const res = await client.post<ApiResponse<{ filePath?: string; reportUrl?: string; endTime?: string }>>(
    `/valuation-report/${reportId}/generate-pdf`,
    undefined,
    { timeout: REPORT_GENERATION_TIMEOUT_MS }
  );
  const { filePath, reportUrl, endTime } = res.data.data;
  if (reportUrl) {
    return { reportUrl, endTime };
  }
  return {
    reportUrl: filePath ? `/uploads/${filePath.replace(/^\/+/, '')}` : '',
    endTime,
  };
}

export async function fetchLatestDownloadableReport(projectId: number): Promise<ValuationReportRecord> {
  const res = await client.get<ApiResponse<ValuationReportRecord>>('/valuation-report/latest-downloadable', {
    params: { projectId },
  });
  return res.data.data;
}
