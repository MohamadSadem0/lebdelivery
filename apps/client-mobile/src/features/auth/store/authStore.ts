import { create } from 'zustand';
import type { ActiveRole, AuthResponse, CurrentUser, LoginRequest, RegisterRequest } from '@lebanon-platform/shared-types';
import { api } from '../../../lib/api';
import { clearTokens, getAccessToken, getRefreshToken, setAccessToken, setRefreshToken } from '../../../lib/secureStorage';

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
  logout: () => Promise<void>;
  selectActiveRole: (role: ActiveRole) => Promise<void>;
};

type ApiEnvelope<T> = {
  success: boolean;
  message: string;
  data: T;
  code?: string;
  errors: string[];
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
    const accessToken = await getAccessToken();
    const refreshToken = await getRefreshToken();

    if (!accessToken) {
      set({ bootstrapping: false, accessToken: undefined, refreshToken: undefined, user: undefined });
      return;
    }

    try {
      const response = await api.get<ApiEnvelope<CurrentUser>>('/auth/me');
      set({ accessToken, refreshToken: refreshToken ?? undefined, user: response.data.data, bootstrapping: false });
    } catch {
      await clearTokens();
      set({ accessToken: undefined, refreshToken: undefined, user: undefined, activeRole: undefined, bootstrapping: false });
    }
  },
  async login(request) {
    set({ loading: true, error: undefined });
    try {
      const response = await api.post<ApiEnvelope<AuthResponse>>('/auth/login', request);
      await persistAuth(response.data.data);
      const me = await api.get<ApiEnvelope<CurrentUser>>('/auth/me');
      set({ user: me.data.data, loading: false });
    } catch {
      set({ loading: false, error: 'Login failed. Check your phone and password.' });
    }
  },
  async register(request) {
    set({ loading: true, error: undefined });
    try {
      const response = await api.post<ApiEnvelope<AuthResponse>>('/auth/register', request);
      await persistAuth(response.data.data);
      const me = await api.get<ApiEnvelope<CurrentUser>>('/auth/me');
      set({ user: me.data.data, loading: false });
    } catch {
      set({ loading: false, error: 'Registration failed. Check the entered details.' });
    }
  },
  async logout() {
    const refreshToken = get().refreshToken;
    try {
      await api.post('/auth/logout', { refreshToken });
    } finally {
      await clearTokens();
      set({ accessToken: undefined, refreshToken: undefined, user: undefined, activeRole: undefined });
    }
  },
  async selectActiveRole(role) {
    const response = await api.post<ApiEnvelope<{ activeRole: ActiveRole }>>('/auth/active-role', role);
    set({ activeRole: response.data.data.activeRole });
  }
}));

async function persistAuth(response: AuthResponse) {
  await setAccessToken(response.accessToken);
  await setRefreshToken(response.refreshToken);
  useAuthStore.setState({
    accessToken: response.accessToken,
    refreshToken: response.refreshToken
  });
}
