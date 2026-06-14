import React, { createContext, useContext, useState, useEffect, useCallback, useRef } from 'react';
import { AppState, AppStateStatus } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { setMemoryToken, restoreToken } from '@/api/client';
import { AUTH_TOKEN_KEY, USER_INFO_KEY } from '@/utils/constants';
import type { UserInfo } from '@/api/types';

interface AuthState {
  token: string | null;
  userInfo: UserInfo | null;
  isLoading: boolean;
  isLoggedIn: boolean;
  login: (token: string, userInfo: UserInfo) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthState>({
  token: null,
  userInfo: null,
  isLoading: true,
  isLoggedIn: false,
  login: async () => {},
  logout: async () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const tokenRef = useRef<string | null>(null);

  // On mount: restore token from storage into memory
  useEffect(() => {
    (async () => {
      try {
        const savedToken = await restoreToken(); // sets memoryToken
        const savedUser = await AsyncStorage.getItem(USER_INFO_KEY);
        if (savedToken && savedUser) {
          setToken(savedToken);
          setUserInfo(JSON.parse(savedUser));
          tokenRef.current = savedToken;
        }
      } catch {
        // ignore
      } finally {
        setIsLoading(false);
      }
    })();
  }, []);

  // Detect if token was cleared by 401/403 interceptor when app resumes
  useEffect(() => {
    const sub = AppState.addEventListener('change', (nextState: AppStateStatus) => {
      if (nextState === 'active') {
        AsyncStorage.getItem(AUTH_TOKEN_KEY).then((stored) => {
          if (!stored && tokenRef.current) {
            setToken(null);
            setUserInfo(null);
            tokenRef.current = null;
          }
        }).catch(() => {});
      }
    });
    return () => sub.remove();
  }, []);

  const login = useCallback(async (newToken: string, newUser: UserInfo) => {
    // 1. Set in-memory token FIRST (used by interceptor for all API calls)
    setMemoryToken(newToken);
    // 2. Persist to storage (for next app start)
    await AsyncStorage.setItem(AUTH_TOKEN_KEY, newToken);
    await AsyncStorage.setItem(USER_INFO_KEY, JSON.stringify(newUser));
    // 3. Update React state (triggers navigation to main screen)
    setToken(newToken);
    setUserInfo(newUser);
    tokenRef.current = newToken;
  }, []);

  const logout = useCallback(async () => {
    // 1. Clear memory token FIRST
    setMemoryToken(null);
    // 2. Clear React state (triggers navigation to login)
    setToken(null);
    setUserInfo(null);
    tokenRef.current = null;
    // 3. Clear storage (fire-and-forget)
    AsyncStorage.multiRemove([AUTH_TOKEN_KEY, USER_INFO_KEY]).catch(() => {});
  }, []);

  return (
    <AuthContext.Provider value={{ token, userInfo, isLoading, isLoggedIn: !!token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
