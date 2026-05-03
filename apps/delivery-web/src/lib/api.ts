import axios from 'axios';
import { clearAuthStorage, getAccessToken } from './authStorage';

export type ApiEnvelope<T> = {
  success: boolean;
  message: string;
  data: T;
  code?: string;
  errors: string[];
};

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1',
  timeout: 15000
});

api.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearAuthStorage();
      window.dispatchEvent(new Event('delivery-web:unauthorized'));
    }
    return Promise.reject(error);
  }
);
