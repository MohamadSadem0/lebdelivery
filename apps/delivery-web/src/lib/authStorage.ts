import type { ActiveRole } from '@lebanon-platform/shared-types';

const ACCESS_TOKEN_KEY = 'delivery_web_access_token';
const REFRESH_TOKEN_KEY = 'delivery_web_refresh_token';
const ACTIVE_ROLE_KEY = 'delivery_web_active_role';

export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY) ?? undefined;
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY) ?? undefined;
}

export function saveTokens(accessToken: string, refreshToken: string) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

export function saveActiveRole(role: ActiveRole) {
  localStorage.setItem(ACTIVE_ROLE_KEY, JSON.stringify(role));
}

export function readActiveRole() {
  const raw = localStorage.getItem(ACTIVE_ROLE_KEY);
  if (!raw) {
    return undefined;
  }
  try {
    return JSON.parse(raw) as ActiveRole;
  } catch {
    return undefined;
  }
}

export function clearAuthStorage() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(ACTIVE_ROLE_KEY);
}
