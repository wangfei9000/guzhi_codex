import client from './client';
import type { ApiResponse } from './types';
import type {
  CollateralRecord, ValuationReportRecord, ValuationMethodRecord,
  SurveyRecord, SurveyPhotoRecord, OwnershipInfoRecord, ReportReviewRecord,
  ProjectRecord,
} from './types';

// ---- Project ----
export async function updateProject(id: number, data: Partial<ProjectRecord>): Promise<ProjectRecord> {
  const res = await client.put<ApiResponse<ProjectRecord>>(`/project/${id}`, data);
  return res.data.data;
}

export async function queryValuationPrice(city: string | undefined, address: string, valuationTime?: string): Promise<{ valuationUnitPrice: number; valuationTotalPrice: number }> {
  const res = await client.post<ApiResponse<{ valuationUnitPrice: number; valuationTotalPrice: number }>>('/project/valuation-price', null, {
    params: { city, address, valuationTime },
  });
  return res.data.data;
}

// ---- Collateral ----
export async function createCollateral(projectId: number, data: Partial<CollateralRecord>): Promise<CollateralRecord> {
  const res = await client.post<ApiResponse<CollateralRecord>>(`/collateral?projectId=${projectId}`, data);
  return res.data.data;
}

export async function updateCollateral(id: number, data: Partial<CollateralRecord>): Promise<CollateralRecord> {
  const res = await client.put<ApiResponse<CollateralRecord>>(`/collateral/${id}`, data);
  return res.data.data;
}

export async function deleteCollateral(id: number): Promise<void> {
  await client.delete(`/collateral/${id}`);
}

// ---- ValuationReport ----
export async function createValuationReport(projectId: number, data: Partial<ValuationReportRecord>): Promise<ValuationReportRecord> {
  const res = await client.post<ApiResponse<ValuationReportRecord>>(`/valuation-report?projectId=${projectId}`, data);
  return res.data.data;
}

export async function updateValuationReport(id: number, data: Partial<ValuationReportRecord>): Promise<ValuationReportRecord> {
  const res = await client.put<ApiResponse<ValuationReportRecord>>(`/valuation-report/${id}`, data);
  return res.data.data;
}

export async function checkReportCodeAvailable(reportCode: string, excludeId?: number): Promise<boolean> {
  const res = await client.get<ApiResponse<{ available: boolean }>>('/valuation-report/check-report-code', {
    params: { reportCode, excludeId },
  });
  return res.data.data.available;
}

export async function deleteValuationReport(id: number): Promise<void> {
  await client.delete(`/valuation-report/${id}`);
}

// ---- ValuationMethod ----
export async function createValuationMethod(reportId: number, data: Partial<ValuationMethodRecord>): Promise<ValuationMethodRecord> {
  const res = await client.post<ApiResponse<ValuationMethodRecord>>(`/valuation-method?reportId=${reportId}`, data);
  return res.data.data;
}

export async function updateValuationMethod(id: number, data: Partial<ValuationMethodRecord>): Promise<ValuationMethodRecord> {
  const res = await client.put<ApiResponse<ValuationMethodRecord>>(`/valuation-method/${id}`, data);
  return res.data.data;
}

export async function deleteValuationMethod(id: number): Promise<void> {
  await client.delete(`/valuation-method/${id}`);
}

// ---- Survey ----
export async function createSurvey(projectId: number, data: Partial<SurveyRecord>): Promise<SurveyRecord> {
  const res = await client.post<ApiResponse<SurveyRecord>>(`/survey?projectId=${projectId}`, data);
  return res.data.data;
}

export async function updateSurvey(id: number, data: Partial<SurveyRecord>): Promise<SurveyRecord> {
  const res = await client.put<ApiResponse<SurveyRecord>>(`/survey/${id}`, data);
  return res.data.data;
}

export async function deleteSurvey(id: number): Promise<void> {
  await client.delete(`/survey/${id}`);
}

// ---- SurveyPhoto ----
export async function createSurveyPhoto(projectId: number, surveyId: number, data: Partial<SurveyPhotoRecord>): Promise<SurveyPhotoRecord> {
  const res = await client.post<ApiResponse<SurveyPhotoRecord>>(`/survey-photo?projectId=${projectId}&surveyId=${surveyId}`, data);
  return res.data.data;
}

export async function updateSurveyPhoto(id: number, data: Partial<SurveyPhotoRecord>): Promise<SurveyPhotoRecord> {
  const res = await client.put<ApiResponse<SurveyPhotoRecord>>(`/survey-photo/${id}`, data);
  return res.data.data;
}

export async function deleteSurveyPhoto(id: number): Promise<void> {
  await client.delete(`/survey-photo/${id}`);
}

// ---- OwnershipInfo ----
export async function saveOwnershipInfo(projectId: number, data: Partial<OwnershipInfoRecord>): Promise<OwnershipInfoRecord> {
  const res = await client.post<ApiResponse<OwnershipInfoRecord>>(`/ownership-info/save/${projectId}`, data);
  return res.data.data;
}

// ---- ReportReview ----
export async function createReportReview(projectId: number, reportId: number, data: Partial<ReportReviewRecord>): Promise<ReportReviewRecord> {
  const res = await client.post<ApiResponse<ReportReviewRecord>>(`/report-review?projectId=${projectId}&reportId=${reportId}`, data);
  return res.data.data;
}

export async function updateReportReview(id: number, data: Partial<ReportReviewRecord>): Promise<ReportReviewRecord> {
  const res = await client.put<ApiResponse<ReportReviewRecord>>(`/report-review/${id}`, data);
  return res.data.data;
}

export async function deleteReportReview(id: number): Promise<void> {
  await client.delete(`/report-review/${id}`);
}
