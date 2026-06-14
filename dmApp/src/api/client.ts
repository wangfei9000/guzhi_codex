import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_BASE, AUTH_TOKEN_KEY } from '@/utils/constants';
import type { ApiResponse } from './types';

// ---- In-memory token cache ----
// The interceptor reads from this (synchronous, no AsyncStorage race).
// AsyncStorage is only used for persistence across app restarts.
let memoryToken: string | null = null;

/** Call this after login or on app startup to set the cached token. */
export function setMemoryToken(token: string | null) {
  memoryToken = token;
}

/** Call on app startup to restore token from persisted storage. */
export async function restoreToken(): Promise<string | null> {
  try {
    const saved = await AsyncStorage.getItem(AUTH_TOKEN_KEY);
    if (saved) {
      memoryToken = saved;
    }
    return saved;
  } catch {
    return null;
  }
}

// ---- ApiError ----
export interface ApiError {
  _apiError: true;
  status: number;
  message: string;
}

export function makeApiError(status: number, message: string): ApiError {
  return { _apiError: true, status, message };
}

export function isApiError(e: unknown): e is ApiError {
  return !!e && typeof e === 'object' && (e as any)._apiError === true;
}

// ---- Axios client ----
const client = axios.create({
  baseURL: API_BASE,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

// Request interceptor: attach token from MEMORY (synchronous — no AsyncStorage read)
client.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  if (memoryToken && config.headers) {
    config.headers.Authorization = `Bearer ${memoryToken}`;
  }
  return config;
});

// Response interceptor
client.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse;
    if (res.code !== 200) {
      return Promise.reject(makeApiError(res.code, res.message || '请求失败'));
    }
    return response;
  },
  async (error: AxiosError<ApiResponse>) => {
    const status = error.response?.status;
    const serverMsg = error.response?.data?.message;

    if (status === 401) {
      // Token invalid/expired — clear both memory and storage
      memoryToken = null;
      try { await AsyncStorage.multiRemove([AUTH_TOKEN_KEY, 'user_info']); } catch {}
      return Promise.reject(makeApiError(401, '登录已过期，请重新登录'));
    }

    if (status === 403) {
      // No token or insufficient permissions
      // If memoryToken was null, this means the token was never set (login bug)
      // If memoryToken was set but server still returned 403, the token is invalid
      memoryToken = null;
      try { await AsyncStorage.multiRemove([AUTH_TOKEN_KEY, 'user_info']); } catch {}
      return Promise.reject(makeApiError(403, '认证失败，Token 无效或缺失，请重新登录'));
    }

    if (!error.response) {
      return Promise.reject(
        makeApiError(0, `无法连接服务器 (${API_BASE})\n请检查：\n1. 手机网络是否正常\n2. 服务器地址是否正确`),
      );
    }

    const msg = serverMsg || getHttpMessage(status || 500);
    return Promise.reject(makeApiError(status || 500, msg));
  },
);

function getHttpMessage(status: number): string {
  switch (status) {
    case 404: return '请求的资源不存在';
    case 500: return '服务器内部错误，请稍后重试';
    default: return `请求失败 (HTTP ${status})`;
  }
}

export default client;
