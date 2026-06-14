import client from './client';
import type { ApiResponse, DashboardStats } from './types';

export async function fetchDashboardStats(): Promise<DashboardStats> {
  const res = await client.get<ApiResponse<DashboardStats>>('/dashboard/stats');
  return res.data.data;
}
