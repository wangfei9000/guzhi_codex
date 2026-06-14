export const API_BASE = '/api';

export const AUTH_TOKEN_KEY = 'auth_token';
export const REFRESH_TOKEN_KEY = 'refresh_token';
export const USER_INFO_KEY = 'user_info';

export const STATUS_MAP: Record<number, string> = {
  200: 'success',
  400: '请求错误',
  401: '未授权',
  403: '权限不足',
  404: '资源不存在',
  500: '服务器错误',
};
