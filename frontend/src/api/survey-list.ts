import client from './client';
import type { ApiResponse, PageResponse, SurveyListRecord } from './types';

export interface SurveyListQuery {
  projectCode?: string;
  page?: number;
  size?: number;
}

export async function fetchSurveys(params: SurveyListQuery): Promise<PageResponse<SurveyListRecord>> {
  const res = await client.get<ApiResponse<PageResponse<SurveyListRecord>>>('/survey/surveys', { params });
  return res.data.data;
}
