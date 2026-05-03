import { create } from 'zustand';
import type { ActiveRole, AuthResponse, CurrentUser, LoginRequest } from '@lebanon-platform/shared-types';
import { api, type ApiEnvelope } from '../../../lib/api';
import { clearAuthStorage, getAccessToken, getRefreshToken, readActiveRole, saveActiveRole, saveTokens } from '../../../lib/authStorage';

let unauthorizedListenerAttached = false;

type AuthState = {
  accessToken?: string;
  refreshToken?: string;
  user?: CurrentUser;
  activeRole?: ActiveRole;
  bootstrapping: boolean;
  loading: boolean;
  error?: string;
  bootstrap: () => Promise<void>;
  login: (request: LoginRequest) => Promise<void>;
  selectActiveRole: (role: ActiveRole) => Promise<void>;
  logout: () => Promise<void>;
  logoutLocal: () => void;
};

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: undefined,
  refreshToken: undefined,
  user: undefined,
  activeRole: undefined,
  bootstrapping: true,
  loading: false,
  error: undefined,
  async bootstrap() {
    if (!unauthorizedListenerAttached) {
      window.addEventListener('business-dashboard:unauthorized', () => get().logoutLocal());
      unauthorizedListenerAttached = true;
    }
    const accessToken = getAccessToken();
    const refreshToken = getRefreshToken();
    const activeRole = readActiveRole();
    if (!accessToken) {
      set({ bootstrapping: false });
      return;
    }

    set({ accessToken, refreshToken, activeRole });
    try {
      const response = await api.get<ApiEnvelope<CurrentUser>>('/auth/me');
      set({ user: response.data.data, bootstrapping: false });
    } catch {
      clearAuthStorage();
      set({ accessToken: undefined, refreshToken: undefined, activeRole: undefined, user: undefined, bootstrapping: false });
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
      set({ loading: false, error: 'Login failed. Check your phone and password.' });
    }
  },
  async selectActiveRole(role) {
    const response = await api.post<ApiEnvelope<{ activeRole: ActiveRole }>>('/auth/active-role', role);
    const activeRole = response.data.data.activeRole;
    saveActiveRole(activeRole);
    set({ activeRole });
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
    set({ accessToken: undefined, refreshToken: undefined, activeRole: undefined, user: undefined });
  }
}));

export function businessRoles(user?: CurrentUser) {
  return user?.roles.filter((role) => ['STORE_OWNER', 'STORE_STAFF', 'PROVIDER_OWNER', 'PROVIDER_STAFF', 'ADMIN'].includes(role.role)) ?? [];
}
