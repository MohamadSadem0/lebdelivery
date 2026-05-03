import type { ActiveRole } from '@lebanon-platform/shared-types';

export const ACCESS_TOKEN_KEY = 'business_dashboard_access_token';
export const REFRESH_TOKEN_KEY = 'business_dashboard_refresh_token';
export const ACTIVE_ROLE_KEY = 'business_dashboard_active_role';

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

export function saveActiveRole(activeRole: ActiveRole) {
  localStorage.setItem(ACTIVE_ROLE_KEY, JSON.stringify(activeRole));
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
