import client from './client';
import type { ApiResponse, PageResponse, FileRecordItem } from './types';

export async function uploadFile(file: File, projectCode?: string): Promise<FileRecordItem> {
  const formData = new FormData();
  formData.append('file', file);
  const url = projectCode ? `/file/upload?projectCode=${encodeURIComponent(projectCode)}` : '/file/upload';
  const res = await client.post<ApiResponse<FileRecordItem>>(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data.data;
}

export async function fetchFiles(params: { page?: number; size?: number }): Promise<PageResponse<FileRecordItem>> {
  const res = await client.get<ApiResponse<PageResponse<FileRecordItem>>>('/file', { params });
  return res.data.data;
}

export async function deleteFile(id: number): Promise<void> {
  await client.delete(`/file/${id}`);
}
