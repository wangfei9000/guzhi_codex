import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { message } from 'antd';
import { API_BASE, AUTH_TOKEN_KEY, REFRESH_TOKEN_KEY } from '@/utils/constants';
import type { ApiResponse } from './types';

const client = axios.create({
  baseURL: API_BASE,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

let isRefreshing = false;
let pendingRequests: Array<(token: string) => void> = [];

function resolvePendingRequests(token: string) {
  pendingRequests.forEach((cb) => cb(token));
  pendingRequests = [];
}

function rejectPendingRequests() {
  pendingRequests.forEach((cb) => cb(''));
  pendingRequests = [];
}

// Request interceptor: attach token
client.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem(AUTH_TOKEN_KEY);
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor: handle errors, refresh token
client.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse;
    if (res.code !== 200) {
      message.error(res.message || '请求失败');
      return Promise.reject(new Error(res.message));
    }
    return response;
  },
  async (error: AxiosError<ApiResponse>) => {
    const status = error.response?.status;

    if (status === 401) {
      const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
      if (!refreshToken) {
        localStorage.removeItem(AUTH_TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        window.location.href = '/login';
        return Promise.reject(error);
      }

      if (!isRefreshing) {
        isRefreshing = true;
        try {
          const res = await axios.post(`${API_BASE}/auth/refresh`, { refreshToken });
          const newToken = res.data.data.accessToken;
          localStorage.setItem(AUTH_TOKEN_KEY, newToken);
          resolvePendingRequests(newToken);
          isRefreshing = false;

          if (error.config) {
            error.config.headers.Authorization = `Bearer ${newToken}`;
            return client(error.config);
          }
        } catch {
          rejectPendingRequests();
          isRefreshing = false;
          localStorage.removeItem(AUTH_TOKEN_KEY);
          localStorage.removeItem(REFRESH_TOKEN_KEY);
          window.location.href = '/login';
        }
      } else {
        return new Promise((resolve) => {
          pendingRequests.push((token: string) => {
            if (error.config && token) {
              error.config.headers.Authorization = `Bearer ${token}`;
              resolve(client(error.config));
            }
          });
        });
      }
    } else if (status === 403) {
      message.error('权限不足，无法访问该资源');
    } else if (status === 500) {
      message.error('服务器内部错误');
    }

    return Promise.reject(error);
  }
);

export default client;
