import client, { isApiError, makeApiError } from './client';
import { Platform } from 'react-native';
import type { ApiResponse, SurveyRecord, SurveyPhotoRecord, ProjectRecord } from './types';
import { API_BASE } from '@/utils/constants';

// ---- Survey ----
export async function fetchSurveyByCode(code: string): Promise<SurveyRecord> {
  try {
    const res = await client.get<ApiResponse<SurveyRecord>>(
      `/survey/by-short-code/${encodeURIComponent(code)}`,
    );
    return res.data.data;
  } catch (e) {
    if (isApiError(e) && e.status === 404) {
      throw makeApiError(404, `未找到勘查码为 "${code}" 的勘查记录`);
    }
    throw e;
  }
}

export async function fetchProject(id: number): Promise<ProjectRecord> {
  const res = await client.get<ApiResponse<ProjectRecord>>(`/project/${id}`);
  return res.data.data;
}

export async function updateSurvey(id: number, data: Partial<SurveyRecord>): Promise<SurveyRecord> {
  const res = await client.put<ApiResponse<SurveyRecord>>(`/survey/${id}`, data);
  return res.data.data;
}

// ---- Survey Photo ----
export async function createSurveyPhoto(
  projectId: number,
  surveyId: number,
  data: Partial<SurveyPhotoRecord>,
): Promise<SurveyPhotoRecord> {
  const res = await client.post<ApiResponse<SurveyPhotoRecord>>(
    `/survey-photo?projectId=${projectId}&surveyId=${surveyId}`,
    data,
  );
  return res.data.data;
}

export async function deleteSurveyPhoto(id: number): Promise<void> {
  await client.delete(`/survey-photo/${id}`);
}

// ---- File Upload ----
export async function uploadFile(
  fileUri: string,
  fileName: string,
  mimeType: string,
  projectCode?: string,
): Promise<{ filePath: string; originalName: string }> {
  const formData = new FormData();
  formData.append('file', {
    uri: Platform.OS === 'android' ? fileUri : fileUri.replace('file://', ''),
    name: fileName,
    type: mimeType,
  } as any);

  const url = projectCode
    ? `/file/upload?projectCode=${encodeURIComponent(projectCode)}`
    : '/file/upload';

  const res = await client.post<ApiResponse<{ filePath: string; originalName: string }>>(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data.data;
}

// Helper: get full image URL
export function getImageUrl(path: string): string {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  const base = API_BASE.replace('/api', '');
  return `${base}/uploads/${path}`;
}
