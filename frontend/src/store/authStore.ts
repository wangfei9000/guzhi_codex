import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { login as loginApi, getCurrentUser } from '@/api/auth';
import { AUTH_TOKEN_KEY, REFRESH_TOKEN_KEY } from '@/utils/constants';
import type { UserInfo } from '@/api/types';

interface AuthState {
  token: string | null;
  refreshToken: string | null;
  userInfo: UserInfo | null;
  permissions: string[];
  isInitialized: boolean;
  setToken: (token: string, refreshToken?: string) => void;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  fetchUserInfo: () => Promise<void>;
  hasPermission: (code: string) => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      refreshToken: null,
      userInfo: null,
      permissions: [],
      isInitialized: false,

      setToken: (token: string, refreshToken?: string) => {
        localStorage.setItem(AUTH_TOKEN_KEY, token);
        if (refreshToken) {
          localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
        }
        set({ token, refreshToken: refreshToken || get().refreshToken });
      },

      login: async (username: string, password: string) => {
        const tokenInfo = await loginApi(username, password);
        localStorage.setItem(AUTH_TOKEN_KEY, tokenInfo.accessToken);
        set({ token: tokenInfo.accessToken, isInitialized: true });
        // Fetch user info after login
        try {
          const user = await getCurrentUser();
          const perms = user.roles?.flatMap(r => r.permissions?.map(p => p.permCode) || []) || [];
          set({
            userInfo: user,
            permissions: perms,
          });
        } catch {
          // ignore profile fetch error
        }
      },

      logout: () => {
        localStorage.removeItem(AUTH_TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        set({ token: null, refreshToken: null, userInfo: null, permissions: [], isInitialized: false });
      },

      fetchUserInfo: async () => {
        try {
          const user = await getCurrentUser();
          const perms = user.roles?.flatMap(r => r.permissions?.map(p => p.permCode) || []) || [];
          set({ userInfo: user, permissions: perms, isInitialized: true });
        } catch {
          get().logout();
        }
      },

      hasPermission: (code: string) => {
        return get().permissions.includes(code);
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        token: state.token,
        refreshToken: state.refreshToken,
      }),
    }
  )
);
