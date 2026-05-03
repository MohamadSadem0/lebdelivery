import { create } from 'zustand';
import type { ActiveRole, AuthResponse, CurrentUser, LoginRequest, RegisterRequest } from '@lebanon-platform/shared-types';
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
  register: (request: RegisterRequest) => Promise<void>;
  selectActiveRole: (role: ActiveRole) => Promise<void>;
  logout: () => Promise<void>;
  logoutLocal: () => void;
};

export const useAuthStore = create<AuthState>((set, get) => ({
  bootstrapping: true,
  loading: false,
  async bootstrap() {
    if (!unauthorizedListenerAttached) {
      window.addEventListener('client-web:unauthorized', () => get().logoutLocal());
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
      saveTokens(response.data.data.accessToken, response.data.data.refreshToken);
      const me = await api.get<ApiEnvelope<CurrentUser>>('/auth/me');
      const clientRole = me.data.data.roles.find((role) => role.role === 'CLIENT');
      if (clientRole) {
        saveActiveRole(clientRole);
      }
      set({
        accessToken: response.data.data.accessToken,
        refreshToken: response.data.data.refreshToken,
        activeRole: clientRole,
        user: me.data.data,
        loading: false
      });
    } catch {
      set({ loading: false, error: 'Login failed. Check your phone and password.' });
    }
  },
  async register(request) {
    set({ loading: true, error: undefined });
    try {
      const response = await api.post<ApiEnvelope<AuthResponse>>('/auth/register', request);
      saveTokens(response.data.data.accessToken, response.data.data.refreshToken);
      const me = await api.get<ApiEnvelope<CurrentUser>>('/auth/me');
      const clientRole = me.data.data.roles.find((role) => role.role === 'CLIENT');
      if (clientRole) {
        saveActiveRole(clientRole);
      }
      set({
        accessToken: response.data.data.accessToken,
        refreshToken: response.data.data.refreshToken,
        activeRole: clientRole,
        user: me.data.data,
        loading: false
      });
    } catch {
      set({ loading: false, error: 'Registration failed. Check the details and try again.' });
    }
  },
  async selectActiveRole(role) {
    const response = await api.post<ApiEnvelope<{ activeRole: ActiveRole }>>('/auth/active-role', role);
    saveActiveRole(response.data.data.activeRole);
    set({ activeRole: response.data.data.activeRole });
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
