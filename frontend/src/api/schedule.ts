import client from './client';
import type { ApiResponse, PageResponse, ScheduleRecord } from './types';

export interface ScheduleQuery {
  keyword?: string;
  page?: number;
  size?: number;
}

export async function fetchSchedules(params: ScheduleQuery): Promise<PageResponse<ScheduleRecord>> {
  const res = await client.get<ApiResponse<PageResponse<ScheduleRecord>>>('/schedule', { params });
  return res.data.data;
}

export async function fetchScheduleById(id: number): Promise<ScheduleRecord> {
  const res = await client.get<ApiResponse<ScheduleRecord>>(`/schedule/${id}`);
  return res.data.data;
}
