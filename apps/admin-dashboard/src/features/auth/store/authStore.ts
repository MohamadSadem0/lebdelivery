import { create } from 'zustand';
import type { AuthResponse, CurrentUser, LoginRequest } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';
import { clearAuthStorage, getAccessToken, getRefreshToken, saveTokens } from '../../../lib/authStorage';

let unauthorizedListenerAttached = false;

type AuthState = {
  accessToken?: string;
  refreshToken?: string;
  user?: CurrentUser;
  bootstrapping: boolean;
  loading: boolean;
  error?: string;
  bootstrap: () => Promise<void>;
  login: (request: LoginRequest) => Promise<void>;
  logout: () => Promise<void>;
  logoutLocal: () => void;
};

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: undefined,
  refreshToken: undefined,
  user: undefined,
  bootstrapping: true,
  loading: false,
  error: undefined,
  async bootstrap() {
    if (!unauthorizedListenerAttached) {
      window.addEventListener('admin-dashboard:unauthorized', () => get().logoutLocal());
      unauthorizedListenerAttached = true;
    }

    const accessToken = getAccessToken();
    const refreshToken = getRefreshToken();
    if (!accessToken) {
      set({ bootstrapping: false });
      return;
    }

    set({ accessToken, refreshToken });
    try {
      const response = await api.get<ApiEnvelope<CurrentUser>>('/auth/me');
      set({ user: response.data.data, bootstrapping: false });
    } catch {
      clearAuthStorage();
      set({ accessToken: undefined, refreshToken: undefined, user: undefined, bootstrapping: false });
    }
  },
  async login(request) {
    set({ loading: true, error: undefined });
    try {
      const response = await api.post<ApiEnvelope<AuthResponse>>('/auth/login', request);
      const auth = response.data.data;
      saveTokens(auth.accessToken, auth.refreshToken);
      set({ accessToken: auth.accessToken, refreshToken: auth.refreshToken });
      const me = await api.get<ApiEnvelope<CurrentUser>>('/auth/me');
      set({ user: me.data.data, loading: false });
    } catch {
      set({ loading: false, error: 'Login failed. Check admin credentials.' });
    }
  },
  async logout() {
    const refreshToken = get().refreshToken;
    try {
      await api.post('/auth/logout', { refreshToken });
    } finally {
      get().logoutLocal();
    }
  },
  logoutLocal() {
    clearAuthStorage();
    set({ accessToken: undefined, refreshToken: undefined, user: undefined });
  }
}));

export function hasAdminRole(user?: CurrentUser) {
  return user?.roles.some((role) => role.role === 'ADMIN') ?? false;
}
